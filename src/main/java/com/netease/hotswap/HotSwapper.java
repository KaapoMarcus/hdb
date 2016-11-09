package com.netease.hotswap;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;


public class HotSwapper {
	
	private static final Logger logger = Logger.getLogger(HotSwapper.class);

	
	private static final long waitForCloseCount = 10;
	
	private long waitForCloseEachTime = 100;
	
	private SwapObject workingObject = null;
	
	private SwapObject oldObject = null;
	
	private int workingRef = 0;
	
	private int oldRef = 0;
	
	private volatile boolean isInForce = false;
	
	
	private ReentrantLock lock = new ReentrantLock(true);

	
	public void setTimeout(long timeout) throws HotSwapException {
		if (timeout <= 0) 
			throw new HotSwapException("timeout must be positive");
		lock.lock();
		try {
			waitForCloseEachTime = timeout / waitForCloseCount;
		}
		finally
		{
			lock.unlock();
		}
	}

	public long getTimeout() {
		return waitForCloseEachTime * waitForCloseCount;
	}

	
	public HotSwapper(SwapObject o) {
		if (o == null) 
			throw new NullPointerException("Working object can not be null");
		
		workingObject = o;
	}

	
	public int forceSwap(SwapObject newObject) {
		try {
			return doSwap(newObject, true);
		} catch (HotSwapException e) {
			
			e.printStackTrace();
			return 0;
		}
	}

	
	public int swap(SwapObject newObject) throws HotSwapException {
		return doSwap(newObject, false);
	}
	
	
	public SwapObject getSwapObjectRef() {
		lock.lock();
		try {
			workingRef++;
			return workingObject;
		}
		finally
		{
			lock.unlock();
		}
	}

	public int getSwapObjectRefCount(SwapObject o){
		if(o == null){
			return 0;
		}
		lock.lock();
		try{
			if(workingObject == o){
				return workingRef;
			}
			if(oldObject == o){
				return oldRef;
			}
			return 0;
		}finally{
			lock.unlock();
		}
	}
	
	
	public int releaseSwapObjectRef(SwapObject o) {
		if(o == null){
			return -1;
		}
		SwapObject reservedObject = null;
		lock.lock();
		try {
			if (o == workingObject) {
				workingRef--;
				return workingRef;
			} else {
				if (o == oldObject) {
					oldRef--;
					if ((oldRef > 0) || (workingObject == null))
						return oldRef;
					else {
						reservedObject = oldObject;
						oldObject = null;
					}
				} else
					return -1;
			}
		}
		finally
		{
			lock.unlock();
		}
		
		try {
			if(!isInForce)
				reservedObject.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("Old component " + reservedObject.toString() + " has been closed");
		return 0;
	}
	
	
	public SwapObjectHandle getSwapObjectHandle() {
		lock.lock();
		try {
			workingRef++;
			return new SwapObjectHandle(this, workingObject);
		}
		finally
		{
			lock.unlock();
		}
	}

	
	public boolean forceClose() {
		try {
			return doClose(true);
		} catch (HotSwapException e) {
			logger.error("logic error");
			return false;
		}
	}

	
	public void close() throws HotSwapException {
		doClose(false);
	}
	
	private synchronized boolean doClose(boolean force) throws HotSwapException {
		boolean closeSucc = true;
		SwapObject o = null;
		lock.lock();
		o = oldObject;
		
		
		if(force)
			isInForce = true;
		lock.unlock();
		
		try{
			if (o != null) {
				int count = 0;
				
				while ((oldRef > 0) && (count < waitForCloseCount)) {
					try {
						count++;
						Thread.sleep(waitForCloseEachTime);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				if (!force && oldRef > 0) {
					throw new HotSwapException("Close " + o.toString()
							+ " timeout, it has " + oldRef + " references");
				} else {
					if (force) {
						try {
							o.close();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							closeSucc = false;
						}
					}
				}
			}

			lock.lock();
			o = workingObject;
			lock.unlock();

			if (o != null) {
				int count = 0;
				while ((workingRef > 0) && (count < waitForCloseCount)) {
					try {
						count++;
						Thread.sleep(waitForCloseEachTime);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}

				if (!force && workingRef > 0) {
					throw new HotSwapException("Close " + o.toString()
							+ " timeout, it has " + workingRef + " references");
				}

				try {
					o.close();

					lock.lock();
					workingObject = null;
					lock.unlock();

				} catch (Exception e) {
					closeSucc = false;
					logger.error(e.getMessage(), e);
					if (!force) {
						throw new HotSwapException(e.getMessage(), e);
					}
				}
			}
			return oldRef == 0 && workingRef == 0 && closeSucc;
		}
		finally{
			isInForce = false;
		}
	}
	
	private synchronized int doSwap(SwapObject newObject, boolean force) throws HotSwapException {

		logger.info("Switch to " + newObject.toString());
		SwapObject reservedObject = null;
		lock.lock();
		reservedObject = oldObject;
		if(force)
			isInForce = true;
		lock.unlock();
		
		try{
			if (reservedObject != null) {
				int count = 0;
				while ((oldRef > 0) && (count < waitForCloseCount)) {
					try {
						count++;
						Thread.sleep(waitForCloseEachTime);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
				}
				if (!force && oldRef > 0) {
					throw new HotSwapException("Close " + oldObject.toString()
							+ " timeout, it has " + oldRef + " references");
				}
				else{
					if(force){
						try {
							reservedObject.close();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
			
			lock.lock();
			oldObject = workingObject;
			workingObject = newObject;
			oldRef = workingRef;
			workingRef = 0;
			int tmpRef = oldRef;
			lock.unlock();
			
			if (tmpRef > 0)
				return tmpRef;
			
			
			try {
				if(oldObject != null){
					logger.info("Old component " + oldObject.toString() + " would been closed");
					oldObject.close();
				}
			} catch (Exception e) {
				if(force){
					logger.error("fail to close " + oldObject.toString());
					logger.error(e.getMessage(), e);
				}
				else
					throw new HotSwapException(e.getMessage(), e);
			}
			oldObject = null;
			return 0;
		}
		finally{
			isInForce = false;
		}
	}
}
