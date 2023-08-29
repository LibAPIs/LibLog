package com.mclarkdev.tools.liblog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

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

	public long getTime() {
		return time;
	}

	public String getTimeStamp() {
		return stamp;
	}

	public String getLoggedFacility() {
		return facility;
	}

	public String getLoggedMessage() {
		return message;
	}

	public Throwable getLoggedThrowable() {
		return tossed;
	}

	public String getLoggedThrowableString() {
		if (tossed == null) {
			return null;
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tossed.printStackTrace(pw);
		String stack = sw.toString();
		return " --- STACK BEGIN ---\n" + //
				stack + //
				" --- STACK END ---";
	}

	public String getLoggedClassName() {
		return className;
	}

	public long getLoggedLineNumber() {
		return classLine;
	}

	public String buildFullLogLine() {
		return String.format(//
				" +%s - [ %s @ %s : %d ] - %s", //
				stamp, facility, className, classLine, message);
	}

	public String toString() {
		return message;
	}
}
