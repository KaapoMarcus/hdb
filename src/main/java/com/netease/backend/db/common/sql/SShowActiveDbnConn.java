package com.netease.backend.db.common.sql;


public class SShowActiveDbnConn extends Statement {
    private static final long serialVersionUID = 1L;
    
    private Clients clients;

	public SShowActiveDbnConn(Clients clients) {
		this.clients = clients;
	}

	public Clients getClients() {
		return clients;
	}
}
