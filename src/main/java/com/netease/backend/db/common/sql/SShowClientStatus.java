package com.netease.backend.db.common.sql;


public class SShowClientStatus extends Statement {
    private static final long serialVersionUID = 1L;
    
    private Clients clients;

    public SShowClientStatus(Clients clients) {
        this.clients = clients;
    }

    public Clients getClients() {
        return clients;
    }
}
