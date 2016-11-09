package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.enumeration.SQLType;



public class Policy implements Serializable, Comparable<Policy> {
	
	private static final long serialVersionUID = 4692024266972548987L;
	
	public static String DEFAULT_HASH = "com.netease.backend.db.common.schema.DBHash";

	
	public static final byte STATUS_NORMAL = 0;

	
	public static final byte STATUS_MOVING = 1;
	
	
    public static final byte TYPE_STATIC_HASH = 0;

    
    public static final byte TYPE_DYNAMIC_HASH = 1;




	
	private String name;

	
	private byte policyType;
	
	
	private String hashName;

	
	private int policyArg;

	
	private boolean transactional = true;

	
	private List<TableInfo> tableList;

	
	private byte status;

	
	private int[] migBuckets;

	
	private int bucketCount;

	
	private ArrayList<BucketInfo> buckets;

	
	private List<Database> dbList;

	
	private Database desDb;

	
	private Hash hash;

	
	private String comment;

	
	private DbnType dbnType = DbnType.MySQL;
	
	
	private short[] dbIndexArray = null;

	
	public Policy(String plyName, byte plyType,int plyArg, int plyBucketCount, String hashName,
			byte plyStatus,int[] plyMigBuckets, Database plyDesDB, DbnType dbnType) {
		this.name = plyName;
		this.policyType = plyType;
		this.policyArg = plyArg;
		this.tableList = new LinkedList<TableInfo>();
		this.status = plyStatus;
		this.bucketCount = plyBucketCount;
		if(plyMigBuckets != null)
			Arrays.sort(plyMigBuckets);
		this.migBuckets = plyMigBuckets;
		this.desDb = plyDesDB;
		this.buckets = new ArrayList<BucketInfo>(plyBucketCount);
		this.dbnType = dbnType;
		this.hashName = hashName;

		
		for (int i=0;i<plyBucketCount;i++)
			this.buckets.add(i,new BucketInfo(i, null));

		this.dbList = new LinkedList<Database>();
		if (policyType == TYPE_STATIC_HASH) {
			try {
				hash = (Hash) Class.forName(hashName).newInstance();
				hash.setBucketCount(this.bucketCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	public Policy(String name, List<Database> srcBuckets, DbnType type) throws IllegalArgumentException {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("�������������Ϊnull����ַ���");
		if (srcBuckets == null || srcBuckets.size() == 0)
			throw new IllegalArgumentException("bucket�б���Ϊnull���");

		
		try {
			hash = (Hash) Class.forName(DEFAULT_HASH).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		hash.setBucketCount(srcBuckets.size());

		
		dbList = new LinkedList<Database>();
		bucketCount = srcBuckets.size();
		buckets = new ArrayList<BucketInfo>(bucketCount);
		dbnType = null == type ? DbnType.MySQL : type;
		int i = 0;
		for (Database dbInfo: srcBuckets) {
			if (dbInfo.getDbnType() != dbnType)
				throw new IllegalArgumentException("�ڵ������������Ե����Ͳ�һ��");
			BucketInfo b = new BucketInfo(i++, dbInfo);
			buckets.add(b);
			addDb(dbInfo);
		}

		
		this.name = name;
		tableList = new LinkedList<TableInfo>();
		policyType = 0;
	}

	
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;

		if (otherObject == null)
			return false;

		if (this.getClass() != otherObject.getClass())
			return false;

		Policy other = (Policy) otherObject;

		return this.name.equals(other.name);
	}

	
	public int hashCode()
	{
		return this.name.hashCode();
	}

	
	public String getName() {
		return name;
	}

	
	public byte getPolicyType() {
		return policyType;
	}

	
	public int getPolicyArg() {
		return policyArg;
	}

	
	public List<TableInfo> getTableList() {
		return tableList;
	}

	
	public String getComment() {
		return comment;
	}

	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	public void setHash(Hash hash) {
		this.hash = hash;
	}
	
	
	public boolean addTableInfo(TableInfo table) {
		TableInfo oldTable;

		ListIterator<TableInfo> iter = this.tableList.listIterator();

		while (iter.hasNext()) {
			oldTable = (TableInfo) iter.next();
			if (oldTable.getName().equals(table.getName())) {
				iter.remove();
				iter.add(table);
				return true;
			}
		}
		return this.tableList.add(table);
	}


	public boolean removeTable(TableInfo table) {
		return tableList.remove(table);
	}


	
	public void setTableList(List<TableInfo> tableList) {
		this.tableList = tableList;
	}

	
	public String getTableNames() {
		int size = tableList.size();
		
		if(0 < size) {
			StringBuilder sb = new StringBuilder();
			sb.append(tableList.get(0).getName());
			for(int i = 1; i < size; i++) {
				sb.append(", " + tableList.get(i).getName());
			}
			return sb.toString();
		}
		return "";
	}



	
	public byte getStatus() {
		return status;
	}

	
	public void setStatus(byte newStatus) {
		status = newStatus;
	}

	
	public int getBucketCount() {
		return bucketCount;
	}

	
	public void setBucketCount(int aBucketCount) {
		this.bucketCount = aBucketCount;
	}

	
	public int getBucketByKey(String strValue) {
		return hash.hash(strValue);
	}

	
	public int getBucketByKey(long longValue) {
		return hash.hash(longValue);
	}
	
	
    public int getBucketByKey(List<Object> listValue) {
        return hash.hash(listValue);
    }


	


	


	
	public ArrayList<BucketInfo> getBuckets(){
		return this.buckets;
	}

	
	public boolean setSrcBucket(int bucketNo, Database db)
	{
		
		if (db == null)
			return false;
		else if (db.getDbnType() != this.dbnType)
			return false;
		if(bucketNo < 0 || bucketNo >= this.bucketCount)
			return false;

		
		BucketInfo bucket = (BucketInfo)this.buckets.get(bucketNo);
		if (bucket == null)
			return false;

		Database preDb = bucket.getSrcDB();

		
		bucket.setSrcDB(db);

		
		if (!this.dbList.contains(db))
			this.dbList.add(db);

		

		
		if (preDb == null)
			return true;

		

		BucketInfo bucketTemp;
		Database dbTemp;

		for (int i = 0; i < this.bucketCount; i++)
		{
			bucketTemp = (BucketInfo)this.buckets.get(i);
			dbTemp = bucketTemp.getSrcDB();

			if (dbTemp == null) 
				continue;

			if (dbTemp.getURL().equals(preDb.getURL())) 
				return true;
		}

		
		this.dbList.remove(preDb);
		return true;
	}

	
	public boolean setDesBucket(int bucketNo, Database db)
	{
		
		if (db == null || bucketNo < 0 || bucketNo > this.bucketCount)
			return false;

		
		BucketInfo bucket = (BucketInfo)this.buckets.get(bucketNo);
		if (bucket == null)
			return false;

		this.desDb = db;
		return true;
	}

	
	public List<Database> getDbList() {
			return dbList;
}

	
	public List<Database> getDbList(byte statType)
	{
		return dbList;
	}

	
	public Database getDesDb() {
		return desDb;
	}

	
	public String getHashName() {
		if (policyType == TYPE_STATIC_HASH)
			return hash.getClass().getName();
		else 
			return hashName;
	}

	
	public Database getMigDesDB() {
		return this.desDb;
	}


	
	public boolean isMig(int bucketNo)
	{
		if(migBuckets == null)
			return false;
		for(int i=0;i<migBuckets.length;i++)
			if(bucketNo == migBuckets[i])
				return true;
		return false;
	}


	
	public BucketInfo getBucketInfo(int bucketNo) {
		for(BucketInfo bi : buckets) {
			if(bi.getBucketNo() == bucketNo) {
				return bi;
			}
		}
		return null;
	}

	
	public void setDesDb(Database desDb) {
		this.desDb = desDb;
	}

	
	public boolean isUseDB(Database db)
	{
		if(this.getDesDb()!=null && this.getDesDb().equals(db))
			return true;

		Iterator<BucketInfo> itr = this.getBuckets().iterator();
		while(itr.hasNext())
		{
			BucketInfo bucket = itr.next();
			if(bucket.getSrcDB()!=null && bucket.getSrcDB().equals(db))
				return true;
		}
		return false;
	}

	
	public void addDb(Database db)
	{
		if (db == null)
			return;
		if(!db.getDbnType().equals(dbnType)){
			throw new IllegalArgumentException("addDb failed, db type does not match policy type");
		}
		if(!this.dbList.contains(db))
			dbList.add(db);
	}

	
	public void delDb(Database db)
	{
		dbList.remove(db);
	}

	
	public int[] getMigBuckets() {
		return migBuckets;
	}

	
	public void setMigBuckets(int[] migBuckets) {
		if (migBuckets != null)
			Arrays.sort(migBuckets);
		this.migBuckets = migBuckets;
	}

	
	public boolean isTransactional() {
		return transactional;
	}

	
	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	
	public void addOptCount(SQLType optType) throws RuntimeException
	{
		ArrayList<BucketInfo> buckets = this.getBuckets();
		for(int i=0; i<buckets.size();i++)
		{
			BucketInfo bucket = buckets.get(i);
			if(bucket != null)
			{
				
				if(optType == SQLType.SELECT)
					bucket.readAdd();
				else if(optType == SQLType.INSERT)
					bucket.insertAdd();
				else if(optType == SQLType.UPDATE)
					bucket.updateAdd();
			}

		}
	}

	
	public static List<Database> genRoundRobin(List<Database> dbList, int bucketCount) throws IllegalArgumentException {
		if (dbList == null || dbList.size() == 0)
			throw new IllegalArgumentException("���ݿ�ڵ��б���Ϊnull���");
		if (bucketCount < 1)
			throw new IllegalArgumentException("bucket������ȷ: " + bucketCount + "������Ϊ������");

		List<Database> srcBuckets = new LinkedList<Database>();
		for (int i = 0; i < bucketCount; i++) {
			int j = i % dbList.size();
			srcBuckets.add(dbList.get(j));
		}
		return srcBuckets;
	}

	
	public static List<Database> genSerial(List<Database> dbList, int bucketCount) {
		if (dbList == null || dbList.size() == 0)
			throw new IllegalArgumentException("���ݿ�ڵ��б���Ϊnull���");
		if (bucketCount < 1)
			throw new IllegalArgumentException("bucket������ȷ: " + bucketCount + "������Ϊ������");

		List<Database> srcBuckets = new LinkedList<Database>();
		float bucketPerDbn = (float) bucketCount / dbList.size();
		for (int i = 0; i < bucketCount; i++) {
			int dbIdx = (int) (i / bucketPerDbn);
			srcBuckets.add(dbList.get(dbIdx));
		}
		return srcBuckets;
	}

	public void removeDb(Database db) {
		List<BucketInfo> oldBuckets = buckets;
		buckets = new ArrayList<BucketInfo>();
		dbList = new LinkedList<Database>();
		for (BucketInfo bucket : oldBuckets) {
			if (bucket.getSrcDB() != db)
				buckets.add(bucket);
			addDb(db);
		}
		hash.setBucketCount(buckets.size());
		bucketCount = buckets.size();
	}

	public int compareTo(Policy another) {
		if(another == null) return -1;
		return name.compareTo(another.name);
	}

	public int[] getDbnBucketNos(Database db){
		LinkedList<Integer> bucketNos = new LinkedList<Integer>();
		for(BucketInfo bucket : buckets)
			if(bucket.getSrcDB().equals(db))
				bucketNos.add(bucket.getBucketNo());
		int[] buckets = new int[bucketNos.size()];
		int i=0;
		for(Integer no : bucketNos)
			buckets[i++] = no.intValue();
		return buckets;
	}

	public DbnType getDbnType() {
		return dbnType;
	}

	public void setDbnType(DbnType dbnType) {
		this.dbnType = dbnType;
	}

	@Override
	public String toString(){
	    return this.getName();
	}
	
	
	public void compressBucketList() {
		if (buckets == null)
			return;
		short[] dbNumArray = new short[bucketCount]; 
		for (int i = 0; i < bucketCount; i++)
			for (short j = 0; j < dbList.size(); j++)
				if (buckets.get(i).getSrcDBURL().equals(dbList.get(j).getURL())) {
					dbNumArray[i] = j; 
					break;
				}
		buckets = null;
		dbIndexArray = dbNumArray;
	}
	
	
	public void recoverBucketList() {
		if (dbIndexArray == null)
			return;
		buckets = new ArrayList<BucketInfo>();
		for (short i = 0; i<dbIndexArray.length; i++) {
			
			BucketInfo bucket = new BucketInfo(i, dbList.get(dbIndexArray[i]));
			buckets.add(bucket);
		}
		dbIndexArray = null;
	}
}
