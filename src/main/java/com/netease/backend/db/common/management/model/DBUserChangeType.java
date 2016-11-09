package com.netease.backend.db.common.management.model;

import java.io.Serializable;

import com.netease.backend.db.common.schema.User;



public class DBUserChangeType extends DBUserOp implements Serializable {
    private static final long serialVersionUID = 8134705934872940991L;
    
    private String userName;
    private int type;
    
    public DBUserChangeType(String username, int type) {
        super("DBUserChangeType");
        this.userName = username;
        this.type = type;
    }

    public String getUserName() {
        return this.userName;
    }
    
    public int getType() {
        return this.type;
    }
    
    public String toString() {
    	StringBuilder bf = new StringBuilder();
        bf.append("�޸��û����Ͳ�����" + line);
        bf.append("������Ϣ {" + errMsg + "}" + line);
        bf.append("��Ҫ�޸����͵��û����û��� {" + userName + "}" + line);
        bf.append("�޸�Ϊ���� {" + User.getTypeDesc(type) + "}");
        return bf.toString();
    }
}
