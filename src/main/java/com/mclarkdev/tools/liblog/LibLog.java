package com.mclarkdev.tools.liblog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class LibLog {

	private static final SimpleDateFormat _DFORMAT = //
			new SimpleDateFormat("YYYYMMdd HH:mm:ss");

	public static String getTime() {
		return _DFORMAT.format(Calendar.getInstance().getTime());
	}

	private static final File logDir;
	private static final String logPath;

	private static final HashMap<String, PrintWriter> logFiles;

	static {

		String dir = System.getenv("_LOGDIR");
		logPath = (dir == null) ? "logs" : dir;

		logDir = (new File(logPath));
		logDir.mkdirs();

		logFiles = new HashMap<>();

		rollLogs();
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				rollLogs();
			}
		}, timeTillRotate(), 24 * 60 * 60 * 1000);
	}

	private static void rollLogs() {

		for (Map.Entry<String, PrintWriter> entry : logFiles.entrySet()) {

			try {

				// close open and create new
				entry.getValue().close();
				newLog(entry.getKey());
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	private static PrintWriter newLog(String log) {

		File f = new File(logDir, String.format("%s-%s.log", //
				getTime().substring(0, 8), log));

		try {

			PrintWriter p = new PrintWriter(new FileWriter(f, true), true);
			logFiles.put(log, p);
			return p;
		} catch (IOException e) {

			throw new RuntimeException(e);
		}
	}

	public static final File getLogDir() {
		return logDir;
	}

	public static void log(String message) {
		log("server", message, null);
	}

	public static void log(String message, Throwable e) {
		log("server", message, e);
	}

	public static void log(String log, String message) {
		log(log, message, null);
	}

	public static void log(String log, String message, Throwable e) {

		String timeNow = _DFORMAT.format(Calendar.getInstance().getTime());
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		// get call trace
		String fullClassName;
		int lineNumber;
		for (int x = 1;; x++) {

			fullClassName = trace[x].getClassName();

			// first occurrence of not LibLog
			if (!fullClassName.equals(LibLog.class.getName())) {

				lineNumber = trace[x].getLineNumber();
				break;
			}
		}

		// build message
		String baseLogLine = " +";
		baseLogLine += timeNow;
		baseLogLine += " - [ ";
		baseLogLine += log;
		baseLogLine += " @ ";
		baseLogLine += fullClassName;
		baseLogLine += " : ";
		baseLogLine += lineNumber;
		baseLogLine += " ]";

		String fullLogLine = baseLogLine + " - " + message;

		PrintWriter out = logFiles.get(log);
		if (out == null) {
			out = newLog(log);
		}
		synchronized (out) {

			out.println("O:" + fullLogLine);

			// print stack trace if applicable
			if (e != null) {

				// generate an error uuid
				String errorUUID = UUID.randomUUID().toString();

				// print error hash to out
				out.println("O:" + baseLogLine + " - " + "Error UUID [ " + errorUUID + " ]");

				// print error message and error uuid to err
				out.println("E:" + fullLogLine);
				out.println("E:" + baseLogLine + " E " + "Error UUID [ " + errorUUID + " ]");

				// print the stack track
				e.printStackTrace(out);
			}
		}
	}

	private static long timeTillRotate() {

		Calendar midnight = Calendar.getInstance();
		midnight.set(Calendar.HOUR_OF_DAY, 0);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.SECOND, 0);
		midnight.set(Calendar.MILLISECOND, 1);
		midnight.set(Calendar.DAY_OF_YEAR, midnight.get(Calendar.DAY_OF_YEAR) + 1);
		return midnight.getTimeInMillis() - System.currentTimeMillis() - 1;
	}
}
