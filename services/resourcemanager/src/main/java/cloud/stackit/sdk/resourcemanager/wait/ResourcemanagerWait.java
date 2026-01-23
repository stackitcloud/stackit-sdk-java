package cloud.stackit.sdk.resourcemanager.wait;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.wait.AsyncActionHandler;
import cloud.stackit.sdk.core.wait.AsyncActionHandler.AsyncActionResult;
import cloud.stackit.sdk.core.wait.CheckFunction;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.GetProjectResponse;
import cloud.stackit.sdk.resourcemanager.model.LifecycleState;
import java.util.concurrent.TimeUnit;

public final class ResourcemanagerWait {

	/** Prevent instantiation */
	private ResourcemanagerWait() {}

	/**
	 * createProjectWaitHandler will wait for project creation. Uses the default values for
	 * sleepBeforeWait (1 min) and timeout (45 min).
	 *
	 * @param apiClient
	 * @param containerId
	 * @return
	 */
	public static AsyncActionHandler<GetProjectResponse> createProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId) throws ApiException {
		return createProjectWaitHandler(apiClient, containerId, 1, 45);
	}

	/**
	 * createProjectWaitHandler will wait for project creation
	 *
	 * @param apiClient
	 * @param containerId
	 * @param sleepBeforeWait in Minutes
	 * @param timeout in Minutes
	 * @return
	 */
	public static AsyncActionHandler<GetProjectResponse> createProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId, long sleepBeforeWait, long timeout)
			throws ApiException {
		return createOrUpdateProjectWaitHandler(apiClient, containerId, sleepBeforeWait, timeout);
	}

	/**
	 * updateProjectWaitHandler will wait until the project was updated. Uses the default values for
	 * sleepBeforeWait (1 min) and timeout (45 min).
	 *
	 * @param apiClient
	 * @param containerId
	 * @return
	 */
	public static AsyncActionHandler<GetProjectResponse> updateProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId) throws ApiException {
		return updateProjectWaitHandler(apiClient, containerId, 1, 45);
	}

	/**
	 * updateProjectWaitHandler will wait until the project was updated.
	 *
	 * @param apiClient
	 * @param containerId
	 * @param sleepBeforeWait in Minutes
	 * @param timeout in Minutes
	 * @return
	 */
	public static AsyncActionHandler<GetProjectResponse> updateProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId, long sleepBeforeWait, long timeout)
			throws ApiException {
		return createOrUpdateProjectWaitHandler(apiClient, containerId, sleepBeforeWait, timeout);
	}

	/**
	 * Private helper function for create and update wait handler since the logic is the same.
	 *
	 * @param apiClient
	 * @param containerId
	 * @param sleepBeforeWait
	 * @param timeout
	 * @return
	 * @throws ApiException
	 */
	private static AsyncActionHandler<GetProjectResponse> createOrUpdateProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId, long sleepBeforeWait, long timeout)
			throws ApiException {
		CheckFunction<AsyncActionResult<GetProjectResponse>> checkFn =
				() -> {
					GetProjectResponse projectResponse = apiClient.getProject(containerId, false);
					if (projectResponse.getContainerId().equals(containerId)
							&& projectResponse.getLifecycleState() == LifecycleState.ACTIVE) {
						return new AsyncActionResult<>(true, projectResponse);
					}

					if (projectResponse.getContainerId().equals(containerId)
							&& projectResponse.getLifecycleState() == LifecycleState.CREATING) {
						return new AsyncActionResult<>(false, null);
					}
					// An invalid state was received which should not be possible.
					throw new IllegalStateException(
							"Creation failed: received project state '"
									+ projectResponse.getLifecycleState().getValue()
									+ "'");
				};
		AsyncActionHandler<GetProjectResponse> handler = new AsyncActionHandler<>(checkFn);
		handler.setSleepBeforeWait(sleepBeforeWait, TimeUnit.MINUTES);
		handler.setTimeout(timeout, TimeUnit.MINUTES);
		return handler;
	}

	/**
	 * deleteProjectWaitHandler will wait for project deletion. Uses the deault value for timeout
	 * (15 min).
	 *
	 * @param apiClient
	 * @param containerId
	 * @return
	 */
	public static AsyncActionHandler<Void> deleteProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId) {
		return deleteProjectWaitHandler(apiClient, containerId, 15);
	}

	/**
	 * deleteProjectWaitHandler will wait for project deletion
	 *
	 * @param apiClient
	 * @param containerId
	 * @param timeout in minutes
	 * @return
	 */
	public static AsyncActionHandler<Void> deleteProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId, long timeout) {
		CheckFunction<AsyncActionResult<Void>> checkFn =
				() -> {
					try {
						GetProjectResponse projectResponse =
								apiClient.getProject(containerId, false);
						if (projectResponse.getContainerId().equals(containerId)
								&& projectResponse.getLifecycleState() == LifecycleState.DELETING) {
							return new AsyncActionResult<>(true, null);
						}

						// The call does throw an exception for HttpURLConnection.HTTP_NOT_FOUND and
						// HttpURLConnection.HTTP_FORBIDDEN
						return new AsyncActionResult<>(false, null);
					} catch (ApiException e) {
						if (AsyncActionHandler.checkResourceGoneStatusCodes(e)) {
							return new AsyncActionResult<>(true, null);
						}
						throw e;
					}
				};
		AsyncActionHandler<Void> handler = new AsyncActionHandler<>(checkFn);
		handler.setTimeout(timeout, TimeUnit.MINUTES);
		return handler;
	}
}
