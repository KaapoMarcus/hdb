package com.jhh.hdb.meta;

public class Hdb {
	public Integer hdb_id;
	public String hdb_name;
	public Hdb(Integer hdb_id, String hdb_name) {
		super();
		this.hdb_id = hdb_id;
		this.hdb_name = hdb_name;
	}

	public Integer getHdb_id() {
		return hdb_id;
	}
	public void setHdb_id(Integer hdb_id) {
		this.hdb_id = hdb_id;
	}
	public String getHdb_name() {
		return hdb_name;
	}
	public void setHdb_name(String hdb_name) {
		this.hdb_name = hdb_name;
	}

}
