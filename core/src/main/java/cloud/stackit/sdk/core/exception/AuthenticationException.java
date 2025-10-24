package cloud.stackit.sdk.core.exception;

/**
 * Exception thrown when authentication operations fail. This includes token generation, refresh,
 * and validation failures.
 */
public class AuthenticationException extends RuntimeException {
	private static final long serialVersionUID = -7728708330906023941L;

	/**
	 * Constructs a new AuthenticationException with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public AuthenticationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new AuthenticationException with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause the cause of this exception
	 */
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new AuthenticationException with the specified cause.
	 *
	 * @param cause the cause of this exception
	 */
	public AuthenticationException(Throwable cause) {
		super(cause);
	}
}
