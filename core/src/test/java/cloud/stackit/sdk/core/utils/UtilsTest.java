package cloud.stackit.sdk.core.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilsTest {

	@Test
	void isStringSet_null_returnsFalse() {
		assertFalse(Utils.isStringSet(null));
	}

	@Test
	void isStringSet_nullString_returnsFalse() {
		String nullString = null;
		assertFalse(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSet_emptyString_returnsFalse() {
		String nullString = "";
		assertFalse(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSet_stringWithWhitespaces_returnsFalse() {
		String nullString = "    ";
		assertFalse(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSet_stringWithText_returnsTrue() {
		String nullString = "text";
		assertTrue(Utils.isStringSet(nullString));
	}

	@Test
	void isStringSet_stringWithTextAndWhitespaces_returnsTrue() {
		String nullString = " text ";
		assertTrue(Utils.isStringSet(nullString));
	}
}
