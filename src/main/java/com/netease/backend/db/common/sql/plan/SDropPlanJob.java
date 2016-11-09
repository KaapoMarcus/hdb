package com.netease.backend.db.common.sql.plan;



public class SDropPlanJob extends SAlterPlanOp {
    private static final long serialVersionUID = -4095031247409688457L;
    
    private String jobName;
    
    public SDropPlanJob(String jobName) {
        this.jobName = jobName;
    }
    
    public String getJobName() {
        return jobName;
    }
}
