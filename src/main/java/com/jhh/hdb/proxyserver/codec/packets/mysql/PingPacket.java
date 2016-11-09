package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.PingCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * �ͻ��˲������ӵĿ����ԣ�û����Ҫ���������
 * 
 *
 */
public class PingPacket extends MySQLPacket {

	public PingPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		PingCmd cmd = new PingCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
		
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_PING);
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		return 5;
	}
}
