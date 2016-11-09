package com.netease.backend.db.common.management.dbi.memcached;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.netease.backend.db.common.config.PropertiesConfig;
import com.netease.backend.db.common.config.PropertiesConfigHelper;
import com.netease.backend.db.common.config.PropertiesConfigHelperBase;
import com.netease.backend.db.common.utils.Pair;


public final class MemcachedConfig implements PropertiesConfig, Serializable,
		Cloneable {

	private static final long serialVersionUID = -6423912234546965970L;

	
	
	private boolean useMemcached = false;

	
	private int maxRecordsIntoCachePerQuery = 5000;

	
	private int recordCountInBatch = 100;

	
	
	private String memcachedServerAddrStr = "";

	
	private int maxOpQueueLength = 1024;

	
	private int connectionBufferSize = 16;

	
	private int compressionThreshold = 16;

	
	private int operationTimeout = 2 * 1000;

	
	private int expirationTime = 0;

	
	
	private int maxConnectionsInPool = 32;

	
	private int connGetTimeout = 60 * 1000;

	
	private int connIdleTimeout = 600 * 1000;

	
	private static final MemcachedConfigHelper helper;

	static {
		helper = new MemcachedConfigHelper();
	}

	public static MemcachedConfigHelper getHelper() {
		return helper;
	}

	public MemcachedConfig() {

	}

	public boolean isUseMemcached() {
		return useMemcached;
	}

	public void setUseMemcached(boolean useMemcached) {
		this.useMemcached = useMemcached;
	}

	public int getMaxRecordsIntoCachePerQuery() {
		return maxRecordsIntoCachePerQuery;
	}

	public void setMaxRecordsIntoCachePerQuery(int maxRecordsIntoCachePerQuery) {
		this.maxRecordsIntoCachePerQuery = maxRecordsIntoCachePerQuery;
	}

	public int getRecordCountInBatch() {
		return recordCountInBatch;
	}

	public void setRecordCountInBatch(int recordCountInBatch) {
		this.recordCountInBatch = recordCountInBatch;
	}

	public String getMemcachedServerAddrStr() {
		return memcachedServerAddrStr;
	}

	public void setMemcachedServerAddrStr(String memcachedServerAddrStr) {
		this.memcachedServerAddrStr = memcachedServerAddrStr;
	}

	public int getMaxOpQueueLength() {
		return maxOpQueueLength;
	}

	public void setMaxOpQueueLength(int maxOpQueueLength) {
		this.maxOpQueueLength = maxOpQueueLength;
	}

	public int getConnectionBufferSize() {
		return connectionBufferSize;
	}

	public void setConnectionBufferSize(int connectionBufferSize) {
		this.connectionBufferSize = connectionBufferSize;
	}

	public int getCompressionThreshold() {
		return compressionThreshold;
	}

	public void setCompressionThreshold(int compressionThreshold) {
		this.compressionThreshold = compressionThreshold;
	}

	public int getOperationTimeout() {
		return operationTimeout;
	}

	public void setOperationTimeout(int operationTimeout) {
		this.operationTimeout = operationTimeout;
	}

	public int getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(int expireTime) {
		this.expirationTime = expireTime;
	}

	public int getMaxConnectionsInPool() {
		return maxConnectionsInPool;
	}

	public void setMaxConnectionsInPool(int maxConnectionsInPool) {
		this.maxConnectionsInPool = maxConnectionsInPool;
	}

	public int getConnGetTimeout() {
		return connGetTimeout;
	}

	public void setConnGetTimeout(int connGetTimeout) {
		this.connGetTimeout = connGetTimeout;
	}

	public int getConnIdleTimeout() {
		return connIdleTimeout;
	}

	public void setConnIdleTimeout(int connIdleTimeout) {
		this.connIdleTimeout = connIdleTimeout;
	}

	public long getConnGcInterval() {
		long interval = getConnIdleTimeout() / 4 + 1;
		return interval > 10000 ? interval : interval;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("MemcachedConfig ");
		builder.append("useMemcached:").append(useMemcached);
		builder.append(", addr:").append(memcachedServerAddrStr);
		return builder.toString();
	}

	
	public static List<Pair<String, Integer>> checkServerAddrList(
			String serverAddrListStr) throws IllegalArgumentException {
		if (serverAddrListStr == null || serverAddrListStr.length() == 0)
			throw new IllegalArgumentException(
					"memcached server address can not be null.");

		String[] addrArray = serverAddrListStr.split(";");
		List<Pair<String, Integer>> addrList = new ArrayList<Pair<String, Integer>>(
				addrArray.length);
		for (String addr : addrArray) {
			int colonPos = addr.indexOf(':');
			if (colonPos < 0 || (colonPos == addr.length() - 1))
				throw new IllegalArgumentException(
						"invalid memcached server address '" + addr + "'");
			String host = addr.substring(0, colonPos).trim();
			String portStr = addr.substring(colonPos + 1).trim();
			try {
				int port = Integer.parseInt(portStr);
				addrList.add(new Pair<String, Integer>(host, port));
			} catch (NumberFormatException e1) {
				throw new IllegalArgumentException("invalid port in address '"
						+ addr + "'");
			}
		}
		return addrList;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public String getDescription() {
		return "Memcached configure";
	}

	public String toPropertiesString() {
		return helper.toPropertiesString(this);
	}

	public Properties toProperties() {
		return helper.toProperties(this);
	}

	public PropertiesConfig update(Properties props) {
		helper.updateConfig(this, props);
		return this;
	}

	public static final class MemcachedConfigHelper extends
			PropertiesConfigHelperBase<MemcachedConfig> implements
			PropertiesConfigHelper<MemcachedConfig> {
		public MemcachedConfig fromProperties(String name, Properties props,
				String desc) {
			final MemcachedConfig config = new MemcachedConfig();
			this.updateConfig(config, props);
			return config;
		}

		public Class<MemcachedConfig> getTypeClass() {
			return MemcachedConfig.class;
		}
	}
}
