package cloud.stackit.sdk.core.auth;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.KeyFlowInterceptor;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.config.EnvironmentVariables;
import cloud.stackit.sdk.core.exception.CredentialsInFileNotFoundException;
import cloud.stackit.sdk.core.exception.PrivateKeyNotFoundException;
import cloud.stackit.sdk.core.model.ServiceAccountKey;
import cloud.stackit.sdk.core.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import javax.swing.filechooser.FileSystemView;
import okhttp3.Interceptor;

public class SetupAuth {
	private final EnvironmentVariables env;
	private Interceptor authHandler;
	private final CoreConfiguration cfg;
	private final String defaultCredentialsFilePath =
			FileSystemView.getFileSystemView().getHomeDirectory()
					+ File.separator
					+ ".stackit"
					+ File.separator
					+ "credentials.json";

	/**
	 * Set up the KeyFlow Authentication and can be integrated in an OkHttp client, by adding
	 * `SetupAuth().getAuthHandler()` as interceptor. This relies on the configuration methods via
	 * ENVs or the credentials file in `$HOME/.stackit/credentials.json`
	 *
	 * @throws CredentialsInFileNotFoundException when no configuration is set or can be found
	 */
	public SetupAuth() throws CredentialsInFileNotFoundException {
		this(new CoreConfiguration.Builder().build(), new EnvironmentVariables());
	}

	/**
	 * Set up the KeyFlow Authentication and can be integrated in an OkHttp client, by adding
	 * `SetupAuth().getAuthHandler()` as interceptor.
	 *
	 * @param cfg Configuration which describes, which service account and token endpoint should be
	 *     used
	 * @throws IOException when a file can be found
	 * @throws CredentialsInFileNotFoundException when no credentials are set or can be found
	 */
	public SetupAuth(CoreConfiguration cfg) throws IOException, CredentialsInFileNotFoundException {
		this(cfg, new EnvironmentVariables());
	}

	/**
	 * Set up the KeyFlow Authentication and can be integrated in an OkHttp client, by adding
	 * `SetupAuth().getAuthHandler()` as interceptor.
	 *
	 * @param cfg Configuration which describes, which service account and token endpoint should be
	 *     used
	 * @throws CredentialsInFileNotFoundException when no credentials are set or can be found
	 */
	protected SetupAuth(CoreConfiguration cfg, EnvironmentVariables environmentVariables)
			throws CredentialsInFileNotFoundException {

		this.cfg = cfg != null ? cfg : new CoreConfiguration.Builder().build();
		this.env = environmentVariables != null ? environmentVariables : new EnvironmentVariables();
	}

	public void init() throws IOException {
		ServiceAccountKey saKey = setupKeyFlow(cfg);
		authHandler = new KeyFlowInterceptor(new KeyFlowAuthenticator(cfg, saKey));
	}

	public Interceptor getAuthHandler() {
		if (authHandler == null) {
			throw new RuntimeException("init() has to be called first");
		}
		return authHandler;
	}

	/**
	 * setupKeyFlow return first found ServiceAccountKey Reads the configured options in the
	 * following order
	 *
	 * <ol>
	 *   <li>Explicit configuration in `Configuration`
	 *       <ul>
	 *         <li>serviceAccountKey
	 *         <li>serviceAccountKeyPath
	 *         <li>credentialsFilePath -> STACKIT_SERVICE_ACCOUNT_KEY /
	 *             STACKIT_SERVICE_ACCOUNT_KEY_PATH
	 *       </ul>
	 *   <li>Environment variables
	 *       <ul>
	 *         <li>STACKIT_SERVICE_ACCOUNT_KEY
	 *         <li>STACKIT_SERVICE_ACCOUNT_KEY_PATH
	 *         <li>STACKIT_CREDENTIALS_PATH -> STACKIT_SERVICE_ACCOUNT_KEY /
	 *             STACKIT_SERVICE_ACCOUNT_KEY_PATH
	 *       </ul>
	 *   <li>Credentials file
	 *       <ul>
	 *         <li>STACKIT_SERVICE_ACCOUNT_KEY
	 *         <li>STACKIT_SERVICE_ACCOUNT_KEY_PATH
	 *       </ul>
	 * </ol>
	 *
	 * @param cfg
	 * @return ServiceAccountKey
	 * @throws CredentialsInFileNotFoundException thrown when no service account key or private key
	 *     can be found
	 * @throws IOException thrown when a file can not be found
	 */
	protected ServiceAccountKey setupKeyFlow(CoreConfiguration cfg)
			throws CredentialsInFileNotFoundException, IOException {
		// Explicit config in code
		if (Utils.isStringSet(cfg.getServiceAccountKey())) {
			ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(cfg.getServiceAccountKey());
			loadPrivateKey(cfg, saKey);
			return saKey;
		}

		if (Utils.isStringSet(cfg.getServiceAccountKeyPath())) {
			String fileContent =
					new String(
							Files.readAllBytes(Paths.get(cfg.getServiceAccountKeyPath())),
							StandardCharsets.UTF_8);
			ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(fileContent);
			loadPrivateKey(cfg, saKey);
			return saKey;
		}

		// Env config
		if (Utils.isStringSet(env.getStackitServiceAccountKey())) {
			ServiceAccountKey saKey =
					ServiceAccountKey.loadFromJson(env.getStackitServiceAccountKey().trim());
			loadPrivateKey(cfg, saKey);
			return saKey;
		}

		if (Utils.isStringSet(env.getStackitServiceAccountKeyPath())) {
			String fileContent =
					new String(
							Files.readAllBytes(Paths.get(env.getStackitServiceAccountKeyPath())),
							StandardCharsets.UTF_8);
			ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(fileContent);
			loadPrivateKey(cfg, saKey);
			return saKey;
		}

		// Read from credentialsFile
		String credentialsFilePath =
				Utils.isStringSet(env.getStackitCredentialsPath())
						? env.getStackitCredentialsPath()
						: defaultCredentialsFilePath;

		String saKeyJson =
				readValueFromCredentialsFile(
						credentialsFilePath,
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY,
						EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);

		ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(saKeyJson);
		loadPrivateKey(cfg, saKey);
		return saKey;
	}

	protected void loadPrivateKey(CoreConfiguration cfg, ServiceAccountKey saKey)
			throws PrivateKeyNotFoundException {
		if (!saKey.getCredentials().isPrivateKeySet()) {
			try {
				String privateKey = getPrivateKey(cfg);
				saKey.getCredentials().setPrivateKey(privateKey);
			} catch (Exception e) {
				throw new PrivateKeyNotFoundException("could not find private key", e);
			}
		}
	}

	/**
	 * Reads the private key in the following order
	 *
	 * <ol>
	 *   <li>Explicit configuration in `Configuration`
	 *       <ul>
	 *         <li>privateKey
	 *         <li>privateKeyPath
	 *         <li>credentialsFilePath -> STACKIT_PRIVATE_KEY / STACKIT_PRIVATE_KEY_PATH
	 *       </ul>
	 *   <li>Environment variables
	 *       <ul>
	 *         <li>STACKIT_PRIVATE_KEY
	 *         <li>STACKIT_PRIVATE_KEY_PATH
	 *         <li>STACKIT_CREDENTIALS_PATH -> STACKIT_PRIVATE_KEY / STACKIT_PRIVATE_KEY_PATH
	 *       </ul>
	 *   <li>Credentials file
	 *       <ul>
	 *         <li>STACKIT_PRIVATE_KEY
	 *         <li>STACKIT_PRIVATE_KEY_PATH
	 *       </ul>
	 * </ol>
	 *
	 * @param cfg
	 * @return found private key
	 * @throws CredentialsInFileNotFoundException throws if no private key could be found
	 * @throws IOException throws if the provided path can not be found or the file within the
	 *     pathKey can not be found
	 */
	private String getPrivateKey(CoreConfiguration cfg)
			throws CredentialsInFileNotFoundException, IOException {
		// Explicit code config
		// Set private key
		if (Utils.isStringSet(cfg.getPrivateKey())) {
			return cfg.getPrivateKey();
		}
		// Set private key path
		if (Utils.isStringSet(cfg.getPrivateKeyPath())) {
			String privateKeyPath = cfg.getPrivateKeyPath();
			return new String(
					Files.readAllBytes(Paths.get(privateKeyPath)), StandardCharsets.UTF_8);
		}
		// Set credentials file
		if (Utils.isStringSet(cfg.getCredentialsFilePath())) {
			return readValueFromCredentialsFile(
					cfg.getCredentialsFilePath(),
					EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY,
					EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY_PATH);
		}

		// ENVs config
		if (Utils.isStringSet(env.getStackitPrivateKey())) {
			return env.getStackitPrivateKey().trim();
		}
		if (Utils.isStringSet(env.getStackitPrivateKeyPath())) {
			return new String(
					Files.readAllBytes(Paths.get(env.getStackitPrivateKeyPath())),
					StandardCharsets.UTF_8);
		}

		String credentialsFilePath =
				Utils.isStringSet(env.getStackitCredentialsPath())
						? env.getStackitCredentialsPath()
						: defaultCredentialsFilePath;

		return readValueFromCredentialsFile(
				credentialsFilePath,
				EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY,
				EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY_PATH);
	}

	/**
	 * Reads of a json credentials file from `path`, the values of `valueKey` or `pathKey`.
	 *
	 * @param path Path of the credentials file which should be read
	 * @param valueKey key which contains the secret as value
	 * @param pathKey key which contains a path to a file
	 * @return Either the value of `valueKey` or the content of the file in `pathKey`
	 * @throws CredentialsInFileNotFoundException throws if no value was found in the credentials
	 *     file
	 * @throws IOException throws if the provided path can not be found or the file within the
	 *     pathKey can not be found
	 */
	protected String readValueFromCredentialsFile(String path, String valueKey, String pathKey)
			throws IOException, CredentialsInFileNotFoundException {
		// Read credentials file
		String fileContent =
				new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		Type credentialsFileType = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, Object> map = new Gson().fromJson(fileContent, credentialsFileType);

		// Read KEY from credentials file
		String key = null;
		try {
			key = (String) map.get(valueKey);
		} catch (ClassCastException ignored) {
		}
		if (Utils.isStringSet(key)) {
			return key;
		}

		// Read KEY_PATH from credentials file
		String keyPath = null;
		try {
			keyPath = (String) map.get(pathKey);
		} catch (ClassCastException ignored) {
		}
		if (Utils.isStringSet(keyPath)) {
			return new String(Files.readAllBytes(Paths.get(keyPath)));
		}
		throw new CredentialsInFileNotFoundException(
				"could not find " + valueKey + " or " + pathKey + " in " + path);
	}
}
