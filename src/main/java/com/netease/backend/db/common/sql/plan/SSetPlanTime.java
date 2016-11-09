package com.netease.backend.db.common.sql.plan;

import java.util.Date;




public class SSetPlanTime extends SAlterPlanOp {
	private static final long serialVersionUID = 1L;
	
	private String cronExpr;
	private Date startTime;
	private Date endTime;
	
	public SSetPlanTime(String cron, Date start, Date end) {
		if (cron == null && start == null && end == null)
			throw new IllegalArgumentException("SQL����ʽ����");
		this.cronExpr = cron;
		this.startTime = start;
		this.endTime = end;
	}
	
	public String getCronExpr() {
		return cronExpr;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
}
