package com.mclarkdev.tools.liblog.lib;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

import com.mclarkdev.tools.liblog.LibLog;

/**
 * LibLog // LibLogTCPStream
 */
public class LibLogTCPStream implements LibLogStream {

	protected Socket logSocket = null;

	private final int logPort;
	private final InetAddress logAddr;

	/**
	 * Initialize a new TCP based log stream.
	 * 
	 * @param addr the connection address
	 * @throws UnknownHostException failed resolving address
	 */
	public LibLogTCPStream(URI addr) throws UnknownHostException {

		this.logPort = addr.getPort();
		this.logAddr = InetAddress.getByName(addr.getHost());
	}

	/**
	 * Returns true if connected to the log service.
	 * 
	 * @return true if connected
	 */
	public boolean connected() {
		return ((logSocket != null) && (logSocket.isConnected()));
	}

	/**
	 * Connect to the remote logging server.
	 * 
	 * @param force Force a reconnect.
	 * @return connect successful
	 */
	protected boolean connect(boolean force) {
		if (connected() && !force) {
			return true;
		}

		// Disconnect if connected
		disconnect();

		try {

			// Create new socket connection
			logSocket = new Socket(logAddr, logPort);
			return true;

		} catch (IOException e) {

			// Log the stream connection failure
			LibLog.logF("logger", "LibLogTCP: Failed to connect (tcp://%s:%d)", logAddr.toString(), logPort);
			return false;
		}
	}

	/**
	 * Disconnects the existing socket.
	 */
	public void disconnect() {
		if (logSocket == null) {
			return;
		}

		try {
			logSocket.close();
		} catch (IOException e) {
		} finally {
			logSocket = null;
		}
	}

	/**
	 * Write a message to the log server.
	 * 
	 * @param message the log message
	 * @return write successful
	 */
	@Override
	public boolean write(String message) {
		if (!connected() && !connect(false)) {
			return false;
		}

		try {

			// Get the output stream
			OutputStream out = //
					logSocket.getOutputStream();

			// Only one writer at a time
			synchronized (out) {

				// Send the log message
				out.write(message.getBytes());
				out.write('\n');
				out.flush();
			}
			return true;

		} catch (IOException e) {

			LibLog.log("logger", "LibLogTCP: Failed to write message.", e);
			LibLog.log("logger", message);
			disconnect();
			return false;
		}
	}
}
