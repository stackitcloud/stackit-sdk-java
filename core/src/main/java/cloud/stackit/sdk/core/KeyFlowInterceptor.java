package cloud.stackit.sdk.core;

import cloud.stackit.sdk.core.exception.ApiException;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@Deprecated
/*
 * @deprecated use KeyFlowAuthenticator instead. Will be removed in April 2026.
 * */
public class KeyFlowInterceptor implements Interceptor {
	private final KeyFlowAuthenticator authenticator;

	@Deprecated
	/*
	 * @deprecated use KeyFlowAuthenticator instead. Will be removed in April 2026.
	 * */
	public KeyFlowInterceptor(KeyFlowAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	@NotNull @Override
	@Deprecated
	/*
	 * @deprecated use KeyFlowAuthenticator instead. Will be removed in April 2026.
	 * */
	public Response intercept(Chain chain) throws IOException {

		Request originalRequest = chain.request();
		String accessToken;
		try {
			accessToken = authenticator.getAccessToken();
		} catch (InvalidKeySpecException | ApiException e) {
			// try-catch required, because ApiException can not be thrown in the implementation
			// of Interceptor.intercept(Chain chain)
			throw new IllegalStateException(e);
		}

		Request authenticatedRequest =
				originalRequest
						.newBuilder()
						.header("Authorization", "Bearer " + accessToken)
						.build();
		return chain.proceed(authenticatedRequest);
	}
}
