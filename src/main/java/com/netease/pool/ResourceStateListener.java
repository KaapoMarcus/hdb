package com.netease.pool;


public interface ResourceStateListener<T extends Resource<?>> {
	
	void onUse(T resource);

	
	void onRelease(T resource);
	
	
	void onCreate(T resource);
	
	
	void onDispose(T resource);
}
