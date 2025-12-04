package cloud.stackit.sdk.objectstorage.examples;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.objectstorage.api.ObjectStorageApi;
import cloud.stackit.sdk.objectstorage.model.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import okhttp3.OkHttpClient;

@SuppressWarnings("PMD.CouplingBetweenObjects")
final class ObjectStorageExample {

	private ObjectStorageExample() {}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.CognitiveComplexity",
		"PMD.NPathComplexity",
		"PMD.NcssCount",
		"PMD.SystemPrintln",
		"PMD.AvoidThrowingRawExceptionTypes"
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
						.readTimeout(Duration.ofSeconds(15))
						.build();

		ObjectStorageApi objectStorageApi = new ObjectStorageApi(httpClient);

		// the id of your STACKIT project, read from env var for this example
		String projectId = System.getenv("STACKIT_PROJECT_ID");
		if (projectId == null || projectId.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}

		// the region which should be used to interact with objectstorage, read from env var for
		// this example
		String region = System.getenv("STACKIT_REGION");
		if (region == null || region.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_REGION' not found.");
			return;
		}

		try {
			/*
			 * ///////////////////////////////////////////////////////
			 * //       P R O J E C T   E N A B L E M E N T         //
			 * ///////////////////////////////////////////////////////
			 */
			/* check if object storage is enabled for the project */
			ProjectStatus projectStatus;
			System.out.println("Checking if object storage is enabled");
			try {
				projectStatus = objectStorageApi.getServiceStatus(projectId, region);
				System.out.println("* Object storage is enabled");
			} catch (ApiException e) {
				// If response status is not found, object storage is not enabled for the
				// project
				if (e.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
					System.out.println("* Object storage is not enabled for the project");
					System.out.println("* Enabling object storage");
					projectStatus = objectStorageApi.enableService(projectId, region);
					System.out.println("* Object storage successful enabled for the project");
				} else {
					throw new RuntimeException(e);
				}
			}
			ProjectScope scope = projectStatus.getScope();
			System.out.println("* Scope of the object storage: " + scope);

			/*
			 * ///////////////////////////////////////////////////////
			 * //                  B U C K E T S                    //
			 * ///////////////////////////////////////////////////////
			 * */

			/* create a new bucket in the project */
			System.out.println("\nCreating bucket");
			CreateBucketResponse newBucket =
					objectStorageApi.createBucket(projectId, region, "java-sdk-example");
			System.out.println(" * Bucket name: " + newBucket.getBucket());
			System.out.println(" * Project ID: " + newBucket.getProject());

			/* list all available buckets in the project */
			System.out.println("\nListing all buckets:");
			ListBucketsResponse fetchedBuckets = objectStorageApi.listBuckets(projectId, region);
			List<Bucket> listBuckets = fetchedBuckets.getBuckets();
			for (Bucket bucket : listBuckets) {
				System.out.println("*******************");
				System.out.println("* Bucket name: " + bucket.getName());
				System.out.println("* Virtual host: " + bucket.getUrlPathStyle());
			}

			/* deleting the bucket we just created */
			System.out.println("\nDeleting bucket:");
			DeleteBucketResponse deleteBucketResponse =
					objectStorageApi.deleteBucket(projectId, region, newBucket.getBucket());
			System.out.println("* Bucket name: " + deleteBucketResponse.getBucket());
			System.out.println("* Bucket successfully deleted");

			/*
			 * ///////////////////////////////////////////////////////
			 * //        C R E D E N T I A L   G R O U P            //
			 * ///////////////////////////////////////////////////////
			 * */

			/* creating a new credential group in the project */
			System.out.println("\nCreating credential group:");
			CreateCredentialsGroupResponse newCredentialGroup =
					objectStorageApi.createCredentialsGroup(
							projectId,
							region,
							new CreateCredentialsGroupPayload().displayName("java-sdk-example"));
			System.out.println(
					"* Group ID"
							+ newCredentialGroup.getCredentialsGroup().getCredentialsGroupId());
			System.out.println(
					"* Display name" + newCredentialGroup.getCredentialsGroup().getDisplayName());
			System.out.println("* URN" + newCredentialGroup.getCredentialsGroup().getUrn());

			/* list all available credentials groups */
			System.out.println("\nListing all credentials groups:");
			ListCredentialsGroupsResponse fetchedCredentialGroups =
					objectStorageApi.listCredentialsGroups(projectId, region);
			List<CredentialsGroup> listCredentialsGroups =
					fetchedCredentialGroups.getCredentialsGroups();
			for (CredentialsGroup credentialsGroup : listCredentialsGroups) {
				System.out.println("*******************");
				System.out.println("* Group ID: " + credentialsGroup.getCredentialsGroupId());
				System.out.println("* Display name: " + credentialsGroup.getDisplayName());
				System.out.println("* URN: " + credentialsGroup.getUrn());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //               A C C E S S   K E Y                 //
			 * ///////////////////////////////////////////////////////
			 * */

			/* creating a new access key in the credentials group we just created */
			System.out.println("\nCreating access key:");
			CreateAccessKeyResponse newAccessKey =
					objectStorageApi.createAccessKey(
							projectId,
							region,
							new CreateAccessKeyPayload()
									.expires(OffsetDateTime.now().plusMinutes(5)),
							newCredentialGroup.getCredentialsGroup().getCredentialsGroupId());
			System.out.println("* Key ID: " + newAccessKey.getKeyId());
			System.out.println("* Display name: " + newAccessKey.getDisplayName());
			System.out.println("* Expires: " + newAccessKey.getExpires());

			/* list all available access key in the credential group */
			System.out.println("\nListing all access key of the created credentials group:");
			ListAccessKeysResponse fetchedAccessKeys =
					objectStorageApi.listAccessKeys(
							projectId,
							region,
							newCredentialGroup.getCredentialsGroup().getCredentialsGroupId());
			List<AccessKey> listAccessKeys = fetchedAccessKeys.getAccessKeys();
			for (AccessKey accessKey : listAccessKeys) {
				System.out.println("*******************");
				System.out.println("* Key ID: " + accessKey.getKeyId());
				System.out.println("* Display name: " + accessKey.getDisplayName());
				System.out.println("* Expires: " + accessKey.getExpires());
			}

			/*
			 * ///////////////////////////////////////////////////////
			 * //                 D E L E T I O N                   //
			 * ///////////////////////////////////////////////////////
			 * */

			/* deleting the access key we just created */
			System.out.println("\nDeleting access key:");
			DeleteAccessKeyResponse deletedAccessKey =
					objectStorageApi.deleteAccessKey(
							projectId,
							region,
							newAccessKey.getKeyId(),
							newCredentialGroup.getCredentialsGroup().getCredentialsGroupId());
			System.out.println("* Key ID: " + deletedAccessKey.getKeyId());
			System.out.println("* Bucket successfully deleted");

			/* deleting the credentials group we just created */
			System.out.println("\nDeleting credentials group:");
			DeleteCredentialsGroupResponse deleteCredentialsGroupResponse =
					objectStorageApi.deleteCredentialsGroup(
							projectId,
							region,
							newCredentialGroup.getCredentialsGroup().getCredentialsGroupId());
			System.out.println(
					"* Group ID: " + deleteCredentialsGroupResponse.getCredentialsGroupId());
			System.out.println("* Bucket successfully deleted");
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}
}
