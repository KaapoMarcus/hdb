package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.EntityPrivilege;


public class SGrant extends Statement {
    private static final long serialVersionUID = 4649717224629031491L;

    private String user;
    private EntityPrivilege privilege;
    private boolean clearClientConnPool;

    public SGrant(String user, EntityPrivilege privileges) {
        this.user = user;
        this.privilege = privileges;
    }

    public String getUser() {
        return user;
    }

    public EntityPrivilege getPrivilege() {
        return privilege;
    }

    public boolean isClearClientConnPool() {
        return clearClientConnPool;
    }

    public void setClearClientConnPool(boolean clearPool) {
        this.clearClientConnPool = clearPool;
    }
}
