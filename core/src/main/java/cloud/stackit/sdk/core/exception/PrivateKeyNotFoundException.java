package cloud.stackit.sdk.core.exception;

public class PrivateKeyNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -81419539524374575L;

	public PrivateKeyNotFoundException(String msg) {
		super(msg);
	}

	public PrivateKeyNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
