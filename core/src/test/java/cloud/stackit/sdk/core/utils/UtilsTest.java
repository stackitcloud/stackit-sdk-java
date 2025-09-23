package cloud.stackit.sdk.core.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilsTest {

	@Test
	void isStringSetNull() {
		assertFalse(Utils.isStringSet(null));
	}

	@Test
	void isStringSetNullString() {
		String nullString = null;
		assertFalse(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSetEmptyString() {
		String nullString = "";
		assertFalse(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSetStringWithWhitespaces() {
		String nullString = "    ";
		assertFalse(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSetStringWithText() {
		String nullString = "text";
		assertTrue(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSetStringWithTextAndWhitespaces() {
		String nullString = " text ";
		assertTrue(Utils.isStringSet(nullString));
	}
}
