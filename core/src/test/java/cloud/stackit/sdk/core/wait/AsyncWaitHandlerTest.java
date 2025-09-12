package cloud.stackit.sdk.core.wait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.wait.AsyncActionHandler.AsyncActionResult;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AsyncWaitHandlerTest {

	// Helper class for testing
	public static class ApiHelper {

		private final String response = "APIResponse";

		public ApiHelper() {}

		public String callApi() throws ApiException {
			return response;
		}
	}

	@Mock private ApiHelper apiClient;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// testWaitHandler just calls the ApiHelper function
	public static AsyncActionHandler<Void> testWaitHandler(ApiHelper apiClient) {
		Callable<AsyncActionResult<Void>> checkFn =
				() -> {
					try {
						apiClient.callApi();
						return new AsyncActionResult<>(false, null, null);
					} catch (Exception e) {
						return new AsyncActionResult<>(false, null, e);
					}
				};
		return new AsyncActionHandler<>(checkFn);
	}

	// Non GenericOpenAPIError
	@Test
	void testNonGenericOpenAPIError() throws Exception {
		Exception nonApiException = new ArrayIndexOutOfBoundsException();
		when(apiClient.callApi()).thenThrow(nonApiException);

		AsyncActionHandler<Void> handler = testWaitHandler(apiClient);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(40, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown = assertThrows(Exception.class, handler::waitWithContext, "");
		assertEquals(thrown.getMessage(), handler.NonGenericAPIErrorMessage);
	}

	// GenericOpenAPIError(ApiException) not in RetryHttpErrorStatusCodes
	@Test
	void testOpenAPIErrorNotInList() throws Exception {
		ApiException apiException = new ApiException(409, "");
		when(apiClient.callApi()).thenThrow(apiException);

		AsyncActionHandler<Void> handler = testWaitHandler(apiClient);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(40, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown = assertThrows(Exception.class, handler::waitWithContext, "");
		assertEquals(thrown.getMessage(), handler.TimoutErrorMessage);
	}

	// GenericOpenAPIError(ApiException) in RetryHttpErrorStatusCodes -> max retries reached
	@Test
	void testOpenAPIErrorTimeoutBadGateway() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_BAD_GATEWAY, "");
		when(apiClient.callApi()).thenThrow(apiException);

		AsyncActionHandler<Void> handler = testWaitHandler(apiClient);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(Exception.class, handler::waitWithContext, apiException.getMessage());
		assertEquals(thrown.getMessage(), handler.TemporaryErrorMessage);
	}

	// GenericOpenAPIError(ApiException) in RetryHttpErrorStatusCodes -> max retries reached
	@Test
	void testOpenAPIErrorTimeoutGatewayTimeout() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "");
		when(apiClient.callApi()).thenThrow(apiException);

		AsyncActionHandler<Void> handler = testWaitHandler(apiClient);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(Exception.class, handler::waitWithContext, apiException.getMessage());
		assertEquals(thrown.getMessage(), handler.TemporaryErrorMessage);
	}
}
