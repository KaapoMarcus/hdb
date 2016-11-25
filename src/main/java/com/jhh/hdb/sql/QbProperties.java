package com.jhh.hdb.sql;

import org.apache.hadoop.hive.ql.QueryProperties;


public class QbProperties extends QueryProperties {
	
	public	boolean  have_union;
	public	boolean  have_unionall;
	public	boolean  have_from_join;
	public	boolean  have_from_subquery;
	public	boolean  have_having;
	public	boolean  have_where;
	public	boolean  have_group;
	public	boolean  have_order;
	public	boolean  have_limit;
	
}
