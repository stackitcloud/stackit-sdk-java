package cloud.stackit.sdk.resourcemanager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.wait.AsyncActionHandler;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.GetProjectResponse;
import cloud.stackit.sdk.resourcemanager.model.LifecycleState;
import cloud.stackit.sdk.resourcemanager.wait.ResourcemanagerWait;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResourcemanagerWaitTestmanagerWaitTest {

	@Mock private ResourceManagerApi apiClient;

	private final String containerId = "my-test-container";

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateProjectSuccess() throws Exception {
		// First call returns "CREATING", second call returns "ACTIVE"
		GetProjectResponse creatingResponse = new GetProjectResponse();
		creatingResponse.setContainerId(containerId);
		creatingResponse.setLifecycleState(LifecycleState.CREATING);

		GetProjectResponse activeResponse = new GetProjectResponse();
		activeResponse.setContainerId(containerId);
		activeResponse.setLifecycleState(LifecycleState.ACTIVE);

		AtomicInteger callCount = new AtomicInteger(0);
		when(apiClient.getProject(containerId, false))
				.thenAnswer(
						invocation -> {
							if (callCount.getAndIncrement() < 1) {
								return creatingResponse;
							}
							return activeResponse;
						});

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);

		GetProjectResponse result = handler.waitWithContextAsync().get();

		assertNotNull(result);
		verify(apiClient, times(2)).getProject(containerId, false);
	}

	@Test
	void testCreateProjectTimeout() throws Exception {
		// Always return "CREATING" to trigger the timeout
		GetProjectResponse creatingResponse = new GetProjectResponse();
		creatingResponse.setContainerId(containerId);
		creatingResponse.setLifecycleState(LifecycleState.CREATING);
		when(apiClient.getProject(containerId, false)).thenReturn(creatingResponse);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(500, TimeUnit.MILLISECONDS);

		Exception thrown =
				assertThrows(Exception.class, () -> handler.waitWithContextAsync().get(), "");
		assertTrue(thrown.getMessage().contains("Timeout occurred"));
	}

	// GenericOpenAPIError not in RetryHttpErrorStatusCodes
	@Test
	void testCreateProjectOpenAPIError() throws Exception {
		// Trigger API Exception which is not in RetryHttpErrorStatusCodes
		ApiException apiException = new ApiException(409, "");
		when(apiClient.getProject(containerId, false)).thenThrow(apiException);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains("Timeout occurred"));
	}

	// GenericOpenAPIError in RetryHttpErrorStatusCodes -> max retries reached
	@Test
	void testOpenAPIErrorTimeoutBadGateway() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_BAD_GATEWAY, "");
		when(apiClient.getProject(containerId, false)).thenThrow(apiException);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains(handler.TemporaryErrorMessage));
	}

	// GenericOpenAPIError in RetryHttpErrorStatusCodes -> max retries reached
	@Test
	void testOpenAPIErrorTimeoutGatewayTimeout() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "");
		when(apiClient.getProject(containerId, false)).thenThrow(apiException);

		AsyncActionHandler<GetProjectResponse> handler =
				ResourcemanagerWait.createProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains(handler.TemporaryErrorMessage));
	}

	@Test
	void testDeleteProjectSuccessDeleting() throws Exception {
		// First call returns "ACTIVE", second call returns "DELETING"
		GetProjectResponse activeResponse = new GetProjectResponse();
		activeResponse.setContainerId(containerId);
		activeResponse.setLifecycleState(LifecycleState.ACTIVE);

		GetProjectResponse deletingResponse = new GetProjectResponse();
		deletingResponse.setContainerId(containerId);
		deletingResponse.setLifecycleState(LifecycleState.DELETING);

		AtomicInteger callCount = new AtomicInteger(0);
		when(apiClient.getProject(containerId, false))
				.thenAnswer(
						invocation -> {
							if (callCount.getAndIncrement() < 1) {
								return activeResponse;
							}
							return deletingResponse;
						});

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);

		handler.waitWithContextAsync().get();
		verify(apiClient, times(2)).getProject(containerId, false);
	}

	@Test
	void testDeleteProjectSuccessNotFoundExc() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_NOT_FOUND, "");
		when(apiClient.getProject(containerId, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);
		handler.waitWithContextAsync().get();
		// Only one invocation since the project is gone (HTTP_NOT_FOUND)
		verify(apiClient, times(1)).getProject(containerId, false);
	}

	@Test
	void testDeleteProjectSuccessForbiddenExc() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_FORBIDDEN, "");
		when(apiClient.getProject(containerId, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(2, TimeUnit.SECONDS);
		handler.waitWithContextAsync().get();
		// Only one invocation since the project is gone (HTTP_FORBIDDEN)
		verify(apiClient, times(1)).getProject(containerId, false);
	}

	@Test
	void testDeleteProjectDifferentErrorCode() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_ENTITY_TOO_LARGE, "");
		when(apiClient.getProject(containerId, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);

		Exception thrown =
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains("Timeout occurred"));
	}

	@Test
	void testOpenAPIErrorGatewayTimeout() throws Exception {
		// Trigger API Exception
		ApiException apiException = new ApiException(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "");
		when(apiClient.getProject(containerId, false)).thenThrow(apiException);

		AsyncActionHandler<Void> handler =
				ResourcemanagerWait.deleteProjectWaitHandler(apiClient, containerId);
		handler.setSleepBeforeWait(0, TimeUnit.SECONDS);
		handler.setThrottle(10, TimeUnit.MILLISECONDS);
		handler.setTimeout(100, TimeUnit.MILLISECONDS);
		handler.setTempErrRetryLimit(2);

		Exception thrown =
				assertThrows(
						Exception.class,
						() -> handler.waitWithContextAsync().get(),
						apiException.getMessage());
		assertTrue(thrown.getMessage().contains(handler.TemporaryErrorMessage));
	}
}
