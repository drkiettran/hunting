package com.ops.hunting.common.util;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StringUtilsTest {

	@Test
	@DisplayName("Should check if string is empty or not empty")
	void shouldCheckIfStringIsEmptyOrNotEmpty() {
		assertTrue(StringUtils.isEmpty(null));
		assertTrue(StringUtils.isEmpty(""));
		assertTrue(StringUtils.isEmpty("   "));

		assertFalse(StringUtils.isEmpty("test"));
		assertFalse(StringUtils.isEmpty("  test  "));

		assertFalse(StringUtils.isNotEmpty(null));
		assertFalse(StringUtils.isNotEmpty(""));
		assertFalse(StringUtils.isNotEmpty("   "));

		assertTrue(StringUtils.isNotEmpty("test"));
		assertTrue(StringUtils.isNotEmpty("  test  "));
	}

	@Test
	@DisplayName("Should safely compare strings")
	void shouldSafelyCompareStrings() {
		assertTrue(StringUtils.equals(null, null));
		assertTrue(StringUtils.equals("test", "test"));

		assertFalse(StringUtils.equals(null, "test"));
		assertFalse(StringUtils.equals("test", null));
		assertFalse(StringUtils.equals("test", "different"));

		// Case insensitive comparison
		assertTrue(StringUtils.equalsIgnoreCase("Test", "TEST"));
		assertTrue(StringUtils.equalsIgnoreCase("test", "Test"));
		assertTrue(StringUtils.equalsIgnoreCase(null, null));

		assertFalse(StringUtils.equalsIgnoreCase(null, "test"));
		assertFalse(StringUtils.equalsIgnoreCase("test", null));
	}

	@Test
	@DisplayName("Should truncate strings correctly")
	void shouldTruncateStringsCorrectly() {
		String longString = "This is a very long string that should be truncated";
		String truncated = StringUtils.truncate(longString, 20);

		assertEquals(20, truncated.length());
		assertTrue(truncated.endsWith("..."));
		assertTrue(truncated.startsWith("This is a very l"));

		String shortString = "Short";
		String notTruncated = StringUtils.truncate(shortString, 20);
		assertEquals("Short", notTruncated);

		assertNull(StringUtils.truncate(null, 20));
	}

	@Test
	@DisplayName("Should convert between camelCase and snake_case")
	void shouldConvertBetweenCamelCaseAndSnakeCase() {
		assertEquals("camel_case", StringUtils.camelToSnake("camelCase"));
		assertEquals("my_variable_name", StringUtils.camelToSnake("myVariableName"));
		assertEquals("simple", StringUtils.camelToSnake("simple"));

		assertEquals("camelCase", StringUtils.snakeToCamel("camel_case"));
		assertEquals("myVariableName", StringUtils.snakeToCamel("my_variable_name"));
		assertEquals("simple", StringUtils.snakeToCamel("simple"));

		// Handle null and empty
		assertNull(StringUtils.camelToSnake(null));
		assertEquals("", StringUtils.camelToSnake(""));
		assertNull(StringUtils.snakeToCamel(null));
		assertEquals("", StringUtils.snakeToCamel(""));
	}

	@Test
	@DisplayName("Should generate random strings")
	void shouldGenerateRandomStrings() {
		String random1 = StringUtils.generateRandomString(10);
		String random2 = StringUtils.generateRandomString(10);

		assertNotNull(random1);
		assertNotNull(random2);
		assertEquals(10, random1.length());
		assertEquals(10, random2.length());
		assertNotEquals(random1, random2);

		// Test different lengths
		String shortRandom = StringUtils.generateRandomString(5);
		String longRandom = StringUtils.generateRandomString(50);

		assertEquals(5, shortRandom.length());
		assertEquals(50, longRandom.length());
	}
}
