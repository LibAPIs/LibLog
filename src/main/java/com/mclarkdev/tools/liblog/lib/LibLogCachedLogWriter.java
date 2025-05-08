package com.mclarkdev.tools.liblog.lib;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * LibLog // LibLogCachedLogWriter
 */
public abstract class LibLogCachedLogWriter extends LibLogWriter {

	protected final LibLogStream stream;

	protected final int messageCacheMax;

	protected final Queue<String> messageCache;

	private long messagesDropped = 0;

	public LibLogCachedLogWriter(URI uri, LibLogStream stream) {
		super(uri);

		// The log stream
		this.stream = stream;

		// Setup log cache
		this.messageCache = new ConcurrentLinkedQueue<>();
		this.messageCacheMax = 500000;

		// Cache flushing thread
		new Thread() {
			public void run() {
				setName(String.format(//
						"LibLogCachedWriter:LogSend (%s)", uri.toString()));

				while (!isInterrupted()) {
					flush();
					Thread.yield();
				}
			}
		}.start();
	}

	/**
	 * Returns the current number of messages in the cache.
	 * 
	 * @return size of the cache
	 */
	public int getMessagesCached() {
		return messageCache.size();
	}

	/**
	 * Returns the number of messages dropped from the cached.
	 * 
	 * @return number of messages dropped
	 */
	public long getMessagesDropped() {
		return messagesDropped;
	}

	/**
	 * Add a message to the cache.
	 * 
	 * @param logLine the message to cache
	 */
	public void cache(String logLine) {

		synchronized (messageCache) {
			messageCache.add(logLine);
			if (messageCache.size() > messageCacheMax) {
				messageCache.remove();
				messagesDropped++;
			}
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
