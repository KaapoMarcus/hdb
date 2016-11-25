package com.jhh.hdb.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinNode extends Node {
	Map<Integer, String> table_alias_map = new HashMap<Integer, String>(); // 有别名的情况
	Map<Integer, String> table_cond_map = new HashMap<Integer, String>(); // on里面的非join条件
	Map<Integer, String> table_map = new HashMap<Integer, String>(); 
	
	// 表和结果字段的对应关系
	Map<Integer, Map<Integer, String>> select_field_map = new HashMap<Integer, Map<Integer, String>>();
	Map<Integer, String> join_field_map = new HashMap<Integer, String>();

	public String where_str = "";
	public String table_name="";
	public String select_field_str="";
	public String shuffle_field_str="";
	
	public String map_sql="";
	public String reduce_sql="";
	
	String[][] join_matrix; // 不考虑两种表的多个join条件  t1.x=t2.x and t1.y=t2.y and t1.z=t2.z  
	public JoinNode(int level, int levelid, String nodename,
			List<Node> parents, int nodetype) {
		super(level, levelid, nodename, parents, nodetype);
		// TODO Auto-generated constructor stub
	}

}
