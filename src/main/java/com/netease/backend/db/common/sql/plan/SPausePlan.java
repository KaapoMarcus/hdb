package com.netease.backend.db.common.sql.plan;

import com.netease.backend.db.common.sql.Statement;


public class SPausePlan extends Statement {
    private static final long serialVersionUID = -7155720572254299549L;
    
    private String name;
    
    public SPausePlan(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
