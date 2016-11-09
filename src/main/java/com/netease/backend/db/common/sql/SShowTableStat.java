package com.netease.backend.db.common.sql;

import java.util.List;



public class SShowTableStat extends Statement {
    private static final long serialVersionUID = 1L;
    
    private List<String> tableNames;
    private List<String> policies;
    
    public SShowTableStat(List<String> tables) {
        this.tableNames = tables;
    }
    
    public List<String> getTableNames() {
        return this.tableNames;
    }
    
    public List<String> getPolicies() {
        return policies;
    }
    
    public void setPolicies(List<String> p) {
        this.policies = p;
    }
}
