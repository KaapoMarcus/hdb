package com.netease.backend.db.common.sql;

import java.util.ArrayList;

import com.netease.backend.db.common.schema.TableInfo;


public class SCreateTable extends Statement {
    private static final long serialVersionUID = 6700471119779383278L;
    
    
	private TableInfo table;
	
	private String policyName;
	
	private int bucketCount;
	
	
	private String dbnClusterName = null;
	
	ArrayList<String> clusterColumns;
	
	private String ntseParam = null;
	
	public SCreateTable(TableInfo table, String policyName, int bucketCount) {
		this.table = table;
		this.policyName = policyName;
		this.bucketCount = bucketCount;
	}
	
	public int getBucketCount() {
		return bucketCount;
	}
	
	public String getPolicyName() {
		return policyName;
	}
	
	public TableInfo getTable() {
		return table;
	}

	public String getDbnClusterName() {
		return dbnClusterName;
	}

	public void setDbnClusterName(String dbnClusterName) {
		this.dbnClusterName = dbnClusterName;
	}
	
	public ArrayList<String> getClusterColumns() {
		return clusterColumns;
	}

	public void setClusterColumns(ArrayList<String> columnNames) {
		this.clusterColumns = columnNames;
	}

	public String getNtseParam() {
		return ntseParam;
	}

	public void setNtseParam(String ntseParam) {
		this.ntseParam = ntseParam;
	}
}
