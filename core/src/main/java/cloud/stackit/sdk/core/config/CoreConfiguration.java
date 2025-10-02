package cloud.stackit.sdk.core.config;

import java.util.Map;

public class CoreConfiguration {
	private Map<String, String> defaultHeader;
	private String serviceAccountKey;
	private String serviceAccountKeyPath;
	private String privateKeyPath;
	private String privateKey;
	private String customEndpoint;
	private String credentialsFilePath;
	private String tokenCustomUrl;
	private Long tokenExpirationLeeway;

	public Map<String, String> getDefaultHeader() {
		return defaultHeader;
	}

	public String getServiceAccountKey() {
		return serviceAccountKey;
	}

	public String getServiceAccountKeyPath() {
		return serviceAccountKeyPath;
	}

	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getCustomEndpoint() {
		return customEndpoint;
	}

	public String getCredentialsFilePath() {
		return credentialsFilePath;
	}

	public String getTokenCustomUrl() {
		return tokenCustomUrl;
	}

	public Long getTokenExpirationLeeway() {
		return tokenExpirationLeeway;
	}

	public CoreConfiguration defaultHeader(Map<String, String> defaultHeader) {
		this.defaultHeader = defaultHeader;
		return this;
	}

	public CoreConfiguration serviceAccountKey(String serviceAccountKey) {
		this.serviceAccountKey = serviceAccountKey;
		return this;
	}

	public CoreConfiguration serviceAccountKeyPath(String serviceAccountKeyPath) {
		this.serviceAccountKeyPath = serviceAccountKeyPath;
		return this;
	}

	public CoreConfiguration privateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
		return this;
	}

	public CoreConfiguration privateKey(String privateKey) {
		this.privateKey = privateKey;
		return this;
	}

	public CoreConfiguration customEndpoint(String customEndpoint) {
		this.customEndpoint = customEndpoint;
		return this;
	}

	public CoreConfiguration credentialsFilePath(String credentialsFilePath) {
		this.credentialsFilePath = credentialsFilePath;
		return this;
	}

	public CoreConfiguration tokenCustomUrl(String tokenCustomUrl) {
		this.tokenCustomUrl = tokenCustomUrl;
		return this;
	}

	public CoreConfiguration tokenExpirationLeeway(Long tokenExpirationLeeway) {
		this.tokenExpirationLeeway = tokenExpirationLeeway;
		return this;
	}
}
