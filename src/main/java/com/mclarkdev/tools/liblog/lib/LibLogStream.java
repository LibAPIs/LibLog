package com.mclarkdev.tools.liblog.lib;

/**
 * LibLog // LibLogStream
 */
public interface LibLogStream {

	/**
	 * The implemented stream write.
	 * 
	 * @param message the message to write
	 * @return write successful
	 */
	public abstract boolean write(String message);
}
