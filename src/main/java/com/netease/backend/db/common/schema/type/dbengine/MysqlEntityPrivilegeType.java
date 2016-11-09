
package com.netease.backend.db.common.schema.type.dbengine;


public enum MysqlEntityPrivilegeType {
    READ("SELECT,"), WRITE("INSERT,UPDATE,DELETE,"), EXEC("EXECUTE,"), GRANT("GRANT OPTION,"), ALL("ALL PRIVILEGES,");

    private String sqlIdentifier;

    private MysqlEntityPrivilegeType(String sqlIdentifier) {
        this.sqlIdentifier = sqlIdentifier;
    }

    
    public String getIdentifier() {
        return this.sqlIdentifier;
    }
}
