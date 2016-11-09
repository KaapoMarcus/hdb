





























package com.netease.backend.db.common.codec.impl;

import java.io.IOException;
import java.io.OutputStream;


public final class CodedOutputStream {

	private final OutputStream output;

	private CodedOutputStream(final OutputStream output) {
		this.output = output;
	}

	
	public static CodedOutputStream newInstance(final OutputStream output) {
		return new CodedOutputStream(output);
	}

	
	public void writeDoubleNoTag(final double value) throws IOException {
		writeRawLittleEndian64(Double.doubleToRawLongBits(value));
	}

	
	public void writeFloatNoTag(final float value) throws IOException {
		writeRawLittleEndian32(Float.floatToRawIntBits(value));
	}

	
	public void writeUInt64NoTag(final long value) throws IOException {
		writeRawVarint64(value);
	}

	
	public void writeInt64NoTag(final long value) throws IOException {
		writeRawVarint64(value);
	}

	
	public void writeInt32NoTag(final int value) throws IOException {
		if (value >= 0) {
			writeRawVarint32(value);
		} else {
			
			writeRawVarint64(value);
		}
	}

	
	public void writeFixed64NoTag(final long value) throws IOException {
		writeRawLittleEndian64(value);
	}

	
	public void writeFixed32NoTag(final int value) throws IOException {
		writeRawLittleEndian32(value);
	}

	
	public void writeBoolNoTag(final boolean value) throws IOException {
		writeRawByte(value ? 1 : 0);
	}

	
	public void writeStringNoTag(final String value, final String charset)
			throws IOException {
		final byte[] bytes = value.getBytes(charset);
		writeRawVarint32(bytes.length);
		writeRawBytes(bytes);
	}

	
	public void writeBytesNoTag(byte[] value) throws IOException {
		writeRawVarint32(value.length);
		writeRawBytes(value);
	}

	
	public void writeUInt32NoTag(final int value) throws IOException {
		writeRawVarint32(value);
	}

	
	public void writeSFixed32NoTag(final int value) throws IOException {
		writeRawLittleEndian32(value);
	}

	
	public void writeSFixed64NoTag(final long value) throws IOException {
		writeRawLittleEndian64(value);
	}

	
	public void writeSInt32NoTag(final int value) throws IOException {
		writeRawVarint32(encodeZigZag32(value));
	}

	
	public void writeSInt64NoTag(final long value) throws IOException {
		writeRawVarint64(encodeZigZag64(value));
	}

	
	public void writeRawByte(final byte value) throws IOException {
		output.write(value);
	}

	
	public void writeRawByte(final int value) throws IOException {
		writeRawByte((byte) value);
	}

	
	public void writeRawBytes(final byte[] value) throws IOException {
		writeRawBytes(value, 0, value.length);
	}

	
	public void writeRawBytes(final byte[] value, int offset, int length)
			throws IOException {
		output.write(value, offset, length);
	}

	
	public void writeTag(final int fieldNumber, final int wireType)
			throws IOException {
		writeRawVarint32(ObjectTypes.makeTag(fieldNumber, wireType));
	}

	
	public void writeRawVarint32(int value) throws IOException {
		while (true) {
			if ((value & ~0x7F) == 0) {
				writeRawByte(value);
				return;
			} else {
				writeRawByte((value & 0x7F) | 0x80);
				value >>>= 7;
			}
		}
	}

	
	public void writeRawVarint64(long value) throws IOException {
		while (true) {
			if ((value & ~0x7FL) == 0) {
				writeRawByte((int) value);
				return;
			} else {
				writeRawByte(((int) value & 0x7F) | 0x80);
				value >>>= 7;
			}
		}
	}

	
	public void writeRawLittleEndian32(final int value) throws IOException {
		writeRawByte((value) & 0xFF);
		writeRawByte((value >> 8) & 0xFF);
		writeRawByte((value >> 16) & 0xFF);
		writeRawByte((value >> 24) & 0xFF);
	}

	
	public void writeRawLittleEndian64(final long value) throws IOException {
		writeRawByte((int) (value) & 0xFF);
		writeRawByte((int) (value >> 8) & 0xFF);
		writeRawByte((int) (value >> 16) & 0xFF);
		writeRawByte((int) (value >> 24) & 0xFF);
		writeRawByte((int) (value >> 32) & 0xFF);
		writeRawByte((int) (value >> 40) & 0xFF);
		writeRawByte((int) (value >> 48) & 0xFF);
		writeRawByte((int) (value >> 56) & 0xFF);
	}

	
	public static int encodeZigZag32(final int n) {
		
		return (n << 1) ^ (n >> 31);
	}

	
	public static long encodeZigZag64(final long n) {
		
		return (n << 1) ^ (n >> 63);
	}
}
