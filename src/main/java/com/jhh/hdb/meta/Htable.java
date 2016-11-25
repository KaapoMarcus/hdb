package com.jhh.hdb.meta;

public class Htable {
	public String hdb_name;
	public String htable_name;
	public String hdp_name;
	public Htable() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Htable(String hdb_name, String htable_name, String hdp_name) {
		super();
		this.hdb_name = hdb_name;
		this.htable_name = htable_name;
		this.hdp_name = hdp_name;
	}
	public String getHdb_name() {
		return hdb_name;
	}
	public void setHdb_name(String hdb_name) {
		this.hdb_name = hdb_name;
	}
	public String getHtable_name() {
		return htable_name;
	}
	public void setHtable_name(String htable_name) {
		this.htable_name = htable_name;
	}
	public String getHdp_name() {
		return hdp_name;
	}
	public void setHdp_name(String hdp_name) {
		this.hdp_name = hdp_name;
	}

}
