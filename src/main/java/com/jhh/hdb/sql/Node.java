package com.jhh.hdb.sql;

import java.util.Arrays;
import java.util.List;

public class Node {

	int level;
	int levelid;
	String nodename;
	public String sql;
	List<Node> parents;
	int nodetype;

	List<TableInfo> inputs;
	TableInfo output;

	public Node(int level, int levelid, String nodename, List<Node> parents,
			int nodetype) {
		super();
		this.level = level;
		this.levelid = levelid;
		this.nodename = nodename;
		this.parents = parents;
		this.nodetype = nodetype;
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
