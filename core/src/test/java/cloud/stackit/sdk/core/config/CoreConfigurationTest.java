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
		CoreConfiguration cfg = new CoreConfiguration().defaultHeader(map);
		Map<String, String> cfgHeader = cfg.getDefaultHeader();

		assertEquals(map, cfgHeader);
	}

	@Test
	void getServiceAccountKey() {
		final String saKey = "<sa-key>";

		CoreConfiguration cfg = new CoreConfiguration().serviceAccountKey(saKey);

		String cfgSaKey = cfg.getServiceAccountKey();

		assertEquals(saKey, cfgSaKey);
	}

	@Test
	void getServiceAccountKeyPath() {
		final String saKeyPath = "<sa-key-path>";

		CoreConfiguration cfg = new CoreConfiguration().serviceAccountKeyPath(saKeyPath);

		String cfgSaKeyPath = cfg.getServiceAccountKeyPath();

		assertEquals(saKeyPath, cfgSaKeyPath);
	}

	@Test
	void getPrivateKeyPath() {
		final String privateKeyPath = "<private-key-path>";

		CoreConfiguration cfg = new CoreConfiguration().privateKeyPath(privateKeyPath);

		String cfgPrivateKeyPath = cfg.getPrivateKeyPath();

		assertEquals(privateKeyPath, cfgPrivateKeyPath);
	}

	@Test
	void getPrivateKey() {
		final String privateKey = "<private-key>";

		CoreConfiguration cfg = new CoreConfiguration().privateKey(privateKey);

		String cfgPrivateKey = cfg.getPrivateKey();

		assertEquals(privateKey, cfgPrivateKey);
	}

	@Test
	void getCustomEndpoint() {
		final String customEndpoint = "<custom-endpoint>";

		CoreConfiguration cfg = new CoreConfiguration().customEndpoint(customEndpoint);

		String cfgCustomEndpoint = cfg.getCustomEndpoint();

		assertEquals(customEndpoint, cfgCustomEndpoint);
	}

	@Test
	void getCredentialsFilePath() {
		final String credFilePath = "<cred-file-path>";

		CoreConfiguration cfg = new CoreConfiguration().credentialsFilePath(credFilePath);

		String cfgCredentialsFilePath = cfg.getCredentialsFilePath();

		assertEquals(credFilePath, cfgCredentialsFilePath);
	}

	@Test
	void getTokenCustomUrl() {
		final String tokenCustomUrl = "<token-custom-url>";

		CoreConfiguration cfg = new CoreConfiguration().tokenCustomUrl(tokenCustomUrl);

		String cfgTokenUrl = cfg.getTokenCustomUrl();

		assertEquals(tokenCustomUrl, cfgTokenUrl);
	}

	@Test
	void getTokenExpirationLeeway() {
		final long tokenExpireLeeway = 100;

		CoreConfiguration cfg = new CoreConfiguration().tokenExpirationLeeway(tokenExpireLeeway);

		Long cfgTokenExpirationLeeway = cfg.getTokenExpirationLeeway();

		assertEquals(tokenExpireLeeway, cfgTokenExpirationLeeway);
	}

	@Test
	void getDefaultHeader_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		Map<String, String> defaultHeader = cfg.getDefaultHeader();

		assertNull(defaultHeader);
	}

	@Test
	void getServiceAccountKey_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		String serviceAccountKey = cfg.getServiceAccountKey();

		assertNull(serviceAccountKey);
	}

	@Test
	void getServiceAccountKeyPath_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		String serviceAccountKeyPath = cfg.getServiceAccountKeyPath();

		assertNull(serviceAccountKeyPath);
	}

	@Test
	void getPrivateKeyPath_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		String privateKeyPath = cfg.getPrivateKeyPath();

		assertNull(privateKeyPath);
	}

	@Test
	void getPrivateKey_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		String privateKey = cfg.getPrivateKey();

		assertNull(privateKey);
	}

	@Test
	void getCustomEndpoint_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		String customEndpoint = cfg.getCustomEndpoint();

		assertNull(customEndpoint);
	}

	@Test
	void getCredentialsFilePath_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		String credentialsFilePath = cfg.getCredentialsFilePath();

		assertNull(credentialsFilePath);
	}

	@Test
	void getTokenCustomUrl_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		String tokenCustomUrl = cfg.getTokenCustomUrl();

		assertNull(tokenCustomUrl);
	}

	@Test
	void getTokenExpirationLeeway_not_set() {
		CoreConfiguration cfg = new CoreConfiguration();
		Long tokenExpirationLeeway = cfg.getTokenExpirationLeeway();

		assertNull(tokenExpirationLeeway);
	}
}
