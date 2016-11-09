package com.netease.backend.db.common.management.model;

import java.io.Serializable;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;


public class DDBTrigger implements Serializable {
	private static final long serialVersionUID = 5728657953350266571L;

	
	private String name;
	
	
	private String tablename;
	
	
	private List<String> dbnList = null;
	
	
	private String define = "";
	
	
	private String desc = "";
	
	
	private DbnType dbnType = DbnType.MySQL;
	
	
	private boolean isDML = true;
	
	
	public DDBTrigger(String name, String table, List<String> dbns, String def,
			String desc, DbnType type, boolean isDML) {
		this.name = name;
		this.tablename = table;
		this.dbnList = dbns;
		this.define = def;
		this.desc = desc;
		this.dbnType = type;
		this.isDML = isDML;
	}
	
	public String getTablename() {
		return tablename;
	}
	
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	
	public List<String> getDbnList() {
		return dbnList;
	}
	
	public void setDbnList(List<String> dbnList) {
		this.dbnList = dbnList;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDefine(String define) {
		this.define = define;
	}
	
	public String getDefine() {
		return define;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
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

	public void setDML(boolean isDML) {
		this.isDML = isDML;
	}
}
