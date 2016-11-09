package com.netease.backend.db.common.management.dbi;

import java.io.Serializable;
import java.util.Properties;

import com.netease.backend.db.common.config.PropertiesConfig;
import com.netease.backend.db.common.config.PropertiesConfigHelper;
import com.netease.backend.db.common.config.PropertiesConfigHelperBase;
import com.netease.backend.db.common.management.PIDConfig;
import com.netease.backend.db.common.management.as.ASSignature;
import com.netease.backend.db.common.management.dbi.memcached.MemcachedConfig;


public final class DBIConfig implements PropertiesConfig, Serializable,
		Cloneable {
	
	private static final long	serialVersionUID		= 6177028250405955402L;

	
	private static final String	DEFAULT_LOG_FILE_EXT	= "log";

	
	public static final int		INVALID_ID				= -1;

	
	public static final boolean isIdValid(
		int id)
	{
		return id > INVALID_ID;
	}

	
	private final String	name;
	
	private DBISignature	signature				= new DBISignature();
	
	private PIDConfig		tablePIDConfig			= new PIDConfig();

	
	
	private int				id						= -1;
	
	private boolean			debug					= false;
	
	private boolean			useDaemon				= true;
	
	private long			connTimeout				= 60 * 1000 ;
	
	private int				socketTimeout			= 10000;
	
	private long			connIdleTimeout			= 600000;
	
	private int				maxConnsPerPool			= 50;
	
	private int				maxConnsPerXAPool		= 50;
	
	private int				maxPSTPerConn			= 50;
	
	private int				asyncTimeout			= 120;
	
	private int				asyncInterval			= 2;
	
	private int				asyncXidListMaxSize		= 100;
	
	private int				asyncThreadInterval		= 5000;
	
	private int				execTimeout				= 120;
	
	private int				prepareParamLimit		= 10;
	
	private int				maxConn					= 1024;
	
	private int				cacheSqlSizeLimit		= 256;
	
	private int				parseTreeCacheSize		= 1024;
	
	private int				maxPendingConnections	= 20;
	
	private int				nodeConnTimeout			= 5000;
	
	private int				maxConditions			= 1024;
	
	private long			maxOffset				= 0;

	
	
	private String			logFileExt				= DEFAULT_LOG_FILE_EXT;
	
	private int				logFilesNumLimit		= 16;
	
	private int				logBufferSize			= 4;
	
	private int				logFileBlocks			= 200;
	
	private boolean			logChecksumEnabled		= false;
	
	private boolean			logChecksumAdler32		= false;
	
	private boolean			logFlushImmediate		= false;
	
	private int				logFlushInterval		= 50;
	
	private boolean			logSql					= false;
	
	private boolean			logReadOnly				= true;
	
	private boolean			logAutoCommit			= true;
	
	
	private MemcachedConfig memcachedConfig = new MemcachedConfig();

	
	public DBIConfig(
		String name)
	{
		this(name, new DBISignature());
	}

	
	public DBIConfig(
		String name,
		DBISignature signature)
	{
		this(name, INVALID_ID, signature);
	}

	
	public DBIConfig(
		String name,
		int id,
		DBISignature signature)
	{
		if (name == null)
			throw new NullPointerException(
					"DBI name(DDB name) must not be null!");
		this.name = name;
		this.setId(id);
		this.setSignature(signature);
	}

	@Override
	public String toString() {
		final String SEP = ", ";
		final StringBuilder s = new StringBuilder();
		s.append('[');
		s.append("name=").append(name);
		s.append(SEP).append("id=").append(id);
		s.append(SEP).append("signature=").append(signature);
		s.append("]");
		return s.toString();
	}

	@Override
	public DBIConfig clone() {
		try {
			return (DBIConfig) super.clone();
		} catch (final CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null; 
		}
	}

	
	public String getName() {
		return name;
	}

	
	public DBISignature getSignature() {
		return this.signature;
	}

	
	public void setSignature(
		DBISignature signature)
	{
		if (signature == null)
			throw new NullPointerException("DBI signagure must not be null!");
		this.signature = signature;
	}

	
	public String getHost() {
		return signature.getHost();
	}

	
	public String getLogDir() {
		return signature.getLogDir();
	}

	
	public ASSignature getServerSignature() {
		return signature.getServerSignature();
	}

	
	public String getServiceHostAddress() {
		return signature.getServiceHostAddress();
	}

	
	public String getServiceHost() {
		return signature.getServiceHost();
	}

	
	public int getServicePort() {
		return signature.getServicePort();
	}

	
	public int getType() {
		return signature.getType();
	}

	
	public String getVersion() {
		return signature.getVersion();
	}

	
	public void setHost(
		String host)
	{
		signature.setHost(host);
	}

	
	public void setLogDir(
		String logDir)
	{
		signature.setLogDir(logDir);
	}

	
	public void setServiceHost(
		String srvHost)
	{
		signature.setServiceHost(srvHost);
	}

	
	public void setServicePort(
		int servicePort)
	{
		signature.setServicePort(servicePort);
	}

	
	public void setType(
		int type)
	{
		signature.setType(type);
	}

	
	public void setVersion(
		String version)
	{
		signature.setVersion(version);
	}

	
	public String getTypeStr() {
		return DBISignature.getTypeStr(this.getType());
	}

	
	public PIDConfig getTablePIDConfig() {
		return tablePIDConfig;
	}

	
	public void setTablePIDConfig(
		PIDConfig tablePIDConfig)
	{
		if (tablePIDConfig == null)
			throw new NullPointerException(
					"PID configurations must not be null!");
		this.tablePIDConfig = tablePIDConfig;
	}

	
	public String getLogFileExt() {
		return logFileExt;
	}

	
	public void setLogFileExt(
		String logFileExt)
	{
		this.logFileExt = logFileExt;
	}

	
	public int getLogFilesNumLimit() {
		return logFilesNumLimit;
	}

	
	public void setLogFilesNumLimit(
		int logFilesNumLimit)
	{
		this.logFilesNumLimit = logFilesNumLimit;
	}

	
	public int getLogFileBlocks() {
		return logFileBlocks;
	}

	
	public void setLogFileBlocks(
		int logFileBlocks)
	{
		this.logFileBlocks = logFileBlocks;
	}

	
	public boolean isLogChecksumEnabled() {
		return logChecksumEnabled;
	}

	
	public void setLogChecksumEnabled(
		boolean logChecksumEnabled)
	{
		this.logChecksumEnabled = logChecksumEnabled;
	}

	
	public boolean isLogChecksumAdler32() {
		return logChecksumAdler32;
	}

	
	public void setLogChecksumAdler32(
		boolean logChecksumAdler32)
	{
		this.logChecksumAdler32 = logChecksumAdler32;
	}

	
	public int getLogBufferSize() {
		return logBufferSize;
	}

	
	public void setLogBufferSize(
		int bufferSize)
	{
		if ((bufferSize == 1) || (bufferSize == 2) || (bufferSize == 4)) {
			this.logBufferSize = bufferSize;
		}
	}

	
	public int getLogFlushInterval() {
		return logFlushInterval;
	}

	
	public void setLogFlushInterval(
		int bufferFlushInterval)
	{
		if (bufferFlushInterval < 10) {
			this.logFlushInterval = 10;
		} else if (bufferFlushInterval > 50) {
			this.logFlushInterval = 50;
		} else {
			this.logFlushInterval = bufferFlushInterval;
		}
	}

	
	public boolean isLogFlushImmediate() {
		return logFlushImmediate;
	}

	
	public void setLogFlushImmediate(
		boolean immediatelyForce)
	{
		this.logFlushImmediate = immediatelyForce;
	}

	
	public boolean isDebug() {
		return debug;
	}

	
	public void setDebug(
		boolean debug)
	{
		this.debug = debug;
	}

	
	public int getExecTimeout() {
		return execTimeout;
	}

	
	public void setExecTimeout(
		int execTimeout)
	{
		if (execTimeout < 5) {
			this.execTimeout = 5;
		} else if (execTimeout > 28800) {
			this.execTimeout = 28800;
		} else {
			this.execTimeout = execTimeout;
		}
	}

	
	public int getSocketTimeout() {
		return socketTimeout;
	}

	
	public void setSocketTimeout(
		int socketTimeout)
	{
		if (socketTimeout < 1000) {
			this.socketTimeout = 1000;
		} else if (socketTimeout > 600000) {
			this.socketTimeout = 600000;
		} else {
			this.socketTimeout = socketTimeout;
		}
	}

	
	public int getPrepareParamLimit() {
		return prepareParamLimit;
	}

	
	public void setPrepareParamLimit(
		int prepareParamLimit)
	{
		if (prepareParamLimit > 100) {
			this.prepareParamLimit = 100;
		} else if (prepareParamLimit < 3) {
			this.prepareParamLimit = 3;
		} else {
			this.prepareParamLimit = prepareParamLimit;
		}
	}

	
	public int getAsyncThreadInterval() {
		return asyncThreadInterval;
	}

	
	public void setAsyncThreadInterval(
		int asyncThreadInterval)
	{
		if (asyncThreadInterval < 5000) {
			this.asyncThreadInterval = 5000;
		} else if (asyncThreadInterval > 15000) {
			this.asyncThreadInterval = 15000;
		} else {
			this.asyncThreadInterval = asyncThreadInterval;
		}
	}

	
	public long getConnTimeout() {
		return connTimeout;
	}

	
	public void setConnTimeout(
		long timeout)
	{
		if (timeout < 5000) {
			this.connTimeout = 5000;
		} else if (timeout > 60000) {
			this.connTimeout = 60000;
		} else {
			this.connTimeout = timeout;
		}
	}

	
	public long getConnGcInterval() {
		return this.getConnIdleTimeout() / 4 + 1;
	}

	public int getNodeConnTimeout() {
		return nodeConnTimeout;
	}

	public void setNodeConnTimeout(
		int nodeConnTimeout)
	{
		this.nodeConnTimeout = nodeConnTimeout;
	}

	
	public boolean isUseDaemon() {
		return useDaemon;
	}

	
	public void setUseDaemon(
		boolean useDaemon)
	{
		this.useDaemon = useDaemon;
	}

	
	public int getAsyncXidListMaxSize() {
		return asyncXidListMaxSize;
	}

	
	public void setAsyncXidListMaxSize(
		int asyncXidListMaxSize)
	{
		if (asyncXidListMaxSize < 100) {
			this.asyncXidListMaxSize = 100;
		} else if (asyncXidListMaxSize > 200) {
			this.asyncXidListMaxSize = 200;
		} else {
			this.asyncXidListMaxSize = asyncXidListMaxSize;
		}
	}

	
	public long getConnIdleTimeout() {
		return connIdleTimeout;
	}

	
	public void setConnIdleTimeout(
		long connIdleTimeout)
	{
		if (connIdleTimeout < 5000) {
			this.connIdleTimeout = 5000;
		} else if (connIdleTimeout > 600000) {
			this.connIdleTimeout = 600000;
		} else {
			this.connIdleTimeout = connIdleTimeout;
		}
	}

	
	public int getAsyncInterval() {
		return asyncInterval;
	}

	
	public void setAsyncInterval(
		int asyncInterval)
	{
		if (asyncInterval < 2) {
			this.asyncInterval = 2;
		} else if (asyncInterval > 10) {
			this.asyncInterval = 10;
		} else {
			this.asyncInterval = asyncInterval;
		}
	}

	
	public int getAsyncTimeout() {
		return asyncTimeout;
	}

	
	public void setAsyncTimeout(
		int asyncTimeout)
	{
		if (asyncTimeout < 60) {
			this.asyncTimeout = 60;
		} else if (asyncTimeout > 180) {
			this.asyncTimeout = 180;
		} else {
			this.asyncTimeout = asyncTimeout;
		}
	}

	
	public int getId() {
		return id;
	}

	
	public void setId(
		int id)
	{
		this.id = id;
	}

	
	public boolean isLogSql() {
		return logSql;
	}

	public void setLogSql(
		boolean logSql)
	{
		this.logSql = logSql;
	}

	
	public boolean isLogAutoCommit() {
		return logAutoCommit;
	}

	public void setLogAutoCommit(
		boolean logAutoCommit)
	{
		this.logAutoCommit = logAutoCommit;
	}

	
	public boolean isLogReadOnly() {
		return logReadOnly;
	}

	public void setLogReadOnly(
		boolean logReadOnly)
	{
		this.logReadOnly = logReadOnly;
	}

	
	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(
		int maxConn)
	{
		this.maxConn = maxConn;
	}

	
	public int getMaxPSTPerConn() {
		return maxPSTPerConn;
	}

	public void setMaxPSTPerConn(
		int maxPSTPerConn)
	{
		this.maxPSTPerConn = maxPSTPerConn;
	}

	public int getMaxConnsPerPool() {
		return maxConnsPerPool;
	}

	public void setMaxConnsPerPool(
		int maxConnsPerPool)
	{
		this.maxConnsPerPool = maxConnsPerPool;
	}

	public int getMaxConnsPerXAPool() {
		return maxConnsPerXAPool;
	}

	public void setMaxConnsPerXAPool(
		int maxConnsPerXAPool)
	{
		this.maxConnsPerXAPool = maxConnsPerXAPool;
	}

	public int getCacheSqlSizeLimit() {
		return cacheSqlSizeLimit;
	}

	public void setCacheSqlSizeLimit(
		int cacheSqlSizeLimit)
	{
		this.cacheSqlSizeLimit = cacheSqlSizeLimit;
	}

	public int getParseTreeCacheSize() {
		return parseTreeCacheSize;
	}

	public void setParseTreeCacheSize(
		int parseTreeCacheSize)
	{
		this.parseTreeCacheSize = parseTreeCacheSize;
	}

	public int getMaxPendingConnections() {
		return maxPendingConnections;
	}

	public void setMaxPendingConnections(
		int maxPendingConnections)
	{
		this.maxPendingConnections = maxPendingConnections;
	}

	public int getMaxConditions() {
		return maxConditions;
	}

	public void setMaxConditions(
		int maxConditions)
	{
		this.maxConditions = maxConditions;
	}

	public long getMaxOffset() {
		return maxOffset;
	}

	public void setMaxOffset(
		long maxOffset)
	{
		this.maxOffset = maxOffset;
	}

	private static final String	description;

	static {
		description = "#\n" + "# !! DO NOT TOUCH ME !!\n"
				+ "# DBI configurations.\n" + "#\n\n";
	}

	public String getDescription() {
		return description;
	}

	public String toPropertiesString() {
		return helper.toPropertiesString(this);
	}

	public Properties toProperties() {
		return helper.toProperties(this);
	}

	public DBIConfig update(
		Properties props)
	{
		helper.updateConfig(this, props);
		return this;
	}

	public void update(
		DBIConfig from)
	{
		helper.updateConfig(this, from);
	}

	private static final DBIConfigHelper	helper;

	static {
		helper = new DBIConfigHelper();
	}

	
	public static DBIConfigHelper getHelper() {
		return helper;
	}

	public static final class DBIConfigHelper extends
			PropertiesConfigHelperBase<DBIConfig> implements
			PropertiesConfigHelper<DBIConfig> {

		public Class<DBIConfig> getTypeClass() {
			return DBIConfig.class;
		}

		public DBIConfig fromProperties(
			String name, Properties props, String desc)
		{
			final DBIConfig config = new DBIConfig(name);
			this.updateConfig(config, props);
			return config;
		}
	}

	public MemcachedConfig getMemcachedConfig() {
		return memcachedConfig;
	}

	public void setMemcachedConfig(MemcachedConfig memcachedConfig) {
		if (memcachedConfig == null)
			throw new NullPointerException("memcachedConfig must not null.");
		this.memcachedConfig = memcachedConfig;
	}
}
