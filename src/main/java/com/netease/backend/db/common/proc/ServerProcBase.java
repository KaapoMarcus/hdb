package com.netease.backend.db.common.proc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;

import org.apache.log4j.Logger;


public abstract class ServerProcBase extends ProcBase {

	private ServerSocket		serverSock	= null;
	private ServiceTaskFactory	taskFactory	= null;
	protected List<ServiceTask> 	runningTaskList = null;
	
	
	public static final int DEFAULT_SO_TIMEOUT = 5000;

	public ServerProcBase() {
		super();
	}

	public ServerProcBase(String name, boolean deamon) {
		super(name, deamon);
	}

	
	protected final ServerSocket getServerSock() {
		return serverSock;
	}

	
	protected final void setServerSock(
		final ServerSocket serverSock)
	{
		final ServerSocket origServerSock;
		if (((origServerSock = this.getServerSock()) != serverSock)
				&& (origServerSock != null)) {
			
			closeServerSock(origServerSock);
		}
		synchronized (this) {
			this.serverSock = serverSock;
			if (serverSock != null) {
				
				this.setName(this.getName() + "["
						+ serverSock.getLocalSocketAddress() + "]");
			}
		}
	}

	
	protected final ServiceTaskFactory getTaskFactory() {
		return taskFactory;
	}

	
	protected final void setTaskFactory(
		final ServiceTaskFactory taskFactory)
	{
		this.taskFactory = taskFactory;
	}

	
	protected static final ServerSocket createServerSock(
		SocketAddress address)
		throws IOException
	{
		final ServerSocket serverSock = new ServerSocket();
		serverSock.bind(address);
		return serverSock;
	}

	@Override
	protected boolean runProc() {
		final ServerSocket s;
		final ServiceTaskFactory f;
		if (((s = this.getServerSock()) != null)
				&& ((f = this.getTaskFactory()) != null))
			return this.serviceProc(s, f);
		return false; 
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	protected final boolean serviceProc(
		ServerSocket serverSock, ServiceTaskFactory taskFactory)
	{
		
		final Logger logger = this.getLogger();
		
		final String name = this.getName();

		if (logger.isDebugEnabled()) {
			logger.debug(name + "�ȴ����󡭡�");
		}
		boolean acted = false;
		ACT: try {
			if (!serverSock.isBound() || serverSock.isClosed()) {
				logger.info(name + "δ��������ֹͣ�����ڣ�" + serverSock);
				break ACT;
			}
			
			final Socket sock = serverSock.accept();
			try {
				sock.setSoTimeout(this.getDefaultSoTimeout());
			} catch (SocketException se) {
				logger.error(name + " set SO_TIMEOUT for [" 
						+ sock.getRemoteSocketAddress() + "] failed", se);
				return true;
			}
			
			final ServiceTask task = createServiceTask(sock, taskFactory);
			if (task == null) {
				
				if (logger.isDebugEnabled()) {
					logger.debug(name + "�Ѻ�������"
							+ sock.getRemoteSocketAddress());
				}
			} else {
				
				if (logger.isDebugEnabled()) {
					logger.debug(name + "�ѽ�������" + task.getName());
				}
				this.addTask(task);
				this.scheduleTask(task);
				if (logger.isDebugEnabled()) {
					logger.debug(name + "�Ѽƻ���������" + task.getName());
				}
			}
			acted = true;
		} catch (final IOException ex) {
			if(!serverSock.isClosed()) 
				logger.error(name + "����I/O����" + ex.getMessage());
		} finally {
			if (!acted) {
				if (!serverSock.isClosed()) {
					try {
						serverSock.close();
					} catch (final IOException ex) {
					}
				}
			}
		}
		return acted;
	}
	
	
	protected void addTask(ServiceTask task) {
		
	}
	
	
	public void removeTask(ServiceTask task) {
		
	}
	
	
	protected void closeAllTasks() {
		
	}

	
	protected static final ServiceTask createServiceTask(
		Socket sock, ServiceTaskFactory taskFactory)
		throws IOException
	{
		return taskFactory.createTask(sock);
	}

	@Override
	protected void shutdownAction() {
		final ServerSocket serverSock;
		this.closeAllTasks();
		if ((serverSock = this.getServerSock()) != null) {
			closeServerSock(serverSock);
		}
	}

	private static final void closeServerSock(
		ServerSocket serverSock)
	{
		try {
			serverSock.close();
		} catch (final IOException ignored) {
		}
	}

	protected static interface ServiceTaskFactory {
		
		ServiceTask createTask(Socket reqSock);
	}

	public void setRestartFlag(boolean flag) {
	}
	
	
	protected int getDefaultSoTimeout() {
		return DEFAULT_SO_TIMEOUT;
	}
}
