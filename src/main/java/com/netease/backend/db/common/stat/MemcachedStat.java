package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;


public class MemcachedStat implements Serializable {
	private static final long serialVersionUID = -7297576019546606435L;

	
	private AtomicLong getRecordCount;

	
	private AtomicLong missRecordCount;

	
	private AtomicLong setRecordCount;

	
	private AtomicLong setDataSize;

	
	private AtomicLong delRecordCount;
	
	public MemcachedStat() {
		this.getRecordCount = new AtomicLong(0);
		this.missRecordCount = new AtomicLong(0);
		this.setRecordCount = new AtomicLong(0);
		this.setDataSize = new AtomicLong(0);
		this.delRecordCount = new AtomicLong(0);
	}

	
	public void addGetRecordCount(int getCount, int missCount) {
		this.getRecordCount.addAndGet(getCount);
		this.missRecordCount.addAndGet(missCount);
	}

	
	public void addSetRecordSize(int setCount, long dataSize) {
		this.setRecordCount.addAndGet(setCount);
		this.setDataSize.addAndGet(dataSize);
	}

	
	public void addDelRecordCount(int delCount) {
		this.delRecordCount.addAndGet(delCount);
	}

	
	public void merge(MemcachedStat s) {
		this.delRecordCount.addAndGet(s.getDelRecordCount());
		this.getRecordCount.addAndGet(s.getGetRecordCount());
		this.missRecordCount.addAndGet(s.getMissRecordCount());
		this.setRecordCount.addAndGet(s.getSetRecordCount());
		this.setDataSize.addAndGet(s.getSetDataSize());
	}
	
	@Override
	public Object clone() {
		MemcachedStat copy = new MemcachedStat();
		copy.merge(this);
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Memcached Stat ");
		builder.append("getCount:").append(getRecordCount.get());
		builder.append(", missCount:").append(missRecordCount.get());
		builder.append(", setCount:").append(setRecordCount.get());
		builder.append(", setSize:").append(setRecordCount.get());
		builder.append(", delCount:").append(delRecordCount.get());
		return builder.toString();
	}
	
	public long getGetRecordCount() {
		return getRecordCount.get();
	}

	public long getMissRecordCount() {
		return missRecordCount.get();
	}

	public long getSetRecordCount() {
		return setRecordCount.get();
	}

	public long getSetDataSize() {
		return setDataSize.get();
	}

	public long getDelRecordCount() {
		return delRecordCount.get();
	}
}
