package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;


public class Trigger implements Serializable{

	private static final long serialVersionUID = 7824771618674177041L;
	
	
	private String name;
	
	
	private String tablename;
	
	
	private List<Database> dbnList = null; 
	
	
	private String define = "";
	
	
	private String desc = "";
	
	
	private boolean inUse = true;
	
	
	private DbnType dbnType = DbnType.MySQL;
	
	
	private boolean isDML = true;
	
	
	public Trigger(String name, String table, List<Database> dbns, String def, 
			String desc, boolean inUse, DbnType type, boolean isDML) {
		this.name = name;
		this.tablename = table;
		this.dbnList = dbns;
		this.define = def;
		this.desc = desc;
		this.inUse = inUse;
		this.dbnType = type;
		this.isDML = isDML;
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		Trigger other = (Trigger)otherObject;
		
		return this.name.equalsIgnoreCase(other.name);
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public List<Database> getDbnList() {
		return dbnList;
	}

	public void setDbnList(List<Database> dbnList) {
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

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	public void setDbnType(DbnType type) {
		this.dbnType = type;
	}
	
	public DbnType getDbnType() {
		return dbnType;
	}

	public boolean isDML() {
		return isDML;
	}

	public void setDML(boolean isDML) {
		this.isDML = isDML;
	}
}
