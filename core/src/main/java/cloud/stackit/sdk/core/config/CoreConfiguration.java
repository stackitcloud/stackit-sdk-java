package cloud.stackit.sdk.core.config;

import java.util.Map;

public class CoreConfiguration {
	private final Map<String, String> defaultHeader;
	private final String serviceAccountKey;
	private final String serviceAccountKeyPath;
	private final String privateKeyPath;
	private final String privateKey;
	private final String customEndpoint;
	private final String credentialsFilePath;
	private final String tokenCustomUrl;
	private final Long tokenExpirationLeeway;

	CoreConfiguration(Builder builder) {
		this.defaultHeader = builder.defaultHeader;
		this.serviceAccountKey = builder.serviceAccountKey;
		this.serviceAccountKeyPath = builder.serviceAccountKeyPath;
		this.privateKeyPath = builder.privateKeyPath;
		this.privateKey = builder.privateKey;
		this.customEndpoint = builder.customEndpoint;
		this.credentialsFilePath = builder.credentialsFilePath;
		this.tokenCustomUrl = builder.tokenCustomUrl;
		this.tokenExpirationLeeway = builder.tokenExpirationLeeway;
	}

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

	public static class Builder {
		private Map<String, String> defaultHeader;
		private String serviceAccountKey;
		private String serviceAccountKeyPath;
		private String privateKeyPath;
		private String privateKey;
		private String customEndpoint;
		private String credentialsFilePath;
		private String tokenCustomUrl;
		private Long tokenExpirationLeeway;

		public Builder defaultHeader(Map<String, String> defaultHeader) {
			this.defaultHeader = defaultHeader;
			return this;
		}

		public Builder serviceAccountKey(String serviceAccountKey) {
			this.serviceAccountKey = serviceAccountKey;
			return this;
		}

		public Builder serviceAccountKeyPath(String serviceAccountKeyPath) {
			this.serviceAccountKeyPath = serviceAccountKeyPath;
			return this;
		}

		public Builder privateKeyPath(String privateKeyPath) {
			this.privateKeyPath = privateKeyPath;
			return this;
		}

		public Builder privateKey(String privateKey) {
			this.privateKey = privateKey;
			return this;
		}

		public Builder customEndpoint(String customEndpoint) {
			this.customEndpoint = customEndpoint;
			return this;
		}

		public Builder credentialsFilePath(String credentialsFilePath) {
			this.credentialsFilePath = credentialsFilePath;
			return this;
		}

		public Builder tokenCustomUrl(String tokenCustomUrl) {
			this.tokenCustomUrl = tokenCustomUrl;
			return this;
		}

		public Builder tokenExpirationLeeway(Long tokenExpirationLeeway) {
			this.tokenExpirationLeeway = tokenExpirationLeeway;
			return this;
		}

		public CoreConfiguration build() {
			return new CoreConfiguration(this);
		}
	}
}
