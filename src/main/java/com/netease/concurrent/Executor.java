package com.netease.concurrent;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public final class Executor<T> {
	
	private Vector<T> results;
	
	private Subtask<T>[] subTasks;
	
	private Exception[] subExceptions;
	
	private static ExecutorService es;
	
	static {
		es = Executors.newCachedThreadPool();
	}
	
	
	public Executor(Subtask<T>[] subTasks) {
		this.subTasks = subTasks;
		cleanUp();
	}

	private void cleanUp() {
		results = new Vector<T>(subTasks.length);
		for (int i = 0; i < subTasks.length; i++)
			results.add(null);
		subExceptions = new Exception[subTasks.length];
	}
	
	
	@SuppressWarnings("unchecked")
	public void parallelExecute(long timeOut, boolean inlineSingle) throws ExecException {
		cleanUp();
		if (inlineSingle && subTasks.length == 1) {
			try {
				results.set(0, subTasks[0].execute());
				subExceptions[0] = null;
			} catch (Exception e) {
				subExceptions[0] = e;
				throw new ExecException("������ִ���쳣", e);
			}
			return;
		}
		CountDownLatch latch = new CountDownLatch(subTasks.length);
		
		Subthread<T>[] subThreads = new Subthread[subTasks.length];
		for (int i = 0; i < subTasks.length; i++) {
			subThreads[i] = new Subthread<T>(subTasks[i], latch);
			es.execute(subThreads[i]);
		}
		try {
			
			if (!latch.await(timeOut, TimeUnit.MILLISECONDS)) {
				for (Subtask<T> subTask : subTasks) {
					try {
						subTask.cancel();
					} catch (Exception e) {
					}
				}
				throw new ExecException("ִ�г�ʱ");
			}
			
			Exception firstSubException = null;
			for (int i = 0; i < subTasks.length; i++) {
				results.set(i, subThreads[i].getResult());
				subExceptions[i] = subThreads[i].getException();
				if (subThreads[i].getException() != null) 
					firstSubException = subThreads[i].getException();
			}
			if (firstSubException != null) 
				throw new ExecException(firstSubException.getMessage(), firstSubException);
		} catch (ExecException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ExecException("ִ���쳣", ex);
		} 
	}
	
	
	public void serialExecute(long timeOut) throws ExecException {
		cleanUp();
		
		long start = System.currentTimeMillis();
		long remainTimeout = timeOut;
		
		Exception firstSubException = null;
		for (int i = 0; i < subTasks.length; i++) {
			Subtask<T> subTask = subTasks[i];
			CountDownLatch latch = new CountDownLatch(1);
			Subthread<T> subThread = new Subthread<T>(subTask, latch);
			es.execute(subThread);
			
			try {
				if (!latch.await(remainTimeout, TimeUnit.MILLISECONDS)) {
					try {
						subTask.cancel();
					} catch (Exception e) {
					}
					throw new ExecException("ִ�г�ʱ");
				}
			} catch (ExecException ex) {
				throw ex;
			} catch (Exception ex) {
				throw new ExecException("ִ���쳣", ex);
			}
			
			results.set(i, subThread.getResult());
			subExceptions[i] = subThread.getException();
			if (subThread.getException() != null) 
				firstSubException = subThread.getException();
			if (firstSubException != null) 
				throw new ExecException(firstSubException.getMessage(), firstSubException);
			
			remainTimeout = timeOut - (System.currentTimeMillis() - start);
		}
	}

	
	public void cancel() {
		for (Subtask<T> st : subTasks) {
			try {
				st.cancel();
			} catch (Exception e) {
			}
		}
	}
	
	
	public Subtask<T>[] getSubTasks() {
		return subTasks;
	}

	
	public Vector<T> getResults() {
		return results;
	}

	
	public Exception[] getSubExceptions() {
		return subExceptions;
	}
}
