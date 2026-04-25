package de.minetrain.minechat.twitch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TwitchHelperTest {

	@ParameterizedTest
	@MethodSource("provideNamesAndExpectedMatches")
	void testGenerateNameRegex(String name, String testString, boolean shouldMatch) {
		Pattern pattern = Pattern.compile(TwitchHelper.generateNameRegex(name));
		boolean matches = pattern.matcher(testString).matches(); // Changed from .find() to .matches()

		assertEquals(shouldMatch, matches,
				String.format("Pattern for '%s' should %s match '%s'", name, shouldMatch ? "" : "not", testString));
	}

	private static Stream<Arguments> provideNamesAndExpectedMatches() {
		return Stream.of(
			// Exakte Übereinstimmungen (Vollstring-Match)
			Arguments.of("TestUser", "TestUser", true),
			Arguments.of("test_user", "test_user", true),
			Arguments.of("User123", "User123", true),
			Arguments.of("User123", "User", true), // Name-Teil alleine sollte matchen

			// Mit Unterstrichen
			Arguments.of("test_user", "test", true), // Basis-Name
			Arguments.of("test_user", "test_user", true), // Vollständiger Name

			// Mit Zahlen
			Arguments.of("user123", "user", true), // Name ohne Suffix
			Arguments.of("user123", "user123", true), // Vollständiger Name
			Arguments.of("User123", "User", true), // Name-Teil
			Arguments.of("User123", "User123", true), // Vollständiger Name

			// Mit Prefix (z.B. Unterstrich am Anfang)
			Arguments.of("_Boomy0", "_Boomy0", true), // Vollständig
			Arguments.of("_Boomy0", "_Boomy", true), // Ohne Suffix
			Arguments.of("_Boomy0", "Boomy", true), // Nur Basis-Name
			Arguments.of("_Boomy0", "Boomy0", true), // Basis mit Suffix

			// Gemischt
			Arguments.of("Test_User_123", "Test", true), // Nur erster Teil
			Arguments.of("Test_User_123", "Test_User_123", true), // Vollständig

			// Keine Übereinstimmungen (wegen ^$ anchors)
			Arguments.of("TestUser", "TestUser123", false), // Zusätzliche Zeichen
			Arguments.of("TestUser", "MyTestUser", false), // Prefix nicht im Pattern
			Arguments.of("User", "UserTest", false), // Suffix nicht im Pattern
			Arguments.of("User", "TestUser", false), // Prefix nicht im Pattern
			Arguments.of("TestUser", "testuser", false), // Case-sensitive
			Arguments.of("TestUser", "TESTUSER", false), // Case-sensitive
			Arguments.of("TestUser", "@TestUser", false), // @ nicht im Pattern
			Arguments.of("TestUser", "Hello TestUser", false), // Nicht Vollstring

			// Leere Strings
			Arguments.of("", "", true), // Leerer String matcht sich selbst
			Arguments.of("", "test", false) // Leerer Name matcht nicht "test"
		);
	}

	@ParameterizedTest
	@MethodSource("provideSpecialCharacterNames")
	void testGenerateNameRegexWithSpecialCharacters(String name, String testString, boolean shouldMatch) {
		Pattern pattern = Pattern.compile(TwitchHelper.generateNameRegex(name));
		boolean matches = pattern.matcher(testString).matches(); // Changed from .find() to .matches()
		assertEquals(shouldMatch, matches,
				String.format("Pattern for '%s' should %s match '%s'", name, shouldMatch ? "" : "not", testString));
	}

	private static Stream<Arguments> provideSpecialCharacterNames() {
		return Stream.of(
			// Unterstriche
			Arguments.of("test_user", "test_user", true),
			Arguments.of("test_user", "test", true),
			Arguments.of("test_user", "TEST_USER", false), // Case-sensitive
			Arguments.of("test_user", "@test_user", false), // @ nicht im Pattern

			// Zahlen
			Arguments.of("user123", "user123", true),
			Arguments.of("user123", "user", true),
			Arguments.of("123user", "123user", true),
			Arguments.of("123user", "user", true), // Basis-Name

			// Gemischt
			Arguments.of("Test_User_123", "Test_User_123", true),
			Arguments.of("Test_User_123", "Test", true),
			Arguments.of("Test_User_123", "TEST_USER_123", false)); // Case-sensitive
	}
}