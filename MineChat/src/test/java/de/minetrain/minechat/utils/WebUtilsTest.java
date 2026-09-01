package de.minetrain.minechat.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WebUtilsTest {

	@ParameterizedTest
	@MethodSource("urlProvider")
	void testExtractDomain(String testUrl, String expectedDomain) {
		String extractedDomain = WebUtils.extractDomain(testUrl);

		assertEquals(expectedDomain, extractedDomain);
	}

	static Stream<Arguments> urlProvider() {
		return Stream.of(
			Arguments.of("example.com", "example.com"),
			Arguments.of("www.example.com", "www.example.com"),
			Arguments.of("http://example.com", "example.com"),
			Arguments.of("https://example.com", "example.com"),
			Arguments.of("instagram.com/die.doni", "instagram.com"),
			Arguments.of("https://www.twitch.tv/donitv", "www.twitch.tv"),
			Arguments.of("https://subdomain.example.com/path?query=param#fragment", "subdomain.example.com"),
			Arguments.of("invalid-url", "invalid-url"),
			Arguments.of("ftp://example.com", "ftp://example.com")
		);
	}
}
