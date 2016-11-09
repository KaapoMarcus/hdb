package com.netease.backend.db.common.management.model;

import java.io.Serializable;
import java.util.Set;


public class DBUserChangeHost extends DBUserOp implements Serializable {
	private static final long serialVersionUID = -958674752138982503L;
	
	private String userName;
	private Set<String> clientIpSet;
	private Set<String> qsIpSet;
	private boolean ignoreConnErr;
	private boolean ignoreClientErr;
	
	public DBUserChangeHost(String username, Set<String> clientIps,
			Set<String> qsIps, boolean isOnDBN, boolean ignoreConnErr,
			boolean ignoreClientErr) {
		super("DBUserChangeHost");
		this.userName = username;
		this.clientIpSet = clientIps;
		this.qsIpSet = qsIps;
		this.onDbn = isOnDBN;
		this.ignoreConnErr = ignoreConnErr;
		this.ignoreClientErr = ignoreClientErr;
	}
	
	public Set<String> getClientIpSet() {
		return clientIpSet;
	}
	
	public boolean isIgnoreConnErr() {
		return ignoreConnErr;
	}
	
	public Set<String> getQsIpSet() {
		return qsIpSet;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public boolean isIgnoreClientErr() {
		return ignoreClientErr;
	}
	
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("�޸�������ʵ��м����ַ������" + line);
		bf.append("������Ϣ {" + errMsg + "}" + line);
		bf.append("��Ҫ�޸ĵ��û����û��� {" + userName + "}" + line);
		bf.append("�µ��м����ַ " + clientIpSet + line);
		bf.append("�µĲ�ѯ��������ַ " + qsIpSet + line);
		bf.append((onDbn ? "��Ҫͬʱ��DBN���޸�" : "����Ҫ��DBN���޸�") + line);
		bf.append(ignoreClientErr ? "����֪ͨClientʧ�ܵ��쳣" : "������֪ͨClientʧ�ܵ��쳣"
			+ line);
		bf.append(ignoreConnErr ? "�����޷����ӵ�Client����" : "�������޷����ӵ�Client����");
		return bf.toString();
	}
}
