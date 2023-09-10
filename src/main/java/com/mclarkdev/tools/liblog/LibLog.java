package com.mclarkdev.tools.liblog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LibLog // LibLog
 * 
 * A basic application logger.
 */
public class LibLog {

	public interface LogWriter {
		public abstract void write(boolean debug, LibLogMessage message);
	}

	private static final String defaultLog;

	private static final Set<LogWriter> logWriters;

	private static final Properties logCodes = new Properties();

	private static boolean logDebug = false;

	static {

		// Determine default log name
		String defLog = System.getenv("LOG_NAME");
		defaultLog = (defLog != null) ? defLog : "server";

		// Setup logger cache
		logWriters = ConcurrentHashMap.newKeySet();

		// Default write logs to disk
		String logDir = System.getenv("LOG_DIR");
		addLogger(new LibLogFileWriter(logDir));

		// Debug if environment variable set
		logDebug = (System.getenv("LOG_DEBUG") != null);
	}

	/**
	 * Returns true if debug logging is enabled.
	 * 
	 * @return debugging mode enabled
	 */
	public static boolean getDebugEnabled() {
		return logDebug;
	}

	/**
	 * Enable or disable debug logging.
	 * 
	 * @param debug set debugging mode enabled
	 */
	public static void setDebugEnabled(boolean debug) {
		logDebug = debug;
	}

	/**
	 * Load localized strings from disk.
	 * 
	 * @param in stream to .properties file
	 * @throws IOException failed to read .properties file
	 */
	public static void loadStrings(InputStream in) throws IOException {
		logCodes.load(in);
	}

	/**
	 * Add a log receiver.
	 * 
	 * @param logger add a custom LogWriter
	 */
	public static void addLogger(LogWriter logger) {
		logWriters.add(logger);
	}

	/**
	 * Remove a log receiver.
	 * 
	 * @param logger remove a custom LogWriter
	 */
	public static void removeLogger(LogWriter logger) {
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
		return log(new LibLogMessage(defaultLog, message, null));
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @param e
	 * @return
	 */
	public static LibLogMessage _log(String message, Throwable e) {
		return log(new LibLogMessage(defaultLog, message, e));
	}

	/**
	 * Log a message.
	 * 
	 * @param facility
	 * @param message  the message to log
	 * @return
	 */
	public static LibLogMessage log(String facility, String message) {
		return log(new LibLogMessage(facility, message, null));
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
		return log(new LibLogMessage(facility, message, e));
	}

	/**
	 * Log a message.
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static LibLogMessage _logF(String format, Object... args) {
		return log(new LibLogMessage(defaultLog, f(format, args), null));
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
		return log(new LibLogMessage(facility, f(format, args), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code
	 * @return
	 */
	public static LibLogMessage _clog(String code) {
		return log(new LibLogMessage(defaultLog, c(code), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code
	 * @param e
	 * @return
	 */
	public static LibLogMessage _clog(String code, Throwable e) {
		return log(new LibLogMessage(defaultLog, c(code), e));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param facility
	 * @param code
	 * @return
	 */
	public static LibLogMessage clog(String facility, String code) {
		return log(new LibLogMessage(facility, c(code), null));
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
		return log(new LibLogMessage(facility, c(code), e));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code
	 * @param args
	 * @return
	 */
	public static LibLogMessage _clogF(String code, Object... args) {
		return log(new LibLogMessage(defaultLog, f(c(code), args), null));
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
		return log(new LibLogMessage(facility, f(c(code), args), null));
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @return
	 */
	public static LibLogMessage log(LibLogMessage message) {
		for (LogWriter logger : logWriters)
			logger.write(logDebug, message);
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
		return String.format(format, args);
	}

	/**
	 * Retrieve localized text for the given key.
	 * 
	 * @param lookup localization code to resolve
	 * @return the localized string
	 */
	public static String c(String lookup) {
		return ((!logCodes.containsKey(lookup)) ? lookup : //
				String.format("%s : %s", lookup, logCodes.getProperty(lookup)));
	}
}
