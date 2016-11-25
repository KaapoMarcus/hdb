package com.jhh.hdb.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class TableInfo {

	public String db;
	public String tablename;
	public String balancefield;
	public int balancetype;
	
	public Map<Integer, String> connstr_map = new HashMap<Integer, String>();
	public Map<Integer, Connection> conn_map = new HashMap<Integer, Connection>();
	public Map<Integer, String> col_map = new HashMap<Integer, String>();
	public Map<Integer, String> index_map = new HashMap<Integer, String>();
	
	public TableInfo(String db, String tablename, String balancefield,
			int balancetype, Map<Integer, String> node_map) {
		super();
		this.db = db;
		this.tablename = tablename;
		this.balancefield = balancefield;
		this.balancetype = balancetype;
		this.connstr_map = node_map;
	}
	public TableInfo() {
		super();
	}

}
