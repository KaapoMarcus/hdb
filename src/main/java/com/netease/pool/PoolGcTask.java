package com.netease.pool;



public class PoolGcTask extends Thread {
	private AutoGCPool<?, ?> pool;

	public PoolGcTask(AutoGCPool<?, ?> pool) {
		this.pool = pool;
		setDaemon(true);
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				try {
					this.wait(pool.getGcInterval());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				pool.gc();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	void wakeUp() {
		synchronized (this) {
			this.notify();
		}
	}
}
