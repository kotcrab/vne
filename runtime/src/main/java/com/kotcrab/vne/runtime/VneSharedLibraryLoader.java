/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.kotcrab.vne.runtime;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;

/**
 * Loads shared libraries from a natives jar file (desktop) or arm folders (Android).
 * @author mzechner
 * @author Kotcrab
 */
public class VneSharedLibraryLoader {
	private static final VneSharedLibraryLoader INSTANCE = new VneSharedLibraryLoader();

	private static Set<String> loadedLibraries = new HashSet<String>();

	public static VneSharedLibraryLoader getInstance () {
		return INSTANCE;
	}

	/** Returns a CRC of the remaining bytes in the stream. */
	private String crc (InputStream input) {
		if (input == null) return "" + System.nanoTime(); // fallback
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[4096];
		try {
			while (true) {
				int length = input.read(buffer);
				if (length == -1) break;
				crc.update(buffer, 0, length);
			}
		} catch (Exception ex) {
			try {
				input.close();
			} catch (Exception ignored) {
			}
		}
		return Long.toString(crc.getValue());
	}

	private boolean loadLibrary (String sharedLibName) {
		if (sharedLibName == null) return false;

		String path = extractLibrary(sharedLibName);
		if (path != null) System.load(path);
		return path != null;
	}

	private String extractLibrary (String sharedLibName) {
		String srcCrc = crc(VneSharedLibraryLoader.class.getResourceAsStream("/" + sharedLibName));
		File nativesDir = new File(System.getProperty("java.io.tmpdir") + "/vne/" + srcCrc);
		File nativeFile = new File(nativesDir, sharedLibName);

		String extractedCrc = null;
		if (nativeFile.exists()) {
			try {
				extractedCrc = crc(new FileInputStream(nativeFile));
			} catch (FileNotFoundException ignored) {
			}
		}

		if (extractedCrc == null || !extractedCrc.equals(srcCrc)) {
			try {
				// Extract native from classpath to temp dir.
				InputStream input = VneSharedLibraryLoader.class.getResourceAsStream("/" + sharedLibName);
				if (input == null) return null;
				nativeFile.getParentFile().mkdirs();
				FileOutputStream output = new FileOutputStream(nativeFile);
				byte[] buffer = new byte[4096];
				while (true) {
					int length = input.read(buffer);
					if (length == -1) break;
					output.write(buffer, 0, length);
				}
				input.close();
				output.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		}
		return nativeFile.exists() ? nativeFile.getAbsolutePath() : null;
	}

	/**
	 * Loads a shared library with the given name for the platform the application is running on. The name should not contain a
	 * prefix (e.g. 'lib') or suffix (e.g. '.dll).
	 */
	public synchronized void load (String sharedLibName) {
		if (loadedLibraries.contains(sharedLibName)) return;

		boolean isWindows = System.getProperty("os.name").contains("Windows");
		boolean isLinux = System.getProperty("os.name").contains("Linux");
		boolean isMac = System.getProperty("os.name").contains("Mac");
		boolean isAndroid = false;
		boolean is64Bit = System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64");
		boolean isArm = System.getProperty("os.arch").equals("arm");

		String vm = System.getProperty("java.vm.name");
		if (vm != null && vm.contains("Dalvik")) {
			isAndroid = true;
			isWindows = false;
			isLinux = false;
			isMac = false;
			is64Bit = false;
		}

		boolean loaded = false;
		if (isWindows) {
			if (!is64Bit)
				loaded = loadLibrary("win32/" + sharedLibName + ".dll");
			else
				loaded = loadLibrary("win64/" + sharedLibName + ".dll");
		}
		if (isLinux) {
			throw new UnsupportedOperationException();

//			if (!is64Bit) {
//				if (isArm)
//					loaded = loadLibrary("lib" + sharedLibName + "Arm.so");
//				else
//					loaded = loadLibrary("lib" + sharedLibName + ".so");
//			} else {
//				if (isArm)
//					loaded = loadLibrary("lib" + sharedLibName + "Arm64.so");
//				else
//					loaded = loadLibrary("lib" + sharedLibName + "64.so");
//			}
		}
		if (isMac) {
			throw new UnsupportedOperationException();
//			if (!is64Bit)
//				loaded = loadLibrary("lib" + sharedLibName + ".dylib");
//			else
//				loaded = loadLibrary("lib" + sharedLibName + "64.dylib");
		}
		if (isAndroid) {
			throw new UnsupportedOperationException();
//			System.loadLibrary(sharedLibName);
//			loaded = true;
		}
		if (loaded) loadedLibraries.add(sharedLibName);
	}
}
