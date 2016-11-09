package com.netease.backend.db.common.sql;


public class SDisableDbn extends Statement {
    private static final long serialVersionUID = 5331048076031057097L;
    
    private String dbn;

	public SDisableDbn(String dbn) {
		this.dbn = dbn;
	}

	public String getDbn() {
		return dbn;
	}
}
