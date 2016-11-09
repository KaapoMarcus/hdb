package com.netease.concurrent;

import java.util.concurrent.CountDownLatch;


class Subthread<T> implements Runnable {
	private Subtask<T> task; 
	private T result;
	private Exception exception;
	private CountDownLatch latch;
	
	public Subthread(Subtask<T> task, CountDownLatch latch) {
		this.task = task;
		this.latch = latch;
	}
	
	public void run() {
		exception = null;
		try {
			result = task.execute();
		} catch (Exception e) {
			exception = e;
		} finally {
			latch.countDown();
		}
	}

	public Exception getException() {
		return exception;
	}

	public T getResult() {
		return result;
	}
}
