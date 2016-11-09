package com.netease.backend.db.common.management.model;


public class DBUserChangeDesc extends DBUserOp {
	private static final long serialVersionUID = -3543497889268536552L;
	private String userName;
	private String desc;
	
	public DBUserChangeDesc(String user, String description) {
		super("DBUserChangeDesc");
		this.userName = user;
		this.desc = description;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("�޸��û�����������" + line);
		bf.append("������Ϣ {" + errMsg + "}" + line);
		bf.append("�û��� {" + userName + "}" + line);
		bf.append("�µ�������Ϣ {"  + desc + "}");
		return bf.toString();
	}
}
