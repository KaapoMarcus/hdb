package com.netease.backend.db.common.sql;

public class SStopClients extends Statement {
    private static final long serialVersionUID = -3779084979829566190L;
    
    private Clients clients;

	public SStopClients(Clients clients) {
		super();
		this.clients = clients;
	}

	public Clients getClients() {
		return clients;
	}
}
