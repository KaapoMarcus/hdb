package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.List;




public class StatSig  implements Serializable {
	private static final long serialVersionUID = 377824966188783733L;
	
	
	private String sqlSig;
	
	
	private String planSkeleton;
	
	
	private List<String> tables;
	
	
	private int dbnId = 0;
	
	
	private String 	originalSql;
	
	
	private List<List<Object>> values;
	
	
	public StatSig(String sql, String skeleton, List<String> tables, int dbnID, 
			String original, List<List<Object>> valueList)
	{
		this.sqlSig = sql;
		this.planSkeleton = skeleton;
		this.tables = tables;
		this.dbnId = dbnID;
		this.originalSql = original;
		this.values = valueList;
	}

	public String getPlanSkeleton() {
		return planSkeleton;
	}

	public String getSqlSig() {
		return sqlSig;
	}

	public List<String> getTables() {
		return tables;
	}
	
	public int getDbnId() {
		return dbnId;
	}
	
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((planSkeleton == null) ? 0 : planSkeleton.hashCode());
		result = PRIME * result + ((sqlSig == null) ? 0 : sqlSig.hashCode());
		result = PRIME * result + ((originalSql == null) ? 0 : originalSql.hashCode());
		result = PRIME * result + dbnId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final StatSig other = (StatSig) obj;
		if (planSkeleton == null) {
			if (other.planSkeleton != null)
				return false;
		} else if (!planSkeleton.equals(other.planSkeleton))
			return false;
		if (sqlSig == null) {
			if (other.sqlSig != null)
				return false;
		} else if (!sqlSig.equals(other.sqlSig))
			return false;
		if(originalSql == null) {
			if(other.originalSql != null)
				return false;
		} else if(!originalSql.equals(other.originalSql))
			return false;
		return dbnId == other.dbnId;
	}

	public List<List<Object>> getValues() {
		return values;
	}

	public String getOriginalSql() {
		return originalSql;
	}
}
