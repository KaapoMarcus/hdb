package com.netease.backend.db.common.schema;

import java.io.Serializable;

import com.netease.backend.db.common.schema.dbengine.MysqlInfo;


public class LoadInfo implements Serializable {

	private static final long serialVersionUID = 7273538838293263238L;

	
	private SysStatusInfo sysInfo = null;
	
	
	private MysqlInfo mysqlInfo = null;
	
	
	public LoadInfo()
	{
	}

	public MysqlInfo getMysqlInfo() {
		return mysqlInfo;
	}

	public void setMysqlInfo(MysqlInfo mysqlInfo) {
		this.mysqlInfo = mysqlInfo;
	}

	public SysStatusInfo getSysInfo() {
		return sysInfo;
	}

	public void setSysInfo(SysStatusInfo sysInfo) {
		this.sysInfo = sysInfo;
	}
	
	

}
