package com.jhh.hdb.proxyserver.commands;

/**
 * 用户认证命令

client_flags:            CLIENT_xxx options. The list of possible flag
                          values is in the description of the Handshake
                          Initialisation Packet, for server_capabilities.
                          For some of the bits, the server passed "what
                          it's capable of". The client leaves some of the
                          bits on, adds others, and passes back to the server.
                          One important flag is: whether compression is desired.
                          Another interesting one is CLIENT_CONNECT_WITH_DB,
                          which shows the presence of the optional databasename.
 
 max_packet_size:         the maximum number of bytes in a packet for the client
 
 charset_number:          in the same domain as the server_language field that
                          the server passes in the Handshake Initialization packet.
 
 user:                    identification
 
 scramble_buff:           the password, after encrypting using the scramble_buff
                          contents passed by the server (see "Password functions"
                          section elsewhere in this document)
                          if length is zero, no password was given
 
 databasename:            name of schema to use initially
 
 * 
 *
 */
public class AuthenticationCmd extends MySQLCmd {

	private int clientFlags;

	private int maxPacketSize;

	private byte charsetNumber;

	private String user;

	private byte[] encryptedPassword;

	private String database;

	public MySQLCmdType getType() {
		return MySQLCmdType.authentication;
	}

	public int getClientFlags() {
		return clientFlags;
	}

	public void setClientFlags(int clientFlags) {
		this.clientFlags = clientFlags;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	public byte getCharsetNumber() {
		return charsetNumber;
	}

	public void setCharsetNumber(byte charsetNumber) {
		this.charsetNumber = charsetNumber;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[AuthenticationCmd] User:").append(this.user);
		sb.append(", Database:").append(this.database);
		sb.append(", CharsetNumber:").append(this.charsetNumber);
		sb.append(", MaxPacketSize:").append(this.maxPacketSize);
		sb.append(", ClientFlags:").append(this.clientFlags);
		return sb.toString();
	}
}
