package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DbnCluster implements Serializable, Comparable<DbnCluster> {
	private static final long serialVersionUID = 5845251657448157827L;

	
	public static final int CLUSTER_TYPE_INDEX = 1;
	
	public static final int CLUSTER_TYPE_HASH = 2;

	
	private String clusterName;
	
	private String clusterIndexName = null;
	
	private String clusterIndexTableSpace = null;
	
	private boolean clusterIndexReverse = false;
	
	private List<String> tableNameList;
	
	private int clusterType;
	
	private Policy policy;
	
	private String tableSpace;
	
	private int size;
	
	private List<DbnClusterColumn> columns;

	
	public DbnCluster(String name, Policy policy, int type, int size,
			List<DbnClusterColumn> columns, boolean clusterIndexReverse) {
		this.clusterName = name;
		this.policy = policy;
		this.clusterType = type;
		if (clusterType != CLUSTER_TYPE_INDEX
				&& clusterType != CLUSTER_TYPE_HASH)
			throw new IllegalArgumentException("����ȷ��DbnCluster���ͣ�");
		tableNameList = new ArrayList<String>();
		if (size <= 0)
			throw new IllegalArgumentException("DbnCluster��size�������0");
		this.size = size;
		this.columns = columns;
		this.clusterIndexReverse = clusterIndexReverse;
	}

	
	public DbnCluster(String name, Policy policy, int type, String indexName,
			String idxTableSpace, String tableSpace, int size,
			List<DbnClusterColumn> columns, boolean clusterIndexReverse) {
		this.clusterName = name;
		this.policy = policy;
		this.clusterType = type;
		this.clusterIndexName = indexName;
		this.clusterIndexTableSpace = idxTableSpace;
		if (clusterType != CLUSTER_TYPE_INDEX
				&& clusterType != CLUSTER_TYPE_HASH)
			throw new IllegalArgumentException("����ȷ��DbnCluster���ͣ�");
		this.tableNameList = new ArrayList<String>();
		this.tableSpace = tableSpace;
		if (size <= 0)
			throw new IllegalArgumentException("DbnCluster��size�������0");
		this.size = size;
		this.columns = columns;
		this.clusterIndexReverse = clusterIndexReverse;
	}

	
	public int getTableCount() {
		return tableNameList.size();
	}

	
	public boolean isContain(String tbl_name) {
		return tableNameList.contains(tbl_name);
	}

	
	public void addTable(String tbl_name) {
		tableNameList.add(tbl_name);
	}

	public String getName() {
		return clusterName;
	}

	public void setClusterIndexName(String name) {
		this.clusterIndexName = name;
	}

	public String getClusterIndexName() {
		return clusterIndexName;
	}

    public boolean hasClusterIndex() {
        return (null == this.clusterIndexName ? false : true);
    }

	public int getType() {
		return clusterType;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy ply) {
		this.policy = ply;
	}

	public String getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		if (size < 1)
			throw new IllegalArgumentException("Incorrect cluster size!");
		this.size = size;
	}

	public String getClusterIndexTableSpace() {
		return clusterIndexTableSpace;
	}

	public void setClusterIndexTableSpace(String clusterIndexTableSpace) {
		this.clusterIndexTableSpace = clusterIndexTableSpace;
	}

	public List<DbnClusterColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DbnClusterColumn> columns) {
		this.columns = columns;
	}

	public boolean isClusterIndexReverse() {
		return clusterIndexReverse;
	}

	public void setClusterIndexReverse(boolean clusterIndexReverse) {
		this.clusterIndexReverse = clusterIndexReverse;
	}

	public List<String> getTableNameList() {
		return tableNameList;
	}

	public void setTableNameList(List<String> tableNameList) {
		this.tableNameList = tableNameList;
	}

	@Override
	public String toString(){
	    return this.getName();
	}

    public int compareTo(DbnCluster o) {
        int result;
        result = this.getName().compareTo(o.getName());
        return result;
    }
}
