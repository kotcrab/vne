package com.kotcrab.vne.win.thumbnails;

import com.kotcrab.vne.runtime.VneSharedLibraryLoader;

/**
 * Utility class for extracting images thumbnails. Uses Windows thumbnail to achieve very high performance and memory efficiency.
 * Note that you must load native library 'WinThumbnails.dll` before using this. Call ({@link VneSharedLibraryLoader#load(String)}
 * with {@link WinThumbnailProvider#SHARED_LIBRARY_NAME}
 * @author Kotcrab
 */
public class WinThumbnailProvider {
	public static final String SHARED_LIBRARY_NAME = "WinThumbnails";
	private boolean initialized = false;

	/** Initialized required native resources. {@link #dispose()} must be called when this provider is no longer needed. */
	public void init () {
		//TODO ensure only on vista
		//TODO improve VneSharedLibraryLoader loading process
		//TODO move this to constructor

		if (initialized) throw new IllegalStateException("Cannot initialize thumbnail provider twice");
		initJni();
		initialized = true;
	}

	/**
	 * Extract thumbnails pixels from given path using WinAPI. This method is thread safe however calling this from too many
	 * threads may result in Windows's thumbnail cache service timeout and null will be returned.
	 * @param path path for the image, '/' are replaced with Windows's '\\'.
	 * @param size requested size for thumbnail. Note that the actual image dimensions may be different. Typically powers of
	 * 2 are returned. Check returned array for actual image size.
	 * @return abc First two elements of array are image width and height, remaining elements are thumbnails pixels packed
	 * in RGBA8888 format. Returns null when path is longer than 255 characters, returns null when size is greater than 1024,
	 * returns null when thumbnail extraction failed.
	 */
	public int[] getThumbnail (String path, int size) {
		path = path.replace("/", "\\");
		if (path.length() > 255) return null;
		if (size > 1024) return null;
		return getThumbnailJni(path, size);
	}

	/** Releases native resources. After calling this {@link #init()} must be called again to prepare native resources. */
	public void dispose () {
		disposeJni();
		initialized = false;
	}

	private native void initJni ();

	private native int[] getThumbnailJni (String path, int size);

	private native void disposeJni ();
}
