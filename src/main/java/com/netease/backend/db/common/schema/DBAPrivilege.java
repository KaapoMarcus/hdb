package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class DBAPrivilege implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	
	public final static int RIGHT_NULL = 0;

	
	public final static int RIGHT_READ = 1;

	
	public final static int RIGHT_WRITE = 2;

	
	public final static int ROLE_ANALYZE = 0;

	
	public final static int ROLE_MAINTAIN = 1;

	
	public final static int ROLE_DEPLOY = 2;

	
	public final static int ROLE_USERADM = 3;

	
	private int[] roleRights;

	
	public DBAPrivilege() {
		roleRights = new int[4];
		for (int i = 0; i < roleRights.length; i++) {
			roleRights[i] = RIGHT_NULL;
		}
	}

	
	public DBAPrivilege(int[] roleRights) throws IllegalArgumentException {
		if (roleRights == null || roleRights.length != 4) {
			throw new IllegalArgumentException("����DBARoleʧ�ܣ��������������Ҫ��");
		}

		this.roleRights = roleRights;
	}

	
	public Object clone() {
		try {
			DBAPrivilege cloned = (DBAPrivilege) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	
	public boolean verify(int roleType, int right) throws IllegalArgumentException {
		if (right < RIGHT_NULL || right > RIGHT_WRITE || roleType < ROLE_ANALYZE
				|| roleType > ROLE_USERADM) {
			throw new IllegalArgumentException("Ȩ����֤ʧ�ܣ��������������Ҫ��");
		}

		return (right <= roleRights[roleType]);
	}

	
	public void setRight(int roleType, int right) throws IllegalArgumentException {
		if (right < RIGHT_NULL || right > RIGHT_WRITE || roleType < ROLE_ANALYZE
				|| roleType > ROLE_USERADM) {
			throw new IllegalArgumentException("����Ȩ��ʧ�ܣ��������������Ҫ��");
		}

		roleRights[roleType] = right;
	}

	
	public int[] getRights() {
		return roleRights;
	}

	
	public int getRight(int roleType) throws IllegalArgumentException {
		if (roleType < ROLE_ANALYZE || roleType > ROLE_USERADM) {
			throw new IllegalArgumentException("����Ȩ��ʧ�ܣ��������������Ҫ��");
		}

		return roleRights[roleType];
	}

	
	public void setRights(int[] roleRights) throws IllegalArgumentException {
		if (roleRights == null || roleRights.length != 4) {
			throw new IllegalArgumentException("����DBARoleʧ�ܣ��������������Ҫ��");
		}

		this.roleRights = roleRights;
	}
}
