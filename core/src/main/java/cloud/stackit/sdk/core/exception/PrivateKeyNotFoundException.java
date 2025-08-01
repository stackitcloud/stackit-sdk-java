package cloud.stackit.sdk.core.exception;

public class PrivateKeyNotFoundException extends RuntimeException {

    public PrivateKeyNotFoundException(String msg) {
        super(msg);
    }

    public PrivateKeyNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
