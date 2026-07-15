package cloud.stackit.sdk.iaas.examples;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.iaas.v2alpha1api.api.IaasApi;
import cloud.stackit.sdk.iaas.v2alpha1api.model.AddVPCRoutingTablePayload;
import cloud.stackit.sdk.iaas.v2alpha1api.model.AddVPCStaticRoutePayload;
import cloud.stackit.sdk.iaas.v2alpha1api.model.AddVPCStaticRoutePayloadDestination;
import cloud.stackit.sdk.iaas.v2alpha1api.model.AddVPCStaticRoutePayloadNexthop;
import cloud.stackit.sdk.iaas.v2alpha1api.model.CreateVPCPayload;
import cloud.stackit.sdk.iaas.v2alpha1api.model.CreateVPCRegionPayload;
import cloud.stackit.sdk.iaas.v2alpha1api.model.DestinationCIDRv4;
import cloud.stackit.sdk.iaas.v2alpha1api.model.NetworkRangeIPv4Request;
import cloud.stackit.sdk.iaas.v2alpha1api.model.NexthopInternet;
import cloud.stackit.sdk.iaas.v2alpha1api.model.RegionalVPC;
import cloud.stackit.sdk.iaas.v2alpha1api.model.Route;
import cloud.stackit.sdk.iaas.v2alpha1api.model.VPC;
import cloud.stackit.sdk.iaas.v2alpha1api.model.VPCNetworkRange;
import cloud.stackit.sdk.iaas.v2alpha1api.model.VPCRoutingTable;
import cloud.stackit.sdk.resourcemanager.v0api.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.v0api.model.CreateProjectPayload;
import cloud.stackit.sdk.resourcemanager.v0api.model.Member;
import cloud.stackit.sdk.resourcemanager.v0api.model.Project;
import cloud.stackit.sdk.resourcemanager.v0api.wait.ResourcemanagerWait;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class IaaSVPCExample {
    public static void main(String[] args) throws IOException {
        /*
         * Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
         * STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
         * */
        IaasApi iaasApi = new IaasApi();
        ResourceManagerApi resourceManagerApi = new ResourceManagerApi();

        String region = "eu01";
        String parentContainerId = System.getenv("STACKIT_CONTAINER_ID");
        String ownerEmail = System.getenv("STACKIT_OWNER_EMAIL");

        final Resources r = new Resources();

        Runnable cleanup = () -> {
            try {
                if (r.networkRange != null) {
                    iaasApi.deleteVPCNetworkRange(
                            r.project.getProjectId(),
                            r.vpc.getId(),
                            region,
                            r.networkRange.getVPCNetworkRangeIPv4().getId()
                    );
                    while (true) {
                        try {
                            String status = iaasApi.getVPCNetworkRange(r.project.getProjectId(), r.vpc.getId(), region, r.networkRange.getVPCNetworkRangeIPv4().getId())
                                    .getVPCNetworkRangeIPv4().getStatus();
                            if ("FAILED".equals(status)) {
                                throw new RuntimeException("network range deletion failed");
                            }
                            TimeUnit.SECONDS.sleep(5);
                        } catch (ApiException e) {
                            if (e.getCode() == 404) {
                                break;
                            }
                            throw e;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (r.staticRoute != null) {
                    iaasApi.deleteVPCStaticRoute(
                            r.project.getProjectId(),
                            r.vpc.getId(),
                            region,
                            r.vpcRoutingTable.getId(),
                            r.staticRoute.getId()
                    );
                }
                if (r.vpcRoutingTable != null) {
                    iaasApi.deleteVPCRoutingTable(
                            r.project.getProjectId(),
                            r.vpc.getId(),
                            region,
                            r.vpcRoutingTable.getId()
                    );
                }
                if (r.vpcRegion != null) {
                    iaasApi.deleteVPCRegion(
                            r.project.getProjectId(),
                            r.vpc.getId(),
                            region
                    );
                    while (true) {
                        try {
                            String status = iaasApi.getVPCRegion(r.project.getProjectId(), r.vpc.getId(), region)
                                    .getStatus();
                            if ("FAILED".equals(status)) {
                                throw new RuntimeException("region deletion failed");
                            }
                            TimeUnit.SECONDS.sleep(5);
                        } catch (ApiException e) {
                            if (e.getCode() == 404 || e.getCode() == 400) {
                                break;
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (r.vpc != null) {
                    iaasApi.deleteVPC(
                            r.project.getProjectId(),
                            r.vpc.getId()
                    );
                }
                if (r.project != null) {
                    resourceManagerApi.deleteProject(r.project.getProjectId().toString());
                }
            } catch (ApiException e) {
                System.out.println("cleanup failed, there may be resource left over: " + e.getMessage());
            }
        };

        try {
            // create project
            System.out.println("creating project...");
            Set<Member> members = new HashSet<>();
            members.add(new Member().subject(ownerEmail).role("owner"));
            Map<String, String> labels = new HashMap<>();
            labels.put("enable-vpc", "true");
            r.project = resourceManagerApi.createProject(new CreateProjectPayload()
                    .containerParentId(parentContainerId)
                    .name("vpc-example-project")
                    .members(members)
                    .labels(labels)
            );
            ResourcemanagerWait.createProjectWaitHandler(resourceManagerApi, r.project.getContainerId())
                            .waitWithContextAsync()
                                    .get();
            System.out.println("project created: " + r.project.getProjectId());

            // create vpc
            System.out.println("creating vpc...");
            r.vpc = iaasApi.createVPC(r.project.getProjectId(), new CreateVPCPayload()
                    .name("example-vpc")
                    .description("example-vpc")
            );
            System.out.println("vpc created: " + r.vpc.getId());

            // create region (this must happen before creating a routing table or network area)
            System.out.println("creating region...");
            r.vpcRegion = iaasApi.createVPCRegion(r.project.getProjectId(), r.vpc.getId(), region, new CreateVPCRegionPayload());
            while (true) {
                try {
                    String status = iaasApi.getVPCRegion(r.project.getProjectId(), r.vpc.getId(), region).getStatus();
                    if ("FAILED".equals(status)) {
                        throw new RuntimeException("region creation failed");
                    }
                    if ("CREATED".equals(status)) {
                        break;
                    }
                    TimeUnit.SECONDS.sleep(5);
                } catch (ApiException e) {
                    if (e.getCode() == 404) {
                        break;
                    }
                }
            }
            System.out.println("region created");

            // create routing table
            System.out.println("creating routing table...");
            r.vpcRoutingTable = iaasApi.addVPCRoutingTable(
                    r.project.getProjectId(),
                    r.vpc.getId(),
                    region,
                    new AddVPCRoutingTablePayload()
                            .name("example-routing-table")
            );
            System.out.println("routing table created: " + r.vpcRoutingTable.getId());

            // create a static route
            System.out.println("creating static route...");
            r.staticRoute = iaasApi.addVPCStaticRoute(
                    r.project.getProjectId(),
                    r.vpc.getId(),
                    region,
                    r.vpcRoutingTable.getId(),
                    new AddVPCStaticRoutePayload()
                            .destination(new AddVPCStaticRoutePayloadDestination(new DestinationCIDRv4().cidrv4("0.0.0.0/0")))
                            .nexthop(new AddVPCStaticRoutePayloadNexthop(new NexthopInternet().type("internet")))
            );
            System.out.println("static route created: " + r.staticRoute.getId());

            // create a network range
            System.out.println("creating network range...");
            r.networkRange = iaasApi.createVPCNetworkRange(
                    r.project.getProjectId(),
                    r.vpc.getId(),
                    region,
                    new NetworkRangeIPv4Request()
                            .ipVersion(NetworkRangeIPv4Request.IpVersionEnum.IPV4)
                            .prefix("192.168.1.0/24")
                            .description("example-network-range")
                            .defaultPrefixLen(26L)
                            .maxPrefixLen(29L)
                            .minPrefixLen(16L)
                            .addNameserversItem("10.0.0.1")
                            .addNameserversItem("10.0.0.8")
            );
            while (true) {
                try {
                    String status = iaasApi.getVPCNetworkRange(r.project.getProjectId(), r.vpc.getId(), region, r.networkRange.getVPCNetworkRangeIPv4().getId()).getVPCNetworkRangeIPv4().getStatus();
                    if ("FAILED".equals(status)) {
                        throw new RuntimeException("region creation failed");
                    }
                    if ("CREATED".equals(status)) {
                        break;
                    }
                    TimeUnit.SECONDS.sleep(5);
                } catch (ApiException e) {
                    if (e.getCode() == 404) {
                        break;
                    }
                }
            }
            System.out.println("network range created: " + r.networkRange.getVPCNetworkRangeIPv4().getId());
        } catch (Exception e) {
            System.out.println("failed to create resources: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup.run();
        }
    }

    private static class Resources {
        Project project = null;
        VPC vpc = null;
        RegionalVPC vpcRegion = null;
        VPCRoutingTable vpcRoutingTable = null;
        Route staticRoute = null;
        VPCNetworkRange networkRange = null;
    }
}
