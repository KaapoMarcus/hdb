package com.netease.backend.db.common.utils;

import java.util.concurrent.atomic.AtomicInteger;


public final class InitHelper {

	
	private final AtomicInteger	initd	= new AtomicInteger(0);

	
	public void reset() {
		initd.set(0);
	}

	
	public boolean initd() {
		return initd.getAndSet(2) != 2;
	}

	
	public boolean tryInit() {
		return initd.compareAndSet(0, 1);
	}

	
	public boolean isInitd() {
		return initd.get() == 2;
	}

}
