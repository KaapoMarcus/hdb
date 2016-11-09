package com.jhh.hdb.proxyserver.define;

/**
 * mysql服务器状态类，包括状态值宏定义(各值可并存)和状态转换函数；
 * <p>具体的状态转变规则，主要是通过Connector/J调试获得</p>
 * 
 * 
 *
 */
public class ServerStatus {
	
	/** 服务器状态：处于事务中*/
	public static final short SERVER_STATUS_IN_TRANS = 1;
	/** 服务器状态：处于自动提交*/
	public static final short SERVER_STATUS_AUTOCOMMIT = 2;
	/** 服务器状态：游标已经开启*/
	public static final short SERVER_STATUS_CURSOR_EXISTS = 64;
	
	/*以下状态值暂不使用*/
	public static final short SERVER_STATUS_LAST_ROW_SENT = 128;
	public static final short SERVER_STATUS_DB_DROPPED = 256;
	public static final short SERVER_STATUS_NO_BACKSLASH_ESCAPES = 512;
	public static final short SERVER_STATUS_METADATA_CHANGED = 1024;
	public static final short SERVER_QUERY_WAS_SLOW = 2048;
	public static final short SERVER_PS_OUT_PARAMS = 4096;
	
	
	private short serverStatus;
	
	public ServerStatus() {
		//初始状态是什么都没有
		this.serverStatus = 0;
	}

	/**
	 * 设置服务器处于自动提交状态
	 */
	public void setAutoCommitTrue() {
		serverStatus = (short) (serverStatus | SERVER_STATUS_AUTOCOMMIT);
	}
	
	/**
	 * 设置服务器处于非自动提交状态
	 */
	public void setAutoCommitFalse() {
		serverStatus = (short) (serverStatus & (~(SERVER_STATUS_AUTOCOMMIT & 0xffff)));
	}
	
	/**
	 * 服务器执行查询或修改命令，如果服务器处于非自动提交状态，则设置服务器处于事务中状态，否则状态不变
	 */
	public void execute() {
		if ((serverStatus & SERVER_STATUS_AUTOCOMMIT) == 0)
			begin();
	}
	
	/**
	 * 服务器开启事务，设置服务器处于事务中状态
	 */
	public void begin() {
		serverStatus = (short) (serverStatus | SERVER_STATUS_IN_TRANS);
	}
	
	/**
	 * 事务操作结束，清除服务器的事务中状态值
	 */
	public void commitOrRollback() {
		serverStatus = (short) (serverStatus & (~(SERVER_STATUS_IN_TRANS & 0xffff)));
	}
	
	public short getStatus() {
		return this.serverStatus;
	}
	
	public void setStatus(short status) {
		serverStatus = status;
	}
}
