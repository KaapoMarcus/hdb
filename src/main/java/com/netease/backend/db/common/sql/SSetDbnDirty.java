package com.netease.backend.db.common.sql;

import java.util.List;
import java.util.Set;


public class SSetDbnDirty extends Statement {
	private static final long serialVersionUID = -2081120127327175656L;
	
	private List<String> names;
	private boolean dirty;
	private Set<String> plys;
	
	public SSetDbnDirty(List<String> dbns, boolean b) {
		super();
		this.names = dbns;
		this.dirty = b;
	}
	
	public List<String> getNames() {
		return this.names;
	}
	
	public boolean isDirty() {
		return this.dirty;
	}
	
	public Set<String> getDirtyPlys() {
		return this.plys;
	}
	
	public void setDirtyPlys(Set<String> dirtyPlys) {
		this.plys = dirtyPlys;
	}
}
