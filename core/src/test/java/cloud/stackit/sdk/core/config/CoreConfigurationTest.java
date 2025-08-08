package cloud.stackit.sdk.core.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CoreConfigurationTest {

	@Test
	void getDefaultHeader() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		CoreConfiguration cfg = new CoreConfiguration.Builder().defaultHeader(map).build();
		Map<String, String> cfgHeader = cfg.getDefaultHeader();

		assertEquals(map, cfgHeader);
	}

	@Test
	void getServiceAccountKey() {
		final String saKey = "<sa-key>";

		CoreConfiguration cfg = new CoreConfiguration.Builder().serviceAccountKey(saKey).build();

		String cfgSaKey = cfg.getServiceAccountKey();

		assertEquals(saKey, cfgSaKey);
	}

	@Test
	void getServiceAccountKeyPath() {
		final String saKeyPath = "<sa-key-path>";

		CoreConfiguration cfg =
				new CoreConfiguration.Builder().serviceAccountKeyPath(saKeyPath).build();

		String cfgSaKeyPath = cfg.getServiceAccountKeyPath();

		assertEquals(saKeyPath, cfgSaKeyPath);
	}

	@Test
	void getPrivateKeyPath() {
		final String privateKeyPath = "<private-key-path>";

		CoreConfiguration cfg =
				new CoreConfiguration.Builder().privateKeyPath(privateKeyPath).build();

		String cfgPrivateKeyPath = cfg.getPrivateKeyPath();

		assertEquals(privateKeyPath, cfgPrivateKeyPath);
	}

	@Test
	void getPrivateKey() {
		final String privateKey = "<private-key>";

		CoreConfiguration cfg = new CoreConfiguration.Builder().privateKey(privateKey).build();

		String cfgPrivateKey = cfg.getPrivateKey();

		assertEquals(privateKey, cfgPrivateKey);
	}

	@Test
	void getCustomEndpoint() {
		final String customEndpoint = "<custom-endpoint>";

		CoreConfiguration cfg =
				new CoreConfiguration.Builder().customEndpoint(customEndpoint).build();

		String cfgCustomEndpoint = cfg.getCustomEndpoint();

		assertEquals(customEndpoint, cfgCustomEndpoint);
	}

	@Test
	void getCredentialsFilePath() {
		final String credFilePath = "<cred-file-path>";

		CoreConfiguration cfg =
				new CoreConfiguration.Builder().credentialsFilePath(credFilePath).build();

		String cfgCredentialsFilePath = cfg.getCredentialsFilePath();

		assertEquals(credFilePath, cfgCredentialsFilePath);
	}

	@Test
	void getTokenCustomUrl() {
		final String tokenCustomUrl = "<token-custom-url>";

		CoreConfiguration cfg =
				new CoreConfiguration.Builder().tokenCustomUrl(tokenCustomUrl).build();

		String cfgTokenUrl = cfg.getTokenCustomUrl();

		assertEquals(tokenCustomUrl, cfgTokenUrl);
	}

	@Test
	void getTokenExpirationLeeway() {
		final long tokenExpireLeeway = 100;

		CoreConfiguration cfg =
				new CoreConfiguration.Builder().tokenExpirationLeeway(tokenExpireLeeway).build();

		Long cfgTokenExpirationLeeway = cfg.getTokenExpirationLeeway();

		assertEquals(tokenExpireLeeway, cfgTokenExpirationLeeway);
	}

	@Test
	void getDefaultHeader_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		Map<String, String> defaultHeader = cfg.getDefaultHeader();

		assertNull(defaultHeader);
	}

	@Test
	void getServiceAccountKey_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		String serviceAccountKey = cfg.getServiceAccountKey();

		assertNull(serviceAccountKey);
	}

	@Test
	void getServiceAccountKeyPath_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		String serviceAccountKeyPath = cfg.getServiceAccountKeyPath();

		assertNull(serviceAccountKeyPath);
	}

	@Test
	void getPrivateKeyPath_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		String privateKeyPath = cfg.getPrivateKeyPath();

		assertNull(privateKeyPath);
	}

	@Test
	void getPrivateKey_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		String privateKey = cfg.getPrivateKey();

		assertNull(privateKey);
	}

	@Test
	void getCustomEndpoint_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		String customEndpoint = cfg.getCustomEndpoint();

		assertNull(customEndpoint);
	}

	@Test
	void getCredentialsFilePath_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		String credentialsFilePath = cfg.getCredentialsFilePath();

		assertNull(credentialsFilePath);
	}

	@Test
	void getTokenCustomUrl_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		String tokenCustomUrl = cfg.getTokenCustomUrl();

		assertNull(tokenCustomUrl);
	}

	@Test
	void getTokenExpirationLeeway_not_set() {
		CoreConfiguration cfg = new CoreConfiguration.Builder().build();
		Long tokenExpirationLeeway = cfg.getTokenExpirationLeeway();

		assertNull(tokenExpirationLeeway);
	}
}
