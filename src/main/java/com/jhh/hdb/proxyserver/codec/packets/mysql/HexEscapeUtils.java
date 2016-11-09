package com.jhh.hdb.proxyserver.codec.packets.mysql;

/**
 * �԰������ֵ�Ķ���תΪ�ַ��sql����ֽ�������д��?
 * 
 *
 */
public class HexEscapeUtils {

	private final static byte[] HEX_DIGITS = new byte[] { (byte) '0',
		(byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
		(byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A',
		(byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };
	
	/**
	 * �����еĵ������������ֵ����16�����ַ�ת����
	 * <pre>
	 * ��Ĵ���������£�
	 * 1 ������Ч�ĵ����'\''(δ��ת��)���������������ǰһ�ַ���'x'��'X'���ֽ�������ȡ��������������ǰ��'_binary'��ʶ����ȥ��ñ�ʶ��
	 * 2 ����ȡ���������ݽ��з�ת�壬Ȼ����16�����ַ���д��?ÿ1�ֽ�ת��2��16���Ƶ��ַ��Ӧ���ֽڣ�
	 * 3 ���������ֽ������õ���������������������ǰ����'X'��Ϊ�ô��?ʽ�ı�ʶ�����Żص�ԭ�ֽ������е���Ӧλ�ã�
	 * 
	 * ��ת������Ǹ��Connector/J_5.0.8��PreparedStatement���е�ת�����(��MySQL_5.1.47��character.c��escape_string_for_mysql()һ��)�����з�����
	 * </pre>
	 * @param in
	 * @return
	 */
	public final static byte[] hexEscape(byte[] in) {
		if (in == null || in.length == 0)
			return null;
		
		ByteBuffer outBuf = new ByteBuffer(in.length);
		ByteBuffer hexBuf = null;
		boolean inSingleQuote = false;
		boolean inEscape = false;
		boolean inBinary = false;
		
		for (int i = 0; i < in.length; i++) {
			final byte curByte = in[i];
			
			if (curByte == '\\') {
				if (inEscape) {
					inEscape = false;
					if (inBinary) {
						//��Ҫת�뷶Χ�ڵ�ת���������2����д��1��
						hexBuf.put(curByte);
					}
				} else {
					inEscape = true;
				}
				
				if (!inBinary) {
					//������Ҫת��ķ�Χ�ڵ��ֽڣ�������Ҫ����
					outBuf.put(curByte);
				}
				
				continue;
			}
			
			if (inBinary) {
				if (inEscape) {
					switch (curByte) {
					case '0':
						hexBuf.put((byte) 0);
						break;
					case 'n':
						hexBuf.put((byte) '\n');
						break;
					case 'r':
						hexBuf.put((byte) '\r');
						break;
					case 'Z':
						hexBuf.put((byte) '\032');
						break;
					/* case '"', '\'', '\\' :ת����Ѿ���ȥ���������⴦�� */
					default:
						hexBuf.put(curByte);
					}
				} else {
					if (curByte == '\'') {
						inSingleQuote = false;
						inBinary = false;
						
						outBuf.put((byte) 'X');
						outBuf.put((byte) '\'');
						hexEscapeBlock(hexBuf.getBytes(), outBuf);
						outBuf.put((byte) '\'');
						
						hexBuf = null;
					} else {
						hexBuf.put(curByte);
					}
				}
			} else {
				if (curByte == '\'') {
					if (inEscape) {
						outBuf.put(curByte);
					} else if (inSingleQuote) {
						inSingleQuote = false;
						outBuf.put(curByte);
					} else {
						//����7���ֽڣ�����Ƿ�Ϊ'_binary'
						byte[] rewindBytes = outBuf.rewind(7);
						
						if (rewindBytes == null) {
							//��������7���ֽڶ�û�У��Ǳ�����ǲ���������
							throw new IllegalArgumentException("sql is illegal for hex escape");
						}
						
						if (!isBinaryMark(rewindBytes)) {
							//������'_binary'������Ҫ��д�ػ�����
							for (byte b : rewindBytes)
								outBuf.put(b);
						}
						
						if (rewindBytes[6] == 'x' || rewindBytes[6] == 'X') {
							//�Ѿ�����16�����ַ����Ĳ��ô���
							inSingleQuote = true;
							inBinary = false;
							outBuf.put(curByte);
						} else {
							inSingleQuote = true;
							inBinary = true;
							hexBuf = new ByteBuffer(20);//20û����������
							rewindBytes = null;
						}
					}
				} else {
					outBuf.put(curByte);
				}
			}
			
			if (inEscape)
				inEscape = false;
		}
		return outBuf.getBytes();
	}
	
	private final static void hexEscapeBlock(byte[] src, ByteBuffer dest) {
		if (src == null || src.length == 0)
			return;
		for (byte b : src) {
			int lowBits = (b & 0xff) / 16;
			int highBits = (b & 0xff) % 16;
			
			dest.put(HEX_DIGITS[lowBits]);
			dest.put(HEX_DIGITS[highBits]);
		}
	}
	
	private final static boolean isBinaryMark(byte[] in) {
		if (in == null || in.length < 7)
			return false;
			
		if (in[0] == '_' 
				&& (in[1] == 'b' || in[1] == 'B')
				&& (in[2] == 'i' || in[2] == 'I')
				&& (in[3] == 'n' || in[3] == 'N')
				&& (in[4] == 'a' || in[4] == 'A')
				&& (in[5] == 'r' || in[5] == 'R')
				&& (in[6] == 'y' || in[6] == 'Y')) {
			return true;
		}
		
		return false;
	}
	
}


class ByteBuffer {
	private byte[] buf;
	private int size;
	private int pos;
	
	ByteBuffer(int size) {
		this.buf = new byte[size];
		this.size = size;
		this.pos = 0;
	}
	
	void put(byte b) {
		if (pos == size) {
			size = size * 2;
			byte[] newBuf = new byte[size];
			System.arraycopy(buf, 0, newBuf, 0, pos);
			buf = newBuf;
		}
		buf[pos++] = b;
	}
	
	byte[] getBytes() {
		byte[] newBuf = new byte[pos];
		System.arraycopy(buf, 0, newBuf, 0, pos);
		return newBuf;
	}
	
	byte[] rewind(int length) {
		if (pos < length)
			return null;
		byte[] newBuf = new byte[length];
		System.arraycopy(buf, pos - length, newBuf, 0, length);
		pos = pos - length;
		return newBuf;
	}
}
