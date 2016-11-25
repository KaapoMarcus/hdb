package com.jhh.hdb.meta;

public class Hcolumn {
	public String hdb_name;
	public String htable_name;
	public String hcolumn_name;
	public String hcolumn_type;
	public Integer htable_orderno;
	public Integer hdc_flag;
	public Integer 	hdc_orderno ;
	
	public Hcolumn() {
		super();
	}

	public Hcolumn(String hdb_name, String htable_name, String hcolumn_name, String hcolumn_type,
			Integer htable_orderno, Integer hdc_flag, Integer hdc_orderno) {
		super();
		this.hdb_name = hdb_name;
		this.htable_name = htable_name;
		this.hcolumn_name = hcolumn_name;
		this.hcolumn_type = hcolumn_type;
		this.htable_orderno = htable_orderno;
		this.hdc_flag = hdc_flag;
		this.hdc_orderno = hdc_orderno;
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

	public Integer getHtable_orderno() {
		return htable_orderno;
	}

	public void setHtable_orderno(Integer htable_orderno) {
		this.htable_orderno = htable_orderno;
	}

	public Integer getHdc_flag() {
		return hdc_flag;
	}

	public void setHdc_flag(Integer hdc_flag) {
		this.hdc_flag = hdc_flag;
	}

	public Integer getHdc_orderno() {
		return hdc_orderno;
	}

	public void setHdc_orderno(Integer hdc_orderno) {
		this.hdc_orderno = hdc_orderno;
	}



	
}
