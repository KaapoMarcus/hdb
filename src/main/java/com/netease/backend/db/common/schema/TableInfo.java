package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.management.PIDConfig;
import com.netease.backend.db.common.stat.MemcachedStat;
import com.netease.backend.db.common.stat.TableStat;
import com.netease.backend.db.common.utils.RWLock;
import com.netease.backend.db.common.utils.SynUtils;



public class TableInfo implements Serializable, Comparable<TableInfo> {
	private static final long serialVersionUID = 6666234495405151526L;
	
	
	public static final String TYPE_MYISAM = "MYISAM";
	
	public static final String TYPE_INNODB = "INNODB";
	
	public static final String TYPE_MEMORY = "MEMORY";
	
	public static final String TYPE_NTSE = "NTSE";
	
	public static final String TYPE_INDEX_ORGANIZED = "INDEX_ORGANIZED";
	
	public static final String TYPE_HEAP_ORGANIZED = "HEAP_ORGANIZED";
	
	public static final String TYPE_INDEX_CLUSTER = "INDEX_CLUSTER";
	
	public static final String TYPE_HASH_CLUSTER = "HASH_CLUSTER";

	
	public static final byte BALANCE_FIELD_TYPE_STRING = 1;

	
	public static final byte BALANCE_FIELD_TYPE_LONG = 2;
	
    
    public static final byte BALANCE_FIELD_TYPE_LIST = 3;

	
	public static final String MEMORY_PERSIST_PREFIX = "_DDB_";
	
	public static final String MEMORY_PERSIST_PROCEDURE_NAME = "_DDB_PERSIST_PROCEDURE";

	
	private String name;

	
	private String type = TYPE_INNODB;

	
	private long startID;

	
	private long remainIDCount;

	
	private long assignCount = 1000;

	
	private List<ColumnInfo> columnList;

	
	private Map<String, IndexInfo> indexMap = null;

	
	private List<ColumnInfo> balanceFieldList;
	
	
	private List<Byte> balanceColumnTypes;

	
	private byte balanceFieldType;

	
	private boolean isBalanceFieldUnique;

	
	private Policy balancePolicy;

	
	private boolean useBucketNo;

	
	private TableStat stats = null;

	
	private RWLock lock = new RWLock();

	
	private boolean writeEnabled = true;

	
	private boolean isView = false;

	
	private List<String> baseTableNames = new LinkedList<String>();

	
	private List<TableInfo> baseTables = new LinkedList<TableInfo>();

	
	private String viewSql = "";

	
	private boolean dupkeyChk = false;

	
	private boolean uniqIncludeBf = false;

	
	private boolean isLoadBalance = true;

	
	private String comment  = null;

	
	private String modelName = null;

	
	private DbnType dbnType = DbnType.MySQL;

	
	private String tableSpace = null;

	
	private boolean persist = false;

	
	private int assignIdType = PIDConfig.TYPE_SIMPLE;
	
	
	
	
	public static String MEMCACHED_KEY_SEPARATOR = "_";
	
	
	private boolean useMemcached = false;
	
	
	private List<ColumnInfo> memcachedKeyFields = null;
	
	
	private IndexInfo memcachedKeyIndex = null;
	
	
	private String cacheVersion = "AA";
	
	
	private transient int lastFieldNumber;
	
	
	private int[] fieldNumbers = null;
	
	
	private transient MemcachedStat memcachedStat;
	
	
    private static boolean statEnable = false;
	
	
	
	
	public TableInfo(String tableName, String type, List<ColumnInfo> columns,
			long startIDValue, long remainIDCount, List<String> balFields, byte balFieldType,
			Policy ply,	Map<String, IndexInfo> indexes, long assignIDCount,
			boolean useBucket, boolean dupKeyChk, DbnType dbntype) {
		this.name = tableName;

		if (type == null)
			type = TYPE_INNODB;
		else {
			type = type.trim().toUpperCase();
			if (type.equals(""))
				this.type = TYPE_INNODB;
			else
				this.type = type;
		}

		this.startID = startIDValue;
		this.remainIDCount = remainIDCount;
		this.dbnType = dbntype;
		this.columnList = columns;
		this.assignCount = assignIDCount;
		if (assignIDCount <= 0)
			this.assignCount = 1000;
		this.useBucketNo = useBucket;
		this.dupkeyChk = dupKeyChk;

		if (indexes == null)
			this.indexMap = new HashMap<String, IndexInfo>();
		else {
			for (IndexInfo index : indexes.values()) {
				if (index.getTableName().equalsIgnoreCase(name) == false)
					throw new IllegalArgumentException("��" + name + "��������������" + index.getTableName()
							+ "��ƥ��");
				for (IndexColumn column : index.getColumnList())
					if (getColumnInfo(column.getColumnName()) == null && !column.isExpression())
						throw new IllegalArgumentException("��" + name + "������" + index.getIndexName()
								+ "�����˲����ڵ��ֶ�" + column.getColumnName() + ".");
			}
			indexMap = indexes;
		}

		this.isView = false;
		setBalanceFields(balFields);
		this.balanceFieldType = balFieldType;
		this.balancePolicy = ply;
		checkUniqueIndex();
	}

	
	public TableInfo(String tableName, String type, List<ColumnInfo> columns, List<String> balFields,
			byte balFieldType, Policy ply, List<String> baseTableNames, String viewSql, DbnType dbntype) {
		this.name = tableName;

		if (type == null)
			type = TYPE_INNODB;
		else {
			type = type.trim().toUpperCase();
			if (type.equals(""))
				this.type = TYPE_INNODB;
			else
				this.type = type;
		}

		this.columnList = columns;
		this.dbnType = dbntype;
		this.indexMap = new HashMap<String, IndexInfo>();

		this.isView = true;
		if (balFields != null && balFields.size() > 0)
			setBalanceFields(balFields);
		this.balanceFieldType = balFieldType;
		this.balancePolicy = ply;
		if (baseTableNames != null)
			this.baseTableNames = baseTableNames;
		this.viewSql = viewSql;
		checkUniqueIndex();
	}

	
	public TableInfo(String name, List<ColumnInfo> columns, Map<String, IndexInfo> indexes, DbnType dbnType)
			throws IllegalArgumentException {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("��������Ϊnull����ַ���");
		if (columns == null || columns.size() == 0)
			throw new IllegalArgumentException("��" + name + "�������б���Ϊnull����б�");

		this.name = name;
		this.dbnType = dbnType;
		columnList = columns;

		if (indexes == null)
			this.indexMap = new HashMap<String, IndexInfo>();
		else {
			for (IndexInfo index : indexes.values()) {
				if (index.getTableName().equalsIgnoreCase(name) == false)
					throw new IllegalArgumentException("��" + name + "��������������" + index.getTableName()
							+ "��ƥ��");
				for (IndexColumn column : index.getColumnList())
					if (getColumnInfo(column.getColumnName()) == null)
						throw new IllegalArgumentException("��" + name + "������" + index.getIndexName()
								+ "�����˲����ڵ��ֶ�" + column.getColumnName() + ".");
			}
			indexMap = indexes;
		}

		startID = 1;
		remainIDCount = Long.MAX_VALUE - 1;
		type = TYPE_INNODB;
		checkUniqueIndex();
	}

	
	public ColumnInfo getColumnInfo(String columnName) {
		if (this.columnList == null)
			return null;

		for (ColumnInfo column : columnList) {
			if (column.getName().equalsIgnoreCase(columnName))
				return column;
		}
		return null;
	}

	
	@Override
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;

		if (otherObject == null)
			return false;

		if (this.getClass() != otherObject.getClass())
			return false;

		TableInfo other = (TableInfo) otherObject;

		return this.isView == other.isView && this.name.equals(other.name);
	}

	
	public int compareTo(TableInfo table) {
		if (table == null)
			return -1;
		return this.getName().compareTo(table.getName());
	}

	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public long getStartID() {
		return startID;
	}

	public void setStartID(long startId) throws IllegalArgumentException {
		if (startId < 0)
			throw new IllegalArgumentException("start id����Ϊ����");
		this.startID = startId;
	}

	
	public long getRemainIDCount() {
		return remainIDCount;
	}

	public void setRemainIDCount(long remainId) throws IllegalArgumentException {
		this.remainIDCount = remainId;
	}

	
	public long getAssignCount() {
		return this.assignCount;
	}

	
	public void setAssignCount(long count) {
		this.assignCount = count;
	}
	
	
	synchronized public void setIDRange(long start_id, long count) {
		this.startID = start_id;
		this.remainIDCount = count;
	}
	
	
	synchronized public long assignID() {
		return assignID(1);
	}

	
	synchronized public long assignID(
		int idNum)
	{
		if (this.remainIDCount < idNum)
			return -1;

		long assignedID = this.startID;
		this.startID += idNum;
		this.remainIDCount -= idNum;

		return assignedID;
	}
	
	
	 
	synchronized public boolean reclaimID(
		long startId, int idNum)
	{
		if (startId + idNum != startID)
			return false;

		
		

		this.remainIDCount += idNum;
		startID -= idNum;
		return true;
	}
	
	
	public int getColumeCount() {
		return columnList.size();
	}

	
	public List<ColumnInfo> getColumns() {
		return columnList;
	}

	
	public List<String> getBalanceFields() {
		if (null == balanceFieldList || balanceFieldList.size() == 0)
			return Collections.emptyList();
		
		ArrayList<String> nameList = new ArrayList<String>(balanceFieldList.size());
		for (ColumnInfo c : balanceFieldList)
			nameList.add(c.getName());
		return nameList;
	}
	
	
	public List<ColumnInfo> getBalanceFieldColumns() {
		if (null == balanceFieldList)
			return Collections.emptyList();
		return balanceFieldList;
	}
	

	
	public List<Byte> getBalanceColumnTypes() {










		return balanceColumnTypes;
	}


	
	public void setBalanceFields(List<String> fields)
			throws IllegalArgumentException {
		if (fields == null || fields.size() == 0)
			throw new IllegalArgumentException("�����ֶβ�������Ϊ��");
		
		List<ColumnInfo> bfList = new ArrayList<ColumnInfo>(fields.size());
		List<Byte> bfTypes = new ArrayList<Byte>(fields.size());
		for (String field : fields) {
			ColumnInfo c = findColumn(field);
			if (c == null)
				throw new IllegalArgumentException("'" + fields.get(0)
						+ "'���Ǳ�'" + name + "'������");
			
			if (c.getTypeName().equalsIgnoreCase("NUMBER"))
				throw new IllegalArgumentException("����Ϊ" + c.getTypeName()
						+ "�����Բ�����Ϊ�����ֶ�");

			if (ColumnInfo.isIntegerBFType(c.getType()))
				bfTypes.add(BALANCE_FIELD_TYPE_LONG);
			else if (ColumnInfo.isStringBFType(c.getType()))
				bfTypes.add(BALANCE_FIELD_TYPE_STRING);
			else
				throw new IllegalArgumentException("����Ϊ" + c.getTypeName()
						+ "�����Բ�����Ϊ�����ֶ�");
			bfList.add(c);
		}
		
		if (bfList.size() == 1) {
			balanceFieldType = bfTypes.get(0);
		} else {
			balanceFieldType = BALANCE_FIELD_TYPE_LIST;
		}
		
		balanceFieldList = Collections.unmodifiableList(bfList);
		balanceColumnTypes = Collections.unmodifiableList(bfTypes);
	}

	
	public void setBalanceFieldNull() {
		balanceFieldList = null;
	}

	
	public byte getBalanceFieldType() {
		return balanceFieldType;
	}

	
	public Policy getBalancePolicy() {
		return balancePolicy;
	}

	
	public boolean isBalanceFieldUnique() {
		return isBalanceFieldUnique;
	}

	
	public void setBalanceFieldUnique(boolean isBalanceFieldUnique) {
		this.isBalanceFieldUnique = isBalanceFieldUnique;
	}

	
	public boolean isUseBucketNo() {
		return useBucketNo;
	}

	
	public void setUseBucketNo(boolean useBucketNo) {
		this.useBucketNo = useBucketNo;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) throws IllegalArgumentException {
		if(type == null) {
			this.type = TYPE_INNODB;
			return;
		}
		type = type.trim().toUpperCase();
		if (type.equals(""))
			this.type = TYPE_INNODB;
		else
			this.type = type;
	}

	
	public boolean isMyisam() {
		return this.type.equals(TYPE_MYISAM);
	}

	
	public boolean isTransactional(){
		return (type.equals(TYPE_INNODB)
				|| type.equals(TYPE_INDEX_ORGANIZED)
				|| type.equals(TYPE_HEAP_ORGANIZED)
				|| type.equals(TYPE_HASH_CLUSTER)
				|| type.equals(TYPE_INDEX_CLUSTER));
	}

	
	public boolean isInnodb() {
		return this.type.equals(TYPE_INNODB);
	}

	public void setBalancePolicy(Policy balancePolicy) {
		this.balancePolicy = balancePolicy;
	}

	
	public void getReadLock() {
		if (isView) {
			List<TableInfo> tableList = new LinkedList<TableInfo>();
			tableList.add(this);
			SynUtils.getReadLock(tableList);
		} else
			this.lock.getReadLock();
	}

	
	public void getWriteLock() {
		if (isView) {
			List<TableInfo> tableList = new LinkedList<TableInfo>();
			tableList.addAll(baseTables);
			Collections.sort(tableList);
			for (TableInfo table : tableList)
				table.getWriteLock();
		} else
			this.lock.getWriteLock();
	}

	
	public void getWriteLock(long timeout) {
		if (isView) {
			List<TableInfo> tableList = new LinkedList<TableInfo>();
			tableList.addAll(baseTables);
			Collections.sort(tableList);
			for (TableInfo table : tableList)
				table.getWriteLock(timeout);
		} else
			this.lock.getWriteLock(timeout);
	}

	
	public void releaseLock() {
		if (isView) {
			List<TableInfo> tableList = new LinkedList<TableInfo>();
			tableList.addAll(baseTables);
			Collections.sort(tableList);
			for (TableInfo table : tableList)
				table.releaseLock();
		} else
			this.lock.releaseLock();
	}

	
	public void releaseLockWithTimeout() {
		if (isView) {
			List<TableInfo> tableList = new LinkedList<TableInfo>();
			tableList.addAll(baseTables);
			Collections.sort(tableList);
			for (TableInfo table : tableList)
				table.releaseLockWithTimeout();
		} else
			this.lock.releaseLockWithTimeout();
	}

	public ColumnInfo findColumn(String name) {
		for (ColumnInfo c : columnList)
			if (c.getName().equalsIgnoreCase(name))
				return c;
		return null;
	}

	public Map<String, IndexInfo> getIndexMap() {
		return indexMap;
	}

	
	public List<List<IndexColumn>> getUniColumnGroup() {
		List<List<IndexColumn>> uniColumnGroup = new Vector<List<IndexColumn>>();
		Iterator<IndexInfo> iterator = indexMap.values().iterator();
		
		while (iterator.hasNext()) {
			IndexInfo indexInfo = iterator.next();
			if (indexInfo.isUnique()) {
				List<IndexColumn> indexColumns = indexInfo.getColumnList();
				uniColumnGroup.add(indexColumns);
			}
		}
		return uniColumnGroup;
	}
	
	public void setIndexMap(HashMap<String, IndexInfo> indexMap) {
		this.indexMap = indexMap;
	}

	
	public IndexInfo getPrimaryKey() {
		if (indexMap == null || indexMap.size() == 0)
			return null;

		for (IndexInfo index : indexMap.values()) {
			if (index.isPrimaryKey())
				return index;
		}
		return null;
	}

	
	public IndexInfo getIndexInfo(String indexName) {
		if (indexMap == null || indexMap.size() == 0)
			return null;

		return indexMap.get(indexName);
	}

	
	public boolean hasIndex(String indexName) {
		if (indexMap == null || indexMap.size() == 0)
			return false;
		return indexMap.containsKey(indexName);
	}

	
	public boolean addIndex(IndexInfo index, boolean isReplace) {
		if (indexMap == null)
			indexMap = new HashMap<String, IndexInfo>();

		if (isReplace == false && hasIndex(index.getIndexName()))
			return false;

		indexMap.put(index.getIndexName(), index);
		return true;
	}

	
	public IndexInfo removeIndex(String indexName) {
		return indexMap.remove(indexName);
	}

	public long getRows() {
		if (stats == null)
			return -1;
		else
			return stats.getRows();
	}

	public long getAvgRowLen() {
		if (stats == null)
			return -1;
		else
			return stats.getAvgRowLen();
	}

	public boolean isWriteEnabled() {
		if (isView)
			for (TableInfo baseTable : baseTables)
				if (baseTable.isWriteEnabled() == false)
					return false;
		return writeEnabled;
	}

	public void setWriteEnabled(boolean writeEnabled) {
		this.writeEnabled = writeEnabled;
	}

	public List<String> getBaseTableNames() {
		return baseTableNames;
	}

	public void setBaseTableNames(List<String> baseTableNames) {
		this.baseTableNames = baseTableNames;
	}

	public boolean isView() {
		return isView;
	}

	public void setView(boolean isView) {
		this.isView = isView;
	}

	public String getViewSql() {
		return viewSql;
	}

	public void setViewSql(String viewSql) {
		this.viewSql = viewSql;
	}

	public List<TableInfo> getBaseTables() {
		return baseTables;
	}

	public void setBaseTables(List<TableInfo> baseTables) {
		this.baseTables = baseTables;
	}

	public Date getStatsTime() {
		if (stats == null)
			return new Date(0);
		else
			return new Date(stats.getStatsTime());
	}

	public long getDataLen() {
		if (stats == null)
			return -1;
		else
			return stats.getDataLength();
	}

	public long getIndexLen() {
		if (stats == null)
			return -1;
		else
			return stats.getIndexLength();
	}

	public TableStat getStats() {
		return stats;
	}

	public void setStats(TableStat stats) {
		this.stats = stats;
	}

	
	public void checkUniqueIndex() {
		if (null != balancePolicy && balancePolicy.getDbList().size() == 1)
			uniqIncludeBf = true;
		if (balanceFieldList != null && balanceFieldList.size() > 0) {
			for (IndexInfo index : this.indexMap.values())
				if (index.isUnique()
						&& !index.getColumnNameList().containsAll(
								getBalanceFields())) {
					uniqIncludeBf = false;
					return;
				}
		}
		uniqIncludeBf = true;
	}

	
	public boolean needCheckDupKey() {
		return this.dupkeyChk == true && this.uniqIncludeBf == false;
	}

	
	public boolean isIndepFile() {
		if (this.type.equals(TYPE_MYISAM))
			return true;
		if (this.balancePolicy == null)
			return false;
		for (Database db : balancePolicy.getDbList())
			if (db.isInnodbFilePerTable())
				return true;
		return false;
	}

	public boolean isDupkeyChk() {
		return dupkeyChk;
	}

	public void setDupkeyChk(boolean dupkeyChk) {
		this.dupkeyChk = dupkeyChk;
	}

	public boolean isUniqIncludeBf() {
		return uniqIncludeBf;
	}

	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
	    return this.comment;
	}

	
	public void setModelName(String name) {
		this.modelName = name;
	}

	public String getModelName() {
	    return this.modelName;
	}





	public DbnType getDbnType() {
		return this.dbnType;
	}

	public void setTableSpace(String tablespace) {
		this.tableSpace = tablespace;
	}

	public String getTableSpace() {
		return this.tableSpace;
	}

	
	public void delColumns(String[] columns) {
		for (int i = 0; i < columns.length; i++) {
			ColumnInfo column = new ColumnInfo(columns[i], 1, false, dbnType);
			this.getColumns().remove(column);
			Iterator<IndexInfo> iter = this.getIndexMap().values().iterator();
			while (iter.hasNext()) {
				IndexInfo index = iter.next();
				IndexColumn indexColumn = index.getIndexColumn(columns[i]);
				if (indexColumn != null) {
					if (index.getColumnList().size() <= 1) {
						iter.remove();
						if (index.isUnique())
							this.checkUniqueIndex();
					} else
						index.getColumnList().remove(indexColumn);
				}
			}
		}
		
		
		this.updateFieldNumbers();
	}

	
	public boolean isLoadBalance() {
		return isLoadBalance;
	}

	
	public void setLoadBalance(boolean isLoadBalance) {
		this.isLoadBalance = isLoadBalance;
	}

	
	public boolean isPersist() {
		return persist;
	}

	
	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public int getAssignIdType() {
		return assignIdType;
	}

	public void setAssignIdType(int assignIdType) {
		this.assignIdType = assignIdType;
	}

	
	public IndexInfo getBaseIndex() {
		IndexInfo pk = getPrimaryKey();
		if (isBaseIndex(pk)) {
			return pk;
		}

		for (IndexInfo key : indexMap.values()) {
			if (isBaseIndex(key)) {
				return pk;
			}
		}

		return null;
	}

	public boolean isBaseIndex(IndexInfo key) {
		if (key != null) {
			ArrayList<IndexColumn> columns = key.getColumnList();
			if (columns != null && columns.size() >= 1) {
				boolean isBaseColumn = true;
				for (IndexColumn indexCol : columns) {
					if (!ColumnInfo.isIntegerBFType(getColumnInfo(
							indexCol.getColumnName()).getType())) {
						isBaseColumn = false;
						break;
					}
				}
				return isBaseColumn;
			}
		}
		return false;
	}
	
	
	public void addColumn(ColumnInfo newColumn, boolean specifiedPosition, String preColumnName) {
		if (specifiedPosition) {
			if (preColumnName != null) {
				for (int i = 0; i < columnList.size(); i++) {
					if (columnList.get(i).getName().equals(preColumnName)) {
						columnList.add(i + 1, newColumn);
						break;
					}
				}
			} else {
				columnList.add(0, newColumn);
			}
		} else {
			columnList.add(newColumn);
		}
		
		this.updateFieldNumbers();
	}
	
	
	public void modifyColumn(ColumnInfo modifiedColumn,
			boolean specifiedPosition, String preColumnName) {
		changeColumn(modifiedColumn, modifiedColumn.getName(),
				specifiedPosition, preColumnName);
	}
	
	
	public void changeColumn(ColumnInfo newColumn, String oldName,
			boolean specifiedPosition, String preColumnName) {
		if (specifiedPosition) {
			List<ColumnInfo> newColumnList = new ArrayList<ColumnInfo>();
			if (preColumnName != null) {
				for (ColumnInfo c : this.getColumns()) {
					if (c.getName().equals(preColumnName)) {
						newColumnList.add(c);
						newColumnList.add(newColumn);
					} else if (c.getName().equals(oldName))
						continue;
					else
						newColumnList.add(c);
				}
			} else {
				newColumnList.add(newColumn);
				for (ColumnInfo c : this.getColumns())
					if (!c.getName().equals(oldName))
						newColumnList.add(c);
			}
			this.columnList = newColumnList;
		} else {
			final ColumnInfo oldColumn = getColumnInfo(oldName);
			final int index = columnList.indexOf(oldColumn);
			if (index >= 0) {
				columnList.set(index, newColumn);
			}
		}
		
		
		if (memcachedKeyFields != null && memcachedKeyFields.size() > 0) {
			for (int i = 0; i < memcachedKeyFields.size(); i++) {
				if (memcachedKeyFields.get(i).getName()
						.equalsIgnoreCase(oldName)) {
					memcachedKeyFields.set(i, newColumn);
					break;
				}
			}
		}
		
		
		this.updateFieldNumbers();
	}

	
	public List<ColumnInfo> getMemcachedKeyFields() {
		return memcachedKeyFields;
	}

	private void setMemcachedKeyFields(List<ColumnInfo> memcachedKeyFields) {
		if (memcachedKeyFields != null && memcachedKeyFields.size() < 1)
			throw new IllegalArgumentException("memcached key fields should not emtpy.");
		this.memcachedKeyFields = memcachedKeyFields;
	}

	public IndexInfo getMemcachedKeyIndex() {
		return memcachedKeyIndex;
	}

	
	public void setMemcachedKeyIndex(String indexName) throws IllegalArgumentException {
		if (indexName == null) {
			setMemcachedKeyFields(null);
			this.memcachedKeyIndex = null;
			return;
		}
		
		IndexInfo index = getIndexInfo(indexName);
		if (index == null)
			throw new IllegalArgumentException("table " + name
					+ " doesn't contain an index named " + indexName);
		if (!(index.isPrimaryKey() || index.isUnique())) {
			throw new IllegalArgumentException(indexName
					+ " is not primary/unique index");
		}

		List<ColumnInfo> keyColumns = new ArrayList<ColumnInfo>(index.getColumnList().size());
		for (IndexColumn ic : index.getColumnList()) {
			keyColumns.add(getColumnInfo(ic.getColumnName()));
		}
		
		setMemcachedKeyFields(keyColumns);
		this.memcachedKeyIndex = index;
	}

	public boolean isUseMemcached() {
		return useMemcached;
	}

	public void setUseMemcached(boolean useMemcached) {
		this.useMemcached = useMemcached;
	}

	public String getCacheVersion() {
		return cacheVersion;
	}
	
	public void setCacheVersion(String newVersion)
			throws IllegalArgumentException {
		checkVersion(newVersion);
		this.cacheVersion = newVersion;
	}
	
	public int getLastFieldNumber() {
		return lastFieldNumber;
	}

	public void setLastFieldNumber(int lastFieldNumber) {
		this.lastFieldNumber = lastFieldNumber;
	}

	
	public void updateCacheVersion() {
		String newVersion = calNextVersion(cacheVersion);
		setCacheVersion(newVersion);
	}
	
	
	public static String calNextVersion(String preVersion) {
		checkVersion(preVersion);
		char[] versionChars = preVersion.toCharArray();
		for (int i = versionChars.length - 1; i >= 0; i--) {
			if (versionChars[i] < 'Z') {
				versionChars[i] ++;
				break;
			} else {
				versionChars[i] = 'A';
			}
		}
		return new String(versionChars);
	}

	
	public String getMemcachedKeyPrefix() {
		return new StringBuilder(name).append(MEMCACHED_KEY_SEPARATOR).append(
				cacheVersion).toString();
	}
	
	
	private static void checkVersion(String version)
			throws IllegalArgumentException {
		if (version == null || version.length() != 2)
			throw new IllegalArgumentException("Version '" + version
					+ "' is not suitable.");
		for (char ch : version.toCharArray()) {
			if (!Character.isUpperCase(ch))
				throw new IllegalArgumentException("Version '" + version
						+ "' is not suitable.");
		}
	}

	
	private void updateFieldNumbers() {
		int[] tmpFieldNumbers = new int[columnList.size()];
    	for (int i = 0; i < columnList.size(); i ++) 
    		tmpFieldNumbers[i] = columnList.get(i).getFieldNumber();
    	this.fieldNumbers = tmpFieldNumbers;
	}
	
	public int[] getFieldNumbers() {
		if (this.fieldNumbers == null) {
			synchronized (this) {
				if (this.fieldNumbers == null)
					updateFieldNumbers();
			}
		}
		return this.fieldNumbers;
	}
	
	public String getMemcachedKey(List<String> values) throws SQLException {
		StringBuilder MemcachedKey = new StringBuilder(getMemcachedKeyPrefix());
		for (String str : values) {
			MemcachedKey.append(MEMCACHED_KEY_SEPARATOR).append(str); 
		}
		return MemcachedKey.toString();
	}
	
	
	public MemcachedStat getMemcachedStat() {
		if (memcachedStat == null) {
			synchronized (this) {
				if (memcachedStat == null) {
					memcachedStat = new MemcachedStat();
				}
			}
		}
		return memcachedStat;
	}
	
	public static boolean isStatEnable() {
		return statEnable;
	}

	public static void setStatEnable(boolean statEnable) {
		TableInfo.statEnable = statEnable;
	}
	
	
	public static void checkTableName(String tableName)
			throws IllegalArgumentException {
		if (null == tableName || "".equals(tableName.trim())) {
			throw new IllegalArgumentException("����Ϊ��");
		}
		tableName = tableName.trim();
		if (tableName.length() > 64) {
			throw new IllegalArgumentException("�����������������ܳ���64���ַ�");
		}
		for (int i = 0; i < tableName.length(); i++) {
			char c = tableName.charAt(i);
			if (c == '/')
				throw new IllegalArgumentException("�������ܺ����ַ�'/'");
			if (c == '\\')
				throw new IllegalArgumentException("�������ܺ����ַ�'\\'");
			if (c == '.')
				throw new IllegalArgumentException("�������ܺ����ַ�'.'");
		}
	}
}
