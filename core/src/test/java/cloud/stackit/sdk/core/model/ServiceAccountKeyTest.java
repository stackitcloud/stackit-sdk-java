package cloud.stackit.sdk.core.model;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

class ServiceAccountKeyTest {

	@Test
	void loadFromJson_validJson_returnSaKey() {
		final String uuid = "6d778bbf-6c86-46e6-952a-0c1b5fd87be3";
		final String iss = "service-account-test@sa.stackit.cloud";
		final String aud = "https://aud.stackit.cloud";

		final String privateKey =
				"-----BEGIN PRIVATE KEY-----\n"
						+ "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqPfgaTEWEP3S9w0t\n"
						+ "gsicURfo+nLW09/0KfOPinhYZ4ouzU+3xC4pSlEp8Ut9FgL0AgqNslNaK34Kq+NZ\n"
						+ "jO9DAQIDAQABAkAgkuLEHLaqkWhLgNKagSajeobLS3rPT0Agm0f7k55FXVt743hw\n"
						+ "Ngkp98bMNrzy9AQ1mJGbQZGrpr4c8ZAx3aRNAiEAoxK/MgGeeLui385KJ7ZOYktj\n"
						+ "hLBNAB69fKwTZFsUNh0CIQEJQRpFCcydunv2bENcN/oBTRw39E8GNv2pIcNxZkcb\n"
						+ "NQIgbYSzn3Py6AasNj6nEtCfB+i1p3F35TK/87DlPSrmAgkCIQDJLhFoj1gbwRbH\n"
						+ "/bDRPrtlRUDDx44wHoEhSDRdy77eiQIgE6z/k6I+ChN1LLttwX0galITxmAYrOBh\n"
						+ "BVl433tgTTQ=\n"
						+ "-----END PRIVATE KEY-----";

		final String jsonContent =
				"{\n"
						+ "  \"id\": \""
						+ uuid
						+ "\",\n"
						+ "  \"publicKey\": \"-----BEGIN PUBLIC KEY-----\\nMFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKj34GkxFhD90vcNLYLInFEX6Ppy1tPf\\n9Cnzj4p4WGeKLs1Pt8QuKUpRKfFLfRYC9AIKjbJTWit+CqvjWYzvQwECAwEAAQ==\\n-----END PUBLIC KEY-----\",\n"
						+ "  \"createdAt\": \"2025-01-01T01:00:00.000+00:00\",\n"
						+ "  \"keyType\": \"USER_MANAGED\",\n"
						+ "  \"keyOrigin\": \"GENERATED\",\n"
						+ "  \"keyAlgorithm\": \"RSA_2048\",\n"
						+ "  \"active\": true,\n"
						+ "  \"credentials\": {\n"
						+ "    \"kid\": \""
						+ uuid
						+ "\",\n"
						+ "    \"iss\": \""
						+ iss
						+ "\",\n"
						+ "    \"sub\": \""
						+ uuid
						+ "\",\n"
						+ "    \"aud\": \""
						+ aud
						+ "\",\n"
						+ "    \"privateKey\": \""
						+ privateKey
						+ "\"\n"
						+ "  }\n"
						+ "}\n";

		assertDoesNotThrow(() -> ServiceAccountKey.loadFromJson(jsonContent));
		ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(jsonContent);

		assertEquals(uuid, saKey.getId());
		assertEquals(uuid, saKey.getCredentials().getKid());
		assertEquals(iss, saKey.getCredentials().getIss());
		assertEquals(uuid, saKey.getCredentials().getSub());
		assertEquals(aud, saKey.getCredentials().getAud());
		assertEquals(privateKey, saKey.getCredentials().getPrivateKey());
	}

	@Test
	void loadFromJson_validJsonWithoutCredentials_throwsException() {
		final String jsonContent =
				"{\n"
						+ "  \"id\": \"6d778bbf-6c86-46e6-952a-0c1b5fd87be3\",\n"
						+ "  \"publicKey\": \"-----BEGIN PUBLIC KEY-----\\nMFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKj34GkxFhD90vcNLYLInFEX6Ppy1tPf\\n9Cnzj4p4WGeKLs1Pt8QuKUpRKfFLfRYC9AIKjbJTWit+CqvjWYzvQwECAwEAAQ==\\n-----END PUBLIC KEY-----\",\n"
						+ "  \"createdAt\": \"2025-01-01T01:00:00.000+00:00\",\n"
						+ "  \"keyType\": \"USER_MANAGED\",\n"
						+ "  \"keyOrigin\": \"GENERATED\",\n"
						+ "  \"keyAlgorithm\": \"RSA_2048\",\n"
						+ "  \"active\": true\n"
						+ "}\n";

		assertThrows(JsonSyntaxException.class, () -> ServiceAccountKey.loadFromJson(jsonContent));
	}

	@Test
	void loadFromJson_validJsonWithoutPrivateKey_returnSaKey() {
		final String uuid = "6d778bbf-6c86-46e6-952a-0c1b5fd87be3";
		final String iss = "service-account-test@sa.stackit.cloud";
		final String aud = "https://aud.stackit.cloud";

		final String jsonContent =
				"{\n"
						+ "  \"id\": \""
						+ uuid
						+ "\",\n"
						+ "  \"publicKey\": \"-----BEGIN PUBLIC KEY-----\\nMFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKj34GkxFhD90vcNLYLInFEX6Ppy1tPf\\n9Cnzj4p4WGeKLs1Pt8QuKUpRKfFLfRYC9AIKjbJTWit+CqvjWYzvQwECAwEAAQ==\\n-----END PUBLIC KEY-----\",\n"
						+ "  \"createdAt\": \"2025-01-01T01:00:00.000+00:00\",\n"
						+ "  \"keyType\": \"USER_MANAGED\",\n"
						+ "  \"keyOrigin\": \"GENERATED\",\n"
						+ "  \"keyAlgorithm\": \"RSA_2048\",\n"
						+ "  \"active\": true,\n"
						+ "  \"credentials\": {\n"
						+ "    \"kid\": \""
						+ uuid
						+ "\",\n"
						+ "    \"iss\": \""
						+ iss
						+ "\",\n"
						+ "    \"sub\": \""
						+ uuid
						+ "\",\n"
						+ "    \"aud\": \""
						+ aud
						+ "\"\n"
						+ "  }\n"
						+ "}\n";

		assertDoesNotThrow(() -> ServiceAccountKey.loadFromJson(jsonContent));
		ServiceAccountKey saKey = ServiceAccountKey.loadFromJson(jsonContent);

		assertEquals(uuid, saKey.getId());
		assertEquals(uuid, saKey.getCredentials().getKid());
		assertEquals(iss, saKey.getCredentials().getIss());
		assertEquals(uuid, saKey.getCredentials().getSub());
		assertEquals(aud, saKey.getCredentials().getAud());
	}
}
