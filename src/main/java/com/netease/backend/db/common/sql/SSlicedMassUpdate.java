package com.netease.backend.db.common.sql;




public class SSlicedMassUpdate extends Statement {
	private static final long serialVersionUID = -1835222481065019506L;
	
	private String table;	
	private String condition;	
	private SSlicedMassUpdatePair start;	
	private SSlicedMassUpdatePair end;	
	private int sliceSize;	
	private double sleepRatio;	
	private long duration;	
	private String action;	
	private String setClause;	
	private String user;	
	private String password;	
	private String baseIndex;  
	
	public SSlicedMassUpdate() {
		password = "";
		start = null;
		end = null;
		sliceSize = 10000;
		sleepRatio = 1.0;
		duration = 14400;	
		baseIndex = null;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public SSlicedMassUpdatePair getEnd() {
		return end;
	}

	public void setEnd(SSlicedMassUpdatePair end) {
		this.end = end;
	}

	public String getSetClause() {
		return setClause;
	}

	public void setSetClause(String setClause) {
		this.setClause = setClause;
	}

	public double getSleepRatio() {
		return sleepRatio;
	}

	public void setSleepRatio(double sleepRatio) {
		this.sleepRatio = sleepRatio;
	}

	public int getSliceSize() {
		return sliceSize;
	}

	public void setSliceSize(int sliceSize) {
		this.sliceSize = sliceSize;
	}

	public SSlicedMassUpdatePair getStart() {
		return start;
	}

	public void setStart(SSlicedMassUpdatePair start) {
		this.start = start;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}
	
	public long getDuration() {
		return this.duration;
	}
	
	public void setDuration(long l) {
		this.duration = l;
	}
	
	public String getUser() {
		return this.user;
	}
	
	public void setUser(String s) {
		this.user = s;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String p) {
		this.password = p;
	}
	
	public String toString() {
		String actionDesc = "delete";
		if (action.equalsIgnoreCase("update"))
			actionDesc = "update \"" + setClause + "\"";
		String userStr = "";
		if (user != null && !"".equals(user)) 
			userStr = "-u " + user + " -p '" + password + "'";
		return "SlicedMassUpdate --start " + start + " --end " + end
		+ " --slice-size " + sliceSize + " --sleep-ratio " + sleepRatio 
		+ " --duration " + duration
		+ (baseIndex != null ? " --index " + baseIndex : "")
		+ " " + userStr + " --" + actionDesc + " " + table + " \"" + condition + "\"";
	}

	public String getBaseIndex() {
		return baseIndex;
	}

	public void setBaseIndex(String baseIndex) {
		this.baseIndex = baseIndex;
	}
	
	
}
