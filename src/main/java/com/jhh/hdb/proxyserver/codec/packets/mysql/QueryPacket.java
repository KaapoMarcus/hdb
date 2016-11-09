package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.QueryCmd;
import com.jhh.hdb.proxyserver.define.MySQLCommandNumber;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * <pre>
 * Bytes   Name
   -----   ----
   n       SQL statement (up to end of packet, no termination character)
 * </pre>
 * 
 *
 */
public class QueryPacket extends MySQLPacket {

	public QueryPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context)
			throws CharacterCodingException, UnsupportedEncodingException, DecodeException {
		QueryCmd cmd = new QueryCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
//		final byte commandType = MySQLPacketBuffer.readByte(in);
//		if (commandType != MySQLCommandNumber.COM_QUERY)
//			throw new DecodeException("Expected " + MySQLCommandNumber.COM_QUERY 
//					+ "(COM_QUERY), but was " + commandType);
		
		int sqlLength = cmd.getLength() - 1;
		cmd.setSql(MySQLPacketBuffer.readString(in, sqlLength));
		
/*		byte[] content = MySQLPacketBuffer.readBytes(in, sqlLength);
		final String charsetName = GlobalContext.getInstance().getQsConfig().getCharset().name();
		String sql = null;
		//���Զ�SQL����ת��
		try {
			sql = new String(content, charsetName);
		} catch (Exception e) {
			//ת��ʧ�ܣ����ڲ���ʶ����ַ����
			byte[] after = HexEscapeUtils.hexEscape(content);
			try {
				sql = new String(after, GlobalContext.getInstance().getQsConfig()
						.getCharset().name());
			} catch (Exception ex) {
				//ת��ʧ�ܣ��򷵻ظ��ϲ�
				throw new IllegalArgumentException("sql decode faild: " + Arrays.toString(content));
			}
		}
		cmd.setSql(sql);*/
		
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		QueryCmd queryCmd = (QueryCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);
		out.put(MySQLCommandNumber.COM_QUERY);
		MySQLPacketBuffer.writeStringWithoutTermination(out, queryCmd.getSql());
		
		return out;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		QueryCmd queryCmd = (QueryCmd) cmd;
		int length = 4;
		length += 1;
		length += queryCmd.getSql().length();
		return length;
	}
}
