package com.netease.backend.db.common.utils;

import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;


public class AppSingletonSupport {

	
	private final String		launchDir;
	
	private volatile FileLock	lock	= null;

	
	public AppSingletonSupport(
		String launchDir)
	{
		if (launchDir == null)
			throw new NullPointerException();
		this.launchDir = launchDir;
	}

	
	public synchronized boolean activated()
		throws IOException
	{
		for (;;) {
			if ((lock != null) && lock.isValid())
				
				return true;

			
			
			try {
				lock = FileLockHelper.tryLockExclusively(launchDir);
			} catch (final OverlappingFileLockException ex) {
				ex.printStackTrace();
				
				
				return false;
			}
			if (lock == null)
				
				return false;
		}
	}

	
	public boolean isActive() {
		final FileLock lock = this.lock;
		return (lock != null) && lock.isValid();
	}

	
	public void deactivate()
		throws IOException
	{
		final FileLock lock = this.lock;
		if (lock != null && lock.isValid()) {
			lock.release();
		}
	}

}
