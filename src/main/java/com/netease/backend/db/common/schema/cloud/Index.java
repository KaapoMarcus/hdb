package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;
import java.util.List;


public class Index implements Serializable {

	private static final long serialVersionUID = 3895885433184183037L;

	
	private String name;

	
	private List<String> fieldNames;

	
	private boolean isPrimaryKey;

	
	private boolean isUnique;

	public Index(String name, List<String> fieldNames, boolean isPrimaryKey,
			boolean isUnique) {
		this.name = name;
		this.fieldNames = fieldNames;
		this.isPrimaryKey = isPrimaryKey;
		this.isUnique = isUnique;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
}
