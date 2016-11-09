package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtResetCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * <pre>
 * Bytes      Name
 * -----      ----
 * 4          Statement ID (little endian)
 * </pre>
 * 
 *
 */
public class StmtResetPacket extends MySQLPacket {

	public StmtResetPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		StmtResetCmd cmd = new StmtResetCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
		
		cmd.setStatementId(MySQLPacketBuffer.readInt(in));
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		StmtResetCmd stmtResetCmd = (StmtResetCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_STMT_RESET);
		
		MySQLPacketBuffer.writeInt(out, stmtResetCmd.getStatementId());
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		return 5;
	}
}
