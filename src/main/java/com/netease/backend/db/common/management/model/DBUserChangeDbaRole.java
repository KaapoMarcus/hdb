package com.netease.backend.db.common.management.model;

import java.util.Arrays;


public class DBUserChangeDbaRole extends DBUserOp {
	private static final long serialVersionUID = 6733714362548182307L;
	private String userName;
	private int[] roleRights;
	
	public DBUserChangeDbaRole(String username, int[] rights) {
		this.userName = username;
		this.roleRights = rights;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public int[] getRoleRights() {
		return this.roleRights;
	}
	
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("�޸Ĺ���Ա�Ĺ���Ȩ�޲�����").append(line);
		bf.append("������Ϣ {").append(errMsg).append("}").append(line);
		bf.append("��Ҫ�޸�Ȩ�޵Ĺ���Ա�û��� {").append(userName).append("}").append(line);
		bf.append("����Ȩ������ ").append(Arrays.asList(new int[0])).append(line);
		return bf.toString();
	}
}
