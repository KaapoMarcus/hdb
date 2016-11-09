package com.netease.backend.db.common.sql;

import java.util.List;


public class SConcurrentMigrate extends Statement {
	private static final long serialVersionUID = -1872829377852845948L;
	
	private List<SMigTask> tasks;
	private List<Integer> ids;
	
	public SConcurrentMigrate(List<SMigTask> tasks) {
		this.tasks = tasks;
	}
	
	public List<SMigTask> getTasks() {
		return tasks;
	}
	
	public List<Integer> getIds() {
		return ids;
	}
	
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
}
