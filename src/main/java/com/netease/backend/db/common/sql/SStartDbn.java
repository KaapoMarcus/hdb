package com.netease.backend.db.common.sql;


public class SStartDbn extends Statement {
    private static final long serialVersionUID = 3665937954625028972L;
    
    private String dbn;

	public SStartDbn(String dbn) {
		this.dbn = dbn;
	}

	public String getDbn() {
		return dbn;
	}
}
