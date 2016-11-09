package com.netease.backend.db.common.management.as;

import java.io.Serializable;
import java.util.Properties;

import com.netease.backend.db.common.config.PropertiesConfig;
import com.netease.backend.db.common.config.PropertiesConfigHelper;
import com.netease.backend.db.common.config.PropertiesConfigHelperBase;


public final class ASSignature implements PropertiesConfig, Serializable,
		Cloneable {

	
	public static final int		TYPE_CLIENT				= 0;
	
	public static final int		TYPE_SERVER				= 1;

	
	private static final long	serialVersionUID		= -7040735397346213948L;

	
	private static final int	TYPE_DEFAULT			= TYPE_CLIENT;
	
	private static final String	VERSION					= "4.3";
	
	private static final String	HOST_DEFAULT			= "127.0.0.1";
	private static final String	SERVICE_HOST_DEFAULT	= "0.0.0.0";
	
	private static final int	SERVICE_PORT_DEFAULT	= 6666;

	private int					type					= TYPE_DEFAULT;
	private String				version					= VERSION;
	private String				host					= HOST_DEFAULT;
	private String				serviceHost				= SERVICE_HOST_DEFAULT;
	private int					servicePort				= SERVICE_PORT_DEFAULT;

	
	public ASSignature() {
	}

	
	public ASSignature(
		int type,
		String version,
		String host,
		String serviceHost,
		int servicePort)
	{
		if (type >= 0) {
			this.type = type;
		}
		if (version != null) {
			this.version = version;
		}
		if (host != null) {
			this.host = host;
		}
		if (serviceHost != null) {
			this.serviceHost = serviceHost;
		}
		if (servicePort >= 0) {
			this.servicePort = servicePort;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result
				+ ((serviceHost == null) ? 0 : serviceHost.hashCode());
		result = prime * result + servicePort;
		return result;
	}

	@Override
	public String toString() {
		final String SEP = ", ";
		final StringBuilder s = new StringBuilder();
		s.append('[');
		s.append("type=").append(type);
		s.append(SEP).append("version=").append("\"").append(version).append(
				"\"");
		s.append(SEP).append("host=").append("\"").append(host).append("\"");
		s.append(SEP).append("serviceHost=").append("\"").append(serviceHost)
				.append("\"");
		s.append(SEP).append("servicePort=").append(servicePort);
		s.append("]");
		return s.toString();
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
		final ASSignature other = (ASSignature) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (type != other.type)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (serviceHost == null) {
			if (other.serviceHost != null)
				return false;
		} else if (!serviceHost.equals(other.serviceHost))
			return false;
		if (servicePort != other.servicePort)
			return false;
		return true;

	}

	@Override
	protected ASSignature clone() {
		try {
			return (ASSignature) super.clone();
		} catch (final CloneNotSupportedException ex ) {
			ex.printStackTrace();
			return null;
		}
	}

	
	public String getTypeStr() {
		return getTypeStr(this.getType());
	}

	
	public static String getTypeStr(
		int type)
	{
		switch (type) {
		case TYPE_SERVER:
			return "Server";
		case TYPE_CLIENT:
			return "Client";
		default:
			return "Unknown";
		}
	}

	
	public String getHost() {
		return host;
	}

	
	public void setHost(
		String host)
	{
		if (host == null)
			throw new NullPointerException("AServer host must not be null!");
		this.host = host;
	}

	
	public int getType() {
		return type;
	}

	
	public void setType(
		int type)
	{
		if (type < 0)
			throw new IllegalArgumentException("AServer type illegal!");
		this.type = type;
	}

	
	public String getVersion() {
		return version;
	}

	
	public void setVersion(
		String version)
	{
		if (version == null)
			throw new NullPointerException("AServer version must not be null!");
		this.version = version;
	}

	
	public String getServiceHost() {
		return serviceHost;
	}

	
	public void setServiceHost(
		String serviceHost)
	{
		if (serviceHost == null)
			throw new NullPointerException(
					"AServer service host must not be null!");
		this.serviceHost = serviceHost;
	}

	
	public int getServicePort() {
		return servicePort;
	}

	
	public void setServicePort(
		int servicePort)
	{
		if ((servicePort < 0) || (servicePort >= 65536))
			throw new IllegalArgumentException("AServer service port illegal!");
		this.servicePort = servicePort;
	}

	public String getDescription() {
		return "AServer signagure.";
	}

	public Properties toProperties() {
		return helper.toProperties(this);
	}

	public String toPropertiesString() {
		return helper.toPropertiesString(this);
	}

	public ASSignature update(
		Properties props)
	{
		helper.updateConfig(this, props);
		return this;
	}

	private static final ASSignatureHelper	helper;

	static {
		helper = new ASSignatureHelper();
	}

	
	public static ASSignatureHelper getHelper() {
		return helper;
	}

	public static final class ASSignatureHelper extends
			PropertiesConfigHelperBase<ASSignature> implements
			PropertiesConfigHelper<ASSignature> {
		public ASSignature fromProperties(
			String name, Properties props, String desc)
		{
			final ASSignature sig = new ASSignature();
			this.updateConfig(sig, props);
			return sig;
		}

		public Class<ASSignature> getTypeClass() {
			return ASSignature.class;
		}
	}

}
