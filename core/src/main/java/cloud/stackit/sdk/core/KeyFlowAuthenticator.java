package cloud.stackit.sdk.core;

import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.model.ServiceAccountKey;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

/** KeyFlowAuthenticator handles the Key Flow Authentication based on the Service Account Key. */
public class KeyFlowAuthenticator {
	private final String REFRESH_TOKEN = "refresh_token";
	private final String ASSERTION = "assertion";
	private final String DEFAULT_TOKEN_ENDPOINT = "https://service-account.api.stackit.cloud/token";
	private final long DEFAULT_TOKEN_LEEWAY = 60;

	private final OkHttpClient httpClient;
	private final ServiceAccountKey saKey;
	private KeyFlowTokenResponse token;
	private final Gson gson;
	private final String tokenUrl;
	private long tokenLeewayInSeconds = DEFAULT_TOKEN_LEEWAY;

	private static class KeyFlowTokenResponse {
		@SerializedName("access_token")
		private String accessToken;

		@SerializedName("refresh_token")
		private String refreshToken;

		@SerializedName("expires_in")
		private long expiresIn;

		@SerializedName("scope")
		private String scope;

		@SerializedName("token_type")
		private String tokenType;

		public boolean isExpired() {
			return expiresIn < new Date().toInstant().getEpochSecond();
		}

		public String getAccessToken() {
			return accessToken;
		}
	}

	/**
	 * Creates the initial service account and refreshes expired access token.
	 *
	 * @param cfg Configuration to set a custom token endpoint and the token expiration leeway.
	 * @param saKey Service Account Key, which should be used for the authentication
	 * @throws InvalidKeySpecException thrown when the private key in the service account can not be
	 *     parsed
	 * @throws IOException thrown on unexpected responses from the key flow
	 * @throws ApiException thrown on unexpected responses from the key flow
	 */
	public KeyFlowAuthenticator(CoreConfiguration cfg, ServiceAccountKey saKey)
			throws InvalidKeySpecException, IOException, ApiException {
		this.saKey = saKey;
		this.gson = new Gson();
		this.httpClient =
				new OkHttpClient.Builder()
						.connectTimeout(10, TimeUnit.SECONDS)
						.writeTimeout(10, TimeUnit.SECONDS)
						.readTimeout(30, TimeUnit.SECONDS)
						.build();
		if (cfg.getTokenCustomUrl() != null && !cfg.getTokenCustomUrl().trim().isEmpty()) {
			this.tokenUrl = cfg.getTokenCustomUrl();
		} else {
			this.tokenUrl = DEFAULT_TOKEN_ENDPOINT;
		}
		if (cfg.getTokenExpirationLeeway() != null && cfg.getTokenExpirationLeeway() > 0) {
			this.tokenLeewayInSeconds = cfg.getTokenExpirationLeeway();
		}

		createAccessToken();
	}

	/**
	 * Returns access token. If the token is expired it creates a new token.
	 *
	 * @throws IOException request for new access token failed
	 * @throws ApiException response for new access token with bad status code
	 */
	public synchronized String getAccessToken() throws IOException, ApiException {
		if (token == null || token.isExpired()) {
			createAccessTokenWithRefreshToken();
		}
		return token.getAccessToken();
	}

	/**
	 * Creates the initial accessToken and stores it in `this.token`
	 *
	 * @throws InvalidKeySpecException can not parse private key
	 * @throws IOException request for access token failed
	 * @throws ApiException response for new access token with bad status code
	 * @throws JsonSyntaxException parsing of the created access token failed
	 */
	private void createAccessToken()
			throws InvalidKeySpecException, IOException, JsonSyntaxException, ApiException {
		String grant = "urn:ietf:params:oauth:grant-type:jwt-bearer";
		String assertion;
		try {
			assertion = generateSelfSignedJWT();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"could not find required algorithm for jwt signing. This should not happen and should be reported on https://github.com/stackitcloud/stackit-sdk-java/issues",
					e);
		}
		Response response = requestToken(grant, assertion).execute();
		parseTokenResponse(response);
		response.close();
	}

	/**
	 * Creates a new access token with the existing refresh token
	 *
	 * @throws IOException request for new access token failed
	 * @throws ApiException response for new access token with bad status code
	 * @throws JsonSyntaxException can not parse new access token
	 */
	private synchronized void createAccessTokenWithRefreshToken()
			throws IOException, JsonSyntaxException, ApiException {
		String refreshToken = token.refreshToken;
		Response response = requestToken(REFRESH_TOKEN, refreshToken).execute();
		parseTokenResponse(response);
		response.close();
	}

	private synchronized void parseTokenResponse(Response response)
			throws ApiException, JsonSyntaxException {
		if (response.code() != HttpURLConnection.HTTP_OK) {
			String body = null;
			if (response.body() != null) {
				body = response.body().toString();
				response.body().close();
			}
			throw new ApiException(
					response.message(), response.code(), response.headers().toMultimap(), body);
		}
		if (response.body() == null) {
			throw new JsonSyntaxException("body from token creation is null");
		}

		token =
				gson.fromJson(
						new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8),
						KeyFlowTokenResponse.class);
		token.expiresIn =
				JWT.decode(token.accessToken)
						.getExpiresAt()
						.toInstant()
						.minusSeconds(tokenLeewayInSeconds)
						.getEpochSecond();
		response.body().close();
	}

	private Call requestToken(String grant, String assertionValue) throws IOException {
		FormBody.Builder bodyBuilder = new FormBody.Builder();
		bodyBuilder.addEncoded("grant_type", grant);
		String assertionKey = grant.equals(REFRESH_TOKEN) ? REFRESH_TOKEN : ASSERTION;
		bodyBuilder.addEncoded(assertionKey, assertionValue);
		FormBody body = bodyBuilder.build();

		Request request =
				new Request.Builder()
						.url(tokenUrl)
						.post(body)
						.addHeader("Content-Type", "application/x-www-form-urlencoded")
						.build();
		return httpClient.newCall(request);
	}

	private String generateSelfSignedJWT()
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		RSAPrivateKey prvKey;

		prvKey = saKey.getCredentials().getPrivateKeyParsed();
		Algorithm algorithm = Algorithm.RSA512(prvKey);

		Map<String, Object> jwtHeader = new HashMap<>();
		jwtHeader.put("kid", saKey.getCredentials().getKid());

		return JWT.create()
				.withIssuer(saKey.getCredentials().getIss())
				.withSubject(saKey.getCredentials().getSub())
				.withJWTId(UUID.randomUUID().toString())
				.withAudience(saKey.getCredentials().getAud())
				.withIssuedAt(new Date())
				.withExpiresAt(new Date().toInstant().plusSeconds(10 * 60))
				.withHeader(jwtHeader)
				.sign(algorithm);
	}
}
