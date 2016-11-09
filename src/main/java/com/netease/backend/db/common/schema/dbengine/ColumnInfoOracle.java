package com.netease.backend.db.common.schema.dbengine;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


public class ColumnInfoOracle {
	private static Map<ColumnTypeOracle, Integer> nameToTypeMap;
	private static Map<Integer, String> typeToNameMap;
	private static Map<ColumnTypeOracle, Integer> nameToLength;
	static {
		nameToTypeMap = new HashMap<ColumnTypeOracle, Integer>();
		nameToTypeMap.put(ColumnTypeOracle.CHAR, Types.CHAR);
		nameToTypeMap.put(ColumnTypeOracle.NCHAR, Types.CHAR);
		nameToTypeMap.put(ColumnTypeOracle.VARCHAR2, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeOracle.NVARCHAR2, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeOracle.RAW, Types.VARCHAR);
		nameToTypeMap.put(ColumnTypeOracle.NUMERIC, Types.NUMERIC);
		nameToTypeMap.put(ColumnTypeOracle.DECIMAL, Types.DECIMAL);
		nameToTypeMap.put(ColumnTypeOracle.INTEGER, Types.INTEGER);
		nameToTypeMap.put(ColumnTypeOracle.SMALLINT, Types.SMALLINT);
		nameToTypeMap.put(ColumnTypeOracle.FLOAT, Types.FLOAT);
		nameToTypeMap.put(ColumnTypeOracle.DOUBLE, Types.DOUBLE);
		nameToTypeMap.put(ColumnTypeOracle.REAL, Types.REAL);
		nameToTypeMap.put(ColumnTypeOracle.NUMBER, Types.DOUBLE);
		nameToTypeMap.put(ColumnTypeOracle.DATE, Types.TIMESTAMP);
		nameToTypeMap.put(ColumnTypeOracle.TIMESTAMP, Types.TIMESTAMP);
		nameToTypeMap.put(ColumnTypeOracle.BLOB, Types.BLOB);
		nameToTypeMap.put(ColumnTypeOracle.CLOB, Types.CLOB);
		nameToTypeMap.put(ColumnTypeOracle.NCLOB, Types.CLOB);
		nameToTypeMap.put(ColumnTypeOracle.BINARY_FLOAT, Types.FLOAT);
		nameToTypeMap.put(ColumnTypeOracle.BINARY_DOUBLE, Types.DOUBLE);

		typeToNameMap = new HashMap<Integer, String>();
		typeToNameMap.put(Types.CHAR, ColumnTypeOracle.CHAR.name());
		typeToNameMap.put(Types.VARCHAR, ColumnTypeOracle.VARCHAR2.name());
		typeToNameMap.put(Types.NUMERIC, ColumnTypeOracle.NUMERIC.name());
		typeToNameMap.put(Types.DECIMAL, ColumnTypeOracle.DECIMAL.name());
		typeToNameMap.put(Types.INTEGER, ColumnTypeOracle.INTEGER.name());
		typeToNameMap.put(Types.SMALLINT, ColumnTypeOracle.SMALLINT.name());
		typeToNameMap.put(Types.FLOAT, ColumnTypeOracle.FLOAT.name());
		typeToNameMap.put(Types.DOUBLE, ColumnTypeOracle.DOUBLE.name());
		typeToNameMap.put(Types.REAL, ColumnTypeOracle.REAL.name());
		typeToNameMap.put(Types.DATE, ColumnTypeOracle.DATE.name());
		typeToNameMap.put(Types.TIMESTAMP, ColumnTypeOracle.TIMESTAMP.name());
		typeToNameMap.put(Types.BLOB, ColumnTypeOracle.BLOB.name());
		typeToNameMap.put(Types.CLOB, ColumnTypeOracle.CLOB.name());

		nameToLength = new HashMap<ColumnTypeOracle, Integer>();
		nameToLength.put(ColumnTypeOracle.CHAR, Integer.valueOf(1));
		nameToLength.put(ColumnTypeOracle.NCHAR, Integer.valueOf(1));
		nameToLength.put(ColumnTypeOracle.VARCHAR2, Integer.valueOf(256));
		nameToLength.put(ColumnTypeOracle.NVARCHAR2, Integer.valueOf(256));
		nameToLength.put(ColumnTypeOracle.RAW, Integer.valueOf(256));
		nameToLength.put(ColumnTypeOracle.NUMERIC, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.DECIMAL, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.INTEGER, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.SMALLINT, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.FLOAT, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.DOUBLE, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.REAL, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.NUMBER, Integer.valueOf(8));
		nameToLength.put(ColumnTypeOracle.DATE, Integer.valueOf(10));
		nameToLength.put(ColumnTypeOracle.TIMESTAMP, Integer.valueOf(31));
		nameToLength.put(ColumnTypeOracle.BLOB, Integer.valueOf(8192));
		nameToLength.put(ColumnTypeOracle.CLOB, Integer.valueOf(8192));
		nameToLength.put(ColumnTypeOracle.NCLOB, Integer.valueOf(8192));
		nameToLength.put(ColumnTypeOracle.BINARY_FLOAT, Integer.valueOf(15));
		nameToLength.put(ColumnTypeOracle.BINARY_DOUBLE, Integer.valueOf(15));

	}

	
	static public long sizeOf(String typeName, long num)
			throws IllegalArgumentException {
		typeName = typeName.toUpperCase();

		
		if (num <= 0)
			return nameToLength.get(ColumnTypeOracle.valueOf(typeName)).intValue();

		
		switch (ColumnTypeOracle.valueOf(typeName)) {
		case VARCHAR2:
		case CHAR:
		case RAW:
			if (num > 65535)
				return num + 3;
			else if (num > 255)
				return num + 2;
			else
				return num + 1;
		case NVARCHAR2:
		case NCHAR:
			if (num > 65535)
				return 2 * num + 3;
			else if (num > 255)
				return 2 * num + 2;
			else
				return 2 * num + 1;
		default:
			return nameToLength.get(ColumnTypeOracle.valueOf(typeName))
					.intValue();
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
            jdbcType = nameToTypeMap.get(ColumnTypeOracle.valueOf(type));
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("����ȷ��ϵͳ��֧�ֵ�SQL����: " + type);
        }
					
		return jdbcType;
	}

	public static int getDefaultDisplaySize(String oracleTypeStr) {
		
		oracleTypeStr = oracleTypeStr.toUpperCase();
		long size = sizeOf(oracleTypeStr, 0);
		if (size >= Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return (int)size;
	}
	
	public static int getDefaultDisplaySize(int jdbcType) {
		return getDefaultDisplaySize(getTypeName(jdbcType));
	}
}
