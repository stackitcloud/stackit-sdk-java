package cloud.stackit.sdk.core.auth;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.config.EnvironmentVariables;
import cloud.stackit.sdk.core.KeyFlowInterceptor;
import cloud.stackit.sdk.core.exception.CredentialsInFileNotFoundException;
import cloud.stackit.sdk.core.exception.PrivateKeyNotFoundException;
import cloud.stackit.sdk.core.model.ServiceAccountKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Interceptor;

import javax.security.auth.login.CredentialNotFoundException;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class SetupAuth {
    private final Interceptor authHandler;
    private final String defaultCredentialsFilePath =
            FileSystemView.getFileSystemView().getHomeDirectory()
                    + File.separator
                    + ".stackit"
                    + File.separator
                    + "credentials.json";

    /**
     * Set up the KeyFlow Authentication and can be integrated in an OkHttp client, by adding `SetupAuth().getAuthHandler()` as interceptor.
     * This relies on the configuration methods via ENVs or the credentials file in `$HOME/.stackit/credentials.json`
     * @throws IOException when a file can be found
     * @throws CredentialNotFoundException when no configuration is set or can be found
     * @throws InvalidKeySpecException when the private key can not be parsed
     * @throws ApiException when access token creation failed
     */
    public SetupAuth() throws IOException, InvalidKeySpecException, CredentialNotFoundException, ApiException {
        this(new CoreConfiguration.Builder().build());
    }

    /**
     * Set up the KeyFlow Authentication and can be integrated in an OkHttp client, by adding `SetupAuth().getAuthHandler()` as interceptor.
     * @param cfg Configuration which describes, which service account and token endpoint should be used
     * @throws IOException when a file can be found
     * @throws CredentialsInFileNotFoundException when no credentials are set or can be found
     * @throws InvalidKeySpecException when the private key can not be parsed
     * @throws ApiException when access token creation failed
     */
    public SetupAuth(CoreConfiguration cfg) throws IOException, CredentialsInFileNotFoundException, InvalidKeySpecException, ApiException {
        if (cfg == null) {
            cfg = new CoreConfiguration.Builder().build();
        }

        ServiceAccountKey saKey = setupKeyFlow(cfg);
        authHandler = new KeyFlowInterceptor(new KeyFlowAuthenticator(cfg, saKey));
    }

    public Interceptor getAuthHandler() {
        return authHandler;
    }

    /**
     * setupKeyFlow return first found ServiceAccountKey
     * Reads the configured options in the following order
     * <ol>
     *     <li>
     *         Explicit configuration in `Configuration`
     *     </li>
     *     <ul>
     *         <li>serviceAccountKey</li>
     *         <li>serviceAccountKeyPath</li>
     *         <li>credentialsFilePath -> STACKIT_SERVICE_ACCOUNT_KEY / STACKIT_SERVICE_ACCOUNT_KEY_PATH</li>
     *     </ul>
     *     <li>
     *         Environment variables
     *     </li>
     *     <ul>
     *         <li>STACKIT_SERVICE_ACCOUNT_KEY</li>
     *         <li>STACKIT_SERVICE_ACCOUNT_KEY_PATH</li>
     *         <li>STACKIT_CREDENTIALS_PATH -> STACKIT_SERVICE_ACCOUNT_KEY / STACKIT_SERVICE_ACCOUNT_KEY_PATH</li>
     *     </ul>
     *     <li>
     *         Credentials file
     *     </li>
     *     <ul>
     *         <li>STACKIT_SERVICE_ACCOUNT_KEY</li>
     *         <li>STACKIT_SERVICE_ACCOUNT_KEY_PATH</li>
     *     </ul>
     * </ol>
     * @param cfg
     * @return ServiceAccountKey
     * @throws CredentialsInFileNotFoundException thrown when no service account key or private key can be found
     * @throws IOException thrown when a file can not be found
     */
    private ServiceAccountKey setupKeyFlow(CoreConfiguration cfg) throws CredentialsInFileNotFoundException, IOException {
        // Explicit config in code
        if (cfg.getServiceAccountKey() != null && !cfg.getServiceAccountKey().trim().isEmpty()) {
            ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(cfg.getServiceAccountKey());
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        if (cfg.getServiceAccountKeyPath() != null && !cfg.getServiceAccountKeyPath().trim().isEmpty()) {
            String fileContent = new String(Files.readAllBytes(Paths.get(cfg.getServiceAccountKeyPath())), StandardCharsets.UTF_8);
            ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(fileContent);
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        // Env config
        if (EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY != null && !EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY.trim().isEmpty()) {
            ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY.trim());
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        if (EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY_PATH != null && !EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY_PATH.trim().isEmpty()) {
            String fileContent = new String(Files.readAllBytes(Paths.get(cfg.getServiceAccountKeyPath())), StandardCharsets.UTF_8);
            ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(fileContent);
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        if (EnvironmentVariables.STACKIT_CREDENTIALS_PATH != null && !EnvironmentVariables.STACKIT_CREDENTIALS_PATH.trim().isEmpty()) {
            String saKeyJson = readValueFromCredentialsFile(EnvironmentVariables.STACKIT_CREDENTIALS_PATH, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);
            ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(saKeyJson);
            loadPrivateKey(cfg, saKey);
            return saKey;
        } else {
            String saKeyJson = readValueFromCredentialsFile(defaultCredentialsFilePath, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);
            ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(saKeyJson);
            loadPrivateKey(cfg, saKey);
            return saKey;
        }
    }

    private void loadPrivateKey(CoreConfiguration cfg, ServiceAccountKey saKey) throws PrivateKeyNotFoundException {
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
     * <ol>
     *     <li>
     *         Explicit configuration in `Configuration`
     *     </li>
     *     <ul>
     *         <li>privateKey</li>
     *         <li>privateKeyPath</li>
     *         <li>credentialsFilePath -> STACKIT_PRIVATE_KEY / STACKIT_PRIVATE_KEY_PATH</li>
     *     </ul>
     *     <li>
     *         Environment variables
     *     </li>
     *     <ul>
     *         <li>STACKIT_PRIVATE_KEY</li>
     *         <li>STACKIT_PRIVATE_KEY_PATH</li>
     *         <li>STACKIT_CREDENTIALS_PATH -> STACKIT_PRIVATE_KEY / STACKIT_PRIVATE_KEY_PATH</li>
     *     </ul>
     *     <li>
     *         Credentials file
     *     </li>
     *     <ul>
     *         <li>STACKIT_PRIVATE_KEY</li>
     *         <li>STACKIT_PRIVATE_KEY_PATH</li>
     *     </ul>
     * </ol>
     * @param cfg
     * @return found private key
     * @throws CredentialsInFileNotFoundException throws if no private key could be found
     * @throws IOException throws if the provided path can not be found or the file within the pathKey can not be found
     */
    private String getPrivateKey(CoreConfiguration cfg) throws CredentialsInFileNotFoundException, IOException {
        // Explicit code config
        // Set private key
        if (cfg.getPrivateKey() != null && !cfg.getPrivateKey().trim().isEmpty()) {
            return cfg.getPrivateKey();
        }
        // Set private key path
        if (cfg.getPrivateKeyPath() != null && !cfg.getPrivateKeyPath().trim().isEmpty()) {
            String privateKeyPath = cfg.getPrivateKeyPath();
            return new String(Files.readAllBytes(Paths.get(privateKeyPath)), StandardCharsets.UTF_8);
        }
        // Set credentials file
        if (cfg.getCredentialsFilePath() != null && !cfg.getCredentialsFilePath().trim().isEmpty()) {
            return readValueFromCredentialsFile(cfg.getCredentialsFilePath(), EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY, EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY_PATH);
        }

        // ENVs config
        if (EnvironmentVariables.STACKIT_PRIVATE_KEY != null && !EnvironmentVariables.STACKIT_PRIVATE_KEY.trim().isEmpty()) {
            return EnvironmentVariables.STACKIT_PRIVATE_KEY.trim();
        }
        if (EnvironmentVariables.STACKIT_PRIVATE_KEY_PATH != null && !EnvironmentVariables.STACKIT_PRIVATE_KEY_PATH.trim().isEmpty()) {
            return new String(Files.readAllBytes(Paths.get(EnvironmentVariables.STACKIT_PRIVATE_KEY_PATH)), StandardCharsets.UTF_8);
        }
        if (EnvironmentVariables.STACKIT_CREDENTIALS_PATH != null && !EnvironmentVariables.STACKIT_CREDENTIALS_PATH.trim().isEmpty()) {
            return readValueFromCredentialsFile(EnvironmentVariables.STACKIT_CREDENTIALS_PATH, EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY, EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY_PATH);
        }

        // Read from credentials file in defaultCredentialsFilePath
        return readValueFromCredentialsFile(defaultCredentialsFilePath, EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY, EnvironmentVariables.ENV_STACKIT_PRIVATE_KEY_PATH);
    }

    /**
     * Reads of a json credentials file from `path`, the values of `valueKey` or `pathKey`.
     * @param path Path of the credentials file which should be read
     * @param valueKey key which contains the secret as value
     * @param pathKey key which contains a path to a file
     * @return Either the value of `valueKey` or the content of the file in `pathKey`
     * @throws CredentialsInFileNotFoundException throws if no value was found in the credentials file
     * @throws IOException throws if the provided path can not be found or the file within the pathKey can not be found
     */
    private String readValueFromCredentialsFile(String path, String valueKey, String pathKey) throws IOException, CredentialsInFileNotFoundException {
        // Read credentials file
        String fileContent = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        Type credentialsFileType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = new Gson().fromJson(fileContent, credentialsFileType);

        // Read KEY from credentials file
        String key = null;
        try {
            key = (String) map.get(valueKey);
        } catch (ClassCastException ignored) {}
        if (key != null && !key.trim().isEmpty()) {
            return key;
        }

        // Read KEY_PATH from credentials file
        String keyPath = null;
        try {
            keyPath = (String) map.get(pathKey);
        } catch (ClassCastException ignored) {}
        if (keyPath != null && !keyPath.trim().isEmpty()) {
            return new String(Files.readAllBytes(Paths.get(keyPath)));
        }
        throw new CredentialsInFileNotFoundException("could not find " + valueKey + " or " + pathKey + " in " + path);
    }
}
