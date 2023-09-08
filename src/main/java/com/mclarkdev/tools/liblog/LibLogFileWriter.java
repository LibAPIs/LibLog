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

	public static final long _1S = (1000);
	public static final long _1M = (_1S * 60);
	public static final long _1H = (_1M * 60);
	public static final long _1D = (_1H * 24);

	public static String getTime() {
		return LibLogMessage._DFORMAT.format(Calendar.getInstance().getTime());
	}

	private final File logDir;
	private final String logPath;

	private final HashMap<String, PrintWriter> logFiles;

	protected LibLogFileWriter(String dir) {

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
		}, timeTillRotate(), _1D);
	}

	public final File getLogDir() {
		return logDir;
	}

	private PrintWriter newLog(String name) {

		File logFile = new File(logDir, //
				String.format("%s-%s.log", //
						getTime().substring(0, 8), name));

		try {

			return (new PrintWriter(new FileWriter(logFile, true), true));
		} catch (IOException e) {

			throw new RuntimeException(e);
		}
	}

	private void rollLogs() {
		for (Map.Entry<String, PrintWriter> entry : logFiles.entrySet()) {
			rollLog(entry.getKey());
		}
	}

	private PrintWriter rollLog(String name) {
		PrintWriter stream = newLog(name);
		logFiles.put(name, stream);
		return stream;
	}

	@Override
	public void write(boolean debug, LibLogMessage message) {

		// Get the output stream
		String facility = message.getLoggedFacility();
		PrintWriter out = logFiles.get(facility);
		out = (out != null) ? out : rollLog(facility);

		// Build the log line
		String logLine = (debug) ? //
				message.buildDebugLine() : message.buildLogLine();

		// Write to disk
		synchronized (out) {
			out.println(logLine);
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
