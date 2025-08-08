package cloud.stackit.sdk.core.exception;

public class CredentialsInFileNotFoundException extends RuntimeException {

	public CredentialsInFileNotFoundException(String msg) {
		super(msg);
	}

	public CredentialsInFileNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
