package cloud.stackit.sdk.resourcemanager.wait;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.wait.AsyncActionHandler;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.GetProjectResponse;
import cloud.stackit.sdk.resourcemanager.model.LifecycleState;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @SuppressWarnings is used here to suppress the PMD.TooManyMethods warning because this class is
 * an intentional testing class with many tests.
 */
@SuppressWarnings("PMD.TooManyMethods")
class ResourcemanagerWaitTest {

	@Mock private ResourceManagerApi apiClient;

	private static final String CONTAINER_ID = "MY_TEST_CONTAINER";
	private static final int SECOND_CALL = 1;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateProjectSuccess() throws Exception {
		// First call returns "CREATING", second call returns "ACTIVE"
		GetProjectResponse creatingResponse = new GetProjectResponse();
		creatingResponse.setContainerId(CONTAINER_ID);
		creatingResponse.setLifecycleState(LifecycleState.CREATING);

		GetProjectResponse activeResponse = new GetProjectResponse();
		activeResponse.setContainerId(CONTAINER_ID);
		activeResponse.setLifecycleState(LifecycleState.ACTIVE);

		AtomicInteger callCount = new AtomicInteger(0);
		when(apiClient.getProject(CONTAINER_ID, false))
				.thenAnswer(
						invocation -> {
							if (callCount.getAndIncrement() < SECOND_CALL) {
								return creatingResponse;
							}
							return activeResponse;
						});

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);

		GetProjectResponse result = handler.waitWithContextAsync().get();

		assertNotNull(result);
		verify(apiClient, times(2)).getProject(CONTAINER_ID, false);
	}

	@Test
	void testCreateProjectTimeout() throws Exception {
		// Always return "CREATING" to trigger the timeout
		GetProjectResponse creatingResponse = new GetProjectResponse();
		creatingResponse.setContainerId(CONTAINER_ID);
		creatingResponse.setLifecycleState(LifecycleState.CREATING);
		when(apiClient.getProject(CONTAINER_ID, false)).thenReturn(creatingResponse);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(500, TimeUnit.MILLISECONDS);

		Exception thrown =
				assertThrows(Exception.class, () -> handler.waitWithContextAsync().get(), "");
		assertTrue(thrown.getMessage().contains(AsyncActionHandler.TIMEOUT_ERROR_MESSAGE));
	}

	// GenericOpenAPIError not in RetryHttpErrorStatusCodes
	@Test
	void testCreateProjectOpenAPIError() throws Exception {
		// Trigger API Exception which is not in RetryHttpErrorStatusCodes
		ApiException apiException = new ApiException(409, "");
		when(apiClient.getProject(CONTAINER_ID, false)).thenThrow(apiException);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains(AsyncActionHandler.TIMEOUT_ERROR_MESSAGE));
	}

	@Test
	void testUpdateProjectSuccess() throws Exception {
		// First call returns "CREATING", second call returns "ACTIVE"
		GetProjectResponse updateResponse = new GetProjectResponse();
		updateResponse.setContainerId(CONTAINER_ID);
		updateResponse.setLifecycleState(LifecycleState.CREATING);

		GetProjectResponse activeResponse = new GetProjectResponse();
		activeResponse.setContainerId(CONTAINER_ID);
		activeResponse.setLifecycleState(LifecycleState.ACTIVE);

		AtomicInteger callCount = new AtomicInteger(0);
		when(apiClient.getProject(CONTAINER_ID, false))
				.thenAnswer(
						invocation -> {
							if (callCount.getAndIncrement() < SECOND_CALL) {
								return updateResponse;
							}
							return activeResponse;
						});

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.updateProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);

		GetProjectResponse result = handler.waitWithContextAsync().get();

		assertNotNull(result);
		verify(apiClient, times(2)).getProject(CONTAINER_ID, false);
	}

	@Test
	void testUpdateProjectTimeout() throws Exception {
		// Always return "CREATING" to trigger the timeout
		GetProjectResponse updateResponse = new GetProjectResponse();
		updateResponse.setContainerId(CONTAINER_ID);
		updateResponse.setLifecycleState(LifecycleState.CREATING);
		when(apiClient.getProject(CONTAINER_ID, false)).thenReturn(updateResponse);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(500, TimeUnit.MILLISECONDS);

		Exception thrown =
				assertThrows(Exception.class, () -> handler.waitWithContextAsync().get(), "");
		assertTrue(thrown.getMessage().contains(AsyncActionHandler.TIMEOUT_ERROR_MESSAGE));
	}

	// GenericOpenAPIError in RetryHttpErrorStatusCodes -> max retries reached
	@Test
	void testOpenAPIErrorTimeoutBadGateway() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_BAD_GATEWAY, "");
		when(apiClient.getProject(CONTAINER_ID, false)).thenThrow(apiException);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, CONTAINER_ID);
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

	// GenericOpenAPIError in RetryHttpErrorStatusCodes -> max retries reached
	@Test
	void testOpenAPIErrorTimeoutGatewayTimeout() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "");
		when(apiClient.getProject(CONTAINER_ID, false)).thenThrow(apiException);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, CONTAINER_ID);
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

	@Test
	void testDeleteProjectSuccessDeleting() throws Exception {
		// First call returns "ACTIVE", second call returns "DELETING"
		GetProjectResponse activeResponse = new GetProjectResponse();
		activeResponse.setContainerId(CONTAINER_ID);
		activeResponse.setLifecycleState(LifecycleState.ACTIVE);

		GetProjectResponse deletingResponse = new GetProjectResponse();
		deletingResponse.setContainerId(CONTAINER_ID);
		deletingResponse.setLifecycleState(LifecycleState.DELETING);

		AtomicInteger callCount = new AtomicInteger(0);
		when(apiClient.getProject(CONTAINER_ID, false))
				.thenAnswer(
						invocation -> {
							if (callCount.getAndIncrement() < SECOND_CALL) {
								return activeResponse;
							}
							return deletingResponse;
						});

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);

		handler.waitWithContextAsync().get();
		verify(apiClient, times(2)).getProject(CONTAINER_ID, false);
	}

	@Test
	void testDeleteProjectSuccessNotFoundExc() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_NOT_FOUND, "");
		when(apiClient.getProject(CONTAINER_ID, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);
		handler.waitWithContextAsync().get();
		// Only one invocation since the project is gone (HTTP_NOT_FOUND)
		verify(apiClient, times(1)).getProject(CONTAINER_ID, false);
	}

	@Test
	void testDeleteProjectSuccessForbiddenExc() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_FORBIDDEN, "");
		when(apiClient.getProject(CONTAINER_ID, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);
		handler.waitWithContextAsync().get();
		// Only one invocation since the project is gone (HTTP_FORBIDDEN)
		verify(apiClient, times(1)).getProject(CONTAINER_ID, false);
	}

	@Test
	void testDeleteProjectDifferentErrorCode() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_ENTITY_TOO_LARGE, "");
		when(apiClient.getProject(CONTAINER_ID, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, CONTAINER_ID);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);

		Exception thrown =
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains(AsyncActionHandler.TIMEOUT_ERROR_MESSAGE));
	}

	@Test
	void testOpenAPIErrorGatewayTimeout() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "");
		when(apiClient.getProject(CONTAINER_ID, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, CONTAINER_ID);
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
