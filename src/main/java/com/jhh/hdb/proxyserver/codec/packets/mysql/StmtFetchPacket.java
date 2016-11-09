package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtFetchCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * <pre>
 *  Bytes      Name
 * -----       ----
 * 4           Statement ID (little endian)
 * 4           number of rows to fetch (little endian)
 * </pre>
 * 
 *
 */
public class StmtFetchPacket extends MySQLPacket {

	public StmtFetchPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		StmtFetchCmd cmd = new StmtFetchCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
		
		cmd.setStatementId(MySQLPacketBuffer.readInt(in));
		cmd.setFetchRow(MySQLPacketBuffer.readInt(in));
		
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		StmtFetchCmd stmtFetchCmd = (StmtFetchCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_STMT_FETCH);
		
		MySQLPacketBuffer.writeInt(out, stmtFetchCmd.getStatementId());
		MySQLPacketBuffer.writeInt(out, stmtFetchCmd.getFetchRow());
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		return 9;
	}

}
