package com.netease.backend.db.common.sql;

import java.io.Serializable;


public abstract class Statement implements Serializable, Cloneable {
	private static final long serialVersionUID = 3616552671226254217L;

	protected String ddbName;

	@Override
	public Statement clone() {
		try {
			return (Statement) super.clone();
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}

	public String getDdbName() {
		return ddbName;
	}

	public void setDdbName(String name) {
		this.ddbName = name;
	}
}
