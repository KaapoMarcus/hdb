





























package com.netease.backend.db.common.codec.impl;

import java.io.IOException;
import java.io.InputStream;


public final class CodedInputStream {

	private final InputStream input;

	private CodedInputStream(final InputStream input) {
		this.input = input;
	}

	
	public static CodedInputStream newInstance(final InputStream input) {
		return new CodedInputStream(input);
	}

	
	public int readTag() throws IOException {

		int firstByte = input.read();
		if (firstByte == -1)
			return 0;

		byte tmp = (byte) firstByte;
		if (tmp >= 0) {
			return tmp;
		}
		int result = tmp & 0x7f;
		if ((tmp = readRawByte()) >= 0) {
			result |= tmp << 7;
		} else {
			result |= (tmp & 0x7f) << 7;
			if ((tmp = readRawByte()) >= 0) {
				result |= tmp << 14;
			} else {
				result |= (tmp & 0x7f) << 14;
				if ((tmp = readRawByte()) >= 0) {
					result |= tmp << 21;
				} else {
					result |= (tmp & 0x7f) << 21;
					result |= (tmp = readRawByte()) << 28;
					if (tmp < 0) {
						
						for (int i = 0; i < 5; i++) {
							if (readRawByte() >= 0) {
								return result;
							}
						}
						throw new IOException(
								"CodedInputStream encountered a malformed varint.");
					}
				}
			}
		}
		return result;
	}

	
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readRawLittleEndian64());
	}

	
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readRawLittleEndian32());
	}

	
	public long readUInt64() throws IOException {
		return readRawVarint64();
	}

	
	public long readInt64() throws IOException {
		return readRawVarint64();
	}

	
	public int readInt32() throws IOException {
		return readRawVarint32();
	}

	
	public long readFixed64() throws IOException {
		return readRawLittleEndian64();
	}

	
	public int readFixed32() throws IOException {
		return readRawLittleEndian32();
	}

	
	public boolean readBool() throws IOException {
		return readRawVarint32() != 0;
	}

	
	public String readString(final String charset) throws IOException {
		final int size = readRawVarint32();
		return new String(readRawBytes(size), charset);
	}

	
	public byte[] readBytes() throws IOException {
		final int size = readRawVarint32();
		if (size == 0) {
			return new byte[0];
		} else {
			return readRawBytes(size);
		}
	}

	
	public int readUInt32() throws IOException {
		return readRawVarint32();
	}

	
	public int readSFixed32() throws IOException {
		return readRawLittleEndian32();
	}

	
	public long readSFixed64() throws IOException {
		return readRawLittleEndian64();
	}

	
	public int readSInt32() throws IOException {
		return decodeZigZag32(readRawVarint32());
	}

	
	public long readSInt64() throws IOException {
		return decodeZigZag64(readRawVarint64());
	}

	

	
	public int readRawVarint32() throws IOException {
		byte tmp = readRawByte();
		if (tmp >= 0) {
			return tmp;
		}
		int result = tmp & 0x7f;
		if ((tmp = readRawByte()) >= 0) {
			result |= tmp << 7;
		} else {
			result |= (tmp & 0x7f) << 7;
			if ((tmp = readRawByte()) >= 0) {
				result |= tmp << 14;
			} else {
				result |= (tmp & 0x7f) << 14;
				if ((tmp = readRawByte()) >= 0) {
					result |= tmp << 21;
				} else {
					result |= (tmp & 0x7f) << 21;
					result |= (tmp = readRawByte()) << 28;
					if (tmp < 0) {
						
						for (int i = 0; i < 5; i++) {
							if (readRawByte() >= 0) {
								return result;
							}
						}
						throw new IOException(
								"CodedInputStream encountered a malformed varint.");
					}
				}
			}
		}
		return result;
	}

	
	public long readRawVarint64() throws IOException {
		int shift = 0;
		long result = 0;
		while (shift < 64) {
			final byte b = readRawByte();
			result |= (long) (b & 0x7F) << shift;
			if ((b & 0x80) == 0) {
				return result;
			}
			shift += 7;
		}
		throw new IOException(
				"CodedInputStream encountered a malformed varint.");
	}

	
	public int readRawLittleEndian32() throws IOException {
		byte[] b = readRawBytes(4);
		
		
		
		
		return (((int) b[0] & 0xff)) | (((int) b[1] & 0xff) << 8)
				| (((int) b[2] & 0xff) << 16) | (((int) b[3] & 0xff) << 24);
	}

	
	public long readRawLittleEndian64() throws IOException {
		byte[] b = readRawBytes(8);
		
		
		
		
		
		
		
		
		return (((long) b[0] & 0xff)) | (((long) b[1] & 0xff) << 8)
				| (((long) b[2] & 0xff) << 16) | (((long) b[3] & 0xff) << 24)
				| (((long) b[4] & 0xff) << 32) | (((long) b[5] & 0xff) << 40)
				| (((long) b[6] & 0xff) << 48) | (((long) b[7] & 0xff) << 56);
	}

	
	public static int decodeZigZag32(final int n) {
		return (n >>> 1) ^ -(n & 1);
	}

	
	public static long decodeZigZag64(final long n) {
		return (n >>> 1) ^ -(n & 1);
	}

	
	public byte readRawByte() throws IOException {
		int tmp = input.read();
		if (tmp == -1) {
			throw new IOException("To the end of inputstream.");
		}
		return (byte) tmp;
	}

	
	public byte[] readRawBytes(final int size) throws IOException {
		if (size < 0) {
			throw new IOException("size is negative.");
		}

		if (size > 0) {
			byte[] copy = new byte[size];
			if (input.read(copy) < size) {
				throw new IOException("To the end of inputstream.");
			}
			return copy;
		} else {
			return new byte[0];
		}
	}
}
