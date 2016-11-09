package com.netease.backend.db.common.sql;


public class SResetOps extends Statement {
    private static final long serialVersionUID = 1L;
    
    private Clients clients;

	public SResetOps(Clients clients) {
		this.clients = clients;
	}

	public Clients getClients() {
		return clients;
	}
}
