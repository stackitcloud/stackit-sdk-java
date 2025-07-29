package cloud.stackit.sdk.core.keyflow;

import cloud.stackit.sdk.core.KeyFlowAuthenticator;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class KeyFlowInterceptor implements Interceptor {
    private final KeyFlowAuthenticator authenticator;

    public KeyFlowInterceptor(KeyFlowAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String accessToken = authenticator.getAccessToken();

        Request authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
        return chain.proceed(authenticatedRequest);
    }
}
