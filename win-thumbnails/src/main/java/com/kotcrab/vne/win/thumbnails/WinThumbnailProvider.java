package com.kotcrab.vne.win.thumbnails;

import com.kotcrab.vne.runtime.*;

/**
 * Utility class for extracting images thumbnails. Uses Windows thumbnail to achieve very high performance and memory efficiency.
 * By default errors messages are logged to console. Set {@link #setErrorHandler(VneErrorHandler)} to null to disable this.
 * @author Kotcrab
 */
public class WinThumbnailProvider extends VneLibrary {
	public static final String SHARED_LIBRARY_NAME = "WinThumbnails";
	private boolean initialized;

	/** Creates new thumbnail provider. {@link #dispose()} must be called when this provider is no longer needed. */
	public WinThumbnailProvider () {
		if (isPlatformSupported() == false)
			throw new IllegalStateException("This class can't be used on current OS. Check #isPlatformSupported before creating.");
		VneSharedLibraryLoader.getInstance().load(SHARED_LIBRARY_NAME);
		setErrorHandler(new VneDefaultErrorHandler("WinThumbnailProvider"));
		initJni();
		initialized = true;
	}

	public static boolean isPlatformSupported () {
		return VnePlatformUtils.isWindowsVistaOrLater();
	}

	/**
	 * Extract thumbnails pixels from given path using WinAPI. This method is thread safe however calling this from too many
	 * threads may result in Windows's thumbnail cache service timeout and null will be returned.
	 * @param path path for the image, '/' are replaced with Windows's '\\'.
	 * @param size requested size for thumbnail. Note that the actual image dimensions may be different. Typically powers of
	 * 2 are returned. Check returned array for actual image size.
	 * @return pixel array, first two elements of array are image width and height, remaining elements are thumbnails pixels packed
	 * in RGBA8888 format. Returns null when path is longer than 255 characters, returns null when size is greater than 1024,
	 * returns null when thumbnail extraction failed.
	 */
	public int[] getThumbnail (String path, int size) {
		path = path.replace("/", "\\");
		if (path.length() > 255) return null;
		if (size > 1024) return null;
		return getThumbnailJni(path, size);
	}

	/** Releases native resources. */
	public void dispose () {
		if (initialized == false) throw new IllegalStateException("Thumbnail provider was already disposed");
		disposeJni();
		initialized = false;
	}

	// Natives

	private void jniErrorCallback (String msg, long errCode) {
		handleError(msg, errCode);
	}

	private native void initJni ();

	private native int[] getThumbnailJni (String path, int size);

	private native void disposeJni ();
}
