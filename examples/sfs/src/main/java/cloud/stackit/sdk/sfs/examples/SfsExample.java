package cloud.stackit.sdk.sfs.examples;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.sfs.api.SfsApi;
import cloud.stackit.sdk.sfs.model.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class SfsExample {
	private static final String REGION = "eu01";
	private static final String CREATED_STATE = "created";
	private static final String ERROR_STATE = "error";

	private SfsExample() {}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.CognitiveComplexity",
		"PMD.NPathComplexity",
		"PMD.NcssCount",
		"PMD.SystemPrintln",
		"PMD.AvoidThrowingRawExceptionTypes",
		"PMD.AvoidDuplicateLiterals"
	})
	public static void main(String[] args) throws IOException {
		// Credentials are read from the credentialsFile in `~/.stackit/credentials.json` or the env
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY
		CoreConfiguration configuration = new CoreConfiguration();

		OkHttpClient httpClient = new OkHttpClient();
		KeyFlowAuthenticator authenticator = new KeyFlowAuthenticator(httpClient, configuration);
		httpClient =
				httpClient
						.newBuilder()
						.authenticator(authenticator)
						// Some requests may take a few seconds.
						// To prevent a timeout, we increase the read timeout to 60 seconds
						.readTimeout(Duration.ofSeconds(60))
						.build();

		SfsApi sfsApi = new SfsApi(httpClient);

		// the id of your STACKIT project, read from env var for this example
		String projectId = System.getenv("STACKIT_PROJECT_ID");
		if (projectId == null || projectId.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}

		try {
			/*
			 * ///////////////////////////////////////////////////////
			 * //       P E R F O R M A N C E   C L A S S E S       //
			 * ///////////////////////////////////////////////////////
			 */
			System.out.println("\nList performance classes:");
			ListPerformanceClassesResponse listPerformanceClassesResponse =
					sfsApi.listPerformanceClasses();
			Objects.requireNonNull(listPerformanceClassesResponse.getPerformanceClasses());
			for (PerformanceClass performanceClass :
					listPerformanceClassesResponse.getPerformanceClasses()) {
				System.out.println("*************************");
				System.out.println("* Performance class name: " + performanceClass.getName());
				System.out.println("* IOPS: " + performanceClass.getIops());
				System.out.println("* Throughput: " + performanceClass.getThroughput());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //             R E S O U R C E   P O O L             //
			 * ///////////////////////////////////////////////////////
			 */
			/* creating a new resource pool */
			System.out.println("\nTrigger resource pool creation:");
			CreateResourcePoolResponse createResourcePoolResponse =
					sfsApi.createResourcePool(
							projectId,
							REGION,
							new CreateResourcePoolPayload()
									.name("java-sdk-example-resource-pool-2")
									.sizeGigabytes(512)
									.performanceClass("Standard")
									.ipAcl(Collections.singletonList("192.168.0.0/24"))
									.availabilityZone("eu01-m")
									.snapshotsAreVisible(false)
									.labels(Collections.singletonMap("environment", "dev")));
			Objects.requireNonNull(createResourcePoolResponse.getResourcePool());
			String createdResourcePoolId =
					Objects.requireNonNull(createResourcePoolResponse.getResourcePool().getId());
			System.out.println("* ID: " + createdResourcePoolId);
			System.out.println(
					"* Status: " + createResourcePoolResponse.getResourcePool().getState());

			/* wait for the creation to complete */
			Objects.requireNonNull(createdResourcePoolId);
			GetResourcePoolResponse getResourcePoolResponse;
			while (true) {
				// fetch the resource pool in creation
				getResourcePoolResponse =
						sfsApi.getResourcePool(projectId, REGION, createdResourcePoolId);

				Objects.requireNonNull(getResourcePoolResponse.getResourcePool());
				Objects.requireNonNull(getResourcePoolResponse.getResourcePool().getState());

				// Check if it's in the wanted "created" state
				if (CREATED_STATE.equals(getResourcePoolResponse.getResourcePool().getState())) {
					break;
				}

				// Check if it's in an error state
				if (ERROR_STATE.equals(getResourcePoolResponse.getResourcePool().getState())) {
					System.err.println("created resource pool is in error state");
					return;
				}

				System.out.println("Waiting for creation of resource pool to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}
			System.out.println("Creation of resource pool finished");

			/* print details of the created resource pool */
			System.out.println("\nResource pool details:");
			System.out.println("* ID: " + getResourcePoolResponse.getResourcePool().getId());
			System.out.println("* Name: " + getResourcePoolResponse.getResourcePool().getName());
			System.out.println(
					"* Mount Path: " + getResourcePoolResponse.getResourcePool().getMountPath());
			System.out.println(
					"* Availability Zone: "
							+ getResourcePoolResponse.getResourcePool().getAvailabilityZone());
			Objects.requireNonNull(getResourcePoolResponse.getResourcePool().getPerformanceClass());
			System.out.println(
					"* Performance Class: "
							+ getResourcePoolResponse
									.getResourcePool()
									.getPerformanceClass()
									.getName());
			Objects.requireNonNull(getResourcePoolResponse.getResourcePool().getSpace());
			System.out.println(
					"* Size (in Gigabytes): "
							+ getResourcePoolResponse
									.getResourcePool()
									.getSpace()
									.getSizeGigabytes());
			System.out.println("* State: " + getResourcePoolResponse.getResourcePool().getState());

			/* list all resource pools in the current project */
			System.out.println("\nList all resource pools:");
			ListResourcePoolsResponse listResourcePools =
					sfsApi.listResourcePools(projectId, REGION);
			Objects.requireNonNull(listResourcePools.getResourcePools());
			for (ResourcePool resourcePool : listResourcePools.getResourcePools()) {
				System.out.println("*************************");
				System.out.println("* ID: " + resourcePool.getId());
				System.out.println("* Name: " + resourcePool.getName());
				System.out.println("* Mount Path: " + resourcePool.getMountPath());
				System.out.println("* Availability Zone: " + resourcePool.getAvailabilityZone());
				Objects.requireNonNull(resourcePool.getPerformanceClass());
				System.out.println(
						"* Performance class: " + resourcePool.getPerformanceClass().getName());
				Objects.requireNonNull(resourcePool.getSpace());
				System.out.println(
						"* Size (in Gigabytes): " + resourcePool.getSpace().getSizeGigabytes());
				System.out.println("* State: " + resourcePool.getState());
			}

			/* update the created resource pool */
			System.out.println("\nTrigger an update of the resource pool:");
			UpdateResourcePoolResponse updateResourcePoolResponse =
					sfsApi.updateResourcePool(
							projectId,
							REGION,
							createdResourcePoolId,
							new UpdateResourcePoolPayload()
									.ipAcl(null) // Set explicit to null, to prevent any updates.
									// Otherwise, it's an empty list
									.snapshotsAreVisible(true)
									.performanceClass("Premium")
									.sizeGigabytes(550)
									.labels(Collections.singletonMap("environment", "qa")));
			Objects.requireNonNull(updateResourcePoolResponse.getResourcePool());
			System.out.println(
					"* State: " + updateResourcePoolResponse.getResourcePool().getState());

			/* wait for the update to complete */
			Objects.requireNonNull(createdResourcePoolId);
			while (true) {
				// fetch the resource pool in update
				getResourcePoolResponse =
						sfsApi.getResourcePool(projectId, REGION, createdResourcePoolId);

				Objects.requireNonNull(getResourcePoolResponse.getResourcePool());
				Objects.requireNonNull(getResourcePoolResponse.getResourcePool().getState());

				// Check if it's in the wanted "created" state
				if (CREATED_STATE.equals(getResourcePoolResponse.getResourcePool().getState())) {
					break;
				}

				// Check if it's in an error state
				if (ERROR_STATE.equals(getResourcePoolResponse.getResourcePool().getState())) {
					System.err.println("updated resource pool is in error state");
					return;
				}

				System.out.println("Waiting for resource pool update to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}
			System.out.println("* Update finished");

			/* print details of the updated resource pool*/
			System.out.println("\nUpdated resource pool details:");
			System.out.println("* ID: " + getResourcePoolResponse.getResourcePool().getId());
			System.out.println("* Name: " + getResourcePoolResponse.getResourcePool().getName());
			System.out.println(
					"* Mount Path: " + getResourcePoolResponse.getResourcePool().getMountPath());
			System.out.println(
					"* Availability Zone: "
							+ getResourcePoolResponse.getResourcePool().getAvailabilityZone());
			Objects.requireNonNull(getResourcePoolResponse.getResourcePool().getPerformanceClass());
			System.out.println(
					"* Performance class: "
							+ getResourcePoolResponse
									.getResourcePool()
									.getPerformanceClass()
									.getName());
			Objects.requireNonNull(getResourcePoolResponse.getResourcePool().getSpace());
			System.out.println(
					"* Size (in Gigabytes): "
							+ getResourcePoolResponse
									.getResourcePool()
									.getSpace()
									.getSizeGigabytes());
			System.out.println("* State: " + getResourcePoolResponse.getResourcePool().getState());

			/*
			 * ///////////////////////////////////////////////////////
			 * //    R E S O U R C E   P O O L   S N A P S H O T    //
			 * ///////////////////////////////////////////////////////
			 */
			/* create a new snapshot of the resource pool */
			System.out.println("\nCreate resource pool snapshot:");
			CreateResourcePoolSnapshotResponse createResourcePoolSnapshotResponse =
					sfsApi.createResourcePoolSnapshot(
							projectId,
							REGION,
							createdResourcePoolId,
							new CreateResourcePoolSnapshotPayload()
									.name("first-snapshot")
									.comment("my first snapshot"));
			Objects.requireNonNull(createResourcePoolSnapshotResponse.getResourcePoolSnapshot());
			System.out.println(
					"* Snapshot name: "
							+ createResourcePoolSnapshotResponse
									.getResourcePoolSnapshot()
									.getSnapshotName());
			System.out.println(
					"* Comment: "
							+ createResourcePoolSnapshotResponse
									.getResourcePoolSnapshot()
									.getComment());
			System.out.println(
					"* Resource pool ID: "
							+ createResourcePoolSnapshotResponse
									.getResourcePoolSnapshot()
									.getResourcePoolId());

			/* list all snapshots of the resource pool */
			System.out.println("\nList all snapshots of the resource pool:");
			ListResourcePoolSnapshotsResponse listResourcePoolsSnapshots =
					sfsApi.listResourcePoolSnapshots(projectId, REGION, createdResourcePoolId);
			Objects.requireNonNull(listResourcePoolsSnapshots.getResourcePoolSnapshots());
			for (ResourcePoolSnapshot snapshot :
					listResourcePoolsSnapshots.getResourcePoolSnapshots()) {
				System.out.println("*************************");
				System.out.println("* Snapshot name: " + snapshot.getSnapshotName());
				System.out.println("* Comment: " + snapshot.getComment());
				System.out.println("* Size (in Gigabytes): " + snapshot.getSizeGigabytes());
				System.out.println("* Created at: " + snapshot.getCreatedAt());
				System.out.println("* Resource pool ID: " + snapshot.getResourcePoolId());
			}

			/* delete the created snapshot */
			System.out.println("\nTrigger deletion of the resource pool snapshot");
			Objects.requireNonNull(
					createResourcePoolSnapshotResponse.getResourcePoolSnapshot().getSnapshotName());
			sfsApi.deleteResourcePoolSnapshot(
					projectId,
					REGION,
					createdResourcePoolId,
					createResourcePoolSnapshotResponse.getResourcePoolSnapshot().getSnapshotName());
			System.out.println("Deleted the resource pool snapshot");

			/*
			 * ///////////////////////////////////////////////////////
			 * //       S H A R E   E X P O R T   P O L I C Y       //
			 * ///////////////////////////////////////////////////////
			 */
			/* create a new export policy */
			CreateShareExportPolicyResponse createShareExportPolicyResponse =
					sfsApi.createShareExportPolicy(
							projectId,
							REGION,
							new CreateShareExportPolicyPayload()
									.name("my-export-policy")
									.labels(Collections.singletonMap("environment", "dev"))
									.rules(
											Arrays.asList(
													new CreateShareExportPolicyRequestRule()
															.ipAcl(
																	Collections.singletonList(
																			"192.168.3.0/24"))
															.description("Java SDK example")
															.order(1)
															.superUser(false),
													new CreateShareExportPolicyRequestRule()
															.ipAcl(
																	Collections.singletonList(
																			"192.168.4.0/24"))
															.description("Java SDK example 2")
															.order(2)
															.readOnly(true))));

			System.out.println("\nCreated new share export policy:");
			Objects.requireNonNull(createShareExportPolicyResponse.getShareExportPolicy());
			System.out.println(
					"* Share export policy ID: "
							+ createShareExportPolicyResponse.getShareExportPolicy().getId());
			System.out.println(
					"* Name: " + createShareExportPolicyResponse.getShareExportPolicy().getName());
			System.out.println(
					"* Shares using export policy: "
							+ createShareExportPolicyResponse
									.getShareExportPolicy()
									.getSharesUsingExportPolicy());
			Objects.requireNonNull(
					createShareExportPolicyResponse.getShareExportPolicy().getRules());
			System.out.println(
					"* Rules amount: "
							+ createShareExportPolicyResponse
									.getShareExportPolicy()
									.getRules()
									.size());

			/* update the created share export policy */
			Objects.requireNonNull(createShareExportPolicyResponse.getShareExportPolicy().getId());
			UpdateShareExportPolicyResponse updateShareExportPolicyResponse =
					sfsApi.updateShareExportPolicy(
							projectId,
							REGION,
							createShareExportPolicyResponse.getShareExportPolicy().getId(),
							new UpdateShareExportPolicyPayload()
									.rules(
											Collections.singletonList(
													new UpdateShareExportPolicyBodyRule()
															.ipAcl(
																	Collections.singletonList(
																			"192.168.2.0/24"))
															.order(1)
															.setUuid(true)
															.readOnly(true)
															.superUser(false))));

			System.out.println("\nUpdated the created export policy:");
			Objects.requireNonNull(updateShareExportPolicyResponse.getShareExportPolicy());
			System.out.println(
					"* Share export policy ID: "
							+ updateShareExportPolicyResponse.getShareExportPolicy().getId());
			System.out.println(
					"* Name: " + updateShareExportPolicyResponse.getShareExportPolicy().getName());
			System.out.println(
					"* Shares using export policy: "
							+ updateShareExportPolicyResponse
									.getShareExportPolicy()
									.getSharesUsingExportPolicy());
			Objects.requireNonNull(
					updateShareExportPolicyResponse.getShareExportPolicy().getRules());
			System.out.println(
					"* Rules amount: "
							+ updateShareExportPolicyResponse
									.getShareExportPolicy()
									.getRules()
									.size());

			/* list all share export policies */
			System.out.println("\nList all share export policies of the project:");
			ListShareExportPoliciesResponse listShareExportPoliciesResponse =
					sfsApi.listShareExportPolicies(projectId, REGION);
			Objects.requireNonNull(listShareExportPoliciesResponse.getShareExportPolicies());
			for (ShareExportPolicy shareExportPolicy :
					listShareExportPoliciesResponse.getShareExportPolicies()) {
				System.out.println("*************************");
				System.out.println("Share export policy ID: " + shareExportPolicy.getId());
				System.out.println("* Name: " + shareExportPolicy.getName());
				System.out.println(
						"* Shares using export policy: "
								+ shareExportPolicy.getSharesUsingExportPolicy());
				Objects.requireNonNull(shareExportPolicy.getRules());
				System.out.println("* Rules amount: " + shareExportPolicy.getRules().size());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //                     S H A R E                     //
			 * ///////////////////////////////////////////////////////
			 */
			/* create a new share */
			System.out.println("\nTrigger creation of share:");
			CreateShareResponse createShareResponse =
					sfsApi.createShare(
							projectId,
							REGION,
							createdResourcePoolId,
							new CreateSharePayload()
									.name("java-sdk-example-share")
									.exportPolicyName(
											createShareExportPolicyResponse
													.getShareExportPolicy()
													.getName())
									.labels(Collections.singletonMap("environment", "dev"))
									.spaceHardLimitGigabytes(100));

			/* wait for the creation to complete */
			Objects.requireNonNull(createShareResponse.getShare());
			Objects.requireNonNull(createShareResponse.getShare().getId());
			GetShareResponse getShareResponse;
			while (true) {
				// fetch the share in creation
				getShareResponse =
						sfsApi.getShare(
								projectId,
								REGION,
								createdResourcePoolId,
								createShareResponse.getShare().getId());

				Objects.requireNonNull(getShareResponse.getShare());
				Objects.requireNonNull(getShareResponse.getShare().getState());

				// Check if it's in the wanted "created" state
				if (CREATED_STATE.equals(getShareResponse.getShare().getState())) {
					break;
				}

				// Check if it's in an error state
				if (ERROR_STATE.equals(getShareResponse.getShare().getState())) {
					System.err.println("created share is in error state");
					return;
				}

				System.out.println("Waiting for share create to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}
			System.out.println("Creation of share finished");

			/* print details of the created share */
			System.out.println("\nCreated share details:");
			System.out.println("* Share ID: " + getShareResponse.getShare().getId());
			System.out.println("* Name: " + getShareResponse.getShare().getName());
			System.out.println("* Mount Path: " + getShareResponse.getShare().getMountPath());
			System.out.println(
					"* Space hard limit (in Gigabytes): "
							+ getShareResponse.getShare().getSpaceHardLimitGigabytes());
			Objects.requireNonNull(getShareResponse.getShare().getExportPolicy());
			System.out.println(
					"* Export policy: " + getShareResponse.getShare().getExportPolicy().getName());
			System.out.println("* State: " + getShareResponse.getShare().getState());
			System.out.println("* Created at: " + getShareResponse.getShare().getCreatedAt());

			/* update the created share */
			System.out.println("\nTrigger update of share:");
			UpdateShareResponse updateShareResponse =
					sfsApi.updateShare(
							projectId,
							REGION,
							createdResourcePoolId,
							createShareResponse.getShare().getId(),
							new UpdateSharePayload()
									// Set exportPolicyName to null to prevent that the previous
									// config will be removed
									.exportPolicyName(null)
									.spaceHardLimitGigabytes(200)
									.labels(Collections.singletonMap("environment", "qa")));

			Objects.requireNonNull(updateShareResponse.getShare());
			Objects.requireNonNull(updateShareResponse.getShare().getId());
			while (true) {
				// fetch the share in update
				getShareResponse =
						sfsApi.getShare(
								projectId,
								REGION,
								createdResourcePoolId,
								updateShareResponse.getShare().getId());

				Objects.requireNonNull(getShareResponse.getShare());
				Objects.requireNonNull(getShareResponse.getShare().getState());

				// Check if it's in the wanted "created" state
				if (CREATED_STATE.equals(getShareResponse.getShare().getState())) {
					break;
				}

				// Check if it's in an error state
				if (ERROR_STATE.equals(getShareResponse.getShare().getState())) {
					System.err.println("updated share is in error state");
					return;
				}

				System.out.println("Waiting for share update to complete ...");
				TimeUnit.SECONDS.sleep(5);
			}
			System.out.println("Update of share finished");

			/* print details of the update share */
			System.out.println("\nUpdated share:");
			System.out.println("* Share ID: " + getShareResponse.getShare().getId());
			System.out.println("* Name: " + getShareResponse.getShare().getName());
			System.out.println("* Mount Path: " + getShareResponse.getShare().getMountPath());
			System.out.println(
					"* Space hard limit (in Gigabytes): "
							+ getShareResponse.getShare().getSpaceHardLimitGigabytes());
			if (getShareResponse.getShare().getExportPolicy() != null) {
				System.out.println(
						"* Export policy: "
								+ getShareResponse.getShare().getExportPolicy().getName());
			}
			System.out.println("* State: " + getShareResponse.getShare().getState());
			System.out.println("* Created at: " + getShareResponse.getShare().getCreatedAt());

			/* list all shares of the created resource pool */
			System.out.println("\nList all shares of the resource pool:");
			ListSharesResponse listSharesResponse =
					sfsApi.listShares(projectId, REGION, createdResourcePoolId);
			Objects.requireNonNull(listSharesResponse.getShares());
			for (Share share : listSharesResponse.getShares()) {
				System.out.println("*************************");
				System.out.println("* Share ID: " + share.getId());
				System.out.println("* Name: " + share.getName());
				System.out.println("* Mount Path: " + share.getMountPath());
				System.out.println(
						"* Space hard limit (in Gigabytes): " + share.getSpaceHardLimitGigabytes());
				if (share.getExportPolicy() != null) {
					System.out.println("* Export policy: " + share.getExportPolicy().getName());
				}
				System.out.println("* State: " + share.getState());
				System.out.println("* Created at: " + share.getCreatedAt());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //                  D E L E T I O N                  //
			 * ///////////////////////////////////////////////////////
			 */
			/* delete the created share */
			System.out.println(
					"\nTrigger deletion of share with ID: "
							+ createShareResponse.getShare().getId());
			sfsApi.deleteShare(
					projectId,
					REGION,
					createdResourcePoolId,
					createShareResponse.getShare().getId());

			/* wait for the deletion to complete */
			while (true) {
				try {
					sfsApi.getShare(
							projectId,
							REGION,
							createdResourcePoolId,
							createShareResponse.getShare().getId());

					System.out.println("Waiting for share deletion to complete ...");
					TimeUnit.SECONDS.sleep(5);
				} catch (ApiException e) {
					// If response status is not found or gone, the share is deleted
					if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND
							|| e.getCode() == HttpURLConnection.HTTP_GONE) {
						System.out.println("Deletion of share completed");
						break;
					}
				}
			}

			/* delete the created share export policy */
			sfsApi.deleteShareExportPolicy(
					projectId,
					REGION,
					createShareExportPolicyResponse.getShareExportPolicy().getId());
			System.out.println(
					"\nDeleted share export policy with ID: "
							+ createShareExportPolicyResponse.getShareExportPolicy().getId());

			/* delete the created resource pool */
			System.out.println(
					"\nTrigger deletion of resource pool with ID: " + createdResourcePoolId);
			sfsApi.deleteResourcePool(projectId, REGION, createdResourcePoolId);

			/* wait for the deletion to complete */
			while (true) {
				try {
					sfsApi.getResourcePool(projectId, REGION, createdResourcePoolId);

					System.out.println("Waiting for resource pool deletion to complete ...");
					TimeUnit.SECONDS.sleep(5);
				} catch (ApiException e) {
					// If response status is not found or gone, the resource pool is deleted
					if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND
							|| e.getCode() == HttpURLConnection.HTTP_GONE) {
						System.out.println("Deletion of resource pool completed");
						break;
					}
				}
			}
		} catch (ApiException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
