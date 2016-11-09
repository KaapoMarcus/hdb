package com.netease.backend.db.common.proc;

public interface ServiceTask extends Runnable {
	
	String getName();
	
	
	void close();
}
