package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtCloseCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * �ر�Ԥ�������
 * <pre>
 * Bytes     Name
 * -----     ----
 * 4         Statement ID (little endian)
 * </pre>
 * 
 *
 */
public class StmtClosePacket extends MySQLPacket {

	public StmtClosePacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context)
			throws CharacterCodingException, DecodeException {
		StmtCloseCmd cmd = new StmtCloseCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
//		final byte commandType = MySQLPacketBuffer.readByte(in);
//		if (commandType != MySQLCommandNumber.COM_STMT_CLOSE)
//			throw new DecodeException("Expected "
//					+ MySQLCommandNumber.COM_STMT_CLOSE
//					+ "(COM_STMT_CLOSE), but was " + commandType);

		cmd.setStatementId(MySQLPacketBuffer.readInt(in));
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		StmtCloseCmd stmtCloseCmd = (StmtCloseCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_STMT_CLOSE);
		
		MySQLPacketBuffer.writeInt(out, stmtCloseCmd.getStatementId());
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		return 5;
	}

}
