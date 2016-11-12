package com.jhh.hdb.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class JDBCTypesUtils {

	private static Map<String, Integer> name2value_map; 
	private static Map<Integer, String> value2name_map; 
	private static Map<Integer, Class<?>> value2javaclass_map; 
															
	static {
		name2value_map = new TreeMap<String, Integer>();
		value2name_map = new TreeMap<Integer, String>();
		value2javaclass_map = new TreeMap<Integer, Class<?>>();
		Field[] fields = java.sql.Types.class.getFields();
		for (int i = 0, len = fields.length; i < len; ++i) {
			if (Modifier.isStatic(fields[i].getModifiers())) {
				try {
					String name = fields[i].getName();
					Integer value = (Integer) fields[i]
							.get(java.sql.Types.class);
					name2value_map.put(name, value);
					value2name_map.put(value, name);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		
		value2javaclass_map.put(new Integer(Types.LONGNVARCHAR), String.class); 
																			
		value2javaclass_map.put(new Integer(Types.NCHAR), String.class); 
		value2javaclass_map.put(new Integer(Types.NVARCHAR), String.class); 
		value2javaclass_map.put(new Integer(Types.ROWID), String.class); 
		value2javaclass_map.put(new Integer(Types.BIT), Boolean.class); 
		value2javaclass_map.put(new Integer(Types.TINYINT), Byte.class); 
		value2javaclass_map.put(new Integer(Types.BIGINT), Long.class); 
		value2javaclass_map.put(new Integer(Types.LONGVARBINARY), Blob.class); 
																			
		value2javaclass_map.put(new Integer(Types.VARBINARY), Blob.class); 
		value2javaclass_map.put(new Integer(Types.BINARY), Blob.class); 
		value2javaclass_map.put(new Integer(Types.LONGVARCHAR), String.class); 
																			
		
		value2javaclass_map.put(new Integer(Types.CHAR), String.class); 
		value2javaclass_map.put(new Integer(Types.NUMERIC), BigDecimal.class); 
		value2javaclass_map.put(new Integer(Types.DECIMAL), BigDecimal.class); 
		value2javaclass_map.put(new Integer(Types.INTEGER), Integer.class); 
		value2javaclass_map.put(new Integer(Types.SMALLINT), Short.class); 
		value2javaclass_map.put(new Integer(Types.FLOAT), BigDecimal.class); 
		value2javaclass_map.put(new Integer(Types.REAL), BigDecimal.class); 
		value2javaclass_map.put(new Integer(Types.DOUBLE), BigDecimal.class); 
		value2javaclass_map.put(new Integer(Types.VARCHAR), String.class); 
		value2javaclass_map.put(new Integer(Types.BOOLEAN), Boolean.class); 
		// jdbcJavaTypes.put(new Integer(Types.DATALINK), String.class); 
		value2javaclass_map.put(new Integer(Types.DATE), Date.class); 
		value2javaclass_map.put(new Integer(Types.TIME), Date.class); 
		value2javaclass_map.put(new Integer(Types.TIMESTAMP), Date.class); 
		value2javaclass_map.put(new Integer(Types.OTHER), Object.class); 
		// jdbcJavaTypes.put(new Integer(Types.JAVA_OBJECT), Object.class); //
		
		// jdbcJavaTypes.put(new Integer(Types.DISTINCT), String.class); // 2001
		// jdbcJavaTypes.put(new Integer(Types.STRUCT), String.class); // 2002
		// jdbcJavaTypes.put(new Integer(Types.ARRAY), String.class); // 2003
		value2javaclass_map.put(new Integer(Types.BLOB), Blob.class); 
		value2javaclass_map.put(new Integer(Types.CLOB), Clob.class); 
		// jdbcJavaTypes.put(new Integer(Types.REF), String.class); // 2006
		// jdbcJavaTypes.put(new Integer(Types.SQLXML), String.class); // 2009
		value2javaclass_map.put(new Integer(Types.NCLOB), Clob.class); 
	}

	public static int getJdbcCode(String jdbcName) {
		return name2value_map.get(jdbcName);
	}

	public static String getJdbcName(int jdbcCode) {
		return value2name_map.get(jdbcCode);
	}

	public static Class<?> jdbcTypeToJavaType(int jdbcType) {
		return value2javaclass_map.get(jdbcType);
	}

	public static boolean isJavaNumberType(int jdbcType) {
		Class<?> type = value2javaclass_map.get(jdbcType);
		return (type == null) ? false
				: (Number.class.isAssignableFrom(type)) ? true : false;
	}

}