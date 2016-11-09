package com.netease.backend.db.common.management.dbi;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.netease.backend.db.common.cmd.CmdRequest;


public class DBIRequest {

	
	private final CmdRequest	cmdRequest;

	
	private DBIContext			context		= null;
	
	volatile private boolean	validated	= false;

	
	public DBIRequest(
		CmdRequest cmdRequest)
	{
		if (cmdRequest == null)
			throw new NullPointerException(
					"Command request must not be null to create an new DBI request");
		this.cmdRequest = cmdRequest;
	}

	
	protected CmdRequest getCmdRequest() {
		return cmdRequest;
	}

	
	public final String getRemoteHost() {
		return this.getCmdRequest().getRemoteHost();
	}

	
	public int getRemotePort() {
		return this.getCmdRequest().getRemotePort();
	}

	
	public final InetSocketAddress getRemoteSocketAddress() {
		return this.getCmdRequest().getRemoteSocketAddress();
	}

	
	public void setTimeout(
		int timeout)
		throws IOException
	{
		this.getCmdRequest().setTimeout(timeout);
	}

	
	public boolean isValid() {
		return validated;
	}

	
	private static void checkIfValid(
		DBIRequest request)
	{
		if (!request.isValid())
			throw new IllegalStateException("DBI request["
					+ request.getRemoteSocketAddress()
					+ "] NOT already validated!");
	}

	
	private DBIConnection	connection	= null;

	
	public DBIConnection getConnection()
		throws IOException
	{
		
		final DBIContext context = this.getContext();
		if (connection == null) {
			synchronized (context) {
				if (connection == null) {
					connection = new DBIConnection(this.getCmdRequest()
							.getConnection(), this.getContext());
				}
			}
		}
		return connection;
	}

	
	public DBIContext getContext() {
		checkIfValid(this);
		return context;
	}

	
	public void setContext(
		DBIContext context)
	{
		if (context == null)
			throw new NullPointerException("DBI Context should not be null");

		
		validated = false;
		if (!validated) {
			synchronized (this) {
				
				if (validated)
					return;
				
				
				boolean acted = false;
				try {
					this.context = context;
					
					
					acted = true;
				} finally {
					if (acted) {
						
						validated = true;
					}
				}
			}
		}
	}

}
