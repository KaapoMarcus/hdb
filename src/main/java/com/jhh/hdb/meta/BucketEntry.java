package com.jhh.hdb.meta;

public class BucketEntry {
	public Integer id;
	public Integer bucket_num;

	public Integer db_id;
	public String policyname;

	public BucketEntry(Integer id, Integer bucket_num, Integer db_id,
			String policyname) {
		super();
		this.id = id;
		this.bucket_num = bucket_num;
		this.db_id = db_id;
		this.policyname = policyname;
	}
}
