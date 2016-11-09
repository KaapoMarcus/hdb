package com.netease.backend.db.common.sql;


public class SAddMonitorClients extends Statement {
    private static final long serialVersionUID = 1L;
    
    private Clients clients;

	public SAddMonitorClients(Clients clients) {
		super();
		this.clients = clients;
	}

	public Clients getClients() {
		return clients;
	}
}
