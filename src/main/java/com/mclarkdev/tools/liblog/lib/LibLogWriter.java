package com.mclarkdev.tools.liblog.lib;

import java.net.URI;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LibLog // LibLogWriter
 */
public abstract class LibLogWriter {

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

	public abstract void setup();

	/**
	 * The implemented message writer.
	 * 
	 * @param logMessage the message to write
	 */
	public abstract void write(LibLogMessage logMessage);

	public abstract void shutdown();

	public static String scheme() {
		return null;
	}
}
