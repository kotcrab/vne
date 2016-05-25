package com.kotcrab.vne.win.thumbnails.test;

import com.kotcrab.vne.win.thumbnails.WinThumbnailProvider;
import org.junit.Test;

/** @author Kotcrab */
public class WinThumbnailProviderTest {
	@Test
	public void testJni () throws Exception {
		System.load("E:\\Git\\vne\\win-thumbnails\\src\\main\\resources\\win64\\WinThumbnails.dll");
		WinThumbnailProvider provider = new WinThumbnailProvider();
		provider.init();
		provider.getThumbnail("L:\\test\\c.jpg", 256);
		provider.dispose();
	}
}
