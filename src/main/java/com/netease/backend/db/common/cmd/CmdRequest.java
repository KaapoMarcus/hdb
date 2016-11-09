package com.netease.backend.db.common.cmd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class CmdRequest {

	
	private final Socket	sock;

	
	public final String getRemoteHost() {
		return this.getSock().getInetAddress().getHostAddress();
	}

	
	public final int getRemotePort() {
		return this.getSock().getPort();
	}

	private InetSocketAddress	socketAddress	= null;

	
	public final InetSocketAddress getRemoteSocketAddress() {
		if (socketAddress == null) {
			synchronized (this.getSock()) {
				if (socketAddress == null) {
					socketAddress = new InetSocketAddress(this.getRemoteHost(),
							this.getRemotePort());
				}
			}
		}
		return socketAddress;
	}

	
	public CmdRequest(
		Socket sock)
	{
		if (sock == null)
			throw new NullPointerException();
		this.sock = sock;
	}

	
	public void setTimeout(
		int timeout)
		throws IOException
	{
		this.getSock().setSoTimeout(timeout);
	}

	private CmdConnection	connection	= null;

	
	public CmdConnection getConnection()
		throws IOException
	{
		final Socket sock = this.getSock();
		if ((sock == null) || !sock.isConnected() || sock.isClosed())
			throw new IOException("���������ѹرգ�");

		if (connection == null) {
			synchronized (sock) {
				if (connection == null) {
					connection = createConnection(sock);
				}
			}
		}
		return connection;
	}

	
	protected Socket getSock() {
		return sock;
	}

	
	
	

	private static final CmdConnection createConnection(
		final Socket sock)
		throws IOException
	{

		return new CmdConnection(sock);
	}

}
