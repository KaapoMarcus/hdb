package com.jhh.hdb.meta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLStatementImpl;

public class MapReduceGroupNode extends MapReduceNode {

	Map<Integer, String> select_field_map = new HashMap<Integer, String>();
	Map<Integer, String> group_field_map = new HashMap<Integer, String>();
	
	public String where_str = "";
	public String table_name="";
	public String select_field_str="";
	public String shuffle_field_str="";
	
	public String map_sql="";
	public String reduce_sql="";
	
	boolean has_having=false;
	String having_str = "";
	
	public MapReduceGroupNode(int level, int levelid, String nodename,
			List<MapReduceNode> parents, int nodetype,SQLStatementImpl stmt) {
		super(level, levelid, nodename, parents, nodetype,stmt);
		 
		// TODO Auto-generated constructor stub
	}

}
