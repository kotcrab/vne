package com.kotcrab.vne.win.thumbnails.test;

import com.kotcrab.vne.win.thumbnails.WinThumbnailProvider;
import org.junit.Test;

/** @author Kotcrab */
public class WinThumbnailProviderTest {
	@Test
	public void testJni () throws Exception {
		WinThumbnailProvider provider = new WinThumbnailProvider();
		int[] bytes = provider.getThumbnail("L:\\test\\c.jpg", 200);
		if (bytes != null) {
			System.out.println(bytes.length);
		}
		provider.dispose();
	}
}
