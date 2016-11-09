package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class DBUserDelete extends DBUserOp implements Serializable {
	private static final long serialVersionUID = -2079756777669332425L;
	
	private String userName;
	private boolean ignoreConnErr;
	private boolean ignoreClientErr;
	
	public DBUserDelete(String username, boolean isOnDBN,
			boolean ignoreConnErr, boolean ignoreClientErr) {
		super("DBUserDelete");
		this.userName = username;
		this.onDbn = isOnDBN;
		this.ignoreConnErr = ignoreConnErr;
		this.ignoreClientErr = ignoreClientErr;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public boolean isIgnoreConnErr() {
		return ignoreConnErr;
	}
	
	public boolean isIgnoreClientErr() {
		return ignoreClientErr;
	}
	
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("ɾ���û�������" + line);
		bf.append("������Ϣ {" + errMsg + "}" + line);
		bf.append("��Ҫɾ���û����û��� {" + userName + "}" + line);
		bf.append((onDbn ? "��Ҫͬʱ��DBN��ɾ���û�" : "����Ҫ��DBN��ɾ���û�") + line);
		bf.append(ignoreClientErr ? "����֪ͨClientʧ�ܵ��쳣" : "������֪ͨClientʧ�ܵ��쳣"
			+ line);
		bf.append(ignoreConnErr ? "�����޷����ӵ�Client����" : "�������޷����ӵ�Client����");
		return bf.toString();
	}
}
