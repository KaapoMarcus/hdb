package com.jhh.hdb.meta;

public class Hdp {

	public Integer hdp_id;
	public String hdp_name;
	public Integer hdp_type;
	public Integer hdp_algo;
	public Long hdp_min;
	public Long hdp_max;
	public Integer hdp_step;
	public Hdp(Integer hdp_id, String hdp_name, Integer hdp_type, Integer hdp_algo, Long hdp_min, Long hdp_max,
			Integer hdp_step) {
		super();
		this.hdp_id = hdp_id;
		this.hdp_name = hdp_name;
		this.hdp_type = hdp_type;
		this.hdp_algo = hdp_algo;
		this.hdp_min = hdp_min;
		this.hdp_max = hdp_max;
		this.hdp_step = hdp_step;
	}
	public Integer getHdp_id() {
		return hdp_id;
	}
	public void setHdp_id(Integer hdp_id) {
		this.hdp_id = hdp_id;
	}
	public String getHdp_name() {
		return hdp_name;
	}
	public void setHdp_name(String hdp_name) {
		this.hdp_name = hdp_name;
	}
	public Integer getHdp_type() {
		return hdp_type;
	}
	public void setHdp_type(Integer hdp_type) {
		this.hdp_type = hdp_type;
	}
	public Integer getHdp_algo() {
		return hdp_algo;
	}
	public void setHdp_algo(Integer hdp_algo) {
		this.hdp_algo = hdp_algo;
	}
	public Long getHdp_min() {
		return hdp_min;
	}
	public void setHdp_min(Long hdp_min) {
		this.hdp_min = hdp_min;
	}
	public Long getHdp_max() {
		return hdp_max;
	}
	public void setHdp_max(Long hdp_max) {
		this.hdp_max = hdp_max;
	}
	public Integer getHdp_step() {
		return hdp_step;
	}
	public void setHdp_step(Integer hdp_step) {
		this.hdp_step = hdp_step;
	}

}
