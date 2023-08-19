package com.mclarkdev.tools.liblog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LibLog {

	public interface LogWriter {
		public abstract void write(LibLogMessage message);
	}

	private static final String defaultFacility = "server";

	private static String appName;

	private static final Set<LogWriter> logWriters;

	private static final Properties logCodes = new Properties();

	static {

		// Determine application name
		String envName = System.getenv("APP_NAME");
		appName = (envName != null) ? envName : "MyApp";

		// Setup logger cache
		logWriters = ConcurrentHashMap.newKeySet();

		// Default write logs to disk
		addLogger(new LibLogFileWriter());
	}

	/**
	 * Get the name of the application, known to the logger.
	 * 
	 * @return name of the application
	 */
	public static String getAppName() {
		return appName;
	}

	/**
	 * Set the name of the application.
	 * 
	 * @param name
	 */
	public static void setAppName(String name) {
		appName = name;
	}

	/**
	 * Load localized strings from disk.
	 * 
	 * @param in
	 * @throws IOException
	 */
	public static void loadStrings(InputStream in) throws IOException {
		logCodes.load(in);
	}

	/**
	 * Add a log receiver.
	 * 
	 * @param logger
	 */
	public static void addLogger(LogWriter logger) {
		logWriters.add(logger);
	}

	/**
	 * Remove a log receiver.
	 * 
	 * @param logger
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
	 * @param message
	 * @return
	 */
	public static LibLogMessage log(String message) {
		return log(new LibLogMessage(defaultFacility, message, null));
	}

	/**
	 * Log a message.
	 * 
	 * @param message
	 * @param e
	 * @return
	 */
	public static LibLogMessage log(String message, Throwable e) {
		return log(new LibLogMessage(defaultFacility, message, e));
	}

	/**
	 * Log a message.
	 * 
	 * @param facility
	 * @param message
	 * @return
	 */
	public static LibLogMessage log(String facility, String message) {
		return log(new LibLogMessage(facility, message, null));
	}

	/**
	 * Log a message
	 * 
	 * @param facility
	 * @param message
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
	public static LibLogMessage logF_(String format, Object... args) {
		return log(new LibLogMessage(defaultFacility, f(format, args), null));
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
	public static LibLogMessage clog(String code) {
		return log(new LibLogMessage(defaultFacility, c(code), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code
	 * @param e
	 * @return
	 */
	public static LibLogMessage clog(String code, Throwable e) {
		return log(new LibLogMessage(defaultFacility, c(code), e));
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
	public static LibLogMessage clogF_(String code, Object... args) {
		return log(new LibLogMessage(defaultFacility, f(c(code), args), null));
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
	 * @param message
	 * @return
	 */
	public static LibLogMessage log(LibLogMessage message) {
		for (LogWriter logger : logWriters)
			logger.write(message);
		return message;
	}

	/**
	 * Format a message.
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static String f(String format, Object... args) {
		return String.format(format, args);
	}

	/**
	 * Retrieve localized text for the given key.
	 * 
	 * @param lookup
	 * @return
	 */
	public static String c(String lookup) {
		return ((!logCodes.containsKey(lookup)) ? lookup : //
				String.format("%s : %s", lookup, logCodes.getProperty(lookup)));
	}
}
