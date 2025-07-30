package cloud.stackit.sdk.core;

import cloud.stackit.sdk.core.model.ServiceAccountKey;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KeyFlowAuthenticator {
    private final String REFRESH_TOKEN = "refresh_token";
    private final String ASSERTION = "assertion";

    private final OkHttpClient httpClient;
    private final ServiceAccountKey saKey;
    private KeyFlowTokenResponse token;
    private final Gson gson;
    private final String tokenUrl;

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
            return expiresIn < new Date().toInstant().minusSeconds(60).getEpochSecond();
        }

        public String getAccessToken() {
            return accessToken;
        }
    }

    public KeyFlowAuthenticator(ServiceAccountKey saKey) {
        this.saKey = saKey;
        this.gson = new Gson();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.tokenUrl = "https://service-account.api.stackit.cloud/token";
        createAccessToken();
    }

    public synchronized String getAccessToken() throws IOException {
        if (token == null || token.isExpired()) {
            createAccessTokenWithRefreshToken();
        }
        return token.getAccessToken();
    }

    private void createAccessToken() {
        String grant = "urn:ietf:params:oauth:grant-type:jwt-bearer";
        String assertion = generateSelfSignedJWT();
        try(Response response = requestToken(grant, assertion).execute()) {
            parseTokenResponse(response);
        } catch (IOException | ApiException e) {
            e.printStackTrace();
        }
    }

    private synchronized void createAccessTokenWithRefreshToken() throws IOException {
        String refreshToken = token.refreshToken;
        try (Response response = requestToken(REFRESH_TOKEN, refreshToken).execute()) {
            parseTokenResponse(response);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private synchronized void parseTokenResponse(Response response) throws ApiException {
        if (response.code() != HttpURLConnection.HTTP_OK) {
            String body = null;
            if (response.body() != null) {
                body = response.body().toString();
                response.body().close();
            }
            throw new ApiException(response.message(), response.code(), response.headers().toMultimap(), body);
        }
        if (response.body() == null) {
            throw new ApiException("body from token creation is null");
        }

        token = gson.fromJson(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8), KeyFlowTokenResponse.class);
        token.expiresIn = JWT.decode(token.accessToken).getExpiresAt().toInstant().getEpochSecond();
        response.body().close();
    }

    private Call requestToken(String grant, String assertion) throws IOException {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.addEncoded("grant_type", grant);
        if (grant.equals(REFRESH_TOKEN)) {
            bodyBuilder.addEncoded(REFRESH_TOKEN, assertion);
        } else {
            bodyBuilder.addEncoded(ASSERTION, assertion);
        }
        FormBody body = bodyBuilder.build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        return httpClient.newCall(request);
    }

    private String generateSelfSignedJWT() {
        RSAPrivateKey prvKey = saKey.getCredentials().getPrivateKeyParsed();
        Algorithm algorithm = null;
        try {
            algorithm  = Algorithm.RSA512(prvKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", saKey.getCredentials().getKid());

        return JWT.create()
                .withIssuer(saKey.getCredentials().getIss())
                .withSubject(saKey.getCredentials().getSub().toString())
                .withJWTId(UUID.randomUUID().toString())
                .withAudience(saKey.getCredentials().getAud())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date().toInstant().plusSeconds(10 * 60))
                .withHeader(jwtHeader)
                .sign(algorithm);
    }
}
