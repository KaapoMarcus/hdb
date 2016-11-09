package com.netease.backend.db.common.codec.impl;



public class ObjectTypes {
	
	
	
	
	public static final int NULL = 1;

	
	public static final int BOOLEAN = 2;
	
	
	public static final int BYTE = 3;
	
	
	public static final int BYTE_ARRAY = 4;
	
	
	public static final int SHORT = 5;
	
	
	public static final int INTEGER = 6;
	
	
	public static final int LONG = 7;
	
	
	public static final int BIGINTEGER = 8;
	
	
	public static final int FLOAT = 9;
	
	
	public static final int DOUBLE = 10;
	
	
	public static final int BIGDECIMAL = 11;
	
	
	public static final int STRING = 12;
	
	
	public static final int DATE = 13;
	
	
	public static final int TIME = 14;
	
	
	public static final int TIMESTAMP = 15;
	
	
	public static final int UNSUPPORTED = -1;
	
	
	
	private ObjectTypes() {}
	
	
	
	public static int getObjectType(Object object) {
		if (object == null)
			return NULL;
		
		if (object instanceof java.lang.Boolean) {
			return BOOLEAN;
		} else if (object instanceof java.lang.Byte) {
			return BYTE;
		} else if (object instanceof byte[]) {
			return BYTE_ARRAY;
		} else if (object instanceof java.lang.Short) {
			return SHORT;
		} else if (object instanceof java.lang.Integer) {
			return INTEGER;
		} else if (object instanceof java.lang.Long) {
			return LONG;
		} else if (object instanceof java.math.BigInteger) {
			return BIGINTEGER;
		} else if (object instanceof java.lang.Float) {
			return FLOAT;
		} else if (object instanceof java.lang.Double) {
			return DOUBLE;
		} else if (object instanceof java.math.BigDecimal) {
			return BIGDECIMAL;
		} else if (object instanceof java.lang.String) {
			return STRING;
		} else if (object instanceof java.sql.Date) {
			return DATE;
		} else if (object instanceof java.sql.Time) {
			return TIME;
		} else if (object instanceof java.sql.Timestamp) {
			return TIMESTAMP;
		} else {
			return UNSUPPORTED;
		}
	}
	
	
	public static boolean isTypeValid(final int type) {
		return (type > UNSUPPORTED && type <= TIMESTAMP);
	}
	
	static final int TAG_TYPE_BITS = 5;
	static final int TAG_TYPE_MASK = (1 << TAG_TYPE_BITS) - 1;
	
	
	static int makeTag(final int fieldNumber, final int objectType) {
		return (fieldNumber << TAG_TYPE_BITS) | objectType;
	}

	
	static int getTagObjectType(final int tag) {
		return tag & TAG_TYPE_MASK;
	}

	
	public static int getTagFieldNumber(final int tag) {
		return tag >>> TAG_TYPE_BITS;
	}
}
