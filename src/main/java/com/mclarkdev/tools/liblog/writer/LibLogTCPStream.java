package com.mclarkdev.tools.liblog.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.liblog.lib.LibLogStream;

/**
 * LibLog // LibLogFileWriter
 */
public class LibLogTCPStream implements LibLogStream {

	protected Socket logSocket = null;

	private final int logPort;
	private final InetAddress logAddr;

	public LibLogTCPStream(URI addr) throws UnknownHostException {

		this.logPort = addr.getPort();
		this.logAddr = InetAddress.getByName(addr.getHost());
	}

	/**
	 * Returns true if connected to the log service.
	 * 
	 * @return
	 */
	public boolean connected() {
		return ((logSocket != null) && (logSocket.isConnected()));
	}

	/**
	 * Connect to the remote logging server.
	 * 
	 * @param force Force a reconnect.
	 * @return
	 */
	protected boolean connect(boolean force) {
		if (connected() && !force) {
			return true;
		}

		try {

			disconnect();
			logSocket = new Socket(logAddr, logPort);
			return true;

		} catch (IOException e) {

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
	 * @param message
	 * @return
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
