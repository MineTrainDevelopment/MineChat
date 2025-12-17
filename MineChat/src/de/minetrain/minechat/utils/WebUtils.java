package de.minetrain.minechat.utils;

import java.net.URI;
import java.util.Locale;

import org.apache.commons.validator.routines.UrlValidator;

public final class WebUtils {

	private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[] { "http", "https" },
			UrlValidator.ALLOW_LOCAL_URLS);

	public static boolean isValidUrl(String input) {
		if (input == null) {
			return false;
		}
		String normalized = normalizeUrl(input);
		if (normalized == null || normalized.length() > 2048) {
			return false;
		}
		if (!URL_VALIDATOR.isValid(normalized)) {
			return false;
		}

		try {
			URI uri = URI.create(normalized);
			String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
			if (!"http".equals(scheme) && !"https".equals(scheme)) {
				return false;
			}
			if (uri.getUserInfo() != null) {
				return false;
			}
			return uri.getHost() != null;
		} catch (Exception _) {
			return false;
		}
	}

	public static String normalizeUrl(String input) {
		if (input == null) {
			return null;
		}
		if (input.regionMatches(true, 0, "http://", 0, 7) || input.regionMatches(true, 0, "https://", 0, 8)) {
			return input;
		}

		if (looksLikeDomain(input)) {
			return "https://" + input;
		}

		return null;
	}

	private static boolean looksLikeDomain(String s) {
		// Reject spaces and schemes we don't support
		if (s.contains("://")) {
			return false;
		}

		// Basic domain.tld check
		int lastDot = s.lastIndexOf('.');
		if (lastDot <= 0 || lastDot == s.length() - 1) {
			return false;
		}

		String tld = s.substring(lastDot + 1);
		if (tld.length() < 2 || !tld.chars().allMatch(Character::isLetter)) {
			return false;
		}

		// Domain labels must be alnum or '-'
		String host = s;
		int slash = s.indexOf('/');
		if (slash >= 0) {
			host = s.substring(0, slash);
		}
		String[] labels = host.split("\\.");
		if (labels.length < 2) {
			return false;
		}
		for (String label : labels) {
			if (label.isEmpty() || label.length() > 63) {
				return false;
			}
			for (int i = 0; i < label.length(); i++) {
				char c = label.charAt(i);
				if (!(Character.isLetterOrDigit(c) || c == '-')) {
					return false;
				}
			}
			if (label.startsWith("-") || label.endsWith("-")) {
				return false;
			}
		}
		return true;
	}

	private WebUtils() {
		// Private constructor to prevent instantiation
	}
}
