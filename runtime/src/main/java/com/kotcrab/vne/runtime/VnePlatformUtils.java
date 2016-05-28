package com.kotcrab.vne.runtime;

/** @author Kotcrab */
public class VnePlatformUtils {
	private static final String OS_VERSION = System.getProperty("os.version");
	private static final String OS = System.getProperty("os.name").toLowerCase();

	private static final boolean WINDOWS = OS.contains("win");
	private static final boolean MAC = OS.contains("mac");
	private static final boolean UNIX = OS.contains("nix") || OS.contains("nux") || OS.contains("aix");

	public static boolean isWindowsVistaOrLater () {
		if (isWindows() == false) return false;

		try {
			return Float.parseFloat(OS_VERSION) >= 6.0f; //6.0f is vista release version number
		} catch (Exception e) {
			return false;
		}
	}

	/** @return {@code true} if the current OS is Windows */
	public static boolean isWindows () {
		return WINDOWS;
	}

	/** @return {@code true} if the current OS is Mac */
	public static boolean isMac () {
		return MAC;
	}

	/** @return {@code true} if the current OS is Unix */
	public static boolean isUnix () {
		return UNIX;
	}
}
