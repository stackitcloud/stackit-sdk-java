package cloud.stackit.sdk.core.wait;

import cloud.stackit.sdk.core.exception.ApiException;
import cloud.stackit.sdk.core.oapierror.GenericOpenAPIException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncActionHandler<T> {
	public static final Set<Integer> RetryHttpErrorStatusCodes =
			new HashSet<>(
					Arrays.asList(
							HttpURLConnection.HTTP_BAD_GATEWAY,
							HttpURLConnection.HTTP_GATEWAY_TIMEOUT));

	public final String TemporaryErrorMessage =
			"Temporary error was found and the retry limit was reached.";
	public final String TimoutErrorMessage = "WaitWithContext() has timed out.";
	public final String NonGenericAPIErrorMessage = "Found non-GenericOpenAPIError.";

	private final Callable<AsyncActionResult<T>> checkFn;

	private long sleepBeforeWaitMillis;
	private long throttleMillis;
	private long timeoutMillis;
	private int tempErrRetryLimit;

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
	 * WaitWithContext starts the wait until there's an error or wait is done
	 *
	 * @return
	 * @throws Exception
	 */
	public T waitWithContext() throws Exception {
		if (throttleMillis <= 0) {
			throw new IllegalArgumentException("Throttle can't be 0 or less");
		}

		long startTime = System.currentTimeMillis();

		// Wait some seconds for the API to process the request
		if (sleepBeforeWaitMillis > 0) {
			try {
				Thread.sleep(sleepBeforeWaitMillis);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new InterruptedException("Wait operation was interrupted before starting.");
			}
		}

		int retryTempErrorCounter = 0;
		while (System.currentTimeMillis() - startTime < timeoutMillis) {
			AsyncActionResult<T> result = checkFn.call();
			if (result.error != null) { // error present
				ErrorResult errorResult = handleException(retryTempErrorCounter, result.error);
				retryTempErrorCounter = errorResult.retryTempErrorCounter;
				if (retryTempErrorCounter == tempErrRetryLimit) {
					throw errorResult.getError();
				}
				result = null;
			}

			if (result != null && result.isFinished()) {
				return result.getResponse();
			}

			try {
				Thread.sleep(throttleMillis);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new InterruptedException("Wait operation was interrupted.");
			}
		}
		throw new TimeoutException(TimoutErrorMessage);
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
