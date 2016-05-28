package com.kotcrab.vne.win.thumbnails.test;

import com.kotcrab.vne.runtime.VneSharedLibraryLoader;
import com.kotcrab.vne.win.thumbnails.WinThumbnailProvider;
import org.junit.Test;

/** @author Kotcrab */
public class WinThumbnailProviderTest {
	@Test
	public void testJni () throws Exception {
		VneSharedLibraryLoader.newInstance().load(WinThumbnailProvider.SHARED_LIBRARY_NAME);
		WinThumbnailProvider provider = new WinThumbnailProvider();
		provider.init();
		int[] bytes = provider.getThumbnail("L:\\test\\c.jpg", 200);
		if (bytes != null) {
			System.out.println(bytes.length);
		}

		long x = 0;
		for(int i = 0; i < bytes.length; i++)
		{
			x += i;
		}
		System.out.println(x);
		provider.dispose();
	}
}
