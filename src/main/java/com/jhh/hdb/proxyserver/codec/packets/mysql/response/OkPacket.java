package com.jhh.hdb.proxyserver.codec.packets.mysql.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.OkCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ���سɹ����ʽ����
 VERSION 4.1
 Bytes                       Name
 -----                       ----
 1   (Length Coded Binary)   field_count, always = 0
 1-9 (Length Coded Binary)   affected_rows
 1-9 (Length Coded Binary)   insert_id
 2                           server_status
 2                           warning_count
 n   (until end of packet)   message

 * 
 *
 */
public class OkPacket extends MySQLPacket {

	public OkPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		//OkCmdû���޲ι��캯�������⴦���ͷ��Ϣ
		int packetLength = MySQLPacketBuffer.readInt3(in);
		byte packetNumber = MySQLPacketBuffer.readByte(in);
		
		byte fieldCount = MySQLPacketBuffer.readByte(in);
		long affectedRows = MySQLPacketBuffer.readLengthCodedBinary(in);
		long insertId = MySQLPacketBuffer.readLengthCodedBinary(in);
		short serverStatus = MySQLPacketBuffer.readShort(in);
		short warningCount = MySQLPacketBuffer.readShort(in);
		String message = MySQLPacketBuffer.readString(in);
		if (message.startsWith("#"))
			message = message.substring(1);
		
		MySQLCmd cmd = new OkCmd(fieldCount, affectedRows, insertId, serverStatus, warningCount, message.trim());
		cmd.setLength(packetLength);
		cmd.setNumber(packetNumber);
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		OkCmd okCmd = (OkCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		
		//Length Coded Binary
		MySQLPacketBuffer.writeLengthCodedBinary(out, 0L);
		MySQLPacketBuffer.writeLengthCodedBinary(out, okCmd.getAffectedRows());
		MySQLPacketBuffer.writeLengthCodedBinary(out, okCmd.getInsertId());
		
		MySQLPacketBuffer.writeShort(out, okCmd.getServerStatus());
		MySQLPacketBuffer.writeShort(out, okCmd.getWarningCount());
		//��1��'#'�������MySQL-Client�̵���һ���ַ�
		MySQLPacketBuffer.writeStringWithoutTermination(out, "#" + okCmd.getMessage());
		
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		OkCmd okCmd = (OkCmd) cmd;
		//27 = 4 + 1 + 9 + 9 + 2 + 2��2��Length Coded Binary��ֱ��ʹ�����ֵ9
		int length = 27 + (okCmd.getMessage() == null ? 0 : okCmd.getMessage().length()); 
		return length;
	}
}
