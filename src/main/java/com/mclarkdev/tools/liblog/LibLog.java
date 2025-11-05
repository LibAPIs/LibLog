package com.mclarkdev.tools.liblog;

import java.util.IllegalFormatException;

import com.mclarkdev.tools.liblog.lib.LibLogConfig;
import com.mclarkdev.tools.liblog.lib.LibLogMessage;
import com.mclarkdev.tools.liblog.lib.LibLogMessage.LogLevel;
import com.mclarkdev.tools.liblog.lib.LibLogWriter;
import com.mclarkdev.tools.liblog.writer.LibLogConsoleWriter;
import com.mclarkdev.tools.liblog.writer.LibLogFileWriter;
import com.mclarkdev.tools.liblog.writer.LibLogTCPWriter;
import com.mclarkdev.tools.liblog.writer.LibLogUDPWriter;

/**
 * LibLog // LibLog
 * 
 * A basic application logger.
 */
public class LibLog {

	private static final LibLogConfig cfg;

	static {

		// Load configuration
		cfg = LibLogConfig.create();

		// Register default loggers
		cfg.registerLogger(LibLogConsoleWriter.class);
		cfg.registerLogger(LibLogFileWriter.class);
		cfg.registerLogger(LibLogUDPWriter.class);
		cfg.registerLogger(LibLogTCPWriter.class);
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @return the logged message
	 */
	public static LibLogMessage _log(String message) {
		return log(new LibLogMessage(LogLevel.INFO, cfg.defaultLog(), message, null));
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @param e       the exception to log
	 * @return the log message
	 */
	public static LibLogMessage _log(String message, Throwable e) {
		return log(new LibLogMessage(LogLevel.WARN, cfg.defaultLog(), message, e));
	}

	/**
	 * Log a message.
	 * 
	 * @param facility the log message facility
	 * @param message  the message to log
	 * @return the log message
	 */
	public static LibLogMessage log(String facility, String message) {
		return log(new LibLogMessage(LogLevel.INFO, facility, message, null));
	}

	/**
	 * Log a message
	 * 
	 * @param facility the log message facility
	 * @param message  the message to log
	 * @param e        the exception to log
	 * @return the log message
	 */
	public static LibLogMessage log(String facility, String message, Throwable e) {
		return log(new LibLogMessage(LogLevel.WARN, facility, message, e));
	}

	/**
	 * Log a message.
	 * 
	 * @param format the log message format
	 * @param args   the log message arguments
	 * @return the log message
	 */
	public static LibLogMessage _logF(String format, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, cfg.defaultLog(), f(format, args), null));
	}

	/**
	 * Log a message.
	 * 
	 * @param facility the log message facility
	 * @param format   the log message format
	 * @param args     the log message arguments
	 * @return the log message
	 */
	public static LibLogMessage logF(String facility, String format, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, facility, f(format, args), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code the localized message code
	 * @return the log message
	 */
	public static LibLogMessage _clog(String code) {
		return log(new LibLogMessage(LogLevel.INFO, cfg.defaultLog(), c(code), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code the localized message code
	 * @param e    the exception to log
	 * @return the log message
	 */
	public static LibLogMessage _clog(String code, Throwable e) {
		return log(new LibLogMessage(LogLevel.INFO, cfg.defaultLog(), c(code), e));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param facility the log message facility
	 * @param code     the localized message code
	 * @return the log message
	 */
	public static LibLogMessage clog(String facility, String code) {
		return log(new LibLogMessage(LogLevel.INFO, facility, c(code), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param facility the log message facility
	 * @param code     the localized message code
	 * @param e        the exception to log
	 * @return the log message
	 */
	public static LibLogMessage clog(String facility, String code, Throwable e) {
		return log(new LibLogMessage(LogLevel.INFO, facility, c(code), e));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param code the localized message code
	 * @param args the log message arguments
	 * @return the log message
	 */
	public static LibLogMessage _clogF(String code, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, cfg.defaultLog(), f(c(code), args), null));
	}

	/**
	 * Log a localized message.
	 * 
	 * @param facility the log message facility
	 * @param code     the localized message code
	 * @param args     the log message arguments
	 * @return the log message
	 */
	public static LibLogMessage clogF(String facility, String code, Object... args) {
		return log(new LibLogMessage(LogLevel.INFO, facility, f(c(code), args), null));
	}

	/**
	 * Log a message.
	 * 
	 * @param message the message to log
	 * @return the log message
	 */
	public static LibLogMessage log(LibLogMessage message) {
		for (LibLogWriter logger : cfg.loggers()) {
			try {
				logger.write(message);
			} catch (Error | Exception ex) {
				System.err.printf("Failed writing log.\n ( %s )\n", message);
				ex.printStackTrace(System.err);
			}
		}

		return message;
	}

	/**
	 * Format a message.
	 * 
	 * @param format the log message format
	 * @param args   the log message arguments
	 * @return the formatted log string
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
	 * Return a localized string for a given code.
	 * 
	 * @param code the localized message code
	 * @return the localized string
	 */
	public static String c(String code) {
		return cfg().l10n(code);
	}

	/**
	 * Return an instance of the logger configuration.
	 * 
	 * @return logger configuration
	 */
	public static LibLogConfig cfg() {
		return cfg;
	}
}
