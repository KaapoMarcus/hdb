package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtSendLongDataCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * <pre>
 *  Bytes       Name
 * -----        ----
 * 4            Statement ID (little endian)
 * 2            Parameter number (little endian)
 * n            payload (up to end of packet, no termination character)
 * </pre>
 * 
 *
 */
public class StmtSendLongDataPacket extends MySQLPacket {

	public StmtSendLongDataPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		StmtSendLongDataCmd cmd = new StmtSendLongDataCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
		
		cmd.setStatementID(MySQLPacketBuffer.readInt(in));
		cmd.setParameterNumber(MySQLPacketBuffer.readShort(in));
		
		//ʣ���ֽ���Ϊ = ��� - CommandType(1) - StatementID(4) - PrarmeterNumber(2)
		int payLoadLength = cmd.getLength() - 7;
		cmd.setPayload(MySQLPacketBuffer.readBytes(in, payLoadLength));
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		StmtSendLongDataCmd stmtSendLongDataCmd = (StmtSendLongDataCmd)cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		
		byte[] payLoad = stmtSendLongDataCmd.getPayload();
		//packet head
		MySQLPacketBuffer.writeInt3(out, 7 + (payLoad == null ? 0 : payLoad.length));
		MySQLPacketBuffer.writeByte(out, (byte)1);
		
		out.put(MySQLCommandNumber.COM_STMT_SEND_LONG_DATA);
		MySQLPacketBuffer.writeInt(out, stmtSendLongDataCmd.getStatementID());
		MySQLPacketBuffer.writeShort(out, stmtSendLongDataCmd.getParameterNumber());
		MySQLPacketBuffer.writeBytes(out, stmtSendLongDataCmd.getPayload());
		
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		StmtSendLongDataCmd stmtSendLongDataCmd = (StmtSendLongDataCmd)cmd;
		return 6 + (stmtSendLongDataCmd.getPayload() == null ? 0
				: stmtSendLongDataCmd.getPayload().length);
	}
}
