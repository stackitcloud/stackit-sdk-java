package cloud.stackit.sdk.core.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.config.EnvironmentVariables;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.exception.PrivateKeyNotFoundException;
import cloud.stackit.sdk.core.model.ServiceAccountCredentials;
import cloud.stackit.sdk.core.model.ServiceAccountKey;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.spec.InvalidKeySpecException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.filechooser.FileSystemView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SetupAuthTest {
	@Mock private EnvironmentVariables envs;
	private final String invalidCredentialsFilePath =
			FileSystemView.getFileSystemView().getHomeDirectory()
					+ File.separator
					+ "invalid"
					+ File.separator
					+ "credentials"
					+ File.separator
					+ "file.json";

	ServiceAccountKey createDummyServiceAccount(String privateKey) {
		ServiceAccountCredentials credentials =
				new ServiceAccountCredentials("aud", "iss", "kid", privateKey, "sub");
		return new ServiceAccountKey(
				"id",
				"publicKey",
				Date.from( // Workaround that ServiceAccountKey can be compared in tests
						new Date().toInstant().truncatedTo(ChronoUnit.SECONDS)),
				"keyType",
				"keyOrigin",
				"keyAlgo",
				true,
				Date.from( // Workaround that ServiceAccountKey can be compared in tests
						new Date().toInstant().truncatedTo(ChronoUnit.SECONDS)),
				credentials);
	}

	Path createJsonFile(Map<String, String> content) throws IOException {
		String contentJson = new Gson().toJson(content);
		Path file = Files.createTempFile("credentials", ".json");
		file.toFile().deleteOnExit();

		Files.write(file, contentJson.getBytes(StandardCharsets.UTF_8));
		return file;
	}

	@Test
	void setupKeyFlow_readServiceAccountFromPath()
			throws IOException, InvalidKeySpecException, ApiException {
		// Create service account key file
		ServiceAccountKey initSaKey = createDummyServiceAccount("privateKey");
		String initSaKeyJson = new Gson().toJson(initSaKey);
		Path saKeyPath = Files.createTempFile("serviceAccountKey", ".json");
		saKeyPath.toFile().deleteOnExit();
		Files.write(saKeyPath, initSaKeyJson.getBytes(StandardCharsets.UTF_8));

		// Create config and read setup auth with the previous created saKey
		CoreConfiguration cfg =
				new CoreConfiguration()
						.serviceAccountKeyPath(saKeyPath.toAbsolutePath().toString());
		ServiceAccountKey parsedSaKey = SetupAuth.setupKeyFlow(cfg);

		assertEquals(initSaKey, parsedSaKey);
	}

	@Test
	void setupKeyFlow_readServiceAccountFromConfig()
			throws IOException, InvalidKeySpecException, ApiException {
		// Create service account key
		ServiceAccountKey initSaKey = createDummyServiceAccount("privateKey");
		String initSaKeyJson = new Gson().toJson(initSaKey);

		// Create config and read setup auth with the previous created saKey
		CoreConfiguration cfg = new CoreConfiguration().serviceAccountKey(initSaKeyJson);
		ServiceAccountKey parsedSaKey = SetupAuth.setupKeyFlow(cfg);

		assertEquals(initSaKey, parsedSaKey);
	}

	@Test
	void setupKeyFlow_readServiceAccountFromKeyEnv() throws IOException {
		// Create service account key
		ServiceAccountKey initSaKey = createDummyServiceAccount("privateKey");
		String initSaKeyJson = new Gson().toJson(initSaKey);

		// Mock env STACKIT_SERVICE_ACCOUNT_KEY
		when(envs.getStackitServiceAccountKey()).thenReturn(initSaKeyJson);

		// Create config and read setup auth with the previous created saKey
		CoreConfiguration cfg = new CoreConfiguration();
		ServiceAccountKey parsedSaKey = SetupAuth.setupKeyFlow(cfg, envs);

		assertEquals(initSaKey, parsedSaKey);
	}

	@Test
	void setupKeyFlow_readServiceAccountFromKeyPathEnv() throws IOException {
		// Create service account key
		ServiceAccountKey initSaKey = createDummyServiceAccount("privateKey");
		String keyPathContent = new Gson().toJson(initSaKey);

		// Create dummy keyPathFile
		Path keyPathFile = Files.createTempFile("serviceAccountKey", ".json");
		keyPathFile.toFile().deleteOnExit();
		Files.write(keyPathFile, keyPathContent.getBytes(StandardCharsets.UTF_8));

		// Mock env STACKIT_SERVICE_ACCOUNT_KEY_PATH
		when(envs.getStackitServiceAccountKeyPath())
				.thenReturn(keyPathFile.toAbsolutePath().toString());

		// Create config and read setup auth with the previous created saKey
		CoreConfiguration cfg = new CoreConfiguration();
		ServiceAccountKey parsedSaKey = SetupAuth.setupKeyFlow(cfg, envs);

		assertEquals(initSaKey, parsedSaKey);
	}

	@Test
	void setupKeyFlow_readServiceAccountFromPathWithoutPrivateKey_throwsException()
			throws IOException, InvalidKeySpecException, ApiException {
		// Create service account key file
		ServiceAccountKey initSaKey = createDummyServiceAccount(null);
		String initSaKeyJson = new Gson().toJson(initSaKey);
		Path saKeyPath = Files.createTempFile("serviceAccountKey", ".json");
		saKeyPath.toFile().deleteOnExit();
		Files.write(saKeyPath, initSaKeyJson.getBytes(StandardCharsets.UTF_8));

		// Create config and read setup auth with the previous created saKey
		CoreConfiguration cfg =
				new CoreConfiguration()
						.serviceAccountKeyPath(saKeyPath.toAbsolutePath().toString())
						.credentialsFilePath( // make sure that the defaultCredentialsFile is not
								// used
								invalidCredentialsFilePath);

		assertThrows(PrivateKeyNotFoundException.class, () -> SetupAuth.setupKeyFlow(cfg));
	}

	@Test
	void setupKeyFlow_readServiceAccountFromConfigWithoutPrivateKey_throwsException()
			throws IOException, InvalidKeySpecException, ApiException {
		// Create service account key
		ServiceAccountKey initSaKey = createDummyServiceAccount(null);
		String initSaKeyJson = new Gson().toJson(initSaKey);

		// Create config and read setup auth with the previous created saKey
		CoreConfiguration cfg =
				new CoreConfiguration()
						.serviceAccountKey(initSaKeyJson)
						.credentialsFilePath( // make sure that the defaultCredentialsFile is not
								// used
								invalidCredentialsFilePath);

		assertThrows(PrivateKeyNotFoundException.class, () -> SetupAuth.setupKeyFlow(cfg));
	}

	@Test
	void loadPrivateKey_setPrivateKeyFromConfig()
			throws IOException, InvalidKeySpecException, ApiException {
		final String prvKey = "prvKey";
		ServiceAccountKey saKey = createDummyServiceAccount(null);

		CoreConfiguration cfg = new CoreConfiguration().privateKey(prvKey);

		assertNull(saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, new EnvironmentVariables(), saKey));
		assertEquals(prvKey, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_doesNotOverwriteExistingPrivateKey()
			throws IOException, InvalidKeySpecException, ApiException {
		final String initialPrivateKey = "prvKey";
		final String cfgPrivateKey = "prvKey-updated";

		// Create Service Account
		ServiceAccountKey saKey = createDummyServiceAccount(initialPrivateKey);
		CoreConfiguration cfg = new CoreConfiguration().privateKey(cfgPrivateKey);

		assertEquals(initialPrivateKey, saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, new EnvironmentVariables(), saKey));
		assertEquals(initialPrivateKey, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_setPrivateKeyPath()
			throws IOException, InvalidKeySpecException, ApiException {
		Path tempPrvKeyFile = Files.createTempFile("privateKey", ".pem");
		tempPrvKeyFile.toFile().deleteOnExit();

		final String privateKeyContent = "<my-private-key>";
		Files.write(tempPrvKeyFile, privateKeyContent.getBytes(StandardCharsets.UTF_8));

		// Create Service Account
		ServiceAccountKey saKey = createDummyServiceAccount(null);
		CoreConfiguration cfg =
				new CoreConfiguration().privateKeyPath(tempPrvKeyFile.toAbsolutePath().toString());

		assertNull(saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, new EnvironmentVariables(), saKey));
		assertEquals(privateKeyContent, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_setPrivateKeyPathViaCredentialsFile()
			throws IOException, InvalidKeySpecException, ApiException {
		// Create privateKeyFile
		Path tempPrvKeyFile = Files.createTempFile("privateKey", ".pem");
		tempPrvKeyFile.toFile().deleteOnExit();

		// Write private key file
		final String privateKeyContent = "<my-private-key>";
		Files.write(tempPrvKeyFile, privateKeyContent.getBytes(StandardCharsets.UTF_8));

		// Create credentialsFile
		Path tempCredentialsFile = Files.createTempFile("credentialsFile", ".json");
		tempCredentialsFile.toFile().deleteOnExit();

		Map<String, String> credFileContent = new HashMap<>();
		credFileContent.put(
				EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY_PATH,
				tempPrvKeyFile.toAbsolutePath().toString());
		String credFileContentJson = new Gson().toJson(credFileContent);

		// Write credentials file
		Files.write(tempCredentialsFile, credFileContentJson.getBytes(StandardCharsets.UTF_8));

		// Create ServiceAccount
		ServiceAccountKey saKey = createDummyServiceAccount(null);
		CoreConfiguration cfg =
				new CoreConfiguration()
						.credentialsFilePath(tempCredentialsFile.toAbsolutePath().toString());

		assertNull(saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, new EnvironmentVariables(), saKey));
		assertEquals(privateKeyContent, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_setPrivateKeyViaCredentialsFile()
			throws IOException, InvalidKeySpecException, ApiException {
		final String privateKeyContent = "<my-private-key>";

		// Create credentialsFile
		Path tempCredentialsFile = Files.createTempFile("credentialsFile", ".json");
		tempCredentialsFile.toFile().deleteOnExit();

		// Create dummy credentialsFile
		Map<String, String> credFileContent = new HashMap<>();
		credFileContent.put(EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY, privateKeyContent);
		String credFileContentJson = new Gson().toJson(credFileContent);

		Files.write(tempCredentialsFile, credFileContentJson.getBytes(StandardCharsets.UTF_8));

		// Create dummy service account and config
		ServiceAccountKey saKey = createDummyServiceAccount(null);

		CoreConfiguration cfg =
				new CoreConfiguration()
						.credentialsFilePath(tempCredentialsFile.toAbsolutePath().toString());

		assertNull(saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, new EnvironmentVariables(), saKey));
		assertEquals(privateKeyContent, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_setPrivateKeyViaEnv() throws IOException {
		final String prvKey = "prvKey";
		ServiceAccountKey saKey = createDummyServiceAccount(null);
		when(envs.getStackitPrivateKey()).thenReturn(prvKey);

		CoreConfiguration cfg = new CoreConfiguration();

		assertNull(saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, envs, saKey));
		assertEquals(prvKey, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_setPrivateKeyPathViaEnv() throws IOException {
		final String prvKey = "prvKey";
		ServiceAccountKey saKey = createDummyServiceAccount(null);
		Path tempPrvKeyFile = Files.createTempFile("privateKey", ".pem");
		tempPrvKeyFile.toFile().deleteOnExit();
		Files.write(tempPrvKeyFile, prvKey.getBytes(StandardCharsets.UTF_8));

		when(envs.getStackitPrivateKeyPath())
				.thenReturn(tempPrvKeyFile.toAbsolutePath().toString());

		CoreConfiguration cfg = new CoreConfiguration();

		assertNull(saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, envs, saKey));
		assertEquals(prvKey, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_setPrivateKeyViaCredentialsFileInEnv()
			throws IOException, InvalidKeySpecException, ApiException {
		final String privateKeyContent = "<my-private-key>";

		// Create credentialsFile
		Path tempCredentialsFile = Files.createTempFile("credentialsFile", ".json");
		tempCredentialsFile.toFile().deleteOnExit();

		// Create dummy credentialsFile
		Map<String, String> credFileContent = new HashMap<>();
		credFileContent.put(EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY, privateKeyContent);
		String credFileContentJson = new Gson().toJson(credFileContent);

		Files.write(tempCredentialsFile, credFileContentJson.getBytes(StandardCharsets.UTF_8));

		// Create dummy service account and config
		ServiceAccountKey saKey = createDummyServiceAccount(null);
		CoreConfiguration cfg = new CoreConfiguration();
		when(envs.getStackitCredentialsPath())
				.thenReturn(tempCredentialsFile.toAbsolutePath().toString());

		assertNull(saKey.getCredentials().getPrivateKey());
		assertDoesNotThrow(() -> SetupAuth.loadPrivateKey(cfg, envs, saKey));
		assertEquals(privateKeyContent, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadPrivateKey_invalidPrivateKeyPath_throwsException()
			throws IOException, InvalidKeySpecException, ApiException {

		String invalidPath =
				FileSystemView.getFileSystemView().getHomeDirectory()
						+ File.separator
						+ "invalid"
						+ File.separator
						+ "privateKey"
						+ File.separator
						+ "path.pem";

		ServiceAccountKey saKey = createDummyServiceAccount(null);

		CoreConfiguration cfg = new CoreConfiguration().privateKeyPath(invalidPath);

		assertNull(saKey.getCredentials().getPrivateKey());
		assertThrows(
				PrivateKeyNotFoundException.class,
				() -> SetupAuth.loadPrivateKey(cfg, new EnvironmentVariables(), saKey));
	}

	@Test
	void readValueFromCredentialsFile_keyAndKeyPathSet_returnsKeyValue()
			throws IOException, InvalidKeySpecException, ApiException {
		String keyContent = "key";
		String keyPathContent = "keyPath";

		// Create dummy keyPathFile
		Path keyPathFile = Files.createTempFile("serviceAccountKey", ".json");
		keyPathFile.toFile().deleteOnExit();
		Files.write(keyPathFile, keyPathContent.getBytes(StandardCharsets.UTF_8));

		// Create dummy credentialsFile
		Map<String, String> credentialsFileContent = new HashMap<>();
		credentialsFileContent.put(
				EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY, keyContent);
		credentialsFileContent.put(
				EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH,
				keyPathFile.toAbsolutePath().toString());
		Path credentialsFile = createJsonFile(credentialsFileContent);

		String result =
				SetupAuth.readValueFromCredentialsFile(
						credentialsFile.toAbsolutePath().toString(),
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY,
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);

		assertEquals(keyContent, result);
	}

	@Test
	void readValueFromCredentialsFile_keySet_returnsKeyValue()
			throws IOException, InvalidKeySpecException, ApiException {
		String keyContent = "key";

		// Create dummy credentialsFile
		Map<String, String> credentialsFileContent = new HashMap<>();
		credentialsFileContent.put(
				EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY, keyContent);
		Path credentialsFile = createJsonFile(credentialsFileContent);

		String result =
				SetupAuth.readValueFromCredentialsFile(
						credentialsFile.toAbsolutePath().toString(),
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY,
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);

		assertEquals(keyContent, result);
	}

	@Test
	void readValueFromCredentialsFile_KeyPathSet_returnsKeyValue()
			throws IOException, InvalidKeySpecException, ApiException {
		// Create dummy keyPathFile
		String keyPathContent = "keyPath";
		Path keyPathFile = Files.createTempFile("serviceAccountKey", ".json");
		keyPathFile.toFile().deleteOnExit();
		Files.write(keyPathFile, keyPathContent.getBytes(StandardCharsets.UTF_8));

		// Create dummy credentialsFile
		Map<String, String> credentialsFileContent = new HashMap<>();
		credentialsFileContent.put(
				EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH,
				keyPathFile.toAbsolutePath().toString());
		Path credentialsFile = createJsonFile(credentialsFileContent);

		String result =
				SetupAuth.readValueFromCredentialsFile(
						credentialsFile.toAbsolutePath().toString(),
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY,
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);

		assertEquals(keyPathContent, result);
	}
}
