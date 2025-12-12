package cloud.stackit.sdk.core;

import cloud.stackit.sdk.core.auth.SetupAuth;
import cloud.stackit.sdk.core.config.CoreConfiguration;
import cloud.stackit.sdk.core.config.EnvironmentVariables;
import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.exception.AuthenticationException;
import cloud.stackit.sdk.core.model.ServiceAccountKey;
import cloud.stackit.sdk.core.utils.Utils;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

/*
 * KeyFlowAuthenticator handles the Key Flow Authentication based on the Service Account Key.
 */
public class KeyFlowAuthenticator implements Authenticator {
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String ASSERTION = "assertion";
	private static final String DEFAULT_TOKEN_ENDPOINT =
			"https://service-account.api.stackit.cloud/token";
	private static final long DEFAULT_TOKEN_LEEWAY = 60;
	private static final int CONNECT_TIMEOUT = 10;
	private static final int WRITE_TIMEOUT = 10;
	private static final int READ_TIMEOUT = 10;

	private final OkHttpClient httpClient;
	private final ServiceAccountKey saKey;
	private KeyFlowTokenResponse token;
	private final Gson gson;
	private final String tokenUrl;
	private long tokenLeewayInSeconds = DEFAULT_TOKEN_LEEWAY;

	private final Object tokenRefreshMonitor = new Object();

	/**
	 * Creates the initial service account and refreshes expired access token.
	 *
	 * <p>NOTE: It's normal that 2 requests are sent, it's regular OkHttp Authenticator behavior.
	 * The first request is always attempted without the authenticator and in case the response is
	 * Unauthorized(=401), OkHttp reattempt the request with the authenticator. See <a
	 * href="https://square.github.io/okhttp/recipes/#handling-authentication-kt-java">OkHttp
	 * Docs</a>
	 *
	 * @deprecated use constructor with OkHttpClient instead to prevent resource leaks. Will be
	 *     removed in April 2026.
	 * @param cfg Configuration to set a custom token endpoint and the token expiration leeway.
	 * @param saKey Service Account Key, which should be used for the authentication
	 */
	@Deprecated
	public KeyFlowAuthenticator(CoreConfiguration cfg, ServiceAccountKey saKey) {
		this(new OkHttpClient(), cfg, saKey, new EnvironmentVariables());
	}

	/**
	 * Creates the initial service account and refreshes expired access token.
	 *
	 * <p>NOTE: It's normal that 2 requests are sent, it's regular OkHttp Authenticator behavior.
	 * The first request is always attempted without the authenticator and in case the response is
	 * Unauthorized(=401), OkHttp reattempt the request with the authenticator. See <a
	 * href="https://square.github.io/okhttp/recipes/#handling-authentication-kt-java">OkHttp
	 * Docs</a>
	 *
	 * @deprecated use constructor with OkHttpClient instead to prevent resource leaks. Will be
	 *     removed in April 2026.
	 * @param cfg Configuration to set a custom token endpoint and the token expiration leeway.
	 * @param saKey Service Account Key, which should be used for the authentication
	 */
	@Deprecated
	public KeyFlowAuthenticator(
			CoreConfiguration cfg,
			ServiceAccountKey saKey,
			EnvironmentVariables environmentVariables) {
		this(new OkHttpClient(), cfg, saKey, environmentVariables);
	}

	/**
	 * Creates the initial service account and refreshes expired access token.
	 *
	 * <p>NOTE: It's normal that 2 requests are sent, it's regular OkHttp Authenticator behavior.
	 * The first request is always attempted without the authenticator and in case the response is
	 * Unauthorized(=401), OkHttp reattempt the request with the authenticator. See <a
	 * href="https://square.github.io/okhttp/recipes/#handling-authentication-kt-java">OkHttp
	 * Docs</a>
	 *
	 * @param httpClient OkHttpClient object
	 * @param cfg Configuration to set a custom token endpoint and the token expiration leeway.
	 */
	public KeyFlowAuthenticator(OkHttpClient httpClient, CoreConfiguration cfg) throws IOException {
		this(httpClient, cfg, SetupAuth.setupKeyFlow(cfg), new EnvironmentVariables());
	}

	/**
	 * Creates the initial service account and refreshes expired access token.
	 *
	 * <p>NOTE: It's normal that 2 requests are sent, it's regular OkHttp Authenticator behavior.
	 * The first request is always attempted without the authenticator and in case the response is
	 * Unauthorized(=401), OkHttp reattempt the request with the authenticator. See <a
	 * href="https://square.github.io/okhttp/recipes/#handling-authentication-kt-java">OkHttp
	 * Docs</a>
	 *
	 * @param httpClient OkHttpClient object
	 * @param cfg Configuration to set a custom token endpoint and the token expiration leeway.
	 * @param saKey Service Account Key, which should be used for the authentication
	 */
	public KeyFlowAuthenticator(
			OkHttpClient httpClient, CoreConfiguration cfg, ServiceAccountKey saKey) {
		this(httpClient, cfg, saKey, new EnvironmentVariables());
	}

	protected KeyFlowAuthenticator(
			OkHttpClient httpClient,
			CoreConfiguration cfg,
			ServiceAccountKey saKey,
			EnvironmentVariables environmentVariables) {
		this.saKey = saKey;
		this.gson = new Gson();
		this.httpClient =
				httpClient
						.newBuilder()
						.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
						.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
						.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
						.build();

		if (Utils.isStringSet(cfg.getTokenCustomUrl())) {
			this.tokenUrl = cfg.getTokenCustomUrl();
		} else if (Utils.isStringSet(environmentVariables.getStackitTokenBaseurl())) {
			this.tokenUrl = environmentVariables.getStackitTokenBaseurl();
		} else {
			this.tokenUrl = DEFAULT_TOKEN_ENDPOINT;
		}
		if (cfg.getTokenExpirationLeeway() != null && cfg.getTokenExpirationLeeway() > 0) {
			this.tokenLeewayInSeconds = cfg.getTokenExpirationLeeway();
		}
	}

	@Override
	public Request authenticate(Route route, @NotNull Response response) throws IOException {
		if (response.request().header("Authorization") != null) {
			return null; // Give up, we've already attempted to authenticate.
		}
		String accessToken;
		try {
			accessToken = getAccessToken();
		} catch (ApiException | InvalidKeySpecException e) {
			throw new AuthenticationException("Failed to obtain access token", e);
		}

		// Return a new request with the refreshed token
		return response.request()
				.newBuilder()
				.header("Authorization", "Bearer " + accessToken)
				.build();
	}

	protected static class KeyFlowTokenResponse {
		@SerializedName("access_token")
		private final String accessToken;

		@SerializedName("refresh_token")
		private final String refreshToken;

		@SerializedName("expires_in")
		private long expiresIn;

		@SerializedName("scope")
		private final String scope;

		@SerializedName("token_type")
		private final String tokenType;

		public KeyFlowTokenResponse(
				String accessToken,
				String refreshToken,
				long expiresIn,
				String scope,
				String tokenType) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.expiresIn = expiresIn;
			this.scope = scope;
			this.tokenType = tokenType;
		}

		protected boolean isExpired() {
			return expiresIn < new Date().toInstant().getEpochSecond();
		}

		protected String getAccessToken() {
			return accessToken;
		}
	}

	/**
	 * Returns access token. If the token is expired it creates a new token.
	 *
	 * @throws InvalidKeySpecException thrown when the private key in the service account can not be
	 *     parsed
	 * @throws IOException request for new access token failed
	 * @throws ApiException response for new access token with bad status code
	 */
	@SuppressWarnings("PMD.AvoidSynchronizedStatement")
	public String getAccessToken() throws IOException, ApiException, InvalidKeySpecException {
		synchronized (tokenRefreshMonitor) {
			if (token == null) {
				createAccessToken();
			} else if (token.isExpired()) {
				createAccessTokenWithRefreshToken();
			}
			return token.getAccessToken();
		}
	}

	/**
	 * Creates the initial accessToken and stores it in `this.token`
	 *
	 * @throws InvalidKeySpecException can not parse private key
	 * @throws IOException request for access token failed
	 * @throws ApiException response for new access token with bad status code
	 * @throws JsonSyntaxException parsing of the created access token failed
	 */
	@SuppressWarnings("PMD.AvoidSynchronizedStatement")
	protected void createAccessToken() throws InvalidKeySpecException, IOException, ApiException {
		synchronized (tokenRefreshMonitor) {
			String assertion;
			try {
				assertion = generateSelfSignedJWT();
			} catch (NoSuchAlgorithmException e) {
				throw new AuthenticationException(
						"could not find required algorithm for jwt signing. This should not happen and should be reported on https://github.com/stackitcloud/stackit-sdk-java/issues",
						e);
			}

			String grant = "urn:ietf:params:oauth:grant-type:jwt-bearer";
			try (Response response = requestToken(grant, assertion).execute()) {
				parseTokenResponse(response);
			}
		}
	}

	/**
	 * Creates a new access token with the existing refresh token
	 *
	 * @throws IOException request for new access token failed
	 * @throws ApiException response for new access token with bad status code
	 * @throws JsonSyntaxException can not parse new access token
	 */
	@SuppressWarnings("PMD.AvoidSynchronizedStatement")
	protected void createAccessTokenWithRefreshToken() throws IOException, ApiException {
		synchronized (tokenRefreshMonitor) {
			String refreshToken = token.refreshToken;
			try (Response response = requestToken(REFRESH_TOKEN, refreshToken).execute()) {
				parseTokenResponse(response);
			}
		}
	}

	/**
	 * Parses the token response from the server
	 *
	 * @param response HTTP response containing the token
	 * @throws ApiException if the response has a bad status code
	 * @throws JsonSyntaxException if the response body cannot be parsed
	 */
	private void parseTokenResponse(Response response) throws ApiException {
		if (response.code() != HttpURLConnection.HTTP_OK) {
			String body = null;
			if (response.body() != null) {
				body = response.body().toString();
				response.body().close();
			}
			throw new ApiException(
					response.message(), response.code(), response.headers().toMultimap(), body);
		}
		if (response.body() == null || response.body().contentLength() == 0) {
			throw new JsonSyntaxException("body from token creation is null");
		}

		KeyFlowTokenResponse keyFlowTokenResponse =
				gson.fromJson(
						new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8),
						KeyFlowTokenResponse.class);
		setToken(keyFlowTokenResponse);
		response.body().close();
	}

	private Call requestToken(String grant, String assertionValue) {
		FormBody.Builder bodyBuilder = new FormBody.Builder();
		bodyBuilder.addEncoded("grant_type", grant);
		String assertionKey = REFRESH_TOKEN.equals(grant) ? REFRESH_TOKEN : ASSERTION;
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

	protected void setToken(KeyFlowTokenResponse response) {
		token = response;
		token.expiresIn =
				JWT.decode(response.accessToken)
						.getExpiresAt()
						.toInstant()
						.minusSeconds(tokenLeewayInSeconds)
						.getEpochSecond();
	}

	private String generateSelfSignedJWT()
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		RSAPrivateKey prvKey;

		prvKey = saKey.getCredentials().getPrivateKeyParsed();
		Algorithm algorithm = Algorithm.RSA512(prvKey);

		Map<String, Object> jwtHeader = new ConcurrentHashMap<>();
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
