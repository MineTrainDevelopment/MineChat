package de.minetrain.minechat.utils;

import java.nio.file.Path;

public final class FileUtils {

	private static final String SYSTEM_PROPERTY_USER_HOME = "user.home";

	/// Resolves the base directory for storing application data based on the operating system.
	///
	/// - Windows: `%APPDATA%\`
	/// - Linux:   `~/.config/`
	/// - macOS:   `~/Library/Application Support/`
	public static Path getOsSpecificDataDirectory() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			return Path.of(appData != null ? appData : System.getProperty(SYSTEM_PROPERTY_USER_HOME));
		} else if (os.contains("mac")) {
			return Path.of(System.getProperty(SYSTEM_PROPERTY_USER_HOME), "Library", "Application Support");
		}
		String xdg = System.getenv("XDG_CONFIG_HOME");
		return Path.of(xdg != null ? xdg : System.getProperty(SYSTEM_PROPERTY_USER_HOME) + "/.config");
	}

	private FileUtils() {
		// Private constructor to prevent instantiation
	}
}
