package com.netease.backend.db.common.stat;

import java.io.Serializable;



public class StmtExplain implements Serializable{

	private static final long serialVersionUID = -6418102375105555381L;

	
	private String sqlSig;
	
	
	private ExplainItem keyExplain;
	
	
	private int count;
	
	
	private String optimizeDesc = "";
	
	
	public StmtExplain(String sql, ExplainItem item, int count)
	{
		this.sqlSig = sql;
		this.keyExplain = item;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public ExplainItem getKeyExplain() {
		return keyExplain;
	}

	public String getSqlSig() {
		return sqlSig;
	}
	
	public String toString()
	{
		return "sql="+sqlSig+", "+keyExplain;
	}
	
	
	public String getOptimizeDesc()
	{
		return optimizeDesc;
	}
	
	
	public void setOptimizeDesc(String desc)
	{
		this.optimizeDesc=desc;
	}
	
}
