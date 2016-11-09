package com.jhh.hdb.meta;


public class IdEntity  {
	int nodeid;
	String tablename;
	String balancefield;
	Long minid;
	Long maxid;
	
	public IdEntity(int nodeid, String tablename, String balancefield,
			Long minid, Long maxid) {
		super();
		this.nodeid = nodeid;
		this.tablename = tablename;
		this.balancefield = balancefield;
		this.minid = minid;
		this.maxid = maxid;
	}
	
	
	
	
}
