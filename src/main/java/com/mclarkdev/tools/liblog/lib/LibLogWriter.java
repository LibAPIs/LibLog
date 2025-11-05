package com.mclarkdev.tools.liblog.lib;

import java.net.URI;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LibLog // LibLogWriter
 */
public abstract class LibLogWriter {

	/**
	 * Returns the logger scheme advertised.
	 * 
	 * @return the logger scheme
	 */
	public static String scheme() {
		return null;
	}

	protected final URI uri;

	protected final String scheme;

	protected final boolean debug;

	public LibLogWriter(URI uri) {

		this.uri = uri;

		this.scheme = uri.getScheme();

		String query = uri.getQuery();
		this.debug = ((query != null) && query.equals("debug"));
		LibLog.logF("logger", "Created LogWriter: %s", uri);
	}

	/**
	 * Called when the logger is created.
	 * 
	 * Used to initialize streams or create directories.
	 */
	public abstract void setup();

	/**
	 * The implemented message writer.
	 * 
	 * @param logMessage the message to write
	 */
	public abstract void write(LibLogMessage logMessage);

	/**
	 * Called when the logger is to be shutdown.
	 * 
	 * Used to close all streams and files.
	 */
	public abstract void shutdown();
}
