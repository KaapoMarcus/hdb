package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.definition.DbnType;


public class SCreateTrigger extends Statement {
	private static final long serialVersionUID = -5511304297146559124L;
	
	private String triggerName;
	private String tableName;
	private String define;
	private DbnType dbnType;
	private boolean isDML;
	
	public SCreateTrigger(String name, String table, String sql, DbnType type, boolean isDML) {
		super();
		this.triggerName = name;
		this.define = sql;
		this.dbnType = type;
		this.isDML = isDML;
		if (null == table)
			this.tableName = "";
		else
			this.tableName = table;
	}
	
	public String getTriggerName() {
		return this.triggerName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public String getDefine() {
		return this.define;
	}
	
	public DbnType getDbnType() {
		return dbnType;
	}

	public void setDbnType(DbnType dbnType) {
		this.dbnType = dbnType;
	}

	public boolean isDML() {
		return isDML;
	}
}
