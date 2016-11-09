package com.jhh.hdb.meta;

public class DbEntry {
	public Integer id;
	public String mydb_name;
	public String db_ip;

	public Integer db_port;
	public String db_name;

	public DbEntry(Integer id, String mydb_name, String db_ip, Integer db_port,
			String db_name) {
		super();
		this.id = id;
		this.mydb_name = mydb_name;
		this.db_ip = db_ip;
		this.db_port = db_port;
		this.db_name = db_name;
	}
}
