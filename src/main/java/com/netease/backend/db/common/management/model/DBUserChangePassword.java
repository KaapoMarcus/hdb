package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class DBUserChangePassword extends DBUserOp implements Serializable {
	private static final long serialVersionUID = -4319439726119136772L;
	
	private String userName;
	private String password;
	private boolean ignoreConnErr;
	private boolean ignoreClientErr;
	
	public DBUserChangePassword(String username, String pass,
			boolean ignoreConnErr, boolean ignoreClientErr) {
		super("DBUserChangePassword");
		this.userName = username;
		this.password = pass;
		this.ignoreConnErr = ignoreConnErr;
		this.ignoreClientErr = ignoreClientErr;
	}
	
	public boolean isIgnoreConnErr() {
		return ignoreConnErr;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public boolean isIgnoreClientErr() {
		return ignoreClientErr;
	}
	
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("�޸��û����������" + line);
		bf.append("������Ϣ {" + errMsg + "}" + line);
		bf.append("��Ҫ�޸�������û����û��� {" + userName + "}" + line);
		bf.append(ignoreClientErr ? "����֪ͨClientʧ�ܵ��쳣" : "������֪ͨClientʧ�ܵ��쳣"
			+ line);
		bf.append(ignoreConnErr ? "�����޷����ӵ�Client����" : "�������޷����ӵ�Client����");
		return bf.toString();
	}
}
