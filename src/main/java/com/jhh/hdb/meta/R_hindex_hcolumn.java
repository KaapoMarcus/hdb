package com.jhh.hdb.meta;

public class R_hindex_hcolumn {

	
	public Integer hindex_id ;
	public Integer hcolumn_id ;
	public Integer hcolumn_idxnum;
	public R_hindex_hcolumn() {
		super();
	}
	public R_hindex_hcolumn(Integer hindex_id, Integer hcolumn_id, Integer hcolumn_idxnum) {
		super();
		this.hindex_id = hindex_id;
		this.hcolumn_id = hcolumn_id;
		this.hcolumn_idxnum = hcolumn_idxnum;
	}
	public Integer getHindex_id() {
		return hindex_id;
	}
	public void setHindex_id(Integer hindex_id) {
		this.hindex_id = hindex_id;
	}
	public Integer getHcolumn_id() {
		return hcolumn_id;
	}
	public void setHcolumn_id(Integer hcolumn_id) {
		this.hcolumn_id = hcolumn_id;
	}
	public Integer getHcolumn_idxnum() {
		return hcolumn_idxnum;
	}
	public void setHcolumn_idxnum(Integer hcolumn_idxnum) {
		this.hcolumn_idxnum = hcolumn_idxnum;
	}
	
	
}
