package com.netease.backend.db.common.utils;



public class TimeOutCheckThread extends Thread {
	
	
	public static final byte WRITE_LOCK_TIMEOUT = 1;
	
	
	private byte cmd;
	
	
	private long timeout = 0;
	
	
	private Object paramOne = null;
	
	
	public TimeOutCheckThread(byte command, long timeout,Object obj)
	{
		this.cmd = command;
		this.timeout = timeout;
		this.paramOne = obj;
	}
	
	
	public void run()
	{
		if(timeout <= 0)
			return;
		
		switch (cmd)
		{
			case WRITE_LOCK_TIMEOUT:
				
				WriteLockTimeOut();
				break;
			default:
				return;
		}
	}
	
	
	private void WriteLockTimeOut()
	{
		
		if(paramOne == null)
			return;
		RWLock lock = (RWLock)paramOne;
		
		
		try
		{
			Thread.sleep(timeout);
		}catch(InterruptedException ie)
		{
			return;
		}
		
		
		lock.releaseLockWithTimeout();
	}

}
