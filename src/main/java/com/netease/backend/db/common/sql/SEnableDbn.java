package com.netease.backend.db.common.sql;


public class SEnableDbn extends Statement {
    private static final long serialVersionUID = -4067458716097026009L;
    
    private String dbn;

	public SEnableDbn(String dbn) {
		this.dbn = dbn;
	}

	public String getDbn() {
		return dbn;
	}
}
