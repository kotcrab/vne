package com.kotcrab.vne.runtime;

/** @author Kotcrab */
public abstract class VneLibrary {
	private VneErrorHandler errorHandler;

	protected void handleError (String message, long errorCode) {
		if (errorHandler != null) errorHandler.nativeError(message, errorCode);
	}

	/**
	 * Sets new error handler
	 * @param errorHandler may be null
	 */
	public void setErrorHandler (VneErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
}
