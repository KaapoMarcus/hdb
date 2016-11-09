package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.definition.DbnType;


public class SCreateProcedure extends Statement {
	private static final long serialVersionUID = 2333990891362091459L;
	
	private String spName;
	private String define;
	private DbnType dbnType;
	
	public SCreateProcedure(String name, String sql, DbnType type) {
		super();
		this.spName = name;
		this.define = sql;
		this.dbnType = type;
	}
	
	public String getSpName() {
		return this.spName;
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
}
