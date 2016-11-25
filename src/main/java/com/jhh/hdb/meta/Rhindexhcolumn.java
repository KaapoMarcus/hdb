package com.jhh.hdb.meta;

public class Rhindexhcolumn {
	public String hdb_name;
	public String htable_name;
	public String hindex_name;
	public String hcolumn_name;
	public Integer hcolumn_orderno;
	public Rhindexhcolumn() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Rhindexhcolumn(String hdb_name, String htable_name, String hindex_name, String hcolumn_name,
			Integer hcolumn_orderno) {
		super();
		this.hdb_name = hdb_name;
		this.htable_name = htable_name;
		this.hindex_name = hindex_name;
		this.hcolumn_name = hcolumn_name;
		this.hcolumn_orderno = hcolumn_orderno;
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
	public String getHcolumn_name() {
		return hcolumn_name;
	}
	public void setHcolumn_name(String hcolumn_name) {
		this.hcolumn_name = hcolumn_name;
	}
	public Integer getHcolumn_orderno() {
		return hcolumn_orderno;
	}
	public void setHcolumn_orderno(Integer hcolumn_orderno) {
		this.hcolumn_orderno = hcolumn_orderno;
	}

}
