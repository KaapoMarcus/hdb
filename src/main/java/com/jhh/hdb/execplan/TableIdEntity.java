package com.jhh.hdb.execplan;

public class TableIdEntity {
	int nodeid;
	String tablename;
	String balancefield;
	Long minid;
	Long maxid;
	Long datacount;

	public TableIdEntity(int nodeid, String tablename, String balancefield,
			Long minid, Long maxid, Long datacount) {
		super();
		this.nodeid = nodeid;
		this.tablename = tablename;
		this.balancefield = balancefield;
		this.minid = minid;
		this.maxid = maxid;
		this.datacount = datacount;
	}

}
