package cloud.stackit.sdk.alb.examples;

import cloud.stackit.sdk.alb.api.AlbApi;
import cloud.stackit.sdk.alb.model.*;
import cloud.stackit.sdk.core.exception.ApiException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

// This examples prerequisite an existing STACKIT network, public ip
// and a server with a network interface
final class AlbExample {

	private AlbExample() {}

	@SuppressWarnings({
		"PMD.CognitiveComplexity",
		"PMD.CommentSize",
		"PMD.CyclomaticComplexity",
		"PMD.NcssCount",
		"PMD.SystemPrintln",
		"PMD.AvoidThrowingRawExceptionTypes"
	})
	public static void main(String[] args) throws IOException {
		// Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		AlbApi applicationLoadBalancerApi = new AlbApi();

		// the id of your STACKIT project, read from env var for this example
		String projectId = System.getenv("STACKIT_PROJECT_ID");
		if (projectId == null || projectId.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}

		@SuppressWarnings("PMD.PrematureDeclaration")
		// the region which should be used to interact with application load balancer
		String region = "eu01";

		// the network id where the listeners and targets reside in
		String networkIdString = System.getenv("STACKIT_NETWORK_ID");
		if (networkIdString == null || networkIdString.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_NETWORK_ID' not found.");
			return;
		}
		UUID networkId = UUID.fromString(networkIdString);

		// the external application load balancer ip address which should be exposed
		String externalAddress = System.getenv("STACKIT_EXTERNAL_ADDRESS");
		if (externalAddress == null || externalAddress.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_EXTERNAL_ADDRESS' not found.");
			return;
		}

		// the listener network interface ipv4 address which will listen to the incoming requests
		String nicIpv4 = System.getenv("STACKIT_LISTENER_NETWORK_INTERFACE_IPV4");
		if (nicIpv4 == null || nicIpv4.isEmpty()) {
			System.err.println(
					"Environment variable 'STACKIT_LISTENER_NETWORK_INTERFACE_IPV4' not found.");
			return;
		}

		try {
			/*
			 * ///////////////////////////////////////////////////////
			 * //            S E R V I C E   P L A N S              //
			 * ///////////////////////////////////////////////////////
			 */
			/* list all available service plans */
			ListPlansResponse listPlans = applicationLoadBalancerApi.listPlans(region);
			System.out.println("Listing service plans:");
			Objects.requireNonNull(listPlans.getValidPlans());
			for (PlanDetails plan : listPlans.getValidPlans()) {
				System.out.println("********************");
				System.out.println("* Plan name: " + plan.getName());
				System.out.println("* Plan ID: " + plan.getPlanId());
				System.out.println("* Flavor name: " + plan.getFlavorName());
				System.out.println("* Max connections: " + plan.getMaxConnections());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //       L O A D   B A L A N C E R   Q U O T A       //
			 * ///////////////////////////////////////////////////////
			 */
			/* get quota of application load balancers in this project */
			System.out.println("\nApplications load balancer quota of this project:");
			GetQuotaResponse getQuota = applicationLoadBalancerApi.getQuota(projectId, region);
			System.out.println("* Max load balancer: " + getQuota.getMaxLoadBalancers());

			/*
			 * ///////////////////////////////////////////////////////
			 * //               C R E D E N T I A L S               //
			 * ///////////////////////////////////////////////////////
			 */
			/* add observability credentials */
			System.out.println(
					"\nAdding observability credentials to application load balancer service:");
			CreateCredentialsResponse newCredentials =
					applicationLoadBalancerApi.createCredentials(
							projectId,
							region,
							new CreateCredentialsPayload()
									.displayName("example-credentials")
									.username("valid-username-for-observability-instance")
									.password("valid-password-for-observability-instance"),
							null);
			Objects.requireNonNull(newCredentials.getCredential());
			System.out.println(
					"* Display name: " + newCredentials.getCredential().getDisplayName());
			System.out.println("* Ref: " + newCredentials.getCredential().getCredentialsRef());
			System.out.println("* Username: " + newCredentials.getCredential().getUsername());

			/* update the created observability credentials*/
			System.out.println("\nUpdating the created credentials:");
			Objects.requireNonNull(newCredentials.getCredential().getCredentialsRef());
			UpdateCredentialsResponse updateCredentials =
					applicationLoadBalancerApi.updateCredentials(
							projectId,
							region,
							newCredentials.getCredential().getCredentialsRef(),
							new UpdateCredentialsPayload()
									.displayName("example-credentials-update")
									.username("valid-username-for-observability-instance-update")
									.password("valid-password-for-observability-instance-update"));
			Objects.requireNonNull(updateCredentials.getCredential());
			System.out.println(
					"* Display name: " + updateCredentials.getCredential().getDisplayName());
			System.out.println("* Ref: " + updateCredentials.getCredential().getCredentialsRef());
			System.out.println("* Username: " + updateCredentials.getCredential().getUsername());

			/* list all credentials in the project */
			System.out.println("\nList all credentials:");
			ListCredentialsResponse listCredentials =
					applicationLoadBalancerApi.listCredentials(projectId, region);
			Objects.requireNonNull(listCredentials.getCredentials());
			for (CredentialsResponse credential : listCredentials.getCredentials()) {
				System.out.println("*************************");
				System.out.println("* Display name: " + credential.getDisplayName());
				System.out.println("* Ref: " + credential.getCredentialsRef());
				System.out.println("* Username: " + credential.getUsername());
			}
			/*
			 * ///////////////////////////////////////////////////////////
			 * //   A P P L I C A T I O N   L O A D   B A L A N C E R   //
			 * ///////////////////////////////////////////////////////////
			 */

			/*
			 * create a new application load balancer
			 *
			 * NOTE: see the docs for available service plans
			 * https://docs.stackit.cloud/products/network/load-balancing-and-content-delivery/load-balancer/nlb-basics/basic-concepts/#service-plans
			 *
			 * */
			System.out.println("\nTrigger creation of application load balancer");
			LoadBalancer newLoadbalancer =
					applicationLoadBalancerApi.createLoadBalancer(
							projectId,
							region,
							new CreateLoadBalancerPayload()
									.name("java-sdk-example")
									.planId("p10")
									.addNetworksItem(
											new Network()
													.networkId(networkId)
													.role(
															Network.RoleEnum
																	.ROLE_LISTENERS_AND_TARGETS))
									.targetPools(
											Collections.singletonList(
													new TargetPool()
															.name("example-target-pool")
															.targetPort(80)
															.targets(
																	Collections.singletonList(
																			new Target()
																					.displayName(
																							"example-server")
																					.ip(nicIpv4)))
															.activeHealthCheck(
																	new ActiveHealthCheck()
																			.healthyThreshold(10)
																			.interval("3s")
																			.intervalJitter("3s")
																			.timeout("3s")
																			.unhealthyThreshold(
																					10))))
									.listeners(
											Collections.singletonList(
													new Listener()
															.port(80)
															.protocol(
																	Listener.ProtocolEnum
																			.PROTOCOL_HTTP)
															.http(
																	new ProtocolOptionsHTTP()
																			.hosts(
																					Collections
																							.singletonList(
																									new HostConfig()
																											.host(
																													"alb.java-sdk-example.runs.onstackit.cloud")
																											.rules(
																													Collections
																															.singletonList(
																																	new Rule()
																																			.targetPool(
																																					"example-target-pool"))))))))
									.disableTargetSecurityGroupAssignment(false)
									.externalAddress(externalAddress)
									.options(
											new LoadBalancerOptions()
													.observability(
															new LoadbalancerOptionObservability()
																	.logs(
																			new LoadbalancerOptionLogs()
																					.credentialsRef(
																							updateCredentials
																									.getCredential()
																									.getCredentialsRef())
																					.pushUrl(
																							"https://logs.stackit<id>.argus.eu01.stackit.cloud/instances/<instance-id>/loki/api/v1/push"))
																	.metrics(
																			new LoadbalancerOptionMetrics()
																					.credentialsRef(
																							updateCredentials
																									.getCredential()
																									.getCredentialsRef())
																					.pushUrl(
																							"https://push.metrics.stackit<id>.argus.eu01.stackit.cloud/instances/<instance-id>/api/v1/receive"))))
									.labels(
											Collections.singletonMap(
													"some-load-balancer-label", "bar")),
							null);

			Objects.requireNonNull(newLoadbalancer.getName());
			/* wait until application load balancer creation is completed */
			while (Objects.equals(
					applicationLoadBalancerApi
							.getLoadBalancer(projectId, region, newLoadbalancer.getName())
							.getStatus(),
					LoadBalancer.StatusEnum.STATUS_PENDING)) {
				System.out.println(
						"Waiting for application load balancer creation to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}

			/* fetch the created application load balancer instance */
			System.out.println("\nGetting created application load balancer instance:");
			LoadBalancer fetchedLoadbalancer =
					applicationLoadBalancerApi.getLoadBalancer(
							projectId, region, "java-sdk-example");
			Objects.requireNonNull(fetchedLoadbalancer.getTargetPools());
			Objects.requireNonNull(fetchedLoadbalancer.getListeners());
			System.out.println("* Name: " + fetchedLoadbalancer.getName());
			System.out.println("* Private address: " + fetchedLoadbalancer.getPrivateAddress());
			System.out.println("* External address: " + fetchedLoadbalancer.getExternalAddress());
			System.out.println("* Status: " + fetchedLoadbalancer.getStatus());
			System.out.println("* Errors: " + fetchedLoadbalancer.getErrors());
			System.out.println("* Version: " + fetchedLoadbalancer.getVersion());
			System.out.println("* Target pools: " + fetchedLoadbalancer.getTargetPools().size());
			System.out.println("* Listeners: " + fetchedLoadbalancer.getListeners().size());

			/* update the application load balancer we just created */
			System.out.println("\nUpdate application load balancer instance:");
			Objects.requireNonNull(fetchedLoadbalancer.getName());
			LoadBalancer updatedLoadbalancer =
					applicationLoadBalancerApi.updateLoadBalancer(
							projectId,
							region,
							fetchedLoadbalancer.getName(),
							new UpdateLoadBalancerPayload()
									.name(fetchedLoadbalancer.getName())
									.externalAddress(externalAddress)
									.version(fetchedLoadbalancer.getVersion())
									.addNetworksItem(
											new Network()
													.networkId(networkId)
													.role(
															Network.RoleEnum
																	.ROLE_LISTENERS_AND_TARGETS))
									.listeners(
											Collections.singletonList(
													new Listener()
															.port(80)
															.protocol(
																	Listener.ProtocolEnum
																			.PROTOCOL_HTTP)
															.http(
																	new ProtocolOptionsHTTP()
																			.hosts(
																					Collections
																							.singletonList(
																									new HostConfig()
																											.host(
																													"alb-updated.java-sdk-example.runs.onstackit.cloud")
																											.rules(
																													Collections
																															.singletonList(
																																	new Rule()
																																			.targetPool(
																																					"example-target-pool-update"))))))))
									.targetPools(
											Collections.singletonList(
													new TargetPool()
															.name("example-target-pool-update")
															.targetPort(80)
															.targets(
																	Collections.singletonList(
																			new Target()
																					.displayName(
																							"example-server-update")
																					.ip(nicIpv4)))
															.activeHealthCheck(
																	new ActiveHealthCheck()
																			.healthyThreshold(5)
																			.interval("5s")
																			.intervalJitter("2s")
																			.timeout("4s")
																			.unhealthyThreshold(
																					5))))
									.labels(
											Collections.singletonMap(
													"some-load-balancer-label", "bar-updated")));

			/* wait until application load balancer update is completed */
			Objects.requireNonNull(updatedLoadbalancer.getName());
			while (Objects.equals(
					applicationLoadBalancerApi
							.getLoadBalancer(projectId, region, updatedLoadbalancer.getName())
							.getStatus(),
					LoadBalancer.StatusEnum.STATUS_PENDING)) {
				System.out.println("Waiting for application load balancer update to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}

			/* fetch the application load balancer we just updated */
			fetchedLoadbalancer =
					applicationLoadBalancerApi.getLoadBalancer(
							projectId, region, fetchedLoadbalancer.getName());
			Objects.requireNonNull(fetchedLoadbalancer.getTargetPools());
			Objects.requireNonNull(fetchedLoadbalancer.getListeners());
			System.out.println("* Name: " + fetchedLoadbalancer.getName());
			System.out.println("* Private address: " + fetchedLoadbalancer.getPrivateAddress());
			System.out.println("* External address: " + fetchedLoadbalancer.getExternalAddress());
			System.out.println("* Status: " + fetchedLoadbalancer.getStatus());
			System.out.println("* Errors: " + fetchedLoadbalancer.getErrors());
			System.out.println("* Version: " + fetchedLoadbalancer.getVersion());
			System.out.println("* Target pools: " + fetchedLoadbalancer.getTargetPools().size());
			System.out.println("* Listeners: " + fetchedLoadbalancer.getListeners().size());

			/* listing all application load balancers */
			System.out.println("\nList all application load balancers:");
			ListLoadBalancersResponse listLoadBalancersResponse =
					applicationLoadBalancerApi.listLoadBalancers(projectId, region, "100", null);
			Objects.requireNonNull(listLoadBalancersResponse.getLoadBalancers());
			for (LoadBalancer loadBalancer : listLoadBalancersResponse.getLoadBalancers()) {
				System.out.println("*****************");
				System.out.println("* Name: " + loadBalancer.getName());
				System.out.println("* Status: " + loadBalancer.getStatus());
				System.out.println("* IP Address: " + loadBalancer.getExternalAddress());
				Objects.requireNonNull(loadBalancer.getListeners());
				System.out.println("* Listeners: " + loadBalancer.getListeners().size());
				Objects.requireNonNull(loadBalancer.getTargetPools());
				System.out.println("* Target pools: " + loadBalancer.getTargetPools().size());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //                  D E L E T I O N                  //
			 * ///////////////////////////////////////////////////////
			 */
			/* trigger deletion of the created application load balancer instance */
			Objects.requireNonNull(fetchedLoadbalancer.getName());
			System.out.println("\nTrigger deletion of the created application load balancer");
			applicationLoadBalancerApi.deleteLoadBalancer(
					projectId, region, fetchedLoadbalancer.getName());

			/* wait for application load balancer deletion to complete */
			while (true) {
				try {
					applicationLoadBalancerApi.getLoadBalancer(
							projectId, region, fetchedLoadbalancer.getName());
					System.out.println(
							"Waiting for application load balancer deletion to complete ...");
					TimeUnit.SECONDS.sleep(5);
				} catch (ApiException e) {
					if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
						break;
					}
				}
			}
			System.out.println("* Successfully deleted");

			/* deleting the credentials we just added */
			Objects.requireNonNull(newCredentials.getCredential().getCredentialsRef());
			System.out.println("\nDeleting the added credentials");
			applicationLoadBalancerApi.deleteCredentials(
					projectId, region, newCredentials.getCredential().getCredentialsRef());
			System.out.println("* Successfully deleted");
		} catch (ApiException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
