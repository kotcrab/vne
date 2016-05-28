#include <com_kotcrab_vne_win_thumbnails_WinThumbnailProvider.h>

#define _WIN32_WINNT 0x0600 //vista

#include <Windows.h>
#include <shlobj.h>
#include <shobjidl.h>
#include <thumbcache.h>

LPCWSTR vnePathToWstr(const char * path)
{
	int wchars_num = MultiByteToWideChar(CP_UTF8, 0, path, -1, NULL, 0);
	wchar_t* wstr = new wchar_t[wchars_num];
	MultiByteToWideChar(CP_UTF8, 0, path, -1, wstr, wchars_num);
	return wstr;
}

void errorHandler(const char * errorMsg, HRESULT hr)
{
	printf("Error %ld: %s\n", hr, errorMsg);
}

HRESULT createShellItem(LPCWSTR path, IShellItem** shellItemOut)
{
	HRESULT hr;
	LPITEMIDLIST pidl;
	hr = SHParseDisplayName(path, NULL, &pidl, 0, NULL);
	if (FAILED(hr)) {
		errorHandler("SHParseDisplayName", hr);
		return hr;
	}

	IShellItem* shellItem;
	hr = SHCreateItemFromIDList(pidl, IID_IShellItem, (void**)&shellItem);
	ILFree(pidl); //pidl is PCIDLIST_ABSOLUTE which needs manual free
	if (FAILED(hr)) {
		errorHandler("SHCreateItemFromIDList", hr);
		return hr;
	}
	*shellItemOut = shellItem;
	return hr;
}

JNIEXPORT void JNICALL Java_com_kotcrab_vne_win_thumbnails_WinThumbnailProvider_initJni(JNIEnv * env, jobject jobj) {
	CoInitializeEx(NULL, COINIT_MULTITHREADED);
}

JNIEXPORT jintArray JNICALL Java_com_kotcrab_vne_win_thumbnails_WinThumbnailProvider_getThumbnailJni(JNIEnv * env, jobject jobj, jstring jpath, jint jsize) {
	const char *str = env->GetStringUTFChars(jpath, NULL);
	LPCWSTR path = vnePathToWstr(str);
	env->ReleaseStringUTFChars(jpath, str);

	HRESULT hr;

	// Obtain IShellItem from path
	IShellItem* shellItem;
	hr = createShellItem(path, &shellItem);
	if (FAILED(hr))
	{
		return NULL; //errors already handlded
	}

	// Obtain IThumbnailCache interface
	IThumbnailCache* pTC;
	hr = CoCreateInstance(CLSID_LocalThumbnailCache, NULL, CLSCTX_INPROC_SERVER, IID_IThumbnailCache, (void**)&pTC);
	if (FAILED(hr))
	{
		errorHandler("CoCreateInstance ThumbnailCache", hr);
		shellItem->Release();
		return NULL;
	}

	// Obtain ISharedBitmap from thumbnail cache
	ISharedBitmap* ppvThumb;
	hr = pTC->GetThumbnail(shellItem, jsize, WTS_EXTRACT, &ppvThumb, nullptr, nullptr);
	shellItem->Release();
	if (FAILED(hr)) {
		pTC->Release();
		errorHandler("Thumbnail extract error", hr);
		return NULL;
	}

	// Obtain handle to bitmap from ISharedBitmap
	HBITMAP hBitmap = NULL;
	hr = ppvThumb->GetSharedBitmap(&hBitmap);
	if (FAILED(hr)) {
		pTC->Release();
		ppvThumb->Release();
		errorHandler("Bitmap extract error", hr);
		return NULL;
	}

	HWND hwnd = GetActiveWindow();
	HDC hdc = GetDC(hwnd);

	BITMAPINFO bmInfo = { 0 };
	bmInfo.bmiHeader.biSize = sizeof(bmInfo.bmiHeader);

	// Extract BITMAPINFO structure from the bitmap
	if (GetDIBits(hdc, hBitmap, 0, 0, NULL, &bmInfo, DIB_RGB_COLORS) == 0)
	{
		pTC->Release();
		ppvThumb->Release();
		ReleaseDC(hwnd, hdc);
		errorHandler("BITMAPINFO extract", hr);
		return NULL;
	}

	// Create the pixel buffer
	DWORD pixelBufferSize = bmInfo.bmiHeader.biSizeImage;
	BYTE* pixelBuffer = new BYTE[pixelBufferSize];

	bmInfo.bmiHeader.biBitCount = 32; //32 bits format BGR8888
	bmInfo.bmiHeader.biCompression = BI_RGB;
	bmInfo.bmiHeader.biHeight = abs(bmInfo.bmiHeader.biHeight); // correct the bottom-up ordering of lines

	// Extract actual colors to buffer 
	if (GetDIBits(hdc, hBitmap, 0, bmInfo.bmiHeader.biHeight, pixelBuffer, &bmInfo, DIB_RGB_COLORS) == 0)
	{
		pTC->Release();
		ppvThumb->Release();
		ReleaseDC(hwnd, hdc);
		errorHandler("Pixels extract", hr);
		return NULL;
	}

	LONG bWidth = bmInfo.bmiHeader.biWidth;
	LONG bHeight = bmInfo.bmiHeader.biHeight;

	int colorBufferSize = 2 + bWidth * bHeight;
	jint* colorBuffer = new jint[colorBufferSize];

	DWORD colorBufferIndex = 0;
	colorBuffer[colorBufferIndex++] = bWidth;
	colorBuffer[colorBufferIndex++] = bHeight;

	for (DWORD i = 0; i < pixelBufferSize; i += 4)
	{
		// GetDIBits returns colors in BGR format
		BYTE blue = pixelBuffer[i];
		BYTE green = pixelBuffer[i + 1];
		BYTE red = pixelBuffer[i + 2];
		BYTE alpha = pixelBuffer[i + 3];
		int color = ((red << 24) | (green << 16) | ((blue << 8) | (alpha)));
		colorBuffer[colorBufferIndex++] = color;
	}

	// Converting colors done, delete buffer
	delete[] pixelBuffer;

	jintArray jcolors = env->NewIntArray(colorBufferSize);
	if (jcolors == NULL) return NULL; //Out of memory occured
	env->SetIntArrayRegion(jcolors, 0, colorBufferSize, colorBuffer);

	delete[] colorBuffer;

	pTC->Release();
	ppvThumb->Release();
	ReleaseDC(hwnd, hdc);

	return jcolors;
}

JNIEXPORT void JNICALL Java_com_kotcrab_vne_win_thumbnails_WinThumbnailProvider_disposeJni(JNIEnv * env, jobject jobj) {
	CoUninitialize();
}
