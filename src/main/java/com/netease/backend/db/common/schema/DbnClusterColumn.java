package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class DbnClusterColumn implements Serializable {
	private static final long serialVersionUID = 1L;

	
	private String name;

	
	private String typeStr;

	public DbnClusterColumn(String name, String typeStr) {
		super();
		this.name = name;
		this.typeStr = typeStr;
	}
	public DbnClusterColumn() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypeStr() {
		return typeStr;
	}
	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	@Override
	public String toString(){
	    return this.name;
	}
}
