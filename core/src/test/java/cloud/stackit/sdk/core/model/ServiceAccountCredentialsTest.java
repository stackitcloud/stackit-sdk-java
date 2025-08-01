package cloud.stackit.sdk.core.model;

import static org.junit.jupiter.api.Assertions.*;

import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.Test;

class ServiceAccountCredentialsTest {

	@Test
	void isPrivateKeySet_null_returnsFalse() {
		ServiceAccountCredentials saCreds =
				new ServiceAccountCredentials(null, null, null, null, null);

		assertFalse(saCreds.isPrivateKeySet());
	}

	@Test
	void isPrivateKeySet_emptyString_returnsFalse() {
		ServiceAccountCredentials saCreds =
				new ServiceAccountCredentials(null, null, null, "", null);

		assertFalse(saCreds.isPrivateKeySet());
	}

	@Test
	void isPrivateKeySet_emptyStringWhitespaces_returnsFalse() {
		ServiceAccountCredentials saCreds =
				new ServiceAccountCredentials(null, null, null, "  ", null);

		assertFalse(saCreds.isPrivateKeySet());
	}

	@Test
	void isPrivateKeySet_string_returnsFalse() {
		ServiceAccountCredentials saCreds =
				new ServiceAccountCredentials(null, null, null, "my-private-key", null);

		assertTrue(saCreds.isPrivateKeySet());
	}

	@Test
	void getPrivateKeyParsed_notBase64Key_throwsException() {
		ServiceAccountCredentials saCreds =
				new ServiceAccountCredentials(null, null, null, "my-private-key", null);

		assertThrows(IllegalArgumentException.class, saCreds::getPrivateKeyParsed);
	}

	@Test
	void getPrivateKeyParsed_invalidKey_throwsException() {
		ServiceAccountCredentials saCreds =
				new ServiceAccountCredentials(null, null, null, "bXktcHJpdmF0ZS1rZXk=", null);

		assertThrows(InvalidKeySpecException.class, saCreds::getPrivateKeyParsed);
	}

	@Test
	void getPrivateKeyParsed_validKey_returnsRsaKey() {
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

		ServiceAccountCredentials saCreds =
				new ServiceAccountCredentials(null, null, null, privateKey, null);

		assertDoesNotThrow(saCreds::getPrivateKeyParsed);
	}
}
