package com.jhh.hdb.sql;

public class HashRange {

	Long range_number;
	int hash_num;

	public HashRange(Long range_number, int hash_num) {
		super();
		this.range_number = range_number;
		this.hash_num = hash_num;
	}

	public int get_nodeid(Long id) throws Exception {

		int ret = 0;
		ret = (int) (id / range_number) % hash_num;
		return ret;

	}

}
