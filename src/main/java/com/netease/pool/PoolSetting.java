package com.netease.pool;


public class PoolSetting<T extends Resource<?>, A> {
	
	protected A createArg;

	
	protected Factory<T, A> factory;

	
	protected int maxSize;
	
	
	protected int maxPendingCreateRequest;
	
	
	protected ReqSchedulePolicy schedulePolicy;

	
	public PoolSetting(A createArg, Factory<T, A> factory) {
		this.createArg = createArg;
		this.factory = factory;
		this.maxSize = Integer.MAX_VALUE;
		this.schedulePolicy = ReqSchedulePolicy.DEFAULT;
	}
	
	public PoolSetting(A createArg, Factory<T, A> factory, int maxSize, ReqSchedulePolicy schedulePolicy) {
		this.createArg = createArg;
		this.factory = factory;
		this.maxSize = maxSize;
		this.schedulePolicy = schedulePolicy;
		maxPendingCreateRequest = Integer.MAX_VALUE;
	}
	
	public PoolSetting(A createArg, Factory<T, A> factory, int maxSize, int maxPendingCreateRequest, ReqSchedulePolicy schedulePolicy) {
		this.createArg = createArg;
		this.factory = factory;
		this.maxSize = maxSize;
		this.schedulePolicy = schedulePolicy;
		this.maxPendingCreateRequest = maxPendingCreateRequest;
	}


	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new PoolSetting<T, A>(createArg, factory, maxSize, schedulePolicy);
	}
	
	
	public A getArg(){
		return createArg;
	}
}
