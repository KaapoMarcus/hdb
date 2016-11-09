package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class SlaveStatusInfo implements Serializable {
	private static final long serialVersionUID = 2723465692088126784L;

	public SlaveStatusInfo(String name, String value) {
		this.name = name;
		this.value = value;
	}

	
	String name;

	
	String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
