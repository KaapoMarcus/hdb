package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class DBUserChangeQuota extends DBUserOp implements Serializable {
	private static final long serialVersionUID = -6498843294874548706L;
	
	private String userName;
	private long quota;
	private long slaveQuota;
	private boolean ignoreConnErr;
	private boolean ignoreClientErr;
	
	public DBUserChangeQuota(String username, long quota,
			long slaveQuota, boolean ignoreConnErr, boolean ignoreClientErr) {
		super("DBUserChangeQuota");
		this.userName = username;
		this.quota = quota;
		this.slaveQuota = slaveQuota;
		this.ignoreConnErr = ignoreConnErr;
		this.ignoreClientErr = ignoreClientErr;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public long getQuota() {
		return this.quota;
	}
	
	public long getSlaveQuota() {
		return this.slaveQuota;
	}
	
	public boolean isIgnoreConnErr() {
		return this.ignoreConnErr;
	}
	
	public boolean isIgnoreClientErr() {
		return ignoreClientErr;
	}
	
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("�޸��û���ѯquota��" + line);
		bf.append("������Ϣ {" + errMsg + "}" + line);
		bf.append("��Ҫ�޸ĵ��û����û��� {" + userName + "}" + line);
		bf.append("�µĲ�ѯquota  " + quota + line);
		bf.append("�µ�SlaveQuota " + slaveQuota + line);
		bf.append(ignoreClientErr ? "����֪ͨClientʧ�ܵ��쳣" : "������֪ͨClientʧ�ܵ��쳣"
			+ line);
		bf.append(ignoreConnErr ? "�����޷����ӵ�Client����" : "�������޷����ӵ�Client����");
		return bf.toString();
	}
}
