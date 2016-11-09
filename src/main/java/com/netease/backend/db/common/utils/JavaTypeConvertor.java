package com.netease.backend.db.common.utils;

import java.util.HashMap;
import java.util.Map;


public class JavaTypeConvertor {

	public static final class Null {
	}

	private static enum DataType {
		String(String.class),
		INTEGER(Integer.class),
		Int(int.class),
		BOOLEAN(Boolean.class),
		Bool(boolean.class),
		BYTE(Byte.class),
		Byte(byte.class),
		CHARACTER(Character.class),
		Char(char.class),
		DOUBLE(Double.class),
		Double(double.class),
		FLOAT(Float.class),
		Float(float.class),
		LONG(Long.class),
		Long(long.class),
		SHORT(Short.class),
		Short(short.class),
		UnKnown(Null.class);

		private final Class<?>	typeClass;

		DataType(
			Class<?> typeClass)
		{
			this.typeClass = typeClass;
		}

		
		public Class<?> getTypeClass() {
			return typeClass;
		}
	}

	private static final Map<Class<?>, DataType>	dataTypes;

	static {
		dataTypes = new HashMap<Class<?>, DataType>();
		for (final DataType dataType : DataType.values()) {
			dataTypes.put(dataType.getTypeClass(), dataType);
		}
	}

	
	public static <T> boolean isTypeSupported(
		Class<T> type)
	{
		if (type == null)
			throw new NullPointerException();
		return dataTypes.containsKey(type);
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T convertType(
		Class<T> type, String value)
	{
		if ((value == null) || "null".equals(value.toLowerCase()))
			return null;
		else {
			final DataType dataType = dataTypes.containsKey(type) ? dataTypes
					.get(type) : DataType.UnKnown;
			switch (dataType) {
			case INTEGER:
				return (T) (Integer.valueOf(value));
			case Int:
				return (T) (Integer.valueOf(value));
			case BOOLEAN:
				return (T) (Boolean.valueOf(value));
			case Bool:
				return (T) (Boolean.valueOf(value));
			case BYTE:
				return (T) (Byte.valueOf(value));
			case Byte:
				return (T) (Byte.valueOf(value));
			case CHARACTER:
				return (T) (Character.valueOf(value.charAt(0)));
			case Char:
				return (T) (Character.valueOf(value.charAt(0)));
			case DOUBLE:
				return (T) (Double.valueOf(value));
			case Double:
				return (T) (Double.valueOf(value));
			case FLOAT:
				return (T) (Float.valueOf(value));
			case Float:
				return (T) (Float.valueOf(value));
			case LONG:
				return (T) (Long.valueOf(value));
			case Long:
				return (T) (Long.valueOf(value));
			case SHORT:
				return (T) (Short.valueOf(value));
			case Short:
				return (T) (Short.valueOf(value));
			case String:
				return (T) (value);
			case UnKnown:
			default:
				throw new IllegalArgumentException("Unsupported data type: "
						+ type);
			}

		}
	}
}
