package com.jhh.hdb.sql;

import java.util.HashMap;
import java.util.Map;

public class HashRangeMap {

	Long range_number;
	int hash_num;
	Map<Integer, Integer> hash_node_map = new HashMap<Integer, Integer>();

	public HashRangeMap(Long range_number, int hash_num,
			Map<Integer, Integer> hash_node_map) {
		super();
		this.range_number = range_number;
		this.hash_num = hash_num;
		this.hash_node_map = hash_node_map;
	}

	public int get_nodeid(Long id) throws Exception {

		int ret = 0;
		int hash = (int) (id / range_number) % hash_num;
		ret = hash_node_map.get(hash);
		return ret;

	}

}
