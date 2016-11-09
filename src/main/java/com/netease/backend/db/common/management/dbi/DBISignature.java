
package com.netease.backend.db.common.management.dbi;

import java.io.Serializable;
import java.util.Properties;

import com.netease.backend.db.common.config.PropertiesConfig;
import com.netease.backend.db.common.config.PropertiesConfigHelper;
import com.netease.backend.db.common.config.PropertiesConfigHelperBase;
import com.netease.backend.db.common.management.as.ASSignature;


public final class DBISignature implements PropertiesConfig, Serializable,
		Cloneable {
	private static final long	serialVersionUID	= -8261907045411670904L;

	
	private static final String	LOG_DIR_DEFAULT		= "log";

	
	private ASSignature			serverSignature		= new ASSignature();
	
	private String				logDir				= LOG_DIR_DEFAULT;

	
	public static String getDefaultLogDir() {
		return LOG_DIR_DEFAULT;
	}

	
	public DBISignature() {
	}

	
	public DBISignature(
		ASSignature serverSignature,
		String logDir)
	{
		this.setServerSignature(serverSignature);
		if (logDir != null) {
			this.logDir = logDir;
		}
	}

	
	public String getLogDir() {
		return logDir;
	}

	
	public void setLogDir(
		String logDir)
	{
		this.logDir = logDir;
	}

	
	public ASSignature getServerSignature() {
		return serverSignature;
	}

	
	public void setServerSignature(
		ASSignature serverSignature)
	{
		if (serverSignature == null)
			throw new NullPointerException(
					"AServer signagure must not be null!");
		this.serverSignature = serverSignature;
	}

	
	public String getHost() {
		return this.getServerSignature().getHost();
	}

	
	public String getServiceHostAddress() {
		return this.getServiceHost() + ':' + this.getServicePort();
	}

	
	public String getServiceHost() {
		return this.getServerSignature().getServiceHost();
	}

	
	public int getType() {
		return this.getServerSignature().getType();
	}

	
	public String getTypeStr() {
		return this.getServerSignature().getTypeStr();
	}

	public static String getTypeStr(
		int type)
	{
		return ASSignature.getTypeStr(type);
	}

	
	public String getVersion() {
		return this.getServerSignature().getVersion();
	}

	
	public void setHost(
		String host)
	{
		this.getServerSignature().setHost(host);
	}

	
	public int getServicePort() {
		return this.getServerSignature().getServicePort();
	}

	
	public void setServicePort(
		int servicePort)
	{
		this.getServerSignature().setServicePort(servicePort);
	}

	
	public void setServiceHost(
		String srvHost)
	{
		this.getServerSignature().setServiceHost(srvHost);
	}

	
	public void setType(
		int type)
	{
		this.getServerSignature().setType(type);
	}

	
	public void setVersion(
		String version)
	{
		this.getServerSignature().setVersion(version);
	}

	@Override
	protected DBISignature clone() {
		try {
			return (DBISignature) super.clone();
		} catch (final CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serverSignature == null) ? 0 : serverSignature.hashCode());
		result = prime * result + ((logDir == null) ? 0 : logDir.hashCode());
		return result;
	}

	@Override
	public boolean equals(
		Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final DBISignature other = (DBISignature) obj;
		if (serverSignature == null) {
			if (other.serverSignature != null)
				return false;
		} else if (!serverSignature.equals(other.serverSignature))
			return false;
		if (logDir == null) {
			if (other.logDir != null)
				return false;
		} else if (!logDir.equals(other.logDir))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final String SEP = ", ";
		final StringBuilder s = new StringBuilder();
		s.append("serverSignature=").append(
				this.getServerSignature().toString());
		s.append(SEP).append("logDir=").append("\"").append(logDir)
				.append("\"");
		s.append("]");
		return s.toString();
	}

	public String getDescription() {
		return "DBI signature";
	}

	public Properties toProperties() {
		return helper.toProperties(this);
	}

	public String toPropertiesString() {
		return helper.toPropertiesString(this);
	}

	public DBISignature update(
		Properties props)
	{
		helper.updateConfig(this, props);
		return this;
	}

	private static final DBISignatureHelper	helper;

	static {
		helper = new DBISignatureHelper();
	}

	
	public static DBISignatureHelper getHelper() {
		return helper;
	}

	public static final class DBISignatureHelper extends
			PropertiesConfigHelperBase<DBISignature> implements
			PropertiesConfigHelper<DBISignature> {
		public DBISignature fromProperties(
			String name, Properties props, String desc)
		{
			final DBISignature sig = new DBISignature();
			this.updateConfig(sig, props);
			return sig;
		}

		public Class<DBISignature> getTypeClass() {
			return DBISignature.class;
		}
	}

}
