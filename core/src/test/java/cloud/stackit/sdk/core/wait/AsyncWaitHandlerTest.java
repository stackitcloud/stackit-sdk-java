package cloud.stackit.sdk.core.wait;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.wait.AsyncActionHandler.AsyncActionResult;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AsyncWaitHandlerTest {

	@Mock private ApiHelper apiClient;

	// Helper class for testing
	public static class ApiHelper {

		private static final String RESPONSE = "APIResponse";

		public String callApi() throws ApiException {
			return RESPONSE;
		}
	}

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// testWaitHandler just calls the ApiHelper function
	@SuppressWarnings("PMD.AvoidRethrowingException")
	private static AsyncActionHandler<Void> testWaitHandler(ApiHelper apiClient) {
		CheckFunction<AsyncActionResult<Void>> checkFn =
				() -> {
					try {
						apiClient.callApi();
						return new AsyncActionResult<>(false, null);
					} catch (ApiException | IllegalStateException e) {
						throw e;
					}
				};
		return new AsyncActionHandler<>(checkFn);
	}

	// Non GenericOpenAPIError
	@Test
	void testNonGenericOpenAPIError() throws Exception {
		Exception nonApiException = new IllegalStateException();
		when(apiClient.callApi()).thenThrow(nonApiException);

		AsyncActionHandler<Void> handler = testWaitHandler(apiClient);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(40, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(
						ExecutionException.class, () -> handler.waitWithContextAsync().get(), "");
		assertTrue(thrown.getMessage().contains("IllegalStateException"));
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

		Exception thrown =
				assertThrows(Exception.class, () -> handler.waitWithContextAsync().get(), "");
		assertTrue(thrown.getMessage().contains(AsyncActionHandler.TIMEOUT_ERROR_MESSAGE));
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
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains(AsyncActionHandler.TEMPORARY_ERROR_MESSAGE));
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
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains(AsyncActionHandler.TEMPORARY_ERROR_MESSAGE));
	}
}
