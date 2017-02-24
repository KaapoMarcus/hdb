package com.jhh.hdb.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatementImpl;

public class MapReduceNode {

	public int level;
	public int levelid;
	public String nodename;
	public String sql;
	public List<MapReduceNode> parents;
	public int nodetype;


	public String map_sql="";
	public String reduce_sql="";
	
	public List<String> mapsql=new ArrayList<String>();
	public List<String> reducesql=new ArrayList<String>();
	
	public List<Htable> inputs;
	public Htable output;
	
	public SQLStatementImpl stmt ;

	public MapReduceNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MapReduceNode(int level, int levelid, String nodename, List<MapReduceNode> parents,
			int nodetype , SQLStatementImpl stmt) {
		super();
		this.level = level;
		this.levelid = levelid;
		this.nodename = nodename;
		this.parents = parents;
		this.nodetype = nodetype;
		this.stmt=stmt;
	}

	@Override
	public String toString() {

		String ret = "";
		if (parents != null && parents.size() > 0) {
			ret += "Node [level=" + level + ", levelid=" + levelid
					+ ", nodename=" + nodename + ", parents="
					+ Arrays.toString(parents.toArray()) + "]";
		} else {
			ret += "Node [level=" + level + ", levelid=" + levelid
					+ ", nodename=" + nodename + ", parents=null" + "]";
		}
		return ret;
	}

}
