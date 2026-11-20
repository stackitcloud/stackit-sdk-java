package cloud.stackit.sdk.vpn.examples;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.vpn.v1api.api.VpnApi;
import cloud.stackit.sdk.vpn.v1api.model.ConnectionList;
import cloud.stackit.sdk.vpn.v1api.model.ConnectionResponse;
import cloud.stackit.sdk.vpn.v1api.model.CreateGatewayConnectionPayload;
import cloud.stackit.sdk.vpn.v1api.model.CreateGatewayPayload;
import cloud.stackit.sdk.vpn.v1api.model.CreateGatewayPayloadAvailabilityZones;
import cloud.stackit.sdk.vpn.v1api.model.GatewayList;
import cloud.stackit.sdk.vpn.v1api.model.GatewayResponse;
import cloud.stackit.sdk.vpn.v1api.model.GatewayStatus;
import cloud.stackit.sdk.vpn.v1api.model.Region;
import cloud.stackit.sdk.vpn.v1api.model.RoutingType;
import cloud.stackit.sdk.vpn.v1api.model.TunnelConfiguration;
import cloud.stackit.sdk.vpn.v1api.model.TunnelConfigurationPhase1;
import cloud.stackit.sdk.vpn.v1api.model.TunnelConfigurationPhase2;
import cloud.stackit.sdk.vpn.v1api.model.UpdateGatewayConnectionPayload;
import cloud.stackit.sdk.vpn.v1api.model.UpdateGatewayPayload;
import cloud.stackit.sdk.vpn.v1api.model.UpdateGatewayPayloadAvailabilityZones;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class VpnExample {
	private static final String REGION = "eu01";

	private VpnExample() {}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.AvoidUsingHardCodedIP",
		"PMD.SystemPrintln",
		"PMD.AvoidThrowingRawExceptionTypes"
	})
	public static void main(String[] args) throws IOException, ApiException, InterruptedException {
		/*
		 * Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		 * STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		 * */

		VpnApi vpnApi = new VpnApi();

		// the id of your STACKIT project, read from env var for this example
		String projectId = System.getenv("STACKIT_PROJECT_ID");
		if (projectId == null || projectId.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}
		Region region = Region.fromValue(REGION);

		try {
			/*
			 * ///////////////////////////////////////////////////////
			 * //              G A T E W A Y S                      //
			 * ///////////////////////////////////////////////////////
			 * */

			/* create a gateway in the project */
			GatewayResponse gatewayResponse =
					vpnApi.createGateway(
							projectId,
							region,
							new CreateGatewayPayload()
									.displayName("example-vpn-gateway")
									.planId("p500")
									.routingType(RoutingType.ROUTE_BASED)
									.availabilityZones(
											new CreateGatewayPayloadAvailabilityZones()
													.tunnel1("eu01-1")
													.tunnel2("eu01-2")));

			Objects.requireNonNull(gatewayResponse.getId());

			/* wait for the gateway creation to complete */
			UUID gatewayId = gatewayResponse.getId();
			Objects.requireNonNull(gatewayId);
			System.out.println(vpnApi.getGateway(projectId, region, gatewayId).getState());
			while (Objects.equals(
					vpnApi.getGateway(projectId, region, gatewayId).getState(),
					GatewayStatus.PENDING)) {
				System.out.println("Waiting for gateway creation to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}
			System.out.println("Gateway created with the ID: " + gatewayId.toString());

			// Delay because of API service: If the Gateway is updated directly after the creation,
			// it will throw an Error 409 "Internal conflict - please retry again later or contact
			// support if the issue persists"
			TimeUnit.SECONDS.sleep(10);

			/* update the gateway we just created (send complete resource because of the PUT endpoint) */
			vpnApi.updateGateway(
					projectId,
					region,
					gatewayId,
					new UpdateGatewayPayload()
							.displayName("example-vpn-gateway-updated")
							.planId("p500")
							.routingType(RoutingType.ROUTE_BASED)
							.availabilityZones(
									new UpdateGatewayPayloadAvailabilityZones()
											.tunnel1("eu01-1")
											.tunnel2("eu01-2"))
							.labels(Collections.singletonMap("some-gateway-label", "updated")));

			/* fetch the gateway we just created */
			GatewayResponse fetchedGateway = vpnApi.getGateway(projectId, region, gatewayId);
			System.out.println("\nFetched gateway: ");
			System.out.println("* Gateway name: " + fetchedGateway.getDisplayName());
			System.out.println("* Id: " + fetchedGateway.getId());

			/* list all available gateways in the project */
			GatewayList gateways = vpnApi.listGateways(projectId, region, null);
			System.out.println("\nAvailable gateways: ");
			for (GatewayResponse gateway : gateways.getGateways()) {
				System.out.println("* " + gateway.getDisplayName());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //               C O N N E C T I O N S               //
			 * ///////////////////////////////////////////////////////
			 * */

			/* create a connection in the gateway */
			TunnelConfiguration tunnelConfiguration =
					new TunnelConfiguration()
							.remoteAddress("198.51.100.10")
							.preSharedKey("Example-super-secret-key-tunnel-0123")
							.phase1(
									new TunnelConfigurationPhase1()
											.dhGroups(
													Collections.singletonList(
															TunnelConfigurationPhase1.DhGroupsEnum
																	.ECP384))
											.encryptionAlgorithms(
													Collections.singletonList(
															TunnelConfigurationPhase1
																	.EncryptionAlgorithmsEnum
																	.AES256))
											.integrityAlgorithms(
													Collections.singletonList(
															TunnelConfigurationPhase1
																	.IntegrityAlgorithmsEnum
																	.SHA2_384)))
							.phase2(
									new TunnelConfigurationPhase2()
											.dhGroups(
													Collections.singletonList(
															TunnelConfigurationPhase2.DhGroupsEnum
																	.ECP384))
											.encryptionAlgorithms(
													Collections.singletonList(
															TunnelConfigurationPhase2
																	.EncryptionAlgorithmsEnum
																	.AES256))
											.integrityAlgorithms(
													Collections.singletonList(
															TunnelConfigurationPhase2
																	.IntegrityAlgorithmsEnum
																	.SHA2_384)));

			ConnectionResponse connectionResponse =
					vpnApi.createGatewayConnection(
							projectId,
							region,
							gatewayId,
							new CreateGatewayConnectionPayload()
									.displayName("example-vpn-connection")
									.tunnel1(tunnelConfiguration)
									.tunnel2(tunnelConfiguration)
									.localSubnets(Collections.singletonList("172.16.0.0/24"))
									.remoteSubnets(Collections.singletonList("172.17.0.0/24")));

			Objects.requireNonNull(connectionResponse.getId());

			UUID connectionId = connectionResponse.getId();
			Objects.requireNonNull(connectionId);
			System.out.println("Gateway created with the ID:" + connectionId.toString());

			/* update the connection we just created */
			vpnApi.updateGatewayConnection(
					projectId,
					region,
					gatewayId,
					connectionId.toString(),
					new UpdateGatewayConnectionPayload()
							.displayName("example-vpn-connection-updated")
							.tunnel1(tunnelConfiguration)
							.tunnel2(tunnelConfiguration)
							.localSubnets(Collections.singletonList("172.16.0.0/24"))
							.remoteSubnets(Collections.singletonList("172.17.0.0/24")));

			/* fetch the connection we just created */
			ConnectionResponse fetchedConnection =
					vpnApi.getGatewayConnection(
							projectId, region, gatewayId, connectionId.toString());
			System.out.println("\nFetched connection: ");
			System.out.println("* Connection name: " + fetchedConnection.getDisplayName());
			System.out.println("* Id: " + fetchedConnection.getId());

			/* list all available connection in the project */
			ConnectionList connections =
					vpnApi.listGatewayConnections(projectId, region, gatewayId, null);
			System.out.println("\nAvailable connections: ");
			for (ConnectionResponse connection : connections.getConnections()) {
				System.out.println("* " + connection.getDisplayName());
			}

			/* delete the connection */
			vpnApi.deleteGatewayConnection(projectId, region, gatewayId, connectionId.toString());
			System.out.println("Deleted connection: " + connectionId.toString());

			/*
			 * ///////////////////////////////////////////////////////
			 * //              D E L E T I O N                      //
			 * ///////////////////////////////////////////////////////
			 * */

			/* delete the gateway we created */
			vpnApi.deleteGateway(projectId, region, gatewayId);
			System.out.println("Started gateway deletion: " + gatewayId);

			/* wait for gateway deletion to complete */
			while (true) {
				try {
					vpnApi.getGateway(projectId, region, gatewayId);
					System.out.println("Waiting for gateway deletion to complete...");
					TimeUnit.SECONDS.sleep(5);
				} catch (ApiException e) {
					if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
						System.out.println("Successfully deleted");
						break;
					}
				}
			}

		} catch (ApiException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
