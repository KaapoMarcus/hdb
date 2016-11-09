package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.sql.Types;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.schema.dbengine.ColumnInfoMySQL;
import com.netease.backend.db.common.schema.dbengine.ColumnInfoOracle;


public class ColumnInfo implements Serializable {
	private static final long serialVersionUID = -8330645721350750993L;

	public static final String BUCKETNO_COLUMN_NAME = "bucketno";
	
	
	private String name;
	
	
	private String typeStr;
	
	
	private int type;
	
	
	private long length = 0;
	
	
	private long size = 0;
	
	
	private String comment = null;
	
	
	private boolean unique = false;
	
	
	private DbnType dbnType;
	
	
	private boolean isVirtual = false;
	
	
    private String expression = null;
    
    
    private boolean isAutoIncrement = false;
    
    
    private int fieldNumber = 0;
    
    
    private String defaultValueStr = null;
    
    
	private int referCount = 0;

    
    private static boolean statEnable = false;
	
	
	public ColumnInfo(String name, String typeName, boolean isUnique,
			DbnType dbnType) throws IllegalArgumentException {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("����������Ϊnull����ַ���");
		if (typeName == null || typeName.length() == 0)
			throw new IllegalArgumentException("�������Ͳ���Ϊnull����ַ���");

		this.name = name;
		this.typeStr = typeName.toUpperCase();
		this.dbnType = dbnType;
		type = parseType(this.typeStr, dbnType);
		this.size = sizeOf(typeStr, length, dbnType);
		this.unique = isUnique;
	}
	
	
	public ColumnInfo(String name, String typeName, long num, boolean isUnique,
			DbnType dbnType) throws IllegalArgumentException {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("����������Ϊnull����ַ���");
		if (typeName == null || typeName.length() == 0)
			throw new IllegalArgumentException("�������Ͳ���Ϊnull����ַ���");

		this.name = name;
		this.typeStr = typeName.toUpperCase();
		this.dbnType = dbnType;
		type = parseType(this.typeStr, dbnType);
		this.length = num;
		this.size = sizeOf(typeStr, length, dbnType);
		this.unique = isUnique;
	}
	
	
	public ColumnInfo(String name, int jdbcType, boolean isUnique,
			DbnType dbnType) throws IllegalArgumentException {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("����������Ϊnull����ַ���");

		this.name = name;
		this.type = jdbcType;
		this.dbnType = dbnType;
		typeStr = getTypeName(jdbcType, dbnType);
		this.size = sizeOf(typeStr, length, dbnType);
		this.unique = isUnique;
	}
	
	
	public ColumnInfo(String name, int jdbcType, long num, boolean isUnique,
			DbnType dbnType) throws IllegalArgumentException {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("����������Ϊnull����ַ���");

		this.name = name;
		this.type = jdbcType;
		this.dbnType = dbnType;
		typeStr = getTypeName(jdbcType, dbnType);
		this.length = num;
		this.size = sizeOf(typeStr, length, dbnType);
		this.unique = isUnique;
	}
	
	
	public void setComment(String comment) {
	    	   this.comment = comment;
	}
	
	public String getComment() {
	    return this.comment;
	}
	
	
	public static long sizeOf(String typeName, long num, DbnType type)
			throws IllegalArgumentException {
		if (type == DbnType.MySQL)
			return ColumnInfoMySQL.sizeOf(typeName, num);
		else if (type == DbnType.Oracle)
			return ColumnInfoOracle.sizeOf(typeName, num);
		else
			throw new IllegalArgumentException("dbn type not recognized:"
					+ type);
	}
	
	
	public static int getDefaultDisplaySize(int jdbcType, DbnType type) {
		if (type == DbnType.MySQL)
			return ColumnInfoMySQL.getDefaultDisplaySize(jdbcType);
		else if (type == DbnType.Oracle)
			return ColumnInfoOracle.getDefaultDisplaySize(jdbcType);
		else
			throw new IllegalArgumentException("dbn type not recognized:"
					+ type);
	}
	
	
	public int getDisplaySize() {
		if (length != 0) {
			if (length >= Integer.MAX_VALUE)
				return Integer.MAX_VALUE;
			else
				return (int)length;
		} else {
			if (dbnType == DbnType.MySQL)
				return ColumnInfoMySQL.getDefaultDisplaySize(typeStr);
			else
				return ColumnInfoOracle.getDefaultDisplaySize(typeStr);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void rename(String name) {
		if (null == name || "".equals(name))
			throw new IllegalArgumentException("column name can't not be null");
		this.name = name;
	}
	
	public String getTypeName() {
		return typeStr;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public ColumnInfo copy() {
		try {
			return new ColumnInfo(name, typeStr, unique, dbnType);
		} catch (IllegalArgumentException e) {
			assert false;
			return null;
		}
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ColumnInfo other = (ColumnInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

	
	public static String getTypeName(int jdbcType, DbnType type) throws IllegalArgumentException {
		if(type == DbnType.MySQL)
			return ColumnInfoMySQL.getTypeName(jdbcType);
		else if(type == DbnType.Oracle)
			return ColumnInfoOracle.getTypeName(jdbcType);
		else
			throw new IllegalArgumentException("dbn type not recognized:" + type);
	}
	
	
	public static boolean isFixSizeType(int jdbcType) {
		return jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT
				|| jdbcType == Types.TINYINT || jdbcType == Types.BIGINT
				|| jdbcType == Types.CHAR 
				|| jdbcType == Types.DATE
				|| jdbcType == Types.BOOLEAN
				|| jdbcType == Types.DOUBLE
				|| jdbcType == Types.REAL
				|| jdbcType == Types.TIME
				|| jdbcType == Types.TIMESTAMP;
	}
	
	
	public static boolean isIntegerBFType(int jdbcType) {
		return jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT
				|| jdbcType == Types.TINYINT || jdbcType == Types.BIGINT;
	}
	
	
	public static boolean isStringBFType(int jdbcType) {
		return jdbcType == Types.CHAR || jdbcType == Types.VARCHAR
			|| jdbcType == Types.LONGVARCHAR;
	}
	
	
	public static int parseType(String type, DbnType dbnType) throws IllegalArgumentException {
		if(dbnType == DbnType.MySQL)
			return ColumnInfoMySQL.parseType(type);
		else if(dbnType == DbnType.Oracle)
			return ColumnInfoOracle.parseType(type);
		else
			throw new IllegalArgumentException("dbn type not recognized:" + type);
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public int getReferCount() {
		return referCount;
	}
	
	public void resetReferCount()
	{
		this.referCount = 0;
	}
	
	synchronized public void increaseReferCount()
	{
		this.referCount++;
	}

	public static boolean isStatEnable() {
		return statEnable;
	}

	public static void setStatEnable(boolean statEnable) {
		ColumnInfo.statEnable = statEnable;
	}

	public long getSize() {
		return size;
	}

	public long getLength() {
		return length;
	}
	
	public void setLength(long length) {
		this.length = length;
		size = sizeOf(typeStr, this.length, dbnType);
	}

	public boolean isVirtual() {
		return isVirtual;
	}

	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public boolean isBucketNo(){
		return name.equalsIgnoreCase(BUCKETNO_COLUMN_NAME);
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public int getFieldNumber() {
		return fieldNumber;
	}

	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	public String getDefaultValueStr() {
		return defaultValueStr;
	}

	public void setDefaultValueStr(String defaultValueStr) {
		this.defaultValueStr = defaultValueStr;
	}
	
	
	public static void checkColumnName(String columnName)
			throws IllegalArgumentException {
		if (null == columnName || "".equals(columnName.trim())) {
			throw new IllegalArgumentException("�ֶ���Ϊ��");
		}
		columnName = columnName.trim();
		if (columnName.length() > 64) {
			throw new IllegalArgumentException("�ֶ���'" + columnName
					+ "'���������ܳ���64���ַ�");
		}
	}
}
