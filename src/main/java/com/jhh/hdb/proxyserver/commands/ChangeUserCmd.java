package com.jhh.hdb.proxyserver.commands;


/**
 * 切换用户命令
 * 
 *
 */
public class ChangeUserCmd extends CommandCmd {

	private String username;

	private byte[] encryptedPassword;

	private String database;

	private byte charsetNumber;
	
	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.changeUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(byte[] password) {
		this.encryptedPassword = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public byte getCharsetNumber() {
		return charsetNumber;
	}

	public void setCharsetNumber(byte charset) {
		this.charsetNumber = charset;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ChangeUderCmd] Username:").append(this.username);
		sb.append(", Database:").append(this.database);
		sb.append(", CharsetNumber:").append(this.charsetNumber);
		return sb.toString();
	}
}
