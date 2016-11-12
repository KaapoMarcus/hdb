package com.jhh.hdb.meta;

public class NodeInstance {

	public Integer nodeinstance_id;
	public String nodeinstance_ip;
	public Integer nodeinstance_port;
	public NodeInstance(Integer nodeinstance_id, String nodeinstance_ip, Integer nodeinstance_port) {
		super();
		this.nodeinstance_id = nodeinstance_id;
		this.nodeinstance_ip = nodeinstance_ip;
		this.nodeinstance_port = nodeinstance_port;
	}
	public Integer getNodeinstance_id() {
		return nodeinstance_id;
	}
	public void setNodeinstance_id(Integer nodeinstance_id) {
		this.nodeinstance_id = nodeinstance_id;
	}
	public String getNodeinstance_ip() {
		return nodeinstance_ip;
	}
	public void setNodeinstance_ip(String nodeinstance_ip) {
		this.nodeinstance_ip = nodeinstance_ip;
	}
	public Integer getNodeinstance_port() {
		return nodeinstance_port;
	}
	public void setNodeinstance_port(Integer nodeinstance_port) {
		this.nodeinstance_port = nodeinstance_port;
	}

}
