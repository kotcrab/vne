#include <com_kotcrab_vne_win_thumbnails_WinThumbnailProvider.h>

#define _WIN32_WINNT 0x0600 //vista

#include <Windows.h>
#include <shlobj.h>
#include <shobjidl.h>
#include <thumbcache.h>

#include <iostream>
#include <assert.h>

int ctn = 0;

LPCWSTR vnePathToWstr(const char * path)
{
	int wchars_num = MultiByteToWideChar(CP_UTF8, 0, path, -1, NULL, 0);
	wchar_t* wstr = new wchar_t[wchars_num];
	MultiByteToWideChar(CP_UTF8, 0, path, -1, wstr, wchars_num);
	return wstr;
}

JNIEXPORT void JNICALL Java_com_kotcrab_vne_win_thumbnails_WinThumbnailProvider_initJni (JNIEnv * env, jobject jobj) {
	CoInitializeEx(NULL, COINIT_MULTITHREADED);
}

JNIEXPORT jboolean JNICALL Java_com_kotcrab_vne_win_thumbnails_WinThumbnailProvider_getThumbnailJni(JNIEnv * env, jobject jobj, jstring jpath, jint jsize) {
	const char *str = env->GetStringUTFChars(jpath, FALSE);
	LPCWSTR path = vnePathToWstr(str);
	env->ReleaseStringUTFChars(jpath, str);

	ctn++;
	printf("%i ", ctn);

	HRESULT hr;

	LPITEMIDLIST pidl;
	hr = SHParseDisplayName(path, NULL, &pidl, 0, NULL);
	if (FAILED(hr)) {
		printf("%ld \n", hr);
		printf("SHParseDisplayName error\n");
		return false;
	}

	IShellItem* shellItem;
	hr = SHCreateItemFromIDList(pidl, IID_IShellItem, (void**) &shellItem);
	if (FAILED(hr)) {
		printf("%ld \n", hr);
		printf("SHCreateItemFromIDList error\n");
		return false;
	}
	ILFree(pidl);

	IThumbnailCache* pTC;
	hr = CoCreateInstance(CLSID_ShellLink, NULL, CLSCTX_INPROC_SERVER, IID_IShellLink, (void**)&pTC);
	if (SUCCEEDED(hr)) {
		ISharedBitmap* ppvThumb;
		hr = pTC->GetThumbnail(shellItem, 256, WTS_EXTRACT, &ppvThumb, NULL, NULL);
		if (FAILED(hr)) {
			printf("Thumb extract error\n");
			pTC->Release();
			return false;
		}
		pTC->Release();
		printf("Extract OK\n");
		return true;
	} else {	
		printf("CoCreateInstance ThumbnailCache failed\n");
	}

	return false;
}

JNIEXPORT void JNICALL Java_com_kotcrab_vne_win_thumbnails_WinThumbnailProvider_disposeJni(JNIEnv * env, jobject jobj) {
	CoUninitialize();
}