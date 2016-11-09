package com.netease.backend.db.common.management.model;

import java.io.Serializable;
import java.util.Set;

import com.netease.backend.db.common.schema.EntityPrivilege;
import com.netease.backend.db.common.schema.User;


public class DBUserChangeGrant extends DBUserOp implements Serializable {
    private static final long serialVersionUID = 1821639134703331593L;

    private final User _user;
    private final Set<EntityPrivilege> _newPrivileges;

    private final boolean _clearClientConnPool;

    
    public DBUserChangeGrant(User user, Set<EntityPrivilege> newPrivileges,
            boolean clearConnPool) {
        super("DBUserChangeGrant");
        this._user = user;
        this._newPrivileges = newPrivileges;
        this._clearClientConnPool = clearConnPool;
    }

    @Override
    public String toString() {
        final StringBuilder bf = new StringBuilder();
        bf.append("�޸��û����ݿ�Ȩ�޲�����").append(this.line);
        bf.append("������Ϣ {").append(this.errMsg).append("}").append(this.line);
        bf.append("��Ҫ�޸�Ȩ�޵��û����û��� {").append(this._user).append("}").append(
                this.line);
        bf.append("�û�����Ȩ��: ").append(this.line);
        for (final EntityPrivilege entityPrivilege : this._newPrivileges) {
            bf.append("  [").append(entityPrivilege).append("]").append(
                    this.line);
        }
        bf.append(this._clearClientConnPool ? "ͬʱ���Client�ϵ����ӳ�"
                : "����Ҫ���Client�ϵ����ӳ�");
        return bf.toString();
    }

    public String getUserName() {
        return this._user.getName();
    }

    public boolean isClearClientConnPool() {
        return this._clearClientConnPool;
    }

    
    public Set<EntityPrivilege> getNewPrivileges() {
        return this._newPrivileges;
    }
}
