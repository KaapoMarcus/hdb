package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;

import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.server.GlobalContext;

/**
 * MySQLͨ�Ű��ȡ������
 * �������͵���ݣ��Ե�λ���Ƚ��д��
 * �ַ����͵���ݣ�����\0����β��ʶ��
 * 
 *
 */
public class MySQLPacketBuffer {
	
	/**
	 * ��ʾnull��Ӧ�ĳ���ֵ
	 */
	public static int NULL_LENGTH = -1;
	
	/**
	 * ��buffer�ж�ȡbyte
	 * @param buffer
	 * @return
	 */
	public static final byte readByte(ComBuffer buffer) {
		return buffer.get();
	}

	/**
	 * ��buffer��д��byte
	 * @param buffer
	 * @param b
	 */
	public static final void writeByte(ComBuffer buffer, byte b) {
		buffer.put(b);
	}

	/**
	 * ��buffer�ж�ȡshort
	 * @param buffer
	 * @return
	 */
	public static final short readShort(ComBuffer buffer) {
		return (short) ((buffer.get() & 0xff) | ((buffer.get() & 0xff) << 8));
	}

	/**
	 * ��buffer��д��short
	 * @param buffer
	 * @param i
	 */
	public static final void writeShort(ComBuffer buffer, short i) {
		buffer.put((byte) (i & 0xff));
		buffer.put((byte) (i >>> 8));
	}

	/**
	 * ��buffer�ж�ȡint
	 * @param buffer
	 * @return
	 */
	public static final int readInt(ComBuffer buffer) {
		return ((int) buffer.get() & 0xff) | (((int) buffer.get() & 0xff) << 8)
				| ((int) (buffer.get() & 0xff) << 16)
				| ((int) (buffer.get() & 0xff) << 24);
	}

	/**
	 * ��buffer��д��int
	 * @param buffer
	 * @param i
	 */
	public static final void writeInt(ComBuffer buffer, int i) {
		buffer.put((byte) (i & 0xff));
		buffer.put((byte) (i >>> 8));
		buffer.put((byte) (i >>> 16));
		buffer.put((byte) (i >>> 24));
	}

	/**
	 * ��buffer�ж�ȡint�����ֻռ��3��byte
	 * @param buffer
	 * @return
	 */
	public static final int readInt3(ComBuffer buffer) {
		return (buffer.get() & 0xff) | ((buffer.get() & 0xff) << 8)
				| ((buffer.get() & 0xff) << 16);
	}

	/**
	 * ��buffer�ж�ȡlong
	 * @param buffer
	 * @return
	 */
	public static final long readLong(ComBuffer buffer) {
		return (buffer.get() & 0xff) | ((long) (buffer.get() & 0xff) << 8)
				| ((long) (buffer.get() & 0xff) << 16)
				| ((long) (buffer.get() & 0xff) << 24)
				| ((long) (buffer.get() & 0xff) << 32)
				| ((long) (buffer.get() & 0xff) << 40)
				| ((long) (buffer.get() & 0xff) << 48)
				| ((long) (buffer.get() & 0xff) << 56);
	}

	/**
	 * ��buffer�ж�ȡnull-terminated string�����û��/0����һֱ��ȡ��buffer���һ���ַ�
	 * @param buffer
	 * @return
	 * @throws CharacterCodingException
	 */
	public static final String readString(ComBuffer buffer)
			throws CharacterCodingException {
		return buffer.getString(GlobalContext.getInstance().getConfig()
				.getCharset().newDecoder());
	}

	/**
	 * ��buffer��д��double
	 * @param buffer
	 * @param d
	 */
	public static final void writeDouble(ComBuffer buffer, double d) {
		long l = Double.doubleToLongBits(d);
		writeLong(buffer, l);
	}

	/**
	 * ��buffer�ж�ȡdouble
	 * @param buffer
	 * @return
	 */
	public static double readDouble(ComBuffer buffer) {
		long result = readLong(buffer);
		return Double.longBitsToDouble(result);
	}

	/**
	 * ��buffer��д��float
	 * @param buffer
	 * @param f
	 */
	public static final void writeFloat(ComBuffer buffer, float f) {
		int i = Float.floatToIntBits(f);
		buffer.put((byte) (i & 0xff));
		buffer.put((byte) (i >>> 8));
		buffer.put((byte) (i >>> 16));
		buffer.put((byte) (i >>> 24));
	}

	/**
	 * ��buffer�ж�ȡfloat
	 * @param buffer
	 * @return
	 */
	public static final float readFloat(ComBuffer buffer) {
		int result = ((int) buffer.get() & 0xff)
				| (((int) buffer.get() & 0xff) << 8)
				| ((int) (buffer.get() & 0xff) << 16)
				| ((int) (buffer.get() & 0xff) << 24);
		return Float.intBitsToFloat(result);
	}

	/**
	 * ��buffer��д��int�����ֻռ��3��byte
	 * @param buffer
	 * @param i
	 */
	public static final void writeInt3(ComBuffer buffer, int i) {
		buffer.put((byte) (i & 0xff));
		buffer.put((byte) (i >>> 8));
		buffer.put((byte) (i >>> 16));
	}

	/**
	 * ��buffer��д��long
	 * @param buffer
	 * @param i
	 */
	public static final void writeLong(ComBuffer buffer, long i) {
		buffer.put((byte) (i & 0xff));
		buffer.put((byte) (i >>> 8));
		buffer.put((byte) (i >>> 16));
		buffer.put((byte) (i >>> 24));
		buffer.put((byte) (i >>> 32));
		buffer.put((byte) (i >>> 40));
		buffer.put((byte) (i >>> 48));
		buffer.put((byte) (i >>> 56));
	}

	/**
	 * ��buffer��д��null-terminated string
	 * @param buffer
	 * @param s
	 * @throws UnsupportedEncodingException
	 * @throws CharacterCodingException
	 */
	public static final void writeString(ComBuffer buffer, String s)
			throws UnsupportedEncodingException, CharacterCodingException {
		buffer.putString(s, GlobalContext.getInstance().getConfig()
				.getCharset().newEncoder());
		buffer.put((byte) 0);
	}
	
	/**
	 * ��buffer��д�벻�������string
	 * @param buffer
	 * @param s
	 * @throws UnsupportedEncodingException
	 * @throws CharacterCodingException
	 */
	public static final void writeStringWithoutTermination(ComBuffer buffer, String s)
			throws UnsupportedEncodingException, CharacterCodingException {
		buffer.putString(s, GlobalContext.getInstance().getConfig()
				.getCharset().newEncoder());
	}
	
	
	/**
	 * ��ȡ�����˳��ȱ�����ַ�
	 * @param buffer
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static final String readLengthCodedString(ComBuffer buffer) throws UnsupportedEncodingException {
		long strLength = readLengthCodedBinary(buffer);
		//null�Ϳ��ַ���Ҫ��ִ���
		if (strLength == NULL_LENGTH)
			return null;
		else if (strLength == 0)
			return "";
		else
			return readString(buffer, strLength);
	}
	
	/**
	 * ����ָ�����ֽڳ��ȶ�ȡ������ΪString
	 * @param buffer
	 * @param byteLength �ַ���ֽ���
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static final String readString(ComBuffer buffer, long byteLength) throws UnsupportedEncodingException {
		if (byteLength <= NULL_LENGTH)
			return null;
		
		byte[] bytes = readBytes(buffer, byteLength);
		//����ϵͳĬ���ַ�
		return new String(bytes, GlobalContext.getInstance().getConfig()
				.getCharset().name());
	}
	
	/**
	 * ��buffer��д������˳��ȱ�����ַ���һ�����ǳ��ȣ��ڶ�������ʵ�ʵ��ַ�ֵ���Ҳ�������
	 * @param buffer
	 * @param s
	 * @throws UnsupportedEncodingException
	 * @throws CharacterCodingException
	 */
	public static final void writeLengthCodedString(ComBuffer buffer, String s)
			throws UnsupportedEncodingException, CharacterCodingException {
		if (s != null) {
			writeLengthCodedBinary(buffer, s.getBytes().length);
			writeStringWithoutTermination(buffer, s);
		} else {
			writeByte(buffer, (byte) 251);
		}
	}
	
	/**
	 * ��ȡ�����˳��ȱ������ֵ��
	 * <pre>
	 * �����˳��ȱ������ֵ������ͨ�����һ���ֽڵ�ֵ����ȷ������ֵ��ռ�õ��ֽ���
	 * Value Of     # Of Bytes  Description
	 * First Byte   Following
	 * ----------   ----------- -----------
	 * 0-250        0           = value of first byte
	 * 251          0           column value = NULL. Only appropriate in a Row Data Packet
	 * 252          2           = value of following 16-bit word
	 * 253          3           = value of following 24-bit word
	 * 254          8           = value of following 64-bit word
	 * <pre/>
	 * @param buffer
	 * @return
	 */
	public static final long readLengthCodedBinary(ComBuffer buffer) {
		int firstByte = readByte(buffer) & 0xff;
		switch (firstByte) {
			case 251:
				return NULL_LENGTH;
			case 252:
				return readShort(buffer);
			case 253:
				return readInt3(buffer);
			case 254:
				return readLong(buffer);
			default:
				return firstByte;
		}
	}
	
	/**
	 * ����ֵ�Ա䳤����ʽд�뻺����
	 * @param buffer
	 * @param value
	 */
	public static final void writeLengthCodedBinary(ComBuffer buffer, long value) {
		if (value < 251) {
			writeByte(buffer, (byte) value);
		} else if (value < 65536L) {
			writeByte(buffer, (byte) 252);
			writeShort(buffer, (short) value);
		} else if (value < 16777216L) {
			writeByte(buffer, (byte) 253);
			writeInt3(buffer, (int) value);
		} else {
			writeByte(buffer, (byte) 254);
			writeLong(buffer, value);
		}
	}
	
	public static final void writeLengthCodedBytes(ComBuffer buffer, byte[] bytes) {
		if (bytes != null) {
			writeLengthCodedBinary(buffer, bytes.length);
			writeBytes(buffer, bytes);
		} else {
			writeByte(buffer, (byte) 251);
		}
	}
	
	/**
	 * �������ֽڸ��Ƶķ�ʽ���ӻ�����ж�ȡָ���������ֽ�
	 * @param buffer
	 * @param length
	 * @return
	 */
	public static final byte[] readBytes(ComBuffer buffer, long length) {
		if (length <= NULL_LENGTH)
			return null;
		byte[] copy = new byte[(int)length];
		//���ַ���Ч�ʵͣ�����ײ��java.nio.ByteBuffer.get(byte[] dest)
		//����Ҳ�ǲ������ֽڸ��Ƶķ�ʽ�����޸���ʵķ���
		for (int i = 0; i < length; i++)
			copy[i] = buffer.get();
		return copy;
	}
	
	/**
	 * �������ָ��Ƶķ�ʽ���򻺳����д���ֽ�����
	 * @param buffer
	 * @param content
	 */
	public static final void writeBytes(ComBuffer buffer, byte[] content) {
		if (content == null || content.length < 1)
			return;
		for (int i = 0; i < content.length; i++)
			buffer.put(content[i]);
	}
}
