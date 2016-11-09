package com.jhh.hdb.proxyserver.codec.packets.mysql.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ���ش�����ʽ����
 * <pre>
 * VERSION 4.1
 *  Bytes                       Name
 *  -----                       ----
 *  1                           field_count, always = 0xff
 *  2                           errno
 *  1                           (sqlstate marker), always '#'
 *  5                           sqlstate (5 characters)
 *  n                           message
 * </pre>
 * 
 *
 */
public class ErrorPacket extends MySQLPacket {

	public ErrorPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		ErrorCmd errorCmd = (ErrorCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		
		MySQLPacketBuffer.writeByte(out, (byte)0xff);
		MySQLPacketBuffer.writeShort(out, errorCmd.getErrno());
		MySQLPacketBuffer.writeString(out, "#" + normalizeSqlState(errorCmd.getSqlstate())
				+ errorCmd.getMessage());
		
		return out;
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		//ErrorCmdû���޲ι��캯��
		int packetLength = MySQLPacketBuffer.readInt3(in);
		byte packetNumber = MySQLPacketBuffer.readByte(in);
		byte fieldCount = MySQLPacketBuffer.readByte(in);
		short errno = MySQLPacketBuffer.readShort(in);
		byte marker = MySQLPacketBuffer.readByte(in);
		String tmpString = MySQLPacketBuffer.readString(in);
		//sql_state�̶�5���ַ������ս��
		String sqlState = tmpString.substring(0, 5);
		String message = tmpString.substring(5);
		
		MySQLCmd cmd = new ErrorCmd(fieldCount, errno, marker, sqlState, message);
		cmd.setLength(packetLength);
		cmd.setNumber(packetNumber);
		
		return cmd;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		ErrorCmd errorCmd = (ErrorCmd) cmd;
		//13 = ��ͷ(4) + ��message��������ֽ�(9)
		return 13 + (errorCmd.getMessage() == null ? 0 : errorCmd.getMessage().length());
	}
	
	//Error Packet��sqlstate����Ϊ5���ֽ�
	private static String normalizeSqlState(String sqlState) {
		final int length = 5;
		if (sqlState == null || sqlState.length() == 0)
			return "null ";//�Ӷ�һ���ո�
		else if (sqlState.length() >= length)
			return sqlState.substring(0, length);
		else {
			StringBuilder sb = new StringBuilder(sqlState);
			for (int i = sqlState.length(); i < length; i++) {
				sb.append(" ");
			}
			return sb.toString();
		}
	}

}
