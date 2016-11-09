package com.netease.backend.db.common.proc;

import org.apache.log4j.Logger;


public abstract class ProcLauncherBase<T extends Proc> implements
		ProcLauncher<T> {

	public void launch(
		T proc)
	{
		
		final Logger runtimeLogger = Logger.getLogger(this.getClass());
		
		if (proc == null)
			throw new NullPointerException("Procedure must not be null");
		if (runtimeLogger == null)
			throw new NullPointerException(
					"Procedure logger should not be null");
		
		final String name = proc.getName();
		boolean acted = false;
		try {
			
			this.initBeforeLaunching();
			
			runtimeLogger.info("����" + name + "����");
			
			proc.startup();
			
			acted = true;
		} catch (final Throwable ex) {
			runtimeLogger.fatal(name + "����ʱ�������ش���", ex);
		} finally {
			runtimeLogger.info(name + "����" + (acted ? "�ɹ�" : "ʧ�ܣ�"));
		}
	}

	
	private void initBeforeLaunching()
		throws Exception
	{
		
		this.initAction();
	}

	
	protected void initAction()
		throws Exception
	{
		
	}

}
