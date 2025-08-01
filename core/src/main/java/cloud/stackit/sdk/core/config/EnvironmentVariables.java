package cloud.stackit.sdk.core.config;

public class EnvironmentVariables {
	public static final String ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH =
			"STACKIT_SERVICE_ACCOUNT_KEY_PATH";
	public static final String ENV_STACKIT_SERVICE_ACCOUNT_KEY = "STACKIT_SERVICE_ACCOUNT_KEY";
	public static final String ENV_STACKIT_PRIVATE_KEY_PATH = "STACKIT_PRIVATE_KEY_PATH";
	public static final String ENV_STACKIT_PRIVATE_KEY = "STACKIT_PRIVATE_KEY";
	public static final String ENV_STACKIT_TOKEN_BASEURL = "STACKIT_TOKEN_BASEURL";
	public static final String ENV_STACKIT_CREDENTIALS_PATH = "STACKIT_CREDENTIALS_PATH";

	public static final String STACKIT_SERVICE_ACCOUNT_KEY_PATH =
			System.getenv(ENV_STACKIT_SERVICE_ACCOUNT_KEY_PATH);
	public static final String STACKIT_SERVICE_ACCOUNT_KEY =
			System.getenv(ENV_STACKIT_SERVICE_ACCOUNT_KEY);
	public static final String STACKIT_PRIVATE_KEY_PATH =
			System.getenv(ENV_STACKIT_PRIVATE_KEY_PATH);
	public static final String STACKIT_PRIVATE_KEY = System.getenv(ENV_STACKIT_PRIVATE_KEY);
	public static final String STACKIT_TOKEN_BASEURL = System.getenv(ENV_STACKIT_TOKEN_BASEURL);
	public static final String STACKIT_CREDENTIALS_PATH =
			System.getenv(ENV_STACKIT_CREDENTIALS_PATH);

	@Override
	public String toString() {
		return "EnvironmentVariables{"
				+ "STACKIT_SERVICE_ACCOUNT_KEY_PATH='"
				+ STACKIT_SERVICE_ACCOUNT_KEY_PATH
				+ '\''
				+ ", STACKIT_SERVICE_ACCOUNT_KEY='"
				+ STACKIT_SERVICE_ACCOUNT_KEY
				+ '\''
				+ ", STACKIT_PRIVATE_KEY_PATH='"
				+ STACKIT_PRIVATE_KEY_PATH
				+ '\''
				+ ", STACKIT_PRIVATE_KEY='"
				+ STACKIT_PRIVATE_KEY
				+ '\''
				+ ", STACKIT_TOKEN_BASEURL='"
				+ STACKIT_TOKEN_BASEURL
				+ '\''
				+ ", STACKIT_CREDENTIALS_PATH='"
				+ STACKIT_CREDENTIALS_PATH
				+ '\''
				+ '}';
	}
}
