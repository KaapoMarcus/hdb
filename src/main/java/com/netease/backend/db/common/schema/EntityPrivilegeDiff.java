
package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class EntityPrivilegeDiff implements Serializable {

    
    private static final long serialVersionUID = 1L;

    private EntityPrivilege grantPrivilege = null;
    private EntityPrivilege revokePrivilege = null;

    public EntityPrivilegeDiff(EntityPrivilege grantPrivilege,
            EntityPrivilege revokePrivilege) {
        this.grantPrivilege = grantPrivilege;
        this.revokePrivilege = revokePrivilege;
    }

    
    public EntityPrivilege getGrantPrivilege() {
        return this.grantPrivilege;
    }

    
    public EntityPrivilege getRevokePrivilege() {
        return this.revokePrivilege;
    }
}
