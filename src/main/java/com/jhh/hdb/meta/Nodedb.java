package com.jhh.hdb.meta;

public class Nodedb {
	public String nodedb_name;
	public String nodeinstance_name;
	public String hdb_name;
	public Integer hdb_orderno;
	public Nodedb() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Nodedb(String nodedb_name, String nodeinstance_name, String hdb_name , Integer hdb_orderno) {
		super();
		this.nodedb_name = nodedb_name;
		this.nodeinstance_name = nodeinstance_name;
		this.hdb_name = hdb_name;
		this. hdb_orderno=  hdb_orderno;
	}
	public String getNodedb_name() {
		return nodedb_name;
	}
	public void setNodedb_name(String nodedb_name) {
		this.nodedb_name = nodedb_name;
	}
	public String getNodeinstance_name() {
		return nodeinstance_name;
	}
	public void setNodeinstance_name(String nodeinstance_name) {
		this.nodeinstance_name = nodeinstance_name;
	}
	public String getHdb_name() {
		return hdb_name;
	}
	public void setHdb_name(String hdb_name) {
		this.hdb_name = hdb_name;
	}

	public Integer getHdb_orderno() {
		return hdb_orderno;
	}

	public void setHdb_orderno(Integer hdb_orderno) {
		this.hdb_orderno = hdb_orderno;
	}

}
