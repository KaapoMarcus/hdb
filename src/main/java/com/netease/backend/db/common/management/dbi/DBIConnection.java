package com.netease.backend.db.common.management.dbi;

import java.io.IOException;

import com.netease.backend.db.common.cmd.CmdConnection;
import com.netease.backend.db.common.management.Cluster;


public final class DBIConnection extends CmdConnection {

	
	private final DBIContext	context;

	
	public DBIConnection(
		CmdConnection conn,
		DBIContext context) throws IOException
	{
		super(conn.getCommandIn(), conn.getCommandOut(), conn.getSock());
		if (context == null)
			throw new NullPointerException("AServer context should not be null");
		this.context = context;
	}

	
	public DBIContext getContext() {
		return context;
	}

	
	public String getSid() {
		return context.getSid();
	}

	
	public int getASID() {
		return context.getId();
	}

	
	public Cluster getCluster() {
		return context.getCluster();
	}

	
	public DBIConfig getConfig() {
		return context.getConfig();
	}

	
	public String getHost() {
		return context.getHost();
	}

	
	public String getServiceHost() {
		return context.getServiceHost();
	}

	
	public int getServicePort() {
		return context.getServicePort();
	}

}
