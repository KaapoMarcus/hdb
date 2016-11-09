package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import org.apache.mina.core.buffer.IoBuffer;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.codec.EncodeException;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * MySQLͨ�Ű��࣬���ڶ�MySQLͨ�Ű��ʽ���б���ͽ������
 * ��������Ӧ��֤�̰߳�ȫ
 * 
 *
 */
public abstract class MySQLPacket {
	// ��ͷ��С
	public static final int LENGTH_BYTE_SIZE = 4;
	
	protected BufferFactory bufferFactory;
	
	public MySQLPacket(BufferFactory factory) {
		this.bufferFactory = factory;
	}
	
	/**
	 * �Է�������MySQL����������б��������������л���buffer��
	 * @param cmd ��������������
	 * @param out ͨ��buffer
	 * @throws UnsupportedEncodingException
	 * @throws CharacterCodingException
	 */
	public abstract ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException, EncodeException;

	/**
	 * �Կͻ��˵�MySQL����������н����������buffer�ж�ȡ����
	 * @param in ͨ��buffer
	 * @param context ��������Ϣ
	 * @return ������MySQL����
	 * @throws CharacterCodingException
	 * @throws UnsupportedEncodingException 
	 */
	public abstract MySQLCmd decode(ComBuffer in, SessionContext context)
			throws CharacterCodingException, DecodeException, UnsupportedEncodingException;

	/**
	 * ��ȡÿ��MySQL�����Ӧ�İ��С
	 * @param cmd MySQL����
	 * @return ���С����byteΪ��λ
	 */
	public abstract int getPacketSize(MySQLCmd cmd);
	
	/**
	 * ��ɻ����
	 * @param size
	 * @return
	 */
	protected ComBuffer createBuffer(int size) {
		return this.bufferFactory.getBuffer(size);
	}

	/**
	 * Ԥ�����ͷ�����Ԥ��Ϊ0����������Ҫ����ǰͨ������ʽ������ȷ�İ��
	 * @param cmd MySQL����
	 * @param buffer ͨ��buffer
	 */
	public static void encodePacketHeader(ComBuffer buffer, MySQLCmd cmd) {
		MySQLPacketBuffer.writeInt3(buffer, 0);
		MySQLPacketBuffer.writeByte(buffer, cmd.getNumber());
	}

	/**
	 * �����ͷ
	 * @param cmd MySQL����
	 * @param in ͨ��buffer
	 */
	public static void decodePacketHeader(MySQLCmd cmd, ComBuffer in) {
		cmd.setLength(MySQLPacketBuffer.readInt3(in));
		cmd.setNumber(MySQLPacketBuffer.readByte(in));
	}

	/**
	 * ���ͨ�Ż���buffer�е�����Ƿ��㹻��һ�����������
	 * @param in ͨ��buffer
	 * @return
	 */
	public static boolean checkPacketLength(IoBuffer in) {
		final int remain = in.remaining();
		if (remain < LENGTH_BYTE_SIZE)
			return false;
		/*if (remain > maxPacketSize)
			throw new IllegalArgumentException("packet size is " + remain
					+ ", maxPacketSize is " + maxPacketSize);*/
		//ǰ3�ֽ��ǰ��
		int pos = in.position();
		int dataLength = (in.get(pos) & 0xff) | ((in.get(pos + 1) & 0xff) << 8)
				| ((in.get(pos + 2) & 0xff) << 16);
		return (remain - LENGTH_BYTE_SIZE) >= dataLength;
	}
}
