package com.jhh.hdb.execplan;

import java.util.List;

public class SimpleNode extends Node {

	
	public String where_str = "";
	public String table_name="";
	public String select_field_str="";
	public SimpleNode(int level, int levelid, String nodename,
			List<Node> parents, int nodetype) {
		super(level, levelid, nodename, parents, nodetype);
		// TODO Auto-generated constructor stub
	}

}
