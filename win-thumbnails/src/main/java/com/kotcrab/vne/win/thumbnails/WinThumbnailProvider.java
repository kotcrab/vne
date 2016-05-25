package com.kotcrab.vne.win.thumbnails;

/** @author Kotcrab */
public class WinThumbnailProvider {

	public void init () {
		initJni();
	}

	public boolean getThumbnail (String path, int size) {
		if(path.length() > 255) return false;
		return getThumbnailJni(path, size);
	}

	public void dispose () {
		disposeJni();
	}

	private native void initJni ();

	private native boolean getThumbnailJni (String path, int size);

	private native void disposeJni ();
}
