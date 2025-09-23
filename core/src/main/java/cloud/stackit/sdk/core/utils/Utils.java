package cloud.stackit.sdk.core.utils;

public final class Utils {
	private Utils() {}

	/*
	 * Assert a string is not null and not empty
	 *
	 * @param input The string to check
	 * @return check result
	 * */
	public static boolean isStringSet(final String input) {
		return input != null && !checkTrimEmpty(input);
	}

	/*
	 * Assert a string is not empty. Helper method because String.trim().length() == 0
	 * / String.trim().isEmpty() is an inefficient way to validate a blank String.
	 *
	 * @param input The string to check
	 * @return check result
	 * */
	private static boolean checkTrimEmpty(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (!Character.isWhitespace(input.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
