package com.mclarkdev.tools.liblog.writer;

import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;

import com.mclarkdev.tools.liblog.lib.LibLogCachedLogWriter;
import com.mclarkdev.tools.liblog.lib.LibLogTCPStream;

/**
 * LibLog // LibLogCachedTCPWriter
 * 
 * Writes log messages to a TCP server.
 * 
 * Messages are buffered if not transmitted.
 */
public class LibLogTCPWriter extends LibLogCachedLogWriter {

	public static String scheme() {
		return "tcp";
	}

	public LibLogTCPWriter(URI uri) //
			throws UnknownHostException, SocketException {
		super(uri, new LibLogTCPStream(uri));
	}
}
