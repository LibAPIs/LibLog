package com.mclarkdev.tools.liblog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.mclarkdev.tools.liblog.LibLog.LogWriter;

public class LibLogFileWriter implements LogWriter {

	public static String getTime() {
		return LibLogMessage._DFORMAT.format(Calendar.getInstance().getTime());
	}

	private static final long _24H = (24 * 60 * 60 * 1000);

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
		}, timeTillRotate(), _24H);
	}

	public static final File getLogDir() {
		return logDir;
	}

	private static PrintWriter newLog(String log) {

		File f = new File(logDir, String.format("%s-%s.log", //
				getTime().substring(0, 8), log));

		try {

			return (new PrintWriter(new FileWriter(f, true), true));
		} catch (IOException e) {

			throw new RuntimeException(e);
		}
	}

	private static void rollLogs() {
		for (Map.Entry<String, PrintWriter> entry : logFiles.entrySet()) {
			rollLog(entry.getKey());
		}
	}

	private static PrintWriter rollLog(String log) {
		PrintWriter stream = newLog(log);
		logFiles.put(log, stream);
		return stream;
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

	@Override
	public void write(LibLogMessage message) {

		String facility = message.getLoggedFacility();
		PrintWriter out = logFiles.get(facility);
		if (out == null) {
			out = rollLog(facility);
		}

		String logLine = message.buildFullLogLine();

		synchronized (out) {
			out.println(logLine);
		}
	}
}
