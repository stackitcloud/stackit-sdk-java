package cloud.stackit.sdk.resourcemanager.wait;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.oapierror.GenericOpenAPIException;
import cloud.stackit.sdk.core.wait.AsyncActionHandler;
import cloud.stackit.sdk.core.wait.AsyncActionHandler.AsyncActionResult;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.GetProjectResponse;
import cloud.stackit.sdk.resourcemanager.model.LifecycleState;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ResourcemanagerWait {
	/**
	 * createProjectWaitHandler will wait for project creation. Uses the default values for
	 * sleepBeforeWait (1 min) and timeout (45 min).
	 *
	 * @param apiClient
	 * @param containerId
	 * @return
	 */
	public static AsyncActionHandler<GetProjectResponse> createProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId) {
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
			ResourceManagerApi apiClient, String containerId, long sleepBeforeWait, long timeout) {
		Callable<AsyncActionResult<GetProjectResponse>> checkFn =
				() -> {
					try {
						GetProjectResponse p = apiClient.getProject(containerId, false);
						if (p.getContainerId().equals(containerId)
								&& p.getLifecycleState().equals(LifecycleState.ACTIVE)) {
							return new AsyncActionResult<>(true, p, null);
						}

						if (p.getContainerId().equals(containerId)
								&& p.getLifecycleState().equals(LifecycleState.CREATING)) {
							return new AsyncActionResult<>(false, null, null);
						}
						return new AsyncActionResult<>(
								true,
								p,
								new Exception(
										"Creation failed: received project state '"
												+ p.getLifecycleState().getValue()
												+ "'"));
					} catch (ApiException e) {
						return new AsyncActionResult<>(false, null, e);
					}
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
		Callable<AsyncActionResult<Void>> checkFn =
				() -> {
					try {
						GetProjectResponse p = apiClient.getProject(containerId, false);

						if (p.getContainerId().equals(containerId)
								&& p.getLifecycleState().equals(LifecycleState.DELETING)) {
							return new AsyncActionResult<>(true, null, null);
						}

						// The call does throw an exception for HttpURLConnection.HTTP_NOT_FOUND and
						// HttpURLConnection.HTTP_FORBIDDEN
						return new AsyncActionResult<>(false, null, null);

					} catch (ApiException e) {
						GenericOpenAPIException oapiErr = new GenericOpenAPIException(e);
						if (oapiErr.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND
								|| oapiErr.getStatusCode() == HttpURLConnection.HTTP_FORBIDDEN) {
							// Resource is gone, so deletion is complete
							return new AsyncActionResult<>(true, null, null);
						}
						return new AsyncActionResult<>(false, null, e);
					}
				};
		AsyncActionHandler<Void> handler = new AsyncActionHandler<>(checkFn);
		handler.setTimeout(timeout, TimeUnit.MINUTES);
		return handler;
	}
}
