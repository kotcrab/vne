package com.kotcrab.vne.runtime;

/**
 * Default implementation of error handler used that simply logs information to standard output.
 * @author Kotcrab
 */
public class VneDefaultErrorHandler implements VneErrorHandler {
	private String owner;

	/** @param owner class name that uses this handler */
	public VneDefaultErrorHandler (String owner) {
		this.owner = owner;
	}

	@Override
	public void nativeError (String message, long errorCode) {
		System.out.println("VneError in " + owner + ": " + message + " error code: " + errorCode);
	}
}
