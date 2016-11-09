package com.netease.backend.db.common.schema;

import java.io.Serializable;

import com.netease.backend.db.common.enumeration.SQLType;


public class BucketInfo implements Serializable {

	private static final long serialVersionUID = 8174259238199994711L;
	
	private static boolean statEnabled = false;
	
	
	int bucketNo;
	
	
	Database srcDB = null;
	
	
	String srcDBURL = "";
	
	
	int readCount = 0;	
	
	
	int updateCount = 0;
	
	
	int insertCount = 0;
	
	
	public BucketInfo(int bucketNumber, Database sourceDB)
	{
		this.bucketNo=bucketNumber;
		this.srcDB = sourceDB;
		if(srcDB !=null)
			this.srcDBURL = srcDB.getURL();
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		BucketInfo other = (BucketInfo)otherObject;
		
		return (this.bucketNo == other.bucketNo);
	}
	
	
	public int hashCode()
	{
		return Integer.valueOf(bucketNo).hashCode();
	}
	
	
	synchronized public void readAdd() throws RuntimeException
	{
		if(statEnabled == false)
			return;
		if( readCount < Integer.MAX_VALUE)
			this.readCount++;
		else
			throw new RuntimeException();
	}
	
	
	synchronized public void updateAdd() throws RuntimeException
	{
		if(statEnabled == false)
			return;
		if(updateCount < Integer.MAX_VALUE)
			this.updateCount++;
		else
			throw new RuntimeException();
	}
	
	
	synchronized public void insertAdd() throws RuntimeException
	{
		if(statEnabled == false)
			return;
		if(insertCount < Integer.MAX_VALUE)
			this.insertCount++;
		else
			throw new RuntimeException();
	}
	
	
	synchronized public void clear()
	{
		this.readCount = 0;
		this.updateCount = 0;
		this.insertCount = 0;
	}

	
	public Database getSrcDB() {
		return srcDB;
	}

	
	public void setSrcDB(Database srcDB) {
		this.srcDB = srcDB;
		if(srcDB !=null)
			this.srcDBURL = srcDB.getURL();
	}

	
	public int getBucketNo() {
		return bucketNo;
	}

	
	synchronized public int getInsertCount() {
		return insertCount;
	}

	
	synchronized public int getReadCount() {
		return readCount;
	}

	
	synchronized public int getUpdateCount() {
		return updateCount;
	}

	public String getSrcDBURL() {
		if((srcDBURL == null || srcDBURL.equals("")) && (srcDB != null))
			return srcDB.getURL();
		return srcDBURL;
	}

	public void setSrcDBURL(String srcDBURL) {
		this.srcDBURL = srcDBURL;
	}
	
	
	public void addOptCount(SQLType optType) throws RuntimeException
	{
		if(optType == SQLType.SELECT)
			readAdd();
		else if(optType == SQLType.INSERT)
			insertAdd();
		else if(optType == SQLType.UPDATE)
			updateAdd();
	}

	public static boolean isStatEnabled() {
		return statEnabled;
	}

	public static void setStatEnabled(boolean enabled) {
		BucketInfo.statEnabled = enabled;
	}
}
