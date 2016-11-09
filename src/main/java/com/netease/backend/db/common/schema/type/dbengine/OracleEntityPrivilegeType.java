package com.netease.backend.db.common.schema.type.dbengine;


public enum OracleEntityPrivilegeType {
    READ("SELECT"), WRITE("INSERT,UPDATE,DELETE"), EXEC("EXECUTE"), ALL("ALL"), DOMAIN_READ(
            "DDB_DOMAIN_READ"), DOMAIN_WRITE("DDB_DOMAIN_WRITE"), DOMAIN_EXEC(
            "DDB_DOMAIN_EXEC"), DOMAIN_ALL("DDB_DOMAIN_ALL"), GLOBAL_READ(
            "DDB_GLOBAL_READ"), GLOBAL_WRITE("DDB_GLOBAL_WRITE"), GLOBAL_EXEC(
            "DDB_GLOBAL_EXEC"), GLOBAL_ALL("DDB_GLOBAL_ALL");

    private String sqlIdentifier;

    private OracleEntityPrivilegeType(String sqlIdentifier) {
        this.sqlIdentifier = sqlIdentifier;
    }

    
    public String getIdentifier() {
        return this.sqlIdentifier;
    }
}
