package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class LoginRecord implements Serializable{
	private static final long	serialVersionUID	= 8108729544313200261L;

	
	String user = "";
	
	
	String clientIp = "";
	
	
	String qsIp = "";
	
	
	int count = 1;
	
	
	long lastTime = 0;
	
	public LoginRecord (String username, String clientIp, String qsIp)
	{
		this.user = username;
		this.clientIp = clientIp;
		if(qsIp != null && !qsIp.trim().equals(""))
			this.qsIp = qsIp.trim();
		this.lastTime = System.currentTimeMillis();
	}
	
	
	@Override
	public boolean equals(Object otherObject)
	{
		if (this == otherObject)
			return true;

		if (otherObject == null)
			return false;

		if (this.getClass() != otherObject.getClass())
			return false;

		LoginRecord other = (LoginRecord) otherObject;

		return this.user.equals(other.user)
				&& this.clientIp.equals(other.clientIp)
				&& this.qsIp.equals(other.qsIp);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + user.hashCode();
		result = prime * result + clientIp.hashCode();
		result = prime * result + qsIp.hashCode();
		return result;
	}
	
	
	public String getSig()
	{
		return this.user+":"+this.qsIp+":"+this.clientIp;
	}
	
	public static String getSig(String user, String clientIp, String qsIp){
		return user+":"+qsIp+":"+clientIp;
	}
	
	
	public void setLogin()
	{
		this.count++;
		this.lastTime = System.currentTimeMillis();
	}

	public String getQsIp() {
		return qsIp;
	}

	public void setQsIp(String qsIp) {
		this.qsIp = qsIp;
	}

	public String getClientIp() {
		return clientIp;
	}

	public int getCount() {
		return count;
	}

	public long getLastTime() {
		return lastTime;
	}

	public String getUser() {
		return user;
	}
}
