package com.jhh.hdb.meta;

public class Hindex {
	public String hdb_name;
	public String htable_name;
	public String hindex_name;
	public String hindex_type;
	public Hindex() {
		super();
	}
	public Hindex(String hdb_name, String htable_name, String hindex_name, String hindex_type) {
		super();
		this.hdb_name = hdb_name;
		this.htable_name = htable_name;
		this.hindex_name = hindex_name;
		this.hindex_type = hindex_type;
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
	public String getHindex_name() {
		return hindex_name;
	}
	public void setHindex_name(String hindex_name) {
		this.hindex_name = hindex_name;
	}
	public String getHindex_type() {
		return hindex_type;
	}
	public void setHindex_type(String hindex_type) {
		this.hindex_type = hindex_type;
	}
}
