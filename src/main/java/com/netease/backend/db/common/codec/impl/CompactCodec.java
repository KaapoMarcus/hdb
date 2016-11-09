package com.netease.backend.db.common.codec.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netease.backend.db.common.codec.Codec;
import com.netease.backend.db.common.codec.DecodeException;
import com.netease.backend.db.common.codec.EncodeException;



public class CompactCodec implements Codec {
	
	
	public static final int DEFAULT_BUFFER_SIZE = 4096;
	
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	
	private CompactCodec() {
	}
	
	private static Codec instance = new CompactCodec();
	
	public static Codec getInstance() {
		return instance;
	}
	
	
	public byte[] encode(final int[] fieldNumbers, final List<Object> objectList)
			throws EncodeException {
		if (fieldNumbers == null || objectList == null
				|| fieldNumbers.length == 0 || objectList.size() == 0
				|| fieldNumbers.length != objectList.size())
			throw new EncodeException(
					"the size of tags and objectList are incorrect.");

		ByteArrayOutputStream bufferStream = new ByteArrayOutputStream(
				DEFAULT_BUFFER_SIZE);
		CodedOutputStream codedStream = CodedOutputStream
				.newInstance(bufferStream);

		try {
			for (int i = 0; i < fieldNumbers.length; i++) {
				if (fieldNumbers[i] < 0)
					throw new EncodeException("fieldNumbers[" + i + "] is "
							+ fieldNumbers[i] + ".");

				writeObject(fieldNumbers[i], objectList.get(i), codedStream);
			}
		} catch (IOException ioe) {
			
			throw new EncodeException("IOException in encoding, "
					+ ioe.getMessage(), ioe);
		}

		return bufferStream.toByteArray();
	}

	
	public List<Object> decode(int[] fieldNumbers, byte[] buffer)
			throws DecodeException {

		if (fieldNumbers == null || fieldNumbers.length == 0 || buffer == null
				|| buffer.length == 0)
			throw new DecodeException("tags or buffer is null or empty.");

		CodedInputStream codedStream = CodedInputStream
				.newInstance(new ByteArrayInputStream(buffer));
		Map<Integer, Object> originalObjectMap = new HashMap<Integer, Object>();

		
		try {
			int tag = 0;
			while ((tag = codedStream.readTag()) != 0) {
				if (tag < 0) {
					throw new DecodeException("invalid tag number: " + tag);
				}

				int objectType = ObjectTypes.getTagObjectType(tag);
				int fieldNumber = ObjectTypes.getTagFieldNumber(tag);
				if (!ObjectTypes.isTypeValid(objectType)) {
					throw new DecodeException("invalid object type: "
							+ objectType);
				}

				Object value = readObject(objectType, codedStream);
				originalObjectMap.put(fieldNumber, value);
			}
		} catch (IOException ioe) {
			throw new DecodeException("IOException in decoding, "
					+ ioe.getMessage(), ioe);
		}

		
		List<Object> objectList = new ArrayList<Object>(fieldNumbers.length);
		for (int num : fieldNumbers) {
			Object value = originalObjectMap.get(num);
			if (value == null && !originalObjectMap.containsKey(num)) {
				
				value = new EmptyObject(num);
			}
			objectList.add(value);
		}

		return objectList;
	}
	
	
	private static void writeObject(int fieldNumber, Object value,
			CodedOutputStream out) throws IOException, EncodeException {
		
		final int objectType = ObjectTypes.getObjectType(value);

		if (objectType == ObjectTypes.UNSUPPORTED)
			throw new EncodeException("unsupported java class type: "
					+ value.getClass().getName() + ".");

		
		out.writeTag(fieldNumber, objectType);

		switch (objectType) {
		case ObjectTypes.NULL: {
			
			break;
		}
		case ObjectTypes.BOOLEAN: {
			out.writeRawByte((Boolean) value ? 1 : 0);
			break;
		}
		case ObjectTypes.BYTE: {
			out.writeRawByte((Byte) value);
			break;
		}
		case ObjectTypes.BYTE_ARRAY: {
			out.writeBytesNoTag((byte[]) value);
			break;
		}
		case ObjectTypes.SHORT: {
			out.writeSInt32NoTag((Short) value);
			break;
		}
		case ObjectTypes.INTEGER: {
			out.writeSInt32NoTag((Integer) value);
			break;
		}
		case ObjectTypes.LONG: {
			out.writeSInt64NoTag((Long) value);
			break;
		}
		case ObjectTypes.FLOAT: {
			out.writeFloatNoTag((Float) value);
			break;
		}
		case ObjectTypes.DOUBLE: {
			out.writeDoubleNoTag((Double) value);
			break;
		}
		case ObjectTypes.BIGINTEGER:
		case ObjectTypes.BIGDECIMAL:
		case ObjectTypes.STRING: {
			Object stringObject = value;
			if (objectType != ObjectTypes.STRING) {
				
				stringObject = value.toString();
			}
			out.writeStringNoTag((String)stringObject, DEFAULT_CHARSET);
			break;
		}
		case ObjectTypes.DATE:
		case ObjectTypes.TIME:
		case ObjectTypes.TIMESTAMP: {
			
			long time = ((java.util.Date) value).getTime();
			out.writeInt64NoTag(time);
			break;
		}
		default: {
			throw new EncodeException("unsupported object type: " + objectType);
		}
		}
	}

	
	private static Object readObject(int objectType, CodedInputStream in)
			throws IOException, DecodeException {
		switch (objectType) {
		case ObjectTypes.NULL: {
			return null;
		}
		case ObjectTypes.BOOLEAN: {
			return Boolean.valueOf(in.readBool());
		}
		case ObjectTypes.BYTE: {
			return Byte.valueOf(in.readRawByte());
		}
		case ObjectTypes.BYTE_ARRAY: {
			return in.readBytes();
		}
		case ObjectTypes.SHORT: {
			return Short.valueOf((short) in.readSInt32());
		}
		case ObjectTypes.INTEGER: {
			return Integer.valueOf(in.readSInt32());
		}
		case ObjectTypes.LONG: {
			return Long.valueOf(in.readSInt64());
		}
		case ObjectTypes.FLOAT: {
			return Float.valueOf(in.readFloat());
		}
		case ObjectTypes.DOUBLE: {
			return Double.valueOf(in.readDouble());
		}
		case ObjectTypes.BIGINTEGER: {
			return new BigInteger(in.readString(DEFAULT_CHARSET));
		}
		case ObjectTypes.BIGDECIMAL: {
			return new BigDecimal(in.readString(DEFAULT_CHARSET));
		}
		case ObjectTypes.STRING: {
			return in.readString(DEFAULT_CHARSET);
		}
		case ObjectTypes.DATE: {
			return new java.sql.Date(in.readInt64());
		}
		case ObjectTypes.TIME: {
			return new java.sql.Time(in.readInt64());
		}
		case ObjectTypes.TIMESTAMP: {
			return new java.sql.Timestamp(in.readInt64());
		}
		default: {
			throw new DecodeException("unsupported object type: " + objectType);
		}
		}
	}
}
