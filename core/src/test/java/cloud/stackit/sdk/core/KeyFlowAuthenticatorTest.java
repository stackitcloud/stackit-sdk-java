package cloud.stackit.sdk.core;

import static org.junit.jupiter.api.Assertions.*;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.model.ServiceAccountCredentials;
import cloud.stackit.sdk.core.model.ServiceAccountKey;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyFlowAuthenticatorTest {
	private static MockWebServer mockWebServer;
	private ServiceAccountKey defaultSaKey;
	private final String privateKey =
			"-----BEGIN PRIVATE KEY-----\n"
					+ "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC0jVPq7ACbkwW6\n"
					+ "ojf6akoAlqkSLpAaTESOKEw6Hi2chr6gV4I1jtVLJM5K1e+vR+bKFBAzBVk9NCKS\n"
					+ "EiN+fTzEuz+z7sEhM5yBv4LUCrk3HoUT0nptFCLlJ40dmFlqmFSRtqSfX04kCs8N\n"
					+ "+HqTgCWEMVKd4Vq75jJAH2QYKLTa9nWPolxZK+e2twd3HQoVFP9fZQPNK0TNrn85\n"
					+ "beA1MYYT7U+oxAJEzPrFHcmJvvZW88rL7iZ0A7lqpXSVg/Uvu29nfW7lIZgc/FFI\n"
					+ "eWeHySKJ68YLzl4WV5rNRqzcJ2NKMn7+j1SYSJvsOrlAcptz5jyp/pwyVK3RpoQt\n"
					+ "mKsZKZJHAgMBAAECggEBAJSdH83mpDlqMvUEQX9lrbP+TvwgR4zd6i/5C4VrAbAt\n"
					+ "WQsx/IOJJhfMG+GNZtSoIleDXDIi3Ol72FjThVPAUhy85Bp/E4j4qoJB2LYgfYPZ\n"
					+ "I0DFpu/R+0cT3xvVIwSSjknCRI7KK8+O9g9Rz9NJT5gX4SEDNWQkfog5TnJ0TylL\n"
					+ "Ako6XIXFUar93AWuRKJgrsNvt/47ojb4hbTe8kUSMK2yXUJ80AvjHw0TiAFLjBxw\n"
					+ "YvlYtaoSbBB5Wp+3FuedKfJWbnMs6K/EwH1McDFc1xo9zS/ovmV2/YlRWDzRIJ5n\n"
					+ "ozTbHjnOmJ8ZF3NQh7kX0UmUbUi+qdL4yY1ON9SWmYECgYEA20YNmAJIrm6RrhA1\n"
					+ "2bCshnWqOw7PIkV3Pv3U5gPqZjaT9wSgnI2y/rauooTjiT/gNSRlUZZot+4hmKq5\n"
					+ "dxAlyFv8ibQYKRpwAJUMtTL8W66YMpfGkZ4WQ6hOsuC7ZcpGRyUXIPgqv/JOxR0D\n"
					+ "PpAGkAIU5bhauZRW8Yl54gy0f1kCgYEA0sr7lTlNy4XNxJli1L2tX7jzkF/2AZDb\n"
					+ "4ltj8aGYkhpOjNIHSn2lHQwbJiksTfVq+8XZ7hlwOk6DbT5Ev1qZ72i+lcxpsf8a\n"
					+ "/NNn7MUw9woAIYe+iKJfA9jagg73rT5HjhXXob1KAFui4Jp00MCk5JJ5MGV+jf2+\n"
					+ "mY9Q/TMdCp8CgYAfDIxgOfKQwJdgTmtRp/LGF2NDeZVbBPsdsFO1PliyoIfTMpSL\n"
					+ "loUCDFwuJyMRDDpzS/QM2X96i/214HbipSa0eFIKLbY+G8BAVNq3zcBuOwrSHyu+\n"
					+ "8uO0MODz816Vy06oRFhCEuH6zBTbVIBhG4PSYHkVDkXKgXbOPOlFWQc2AQKBgQC8\n"
					+ "yUyO9haFi52hURqhnAsVquiAymDiQCGeVelp9CdX2rW1Czm6blMdc8Uw5TknzP/2\n"
					+ "49jtlNzda4nrohQiKPuq3m2qbbvPzcEW5CO0e1sCNXOulAuCBaIBKQtx5gPOpfOh\n"
					+ "/k/0LDqFnYx/ifXLLG3BxKlDPfMdKj+0+hU337pH0wKBgHZ4A6fWlsWMXjr2blLt\n"
					+ "eHOkcU4xJ6Rkwnpn03IgWwbMV6UAXOLOfJ16JPNGQ9xxt4uEo4BMTBXr30l1LULj\n"
					+ "5vKz3Q54ZQY8DYKoQ0b66MZX/YeFvCKC3pr7YILBXt1gPA+/9PXSKPb9HlE8NSG6\n"
					+ "h/9afEtu5aUE/m+1vGBoH8z1\n"
					+ "-----END PRIVATE KEY-----\n";

	ServiceAccountKey createDummyServiceAccount() {
		ServiceAccountCredentials credentials =
				new ServiceAccountCredentials("aud", "iss", "kid", privateKey, "sub");
		return new ServiceAccountKey(
				"id",
				"publicKey",
				Date.from( // Workaround that ServiceAccountKey can be compared in tests
						new Date().toInstant().truncatedTo(ChronoUnit.SECONDS)),
				"keyType",
				"keyOrigin",
				"keyAlgo",
				true,
				Date.from( // Workaround that ServiceAccountKey can be compared in tests
						new Date().toInstant().truncatedTo(ChronoUnit.SECONDS)),
				credentials);
	}

	KeyFlowAuthenticator.KeyFlowTokenResponse mockResponseBody(boolean expired)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		Date issuedAt = new Date();
		Date expiredAt = Date.from(new Date().toInstant().plusSeconds(60 * 10));
		if (expired) {
			expiredAt = Date.from(new Date().toInstant().minusSeconds(60 * 10));
		}

		// Create mock response
		RSAPrivateKey signingKey = defaultSaKey.getCredentials().getPrivateKeyParsed();
		Algorithm algorithm = Algorithm.RSA512(signingKey);
		String accessTokenResponse =
				JWT.create().withIssuedAt(issuedAt).withExpiresAt(expiredAt).sign(algorithm);
		return new KeyFlowAuthenticator.KeyFlowTokenResponse(
				accessTokenResponse, accessTokenResponse, 0, "scope", "USER_GENERATED");
	}

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
		defaultSaKey = createDummyServiceAccount();
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void getAccessToken_response200_noException()
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, ApiException {

		// Setup mockServer
		KeyFlowAuthenticator.KeyFlowTokenResponse responseBody = mockResponseBody(false);
		String responseBodyJson = new Gson().toJson(responseBody);
		MockResponse mockedResponse =
				new MockResponse().setBody(responseBodyJson).setResponseCode(200);
		mockWebServer.enqueue(mockedResponse);

		// Config
		HttpUrl url = mockWebServer.url("/token");
		CoreConfiguration cfg =
				new CoreConfiguration().tokenCustomUrl(url.toString()); // Use mockWebServer

		KeyFlowAuthenticator keyFlowAuthenticator = new KeyFlowAuthenticator(cfg, defaultSaKey);

		assertDoesNotThrow(keyFlowAuthenticator::getAccessToken);
		assertEquals(responseBody.getAccessToken(), keyFlowAuthenticator.getAccessToken());
	}

	@Test
	void getAccessToken_expiredToken_noException()
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, ApiException {
		// Setup expiredToken and newToken
		KeyFlowAuthenticator.KeyFlowTokenResponse expiredKey = mockResponseBody(true);

		KeyFlowAuthenticator.KeyFlowTokenResponse newToken = mockResponseBody(false);

		// Setup mockServer
		String responseBodyJson = new Gson().toJson(newToken);
		MockResponse mockedResponse =
				new MockResponse().setBody(responseBodyJson).setResponseCode(200);
		mockWebServer.enqueue(mockedResponse);

		// Config
		HttpUrl url = mockWebServer.url("/token");
		CoreConfiguration cfg =
				new CoreConfiguration().tokenCustomUrl(url.toString()); // Use mockWebServer

		KeyFlowAuthenticator keyFlowAuthenticator = new KeyFlowAuthenticator(cfg, defaultSaKey);
		keyFlowAuthenticator.setToken(expiredKey);

		assertEquals(newToken.getAccessToken(), keyFlowAuthenticator.getAccessToken());
	}

	@Test
	void createAccessToken_response200WithEmptyBody_throwsException() {
		// Setup mockServer
		MockResponse mockedResponse = new MockResponse().setResponseCode(200);
		mockWebServer.enqueue(mockedResponse);
		HttpUrl url = mockWebServer.url("/token");

		// Config
		CoreConfiguration cfg =
				new CoreConfiguration().tokenCustomUrl(url.toString()); // Use mockWebServer

		// Init keyFlowAuthenticator
		KeyFlowAuthenticator keyFlowAuthenticator =
				new KeyFlowAuthenticator(cfg, createDummyServiceAccount());

		assertThrows(JsonSyntaxException.class, keyFlowAuthenticator::createAccessToken);
	}

	@Test
	void createAccessToken_response400_throwsApiException() {
		// Setup mockServer
		MockResponse mockedResponse = new MockResponse().setResponseCode(400);
		mockWebServer.enqueue(mockedResponse);
		HttpUrl url = mockWebServer.url("/token");

		// Config
		CoreConfiguration cfg =
				new CoreConfiguration().tokenCustomUrl(url.toString()); // Use mockWebServer

		// Init keyFlowAuthenticator
		KeyFlowAuthenticator keyFlowAuthenticator =
				new KeyFlowAuthenticator(cfg, createDummyServiceAccount());

		assertThrows(ApiException.class, keyFlowAuthenticator::createAccessToken);
	}

	@Test
	void createAccessToken_response200WithValidResponse_noException()
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Setup mockServer
		KeyFlowAuthenticator.KeyFlowTokenResponse responseBody = mockResponseBody(false);
		String responseBodyJson = new Gson().toJson(responseBody);
		MockResponse mockedResponse =
				new MockResponse().setBody(responseBodyJson).setResponseCode(200);
		mockWebServer.enqueue(mockedResponse);

		// Config
		HttpUrl url = mockWebServer.url("/token");
		CoreConfiguration cfg =
				new CoreConfiguration().tokenCustomUrl(url.toString()); // Use mockWebServer

		// Init keyFlowAuthenticator
		KeyFlowAuthenticator keyFlowAuthenticator = new KeyFlowAuthenticator(cfg, defaultSaKey);

		assertDoesNotThrow(keyFlowAuthenticator::createAccessToken);
	}

	@Test
	void createAccessTokenWithRefreshToken_response200WithValidResponse_noException()
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Setup mockServer
		KeyFlowAuthenticator.KeyFlowTokenResponse mockedBody = mockResponseBody(false);
		String mockedBodyJson = new Gson().toJson(mockedBody);
		MockResponse mockedResponse =
				new MockResponse().setBody(mockedBodyJson).setResponseCode(200);
		mockWebServer.enqueue(mockedResponse);

		// Config
		HttpUrl url = mockWebServer.url("/token");
		CoreConfiguration cfg =
				new CoreConfiguration().tokenCustomUrl(url.toString()); // Use mockWebServer

		// Prepare keyFlowAuthenticator
		KeyFlowAuthenticator keyFlowAuthenticator = new KeyFlowAuthenticator(cfg, defaultSaKey);
		keyFlowAuthenticator.setToken(mockedBody);

		assertDoesNotThrow(keyFlowAuthenticator::createAccessTokenWithRefreshToken);
	}

	@Test
	void createAccessTokenWithRefreshToken_response200WithEmptyBody_throwsException()
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Setup mockServer
		KeyFlowAuthenticator.KeyFlowTokenResponse mockResponse = mockResponseBody(false);
		MockResponse mockedResponse = new MockResponse().setResponseCode(200);
		mockWebServer.enqueue(mockedResponse);
		HttpUrl url = mockWebServer.url("/token");

		// Config
		CoreConfiguration cfg =
				new CoreConfiguration().tokenCustomUrl(url.toString()); // Use mockWebServer

		// Prepare keyFlowAuthenticator
		KeyFlowAuthenticator keyFlowAuthenticator =
				new KeyFlowAuthenticator(cfg, createDummyServiceAccount());
		keyFlowAuthenticator.setToken(mockResponse);

		// Refresh token
		assertThrows(
				JsonSyntaxException.class, keyFlowAuthenticator::createAccessTokenWithRefreshToken);
	}
}
