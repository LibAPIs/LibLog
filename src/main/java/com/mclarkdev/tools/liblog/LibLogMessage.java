package com.mclarkdev.tools.liblog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

/**
 * LibLog // LibLogMessage
 */
public class LibLogMessage {

	public static final SimpleDateFormat _DFORMAT = //
			new SimpleDateFormat("YYYYMMdd HH:mm:ss");

	private final long time;
	private final String stamp;

	private final String facility;
	private final String message;
	private final Throwable tossed;

	private final String className;
	private final long classLine;

	/**
	 * Build a new LibLogMessage.
	 * 
	 * @param facility log facility
	 * @param message  log message
	 * @param tossed   optional throwable
	 */
	public LibLogMessage(String facility, String message, Throwable tossed) {

		this.time = System.currentTimeMillis();
		this.stamp = _DFORMAT.format(time);

		this.facility = facility;
		this.tossed = tossed;

		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		// get call trace
		for (int x = 2;; x++) {

			// first occurrence of not LibLog
			if (!trace[x].getClassName().equals(LibLog.class.getName())) {

				this.className = trace[x].getClassName();
				this.classLine = trace[x].getLineNumber();
				break;
			}
		}

		// build the message
		this.message = message + //
				((tossed != null) ? ("\n" + getLoggedThrowableString()) : "");
	}

	/**
	 * Returns the time-stamp of message generation.
	 * 
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Returns the time-stamp of message generation.
	 * 
	 * @return the timestamp
	 */
	public String getTimeStamp() {
		return stamp;
	}

	/**
	 * Returns the log facility passed to the logger.
	 * 
	 * @return the facility
	 */
	public String getLoggedFacility() {
		return facility;
	}

	/**
	 * Returns the message passed to the logger.
	 * 
	 * @return the message
	 */
	public String getLoggedMessage() {
		return message;
	}

	/**
	 * Returns the throwable passed to the logger.
	 * 
	 * @return the throwable
	 */
	public Throwable getLoggedThrowable() {
		return tossed;
	}

	/**
	 * Returns the logged throwable wrapped with header / footer.
	 * 
	 * @return throwable as a string
	 */
	public String getLoggedThrowableString() {
		if (tossed == null) {
			return null;
		}

		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw)) {
			tossed.printStackTrace(pw);
			return String.format(//
					" --- STACK BEGIN ---\n%s\n%s --- STACK END ---", //
					tossed.getClass().getName(), sw.toString());
		}
	}

	/**
	 * Returns the class name that invoked the log operation.
	 * 
	 * @return name of calling class
	 */
	public String getLoggedClassName() {
		return className;
	}

	/**
	 * Returns the line number that invoked the log operation.
	 * 
	 * @return line number in calling class
	 */
	public long getLoggedLineNumber() {
		return classLine;
	}

	/**
	 * Build a basic log line.
	 * 
	 * @return formatted log line
	 */
	public String buildLogLine() {
		return String.format(//
				" +%s - [ %s ] - %s", //
				stamp, facility, message);
	}

	/**
	 * Build a log line with debugging information.
	 * 
	 * @return formatted log line with debug information
	 */
	public String buildDebugLine() {
		return String.format(//
				" +%s - [ %s @ %s : %d ] - %s", //
				stamp, facility, className, classLine, message);
	}

	/**
	 * Returns the logged message.
	 */
	public String toString() {
		return getLoggedMessage();
	}

	/**
	 * Returns a runtime exception with the logged message as the cause.
	 * 
	 * @return the log message as an exception
	 */
	public RuntimeException asException() {
		return asException(RuntimeException.class);
	}

	/**
	 * Returns user defined exception type with the logged message as the cause.
	 * 
	 * @return the log message as an exception
	 */
	public <T extends Exception> T asException(Class<T> clazz) {
		try {
			return clazz.getConstructor(String.class).newInstance(getLoggedMessage());
		} catch (Exception e) {
			throw new RuntimeException(getLoggedMessage());
		}
	}
}
