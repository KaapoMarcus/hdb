package com.netease.pool;


public interface Walker<T extends Resource<?>> {
	boolean walk(T Resource);
}
