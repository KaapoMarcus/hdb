package com.jhh.hdb.proxyserver.commands.response;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmdType;

/**
 * ���ְ�ͨ�Ÿս���ʱ�ɷ���������ͻ���
 * 
 * protocol_version:    The server takes this from PROTOCOL_VERSION
                      in /include/mysql_version.h. Example value = 10.
 
 server_version:      The server takes this from MYSQL_SERVER_VERSION
                      in /include/mysql_version.h. Example value = "4.1.1-alpha".
 
 thread_number:       ID of the server thread for this connection.
 
 scramble_buff:       The password mechanism uses this. The second part are the
                      last 13 bytes.
                      (See "Password functions" section elsewhere in this document.)
 
 server_capabilities: CLIENT_XXX options
 
 server_language:     current server character set number
 
 server_status:       SERVER_STATUS_xxx flags: e.g. SERVER_STATUS_AUTOCOMMIT

 * 
 *
 */
public class HandshakeCmd extends MySQLCmd implements Cloneable {
	private byte protocolVersion;
	private String serverVersion;
	private int threadId;
	private String scrambleBuff;
	private short serverCapabilities;
	private byte serverLanguage;
	private short serverStatus;
	private String restScrambleBuff;

	public MySQLCmdType getType() {
		return MySQLCmdType.handshake;
	}

	public byte getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(byte protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public String getScrambleBuff() {
		return scrambleBuff;
	}

	public void setScrambleBuff(String scrambleBuff) {
		this.scrambleBuff = scrambleBuff;
	}

	public short getServerCapabilities() {
		return serverCapabilities;
	}

	public void setServerCapabilities(short serverCapabilities) {
		this.serverCapabilities = serverCapabilities;
	}

	public byte getServerLanguage() {
		return serverLanguage;
	}

	public void setServerLanguage(byte serverLanguage) {
		this.serverLanguage = serverLanguage;
	}

	public short getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(short serverStatus) {
		this.serverStatus = serverStatus;
	}

	public String getRestScrambleBuff() {
		return restScrambleBuff;
	}

	public void setRestScrambleBuff(String restScrambleBuff) {
		this.restScrambleBuff = restScrambleBuff;
	}

	public Object clone() {
		try {
			final HandshakeCmd cloned = (HandshakeCmd) super.clone();
			return cloned;
		} catch (final CloneNotSupportedException e) {
			// System.out.println("CloneNotSupportedException: "+e.getMessage());
			return null;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[HandshakeCmd] ProtocolVersion:").append(this.protocolVersion);
		sb.append(", ServerVersion:").append(this.serverVersion);
		sb.append(", ThreadId:").append(this.threadId);
		sb.append(", ServerLanguage:").append(this.serverLanguage);
		sb.append(", ServerStatus:").append(this.serverStatus);
		sb.append(", ServerCapabilities:").append(this.serverCapabilities);
		sb.append(", ScrambleBuff:").append(this.scrambleBuff);
		sb.append(", RestScrambleBuff:").append(this.restScrambleBuff);
		return sb.toString();
	}
}
