package com.mclarkdev.tools.liblog.writer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;

import com.mclarkdev.tools.liblog.lib.LibLogMessage;
import com.mclarkdev.tools.liblog.lib.LibLogWriter;

/**
 * LibLog // LibLogUDPWriter
 * 
 * Writes log messages to a UDP server.
 */
public class LibLogUDPWriter extends LibLogWriter {

	public static String scheme() {
		return "udp";
	}

	protected DatagramSocket logSocket = null;

	private final int logPort;
	private final InetAddress logAddr;

	public LibLogUDPWriter(URI uri) throws UnknownHostException, SocketException {
		super(uri);

		this.logPort = uri.getPort();
		this.logAddr = InetAddress.getByName(uri.getHost());

		this.logSocket = new DatagramSocket();
	}

	@Override
	public void write(LibLogMessage message) {

		// Build the log line
		String logLine = (debug) ? //
				message.buildDebugLine() : message.buildLogLine();
		byte[] bytes = logLine.getBytes();

		// Assemble UDP packet
		DatagramPacket datagram = new DatagramPacket(//
				bytes, bytes.length, logAddr, logPort);

		try {

			// Write to UDP stream
			logSocket.send(datagram);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void shutdown() {
		this.logSocket.close();
		this.logSocket = null;
		System.gc();
	}
}
