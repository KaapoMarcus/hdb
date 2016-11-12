package com.jhh.hdb.meta;

public class Htable {

	public Integer htable_id;
	public String htable_name;
	public Integer hdb_id;
	public Integer hdp_id;
	public Htable(Integer htable_id, String htable_name, Integer hdb_id, Integer hdp_id) {
		super();
		this.htable_id = htable_id;
		this.htable_name = htable_name;
		this.hdb_id = hdb_id;
		this.hdp_id = hdp_id;
	}
	public Integer getHtable_id() {
		return htable_id;
	}
	public void setHtable_id(Integer htable_id) {
		this.htable_id = htable_id;
	}
	public String getHtable_name() {
		return htable_name;
	}
	public void setHtable_name(String htable_name) {
		this.htable_name = htable_name;
	}
	public Integer getHdb_id() {
		return hdb_id;
	}
	public void setHdb_id(Integer hdb_id) {
		this.hdb_id = hdb_id;
	}
	public Integer getHdp_id() {
		return hdp_id;
	}
	public void setHdp_id(Integer hdp_id) {
		this.hdp_id = hdp_id;
	}

}
