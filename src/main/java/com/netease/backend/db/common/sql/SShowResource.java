package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.enumeration.ResourceStatusType;
import com.netease.backend.db.common.enumeration.ResourceType;


public class SShowResource extends Statement {
    private static final long serialVersionUID = 1L;
    
    private ResourceType type;
	private Clients clients;
	private ResourceStatusType statusType;
	
	public SShowResource(ResourceType type, Clients clients, ResourceStatusType statusType) {
		this.type = type;
		this.clients = clients;
		this.statusType = statusType;
	}
	
	public Clients getClients() {
		return clients;
	}
	
	public ResourceType getType() {
		return type;
	}

	public ResourceStatusType getStatusType() {
		return statusType;
	}
}
