package com.netease.backend.db.common.sql.plan;

import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.sql.Statement;
import com.netease.backend.db.common.utils.StringUtils;


public class SPlan extends Statement implements Comparable<SPlan>, Cloneable {
	private static final long serialVersionUID = 6128798012876796672L;
	
	public static final int STATUS_SCHEDULE = 1;
	
	public static final int STATUS_OVERDUE = 2;
	
	public static final int STATUS_SUSPEND = 3;
	
	public static final int STATUS_FINISH = 4;
	
	
	private String planName;
	
	
	private PlanTime planTime;
	
	
	private List<PlanJob> planJobs;
	
	
	private PlanType type;
	
	
	private int status;
	
	
	private String previousResult;
	
	
	private String planUser;
	
	
	private boolean needMail;
	
	public SPlan(String name) {
		this.planName = name;
		this.planJobs = new ArrayList<PlanJob>();
		this.status = STATUS_SCHEDULE;
		this.previousResult = "";
		this.needMail = true;
		this.type = PlanType.NOTHING;
	}

	@Override
	public SPlan clone() {
		return (SPlan) super.clone();
	}

	public PlanType getType() {
		return type;
	}
	
	public boolean isNeedMail() {
		return needMail;
	}
	
	public void setNeedMail(boolean isNeedMail) {
		this.needMail = isNeedMail;
	}
	
	public boolean addPlanJob(PlanJob job) {
		boolean b = planJobs.add(job);
		checkType();
		return b;
	}
	
	public void addPlanJob(int index, PlanJob job) {
		planJobs.add(index, job);
		checkType();
	}
	
	public boolean removePlanJob(PlanJob job) {
		boolean b = planJobs.remove(job);
		checkType();
		return b;
	}
	
	public PlanJob removePlanJob(int index) {
		PlanJob job = planJobs.remove(index);
		checkType();
		return job;
	}
	
	public List<PlanJob> getPlanJobs() {
		List<PlanJob> result = new ArrayList<PlanJob>();
		result.addAll(planJobs);
		return result;
	}
	
	public void setPlanJobs(List<PlanJob> jobs) {
		this.planJobs = jobs;
		checkType();
	}
	
	public String getPlanName() {
		return planName;
	}
	
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	
	public PlanTime getPlanTime() {
		return planTime;
	}
	
	public void setPlanTime(PlanTime planTime) {
		this.planTime = planTime;
	}
	
	public String getPlanUser() {
		return planUser;
	}
	
	public void setPlanUser(String planUser) {
		this.planUser = planUser;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getPreviousResult() {
		return previousResult;
	}
	
	public void setPreviousResult(String result) {
		this.previousResult = result;
	}
	
	public String getSqlDesc() {
		return planName + " START " + planTime + " " + StringUtils.convertToString(planJobs, ", ");
	}
	
	@Override
	public String toString() {
		return getSqlDesc();
	}
	
	public static String getStatusDesc(int s) {
		switch(s) {
		case STATUS_SCHEDULE:
			return "SCHEDULE";
		case STATUS_OVERDUE:
			return "OVERDUE";
		case STATUS_SUSPEND:
			return "SUSPEND";
		case STATUS_FINISH:
			return "FINISH";
		default:
			return "UNKNOWN";
		}
	}
	
	public int compareTo(SPlan o) {
		return planName.compareTo(o.getPlanName());
	}
	
	@Override
	public int hashCode() {
        return planName.hashCode();
    }
	
	private void checkType() {
		PlanType t = PlanType.NOTHING;
		for (PlanJob job : planJobs) {
			if (PlanType.NOTHING == t)
				t = job.getType();
			else if (t == PlanType.OPTION)
				t = job.getType();
			else if (PlanType.OPTION != job.getType() 
					&& t != job.getType()) {
				t = PlanType.OTHER;
				break;
			}
		}
		if (type != t)
			type = t;
	}
}