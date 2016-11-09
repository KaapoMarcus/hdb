package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.netease.backend.db.common.exceptions.CryptException;
import com.netease.backend.db.common.utils.CryptUtils;


public class User implements Serializable, Cloneable {

	private static final long				serialVersionUID		= 2989118062918659471L;

	
	public final static int					USER_TYPE_DBA			= 1;

	
	public final static int					USER_TYPE_MAN			= 2;

	
	public final static int					USER_TYPE_AGENT			= 3;

	
	private String							name;

	
	private String							password;
	
	
	byte[]									userBytes				= null;
	
	byte[]									passBytes				= null;

	
	private int								type;

	
	
	private Set<EntityPrivilege>	entityPrivileges		= new HashSet<EntityPrivilege>();
	
	private Map<String, EntityPrivilege>	entityPrivilegeIndex	= new HashMap<String, EntityPrivilege>();

	
	
	private Set<String>			clientIpSet				= null;
	
	private Set<String>			qsIpSet					= null;
	
	private Set<String>			adminIpSet				= null;

	
	private long							quota					= 10000;

	
	private long							slaveQuota				= 100000;

	
	private Date							expireTime				= new Date(
																			0);

	
	private String							desc;

	
	private DBAPrivilege					dbaRole;

	
	
	public User(
		String user,
		String pass,
		int type,
		Set<String> clientIps,
		Set<String> qsIps,
		Set<String> adminIps,
		String desc,
		DBAPrivilege dbaRole)
	{
		this.name = user;
		this.password = pass;
		this.type = type;
		this.clientIpSet = clientIps;
		if (this.clientIpSet == null) {
			this.clientIpSet = new TreeSet<String>();
		}
		this.qsIpSet = qsIps;
		if (this.qsIpSet == null) {
			this.qsIpSet = new TreeSet<String>();
		}
		this.adminIpSet = adminIps;
		if (this.adminIpSet == null) {
			this.adminIpSet = new TreeSet<String>();
		}
		this.desc = desc;
		this.dbaRole = dbaRole;
	}

	
	
	public Set<EntityPrivilege> getEntityPrivileges() {
		return this.entityPrivileges;
	}

	
	public EntityPrivilege getEntityPrivilege(
		String qualifiedName)
	{
		return this.getEntityPrivilegeIndex().get(qualifiedName);
	}

	
	protected Map<String, EntityPrivilege> getEntityPrivilegeIndex() {
		return this.entityPrivilegeIndex;
	}

	
	public void setEntityPrivileges(
		Set<EntityPrivilege> entityPrivileges)
	{
		synchronized (this.entityPrivileges) {
			this.entityPrivileges.clear();
			this.entityPrivileges.addAll(entityPrivileges);
			
			this.entityPrivilegeIndex.clear();
			for (EntityPrivilege entityPrivilege : entityPrivileges) {
				this.entityPrivilegeIndex.put(entityPrivilege
						.getQualifiedName(), entityPrivilege);
			}
		}
	}

	
	public void updateEntityPrivilege(
		EntityPrivilege privilege)
	{
		synchronized (this.entityPrivileges) {
			
			if (privilege.hasNonePrivileges()) {
				this.entityPrivileges.remove(this.entityPrivilegeIndex
						.remove(privilege.getQualifiedName()));
				return;
			} else {
				EntityPrivilege oldPrivilege = this.entityPrivilegeIndex.put(
						privilege.getQualifiedName(), privilege);
				if (null != oldPrivilege) {
					this.entityPrivileges.remove(oldPrivilege);
				}
				this.entityPrivileges.add(privilege);
				return;
			}
		}
	}

	
	
	public void encrypt()
		throws CryptException
	{
		this.userBytes = CryptUtils.encrypt(this.name.getBytes());
		this.passBytes = CryptUtils.encrypt(this.password.getBytes());
		this.name = "";
		this.password = "";
	}

	
	public void encrypt(
		Key key)
		throws CryptException
	{
		this.userBytes = CryptUtils.encrypt(this.name.getBytes(), key);
		this.passBytes = CryptUtils.encrypt(this.password.getBytes(), key);
		this.name = "";
		this.password = "";
	}

	
	public void decrypt()
		throws CryptException
	{
		this.name = new String(CryptUtils.decrypt(this.userBytes));
		this.password = new String(CryptUtils.decrypt(this.passBytes));
	}

	
	public void decrypt(
		Key key)
		throws CryptException
	{
		this.name = new String(CryptUtils.decrypt(this.userBytes, key));
		this.password = new String(CryptUtils.decrypt(this.passBytes, key));
	}

	
	public static String getTypeDesc(
		int type)
	{
		switch (type) {
		case USER_TYPE_DBA:
			return "DBA";
		case USER_TYPE_MAN:
			return "MAN";
		case USER_TYPE_AGENT:
			return "AGENT";
		default:
			return "UNKNOWN";
		}
	}

	public String getTypeDesc() {
		return getTypeDesc(this.getType());
	}

	
	
	@Override
	public boolean equals(
		Object otherObject)
	{
		if (this == otherObject) {
			return true;
		}
		if (otherObject == null) {
			return false;
		}
		if (this.getClass() != otherObject.getClass()) {
			return false;
		}
		final User other = (User) otherObject;
		return (this.name.equals(other.name));
	}

	
	@Override
	public String toString() {
		return this.name + ":" + getTypeDesc(this.type) + ":" + this.desc;
	}

	
	@Override
	public Object clone() {
		try {
			final User cloned = (User) super.clone();
			return cloned;
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	
	public String getDesc() {
		return this.desc;
	}

	public void setDesc(
		String desc)
	{
		this.desc = desc;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(
		String password)
	{
		this.password = password;
	}

	public String getName() {
		return this.name;
	}

	public void setName(
		String name)
	{
		this.name = name;
	}

	public Set<String> getClientIps() {
		return this.clientIpSet;
	}

	public void setClientIps(
		Set<String> clientIps)
	{
		this.clientIpSet = clientIps;
	}

	public Set<String> getQsIps() {
		return this.qsIpSet;
	}

	public void setQstIps(
		Set<String> qsIps)
	{
		this.qsIpSet = qsIps;
	}

	public Set<String> getAdminIps() {
		return this.adminIpSet;
	}

	public void setAdminIps(
		Set<String> adminIps)
	{
		this.adminIpSet = adminIps;
	}

	public int getType() {
		return this.type;
	}

	public void setType(
		int type)
	{
		
		if (this.type != User.USER_TYPE_DBA && type == User.USER_TYPE_DBA) {
			this.dbaRole = new DBAPrivilege();
		}
		this.type = type;
	}

	public Date getExpireTime() {
		return this.expireTime;
	}

	public void setExpireTime(
		Date expireTime)
	{
		this.expireTime = expireTime;
	}

	public long getQuota() {
		return this.quota;
	}

	public void setQuota(
		long quota)
	{
		this.quota = quota;
	}

	public long getSlaveQuota() {
		return this.slaveQuota;
	}

	public void setSlaveQuota(
		long quota)
	{
		this.slaveQuota = quota;
	}

	public String getMysqlPassword() {
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return this.getPassword();
	}

	
	public DBAPrivilege getDBARole() {
		return this.dbaRole;
	}

	
	public void setDBARole(
		DBAPrivilege dbaRole)
	{
		this.dbaRole = dbaRole;
	}

	
	
	public static void main(
		String[] args)
	{
		
	}

}
