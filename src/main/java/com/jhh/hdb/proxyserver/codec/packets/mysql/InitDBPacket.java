package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.commands.InitDBCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ��ͬ��'USE database'
 * <pre>
 * Bytes    Name
 -----      ----
   n        database name (up to end of packet, no termination character)
 * </pre>
 * 
 *
 */
public class InitDBPacket extends MySQLPacket {

	public InitDBPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException, UnsupportedEncodingException {
		InitDBCmd cmd = new InitDBCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
		
		int strLength = cmd.getLength() - 1 ;
		cmd.setDatabase(MySQLPacketBuffer.readString(in, strLength));
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		InitDBCmd initDBCmd = (InitDBCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_INIT_DB);
		
		MySQLPacketBuffer.writeStringWithoutTermination(out, initDBCmd.getDatabase());
		
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		InitDBCmd initDBCmd = (InitDBCmd) cmd;
		int length = 4;
		length += 1;
		length += initDBCmd.getDatabase().length();
		return length;
	}
}
