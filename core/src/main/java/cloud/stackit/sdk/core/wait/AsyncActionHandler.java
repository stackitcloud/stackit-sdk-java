package cloud.stackit.sdk.core.wait;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.oapierror.GenericOpenAPIException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncActionHandler<T> {
	public static final Set<Integer> RetryHttpErrorStatusCodes =
			new HashSet<>(
					Arrays.asList(
							HttpURLConnection.HTTP_BAD_GATEWAY,
							HttpURLConnection.HTTP_GATEWAY_TIMEOUT));

	public final String TemporaryErrorMessage =
			"Temporary error was found and the retry limit was reached.";
	// public final String TimoutErrorMessage = "WaitWithContext() has timed out.";
	public final String NonGenericAPIErrorMessage = "Found non-GenericOpenAPIError.";

	private final Callable<AsyncActionResult<T>> checkFn;

	private long sleepBeforeWaitMillis;
	private long throttleMillis;
	private long timeoutMillis;
	private int tempErrRetryLimit;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	// private final WaitHandler waitHandler;

	public AsyncActionHandler(Callable<AsyncActionResult<T>> checkFn) {
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
	 * @return
	 */
	public AsyncActionHandler<T> setThrottle(long duration, TimeUnit unit) {
		this.throttleMillis = unit.toMillis(duration);
		return this;
	}

	/**
	 * SetTimeout sets the duration for wait timeout.
	 *
	 * @param duration
	 * @param unit
	 * @return
	 */
	public AsyncActionHandler<T> setTimeout(long duration, TimeUnit unit) {
		this.timeoutMillis = unit.toMillis(duration);
		return this;
	}

	/**
	 * SetSleepBeforeWait sets the duration for sleep before wait.
	 *
	 * @param duration
	 * @param unit
	 * @return
	 */
	public AsyncActionHandler<T> setSleepBeforeWait(long duration, TimeUnit unit) {
		this.sleepBeforeWaitMillis = unit.toMillis(duration);
		return this;
	}

	/**
	 * SetTempErrRetryLimit sets the retry limit if a temporary error is found. The list of
	 * temporary errors is defined in the RetryHttpErrorStatusCodes variable.
	 *
	 * @param limit
	 * @return
	 */
	public AsyncActionHandler<T> setTempErrRetryLimit(int limit) {
		this.tempErrRetryLimit = limit;
		return this;
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
		Runnable checkTask =
				new Runnable() {
					@Override
					public void run() {
						if (System.currentTimeMillis() - startTime >= timeoutMillis) {
							future.completeExceptionally(new TimeoutException("Timeout occurred."));
						}

						try {
							AsyncActionResult<T> result = checkFn.call();
							if (result.error != null) {
								ErrorResult errorResult =
										handleException(retryTempErrorCounter.get(), result.error);
								retryTempErrorCounter.set(errorResult.retryTempErrorCounter);

								if (retryTempErrorCounter.get() == tempErrRetryLimit) {
									future.completeExceptionally(errorResult.getError());
								}
							}

							if (result != null && result.isFinished()) {
								future.complete(result.getResponse());
							}
						} catch (Exception e) {
							future.completeExceptionally(e);
						}
					}
				};

		// start the periodic execution
		ScheduledFuture<?> scheduledFuture =
				scheduler.scheduleAtFixedRate(
						checkTask, sleepBeforeWaitMillis, throttleMillis, TimeUnit.MILLISECONDS);

		// stop task when future is completed
		future.whenComplete(
				(result, error) -> {
					scheduledFuture.cancel(true);
				});

		return future;
	}

	private ErrorResult handleException(int retryTempErrorCounter, Exception exception) {
		if (exception instanceof ApiException) {
			ApiException apiException = (ApiException) exception;
			GenericOpenAPIException oapiErr = new GenericOpenAPIException(apiException);
			// Some APIs may return temporary errors and the request should be retried
			if (!RetryHttpErrorStatusCodes.contains(oapiErr.getStatusCode())) {
				return new ErrorResult(retryTempErrorCounter, oapiErr);
			}
			retryTempErrorCounter++;
			if (retryTempErrorCounter == tempErrRetryLimit) {
				return new ErrorResult(
						retryTempErrorCounter, new Exception(TemporaryErrorMessage, oapiErr));
			}
			return new ErrorResult(retryTempErrorCounter, null);
		} else {
			retryTempErrorCounter++;
			// If it's not a GenericOpenAPIError, handle it differently
			return new ErrorResult(
					retryTempErrorCounter, new Exception(NonGenericAPIErrorMessage, exception));
		}
	}

	// Helper class to encapsulate the result of handleError
	public static class ErrorResult {
		private final int retryTempErrorCounter;
		private final Exception error;

		public ErrorResult(int retryTempErrorCounter, Exception error) {
			this.retryTempErrorCounter = retryTempErrorCounter;
			this.error = error;
		}

		public int getRetryErrorCounter() {
			return retryTempErrorCounter;
		}

		public Exception getError() {
			return error;
		}
	}

	// Helper class to encapsulate the result of the checkFn
	public static class AsyncActionResult<T> {
		private final boolean finished;
		private final T response;
		private final Exception error;

		public AsyncActionResult(boolean finished, T response, Exception error) {
			this.finished = finished;
			this.response = response;
			this.error = error;
		}

		public boolean isFinished() {
			return finished;
		}

		public T getResponse() {
			return response;
		}

		public Exception getError() {
			return error;
		}
	}
}
