package com.netease.backend.db.common.sql;


public class SShowDbnConn extends Statement {
    private static final long serialVersionUID = 1L;
    
    private Clients clients;

	public SShowDbnConn(Clients clients) {
		this.clients = clients;
	}

	public Clients getClients() {
		return clients;
	}
}
