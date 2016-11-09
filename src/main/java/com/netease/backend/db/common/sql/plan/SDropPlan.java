package com.netease.backend.db.common.sql.plan;

import com.netease.backend.db.common.sql.Statement;


public class SDropPlan extends Statement {
    private static final long serialVersionUID = -9051193619781063442L;
    
    private String name;
    
    public SDropPlan(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
