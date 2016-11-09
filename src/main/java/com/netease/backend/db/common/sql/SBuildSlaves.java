package com.netease.backend.db.common.sql;

import java.util.List;


public class SBuildSlaves extends Statement {
	private static final long serialVersionUID = -3512635940910476207L;
	
	private List<String> names;
	private List<String> linkDirs;
	
	public SBuildSlaves(List<String> names) {
		super();
		this.names = names;
	}
	
	public void setLinkDirs(List<String> dirs) {
		this.linkDirs = dirs;
	}
	
	public List<String> getNames() {
		return this.names;
	}
	
	public List<String> getLinkDirs() {
		return this.linkDirs;
	}
}
