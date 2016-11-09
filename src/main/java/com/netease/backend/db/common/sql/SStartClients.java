package com.netease.backend.db.common.sql;

public class SStartClients extends Statement {
    private static final long serialVersionUID = 3605966751837331473L;
    
    private Clients clients;

	public SStartClients(Clients clients) {
		super();
		this.clients = clients;
	}

	public Clients getClients() {
		return clients;
	}
}
