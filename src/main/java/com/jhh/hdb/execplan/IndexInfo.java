package com.jhh.hdb.execplan;

import java.util.HashMap;
import java.util.Map;

public class IndexInfo {

	public String db;
	public String tablename;
	public String balancefield;
	public int balancetype;
	public Map<Integer, String> node_map = new HashMap<Integer, String>();
	public Map<Integer, String> col_map = new HashMap<Integer, String>();
	public Map<Integer, String> index_map = new HashMap<Integer, String>();
	
	public IndexInfo(String db, String tablename, String balancefield,
			int balancetype, Map<Integer, String> node_map) {
		super();
		this.db = db;
		this.tablename = tablename;
		this.balancefield = balancefield;
		this.balancetype = balancetype;
		this.node_map = node_map;
	}
	public IndexInfo() {
		super();
	}

}
