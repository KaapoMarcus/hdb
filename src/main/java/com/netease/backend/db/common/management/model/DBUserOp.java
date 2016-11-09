package com.netease.backend.db.common.management.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



public class DBUserOp implements Serializable {
	private static final long serialVersionUID = 4836078624812209697L;
	
	String opType;
	boolean onDbn;
	String errMsg;
	String line;
	
	public DBUserOp() {
		line = System.getProperty("line.separator");
		onDbn = true;
	}
	
	public DBUserOp(String type) {
		this();
		this.opType = type;
		this.errMsg = "";
	}
	
	public String getOpType() {
		return this.opType;
	}
	
	public String getErrMsg() {
		return this.errMsg;
	}
	
	public DBUserOp setErrMsg(String msg) {
		this.errMsg = msg;
		return this;
	}
	
	public boolean isOnDbn() {
		return onDbn;
	}
	
	public void setOnDbn(boolean b) {
		this.onDbn = b;
	}
	
	public static DBUserOp readOp(String fileName) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(fileName));
			DBUserOp op = (DBUserOp) ois.readObject();
			return op;
		} catch (FileNotFoundException e) {
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != ois)
				try {
					ois.close();
				} catch (IOException e) {
				}
		}
		return null;
	}
	
	public static void writeOp(DBUserOp op, String fileName) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(op);
			oos.close();
		} finally {
			if (null != oos)
				oos.close();
		}
	}
}
