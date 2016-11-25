package com.jhh.hdb.meta;

public class Hdbconfig {

	public String config_name;
	public String config_type;
	public String config_value;
	public Hdbconfig() {
		super();
	}
	public Hdbconfig(String config_name, String config_type, String config_value) {
		super();
		this.config_name = config_name;
		this.config_type = config_type;
		this.config_value = config_value;
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
