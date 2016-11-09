package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;


public class Column implements Serializable {

	private static final long serialVersionUID = 8030002477150696118L;

	
	private String name;

	
	private String type;

	
	private int length;

	
	private boolean isPartitionKey = false;

	
	private boolean isAutoIncrement = false;

	public Column(String name, String type, int length, boolean isPartitionKey,
			boolean isAutoIncrement) {
		this.name = name;
		this.type = type;
		this.length = length;
		this.isPartitionKey = isPartitionKey;
		this.isAutoIncrement = isAutoIncrement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isPartitionKey() {
		return isPartitionKey;
	}

	public void setPartitionKey(boolean isPartitionKey) {
		this.isPartitionKey = isPartitionKey;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}
}
