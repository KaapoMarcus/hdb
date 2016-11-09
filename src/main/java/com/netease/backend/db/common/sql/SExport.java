package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.management.DumpConfig;

public class SExport extends Statement {
	private static final long serialVersionUID = 1122507352409828530L;
	private DumpConfig config;
	
	public SExport(DumpConfig config) {
		super();
		this.config = config;
	}

	










































	public DumpConfig getConfig() {
		return config;
	}
}
