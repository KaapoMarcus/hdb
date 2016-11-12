package com.jhh.hdb.meta;

public class NodeDb {

	public Integer nodedb_id;
	public String nodedb_name;
	public Integer nodeinstance_id;
	public Integer hdb_id;
	public NodeDb(Integer nodedb_id, String nodedb_name, Integer nodeinstance_id, Integer hdb_id) {
		super();
		this.nodedb_id = nodedb_id;
		this.nodedb_name = nodedb_name;
		this.nodeinstance_id = nodeinstance_id;
		this.hdb_id = hdb_id;
	}
	public Integer getNodedb_id() {
		return nodedb_id;
	}
	public void setNodedb_id(Integer nodedb_id) {
		this.nodedb_id = nodedb_id;
	}
	public String getNodedb_name() {
		return nodedb_name;
	}
	public void setNodedb_name(String nodedb_name) {
		this.nodedb_name = nodedb_name;
	}
	public Integer getNodeinstance_id() {
		return nodeinstance_id;
	}
	public void setNodeinstance_id(Integer nodeinstance_id) {
		this.nodeinstance_id = nodeinstance_id;
	}
	public Integer getHdb_id() {
		return hdb_id;
	}
	public void setHdb_id(Integer hdb_id) {
		this.hdb_id = hdb_id;
	}

}
