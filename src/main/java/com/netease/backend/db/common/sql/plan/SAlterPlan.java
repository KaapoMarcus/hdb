package com.netease.backend.db.common.sql.plan;

import com.netease.backend.db.common.sql.Statement;



public class SAlterPlan extends Statement {
    private static final long serialVersionUID = -1271668219663249390L;
    
    private String planName;
    private SAlterPlanOp op;
    
    public SAlterPlan(String name, SAlterPlanOp op) {
        this.planName = name;
        this.op = op;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public SAlterPlanOp getOp() {
        return op;
    }
}
