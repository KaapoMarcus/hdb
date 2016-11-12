package com.jhh.hdb.meta;

public class Hindex {

	public Integer hindex_id;
	public Integer htable_id;
	public Integer hindex_type;
	
	public String hindex_name;

	public Hindex() {
		super();
	}

	public Hindex(Integer hindex_id, Integer htable_id, Integer hindex_type, String hindex_name) {
		super();
		this.hindex_id = hindex_id;
		this.htable_id = htable_id;
		this.hindex_type = hindex_type;
		this.hindex_name = hindex_name;
	}

	public Integer getHindex_id() {
		return hindex_id;
	}

	public void setHindex_id(Integer hindex_id) {
		this.hindex_id = hindex_id;
	}

	public Integer getHtable_id() {
		return htable_id;
	}

	public void setHtable_id(Integer htable_id) {
		this.htable_id = htable_id;
	}

	public Integer getHindex_type() {
		return hindex_type;
	}

	public void setHindex_type(Integer hindex_type) {
		this.hindex_type = hindex_type;
	}

	public String getHindex_name() {
		return hindex_name;
	}

	public void setHindex_name(String hindex_name) {
		this.hindex_name = hindex_name;
	}
	
	

}
