package com.netease.backend.db.common.sql;

import java.io.Serializable;
import java.util.List;

import com.netease.util.Pair;


public class Clients implements Serializable {
    private static final long serialVersionUID = 1L;
    
	private List<Integer> ids;
	
	private List<String> names;
	
	private List<String> ips;
	
	private List<Pair<String, Integer>> ipPorts;
	
	public Clients(List<Integer> ids, List<String> names, List<String> ips, List<Pair<String, Integer>> ipPorts) {
		this.ids = ids;
		this.names = names;
		this.ips = ips;
		this.ipPorts = ipPorts;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public List<Pair<String, Integer>> getIpPorts() {
		return ipPorts;
	}

	public List<String> getIps() {
		return ips;
	}

	public List<String> getNames() {
		return names;
	}
}
