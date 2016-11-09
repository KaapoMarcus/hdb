package com.netease.backend.db.common.schema.dbengine;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


public class ColumnInfoMySQL {
	private static Map<ColumnTypeMySQL, Integer> nameToTypeMap;
	private static Map<Integer, String> typeToNameMap;
	private static Map<ColumnTypeMySQL, Integer> nameToLength;
	private static Map<ColumnTypeMySQL, Integer> nameToDisplaySize;

	static {
		nameToTypeMap = new HashMap<ColumnTypeMySQL, Integer>();
		nameToTypeMap.put(ColumnTypeMySQL.INT, Types.INTEGER);
		nameToTypeMap.put(ColumnTypeMySQL.INTEGER, Types.INTEGER);
		nameToTypeMap.put(ColumnTypeMySQL.MEDIUMINT, Types.INTEGER);
		nameToTypeMap.put(ColumnTypeMySQL.YEAR, Types.DATE);
		nameToTypeMap.put(ColumnTypeMySQL.BIT, Types.BIT);
		nameToTypeMap.put(ColumnTypeMySQL.TINYINT, Types.TINYINT);
		nameToTypeMap.put(ColumnTypeMySQL.BOOL, Types.BOOLEAN);
		nameToTypeMap.put(ColumnTypeMySQL.BOOLEAN, Types.BOOLEAN);
		nameToTypeMap.put(ColumnTypeMySQL.SMALLINT, Types.SMALLINT);
		nameToTypeMap.put(ColumnTypeMySQL.BIGINT, Types.BIGINT);
		nameToTypeMap.put(ColumnTypeMySQL.FLOAT, Types.FLOAT);
		nameToTypeMap.put(ColumnTypeMySQL.DOUBLE, Types.DOUBLE);
		nameToTypeMap.put(ColumnTypeMySQL.DECIMAL, Types.DECIMAL);
		nameToTypeMap.put(ColumnTypeMySQL.DEC, Types.DECIMAL);
		nameToTypeMap.put(ColumnTypeMySQL.DATE, Types.DATE);
		nameToTypeMap.put(ColumnTypeMySQL.DATETIME, Types.TIMESTAMP);
		nameToTypeMap.put(ColumnTypeMySQL.TIMESTAMP, Types.TIMESTAMP);
		nameToTypeMap.put(ColumnTypeMySQL.TIME, Types.TIME);
		nameToTypeMap.put(ColumnTypeMySQL.CHAR, Types.CHAR);
		nameToTypeMap.put(ColumnTypeMySQL.VARCHAR, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeMySQL.LONGVARCHAR, Types.LONGVARCHAR);
		nameToTypeMap.put(ColumnTypeMySQL.TEXT, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeMySQL.TINYTEXT, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeMySQL.MEDIUMTEXT, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeMySQL.LONGTEXT, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeMySQL.ENUM, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeMySQL.BINARY, Types.BINARY);
		nameToTypeMap.put(ColumnTypeMySQL.VARBINARY, Types.VARBINARY);
		nameToTypeMap.put(ColumnTypeMySQL.LONGVARBINARY, Types.LONGVARBINARY);
		nameToTypeMap.put(ColumnTypeMySQL.BLOB, Types.BLOB);
		nameToTypeMap.put(ColumnTypeMySQL.TINYBLOB, Types.BLOB);
		nameToTypeMap.put(ColumnTypeMySQL.MEDIUMBLOB, Types.BLOB);
		nameToTypeMap.put(ColumnTypeMySQL.LONGBLOB, Types.BLOB);
		nameToTypeMap.put(ColumnTypeMySQL.NUMERIC, Types.NUMERIC);
		nameToTypeMap.put(ColumnTypeMySQL.REAL, Types.REAL);
		nameToTypeMap.put(ColumnTypeMySQL.FIXED, Types.DECIMAL);
		nameToTypeMap.put(ColumnTypeMySQL.SET, Types.VARCHAR);

		typeToNameMap = new HashMap<Integer, String>();
		typeToNameMap.put(Types.ARRAY, "ARRAY");
		typeToNameMap.put(Types.BIGINT, ColumnTypeMySQL.BIGINT.name());
		typeToNameMap.put(Types.BINARY, ColumnTypeMySQL.BINARY.name());
		typeToNameMap.put(Types.BIT, ColumnTypeMySQL.BIT.name());
		typeToNameMap.put(Types.BLOB, ColumnTypeMySQL.BLOB.name());
		typeToNameMap.put(Types.BOOLEAN, ColumnTypeMySQL.BOOLEAN.name());
		typeToNameMap.put(Types.CHAR, ColumnTypeMySQL.CHAR.name());
		typeToNameMap.put(Types.DATE, ColumnTypeMySQL.DATE.name());
		typeToNameMap.put(Types.DECIMAL, ColumnTypeMySQL.DECIMAL.name());
		typeToNameMap.put(Types.DOUBLE, ColumnTypeMySQL.DOUBLE.name());
		typeToNameMap.put(Types.FLOAT, ColumnTypeMySQL.FLOAT.name());
		typeToNameMap.put(Types.INTEGER, ColumnTypeMySQL.INTEGER.name());
		typeToNameMap.put(Types.NUMERIC, ColumnTypeMySQL.NUMERIC.name());
		typeToNameMap.put(Types.REAL, ColumnTypeMySQL.REAL.name());
		typeToNameMap.put(Types.SMALLINT, ColumnTypeMySQL.SMALLINT.name());
		typeToNameMap.put(Types.TIME, ColumnTypeMySQL.TIME.name());
		typeToNameMap.put(Types.TIMESTAMP, ColumnTypeMySQL.TIMESTAMP.name());
		typeToNameMap.put(Types.TINYINT, ColumnTypeMySQL.TINYINT.name());
		typeToNameMap.put(Types.VARBINARY, ColumnTypeMySQL.VARBINARY.name());
		typeToNameMap.put(Types.LONGVARBINARY, ColumnTypeMySQL.LONGVARBINARY.name());
		typeToNameMap.put(Types.VARCHAR, ColumnTypeMySQL.VARCHAR.name());
		typeToNameMap.put(Types.LONGVARCHAR, ColumnTypeMySQL.LONGVARCHAR.name());

		nameToLength = new HashMap<ColumnTypeMySQL, Integer>();
		nameToLength.put(ColumnTypeMySQL.INT, Integer.valueOf(4));
		nameToLength.put(ColumnTypeMySQL.INTEGER, Integer.valueOf(4));
		nameToLength.put(ColumnTypeMySQL.MEDIUMINT, Integer.valueOf(3));
		nameToLength.put(ColumnTypeMySQL.YEAR, Integer.valueOf(1));
		nameToLength.put(ColumnTypeMySQL.BIT, Integer.valueOf(1));
		nameToLength.put(ColumnTypeMySQL.TINYINT, Integer.valueOf(1));
		nameToLength.put(ColumnTypeMySQL.BOOL, Integer.valueOf(1));
		nameToLength.put(ColumnTypeMySQL.BOOLEAN, Integer.valueOf(1));
		nameToLength.put(ColumnTypeMySQL.SMALLINT, Integer.valueOf(2));
		nameToLength.put(ColumnTypeMySQL.BIGINT, Integer.valueOf(8));
		nameToLength.put(ColumnTypeMySQL.FLOAT, Integer.valueOf(8));
		nameToLength.put(ColumnTypeMySQL.DOUBLE, Integer.valueOf(8));
		nameToLength.put(ColumnTypeMySQL.DECIMAL, Integer.valueOf(10));
		nameToLength.put(ColumnTypeMySQL.DEC, Integer.valueOf(10));
		nameToLength.put(ColumnTypeMySQL.DATE, Integer.valueOf(3));
		nameToLength.put(ColumnTypeMySQL.DATETIME, Integer.valueOf(8));
		nameToLength.put(ColumnTypeMySQL.TIMESTAMP, Integer.valueOf(4));
		nameToLength.put(ColumnTypeMySQL.TIME, Integer.valueOf(3));
		nameToLength.put(ColumnTypeMySQL.CHAR, Integer.valueOf(1));
		nameToLength.put(ColumnTypeMySQL.VARCHAR, Integer.valueOf(256));
		nameToLength.put(ColumnTypeMySQL.LONGVARCHAR, Integer.valueOf(65535));
		nameToLength.put(ColumnTypeMySQL.TEXT, Integer.valueOf(65535));
		nameToLength.put(ColumnTypeMySQL.TINYTEXT, Integer.valueOf(256));
		nameToLength.put(ColumnTypeMySQL.MEDIUMTEXT, Integer.valueOf(1677215));
		nameToLength.put(ColumnTypeMySQL.LONGTEXT, Integer
				.valueOf(Integer.MAX_VALUE));
		nameToLength.put(ColumnTypeMySQL.ENUM, Integer.valueOf(2));
		nameToLength.put(ColumnTypeMySQL.BINARY, Integer.valueOf(255));
		nameToLength.put(ColumnTypeMySQL.VARBINARY, Integer.valueOf(255));
		nameToLength.put(ColumnTypeMySQL.LONGVARBINARY, Integer.valueOf(65535));
		nameToLength.put(ColumnTypeMySQL.BLOB, Integer.valueOf(65535));
		nameToLength.put(ColumnTypeMySQL.TINYBLOB, Integer.valueOf(256));
		nameToLength.put(ColumnTypeMySQL.MEDIUMBLOB, Integer.valueOf(16777216));
		nameToLength.put(ColumnTypeMySQL.LONGBLOB, Integer
				.valueOf(Integer.MAX_VALUE));
		nameToLength.put(ColumnTypeMySQL.NUMERIC, Integer.valueOf(10));
		nameToLength.put(ColumnTypeMySQL.REAL, Integer.valueOf(8));
		nameToLength.put(ColumnTypeMySQL.FIXED, Integer.valueOf(10));
		nameToLength.put(ColumnTypeMySQL.SET, Integer.valueOf(8));
		
		nameToDisplaySize = new HashMap<ColumnTypeMySQL, Integer>();
		nameToDisplaySize.put(ColumnTypeMySQL.INT, Integer.valueOf(11));
		nameToDisplaySize.put(ColumnTypeMySQL.INTEGER, Integer.valueOf(11));
		nameToDisplaySize.put(ColumnTypeMySQL.MEDIUMINT, Integer.valueOf(9));
		nameToDisplaySize.put(ColumnTypeMySQL.YEAR, Integer.valueOf(4));
		nameToDisplaySize.put(ColumnTypeMySQL.BIT, Integer.valueOf(1));
		nameToDisplaySize.put(ColumnTypeMySQL.TINYINT, Integer.valueOf(4));
		nameToDisplaySize.put(ColumnTypeMySQL.BOOL, Integer.valueOf(1));
		nameToDisplaySize.put(ColumnTypeMySQL.BOOLEAN, Integer.valueOf(1));
		nameToDisplaySize.put(ColumnTypeMySQL.SMALLINT, Integer.valueOf(6));
		nameToDisplaySize.put(ColumnTypeMySQL.BIGINT, Integer.valueOf(20));
		nameToDisplaySize.put(ColumnTypeMySQL.FLOAT, Integer.valueOf(12));
		nameToDisplaySize.put(ColumnTypeMySQL.DOUBLE, Integer.valueOf(22));
		nameToDisplaySize.put(ColumnTypeMySQL.DECIMAL, Integer.valueOf(11));
		nameToDisplaySize.put(ColumnTypeMySQL.DEC, Integer.valueOf(11));
		nameToDisplaySize.put(ColumnTypeMySQL.DATE, Integer.valueOf(10));
		nameToDisplaySize.put(ColumnTypeMySQL.DATETIME, Integer.valueOf(19));
		nameToDisplaySize.put(ColumnTypeMySQL.TIMESTAMP, Integer.valueOf(19));
		nameToDisplaySize.put(ColumnTypeMySQL.TIME, Integer.valueOf(8));
		nameToDisplaySize.put(ColumnTypeMySQL.CHAR, Integer.valueOf(2));
		nameToDisplaySize.put(ColumnTypeMySQL.TEXT, Integer.valueOf(65535));
		nameToDisplaySize.put(ColumnTypeMySQL.TINYTEXT, Integer.valueOf(255));
		nameToDisplaySize.put(ColumnTypeMySQL.MEDIUMTEXT, Integer.valueOf(16777215));
		nameToDisplaySize.put(ColumnTypeMySQL.LONGTEXT, Integer
				.valueOf(Integer.MAX_VALUE));
		nameToDisplaySize.put(ColumnTypeMySQL.BINARY, Integer.valueOf(1));
		nameToDisplaySize.put(ColumnTypeMySQL.BLOB, Integer.valueOf(65535));
		nameToDisplaySize.put(ColumnTypeMySQL.TINYBLOB, Integer.valueOf(255));
		nameToDisplaySize.put(ColumnTypeMySQL.MEDIUMBLOB, Integer.valueOf(16777215));
		nameToDisplaySize.put(ColumnTypeMySQL.LONGBLOB, Integer
				.valueOf(Integer.MAX_VALUE));
		nameToDisplaySize.put(ColumnTypeMySQL.NUMERIC, Integer.valueOf(11));
		nameToDisplaySize.put(ColumnTypeMySQL.REAL, Integer.valueOf(22));
		nameToDisplaySize.put(ColumnTypeMySQL.FIXED, Integer.valueOf(11));
		
		
		
		nameToDisplaySize.put(ColumnTypeMySQL.VARCHAR, Integer.valueOf(65535));
		nameToDisplaySize.put(ColumnTypeMySQL.LONGVARCHAR, Integer.valueOf(65535));
		nameToDisplaySize.put(ColumnTypeMySQL.VARBINARY, Integer.valueOf(65535));
		nameToDisplaySize.put(ColumnTypeMySQL.LONGVARBINARY, Integer.valueOf(65535));
	}

	
	static public long sizeOf(String typeName, long num)
			throws IllegalArgumentException {
		typeName = typeName.toUpperCase();

		
		if (num <= 0)
			return nameToLength.get(ColumnTypeMySQL.valueOf(typeName)).intValue();

		
		switch (ColumnTypeMySQL.valueOf(typeName)) {
		case VARCHAR:
		case VARBINARY:
		case LONGVARCHAR:
			if (num <= 255)
				return num + 1;
			else
				return num + 2;
		case CHAR:
		case BINARY:
			if (num <= 255)
				return num;
			else
				return 255;
		case TINYBLOB:
		case TINYTEXT:
			if (num < 256)
				return num + 1;
			else
				return 256;
		case BLOB:
		case TEXT:
			if (num < 65536)
				return num + 2;
			else
				return 65535 + 2;
		case MEDIUMBLOB:
		case MEDIUMTEXT:
			if (num < 16777216)
				return num + 3;
			else
				return 16777215 + 3;
		case LONGBLOB:
		case LONGTEXT:
			if (num < 4294967296L)
				return num + 4;
			else
				return 4294967295L + 4;
		case FLOAT:
			if (num <= 24)
				return 4;
			else
				return 8;
		case NUMERIC:
		case DECIMAL:
			return (num / 9 + num % 9 == 0 ? 0 : 1) * 4 * 2;
		default:
			return nameToLength.get(ColumnTypeMySQL.valueOf(typeName)).intValue();
		}
	}

	
	public static String getTypeName(int jdbcType) throws IllegalArgumentException {
		String type = typeToNameMap.get(jdbcType);
		if (type == null)
			throw new IllegalArgumentException("����ȷ��ϵͳ��֧�ֵ�JDBC����: " + jdbcType);
		return type;
	}

	
	public static int parseType(String type) throws IllegalArgumentException {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("�������Ͳ���Ϊnull����ַ���");
		
		type = type.toUpperCase();
        
		Integer jdbcType = null;
        try {
            jdbcType = nameToTypeMap.get(ColumnTypeMySQL.valueOf(type));
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("����ȷ��ϵͳ��֧�ֵ�SQL����: " + type);
        }
					
		return jdbcType;
	}
	
	public static int getDefaultDisplaySize(String mysqlTypeStr) {
		mysqlTypeStr = mysqlTypeStr.toUpperCase();
		Integer size = nameToDisplaySize.get(ColumnTypeMySQL.valueOf(mysqlTypeStr));
		if (size == null)
			throw new IllegalArgumentException("��֧�ֵ�MySQL����:" + mysqlTypeStr);
		return size.intValue();
	}
	
	public static int getDefaultDisplaySize(int jdbcType) {
		
		if (jdbcType == Types.NULL)
			return 0;
		
		String mysqlType = getTypeName(jdbcType);
		try {
			return getDefaultDisplaySize(mysqlType);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("û�ж�Ӧֵ��JDBC����:" 
					+ jdbcType,	e);
		}
	}
}
