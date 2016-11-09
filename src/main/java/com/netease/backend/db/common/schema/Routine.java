package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;



public class Routine implements Serializable{

	private static final long serialVersionUID = -1301527761078503339L;
	
	
	private String name = "";
	
	
	private List<Database> dbnList = null;
	
	
	private String define = "";
	
	
	private String desc = "";
	
	
	private DbnType dbnType;
	
	
	public Routine(String name, List<Database> dbns, String define, String desc, DbnType type) {
		this.name = name;
		this.dbnList = dbns;
		this.define = define;
		this.desc = desc;
		this.dbnType = type;
	}

	public List<Database> getDbnList() {
		return dbnList;
	}

	public void setDbnList(List<Database> dbnList) {
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
	
	public void setDbnType(DbnType type) {
		this.dbnType = type;
	}
	
	public DbnType getDbnType() {
		return dbnType;
	}
	
	public int[] getDbnIds() {
		if(dbnList == null || dbnList.size() == 0)
			return null;
		int[] dbnIds = new int[dbnList.size()];
		int i = 0;
		for(Database db : dbnList)
			dbnIds[i++] = db.getId();
		return dbnIds;
	}
	
	public List<String> getDbnNameList() {
		if (dbnList == null || dbnList.size() == 0)
			return null;
		List<String> dbnNames = new ArrayList<String>();
		for (Database db : dbnList)
			dbnNames.add(db.getName());
		return dbnNames;
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		Routine other = (Routine)otherObject;
		
		return this.name.equalsIgnoreCase(other.name);
	}
}
