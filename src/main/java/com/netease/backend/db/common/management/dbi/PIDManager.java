package com.netease.backend.db.common.management.dbi;


public interface PIDManager {
	
	long genId() throws PIDException;
	
	void clearRemainingIds();

}