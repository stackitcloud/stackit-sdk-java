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
import java.util.logging.Level;
import java.util.logging.Logger;

final class IaaSExample {
	private static final Logger LOGGER = Logger.getLogger(IaaSExample.class.getName());

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.CognitiveComplexity",
		"PMD.NPathComplexity",
		"PMD.NcssCount"
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
			LOGGER.severe("Environment variable 'STACKIT_PROJECT_ID' not found.");
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
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nFetched network: ");
				LOGGER.info("* Network name: " + fetchedNetwork.getName());
				LOGGER.info("* Id: " + fetchedNetwork.getNetworkId());
				LOGGER.info("* DHCP: " + (Boolean.TRUE.equals(fetchedNetwork.getDhcp()) ? "YES" : "NO"));
				LOGGER.info("* Gateway: " + fetchedNetwork.getGateway());
				LOGGER.info("* Public IP: " + fetchedNetwork.getPublicIp());
			}

			/* list all available networks in the project */
			NetworkListResponse networks = iaasApi.listNetworks(projectId, null);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nAvailable networks: ");
				for (Network network : networks.getItems()) {
					LOGGER.info("* " + network.getName());
				}
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //              I M A G E S                          //
			 * ///////////////////////////////////////////////////////
			 * */

			/* list all available images */
			ImageListResponse images = iaasApi.listImages(projectId, false, null);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nAvailable images: ");
				for (Image image : images.getItems()) {
					LOGGER.info(image.getId() + " | " + image.getName());
				}
			}

			/* get an image */
			UUID imageId =
					images.getItems()
							.get(0)
							.getId(); // we just use a random image id in our example
			assert imageId != null;
			Image fetchedImage = iaasApi.getImage(projectId, imageId);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nFetched image:");
				LOGGER.info("* Image name: " + fetchedImage.getName());
				LOGGER.info("* Image id: " + fetchedImage.getId());
				LOGGER.info("* Checksum: " + fetchedImage.getChecksum());
				LOGGER.info("* Created at: " + fetchedImage.getCreatedAt());
				LOGGER.info("* Updated at: " + fetchedImage.getUpdatedAt());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //              K E Y P A I R S                      //
			 * ///////////////////////////////////////////////////////
			 * */

			/* list all available keypairs */
			KeyPairListResponse keypairs = iaasApi.listKeyPairs(null);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nAvailable keypairs: ");
				for (Keypair keypair : keypairs.getItems()) {
					LOGGER.info("* " + keypair.getName());
				}
			}

			/* create a keypair */
			String publicKey =
					"ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIAcLPdv9r0P+PJWX7C2tdV/7vr8k+fbPcTkC6Z6yjclx";
			Keypair newKeypair =
					iaasApi.createKeyPair(
							new CreateKeyPairPayload()
									.name("java-sdk-example-keypair-01")
									.publicKey(publicKey));
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nKeypair created: " + newKeypair.getName());
			}

			/* update the keypair */
			assert newKeypair.getName() != null;
			iaasApi.updateKeyPair(
					newKeypair.getName(),
					new UpdateKeyPairPayload()
							.labels(Collections.singletonMap("some-keypair-label", "bar")));

			/* fetch the keypair we just created / updated */
			Keypair fetchedKeypair = iaasApi.getKeyPair(newKeypair.getName());
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nFetched key pair: ");
				LOGGER.info("* Name: " + fetchedKeypair.getName());
				if (fetchedKeypair.getLabels() != null) {
					LOGGER.info("* Labels: " + fetchedKeypair.getLabels().toString()); // NOPMD GuardLogStatement
				}
				LOGGER.info("* Fingerprint: " + fetchedKeypair.getFingerprint());
				LOGGER.info("* Public key: " + fetchedKeypair.getPublicKey());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //              S E R V E R S                        //
			 * ///////////////////////////////////////////////////////
			 * */

			/* list all available machine types */
			MachineTypeListResponse machineTypes = iaasApi.listMachineTypes(projectId, null);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nAvailable machine types: ");
				for (MachineType machineType : machineTypes.getItems()) {
					LOGGER.info("* " + machineType.getName());
				}
			}

			/* fetch details about a machine type */
			MachineType fetchedMachineType =
					iaasApi.getMachineType(projectId, machineTypes.getItems().get(0).getName());
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nFetched machine type: ");
				LOGGER.info("* Machine type name: " + fetchedMachineType.getName());
				LOGGER.info("* Description: " + fetchedMachineType.getDescription());
				LOGGER.info("* Disk size: " + fetchedMachineType.getDisk());
				LOGGER.info("* RAM: " + fetchedMachineType.getRam());
				LOGGER.info("* vCPUs: " + fetchedMachineType.getVcpus());
				LOGGER.info("* Extra specs: " + fetchedMachineType.getExtraSpecs());
			}

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
				LOGGER.info("Waiting for server creation to complete ...");
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
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nAvailable servers: ");
				for (Server server : servers.getItems()) {
					LOGGER.info("* " + server.getId() + " | " + server.getName());
				}
			}

			/* fetch the server we just created */
			Server fetchedServer = iaasApi.getServer(projectId, serverId, false);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("\nFetched server:");
				LOGGER.info("* Name: " + fetchedServer.getName());
				LOGGER.info("* Id: " + fetchedServer.getId());
				if (fetchedServer.getLabels() != null) {
					LOGGER.info("* Labels: " + fetchedServer.getLabels().toString()); // NOPMD GuardLogStatement
				}
				LOGGER.info("* Machine type: " + fetchedServer.getMachineType());
				LOGGER.info("* Created at: " + fetchedServer.getCreatedAt());
				LOGGER.info("* Updated at: " + fetchedServer.getUpdatedAt());
				LOGGER.info("* Launched at: " + fetchedServer.getLaunchedAt());
			}

			/* stop the server we just created */
			iaasApi.stopServer(projectId, serverId);
			/* wait for the server to stop */
			while (!Objects.equals(
					iaasApi.getServer(projectId, serverId, false).getPowerStatus(), "STOPPED")) {
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.info("Waiting for server " + serverId + " to stop...");
				}
				TimeUnit.SECONDS.sleep(5);
			}

			/* boot the server we just created */
			iaasApi.startServer(projectId, serverId);
			/* wait for the server to boot */
			while (!Objects.equals(
					iaasApi.getServer(projectId, serverId, false).getPowerStatus(), "RUNNING")) {
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.info("Waiting for server " + serverId + " to boot...");
				}
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
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("Deleted server: " + serverId);
			}

			/* wait for server deletion to complete */
			while (true) {
				try {
					iaasApi.getServer(projectId, serverId, false);
					if (LOGGER.isLoggable(Level.INFO)) {
						LOGGER.info("Waiting for server deletion to complete...");
					}
					TimeUnit.SECONDS.sleep(5);
				} catch (ApiException e) {
					if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
						break;
					}
				}
			}

			/* delete the keypair we just created */
			iaasApi.deleteKeyPair(newKeypair.getName());
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("Deleted key pair: " + newKeypair.getName());
			}

			/* delete the network we just created */
			iaasApi.deleteNetwork(projectId, newNetwork.getNetworkId());
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("Deleted network: " + newNetwork.getNetworkId());
			}

		} catch (ApiException | InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private IaaSExample() {}
}
