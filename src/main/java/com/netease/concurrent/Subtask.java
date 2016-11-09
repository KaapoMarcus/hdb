package com.netease.concurrent;


public interface Subtask<T> {
	T execute() throws Exception;
	void cancel();
}
