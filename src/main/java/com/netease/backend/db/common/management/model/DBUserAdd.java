package com.netease.backend.db.common.management.model;

import java.io.Serializable;

import com.netease.backend.db.common.schema.User;


public class DBUserAdd extends DBUserOp implements Serializable {
    private static final long serialVersionUID = -5797565814827658415L;

    private User user;

    public DBUserAdd(User u) {
        super("DBUserAdd");
        this.user = u;
    }

    public User getUser() {
        return this.user;
    }

    public String toString() {
    	StringBuilder bf = new StringBuilder();
        bf.append("�����û�������" + line);
        bf.append("������Ϣ {" + errMsg + "}" + line);
        bf.append("��Ҫ�����û����û��� {" + user.getName() + "}" + line);
        bf.append("���� {" + User.getTypeDesc(user.getType()) + "}" + line);
        bf.append("������ʵ��м����ַ " + user.getClientIps() + line);
        bf.append("������ʵĲ�ѯ��������ַ " + user.getQsIps() + line);
        bf.append("������ΪDBA���ʵ�������ַ " + user.getAdminIps());
        return bf.toString();
    }
}
