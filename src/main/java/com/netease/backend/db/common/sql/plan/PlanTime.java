package com.netease.backend.db.common.sql.plan;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.netease.backend.db.common.utils.TimeUtils;



public class PlanTime implements Serializable {
	private static final long serialVersionUID = 2090349117916073425L;
	
	private CronExpression cronExpression;
	
	private Date startTime;     
	private Date endTime;       
	private Date nextExeTime;   
	private Date previousExeTime;   
	
	
	public PlanTime(CronExpression cronExpression, Date st, Date et) {
		this.cronExpression = cronExpression;
		setStartTime(st);
		setEndTime(et);
		computeNextExeDateByNow();
	}
	
	public CronExpression getCronExpression() {
		return cronExpression;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		if (startTime == null) {
			throw new IllegalArgumentException("Start time cannot be null");
		}
		
		Date eTime = getEndTime();
		if (eTime != null && startTime != null && eTime.before(startTime)) {
			throw new IllegalArgumentException(
			"End time cannot be before start time");
		}
		
		
		Calendar cl = Calendar.getInstance();
		cl.setTime(startTime);
		cl.set(Calendar.MILLISECOND, 0);
		
		this.startTime = cl.getTime();
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		Date sTime = getStartTime();
		if (sTime != null && endTime != null && sTime.after(endTime)) {
			throw new IllegalArgumentException(
			"End time cannot be before start time");
		}
		
		this.endTime = endTime;
	}
	
	public Date getNextExeTime() {
		return nextExeTime;
	}
	
	protected void setNextExeTime(Date nextExeTime) {
		this.nextExeTime = nextExeTime;
	}
	
	public Date getPreviousExeTime() {
		return previousExeTime;
	}
	
	public void setPreviousExeTime(Date previousExeTime) {
		this.previousExeTime = previousExeTime;
	}
	
	public Date computeNextExeDateByNow() {
		nextExeTime = getExeDateAfter(null);
		return nextExeTime;
	}
	
	public Date computeNextExeDate() {
		nextExeTime = getExeDateAfter(nextExeTime);
		return nextExeTime;
	}
	
	public Date getExeDateAfter(Date afterTime) {
		if (afterTime == null) 
			afterTime = new Date();
		
		if (startTime.after(afterTime)) 
			afterTime = new Date(startTime.getTime() - 1000l);
		
		if (getEndTime() != null && (afterTime.compareTo(getEndTime()) >= 0)) 
			return null;
		
		Date d = getDateAfter(afterTime);
		if (getEndTime() != null && d != null && d.after(getEndTime())) 
			return null;
		
		return d;
	}
	
	private Date getDateAfter(Date afterTime) {
		return (cronExpression == null) ? null : cronExpression.getDateAfter(afterTime);
	}
	
	
	@Override
	public String toString() {
		return getSqlDesc();
	}
	
	protected String getSqlDesc() {
		String s = "'" + cronExpression.getCronString() + "'";
		if (null != startTime)
			s += " FROM '" + TimeUtils.formatTime(startTime) + "'";
		if (null != endTime)
			s += " TO '" + TimeUtils.formatTime(endTime) + "'";
		return s;
	}
}
