package com.netease.backend.db.common.proc;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


public abstract class SimpleProcBase extends Thread implements Proc {

	
	private Logger	logger	= null;
	
	public SimpleProcBase() {
		super();
		this.setDaemon(false);
	}

	public SimpleProcBase(String name, boolean deamon) {
		super(name);
		this.setDaemon(deamon);
	}

	
	protected Logger getLogger() {
		if (logger == null) {
			
			final Class<? extends SimpleProcBase> runtimeClass = this
					.getClass();
			synchronized (runtimeClass) {
				if (logger == null) {
					logger = Logger.getLogger(runtimeClass);
				}
			}
		}
		return logger;
	}

	
	@Override
	public void start() {
		this.startup();
	}

	public boolean startup() {
		final String name = this.getName();

		
		final Logger logger = this.getLogger();
		if (!this.isAlive()) {
			synchronized (this) {
				if (!this.isAlive()) {
					logger.info("����" + name + "����");
					
					
					try {
						super.start();
					} catch (final IllegalThreadStateException ex) {
						logger.error(name + "�������л����˳���");
						return false; 
					}
					return true;
				}
			}
		}
		return false;
	}

	public boolean shutdown() {
		final String name = this.getName();

		
		final Logger logger = this.getLogger();
		synchronized (this) {
			logger.info("�ر�" + name + "����");
			this.invokeShutdown();
			return true;
		}
	}

	
	protected void invokeShutdown() {
		this.interrupt();
	}

	public boolean awaitTermination(
		long timeout, TimeUnit unit)
		throws InterruptedException
	{
		this.join(unit.toMillis(timeout));
		return true;
	}

}
