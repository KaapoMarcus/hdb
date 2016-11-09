package com.netease.pool;


public interface Factory<T extends Resource<?>, A> {
	
	T createResource(A arg, Pool<?, ?> pool) throws Exception;
}
