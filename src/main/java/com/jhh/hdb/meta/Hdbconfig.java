package com.jhh.hdb.meta;

public class Hdbconfig {

	public Integer config_id;
	public String config_name;
	
	public String config_type;
	public String config_value;
	public Hdbconfig(Integer config_id, String config_name, String config_type, String config_value) {
		super();
		this.config_id = config_id;
		this.config_name = config_name;
		this.config_type = config_type;
		this.config_value = config_value;
	}
	public Integer getConfig_id() {
		return config_id;
	}
	public void setConfig_id(Integer config_id) {
		this.config_id = config_id;
	}
	public String getConfig_name() {
		return config_name;
	}
	public void setConfig_name(String config_name) {
		this.config_name = config_name;
	}
	public String getConfig_type() {
		return config_type;
	}
	public void setConfig_type(String config_type) {
		this.config_type = config_type;
	}
	public String getConfig_value() {
		return config_value;
	}
	public void setConfig_value(String config_value) {
		this.config_value = config_value;
	}

}
