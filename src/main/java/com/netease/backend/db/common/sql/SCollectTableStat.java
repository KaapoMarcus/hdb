package com.netease.backend.db.common.sql;

import java.util.List;


public class SCollectTableStat extends Statement {
    private static final long serialVersionUID = -4689998163269017935L;
    
    private List<String> tables;
    private List<String> policies;
    private boolean analyzeTable;
    private boolean notifyClients;
    
    public SCollectTableStat(List<String> tableNames) {
        this.tables = tableNames;
        this.analyzeTable = false;
        this.notifyClients = true;
    }
    
    public List<String> getTables() {
        return tables;
    }
    
    public List<String> getPolicies() {
        return policies;
    }
    
    public void setPolicies(List<String> p) {
        this.policies = p;
    }
    
    public boolean isAnalyzeTable() {
        return analyzeTable;
    }
    
    public void setAnalyzeTable(boolean b) {
        this.analyzeTable = b;
    }
    
    public boolean isNotifyClients() {
        return notifyClients;
    }
    
    public void setNotifyClients(boolean b) {
        this.notifyClients = b;
    }
}
