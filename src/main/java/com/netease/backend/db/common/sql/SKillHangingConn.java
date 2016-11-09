package com.netease.backend.db.common.sql;


public class SKillHangingConn extends Statement {
    private static final long serialVersionUID = 1L;
    
    private int minutes;
	private Clients clients;

	public SKillHangingConn(int minutes, Clients clients) {
		this.minutes = minutes;
		this.clients = clients;
	}

	public int getMinutes() {
		return minutes;
	}

	public Clients getClients() {
		return clients;
	}
}
