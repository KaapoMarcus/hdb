package com.netease.backend.db.common.schema;


public class DBHash extends Hash{
	
	private static final long serialVersionUID = 1155167750587274211L;

	
	public int hash(long value) {
		int v = (int)(value % bucketCount);
		return (v >=0 ? v : -v);
	}
	
	
	public int hash(String value) {
		
		
		return (value == null ? 0: Math.abs(value.hashCode())) % bucketCount;
	}
	
	
	public String getHashFun(int bucketId) {
		return "%"+ bucketCount + "=" + bucketId;
	}
	
}
