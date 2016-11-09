package com.netease.backend.db.common.proc;

import java.util.concurrent.TimeUnit;


public interface Proc {

	
	String getName();

	
	boolean shutdown();

	
	boolean startup();

	
	boolean isRunning();

	
	boolean awaitTermination(
		long timeout, TimeUnit unit)
		throws InterruptedException;

}
