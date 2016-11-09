package com.jhh.hdb.meta;

public class TableEntry {
	public Integer id;
	public String name;
	public String tab_type;

	public Long startid;
	public String policyname;
	public String balancefield;

	public TableEntry(Integer id, String name, String tab_type, Long startid,
			String policyname, String balancefield) {
		super();
		this.id = id;
		this.name = name;
		this.tab_type = tab_type;
		this.startid = startid;
		this.policyname = policyname;
		this.balancefield = balancefield;
	}
}
