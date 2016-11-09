package com.netease.backend.db.common.sql;


public class SShowOps extends Statement {
    private static final long serialVersionUID = 1L;
    
    private Clients clients;

	public SShowOps(Clients clients) {
		this.clients = clients;
	}

	public Clients getClients() {
		return clients;
	}
}
