package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtPrepareCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * <pre>
 *  Bytes     Name
 * -----      ----
 *   n        query string with '?' place holders (up to end of packet, no termination character)
 * </pre>
 * 
 *
 */
public class StmtPreparePacket extends MySQLPacket {

	public StmtPreparePacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context)
			throws CharacterCodingException, UnsupportedEncodingException, DecodeException {
		StmtPrepareCmd cmd = new StmtPrepareCmd();
		decodePacketHeader(cmd, in);
//		final byte commandType = MySQLPacketBuffer.readByte(in);
//		if (commandType != MySQLCommandNumber.COM_STMT_PREPARE)
//			throw new DecodeException("Expected " + MySQLCommandNumber.COM_STMT_PREPARE 
//					+ "(COM_STMT_PREPARE), but was " + commandType);
		in.skip(1);//�����������
		
		int sqlLength = cmd.getLength() - 1;
		cmd.setSql(MySQLPacketBuffer.readString(in, sqlLength));
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		StmtPrepareCmd stmtPrepareCmd = (StmtPrepareCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_STMT_PREPARE);
		
		MySQLPacketBuffer.writeStringWithoutTermination(out, stmtPrepareCmd.getSql());
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		StmtPrepareCmd stmtPrepareCmd = (StmtPrepareCmd) cmd;
		if (null == stmtPrepareCmd.getSql())
			return 0;
		else return (stmtPrepareCmd.getSql().getBytes().length + 1);
	}
}
