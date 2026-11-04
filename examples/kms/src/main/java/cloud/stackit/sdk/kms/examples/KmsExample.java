package cloud.stackit.sdk.kms.examples;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.kms.v1api.api.KmsApi;
import cloud.stackit.sdk.kms.v1api.model.Algorithm;
import cloud.stackit.sdk.kms.v1api.model.CreateKeyPayload;
import cloud.stackit.sdk.kms.v1api.model.CreateKeyRingPayload;
import cloud.stackit.sdk.kms.v1api.model.Key;
import cloud.stackit.sdk.kms.v1api.model.KeyList;
import cloud.stackit.sdk.kms.v1api.model.KeyRing;
import cloud.stackit.sdk.kms.v1api.model.KeyRingList;
import cloud.stackit.sdk.kms.v1api.model.Purpose;
import cloud.stackit.sdk.kms.v1api.model.Version;
import cloud.stackit.sdk.kms.v1api.model.VersionList;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * Creates a key ring and symmetric key, lists metadata, then cleans up only the created resources.
 * Key deletion is scheduled by KMS; you'll have to manually delete the key and the keyring
 * afterwards.
 */
@SuppressWarnings("PMD.SystemPrintln")
public final class KmsExample {
	private static final String REGION = "eu01";
	private static final long WAIT_TIMEOUT_NANOS = TimeUnit.MINUTES.toNanos(10);
	private static final long POLL_INTERVAL_SECONDS = 5;

	private KmsExample() {}

	public static void main(String[] args) throws IOException, ApiException, InterruptedException {
		// The id of your STACKIT project, read from an environment variable for this example.
		String projectIdValue = System.getenv("STACKIT_PROJECT_ID");
		if (projectIdValue == null || projectIdValue.isEmpty()) {
			System.err.println("Environment variable 'STACKIT_PROJECT_ID' not found.");
			return;
		}
		UUID projectId = UUID.fromString(projectIdValue);

		// Credentials are read from ~/.stackit/credentials.json or the environment variables
		// STACKIT_SERVICE_ACCOUNT_KEY_PATH / STACKIT_SERVICE_ACCOUNT_KEY.
		CoreConfiguration configuration = new CoreConfiguration();
		OkHttpClient httpClient = new OkHttpClient();
		KeyFlowAuthenticator authenticator = new KeyFlowAuthenticator(httpClient, configuration);
		httpClient =
				httpClient
						.newBuilder()
						.authenticator(authenticator)
						.callTimeout(60, TimeUnit.SECONDS)
						.build();
		KmsApi kmsApi = new KmsApi(httpClient);

		runExample(kmsApi, projectId);
	}

	private static void runExample(KmsApi kmsApi, UUID projectId)
			throws ApiException, InterruptedException {
		UUID keyRingID = null;
		UUID keyID = null;
		System.out.println("creating key ring");
		try {
			KeyRing keyRing =
					kmsApi.createKeyRing(
							projectId,
							REGION,
							new CreateKeyRingPayload()
									.displayName("java-sdk-example-keyring")
									.description("A Java SDK example key ring"));
			keyRingID = Objects.requireNonNull(keyRing.getId());
			waitForKeyRing(kmsApi, projectId, keyRingID);
			System.out.println("key ring created: " + keyRingID);
			System.out.println("creating key");
			Key key =
					kmsApi.createKey(
							projectId,
							REGION,
							keyRingID,
							new CreateKeyPayload()
									.algorithm(Algorithm.AES_256_GCM)
									.protection("software")
									.purpose(Purpose.SYMMETRIC_ENCRYPT_DECRYPT)
									.displayName("java-sdk-example-key")
									.description("A Java SDK example key"));
			keyID = Objects.requireNonNull(key.getId());
			waitForKey(kmsApi, projectId, keyRingID, keyID);
			System.out.println("key created: " + keyID);
			listKeyRings(kmsApi, projectId);
		} finally {
			if (keyID != null) {
				// schedule key for deletion
				System.out.println("scheduling key deletion");
				kmsApi.deleteKey(projectId, REGION, keyRingID, keyID);
				System.out.println(
						"Key scheduled for deletion. ⚠️ The key ring can't be deleted until the key deletion is done. This may take some days. Delete the key ring manually after this period.");
			}
			// key ring can only be deleted if no key exists.
			if (keyRingID != null && keyID == null) {
				System.out.println("deleting key ring");
				kmsApi.deleteKeyRing(projectId, REGION, keyRingID);
				System.out.println("key ring deleted");
			}
		}
	}

	private static void waitForKeyRing(KmsApi kmsApi, UUID projectId, UUID keyRingId)
			throws ApiException, InterruptedException {
		waitUntil(
				"key ring " + keyRingId + " to become active",
				() -> {
					KeyRing.StateEnum state =
							kmsApi.getKeyRing(projectId, REGION, keyRingId).getState();
					if (state == KeyRing.StateEnum.ACTIVE) {
						return true;
					}
					if (state != KeyRing.StateEnum.CREATING) {
						throw new IllegalStateException("Unexpected key ring state: " + state);
					}
					return false;
				});
	}

	private static void waitForKey(KmsApi kmsApi, UUID projectId, UUID keyRingId, UUID keyId)
			throws ApiException, InterruptedException {
		waitUntil(
				"key " + keyId + " to become active",
				() -> {
					Key.StateEnum state =
							kmsApi.getKey(projectId, REGION, keyRingId, keyId).getState();
					if (state == Key.StateEnum.ACTIVE) {
						return true;
					}
					if (state != Key.StateEnum.CREATING) {
						throw new IllegalStateException("Unexpected key state: " + state);
					}
					return false;
				});
	}

	private static void waitUntil(String description, ApiCondition condition)
			throws ApiException, InterruptedException {
		long start = System.nanoTime();
		while (System.nanoTime() - start < WAIT_TIMEOUT_NANOS) {
			if (condition.isSatisfied()) {
				return;
			}
			TimeUnit.SECONDS.sleep(POLL_INTERVAL_SECONDS);
		}
		throw new IllegalStateException("Timed out waiting for " + description);
	}

	@FunctionalInterface
	private interface ApiCondition {
		boolean isSatisfied() throws ApiException;
	}

	private static void listKeyRings(KmsApi kmsApi, UUID projectId) throws ApiException {
		System.out.println("List key rings:");
		KeyRingList keyRings = kmsApi.listKeyRings(projectId, REGION);
		for (KeyRing keyRing : Objects.requireNonNull(keyRings.getKeyRings())) {
			UUID keyRingId = Objects.requireNonNull(keyRing.getId());
			System.out.println("* Key Ring ID: " + keyRingId);
			System.out.println("* Key Ring Name: " + keyRing.getDisplayName());
			System.out.println("* Key Ring State: " + keyRing.getState());
			listKeys(kmsApi, projectId, keyRingId);
		}
	}

	private static void listKeys(KmsApi kmsApi, UUID projectId, UUID keyRingId)
			throws ApiException {
		KeyList keys = kmsApi.listKeys(projectId, REGION, keyRingId);
		for (Key key : Objects.requireNonNull(keys.getKeys())) {
			UUID keyId = Objects.requireNonNull(key.getId());
			System.out.println("  * Key ID: " + keyId);
			System.out.println("  * Key Name: " + key.getDisplayName());
			System.out.println("  * Algorithm: " + key.getAlgorithm());
			System.out.println("  * Purpose: " + key.getPurpose());
			System.out.println("  * Key State: " + key.getState());
			if (key.getState() == Key.StateEnum.DELETED) {
				continue;
			}

			VersionList versions = kmsApi.listVersions(projectId, REGION, keyRingId, keyId);
			for (Version version : Objects.requireNonNull(versions.getVersions())) {
				System.out.println("    * Version Number: " + version.getNumber());
				System.out.println("    * Version State: " + version.getState());
				System.out.println("    * Disabled: " + version.getDisabled());
			}
		}
	}
}
