package com.netease.backend.db.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class ByteUtils {

	private static final int BUFFER_BLOCK_SIZE = 4 * 1024;
	
	public static byte[] convertStringToBytes(String s) throws SQLException {
		int len = s.length();
		if (len % 2 == 1) {
			throw Message.getSQLException(Message.U_HEX_STRING_ODD);
		}
		len /= 2;
		byte[] buff = new byte[len];
		try {
			for (int i = 0; i < len; i++) {
				String t = s.substring(i + i, i + i + 2);
				buff[i] = (byte) Integer.parseInt(t, 16);
			}
		} catch (NumberFormatException e) {
			throw Message.getSQLException(Message.U_HEX_STRING_WRONG);
		}
		return buff;
	}
	
	public static int getByteArrayHash(byte[] value) {
		int h = 1;
		for (int i = 0; i < value.length;) {
			h = 31 * h + value[i++];
		}
		return h;
	}
	
	public static String convertBytesToString(byte[] value) {
		StringBuilder buff = new StringBuilder(value.length*2);
		for (int i = 0; value!=null && i < value.length; i++) {
			int c = value[i] & 0xff;
			buff.append(Integer.toHexString((c >> 4) & 0xf));
			buff.append(Integer.toHexString(c & 0xf));
		}
		return buff.toString();
	}
	
	public static boolean compareSecure(byte[] test, byte[] good) {
		if((test==null) || (good==null)) {
			return (test == null) && (good == null);
		}
		if(test.length != good.length) {
			return false;
		}
		
		boolean correct = true;
		for(int i=0; i<good.length; i++) {
			if(test[i] != good[i]) {
				correct = false;
			}
		}
		return correct;
	}
	
	public static void clear(byte[] buff) {
		for(int i=0; i<buff.length; i++) {
			buff[i] = 0;
		}
	}
	
	public static int compareNotNull(byte[] data1, byte[] data2) {
		int len = Math.min(data1.length, data2.length);
		for (int i = 0; i < len; i++) {
			byte b = data1[i];
			byte b2 = data2[i];
			if (b != b2) {
				return b > b2 ? 1 : -1;
			}
		}
		int c = data1.length - data2.length;
		return c == 0 ? 0 : (c < 0 ? -1 : 1);
	}
	
	public static byte[] getBytesAndClose(InputStream in, int length) throws IOException {
		if(length <= 0) {
			length = Integer.MAX_VALUE;
		}
		int block = Math.min(BUFFER_BLOCK_SIZE, length);
		ByteArrayOutputStream out=new ByteArrayOutputStream(block);
		byte[] buff=new byte[block];
		while(length > 0) {
			int len = Math.min(block, length);
			len = in.read(buff, 0, len);
			if(len < 0) {
				break;
			}
			out.write(buff, 0, len);
			length -= len;
		}
		in.close();
		return out.toByteArray();
	}
	
	public static String convertToBinString(byte[] buff) {
		char[] chars = new char[buff.length];
		for(int i=0; i<buff.length; i++) {
			chars[i] = (char) (buff[i] & 0xff);
		}
		return new String(chars);
	}
	
	public static byte[] convertBinStringToBytes(String data) {
		byte[] buff = new byte[data.length()];
		for(int i=0; i<data.length(); i++) {
			buff[i] = (byte) (data.charAt(i) & 0xff);
		}
		return buff;
	}
	
	
	public static synchronized byte[] compressByteArray(byte[] ba, int len) {
		byte[] ba2 = new byte[len];
		for (int i = 0; i < len; i++) {
			ba2[i] = ba[i];
		}
		
		return ba2;
	}
	
	public static byte[] compressObject2Bytes(Object object) throws IOException {
		byte[] data = null;
		ByteArrayOutputStream bos = null;
		GZIPOutputStream gzout = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			gzout = new GZIPOutputStream(bos);
			oos = new ObjectOutputStream(gzout);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			gzout.close();
			data = bos.toByteArray();
			bos.close();
			return data;
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (gzout != null)
					gzout.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {}
		}
	}
	
	public static Object readCompressObject(byte[] data) throws Exception {
		Object object = null;
		ByteArrayInputStream bis = null;
		GZIPInputStream gzin = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(data);
			gzin = new GZIPInputStream(bis);
			ois = new ObjectInputStream(gzin);
			object = ois.readObject();
			ois.close();
			gzin.close();
			bis.close();
			return object;
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (gzin != null)
					gzin.close();
				if (bis != null)
					bis.close();
			} catch (IOException e) {}
		}
	}
	
	public static byte[] convertObject2Bytes(Object object) throws IOException {
		byte[] data = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			data = bos.toByteArray();
			bos.close();
			return data;
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {}
		}
	}
	
	public static Object convertBytes2Object(byte[] data) throws IOException, ClassNotFoundException {
		Object object = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bis);
			object = ois.readObject();
			ois.close();
			bis.close();
			return object;
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (bis != null)
					bis.close();
			} catch (IOException e) {}
		}
	}
	

	
	public static void i2bBig4(
		int val, byte[] out, int outOfs)
	{
		if (littleEndianUnaligned) {
			putInt(out, byteArrayOfs + outOfs, false, val);
		} else if (bigEndian && ((outOfs & 3) == 0)) {
			putInt(out, byteArrayOfs + outOfs, true, val);
		} else {
			out[outOfs] = (byte) (val >> 24);
			out[outOfs + 1] = (byte) (val >> 16);
			out[outOfs + 2] = (byte) (val >> 8);
			out[outOfs + 3] = (byte) (val);
		}
	}

	
	public static void i2bBig(
		int[] in, int inOfs, byte[] out, int outOfs, int len)
	{
		if (littleEndianUnaligned) {
			outOfs += byteArrayOfs;
			len += outOfs;
			while (outOfs < len) {
				putInt(out, outOfs, false, in[inOfs++]);
				outOfs += 4;
			}
		} else if (bigEndian && ((outOfs & 3) == 0)) {
			outOfs += byteArrayOfs;
			len += outOfs;
			while (outOfs < len) {
				putInt(out, outOfs, true, in[inOfs++]);
				outOfs += 4;
			}
		} else {
			len += outOfs;
			while (outOfs < len) {
				int i = in[inOfs++];
				out[outOfs++] = (byte) (i >> 24);
				out[outOfs++] = (byte) (i >> 16);
				out[outOfs++] = (byte) (i >> 8);
				out[outOfs++] = (byte) (i);
			}
		}
	}

	
	public static void b2iBig64(
		byte[] in, int inOfs, int[] out)
	{
		if (littleEndianUnaligned) {
			inOfs += byteArrayOfs;
			out[0] = getInt(in, (inOfs), false);
			out[1] = getInt(in, (inOfs + 4), false);
			out[2] = getInt(in, (inOfs + 8), false);
			out[3] = getInt(in, (inOfs + 12), false);
			out[4] = getInt(in, (inOfs + 16), false);
			out[5] = getInt(in, (inOfs + 20), false);
			out[6] = getInt(in, (inOfs + 24), false);
			out[7] = getInt(in, (inOfs + 28), false);
			out[8] = getInt(in, (inOfs + 32), false);
			out[9] = getInt(in, (inOfs + 36), false);
			out[10] = getInt(in, (inOfs + 40), false);
			out[11] = getInt(in, (inOfs + 44), false);
			out[12] = getInt(in, (inOfs + 48), false);
			out[13] = getInt(in, (inOfs + 52), false);
			out[14] = getInt(in, (inOfs + 56), false);
			out[15] = getInt(in, (inOfs + 60), false);
		} else if (bigEndian && ((inOfs & 3) == 0)) {
			inOfs += byteArrayOfs;
			out[0] = getInt(in, (inOfs), true);
			out[1] = getInt(in, (inOfs + 4), true);
			out[2] = getInt(in, (inOfs + 8), true);
			out[3] = getInt(in, (inOfs + 12), true);
			out[4] = getInt(in, (inOfs + 16), true);
			out[5] = getInt(in, (inOfs + 20), true);
			out[6] = getInt(in, (inOfs + 24), true);
			out[7] = getInt(in, (inOfs + 28), true);
			out[8] = getInt(in, (inOfs + 32), true);
			out[9] = getInt(in, (inOfs + 36), true);
			out[10] = getInt(in, (inOfs + 40), true);
			out[11] = getInt(in, (inOfs + 44), true);
			out[12] = getInt(in, (inOfs + 48), true);
			out[13] = getInt(in, (inOfs + 52), true);
			out[14] = getInt(in, (inOfs + 56), true);
			out[15] = getInt(in, (inOfs + 60), true);
		} else {
			b2iBig(in, inOfs, out, 0, 64);
		}
	}

	
	public static void b2iBig(
		byte[] in, int inOfs, int[] out, int outOfs, int len)
	{
		if (littleEndianUnaligned) {
			inOfs += byteArrayOfs;
			len += inOfs;
			while (inOfs < len) {
				out[outOfs++] = getInt(in, inOfs, false);
				inOfs += 4;
			}
		} else if (bigEndian && ((inOfs & 3) == 0)) {
			inOfs += byteArrayOfs;
			len += inOfs;
			while (inOfs < len) {
				out[outOfs++] = getInt(in, inOfs, true);
				inOfs += 4;
			}
		} else {
			len += inOfs;
			while (inOfs < len) {
				out[outOfs++] = ((in[inOfs + 3] & 0xff))
						| ((in[inOfs + 2] & 0xff) << 8)
						| ((in[inOfs + 1] & 0xff) << 16) | ((in[inOfs]) << 24);
				inOfs += 4;
			}
		}
	}

	public static String toHexString(
		int[] in, int ofs, int len)
	{
		final StringBuilder ss = new StringBuilder(in.length * 4);
		for (int i = ofs, j = 0; i < in.length && j < len; i++, j++) {
			ss.append(Integer.toHexString(in[i]));
		}
		return ss.toString();
	}

	
	public static int getInt(
		byte[] b, int offset, boolean asc)
	{
		if (b == null)
			throw new NullPointerException();
		if (b.length - offset < 4)
			throw new IllegalArgumentException();
		int r = 0;
		if (asc) {
			for (int i = b.length - offset - 1, j = 0; j < 4; i--, j++) {
				r <<= 8;
				r |= (b[i] & 0xff);
			}
		} else {
			for (int i = offset, j = 0; j < 4; i++, j++) {
				r <<= 8;
				r |= (b[i] & 0xff);
			}
		}
		return r;
	}

	
	public static void putInt(
		byte[] buf, int offset, boolean asc, int value)
	{
		if (buf == null)
			throw new NullPointerException();
		if (buf.length - offset < 4)
			throw new IllegalArgumentException();

		if (asc) {
			for (int i = offset, j = 0; i < buf.length && j < 4; i++, j++) {
				buf[i] = (byte) (value & 0xff);
				value >>= 8;
			}
		} else {
			for (int i = offset + 4 - 1, j = 0; i >= 0 && j < 4; i--, j++) {
				buf[i] = (byte) (value & 0xff);
				value >>= 8;
			}
		}
	}

	
	
	private static final boolean	littleEndianUnaligned;

	
	
	
	
	private static final boolean	bigEndian;

	private final static int		byteArrayOfs	= 0;

	static {
		final ByteOrder byteOrder = ByteOrder.nativeOrder();
		
		littleEndianUnaligned = (byteOrder == ByteOrder.LITTLE_ENDIAN);
		bigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
	}
}
