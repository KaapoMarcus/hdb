package com.netease.backend.db.common.sql.plan;

import com.netease.backend.db.common.sql.Statement;


public class SResumePlan extends Statement {
    private static final long serialVersionUID = 6417754397505878613L;
    
    private String name;
    
    public SResumePlan(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
