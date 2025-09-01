package cloud.stackit.sdk.core.exception;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GenericOpenAPIException extends ApiException {
	// Created with serialver
	private static final long serialVersionUID = 3551449573139480120L;
	// When a response has a bad status, this limits the number of characters that are shown from
	// the response Body
	public int apiErrorMaxCharacterLimit = 500;

	private final int statusCode;
	private byte[] body;
	private final String errorMessage;

	public GenericOpenAPIException(ApiException apiException) {
		super(apiException.getMessage());
		this.statusCode = apiException.getCode();
		this.errorMessage = apiException.getMessage();
	}

	public GenericOpenAPIException(int statusCode, String errorMessage) {
		this(statusCode, errorMessage, null);
	}

	public GenericOpenAPIException(int statusCode, String errorMessage, byte[] body) {
		super(errorMessage);
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
		if (body != null) {
			this.body = Arrays.copyOf(body, body.length);
		}
	}

	@Override
	public String getMessage() {
		// Prevent negative values
		if (apiErrorMaxCharacterLimit < 0) {
			apiErrorMaxCharacterLimit = 500;
		}

		if (body == null) {
			return String.format("%s, status code %d", errorMessage, statusCode);
		}

		String bodyStr = new String(body, StandardCharsets.UTF_8);

		if (bodyStr.length() <= apiErrorMaxCharacterLimit) {
			return String.format("%s, status code %d, Body: %s", errorMessage, statusCode, bodyStr);
		}

		int indexStart = apiErrorMaxCharacterLimit / 2;
		int indexEnd = bodyStr.length() - apiErrorMaxCharacterLimit / 2;
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
		if (body == null) {
			return new byte[0];
		}
		return Arrays.copyOf(body, body.length);
	}
}
