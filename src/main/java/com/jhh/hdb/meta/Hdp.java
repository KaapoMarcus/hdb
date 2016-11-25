package com.jhh.hdb.meta;

public class Hdp {
	public String hdp_name;
	public String hdp_type;
	public String hdp_algo;
	public String hdp_min;
	public String hdp_max;
	public String hdp_step;
	public Hdp() {
		super();
	}
	public Hdp(String hdp_name, String hdp_type, String hdp_algo, String hdp_min, String hdp_max, String hdp_step) {
		super();
		this.hdp_name = hdp_name;
		this.hdp_type = hdp_type;
		this.hdp_algo = hdp_algo;
		this.hdp_min = hdp_min;
		this.hdp_max = hdp_max;
		this.hdp_step = hdp_step;
	}
	public String getHdp_name() {
		return hdp_name;
	}
	public void setHdp_name(String hdp_name) {
		this.hdp_name = hdp_name;
	}
	public String getHdp_type() {
		return hdp_type;
	}
	public void setHdp_type(String hdp_type) {
		this.hdp_type = hdp_type;
	}
	public String getHdp_algo() {
		return hdp_algo;
	}
	public void setHdp_algo(String hdp_algo) {
		this.hdp_algo = hdp_algo;
	}
	public String getHdp_min() {
		return hdp_min;
	}
	public void setHdp_min(String hdp_min) {
		this.hdp_min = hdp_min;
	}
	public String getHdp_max() {
		return hdp_max;
	}
	public void setHdp_max(String hdp_max) {
		this.hdp_max = hdp_max;
	}
	public String getHdp_step() {
		return hdp_step;
	}
	public void setHdp_step(String hdp_step) {
		this.hdp_step = hdp_step;
	}
}
