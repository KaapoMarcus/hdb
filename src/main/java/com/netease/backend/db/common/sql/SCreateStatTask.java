package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.stat.StatTask;


public class SCreateStatTask extends Statement {
	private static final long serialVersionUID = 1L;
	
	private StatTask task;
	private Clients clients;
	private int like;
	
	public SCreateStatTask(StatTask task, Clients clients) {
		this.task = task;
		this.clients = clients;
	}
	
	public SCreateStatTask(int like) {
		this.like = like;
	}
	
	public int getLike() {
		return like;
	}
	
	public StatTask getTask() {
		return task;
	}
	
	public Clients getClients() {
		return clients;
	}
}
