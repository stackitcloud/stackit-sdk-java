package cloud.stackit.sdk.core.oapierror;

import cloud.stackit.sdk.core.exception.ApiException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class GenericOpenAPIException extends ApiException {

	// When a response has a bad status, this limits the number of characters that are shown from
	// the response Body
	public static int ApiErrorMaxCharacterLimit = 500;

	private final int statusCode;
	private byte[] body;
	private final String errorMessage;
	private Object model;

	public GenericOpenAPIException(ApiException e) {
		this.statusCode = e.getCode();
		this.errorMessage = e.getMessage();
	}

	public GenericOpenAPIException(int statusCode, String errorMessage) {
		this(statusCode, errorMessage, null, new HashMap<>());
	}

	public GenericOpenAPIException(int statusCode, String errorMessage, byte[] body, Object model) {
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
		this.body = body;
		this.model = model;
	}

	@Override
	public String getMessage() {
		// Prevent negative values
		if (ApiErrorMaxCharacterLimit < 0) {
			ApiErrorMaxCharacterLimit = 500;
		}

		if (body == null) {
			return String.format("%s, status code %d", errorMessage, statusCode);
		}

		String bodyStr = new String(body, StandardCharsets.UTF_8);

		if (bodyStr.length() <= ApiErrorMaxCharacterLimit) {
			return String.format("%s, status code %d, Body: %s", errorMessage, statusCode, bodyStr);
		}

		int indexStart = ApiErrorMaxCharacterLimit / 2;
		int indexEnd = bodyStr.length() - ApiErrorMaxCharacterLimit / 2;
		int numberTruncatedCharacters = indexEnd - indexStart;

		return String.format(
				"%s, status code %d, Body: %s [...truncated %d characters...] %s",
				errorMessage,
				statusCode,
				bodyStr.substring(0, indexStart),
				numberTruncatedCharacters,
				bodyStr.substring(indexEnd));
	}

	public int getStatusCode() {
		return statusCode;
	}

	public byte[] getBody() {
		return body;
	}

	public Object getModel() {
		return model;
	}
}
