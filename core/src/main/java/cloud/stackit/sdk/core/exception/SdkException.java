package cloud.stackit.sdk.core.exception;

/**
 * Exception thrown when SDK operations fail.
 * This includes API calls, network issues, and other SDK-related failures.
 */
public class SdkException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new SdkException with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public SdkException(String message) {
		super(message);
	}

	/**
	 * Constructs a new SdkException with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause the cause of this exception
	 */
	public SdkException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new SdkException with the specified cause.
	 *
	 * @param cause the cause of this exception
	 */
	public SdkException(Throwable cause) {
		super(cause);
	}
}
