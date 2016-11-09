package com.netease.backend.db.common.sql;

import java.util.List;
import java.util.Set;


public class SStat extends Statement {
    private static final long serialVersionUID = 1L;

    public enum StatType {
		SHOW_STAT_DDB,
		SHOW_STAT_EXTENDED_DDB,
		SHOW_STAT_MYSQL,
		SHOW_STAT_EXTENDED_MYSQL,
		SHOW_STAT_OPS,
		SHOW_STAT_INDEX,
		SHOW_STAT_COLUMN,

		SHOW_STAT_MCV,
		SHOW_STAT_EXPLAIN,
		SHOW_STAT_BUCKET,
		SHOW_STAT_TABLE_MEMCACHED
	}
	
	
	private StatType statType;
	
	private String file;
	private String where;
	private Set<String> groupBy;
	private String having;
	private String orderBy;
	private int limit;
	private List<String> labelBy;
	
	public SStat(StatType statType) {
		this.statType = statType;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public Set<String> getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(Set<String> groupBy) {
		this.groupBy = groupBy;
	}
	public String getHaving() {
		return having;
	}
	public void setHaving(String having) {
		this.having = having;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public StatType getStatType() {
		return statType;
	}

	public List<String> getLabelBy() {
		return labelBy;
	}

	public void setLabelBy(List<String> labelBy) {
		this.labelBy = labelBy;
	}
}
