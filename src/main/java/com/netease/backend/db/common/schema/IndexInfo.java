package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class IndexInfo implements Serializable, Cloneable {
	
	private static final long serialVersionUID = -7064816289665230396L;
	
	
	public static final int INDEX_TYPE_BTREE = 1;
	
    public static final int INDEX_TYPE_HASH = 2;
    
    public static final int INDEX_TYPE_CLUSTER_BTREE = 3;
    
    public static final int INDEX_TYPE_CLUSTER_HASH = 4;
    
    public static final int INDEX_TYPE_BTREE_COMPRESS = 5;
    
    public static final int INDEX_TYPE_BITMAP = 6;
	
	
	private static boolean statEnable = false;

	
	private String indexName = "";
	
	
	private String tableName = "";
	
	
	private ArrayList<IndexColumn> columnList = null;
	
	
	private boolean isPrimaryKey = false;
	
	
	private boolean isUnique = false;
	
	
	private int usedCount = 0;
	
	
	private String dbnClusterName = null;
	
	
	private boolean isClusterIndex = false;
	
	
	private boolean isReverse = false;
	
	
    private int indexType = INDEX_TYPE_BTREE;
    
    
    private int compressNum = 0;
    
    
    private String tableSpace = null;
    
    
    private boolean isOracleUniqueConstraint = false;
    
	
	public IndexInfo(String name, String tableName,	boolean isPrimary, 
			boolean isUnique, ArrayList<IndexColumn> columns) throws IllegalArgumentException
	{
		if(tableName == null || tableName.trim().equals(""))
			throw new IllegalArgumentException("��������Ϊnull����ַ���");
		if(columns == null || columns.size() == 0)
			throw new IllegalArgumentException("���������õ��ֶ��б���Ϊ��");
		this.tableName = tableName;
		if(isPrimary)
			this.indexName = "PRIMARY";
		else if(name == null || name.trim().equals(""))
			throw new IllegalArgumentException("�������Ʋ���Ϊ��");
		else
			this.indexName = name;
		this.columnList = columns;
		this.isPrimaryKey = isPrimary;
		this.isUnique = isUnique;
	}
	
	
	public IndexInfo(String name, String tableName, ArrayList<String> columns, 
			boolean isPrimary, boolean isUnique) throws IllegalArgumentException
	{
		if(tableName == null || tableName.trim().equals(""))
			throw new IllegalArgumentException("��������Ϊnull����ַ���");
		if(columns == null || columns.size() == 0)
			throw new IllegalArgumentException("���������õ��ֶ��б���Ϊ��");
		this.tableName = tableName;
		if(isPrimary)
			this.indexName = "PRIMARY";
		else if(name == null || name.trim().equals(""))
			throw new IllegalArgumentException("�������Ʋ���Ϊ��");
		else
			this.indexName = name;
		this.columnList = new ArrayList<IndexColumn> ();
		int i=1;
		for(String columnName : columns)
		{
			this.columnList.add(new IndexColumn(tableName, indexName, columnName, i, 0, 0, false, false));
			i++;
		}
		this.isPrimaryKey = isPrimary;
		this.isUnique = isUnique;
	}
	
	
	public IndexInfo(DbnCluster dbnCluster, String tableName, ArrayList<String> columns) 
			throws IllegalArgumentException {
		this(dbnCluster.getClusterIndexName(), tableName, columns, false, false);
		this.dbnClusterName = dbnCluster.getName();
		this.tableSpace = dbnCluster.getTableSpace();
		if (DbnCluster.CLUSTER_TYPE_HASH == dbnCluster.getType()) {
			this.indexType = INDEX_TYPE_CLUSTER_HASH;
		} else if (DbnCluster.CLUSTER_TYPE_INDEX == dbnCluster.getType()) {
			this.indexType = INDEX_TYPE_CLUSTER_BTREE;
		}
	}
	
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		IndexInfo other = (IndexInfo)otherObject;
		
		return this.tableName.equalsIgnoreCase(other.getIndexName()) 
		&& this.indexName.equalsIgnoreCase(other.getIndexName());
	}
	
	
	public String toString()
	{
		return tableName+"."+indexName;
	}
	
	
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		try
		{
			IndexInfo cloned = (IndexInfo)super.clone();
			cloned.columnList = (ArrayList<IndexColumn>)columnList.clone();
			return cloned;
		}catch(CloneNotSupportedException e) { return null; }
	}


	public ArrayList<IndexColumn> getColumnList() {
		return columnList;
	}

	public String getIndexName() {
		return indexName;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public String getTableName() {
		return tableName;
	}
    
    public void setTableName(String name) {
        this.tableName = name;
    }

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public static boolean isStatEnable() {
		return statEnable;
	}

	public static void setStatEnable(boolean statEnable) {
		IndexInfo.statEnable = statEnable;
	}

	synchronized public int getUsedCount() {
		return usedCount;
	}
	
	synchronized public void increateUsedCount()
	{
		this.usedCount++;
	}
	
	
	
	public boolean hasUnusedField(List<String> fields)
	{
		if(fields == null)
			return false;
		for(String fieldName : fields)
		{
			boolean hasField = false;
			for(IndexColumn column : columnList)
				if(column.getColumnName().equalsIgnoreCase(fieldName))
				{
					hasField = true;
					break;
				}
			if(!hasField)
				return true;
		}
		return false;
	}

	
	
	public int getColumnSeq(String columnName)
	{
		int seq = 1;
		for(IndexColumn column : columnList)
		{
			if(column.getColumnName().equalsIgnoreCase(columnName))
				return seq;
			seq++;
		}
		return -1;
	}
	
	
	public void resetCardinality()
	{
		for(IndexColumn column : columnList)
			column.setCardinality(0);
	}
	
	
	public void setColumnCardinality(String columnName, long cardinality)
	{
		for(IndexColumn column : columnList)
			if(column.getColumnName().equalsIgnoreCase(columnName))
			{
				column.setCardinality(cardinality);
				return;
			}
	}
	
	
	
	public List<String> getColumnNameList()
	{
		List<String> columnNameList = new ArrayList<String>();
		for(IndexColumn column : columnList)
			columnNameList.add(column.getColumnName());
		return columnNameList;
	}
	
	
	public long getColumnCardinality(String columnName)
	{
		for(IndexColumn column : columnList)
			if(column.getColumnName().equalsIgnoreCase(columnName))
				return column.getCardinality();
		return -1;
	}
	
	
	public long getCardinality()
	{
		return columnList.get(columnList.size()-1).getCardinality();
	}
	
	
	public IndexColumn getIndexColumn(int seq)
	{
		if(seq <=0 || seq > columnList.size())
			return null;
		return columnList.get(seq - 1);
	}
	
	
	public IndexColumn getIndexColumn(String columnName)
	{
		for(IndexColumn column : columnList)
			if(column.getColumnName().equalsIgnoreCase(columnName))
				return column;
		return null;
	}
	
	
	public void addIndexColumn(IndexColumn column)
	{
		if(!columnList.contains(column))
			columnList.add(column);
	}

	public String getDbnClusterName() {
		return dbnClusterName;
	}

	public void setDbnCluster(String clusterName) {
		this.dbnClusterName = clusterName;
		if (null != clusterName) {
			this.isClusterIndex = true;
		}
	}
	
	public boolean isClusterIndex() {
		if (null == this.dbnClusterName)
			return false;
		return this.isClusterIndex;
	}

	public int getIndexType() {
		return indexType;
	}

	public void setIndexType(int indexType) {
		if (indexType < 1 || indexType > 6)
			throw new IllegalArgumentException("Incorrect index type!");
		this.indexType = indexType;
	}

	public int getCompressNum() {
		return compressNum;
	}

	public void setCompressNum(int compressNum) {
		if (compressNum < 0)
			throw new IllegalArgumentException("The compress number of index can't be negative");
		this.compressNum = compressNum;
	}

	public String getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}

	public boolean isReverse() {
		return isReverse;
	}

	public void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}

	public boolean isOracleUniqueConstraint() {
		return isOracleUniqueConstraint;
	}

	public void setOracleUniqueConstraint(boolean isConstraint) {
		this.isOracleUniqueConstraint = isConstraint;
	}
	
	
	public static String getIndexTypeStr(IndexInfo index) {
		switch(index.getIndexType()) {
		case 1:
			return "BTREE";
		case 2:
			return "HASH";
		case 3:
			return "CLUSTER BTREE";
		case 4:
			return "CLUSTER HASH";
		case 5:
			return "BTREE COMPRESS";
		case 6:
			return "BITMAP";
		default :
			return "BTREE";
		}
	}
	
	
	public static void checkIndexName(String indexName)
			throws IllegalArgumentException {
		if (null == indexName || "".equals(indexName.trim())) {
			throw new IllegalArgumentException("������Ϊ��");
		}
		indexName = indexName.trim();
		if (indexName.length() > 64) {
			throw new IllegalArgumentException("������'" + indexName
					+ "'���������ܳ���64���ַ�");
		}
	}
}
