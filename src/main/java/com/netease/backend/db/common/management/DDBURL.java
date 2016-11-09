package com.netease.backend.db.common.management;

import java.io.Serializable;
import java.util.Properties;


public final class DDBURL implements Serializable, Cloneable {

	
	public static final String	DEFAULT_KEY_PATH	= "conf/secret.key";
	
	public static final String	DEFAULT_LOG_NAME	= "howl";

	private static final long	serialVersionUID	= -4032597501863717181L;

	
	private String				host;
	
	private int					port;
	
	private String				name;
	
	private String				userName;
	private String				password;
	private String				keyPath				= DEFAULT_KEY_PATH;
	private String				logName				= DEFAULT_LOG_NAME;
	private final Properties	params				= new Properties();

	
	public DDBURL(
		String host,
		int port,
		String name)
	{
		this(host, port, name, null, null);
	}

	
	public DDBURL(
		String host,
		int port,
		String name,
		String userName,
		String password)
	{
		this(host, port, name, userName, password, null, null, null);
	}

	
	public DDBURL(
		String host,
		int port,
		String name,
		String userName,
		String password,
		String keyPath,
		String logName,
		Properties params)
	{
		this.host = host;
		this.port = port;
		this.name = name;
		this.userName = userName;
		this.password = password;
		if (keyPath != null) {
			this.keyPath = keyPath;
		}
		if (logName != null) {
			this.logName = logName;
		}
		this.setParams(params);
	}

	@Override
	public DDBURL clone() {
		try {
			return (DDBURL) super.clone();
		} catch (final CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	
	public void setHost(
		String host)
	{
		this.host = host;
	}

	
	public void setPort(
		int port)
	{
		this.port = port;
	}

	
	public void setName(
		String name)
	{
		this.name = name;
	}

	
	public String getUserName() {
		return userName;
	}

	
	public void setUserName(
		String userName)
	{
		this.userName = userName;
	}

	
	public String getPassword() {
		return password;
	}

	
	public void setPassword(
		String password)
	{
		this.password = password;
	}

	
	public String getKeyPath() {
		return keyPath;
	}

	
	public void setKeyPath(
		String keyPath)
	{
		this.keyPath = keyPath;
	}

	
	public String getLogName() {
		return logName;
	}

	
	public void setLogName(
		String logName)
	{
		this.logName = logName;
	}

	
	public Properties getParams() {
		return params;
	}

	
	public void setParams(
		Properties params)
	{
		if (params != null) {
			this.params.putAll(params);
		}
	}

	
	public String getAddress() {
		return this.getAddressHostOnly() + '/' + this.getName();
	}

	
	public String getAddressHostOnly() {
		return this.getHost() + ':' + this.getPort();
	}

	
	public String getHost() {
		return host;
	}

	
	public int getPort() {
		return port;
	}

	
	public String getName() {
		return name;
	}
	
}
