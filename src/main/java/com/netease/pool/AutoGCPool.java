package com.netease.pool;

import java.util.ArrayList;

import com.netease.util.DList.DLink;


public class AutoGCPool<T extends Resource<?>, A> extends Pool<T, A> {
	private PoolGcTask gcTask;
	
	private int	gcTimes;
	
	private int numGcResources;

	public AutoGCPool(Pool<T, A> parent, String name, AutoGCPoolSetting<T, A> settings) {
		super(parent, name, settings);
		gcTask = new PoolGcTask(this);
		gcTask.start();
	}

	public int getReserveSize() {
		return ((AutoGCPoolSetting<T, A>)settings).reserveSize;
	}

	public void setReserveSize(int reserveSize) {
		((AutoGCPoolSetting<T, A>)settings).reserveSize = reserveSize;
	}

	public long getGcThreshold() {
		return ((AutoGCPoolSetting<T, A>)settings).gcThreshold;
	}

	
	public void setGcThreshold(long gcThreshold) {
		((AutoGCPoolSetting<T, A>)settings).gcThreshold = gcThreshold;
		
		if (((AutoGCPoolSetting<T, A>)settings).gcInterval >= gcThreshold / 5)
			setGcInterval(gcThreshold / 5 + 1);
		gcTask.wakeUp();
		synchronized(lock) {
			for (Pool<T, A> subPool: subPools.values()) {
				if (subPool instanceof AutoGCPool) {
					((AutoGCPool<?, ?>)subPool).setGcThreshold(gcThreshold);
				}
			}
		}
	}

	public long getGcInterval() {
		return ((AutoGCPoolSetting<T, A>)settings).gcInterval;
	}

	
	public void setGcInterval(long gcInterval) {
		
		if (gcInterval >= ((AutoGCPoolSetting<T, A>)settings).gcThreshold / 5)
			gcInterval = ((AutoGCPoolSetting<T, A>)settings).gcThreshold / 5 + 1;
		((AutoGCPoolSetting<T, A>)settings).gcInterval = gcInterval;
		gcTask.wakeUp();
		synchronized(lock) {
			for (Pool<T, A> subPool: subPools.values()) {
				if (subPool instanceof AutoGCPool) {
					((AutoGCPool<?, ?>)subPool).setGcInterval(gcInterval);
				}
			}
		}
	}

	
	public int getGcTimes() {
		return gcTimes;
	}

	
	public int getNumGcResources() {
		return numGcResources;
	}

	
	public void gc() {
		long now = System.currentTimeMillis();
		if (size <= ((AutoGCPoolSetting<T, A>)settings).reserveSize)
			return;
		
		
		
		ArrayList<T> toClose = new ArrayList<T>();
		synchronized (lock) {
			DLink<T> e, n;
			for (e = freeList.getHeader().getNext(), n = e.getNext(); e != freeList.getHeader(); e = n, n = n.getNext()) {
				T r = e.get();
				if (now - r.getATime() > getGcThreshold()) {
					remove(r);
					toClose.add(r);
				}
				if (size <= ((AutoGCPoolSetting<T, A>)settings).reserveSize)
					break;
			}
		}
		for (T r: toClose) {
			r.dispose();
			numGcResources++; 
		}
		gcTimes++;
	}
}
