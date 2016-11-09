package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class Operation implements Serializable {
	private static final long serialVersionUID = -4338343922177402017L;
	
	
	public static final int OP_EQ = 0;
	
	
	public static final int OP_NE = 1;
	
	
	public static final int OP_GT = 2;
	
	
	public static final int OP_GE = 3;
	
	
	public static final int OP_LT = 4;
	
	
	public static final int OP_LE = 5;
	
	
	public static final int OP_LK = 6;
	
	
	private int op;
	
	
	private Object data;
	
	
	public Operation(Object dat, int operation) {
		this.data = dat;
		this.op = operation;
	}
    
    public Object getData() {
        return this.data;
    }
    
    public int getOperation() {
        return this.op;
    }
	
	
	public String toString() {
		String strData = "";
		if(data instanceof String) {
			strData = "'" + data + "'";
		} else {
			strData = "" + data;
		}
		switch(op) {
			case OP_EQ:
				return "=" + strData;
			case OP_NE:
				return "<>" + strData;
			case OP_GT:
				return ">" + strData;
			case OP_GE:
				return ">=" + strData;
			case OP_LT:
				return "<" + strData;
			case OP_LE:
				return "<=" + strData;
			case OP_LK:
				return " LIKE '%" + data + "%'";
			default:
				return "";
		}
	}
}
