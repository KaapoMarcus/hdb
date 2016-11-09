package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;
import java.util.Set;

import com.netease.backend.db.common.schema.EntityPrivilege;


public class DBUser implements Serializable {

	private static final long serialVersionUID = 3480176187514310741L;

	
	private static int MAX_NAME_LENGTH = 16;

	
	private static int MAX_PASSWORD_LENGTH = 41;

	
	private String name;

	
	private String password;

	
	private Set<String> clientIpSet;

	
	private Set<EntityPrivilege> privileges;

	
	private String desc;

	
	public DBUser(String name, String password, Set<String> clientIpSet,
			Set<EntityPrivilege> privileges, String comment) {
		this.name = name;
		this.password = password;
		this.clientIpSet = clientIpSet;
		this.privileges = privileges;
		this.desc = comment;
	}

	
	public DBUser(String name, Set<String> clientIpSet,
			Set<EntityPrivilege> privileges, String desc) {
		this.name = name;
		this.password = null;
		this.clientIpSet = clientIpSet;
		this.privileges = privileges;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getClientIpSet() {
		return clientIpSet;
	}

	public void setClientIpSet(Set<String> clientIpSet) {
		this.clientIpSet = clientIpSet;
	}

	public Set<EntityPrivilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<EntityPrivilege> privileges) {
		this.privileges = privileges;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	
	public static void checkDBUserName(String name)
			throws IllegalArgumentException {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("�û�������Ϊ��");
		if (name.length() > MAX_NAME_LENGTH)
			throw new IllegalArgumentException("�û������ܳ���" + MAX_NAME_LENGTH
					+ "���ַ�");
	}

	
	public static void checkDBUserPassword(String password)
			throws IllegalArgumentException {
		if (password == null || password.length() == 0)
			throw new IllegalArgumentException("���벻��Ϊ��");
		if (password.length() > MAX_PASSWORD_LENGTH) {
			throw new IllegalArgumentException("���벻�ܳ���" + MAX_PASSWORD_LENGTH
					+ "���ַ�");
		}
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			
			if (c < 32 || c > 126 || c == 34 || c == 47 || c == 64) {
				throw new IllegalArgumentException("���벻����Ҫ��");
			}
		}
	}
}
