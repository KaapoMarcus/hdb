package com.netease.backend.db.common.sql.plan;



public class SAddPlanJob extends SAlterPlanOp {
    private static final long serialVersionUID = -1021932298033660890L;
    
    private PlanJob job;
    
    public SAddPlanJob(PlanJob job) {
        this.job = job;
    }
    
    public PlanJob getJob() {
        return job;
    }
}
