package cloud.stackit.sdk.resourcemanager.wait;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.oapierror.GenericOpenAPIError;
import cloud.stackit.sdk.core.wait.AsyncActionHandler;
import cloud.stackit.sdk.core.wait.AsyncActionHandler.AsyncActionResult;
import cloud.stackit.sdk.resourcemanager.api.ResourceManagerApi;
import cloud.stackit.sdk.resourcemanager.model.GetProjectResponse;
import cloud.stackit.sdk.resourcemanager.model.LifecycleState;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Wait {

	// createProjectWaitHandler will wait for project creation
	public static AsyncActionHandler<GetProjectResponse> createProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId) {
		Callable<AsyncActionResult<GetProjectResponse>> checkFn =
				() -> {
					try {
						GetProjectResponse p = apiClient.getProject(containerId, false);
						if (p.getContainerId() == null || p.getLifecycleState() == null) {
							return new AsyncActionResult<>(
									false,
									null,
									new Exception(
											"Creation failed: response invalid for container id "
													+ containerId
													+ ". Container ID or LifecycleState missing."));
						}

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
		handler.setSleepBeforeWait(1, TimeUnit.MINUTES);
		handler.setTimeout(45, TimeUnit.MINUTES);
		return handler;
	}

	// deleteProjectWaitHandler will wait for project deletion
	public static AsyncActionHandler<Void> deleteProjectWaitHandler(
			ResourceManagerApi apiClient, String containerId) {
		Callable<AsyncActionResult<Void>> checkFn =
				() -> {
					try {
						GetProjectResponse p = apiClient.getProject(containerId, false);
						if (p.getContainerId() == null || p.getLifecycleState() == null) {
							return new AsyncActionResult<>(true, null, null);
						}

						if (p.getContainerId().equals(containerId)
								&& p.getLifecycleState().equals(LifecycleState.DELETING)) {
							return new AsyncActionResult<>(true, null, null);
						}

						// The call does throw an exception for HttpURLConnection.HTTP_NOT_FOUND and
						// HttpURLConnection.HTTP_FORBIDDEN
						return new AsyncActionResult<>(false, null, null);

					} catch (ApiException e) {
						GenericOpenAPIError oapiErr = new GenericOpenAPIError(e);
						if (oapiErr.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND
								|| oapiErr.getStatusCode() == HttpURLConnection.HTTP_FORBIDDEN) {
							// Resource is gone, so deletion is complete
							return new AsyncActionResult<>(true, null, null);
						}
						return new AsyncActionResult<>(false, null, e);
					}
				};
		AsyncActionHandler<Void> handler = new AsyncActionHandler<>(checkFn);
		handler.setTimeout(15, TimeUnit.MINUTES);
		return handler;
	}
}
