package com.jhh.hdb.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderNode extends Node {

	public Map<Integer, String> select_field_map = new HashMap<Integer, String>();
	public Map<Integer, String> order_field_map = new HashMap<Integer, String>();
	
	public String where_str = "";
	public String table_name="";
	public String select_field_str="";
	public String shuffle_field_str="";
	
	public String map_sql="";
	public String reduce_sql="";
	
	public OrderNode(int level, int levelid, String nodename,
			List<Node> parents, int nodetype) {
		super(level, levelid, nodename, parents, nodetype);
		// TODO Auto-generated constructor stub
	}

}
