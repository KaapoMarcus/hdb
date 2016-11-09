package com.netease.backend.db.common.management.model;

import java.io.Serializable;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;



public class DDBRoutine implements Serializable{
	private static final long serialVersionUID = 5287173609254671956L;
	
	
	private String name = "";
	
	
	private List<String> dbnList = null;
	
	
	private String define = "";
	
	
	private String desc = "";
	
	
	private DbnType dbnType;
	
	
	public DDBRoutine(String name, List<String> dbns, String define, String desc, DbnType type) {
		this.name = name;
		this.dbnList = dbns;
		this.define = define;
		this.desc = desc;
		this.dbnType = type;
	}
	
	public List<String> getDbnList() {
		return dbnList;
	}
	
	public void setDbnList(List<String> dbnList) {
		this.dbnList = dbnList;
	}
	
	public String getDefine() {
		return define;
	}
	
	public void setDefine(String define) {
		this.define = define;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getName() {
		return name;
	}

	public DbnType getDbnType() {
		return dbnType;
	}

	public void setDbnType(DbnType dbnType) {
		this.dbnType = dbnType;
	}
}

