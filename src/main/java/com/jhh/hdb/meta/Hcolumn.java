package com.jhh.hdb.meta;

public class Hcolumn {

	public Integer hcolumn_id;
	public String hcolumn_name;
	public String hcolumn_type;
	public Integer htable_id;
	public Integer hdc_flag;
	public Integer hdc_idxnum;
	public Hcolumn() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Hcolumn(Integer hcolumn_id, String hcolumn_name, String hcolumn_type, Integer htable_id, Integer hdc_flag,
			Integer hdc_idxnum) {
		super();
		this.hcolumn_id = hcolumn_id;
		this.hcolumn_name = hcolumn_name;
		this.hcolumn_type = hcolumn_type;
		this.htable_id = htable_id;
		this.hdc_flag = hdc_flag;
		this.hdc_idxnum = hdc_idxnum;
	}
	public Integer getHcolumn_id() {
		return hcolumn_id;
	}
	public void setHcolumn_id(Integer hcolumn_id) {
		this.hcolumn_id = hcolumn_id;
	}
	public String getHcolumn_name() {
		return hcolumn_name;
	}
	public void setHcolumn_name(String hcolumn_name) {
		this.hcolumn_name = hcolumn_name;
	}
	public String getHcolumn_type() {
		return hcolumn_type;
	}
	public void setHcolumn_type(String hcolumn_type) {
		this.hcolumn_type = hcolumn_type;
	}
	public Integer getHtable_id() {
		return htable_id;
	}
	public void setHtable_id(Integer htable_id) {
		this.htable_id = htable_id;
	}
	public Integer getHdc_flag() {
		return hdc_flag;
	}
	public void setHdc_flag(Integer hdc_flag) {
		this.hdc_flag = hdc_flag;
	}
	public Integer getHdc_idxnum() {
		return hdc_idxnum;
	}
	public void setHdc_idxnum(Integer hdc_idxnum) {
		this.hdc_idxnum = hdc_idxnum;
	}

}
