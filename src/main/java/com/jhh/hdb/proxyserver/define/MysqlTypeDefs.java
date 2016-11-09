
package com.jhh.hdb.proxyserver.define;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


public final class MysqlTypeDefs {


	public static final int FIELD_LIST = 4;

	public static final int FIELD_TYPE_BIT = 16;

	public static final int FIELD_TYPE_BLOB = 252;

	public static final int FIELD_TYPE_DATE = 10;

	public static final int FIELD_TYPE_DATETIME = 12;

	public static final int FIELD_TYPE_DECIMAL = 0;

	public static final int FIELD_TYPE_DOUBLE = 5;

	public static final int FIELD_TYPE_ENUM = 247;

	public static final int FIELD_TYPE_FLOAT = 4;

	public static final int FIELD_TYPE_GEOMETRY = 255;

	public static final int FIELD_TYPE_INT24 = 9;

	public static final int FIELD_TYPE_LONG = 3;

	public static final int FIELD_TYPE_LONG_BLOB = 251;

	public static final int FIELD_TYPE_LONGLONG = 8;

	public static final int FIELD_TYPE_MEDIUM_BLOB = 250;

	public static final int FIELD_TYPE_NEW_DECIMAL = 246;

	public static final int FIELD_TYPE_NEWDATE = 14;

	public static final int FIELD_TYPE_NULL = 6;

	public static final int FIELD_TYPE_SET = 248;

	public static final int FIELD_TYPE_SHORT = 2;

	public static final int FIELD_TYPE_STRING = 254;

	public static final int FIELD_TYPE_TIME = 11;

	public static final int FIELD_TYPE_TIMESTAMP = 7;

	public static final int FIELD_TYPE_TINY = 1;

	public static final int FIELD_TYPE_TINY_BLOB = 249;

	public static final int FIELD_TYPE_VAR_STRING = 253;

	public static final int FIELD_TYPE_VARCHAR = 15;

	public static final int FIELD_TYPE_YEAR = 13;


	public static int javaType2MysqlType(int javaType) {

		switch (javaType) {
		case Types.NUMERIC:
			/*return MysqlDefs.FIELD_TYPE_LONG;*/
			//不管是否有小数位，NUMERIC都转为DECIMAL
			return MysqlTypeDefs.FIELD_TYPE_NEW_DECIMAL;

		case Types.DECIMAL:
			return MysqlTypeDefs.FIELD_TYPE_NEW_DECIMAL;

		case Types.TINYINT:
			return MysqlTypeDefs.FIELD_TYPE_TINY;

		case Types.SMALLINT:
			return MysqlTypeDefs.FIELD_TYPE_SHORT;

		case Types.INTEGER:
			return MysqlTypeDefs.FIELD_TYPE_LONG;

		case Types.REAL:
			return MysqlTypeDefs.FIELD_TYPE_FLOAT;

		case Types.DOUBLE:
			return MysqlTypeDefs.FIELD_TYPE_DOUBLE;

		case Types.NULL:
			return MysqlTypeDefs.FIELD_TYPE_NULL;

		case Types.TIMESTAMP:
			return MysqlTypeDefs.FIELD_TYPE_TIMESTAMP;

		case Types.BIGINT:
			return MysqlTypeDefs.FIELD_TYPE_LONGLONG;

		case Types.DATE:
			return MysqlTypeDefs.FIELD_TYPE_DATE;

		case Types.TIME:
			return MysqlTypeDefs.FIELD_TYPE_TIME;
			
		case Types.BINARY:
			return MysqlTypeDefs.FIELD_TYPE_STRING;

		case Types.VARBINARY:
			return MysqlTypeDefs.FIELD_TYPE_TINY_BLOB;

		case Types.LONGVARBINARY:
			return MysqlTypeDefs.FIELD_TYPE_BLOB;

		case Types.VARCHAR:
			return MysqlTypeDefs.FIELD_TYPE_VAR_STRING;

		case Types.CHAR:
			return MysqlTypeDefs.FIELD_TYPE_STRING;

		case Types.BIT:
			return MysqlTypeDefs.FIELD_TYPE_BIT;
			
		case Types.CLOB:
		case Types.LONGVARCHAR:
			return MysqlTypeDefs.FIELD_TYPE_VAR_STRING;
			
		case Types.BLOB:
			return MysqlTypeDefs.FIELD_TYPE_BLOB;

		default:
			return MysqlTypeDefs.FIELD_TYPE_VAR_STRING;
		}

	}
	
	/**
	 * 根据displaysize计算对应的字段长度
	 * @param javaType
	 * @param charset
	 * @param displaySize
	 * @return
	 */
	public static int getMysqlFieldLength(int javaType, String charset,
			int displaySize) {
		int length = displaySize;
		// 字符型需要考虑不同字符集中每个字符的字节数不同
		if (javaType == Types.CHAR || javaType == Types.VARCHAR
				|| javaType == Types.LONGVARCHAR)
			length = displaySize * CharsetMappingTool.getMaxBytesPerChar(charset) + 1;
		if (length >= Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return length;
	}
	
	/**
	 * Maps the given MySQL type to the correct JDBC type.
	 */

	public static int mysqlType2JavaType(int mysqlType) {
		int jdbcType;

		switch (mysqlType) {
		case MysqlTypeDefs.FIELD_TYPE_NEW_DECIMAL:
		case MysqlTypeDefs.FIELD_TYPE_DECIMAL:
			jdbcType = Types.DECIMAL;

			break;

		case MysqlTypeDefs.FIELD_TYPE_TINY:
			jdbcType = Types.TINYINT;

			break;

		case MysqlTypeDefs.FIELD_TYPE_SHORT:
			jdbcType = Types.SMALLINT;

			break;

		case MysqlTypeDefs.FIELD_TYPE_LONG:
			jdbcType = Types.INTEGER;

			break;

		case MysqlTypeDefs.FIELD_TYPE_FLOAT:
			jdbcType = Types.REAL;

			break;

		case MysqlTypeDefs.FIELD_TYPE_DOUBLE:
			jdbcType = Types.DOUBLE;

			break;

		case MysqlTypeDefs.FIELD_TYPE_NULL:
			jdbcType = Types.NULL;

			break;

		case MysqlTypeDefs.FIELD_TYPE_TIMESTAMP:
			jdbcType = Types.TIMESTAMP;

			break;

		case MysqlTypeDefs.FIELD_TYPE_LONGLONG:
			jdbcType = Types.BIGINT;

			break;

		case MysqlTypeDefs.FIELD_TYPE_INT24:
			jdbcType = Types.INTEGER;

			break;

		case MysqlTypeDefs.FIELD_TYPE_DATE:
			jdbcType = Types.DATE;

			break;

		case MysqlTypeDefs.FIELD_TYPE_TIME:
			jdbcType = Types.TIME;

			break;

		case MysqlTypeDefs.FIELD_TYPE_DATETIME:
			jdbcType = Types.TIMESTAMP;

			break;

		case MysqlTypeDefs.FIELD_TYPE_YEAR:
			jdbcType = Types.DATE;

			break;

		case MysqlTypeDefs.FIELD_TYPE_NEWDATE:
			jdbcType = Types.DATE;

			break;

		case MysqlTypeDefs.FIELD_TYPE_ENUM:
			jdbcType = Types.CHAR;

			break;

		case MysqlTypeDefs.FIELD_TYPE_SET:
			jdbcType = Types.CHAR;

			break;

		case MysqlTypeDefs.FIELD_TYPE_TINY_BLOB:
			jdbcType = Types.VARBINARY;

			break;

		case MysqlTypeDefs.FIELD_TYPE_MEDIUM_BLOB:
			jdbcType = Types.LONGVARBINARY;

			break;

		case MysqlTypeDefs.FIELD_TYPE_LONG_BLOB:
			jdbcType = Types.LONGVARBINARY;

			break;

		case MysqlTypeDefs.FIELD_TYPE_BLOB:
			jdbcType = Types.LONGVARBINARY;

			break;

		case MysqlTypeDefs.FIELD_TYPE_VAR_STRING:
		case MysqlTypeDefs.FIELD_TYPE_VARCHAR:
			jdbcType = Types.VARCHAR;

			break;

		case MysqlTypeDefs.FIELD_TYPE_STRING:
			jdbcType = Types.CHAR;

			break;
		case MysqlTypeDefs.FIELD_TYPE_GEOMETRY:
			jdbcType = Types.BINARY;

			break;
		case MysqlTypeDefs.FIELD_TYPE_BIT:
			jdbcType = Types.BIT;

			break;
		default:
			jdbcType = Types.VARCHAR;
		}

		return jdbcType;
	}
	

	/**
	 * 根据小数精度，将NUMERIC转为LONG或DECIMAL。
	 * @param javaType
	 * @param scale
	 * @return
	 */
	
	public static int javaTypeDetect(int javaType, int scale) {
		switch (javaType) {
		case Types.NUMERIC: {
			if (scale > 0) {
				return Types.DECIMAL;
			} else {
				return javaType;
			}
		}
		default: {
			return javaType;
		}
		}
	}
	


	/**
	 * Maps the given MySQL type to the correct JDBC type.
	 */
	
	static int mysqlTypeToJavaType(String mysqlType) {
		if (mysqlType.equalsIgnoreCase("BIT")) {
			return mysqlType2JavaType(FIELD_TYPE_BIT);
		} else if (mysqlType.equalsIgnoreCase("TINYINT")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_TINY);
		} else if (mysqlType.equalsIgnoreCase("SMALLINT")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_SHORT);
		} else if (mysqlType.equalsIgnoreCase("MEDIUMINT")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_INT24);
		} else if (mysqlType.equalsIgnoreCase("INT") || mysqlType.equalsIgnoreCase("INTEGER")) { //$NON-NLS-1$ //$NON-NLS-2$
			return mysqlType2JavaType(FIELD_TYPE_LONG);
		} else if (mysqlType.equalsIgnoreCase("BIGINT")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_LONGLONG);
		} else if (mysqlType.equalsIgnoreCase("INT24")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_INT24);
		} else if (mysqlType.equalsIgnoreCase("REAL")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_DOUBLE);
		} else if (mysqlType.equalsIgnoreCase("FLOAT")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_FLOAT);
		} else if (mysqlType.equalsIgnoreCase("DECIMAL")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_DECIMAL);
		} else if (mysqlType.equalsIgnoreCase("NUMERIC")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_DECIMAL);
		} else if (mysqlType.equalsIgnoreCase("DOUBLE")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_DOUBLE);
		} else if (mysqlType.equalsIgnoreCase("CHAR")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_STRING);
		} else if (mysqlType.equalsIgnoreCase("VARCHAR")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_VAR_STRING);
		} else if (mysqlType.equalsIgnoreCase("DATE")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_DATE);
		} else if (mysqlType.equalsIgnoreCase("TIME")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_TIME);
		} else if (mysqlType.equalsIgnoreCase("YEAR")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_YEAR);
		} else if (mysqlType.equalsIgnoreCase("TIMESTAMP")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_TIMESTAMP);
		} else if (mysqlType.equalsIgnoreCase("DATETIME")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_DATETIME);
		} else if (mysqlType.equalsIgnoreCase("TINYBLOB")) { //$NON-NLS-1$
			return java.sql.Types.BINARY;
		} else if (mysqlType.equalsIgnoreCase("BLOB")) { //$NON-NLS-1$
			return java.sql.Types.LONGVARBINARY;
		} else if (mysqlType.equalsIgnoreCase("MEDIUMBLOB")) { //$NON-NLS-1$
			return java.sql.Types.LONGVARBINARY;
		} else if (mysqlType.equalsIgnoreCase("LONGBLOB")) { //$NON-NLS-1$
			return java.sql.Types.LONGVARBINARY;
		} else if (mysqlType.equalsIgnoreCase("TINYTEXT")) { //$NON-NLS-1$
			return java.sql.Types.VARCHAR;
		} else if (mysqlType.equalsIgnoreCase("TEXT")) { //$NON-NLS-1$
			return java.sql.Types.LONGVARCHAR;
		} else if (mysqlType.equalsIgnoreCase("MEDIUMTEXT")) { //$NON-NLS-1$
			return java.sql.Types.LONGVARCHAR;
		} else if (mysqlType.equalsIgnoreCase("LONGTEXT")) { //$NON-NLS-1$
			return java.sql.Types.LONGVARCHAR;
		} else if (mysqlType.equalsIgnoreCase("ENUM")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_ENUM);
		} else if (mysqlType.equalsIgnoreCase("SET")) { //$NON-NLS-1$
			return mysqlType2JavaType(FIELD_TYPE_SET);
		} else if (mysqlType.equalsIgnoreCase("GEOMETRY")) {
			return mysqlType2JavaType(FIELD_TYPE_GEOMETRY);
		} else if (mysqlType.equalsIgnoreCase("BINARY")) {
			return Types.BINARY; // no concrete type on the wire
		} else if (mysqlType.equalsIgnoreCase("VARBINARY")) {
			return Types.VARBINARY; // no concrete type on the wire
		} else if (mysqlType.equalsIgnoreCase("BIT")) {
			return mysqlType2JavaType(FIELD_TYPE_BIT);
		}

		// Punt
		return java.sql.Types.OTHER;
	}
	
	

	/**
	 * @param mysqlType
	 * @return
	 */
	
	public static String typeToName(int mysqlType) {
		switch (mysqlType) {
		case MysqlTypeDefs.FIELD_TYPE_DECIMAL:
			return "FIELD_TYPE_DECIMAL";

		case MysqlTypeDefs.FIELD_TYPE_TINY:
			return "FIELD_TYPE_TINY";

		case MysqlTypeDefs.FIELD_TYPE_SHORT:
			return "FIELD_TYPE_SHORT";

		case MysqlTypeDefs.FIELD_TYPE_LONG:
			return "FIELD_TYPE_LONG";

		case MysqlTypeDefs.FIELD_TYPE_FLOAT:
			return "FIELD_TYPE_FLOAT";

		case MysqlTypeDefs.FIELD_TYPE_DOUBLE:
			return "FIELD_TYPE_DOUBLE";

		case MysqlTypeDefs.FIELD_TYPE_NULL:
			return "FIELD_TYPE_NULL";

		case MysqlTypeDefs.FIELD_TYPE_TIMESTAMP:
			return "FIELD_TYPE_TIMESTAMP";

		case MysqlTypeDefs.FIELD_TYPE_LONGLONG:
			return "FIELD_TYPE_LONGLONG";

		case MysqlTypeDefs.FIELD_TYPE_INT24:
			return "FIELD_TYPE_INT24";

		case MysqlTypeDefs.FIELD_TYPE_DATE:
			return "FIELD_TYPE_DATE";

		case MysqlTypeDefs.FIELD_TYPE_TIME:
			return "FIELD_TYPE_TIME";

		case MysqlTypeDefs.FIELD_TYPE_DATETIME:
			return "FIELD_TYPE_DATETIME";

		case MysqlTypeDefs.FIELD_TYPE_YEAR:
			return "FIELD_TYPE_YEAR";

		case MysqlTypeDefs.FIELD_TYPE_NEWDATE:
			return "FIELD_TYPE_NEWDATE";

		case MysqlTypeDefs.FIELD_TYPE_ENUM:
			return "FIELD_TYPE_ENUM";

		case MysqlTypeDefs.FIELD_TYPE_SET:
			return "FIELD_TYPE_SET";

		case MysqlTypeDefs.FIELD_TYPE_TINY_BLOB:
			return "FIELD_TYPE_TINY_BLOB";

		case MysqlTypeDefs.FIELD_TYPE_MEDIUM_BLOB:
			return "FIELD_TYPE_MEDIUM_BLOB";

		case MysqlTypeDefs.FIELD_TYPE_LONG_BLOB:
			return "FIELD_TYPE_LONG_BLOB";

		case MysqlTypeDefs.FIELD_TYPE_BLOB:
			return "FIELD_TYPE_BLOB";

		case MysqlTypeDefs.FIELD_TYPE_VAR_STRING:
			return "FIELD_TYPE_VAR_STRING";

		case MysqlTypeDefs.FIELD_TYPE_STRING:
			return "FIELD_TYPE_STRING";

		case MysqlTypeDefs.FIELD_TYPE_VARCHAR:
			return "FIELD_TYPE_VARCHAR";

		case MysqlTypeDefs.FIELD_TYPE_GEOMETRY:
			return "FIELD_TYPE_GEOMETRY";

		default:
			return " Unknown MySQL Type # " + mysqlType;
		}
	}

	private static Map<String, Integer> mysqlToJdbcTypesMap = new HashMap<String, Integer>();

	static {
		mysqlToJdbcTypesMap.put("BIT", new Integer(
				mysqlType2JavaType(FIELD_TYPE_BIT)));

		mysqlToJdbcTypesMap.put("TINYINT", new Integer(
				mysqlType2JavaType(FIELD_TYPE_TINY)));
		mysqlToJdbcTypesMap.put("SMALLINT", new Integer(
				mysqlType2JavaType(FIELD_TYPE_SHORT)));
		mysqlToJdbcTypesMap.put("MEDIUMINT", new Integer(
				mysqlType2JavaType(FIELD_TYPE_INT24)));
		mysqlToJdbcTypesMap.put("INT", new Integer(
				mysqlType2JavaType(FIELD_TYPE_LONG)));
		mysqlToJdbcTypesMap.put("INTEGER", new Integer(
				mysqlType2JavaType(FIELD_TYPE_LONG)));
		mysqlToJdbcTypesMap.put("BIGINT", new Integer(
				mysqlType2JavaType(FIELD_TYPE_LONGLONG)));
		mysqlToJdbcTypesMap.put("INT24", new Integer(
				mysqlType2JavaType(FIELD_TYPE_INT24)));
		mysqlToJdbcTypesMap.put("REAL", new Integer(
				mysqlType2JavaType(FIELD_TYPE_DOUBLE)));
		mysqlToJdbcTypesMap.put("FLOAT", new Integer(
				mysqlType2JavaType(FIELD_TYPE_FLOAT)));
		mysqlToJdbcTypesMap.put("DECIMAL", new Integer(
				mysqlType2JavaType(FIELD_TYPE_DECIMAL)));
		mysqlToJdbcTypesMap.put("NUMERIC", new Integer(
				mysqlType2JavaType(FIELD_TYPE_DECIMAL)));
		mysqlToJdbcTypesMap.put("DOUBLE", new Integer(
				mysqlType2JavaType(FIELD_TYPE_DOUBLE)));
		mysqlToJdbcTypesMap.put("CHAR", new Integer(
				mysqlType2JavaType(FIELD_TYPE_STRING)));
		mysqlToJdbcTypesMap.put("VARCHAR", new Integer(
				mysqlType2JavaType(FIELD_TYPE_VAR_STRING)));
		mysqlToJdbcTypesMap.put("DATE", new Integer(
				mysqlType2JavaType(FIELD_TYPE_DATE)));
		mysqlToJdbcTypesMap.put("TIME", new Integer(
				mysqlType2JavaType(FIELD_TYPE_TIME)));
		mysqlToJdbcTypesMap.put("YEAR", new Integer(
				mysqlType2JavaType(FIELD_TYPE_YEAR)));
		mysqlToJdbcTypesMap.put("TIMESTAMP", new Integer(
				mysqlType2JavaType(FIELD_TYPE_TIMESTAMP)));
		mysqlToJdbcTypesMap.put("DATETIME", new Integer(
				mysqlType2JavaType(FIELD_TYPE_DATETIME)));
		mysqlToJdbcTypesMap.put("TINYBLOB", new Integer(java.sql.Types.BINARY));
		mysqlToJdbcTypesMap.put("BLOB", new Integer(
				java.sql.Types.LONGVARBINARY));
		mysqlToJdbcTypesMap.put("MEDIUMBLOB", new Integer(
				java.sql.Types.LONGVARBINARY));
		mysqlToJdbcTypesMap.put("LONGBLOB", new Integer(
				java.sql.Types.LONGVARBINARY));
		mysqlToJdbcTypesMap
				.put("TINYTEXT", new Integer(java.sql.Types.VARCHAR));
		mysqlToJdbcTypesMap
				.put("TEXT", new Integer(java.sql.Types.LONGVARCHAR));
		mysqlToJdbcTypesMap.put("MEDIUMTEXT", new Integer(
				java.sql.Types.LONGVARCHAR));
		mysqlToJdbcTypesMap.put("LONGTEXT", new Integer(
				java.sql.Types.LONGVARCHAR));
		mysqlToJdbcTypesMap.put("ENUM", new Integer(
				mysqlType2JavaType(FIELD_TYPE_ENUM)));
		mysqlToJdbcTypesMap.put("SET", new Integer(
				mysqlType2JavaType(FIELD_TYPE_SET)));
		mysqlToJdbcTypesMap.put("GEOMETRY", new Integer(
				mysqlType2JavaType(FIELD_TYPE_GEOMETRY)));
	}
	
}
