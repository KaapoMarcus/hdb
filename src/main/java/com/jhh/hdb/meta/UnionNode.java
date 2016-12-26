package com.jhh.hdb.meta;

import java.util.List;

public class UnionNode extends Node {

	String getCatalogName = null ;
	String getSchemaName = null ;

	String getTableName = null ;
	
	String getColumnName = null ;
	String getColumnLabel = null ;
	String getColumnTypeName = null ;
	String getColumnClassName=null ;
	
	int getColumnType = 0 ;
	int getColumnDisplaySize = 0 ;
	int getScale = 0 ;
	int getPrecision = 0 ;
	boolean isSigned = false ;
	
	
	@Override
	public String toString() {
		return "UnionNode [getCatalogName=" + getCatalogName + ", getSchemaName=" + getSchemaName + ", getTableName="
				+ getTableName + ", getColumnName=" + getColumnName + ", getColumnLabel=" + getColumnLabel
				+ ", getColumnTypeName=" + getColumnTypeName + ", getColumnClassName=" + getColumnClassName
				+ ", getColumnType=" + getColumnType + ", getColumnDisplaySize=" + getColumnDisplaySize + ", getScale="
				+ getScale + ", getPrecision=" + getPrecision + ", isSigned=" + isSigned + "]";
	}


	public UnionNode(int level, int levelid, String nodename,
			List<Node> parents, int nodetype) {
		super(level, levelid, nodename, parents, nodetype);
		// TODO Auto-generated constructor stub
	}

}
