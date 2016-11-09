package com.netease.backend.db.common.proc;


public interface ProcLauncher<T extends Proc> {

	
	void launch(
		T proc);

}
