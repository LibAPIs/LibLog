package com.mclarkdev.tools.liblog;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.IllegalFormatException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mclarkdev.tools.liblog.lib.LibLogMessage;
import com.mclarkdev.tools.liblog.lib.LibLogMessage.LogLevel;
import com.mclarkdev.tools.liblog.lib.LibLogWriter;
import com.mclarkdev.tools.liblog.writer.LibLogCachedLogWriter;
import com.mclarkdev.tools.liblog.writer.LibLogConsoleWriter;
import com.mclarkdev.tools.liblog.writer.LibLogFileWriter;
import com.mclarkdev.tools.liblog.writer.LibLogTCPStream;
import com.mclarkdev.tools.liblog.writer.LibLogUDPWriter;

/**
 * LibLog // LibLog
 * 
 * A basic application logger.
 */
public class LibLog {

	private static final String defaultLog;

	private static final Set<LibLogWriter> logWriters;

	private static final Properties logStrings = new Properties();

	private static boolean logCodes = false;

	static {

		// Determine default log name
		String defLog = System.getenv("LOG_NAME");
		defaultLog = (defLog != null) ? defLog : "server";

		// Setup logger cache
		logWriters = ConcurrentHashMap.newKeySet();

		// Debug if environment variable set
		logCodes = (System.getenv("LOG_CODES") != null);

		// Setup log streams
		String logStreams = System.getenv("LOG_STREAMS");
		for (String logStream : logStreams.split(";")) {
			addLogger(URI.create(logStream));
		}
	}

	/**
	 * Load localized strings from disk.
	 * 
	 * @param in stream to .properties file
	 * @throws IOException failed to read .properties file
	 */
	public static void loadStrings(InputStream in) throws IOException {
		logStrings.load(in);
	}

	/**
	 * Add a log receiver.
	 * 
	 * @param logURI add logger from URI
	 */
	public static void addLogger(URI logURI) {

		String scheme = logURI.getScheme();

		try {
			switch (scheme) {
			case "console":
				addLogger(new LibLogConsoleWriter(logURI));
				break;

			case "file":
				addLogger(new LibLogFileWriter(logURI));
				break;

			case "tcp":
				addLogger(new LibLogCachedLogWriter(logURI, //
						new LibLogTCPStream(logURI)));
				break;

			case "udp":
				addLogger(new LibLogUDPWriter(logURI));
				break;

			default:
				throw new IllegalArgumentException(//
						"Unsupported logger scheme: " + scheme);
			}
		} catch (SocketException e) {
			e.printStackTrace(System.err);
		} catch (UnknownHostException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Add a log receiver.
	 * 
	 * @param writer a custom LogWriter
	 */
	public static void addLogger(LibLogWriter writer) {

		logWriters.add(writer);
	}

	/**
	 * Remove a log receiver.
	 * 
	 * @param logger remove a custom LogWriter
	 */
	public static void removeLogger(LibLogWriter logger) {
		if (logger == null) {
			logWriters.clear();
		} else {
			logWriters.remove(logger);
		}
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @return the logged message
	 */
	public static LibLogMessage _log(String message) {
		return log(new LibLogMessage(LogLevel.INFO, defaultLog, message, null));
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @param e
	 * @return
	 */
	public static LibLogMessage _log(String message, Throwable e) {
		return log(new LibLogMessage(LogLevel.WARN, defaultLog, message, e));
	}

	/**
	 * Log a message.
	 * 
	 * @param facility
	 * @param message  the message to log
	 * @return
	 */
	public static LibLogMessage log(String facility, String message) {
		return log(new LibLogMessage(LogLevel.INFO, facility, message, null));
	}

	/**
	 * Log a message
	 * 
	 * @param facility
	 * @param message  the message to log
	 * @param e
	 * @return
	 */
	public static LibLogMessage log(String facility, String message, Throwable e) {
		return log(new LibLogMessage(LogLevel.WARN, facility, message, e));
	}

	/**
	 * Log a message.
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static LibLogMessage _logF(String format, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, defaultLog, f(format, args), null));
	}

	/**
	 * Log a message.
	 * 
	 * @param facility
	 * @param format
	 * @param args
	 * @return
	 */
	public static LibLogMessage logF(String facility, String format, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, facility, f(format, args), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code
	 * @return
	 */
	public static LibLogMessage _clog(String code) {
		return log(new LibLogMessage(LogLevel.INFO, defaultLog, c(code), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code
	 * @param e
	 * @return
	 */
	public static LibLogMessage _clog(String code, Throwable e) {
		return log(new LibLogMessage(LogLevel.INFO, defaultLog, c(code), e));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param facility
	 * @param code
	 * @return
	 */
	public static LibLogMessage clog(String facility, String code) {
		return log(new LibLogMessage(LogLevel.INFO, facility, c(code), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param facility
	 * @param code
	 * @param e
	 * @return
	 */
	public static LibLogMessage clog(String facility, String code, Throwable e) {
		return log(new LibLogMessage(LogLevel.INFO, facility, c(code), e));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code
	 * @param args
	 * @return
	 */
	public static LibLogMessage _clogF(String code, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, defaultLog, f(c(code), args), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param facility
	 * @param code
	 * @param args
	 * @return
	 */
	public static LibLogMessage clogF(String facility, String code, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, facility, f(c(code), args), null));
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @return
	 */
	public static LibLogMessage log(LibLogMessage message) {
		for (LibLogWriter logger : logWriters)
			logger.write(message);
		return message;
	}

	/**
	 * Format a message.
	 * 
	 * @param format the string format
	 * @param args   the format arguments
	 * @return the formatted string
	 */
	public static String f(String format, Object... args) {
		try {
			return String.format(format, args);
		} catch (IllegalFormatException e) {
			String exc = e.getClass().getName();
			exc = exc.substring(exc.lastIndexOf('.') + 1);
			return String.format("%s (%s: %s)", format, exc, e.getMessage());
		}
	}

	/**
	 * Retrieve localized text for the given key.
	 * 
	 * @param lookup localization code to resolve
	 * @return the localized string
	 */
	public static String c(String lookup) {
		String value = (logStrings.containsKey(lookup)) ? logStrings.getProperty(lookup) : lookup;
		return (logCodes) ? String.format("%s : %s", lookup, value) : value;
	}
}
