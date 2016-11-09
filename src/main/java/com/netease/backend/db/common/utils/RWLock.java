
package com.netease.backend.db.common.utils;

import java.io.Serializable;



public class RWLock implements Serializable {

	private static final long serialVersionUID = 7098316185650236000L;
	
	private boolean waitingWriter;
    private int givenLocks;
    private long readAcquireCount;
    private long readReleaseCount;
    private long writeAcquireCount;
    private long writeReleaseCount;
    private class Mutex implements Serializable {

			private static final long serialVersionUID = -8841938132646591927L;
    	}
    Mutex mutex;
    
    long timeout = 10000;
    		
    	
    	boolean checkTimeout = false;
    	
    	
    transient 	TimeOutCheckThread timeOutThread = null;
    
    public RWLock () {
        waitingWriter = false;
        givenLocks = 0;
        mutex = new Mutex();
    }
    
    public void getReadLock() {
        long begin = System.currentTimeMillis();
        
        synchronized (mutex) {
            while (waitingWriter || givenLocks == -1) {
                try {
                    mutex.wait();
                } catch (InterruptedException ie) {
                    if ((System.currentTimeMillis() - begin) > timeout) {
                        
                    }
                }
            }
            
            givenLocks++;
            readAcquireCount++;
        }
    }
    
    
    public void getWriteLock() {
        long begin = System.currentTimeMillis();

        synchronized (mutex) {
            waitingWriter = true;
            while (givenLocks != 0) {
                try {
                    mutex.wait();
                } catch (InterruptedException ie) {
                    if ((System.currentTimeMillis() - begin) > timeout) {
                        
                    }
                }
            }
            waitingWriter = false;
            givenLocks = -1;
            writeAcquireCount++;
        }
    }
    
    
	public void getWriteLock(long timeout)
    {
		long begin = System.currentTimeMillis();

		synchronized (mutex) 
		{
			waitingWriter = true;
			while (givenLocks != 0) 
			{
				try 
				{
					mutex.wait();
				} catch (InterruptedException ie) 
				{
					if ((System.currentTimeMillis() - begin) > timeout)
					{
						
					}
				}
			}
			waitingWriter = false;
			givenLocks = -1;
			writeAcquireCount++;
			
			
			checkTimeout = true;
			
			
			if(timeout > 0)
			{
				timeOutThread = new TimeOutCheckThread(TimeOutCheckThread.WRITE_LOCK_TIMEOUT, timeout,this);
				timeOutThread.start();
			}
        }
    }
	
    
    public void releaseLock() {
        synchronized (mutex) {
            if (givenLocks == 0) { 
                return;
            } else if (givenLocks == -1) { 
                givenLocks = 0;
                writeReleaseCount++;
            } else { 
                givenLocks--;
                readReleaseCount++;
            }
            
            mutex.notifyAll();
        }
    }
    
    
	public void releaseLockWithTimeout()
    {
		synchronized (mutex) 
		{
			if (givenLocks == -1 && checkTimeout == true) 
			{ 
				givenLocks = 0;
				writeReleaseCount++;
				checkTimeout = false;
				
				
				if(timeOutThread != null && !Thread.currentThread().equals(timeOutThread))					
					timeOutThread.interrupt();
				mutex.notifyAll();
			}	
        }
    }

	public int getGivenLocks() {
		return givenLocks;
	}

	public long getReadAcquireCount() {
		return readAcquireCount;
	}

	public long getReadReleaseCount() {
		return readReleaseCount;
	}

	public long getWriteAcquireCount() {
		return writeAcquireCount;
	}

	public long getWriteReleaseCount() {
		return writeReleaseCount;
	}
	
	public static void main(String[] agrs)
	{
		RWLock lock = new RWLock();
		lock.getWriteLock(1000);
		lock.releaseLockWithTimeout();
	}
}
