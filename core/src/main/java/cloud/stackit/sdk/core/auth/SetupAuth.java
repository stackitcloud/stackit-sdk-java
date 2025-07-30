package cloud.stackit.sdk.core.auth;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import cloud.stackit.sdk.core.config.Configuration;
import cloud.stackit.sdk.core.config.EnvironmentVariables;
import cloud.stackit.sdk.core.KeyFlowInterceptor;
import cloud.stackit.sdk.core.model.ServiceAccountKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Interceptor;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class SetupAuth {
    private Interceptor authHandler;
    private final String defaultCredentialsFilePath = "~/.stackit/credentials.json";

    public SetupAuth() {
        this(new Configuration.Builder().build());
    }

    public SetupAuth(Configuration cfg) {
        if (cfg == null) {
            cfg = new Configuration.Builder().build();
        }

        try {
            ServiceAccountKey saKey = setupKeyFlow(cfg);
            authHandler = new KeyFlowInterceptor(new KeyFlowAuthenticator(saKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Interceptor getAuthHandler() {
        return authHandler;
    }

    private ServiceAccountKey setupKeyFlow(Configuration cfg) throws Exception {
        ServiceAccountKey saKey = null;
        // Explicit config in code
        if (cfg.getServiceAccountKey() != null && !cfg.getServiceAccountKey().trim().isEmpty()) {
            saKey = ServiceAccountKey.loadCredentials(cfg.getServiceAccountKey());
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        if (cfg.getServiceAccountKeyPath() != null && !cfg.getServiceAccountKeyPath().trim().isEmpty()) {
            String fileContent = new String(Files.readAllBytes(Paths.get(cfg.getServiceAccountKeyPath())), StandardCharsets.UTF_8);
            saKey = new Gson().fromJson(fileContent, ServiceAccountKey.class);
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        // Env config
        if (!EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY.trim().isEmpty()) {
            saKey = ServiceAccountKey.loadCredentials(EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY.trim());
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        if (!EnvironmentVariables.STACKIT_SERVICE_ACCOUNT_KEY_PATH.trim().isEmpty()) {
            String fileContent = new String(Files.readAllBytes(Paths.get(cfg.getServiceAccountKeyPath())), StandardCharsets.UTF_8);
            saKey = new Gson().fromJson(fileContent, ServiceAccountKey.class);
            loadPrivateKey(cfg, saKey);
            return saKey;
        }

        if (!EnvironmentVariables.STACKIT_CREDENTIALS_PATH.trim().isEmpty()) {
            String saKeyJson = readValueFromCredentialsFile(EnvironmentVariables.STACKIT_CREDENTIALS_PATH, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);
            saKey = new Gson().fromJson(saKeyJson, ServiceAccountKey.class);
            loadPrivateKey(cfg, saKey);
            return saKey;
        } else {
            try {
                String saKeyJson = readValueFromCredentialsFile(defaultCredentialsFilePath, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY, EnvironmentVariables.ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);
                saKey = new Gson().fromJson(saKeyJson, ServiceAccountKey.class);
                loadPrivateKey(cfg, saKey);
                return saKey;
            } catch (Exception e) {
                throw new Exception("could not find service account key");
            }
        }
    }

    private void loadPrivateKey(Configuration cfg, ServiceAccountKey saKey) throws Exception {
        if (!saKey.getCredentials().isPrivateKeySet()) {
            try {
             String privateKey = getPrivateKey(cfg);
             saKey.getCredentials().setPrivateKey(privateKey);
            } catch (Exception e) {
                throw new Exception("could not find private key", e);
            }
        }
    }

    private String getPrivateKey(Configuration cfg) throws Exception {
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

    private String readValueFromCredentialsFile(String path, String valueKey, String pathKey) throws Exception {
        // Read credentials file
        String fileContent = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        Type credentialsFileType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = new Gson().fromJson(fileContent, credentialsFileType);

        // Read STACKIT_PRIVATE_KEY from credentials file
        String privateKey = map.get(valueKey);
        if (privateKey != null && !privateKey.trim().isEmpty()) {
            return privateKey;
        }

        // Read STACKIT_PRIVATE_KEY_PATH from credentials file
        String privateKeyPath = map.get(pathKey);
        if (privateKeyPath != null && !privateKeyPath.trim().isEmpty()) {
            return new String(Files.readAllBytes(Paths.get(privateKeyPath)));
        }
        throw new Exception("could not find private key");
    }
}
