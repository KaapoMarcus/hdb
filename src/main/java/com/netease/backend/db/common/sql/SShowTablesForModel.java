package com.netease.backend.db.common.sql;

public class SShowTablesForModel extends Statement {
	private static final long serialVersionUID = 7029225530747871806L;
	private String modelName;
	
	public SShowTablesForModel(String modelName) {
		super();
		this.modelName = modelName;
	}

	public String getModelName() {
		return modelName;
	}
}
