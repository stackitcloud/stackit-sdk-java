package cloud.stackit.sdk.core.config;

public class EnvironmentVariables {
	public static final String ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH =
			"STACKIT_SERVICE_ACCOUNT_KEY_PATH";
	public static final String ENV_STACKIT_SERVICE_ACCOUNT_KEY = "STACKIT_SERVICE_ACCOUNT_KEY";
	public static final String ENV_STACKIT_PRIVATE_KEY_PATH = "STACKIT_PRIVATE_KEY_PATH";
	public static final String ENV_STACKIT_PRIVATE_KEY = "STACKIT_PRIVATE_KEY";
	public static final String ENV_STACKIT_TOKEN_BASEURL = "STACKIT_TOKEN_BASEURL";
	public static final String ENV_STACKIT_CREDENTIALS_PATH = "STACKIT_CREDENTIALS_PATH";

	public String getStackitServiceAccountKeyPath() {
		return System.getenv(ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);
	}

	public String getStackitServiceAccountKey() {
		return System.getenv(ENV_STACKIT_SERVICE_ACCOUNT_KEY);
	}

	public String getStackitPrivateKeyPath() {
		return System.getenv(ENV_STACKIT_PRIVATE_KEY_PATH);
	}

	public String getStackitPrivateKey() {
		return System.getenv(ENV_STACKIT_PRIVATE_KEY);
	}

	public String getStackitTokenBaseurl() {
		return System.getenv(ENV_STACKIT_TOKEN_BASEURL);
	}

	public String getStackitCredentialsPath() {
		return System.getenv(ENV_STACKIT_CREDENTIALS_PATH);
	}
}
