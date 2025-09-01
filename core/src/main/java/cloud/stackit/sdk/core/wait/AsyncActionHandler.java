package cloud.stackit.sdk.core.wait;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.exception.GenericOpenAPIException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncActionHandler<T> {
	public static final Set<Integer> RETRY_HTTP_ERROR_STATUS_CODES =
			new HashSet<>(
					Arrays.asList(
							HttpURLConnection.HTTP_BAD_GATEWAY,
							HttpURLConnection.HTTP_GATEWAY_TIMEOUT));

	public static final String TEMPORARY_ERROR_MESSAGE =
			"Temporary error was found and the retry limit was reached.";
	public static final String TIMEOUT_ERROR_MESSAGE = "WaitWithContextAsync() has timed out.";
	public static final String NON_GENERIC_API_ERROR_MESSAGE = "Found non-GenericOpenAPIError.";

	private final CheckFunction<AsyncActionResult<T>> checkFn;

	private long sleepBeforeWaitMillis;
	private long throttleMillis;
	private long timeoutMillis;
	private int tempErrRetryLimit;

	// The linter is complaining about this but since we are using Java 8 the
	// possibilities are restricted.
	// @SuppressWarnings("PMD.DoNotUseThreads")
	// private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public AsyncActionHandler(CheckFunction<AsyncActionResult<T>> checkFn) {
		this.checkFn = checkFn;
		this.sleepBeforeWaitMillis = 0;
		this.throttleMillis = TimeUnit.SECONDS.toMillis(5);
		this.timeoutMillis = TimeUnit.MINUTES.toMillis(30);
		this.tempErrRetryLimit = 5;
	}

	/**
	 * SetThrottle sets the time interval between each check of the async action.
	 *
	 * @param duration
	 * @param unit
	 */
	public void setThrottle(long duration, TimeUnit unit) {
		this.throttleMillis = unit.toMillis(duration);
	}

	/**
	 * SetTimeout sets the duration for wait timeout.
	 *
	 * @param duration
	 * @param unit
	 */
	public void setTimeout(long duration, TimeUnit unit) {
		this.timeoutMillis = unit.toMillis(duration);
	}

	/**
	 * SetSleepBeforeWait sets the duration for sleep before wait.
	 *
	 * @param duration
	 * @param unit
	 */
	public void setSleepBeforeWait(long duration, TimeUnit unit) {
		this.sleepBeforeWaitMillis = unit.toMillis(duration);
	}

	/**
	 * SetTempErrRetryLimit sets the retry limit if a temporary error is found. The list of
	 * temporary errors is defined in the RetryHttpErrorStatusCodes variable.
	 *
	 * @param limit
	 */
	public void setTempErrRetryLimit(int limit) {
		this.tempErrRetryLimit = limit;
	}

	/**
	 * Runnable task which is executed periodically.
	 *
	 * @param future
	 * @param startTime
	 * @param retryTempErrorCounter
	 */
	private void executeCheckTask(
			CompletableFuture<T> future, long startTime, AtomicInteger retryTempErrorCounter) {
		if (future.isDone()) {
			return;
		}
		if (System.currentTimeMillis() - startTime >= timeoutMillis) {
			future.completeExceptionally(new TimeoutException(TIMEOUT_ERROR_MESSAGE));
		}
		try {
			AsyncActionResult<T> result = checkFn.execute();
			if (result != null && result.isFinished()) {
				future.complete(result.getResponse());
			}
		} catch (ApiException e) {
			GenericOpenAPIException oapiErr = new GenericOpenAPIException(e);
			// Some APIs may return temporary errors and the request should be retried
			if (!RETRY_HTTP_ERROR_STATUS_CODES.contains(oapiErr.getStatusCode())) {
				return;
			}
			if (retryTempErrorCounter.incrementAndGet() == tempErrRetryLimit) {
				// complete the future with corresponding exception
				future.completeExceptionally(new Exception(TEMPORARY_ERROR_MESSAGE, oapiErr));
			}
		} catch (IllegalStateException e) {
			future.completeExceptionally(e);
		}
	}

	/**
	 * WaitWithContextAsync starts the wait until there's an error or wait is done
	 *
	 * @return
	 */
	public CompletableFuture<T> waitWithContextAsync() {
		if (throttleMillis <= 0) {
			throw new IllegalArgumentException("Throttle can't be 0 or less");
		}

		CompletableFuture<T> future = new CompletableFuture<>();
		long startTime = System.currentTimeMillis();
		AtomicInteger retryTempErrorCounter = new AtomicInteger(0);

		// This runnable is called periodically.
		Runnable checkTask = () -> executeCheckTask(future, startTime, retryTempErrorCounter);

		// start the periodic execution
		ScheduledFuture<?> scheduledFuture =
				ScheduleExecutorSingleton.getInstance()
						.getScheduler()
						.scheduleAtFixedRate(
								checkTask,
								sleepBeforeWaitMillis,
								throttleMillis,
								TimeUnit.MILLISECONDS);

		// stop task when future is completed
		future.whenComplete(
				(result, error) -> {
					scheduledFuture.cancel(true);
					// scheduler.shutdown();
				});

		return future;
	}

	// Helper class to encapsulate the result of the checkFn
	public static class AsyncActionResult<T> {
		private final boolean finished;
		private final T response;

		public AsyncActionResult(boolean finished, T response) {
			this.finished = finished;
			this.response = response;
		}

		public boolean isFinished() {
			return finished;
		}

		public T getResponse() {
			return response;
		}
	}

	/**
	 * Helper function to check http status codes during deletion of a resource.
	 *
	 * @param e ApiException to check
	 * @return true if resource is gone otherwise false
	 */
	public static boolean checkResourceGoneStatusCodes(ApiException apiException) {
		GenericOpenAPIException oapiErr = new GenericOpenAPIException(apiException);
		return oapiErr.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND
				|| oapiErr.getStatusCode() == HttpURLConnection.HTTP_FORBIDDEN;
	}
}
