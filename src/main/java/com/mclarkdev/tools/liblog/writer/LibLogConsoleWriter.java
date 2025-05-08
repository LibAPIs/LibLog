package com.mclarkdev.tools.liblog.writer;

import java.io.PrintStream;
import java.net.URI;

import com.mclarkdev.tools.liblog.lib.LibLogMessage;
import com.mclarkdev.tools.liblog.lib.LibLogWriter;

/**
 * LibLog // LibLogConsoleWriter
 * 
 * Writes messages to STDOUT.
 */
public class LibLogConsoleWriter extends LibLogWriter {

	public static String scheme() {
		return "console";
	}

	private final PrintStream out;

	public LibLogConsoleWriter(URI uri) {
		super(uri);

		this.out = System.out;
	}

	/**
	 * Write log messages to the console. (stdout)
	 */
	@Override
	public void write(LibLogMessage message) {

		String logLine = (debug) ? //
				message.buildDebugLine() : message.buildLogLine();

		// Write to stream
		synchronized (out) {
			out.println(logLine);
		}
	}
}
