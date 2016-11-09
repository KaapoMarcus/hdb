package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.commands.ChangeUserCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * �û��л���ݰ��ʽ����
 * <pre>
 * Bytes     Name
 -----       ----
 n           user name (Null-terminated string)
 n           password
             4.1 scramble - Length (1 byte) coded string (21 byte)
 n           database name (Null-terminated string)
 2           character set number
 * </pre>
 * 
 *
 */
public class ChangeUserPacket extends MySQLPacket {

	public ChangeUserPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		ChangeUserCmd cmd = new ChangeUserCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
		
		cmd.setUsername(MySQLPacketBuffer.readString(in));
		long passwordLength = MySQLPacketBuffer.readLengthCodedBinary(in);
		cmd.setEncryptedPassword(MySQLPacketBuffer.readBytes(in, passwordLength));
		cmd.setDatabase(MySQLPacketBuffer.readString(in));
		
		//������������ַ�������2��byte�����ǳ����1��byte
		short charsetNumber = MySQLPacketBuffer.readShort(in);
		cmd.setCharsetNumber((byte)charsetNumber);
		
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		ChangeUserCmd changeUserCmd = (ChangeUserCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_CHANGE_USER);
		
		MySQLPacketBuffer.writeString(out, changeUserCmd.getUsername());
		MySQLPacketBuffer.writeLengthCodedBinary(out, changeUserCmd.getEncryptedPassword().length);
		MySQLPacketBuffer.writeBytes(out, changeUserCmd.getEncryptedPassword());
		MySQLPacketBuffer.writeString(out, changeUserCmd.getDatabase());
		MySQLPacketBuffer.writeShort(out, changeUserCmd.getCharsetNumber());
		
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		ChangeUserCmd changeUserCmd = (ChangeUserCmd) cmd;
		int length = 4;
		length += 1;
		length += changeUserCmd.getUsername().length();
		length += changeUserCmd.getEncryptedPassword().length;//���Գ���ֵ
		length += changeUserCmd.getDatabase().length();
		length += 2;
		return length;
	}

}
