package com.netease.backend.db.common.codec.impl;

import com.netease.backend.db.common.codec.Empty;


public class EmptyObject implements Empty {
	
	private int fieldNumber = -1;
	
	EmptyObject(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	public int getFieldNumber() {
		return fieldNumber;
	}

}
