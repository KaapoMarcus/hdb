package com.netease.backend.db.common.sql.plan;



public class SSetPlanNeedMail extends SAlterPlanOp {
	private static final long serialVersionUID = -5103773839645116938L;
	
	private boolean needMail;
	
	public SSetPlanNeedMail(boolean b) {
		this.needMail = b;
	}
	
	public boolean isNeedMail() {
		return needMail;
	}
}
