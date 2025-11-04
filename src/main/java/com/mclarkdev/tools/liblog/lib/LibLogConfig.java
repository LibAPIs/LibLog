package com.mclarkdev.tools.liblog.lib;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LibLogConfig {

	private final String defaultLog;

	private final boolean logCodes;

	private final List<URI> userLogs = new ArrayList<>();

	private final HashMap<String, Class<? extends LibLogWriter>> logHandlers;

	private final Set<LibLogWriter> logWriters;

	private final Properties logStrings = new Properties();

	private LibLogConfig() {

		// Determine default log name
		String defLog = System.getenv("LOG_NAME");
		this.defaultLog = (defLog != null) ? defLog : "server";

		// Debug if environment variable set
		this.logCodes = (System.getenv("LOG_CODES") != null);

		// Parse user requested log streams
		String logStreams = System.getenv("LOG_STREAMS");
		logStreams = (logStreams != null) ? logStreams : "console:/";
		for (String logStream : logStreams.split(";")) {
			userLogs.add(URI.create(logStream));
		}

		// Map for log handlers
		logHandlers = new HashMap<>();

		// Setup logger cache
		logWriters = ConcurrentHashMap.newKeySet();
	}

	public String defaultLog() {
		return defaultLog;
	}

	public Set<LibLogWriter> loggers() {
		return logWriters;
	}

	/**
	 * Lookup a localized string by code.
	 * 
	 * @param lookup the lookup code
	 * @return the localized string
	 */
	public String l10n(String lookup) {
		String value = (logStrings.containsKey(lookup)) ? logStrings.getProperty(lookup) : lookup;
		return (logCodes) ? String.format("%s : %s", lookup, value) : value;
	}

	/**
	 * Shutdown all log writers.
	 */
	public void shutdown() {

		// Loop and shutdown the writers
		for (LibLogWriter writer : logWriters) {
			writer.shutdown();
		}
	}

	/**
	 * Load localized strings from disk.
	 * 
	 * @param in stream to .properties file
	 * @throws IOException failed to read .properties file
	 */
	public void loadStrings(InputStream in) throws IOException {
		logStrings.load(in);
	}

	/**
	 * Register a new log handler scheme.
	 * 
	 * @param clazz the handler class
	 */
	public void registerLogger(Class<? extends LibLogWriter> clazz) {

		String scheme;
		try {
			Method method = clazz.getMethod("scheme");
			scheme = (String) method.invoke(null);
			logHandlers.put(scheme, clazz);
		} catch (Exception e) {

			// Throw logger registration failure
			throw new RuntimeException(//
					"Failed to register logger: " + clazz.getName(), e);
		}

		// Loop user URIs
		for (URI uri : userLogs) {

			// Check if log type matches
			if (uri.getScheme().equals(scheme)) {
				addLogger(uri);
			}
		}
	}

	/**
	 * Add a log receiver.
	 * 
	 * @param logURI add logger from URI
	 */
	public void addLogger(URI logURI) {

		String scheme = logURI.getScheme();

		Class<? extends LibLogWriter> clazz = logHandlers.get(scheme);

		if (clazz == null) {
			throw new IllegalArgumentException("Unknown logger scheme: " + scheme);
		}

		LibLogWriter logWriter = null;

		try {

			// Create and instantiate the logger
			Constructor<? extends LibLogWriter> ctor = //
					clazz.getConstructor(URI.class);
			logWriter = ctor.newInstance(logURI);
		} catch (Error | Exception e) {

			// Throw logger creation failure
			throw new RuntimeException(//
					"Failed to instantiate logger for scheme: " + scheme, e);
		}

		try {

			// Call the logger setup method
			logWriter.setup();
		} catch (Error | Exception e) {

			// Throw logger setup failure
			throw new RuntimeException(//
					"Failed to setup logger for scheme: " + scheme, e);
		}

		// Logger setup success, add to map
		addLogger(logWriter);
	}

	/**
	 * Add a log receiver.
	 * 
	 * @param writer a custom LogWriter
	 */
	public void addLogger(LibLogWriter writer) {

		logWriters.add(writer);
	}

	/**
	 * Remove a log receiver.
	 * 
	 * @param logger remove a custom LogWriter
	 */
	public void removeLogger(LibLogWriter logger) {
		if (logger == null) {
			logWriters.clear();
		} else {
			logWriters.remove(logger);
		}
	}

	public static LibLogConfig create() {
		return new LibLogConfig();
	}
}
