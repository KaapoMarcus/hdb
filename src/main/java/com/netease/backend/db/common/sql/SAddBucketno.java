package com.netease.backend.db.common.sql;


public class SAddBucketno extends Statement {
    private static final long serialVersionUID = -4363307866483214320L;
    
    private String tableName;
    private String policyName;
    private boolean withLock;

    public String getTableName() {
        return tableName;
    }
    
    public String getPolicyName() {
        return policyName;
    }
    
    public boolean isWithLock() {
        return withLock;
    }
    
    public void setTableName(String name) {
        this.tableName = name;
    }
    
    public void setPolicyName(String name) {
        this.policyName = name;
    }
    
    public void setWithLock(boolean b) {
        this.withLock = b;
    }
}
