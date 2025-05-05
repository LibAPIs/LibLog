package com.mclarkdev.tools.liblog.writer;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mclarkdev.tools.liblog.lib.LibLogMessage;
import com.mclarkdev.tools.liblog.lib.LibLogStream;
import com.mclarkdev.tools.liblog.lib.LibLogWriter;

public class LibLogCachedLogWriter extends LibLogWriter {

	protected final LibLogStream stream;

	protected final Queue<String> messageCache;

	public LibLogCachedLogWriter(URI uri, LibLogStream stream) {
		super(uri);

		// The log stream
		this.stream = stream;

		// Setup log cache
		this.messageCache = new ConcurrentLinkedQueue<>();

		// Cache flushing thread
		new Thread() {
			public void run() {
				setName(String.format(//
						"LibLogCachedWriter:LogSend (%s)", uri.toString()));

				while (!isInterrupted()) {
					flush();
					yield();
				}
			}
		}.start();
	}

	/**
	 * Returns the current number of messages in the cache.
	 * 
	 * @return size of the cache
	 */
	public int cacheSize() {
		return messageCache.size();
	}

	/**
	 * Add a message to the cache.
	 * 
	 * @param logLine the message to cache
	 */
	public void cache(String logLine) {

		synchronized (messageCache) {
			messageCache.add(logLine);
		}
	}

	/**
	 * Implemented method to flush all messages to their destination.
	 * 
	 * @return all messages flushed
	 */
	public synchronized boolean flush() {

		while (!messageCache.isEmpty()) {
			if (!stream.write(messageCache.peek())) {
				return false;
			}
			messageCache.remove();
		}

		System.gc();
		return true;
	}

	/**
	 * Basic writer, build and cache the log line.
	 */
	@Override
	public void write(LibLogMessage message) {
		String logLine = (debug) ? //
				message.buildDebugLine() : message.buildLogLine();

		cache(logLine);
	}
}
