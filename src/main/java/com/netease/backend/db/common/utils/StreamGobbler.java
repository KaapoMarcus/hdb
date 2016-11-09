package com.netease.backend.db.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.ClosedByInterruptException;

import org.apache.log4j.Logger;

public class StreamGobbler extends Thread {
	static Logger debugLogger = Logger.getLogger(StreamGobbler.class);

	
	private static final byte STATUS_ON = 1;

	
	private static final byte STATUS_STOPPING = 2;

	
	private static final byte STATUS_DOWN = 3;

	
	private byte status;

	
	private boolean isSplitLine = false;

	InputStream is;

	BufferedReader br;

	
	Logger logger;

	
	String msg = "";

	
	String type;

	public StreamGobbler(InputStream is, String type, Logger log) {
		this.is = is;
		InputStreamReader isr = new InputStreamReader(is);
		br = new BufferedReader(isr);
		this.type = type;
		this.status = STATUS_ON;
		this.logger = log;
	}

	
	public StreamGobbler(InputStream is, String type, Logger log, boolean isSplitLine) {
		this(is, type, log);
		this.isSplitLine = isSplitLine;
	}

	public void stopService() {
		if (this.status == STATUS_DOWN)
			return;
		this.status = STATUS_STOPPING;
		try {
			this.interrupt();
			if (is != null)
				is.close();
			this.join();
			this.status = STATUS_DOWN;
		} catch (Exception e) {
		}
	}

	public void run() {
		try {

			String line = null;
			while (status == STATUS_ON && (line = br.readLine()) != null) {
				if (this.isInterrupted())
					throw new ClosedByInterruptException();
				if (logger != null)
					synchronized (logger) {
						logger.info(type + "> " + line);
					}
				if (isSplitLine) {
					msg += line + "\n";
				} else {
					msg += line;
				}
			}
		} catch (ClosedByInterruptException cbie) {
			debugLogger.debug("StreamGobbler is normally interruptted!");
		} catch (IOException ioe) {
			debugLogger.error("IOException: " + ioe.getMessage());
		} finally {
			try {
				is.close();
				debugLogger.debug("StreamGobbler exit!");
			} catch (Exception e) {
			}
		}
	}

	public String getMsg() {
		return msg;
	}

}
