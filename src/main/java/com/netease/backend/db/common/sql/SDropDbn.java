package com.netease.backend.db.common.sql;


public class SDropDbn extends Statement {
    private static final long serialVersionUID = -8234428222023535381L;
    
    private String dbn;

	public SDropDbn(String dbn) {
		this.dbn = dbn;
	}

	public String getDbn() {
		return dbn;
	}
}
