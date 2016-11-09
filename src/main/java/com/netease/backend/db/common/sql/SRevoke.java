package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.EntityPrivilege;


public class SRevoke extends Statement {
    private static final long serialVersionUID = -2925112064220830158L;

    String user;
    private EntityPrivilege privilege;
    private boolean clearClientConnPool;

    public SRevoke(String user, EntityPrivilege privilege) {
        super();
        this.user = user;
        this.privilege = privilege;
    }

    public EntityPrivilege getPrivilege() {
        return privilege;
    }

    public String getUser() {
        return user;
    }

    public boolean isClearClientConnPool() {
        return clearClientConnPool;
    }

    public void setClearClientConnPool(boolean clearPool) {
        this.clearClientConnPool = clearPool;
    }
}
