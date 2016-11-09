package com.netease.backend.db.common.sql.plan;

import java.io.Serializable;


public class PlanJob implements Serializable, Cloneable {
	private static final long serialVersionUID = 3313368699849354944L;
	
	private String sql;
	private String name;
	private PlanType type;
	
	public PlanJob(String sql, String name, PlanType t) {
		if (sql.endsWith(";"))
			sql = sql.substring(0, sql.length() - 1);
		this.sql = sql;
		this.name = name;
		this.type = t;
	}
	
	@Override
	public PlanJob clone() {
		try {
			return (PlanJob) super.clone();
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}

	public String getSql() {
		return sql;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSqlDesc() {
		return "JOB " + name + " " + sql;
	}
	
	public PlanType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return getSqlDesc();
	}
	
	
	public boolean isExclusive() {
		if (type == PlanType.BUCKETNO_ADD || type == PlanType.DATA_BACKUP
				|| type == PlanType.DATA_EXPORT
				|| type == PlanType.DATA_MIGRATION
				|| type == PlanType.SCHEMA_MODIFY
				|| type == PlanType.USER_MANAGEMENT)
			return true;
		return false;
	}
}
