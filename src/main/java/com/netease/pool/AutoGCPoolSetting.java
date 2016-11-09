package com.netease.pool;


public class AutoGCPoolSetting<T extends Resource<?>, A> extends PoolSetting<T, A> {
	
	long gcThreshold;
	
	long gcInterval;
	
	int reserveSize;

	
	public AutoGCPoolSetting(A createArg, Factory<T, A> factory, long gcThreshold) {
		super(createArg, factory);
		this.gcThreshold = gcThreshold;
		this.gcInterval = gcThreshold / 5;
		this.reserveSize = 0;
	}
	
	public AutoGCPoolSetting(A createArg, Factory<T, A> factory, int maxSize, ReqSchedulePolicy schedulePolicy, int reserveSize, long gcThreshold, long gcInterval) {
		super(createArg, factory, maxSize, schedulePolicy);
		this.gcThreshold = gcThreshold;
		if (gcInterval > gcThreshold / 5)
			gcInterval = gcThreshold / 5;
		this.gcInterval = gcInterval;
		this.reserveSize = reserveSize;
	}
	
	public AutoGCPoolSetting(A createArg, Factory<T, A> factory, int maxSize, int maxPendingCreateRequests, ReqSchedulePolicy schedulePolicy, int reserveSize, long gcThreshold, long gcInterval) {
		super(createArg, factory, maxSize, maxPendingCreateRequests, schedulePolicy);
		this.gcThreshold = gcThreshold;
		if (gcInterval > gcThreshold / 5)
			gcInterval = gcThreshold / 5;
		this.gcInterval = gcInterval;
		this.reserveSize = reserveSize;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new AutoGCPoolSetting<T, A>(createArg, factory, maxSize, schedulePolicy, reserveSize, gcThreshold, gcInterval);
	}
}
