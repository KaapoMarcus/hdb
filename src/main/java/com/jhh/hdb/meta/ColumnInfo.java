package com.jhh.hdb.meta;


public class ColumnInfo {

	public int index; // 从1开始
	public String tablename; // 列名
	public String label; // 列名
	public int type;
	public String typename;
	public int precision;
	public int scale;
	public String other ; // 除了名字和类型之外的,给
	
	public ColumnInfo() {
		super();
	}

}
