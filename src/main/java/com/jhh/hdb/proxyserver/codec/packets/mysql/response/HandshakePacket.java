package com.jhh.hdb.proxyserver.codec.packets.mysql.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.HandshakeCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ���ְ��ʽ����
 *
 *<pre>
Bytes                        Name
 -----                        ----
 1                            protocol_version
 n (Null-Terminated String)   server_version
 4                            thread_id
 8                            scramble_buff
 1                            (filler) always 0x00
 2                            server_capabilities
 1                            server_language
 2                            server_status
 13                           (filler) always 0x00 ...
 13                           rest of scramble_buff (4.1)
 </pre>

 * 
 *
 */
public class HandshakePacket extends MySQLPacket {

	public HandshakePacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		HandshakeCmd response = (HandshakeCmd) cmd;
		ComBuffer out = createBuffer(getPacketSize(cmd));
		encodePacketHeader(out, cmd);

		MySQLPacketBuffer.writeByte(out, response.getProtocolVersion());
		MySQLPacketBuffer.writeString(out, response.getServerVersion());
		MySQLPacketBuffer.writeInt(out, response.getThreadId());
		MySQLPacketBuffer.writeStringWithoutTermination(out, response.getScrambleBuff());
		MySQLPacketBuffer.writeByte(out, (byte)0x00);
		MySQLPacketBuffer.writeShort(out, response.getServerCapabilities());
		MySQLPacketBuffer.writeByte(out, response.getServerLanguage());
		MySQLPacketBuffer.writeShort(out, response.getServerStatus());
		MySQLPacketBuffer.writeBytes(out, new byte[13]);//filler
		MySQLPacketBuffer.writeString(out, response.getRestScrambleBuff());
		
		return out;
	}

	// only for test
	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context) throws CharacterCodingException {
		HandshakeCmd cmd = new HandshakeCmd();

		decodePacketHeader(cmd, in);

		cmd.setProtocolVersion(MySQLPacketBuffer.readByte(in));
		cmd.setServerVersion(MySQLPacketBuffer.readString(in));
		cmd.setThreadId(MySQLPacketBuffer.readInt(in));
		cmd.setScrambleBuff(MySQLPacketBuffer.readString(in));
		cmd.setServerCapabilities(MySQLPacketBuffer.readShort(in));
		cmd.setServerLanguage(MySQLPacketBuffer.readByte(in));
		cmd.setServerStatus(MySQLPacketBuffer.readShort(in));
		in.skip(13);
		cmd.setRestScrambleBuff(MySQLPacketBuffer.readString(in));

		return cmd;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		HandshakeCmd hsCmd = (HandshakeCmd) cmd;
		//49 = 4 + (1 + 4 + 8 + 1 + 2 + 1 + 2 + 13 + 13) 
		return 49 + (hsCmd.getServerVersion() == null ? 0 : hsCmd.getServerVersion().length());
	}

}
