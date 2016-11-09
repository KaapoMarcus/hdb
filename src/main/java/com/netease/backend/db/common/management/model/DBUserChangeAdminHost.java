package com.netease.backend.db.common.management.model;

import java.io.Serializable;
import java.util.Set;


public class DBUserChangeAdminHost extends DBUserOp implements Serializable {
    private static final long serialVersionUID = -6498843294874548706L;
    
    private String userName;
    private Set<String> adminIpSet;

    public DBUserChangeAdminHost(String username, Set<String> adminIps) {
        super("DBUserChangeAdminHost");
        this.userName = username;
        this.adminIpSet = adminIps;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public Set<String> getAdminIpSet() {
        return this.adminIpSet;
    }
    
    public String toString() {
    	StringBuilder bf = new StringBuilder();
        bf.append("�޸�������ΪDBA���ʵ�������ַ������" + line);
        bf.append("������Ϣ {" + errMsg + "}" + line);
        bf.append("��Ҫ�޸ĵ��û����û��� {" + userName + "}" + line);
        bf.append("�µ�DBA������ַ " + adminIpSet);
        return bf.toString();
    }
}
