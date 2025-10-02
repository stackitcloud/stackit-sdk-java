package cloud.stackit.sdk.core.exception;

public class CredentialsInFileNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -3290974267932615412L;

	public CredentialsInFileNotFoundException(String msg) {
		super(msg);
	}

	public CredentialsInFileNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
