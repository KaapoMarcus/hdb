package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Model implements Serializable {

	private static final long serialVersionUID = -274446646584758544L;

	
	private String name;

	
	private List<String> tableNameList;

	
	public Model(String mdlName, List<String> tblNameList) {
		this.name = mdlName;
		this.tableNameList = new ArrayList<String>();
		this.tableNameList.addAll(tblNameList);
	}

	
	public Model(String mdlName, String atblName) {
		this.name = mdlName;
		this.tableNameList = new ArrayList<String>();
		this.tableNameList.add(atblName);
	}

	public String getName() {
		return this.name;
	}

	public List<String> getTableNameList() {
		return this.tableNameList;
	}

	public boolean removeTable(String tblName) {
		return this.tableNameList.remove(tblName);
	}

	public boolean addTable(String tblName) {
		if (this.tableNameList.contains(tblName))
			return false;
		else {
			this.tableNameList.add(tblName);
			return true;
		}
	}

	public boolean isEmpty() {
		return this.tableNameList.isEmpty();
	}

	
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;

		if (otherObject == null)
			return false;

		if (this.getClass() != otherObject.getClass())
			return false;

		Model other = (Model) otherObject;

		return this.name.equalsIgnoreCase(other.name);
	}

}
