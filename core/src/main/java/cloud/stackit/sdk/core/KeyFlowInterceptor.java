package cloud.stackit.sdk.core;

import cloud.stackit.sdk.core.exception.ApiException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class KeyFlowInterceptor implements Interceptor {
    private final KeyFlowAuthenticator authenticator;

    public KeyFlowInterceptor(KeyFlowAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String accessToken;
        try {
            accessToken = authenticator.getAccessToken();
        } catch (ApiException e) {
            // try-catch required, because ApiException can not be thrown in the implementation
            // of Interceptor.intercept(Chain chain)
            throw new RuntimeException(e);
        }

        Request authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
        return chain.proceed(authenticatedRequest);
    }
}
