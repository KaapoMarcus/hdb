package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.commands.AuthenticationCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.define.ServerCapabilities;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * �û���֤���ʽ����
 *  
 *<pre>
 VERSION 4.1
 Bytes                        Name
 -----                        ----
 4                            client_flags
 4                            max_packet_size
 1                            charset_number
 23                           (filler) always 0x00...
 n (Null-Terminated String)   user
 n (Length Coded Binary)      scramble_buff (1 + x bytes)
 n (Null-Terminated String)   databasename (optional)
 *</pre>
 * 
 *
 */
public class AuthenticationPacket extends MySQLPacket {

	public AuthenticationPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		AuthenticationCmd cmd = new AuthenticationCmd();
		decodePacketHeader(cmd, in);
		
		cmd.setClientFlags(MySQLPacketBuffer.readInt(in));
		cmd.setMaxPacketSize(MySQLPacketBuffer.readInt(in));
		cmd.setCharsetNumber(MySQLPacketBuffer.readByte(in));
		in.skip(23);//���23������ֽ�
		cmd.setUser(MySQLPacketBuffer.readString(in));
		long passwordLength = MySQLPacketBuffer.readLengthCodedBinary(in);
		cmd.setEncryptedPassword(MySQLPacketBuffer.readBytes(in, passwordLength));
		
		String dbName = null;
		if ((cmd.getClientFlags() & ServerCapabilities.CLIENT_CONNECT_WITH_DB) != 0) {
			dbName = MySQLPacketBuffer.readString(in);
		}
		cmd.setDatabase(dbName);
		
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		AuthenticationCmd authCmd = (AuthenticationCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		
		MySQLPacketBuffer.writeInt(out, authCmd.getClientFlags());
		MySQLPacketBuffer.writeInt(out, authCmd.getMaxPacketSize());
		MySQLPacketBuffer.writeByte(out, authCmd.getCharsetNumber());
		
		//���23�ֽڵ�0x00
		for (int i = 0; i < 23; i++)
			MySQLPacketBuffer.writeByte(out, (byte)0x00);
		MySQLPacketBuffer.writeString(out, authCmd.getUser());
		if (authCmd.getEncryptedPassword() == null || authCmd.getEncryptedPassword().length < 1)
			MySQLPacketBuffer.writeByte(out, (byte)0x00);
		else {
			MySQLPacketBuffer.writeLengthCodedBinary(out, authCmd.getEncryptedPassword().length);
			MySQLPacketBuffer.writeBytes(out, authCmd.getEncryptedPassword());
		}
		if ((authCmd.getClientFlags() & ServerCapabilities.CLIENT_CONNECT_WITH_DB) != 0) {
			MySQLPacketBuffer.writeString(out, authCmd.getDatabase());
		}
		
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		int length = 4;
		AuthenticationCmd authCmd = (AuthenticationCmd) cmd;
		length += 4 + 4 + 1 + 23;
		length += authCmd.getUser().length();
		length += authCmd.getEncryptedPassword().length;//���Գ���ֵ
		length += authCmd.getDatabase().length();
		return length;
	}
}
