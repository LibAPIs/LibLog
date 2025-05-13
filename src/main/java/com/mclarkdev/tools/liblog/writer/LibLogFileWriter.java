package com.mclarkdev.tools.liblog.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.liblog.lib.LibLogMessage;
import com.mclarkdev.tools.liblog.lib.LibLogWriter;

/**
 * LibLog // LibLogFileWriter
 * 
 * Writes messages to a series of log files.
 */
public class LibLogFileWriter extends LibLogWriter {

	public static String scheme() {
		return "file";
	}

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

	public LibLogFileWriter(URI uri) {
		super(uri);

		String dir = uri.getPath();

		logPath = (dir.equals("/") ? "logs" : dir);

		logDir = (new File(logPath));
		logDir.mkdirs();

		logFiles = new HashMap<>();

		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				closeLogs();
			}
		}, timeUntilRotate(), _1D);
	}

	public final File getLogDir() {
		return logDir;
	}

	public void closeLog(String facility) {

		PrintWriter log = logFiles.get(facility);
		synchronized (log) {
			logFiles.remove(facility);
			log.close();
		}
	}

	public void closeLogs() {
		for (Map.Entry<String, PrintWriter> entry : logFiles.entrySet()) {
			closeLog(entry.getKey());
		}
	}

	private PrintWriter newLog(String name) {

		File logFile = new File(logDir, //
				String.format("%s-%s.log", //
						getTime().substring(0, 8), name));

		try {

			PrintWriter stream = (new PrintWriter(//
					new FileWriter(logFile, true), true));
			logFiles.put(name, stream);
			return stream;
		} catch (IOException e) {

			throw LibLog.log("logger", //
					"Failed to write to file.", e).asException();
		}
	}

	@Override
	public void write(LibLogMessage message) {

		// Get the output stream
		String facility = message.getLoggedFacility();
		PrintWriter out = logFiles.get(facility);
		out = (out != null) ? out : newLog(facility);

		// Build the log line
		String logLine = (debug) ? //
				message.buildDebugLine() : message.buildLogLine();

		// Write to disk
		synchronized (out) {
			out.println(logLine);
		}
	}

	@Override
	public void shutdown() {
		for (Map.Entry<String, PrintWriter> entry : logFiles.entrySet()) {
			entry.getValue().close();
			logFiles.remove(entry.getKey());
		}
	}

	private static long timeUntilRotate() {

		Duration untilMidnight = Duration.between(LocalDateTime.now(),
				LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());
		return untilMidnight.toMillis();
	}
}
