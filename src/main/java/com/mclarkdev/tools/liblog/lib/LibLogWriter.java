package com.mclarkdev.tools.liblog.lib;

import java.net.URI;

/**
 * LibLog // LibLogWriter
 */
public abstract class LibLogWriter {

	protected final boolean debug;

	public LibLogWriter(URI uri) {
		String query = uri.getQuery();
		this.debug = ((query != null) && query.equals("debug"));
	}

	/**
	 * The implemented message writer.
	 * 
	 * @param logMessage the message to write
	 */
	public abstract void write(LibLogMessage logMessage);
}
