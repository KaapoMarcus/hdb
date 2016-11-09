package com.netease.backend.db.common.stat;

import java.util.Set;
import java.util.TreeSet;


public class DDBSsObject {
	
	private DDBStmtStat stmtStat;
	
	private Set<String> dbnSet;
	
	public DDBSsObject(DDBStmtStat stmtStat) {
		this.stmtStat = stmtStat;
		dbnSet = new TreeSet<String>();
	}

	public DDBStmtStat getStmtStat() {
		return stmtStat;
	}

	public Set<String> getDbnSet() {
		return dbnSet;
	}
	
	public void addDbn(String dbn) {
		dbnSet.add(dbn);
	}
	
	public void addDbns(String[] dbns) {
		for (String dbn: dbns)
			dbnSet.add(dbn);
	}
}
