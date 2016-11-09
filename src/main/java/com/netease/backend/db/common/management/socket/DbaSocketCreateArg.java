
package com.netease.backend.db.common.management.socket;


public class DbaSocketCreateArg {

	private String master;
	private int port;
	private int connectTimeout;
	private int soTimeOut;
	private int retryTimes;

	
	public DbaSocketCreateArg(String master, int port, int connectTimeout, int soTimeout, int retryTimes) {
		this.master = master;
		this.port = port;
		this.connectTimeout = connectTimeout;
		this.soTimeOut = soTimeout;
		this.retryTimes = retryTimes;
	}

	
	public String getMaster() {
		return master;
	}

	
	public int getPort() {
		return port;
	}

	
	public int getConnectTimeout() {
		return connectTimeout;
	}

	
	public int getSoTimeOut() {
		return soTimeOut;
	}

	
	public int getRetryTimes() {
		return retryTimes;
	}
}
