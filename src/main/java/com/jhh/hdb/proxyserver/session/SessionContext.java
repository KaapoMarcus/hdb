package com.jhh.hdb.proxyserver.session;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.jhh.hdb.proxyserver.define.ServerCapabilities;
import com.jhh.hdb.proxyserver.define.ServerStatus;
import com.jhh.hdb.proxyserver.qssql.QSIsqlCmdExecutor;

/**
 * session信息上下文，一个session对应一个客户端连接
 * 
 *
 */
public class SessionContext {
	public static final String SESSION_CONTEXT_KEY = "SESSION.CONTEXT";

	// 标示当前session所处的状态
	private SessionStatusType sessionStatus;

	// 密码产生种子，由服务器在握手时发送给客户端
	private String passwordSeed;

	// 密码产生种子（第二部分），由服务器在握手时发送给客户端
	private String restPasswordSeed;

	// 客户端字符集
	private Charset clientCharset;

	// 当前session的服务器状态
	private ServerStatus serverStatus = new ServerStatus();

	// 最大数据包大小，由客户端返回给服务器
	private int maxPacketSize;

	// 用户名
	private String username;

	// 加密后密码
	private String encryptedPassword;

	// 密码
	private String password;

	// 数据库连接
	private Connection dbConnection;

	// 用户认证时客户端返回的标志
	private int clientFlags;

	// 客户端ip
	private String clientIp;

	//预处理语句上下文信息
	private Map<Integer, StmtContext> stmtContextMap = new HashMap<Integer, StmtContext>();

	private int pstmtId = 1;
	
	//Isql命令执行器
	private QSIsqlCmdExecutor executor = null;

	public static SessionContext getSessionContext(IoSession session) {
		return (SessionContext) session.getAttribute(SESSION_CONTEXT_KEY);
	}

	public static void setSessionContext(IoSession session,
			SessionContext context) {
		session.setAttribute(SESSION_CONTEXT_KEY, context);
	}

	public SessionStatusType getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(SessionStatusType status) {
		this.sessionStatus = status;
	}

	public String getPasswordSeed() {
		return passwordSeed;
	}

	public void setPasswordSeed(String passwordSeed) {
		this.passwordSeed = passwordSeed;
	}

	public String getRestPasswordSeed() {
		return restPasswordSeed;
	}

	public void setRestPasswordSeed(String restPasswordSeed) {
		this.restPasswordSeed = restPasswordSeed;
	}

	public Charset getClientCharset() {
		return clientCharset;
	}

	public void setClientCharset(Charset clientCharset) {
		this.clientCharset = clientCharset;
	}

	public ServerStatus getServerStatus() {
		return serverStatus;
	}

	public void updateServerStatus(short serverStatus) {
		this.serverStatus.setStatus(serverStatus);
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	public Connection getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public void setEncryptedPassword(byte[] encryptedPassword)
			throws UnsupportedEncodingException {
		this.encryptedPassword = new String(encryptedPassword, "latin1");
	}

	public int getClientFlags() {
		return clientFlags;
	}

	public void setClientFlags(int clientFlags) {
		this.clientFlags = clientFlags;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getRealPasswordSeed() {
		return ServerCapabilities.isLongPassword(clientFlags) ? passwordSeed
				+ restPasswordSeed : passwordSeed;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 清除上下文信息，回收资源
	 * @throws SQLException 
	 */
	public void clear() throws SQLException {
		for (StmtContext stmtContext : stmtContextMap.values()) {
			stmtContext.closePstmt();
		}
		
		if (dbConnection != null) {
			dbConnection.close();
		}
		
		this.executor = null;
	}

	public Map<Integer, StmtContext> getStmtContextMap() {
		return stmtContextMap;
	}

	public int newPstmtId() {
		return pstmtId++;
	}
	
	public QSIsqlCmdExecutor getIsqlExecutor() {
		if (executor != null)
			return executor;
		else {
			synchronized(this) {
				if (executor != null)
					return executor;
				else {
					executor = new QSIsqlCmdExecutor(this);
					return executor;
				}
			}
		}
	}
}
