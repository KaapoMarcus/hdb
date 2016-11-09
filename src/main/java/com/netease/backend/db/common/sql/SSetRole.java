package com.netease.backend.db.common.sql;


public class SSetRole extends Statement {
	private static final long serialVersionUID = -2337653140314468219L;
	private String user;
	private int[] roles;
	
	public SSetRole(String user, int[] role) {
		this.user = user;
		this.roles = role;
	}
	
	public String getUser() {
		return user;
	}
	
	public int[] getRoles() {
		return roles;
	}
}
