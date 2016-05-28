package com.kotcrab.vne.runtime;

/**
 * Used to handle error that occurred in native code. Note that such errors are unrecoverable and error handlers
 * are usually used for logging debugging information.
 * @author Kotcrab
 */
public interface VneErrorHandler {
	/**
	 * Called when error occurred in native code.
	 * @param message error message
	 * @param errorCode error code (if applicable, for example value of HRESULT from WinAPI)
	 */
	void nativeError (String message, long errorCode);
}
