package com.netease.backend.db.common.schema;

import java.util.HashMap;
import java.util.Set;


public class OnlineMigStartId {
	
	private HashMap<String, Long> dirtyStartId = new HashMap<String, Long>();

	
	private long taskId;

	public OnlineMigStartId(long taskId) {
		super();
		this.taskId = taskId;
	}

	public void addStartId(String tablename, long startid) {
		dirtyStartId.put(tablename, startid);
	}

	public long getStartId(String tablename) {
		Long startid = dirtyStartId.get(tablename);
		if (startid == null) {
			throw new IllegalArgumentException(
					"can not find dirty start id, tablename:" + tablename);
		}

		return startid;
	}

	public long getTaskId() {
		return taskId;
	}

	public Set<String> getTables(){
		return dirtyStartId.keySet();
	}
}
