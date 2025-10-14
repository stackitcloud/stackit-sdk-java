package cloud.stackit.sdk.iaas.examples;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.iaas.api.IaasApi;
import cloud.stackit.sdk.iaas.model.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

final class IaaSExample {

	private IaaSExample() {}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.CognitiveComplexity",
		"PMD.NPathComplexity",
		"PMD.NcssCount",
		"PMD.SystemPrintln"
	})
	public static void main(String[] args) throws IOException {
		/*
		 * Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		 * STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		 * */
		IaasApi iaasApi = new IaasApi();

		// the id of your STACKIT project, read from env var for this example
		String projectIdString = System.getenv("STACKIT_PROJECT_ID");
		if (projectIdString == null || projectIdString.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}
		UUID projectId = UUID.fromString(projectIdString);

		try {
			/*
			 * ///////////////////////////////////////////////////////
			 * //              N E T W O R K S                      //
			 * ///////////////////////////////////////////////////////
			 * */

			/* create a network in the project */
			@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
			Network newNetwork =
					iaasApi.createNetwork(
							projectId,
							new CreateNetworkPayload()
									.name("java-sdk-example-network-01")
									.dhcp(true)
									.routed(false)
									.labels(Collections.singletonMap("some-network-label", "bar"))
									.addressFamily(
											new CreateNetworkAddressFamily()
													.ipv4(
															new CreateNetworkIPv4Body()
																	.addNameserversItem(
																			"8.8.8.8"))));

			/* update the network we just created */
			iaasApi.partialUpdateNetwork(
					projectId,
					newNetwork.getNetworkId(),
					new PartialUpdateNetworkPayload()
							.dhcp(false)
							.labels(Collections.singletonMap("some-network-label", "bar-updated")));

			/* fetch the network we just created */
			Network fetchedNetwork = iaasApi.getNetwork(projectId, newNetwork.getNetworkId());
			System.out.println("\nFetched network: ");
			System.out.println("* Network name: " + fetchedNetwork.getName());
			System.out.println("* Id: " + fetchedNetwork.getNetworkId());
			System.out.println(
					"* DHCP: " + (Boolean.TRUE.equals(fetchedNetwork.getDhcp()) ? "YES" : "NO"));
			System.out.println("* Gateway: " + fetchedNetwork.getGateway());
			System.out.println("* Public IP: " + fetchedNetwork.getPublicIp());

			/* list all available networks in the project */
			NetworkListResponse networks = iaasApi.listNetworks(projectId, null);
			System.out.println("\nAvailable networks: ");
			for (Network network : networks.getItems()) {
				System.out.println("* " + network.getName());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //              I M A G E S                          //
			 * ///////////////////////////////////////////////////////
			 * */

			/* list all available images */
			ImageListResponse images = iaasApi.listImages(projectId, false, null);
			System.out.println("\nAvailable images: ");
			for (Image image : images.getItems()) {
				System.out.println(image.getId() + " | " + image.getName());
			}

			/* get an image */
			UUID imageId =
					images.getItems()
							.get(0)
							.getId(); // we just use a random image id in our example
			assert imageId != null;
			Image fetchedImage = iaasApi.getImage(projectId, imageId);
			System.out.println("\nFetched image:");
			System.out.println("* Image name: " + fetchedImage.getName());
			System.out.println("* Image id: " + fetchedImage.getId());
			System.out.println("* Checksum: " + fetchedImage.getChecksum());
			System.out.println("* Created at: " + fetchedImage.getCreatedAt());
			System.out.println("* Updated at: " + fetchedImage.getUpdatedAt());

			/*
			 * ///////////////////////////////////////////////////////
			 * //              K E Y P A I R S                      //
			 * ///////////////////////////////////////////////////////
			 * */

			/* list all available keypairs */
			KeyPairListResponse keypairs = iaasApi.listKeyPairs(null);
			System.out.println("\nAvailable keypairs: ");
			for (Keypair keypair : keypairs.getItems()) {
				System.out.println("* " + keypair.getName());
			}

			/* create a keypair */
			String publicKey =
					"ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIAcLPdv9r0P+PJWX7C2tdV/7vr8k+fbPcTkC6Z6yjclx";
			Keypair newKeypair =
					iaasApi.createKeyPair(
							new CreateKeyPairPayload()
									.name("java-sdk-example-keypair-01")
									.publicKey(publicKey));
			System.out.println("\nKeypair created: " + newKeypair.getName());

			/* update the keypair */
			assert newKeypair.getName() != null;
			iaasApi.updateKeyPair(
					newKeypair.getName(),
					new UpdateKeyPairPayload()
							.labels(Collections.singletonMap("some-keypair-label", "bar")));

			/* fetch the keypair we just created / updated */
			Keypair fetchedKeypair = iaasApi.getKeyPair(newKeypair.getName());
			System.out.println("\nFetched key pair: ");
			System.out.println("* Name: " + fetchedKeypair.getName());
			if (fetchedKeypair.getLabels() != null) {
				System.out.println("* Labels: " + fetchedKeypair.getLabels().toString());
			}
			System.out.println("* Fingerprint: " + fetchedKeypair.getFingerprint());
			System.out.println("* Public key: " + fetchedKeypair.getPublicKey());

			/*
			 * ///////////////////////////////////////////////////////
			 * //              S E R V E R S                        //
			 * ///////////////////////////////////////////////////////
			 * */

			/* list all available machine types */
			MachineTypeListResponse machineTypes = iaasApi.listMachineTypes(projectId, null);
			System.out.println("\nAvailable machine types: ");
			for (MachineType machineType : machineTypes.getItems()) {
				System.out.println("* " + machineType.getName());
			}

			/* fetch details about a machine type */
			MachineType fetchedMachineType =
					iaasApi.getMachineType(projectId, machineTypes.getItems().get(0).getName());
			System.out.println("\nFetched machine type: ");
			System.out.println("* Machine type name: " + fetchedMachineType.getName());
			System.out.println("* Description: " + fetchedMachineType.getDescription());
			System.out.println("* Disk size: " + fetchedMachineType.getDisk());
			System.out.println("* RAM: " + fetchedMachineType.getRam());
			System.out.println("* vCPUs: " + fetchedMachineType.getVcpus());
			System.out.println("* Extra specs: " + fetchedMachineType.getExtraSpecs());

			/*
			 * create a server
			 *
			 * NOTE: see the following link for available machine types
			 * https://docs.stackit.cloud/stackit/en/virtual-machine-flavors-75137231.html
			 *
			 * */
			Server newServer =
					iaasApi.createServer(
							projectId,
							new CreateServerPayload()
									.name("java-sdk-example-server-01")
									.machineType("t2i.1")
									.imageId(imageId)
									.labels(Collections.singletonMap("foo", "bar"))
									// add the keypair we created above
									.keypairName(newKeypair.getName())
									// add the server to the network we created above
									.networking(
											new CreateServerPayloadNetworking(
													new CreateServerNetworking()
															.networkId(
																	newNetwork.getNetworkId()))));
			assert newServer.getId() != null;

			/* wait for the server creation to complete */
			UUID serverId = newServer.getId();
			assert serverId != null;
			while (Objects.equals(
					iaasApi.getServer(projectId, serverId, false).getStatus(), "CREATING")) {
				System.out.println("Waiting for server creation to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}

			/* update the server we just created */
			iaasApi.updateServer(
					projectId,
					newServer.getId(),
					new UpdateServerPayload()
							.labels(Collections.singletonMap("foo", "bar-updated")));

			/* list all servers */
			ServerListResponse servers = iaasApi.listServers(projectId, false, null);
			System.out.println("\nAvailable servers: ");
			for (Server server : servers.getItems()) {
				System.out.println("* " + server.getId() + " | " + server.getName());
			}

			/* fetch the server we just created */
			Server fetchedServer = iaasApi.getServer(projectId, serverId, false);
			System.out.println("\nFetched server:");
			System.out.println("* Name: " + fetchedServer.getName());
			System.out.println("* Id: " + fetchedServer.getId());
			if (fetchedServer.getLabels() != null) {
				System.out.println("* Labels: " + fetchedServer.getLabels().toString());
			}
			System.out.println("* Machine type: " + fetchedServer.getMachineType());
			System.out.println("* Created at: " + fetchedServer.getCreatedAt());
			System.out.println("* Updated at: " + fetchedServer.getUpdatedAt());
			System.out.println("* Launched at: " + fetchedServer.getLaunchedAt());

			/* stop the server we just created */
			iaasApi.stopServer(projectId, serverId);
			/* wait for the server to stop */
			while (!Objects.equals(
					iaasApi.getServer(projectId, serverId, false).getPowerStatus(), "STOPPED")) {
				System.out.println("Waiting for server " + serverId + " to stop...");
				TimeUnit.SECONDS.sleep(5);
			}

			/* boot the server we just created */
			iaasApi.startServer(projectId, serverId);
			/* wait for the server to boot */
			while (!Objects.equals(
					iaasApi.getServer(projectId, serverId, false).getPowerStatus(), "RUNNING")) {
				System.out.println("Waiting for server " + serverId + " to boot...");
				TimeUnit.SECONDS.sleep(5);
			}

			/* reboot the server we just created */
			iaasApi.rebootServer(projectId, serverId, null);

			/*
			 * ///////////////////////////////////////////////////////
			 * //              D E L E T I O N                      //
			 * ///////////////////////////////////////////////////////
			 * */

			/* delete the server we just created */
			iaasApi.deleteServer(projectId, serverId);
			System.out.println("Deleted server: " + serverId);

			/* wait for server deletion to complete */
			while (true) {
				try {
					iaasApi.getServer(projectId, serverId, false);
					System.out.println("Waiting for server deletion to complete...");
					TimeUnit.SECONDS.sleep(5);
				} catch (ApiException e) {
					if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
						break;
					}
				}
			}

			/* delete the keypair we just created */
			iaasApi.deleteKeyPair(newKeypair.getName());
			System.out.println("Deleted key pair: " + newKeypair.getName());

			/* delete the network we just created */
			iaasApi.deleteNetwork(projectId, newNetwork.getNetworkId());
			System.out.println("Deleted network: " + newNetwork.getNetworkId());

		} catch (ApiException | InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
