package com.netease.backend.db.common.sql;

import java.util.List;


public class SDropMigrate extends Statement {
	private static final long serialVersionUID = -6453789159219005297L;
	
	private List<Integer> ids;
	
	public SDropMigrate(List<Integer> ids) {
		this.ids = ids;
	}
	
	public List<Integer> getIds() {
		return ids;
	}
}
