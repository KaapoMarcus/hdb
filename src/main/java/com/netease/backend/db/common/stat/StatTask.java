package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.netease.stat.MCVCollector;


public class StatTask implements Serializable, Comparable<StatTask>, Cloneable {
	
	private static final long serialVersionUID = -4438684756550382053L;
	public static final int MYSQL_EXPLAIN_DISABLED = 0;
	public static final int MYSQL_EXPLAIN_FIRST = 1;
	public static final int MYSQL_EXPLAIN_EACH = 2;
	
	public static final int MYSQL_HS_DISABLED = 0;
	public static final int MYSQL_HS_EACH = 1;
	public static final int MYSQL_HS_SAMPLE = 2;
	
	
	public static final int STAT_TASK_IDLE = 1;
	
	
	public static final int STAT_TASK_RUNNING = 2;
	
	
	public static final int STAT_TASK_END = 3;
	
	
	public static final int STAT_TASK_ERROR = 4;
	
	
	private int id;
	
	
	private long startTime = 0;
	
	
	private long endTime = 0;
	
	
	private List<Integer> clientIdList = null;
	
	
	private boolean statBucket = false;
	
	
	private boolean statOp = false;
	
	
	private boolean statDBStmt = false;
	
	
	private boolean statMysqlStmt = false;
	
	
	private boolean statColumn = false;
	
	
	private boolean statIndex = false;
	
	
	private boolean statTableMemcached = false;
	
	
	private boolean statMCV = false;
	
	
	private int mysqlExplainPly = MYSQL_EXPLAIN_DISABLED;
	
	
	private int mysqlHandlerPly = MYSQL_HS_DISABLED;
	
	
	private int mysqlHandlerSample = 10;
	
	
	private List<Integer> clientInStat = new ArrayList<Integer>();
	
	
	private List<Integer> clientsReply = new ArrayList<Integer>();
	
	
	private int mcvNumLimit = 5;
	
	
	private double mcvFreqLimit = MCVCollector.TARGET_FREQ_UNSPECIFIED;
	
	
	private List<String> policyList = new ArrayList<String>();
	
	
	private int status = STAT_TASK_IDLE;	
	
	
	private List<StatResult> results = new ArrayList<StatResult>();
	
	
	public StatTask(int taskId, long start, long end, List<Integer> clientList) throws IllegalArgumentException
	{
		this.id = taskId;
		this.startTime = start;
		this.endTime = end;
		if(clientList != null)
			this.clientIdList = clientList;
		else
			clientIdList = new ArrayList<Integer>();
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getMysqlExplainPly() {
		return mysqlExplainPly;
	}

	public void setMysqlExplainPly(int mysqlExplainPly) {
		this.mysqlExplainPly = mysqlExplainPly;
	}

	public int getMysqlHandlerPly() {
		return mysqlHandlerPly;
	}

	public void setMysqlHandlerPly(int mysqlHandlerPly) {
		this.mysqlHandlerPly = mysqlHandlerPly;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public boolean isStatBucket() {
		return statBucket;
	}

	public void setStatBucket(boolean statBucket) {
		this.statBucket = statBucket;
	}

	public boolean isStatDBStmt() {
		return statDBStmt;
	}

	public void setStatDBStmt(boolean statDBStmt) {
		this.statDBStmt = statDBStmt;
	}

	public boolean isStatMysqlStmt() {
		return statMysqlStmt;
	}

	public void setStatMysqlStmt(boolean statMysqlStmt) {
		this.statMysqlStmt = statMysqlStmt;
	}

	public boolean isStatOp() {
		return statOp;
	}

	public void setStatOp(boolean statOp) {
		this.statOp = statOp;
	}

	public List<Integer> getClientIdList() {
		return clientIdList;
	}
	
	public void setClientIdList(List<Integer> clientIdList) {
		this.clientIdList = clientIdList;
	}

	public int getId() {
		return id;
	}

	public int getMysqlHandlerSample() {
		return mysqlHandlerSample;
	}
    
    public void setId(int taskID) {
        this.id = taskID;
    }

	public void setMysqlHandlerSample(int mysqlHandlerSample) {
		this.mysqlHandlerSample = mysqlHandlerSample;
	}

	public boolean isStatColumn() {
		return statColumn;
	}

	public void setStatColumn(boolean statColumn) {
		this.statColumn = statColumn;
	}
	
	public boolean isStatTableMemcached() {
		return statTableMemcached;
	}

	public void setStatTableMemcached(boolean statTableMemcached) {
		this.statTableMemcached = statTableMemcached;
	}

	public boolean isStatIndex() {
		return statIndex;
	}

	public void setStatIndex(boolean statIndex) {
		this.statIndex = statIndex;
	}
	
    public String toString() {
        return id + ": ͳ��" + clientIdList + "�ϵ�״̬��Ϣ";
    }
    
    public int compareTo(StatTask other) {
        if (id < other.getId())
            return -1;
        else if (id == other.getId())
            return 0;
        else 
            return 1;
    }
    
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (this.getClass() != otherObject.getClass()) {
            return false;
        }
        return ((StatTask) otherObject).getId() == id;
    }
    
    @Override
	public int hashCode() {
        return ((Integer) id).hashCode();
    }

	public List<Integer> getClientInStat() {
		return clientInStat;
	}

	public List<Integer> getClientsReply() {
		return clientsReply;
	}

	public double getMcvFreqLimit() {
		return mcvFreqLimit;
	}

	public void setMcvFreqLimit(double mcvFreqLimit) {
		this.mcvFreqLimit = mcvFreqLimit;
	}

	public int getMcvNumLimit() {
		return mcvNumLimit;
	}

	public void setMcvNumLimit(int mcvNumLimit) {
		this.mcvNumLimit = mcvNumLimit;
	}

	public List<String> getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List<String> policyList) {
		this.policyList = policyList;
	}

	public boolean isStatMCV() {
		return statMCV;
	}

	public void setStatMCV(boolean statMCV) {
		this.statMCV = statMCV;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		StatTask copy = new StatTask(id, startTime, endTime, clientIdList);
		copy.mcvFreqLimit = mcvFreqLimit;
		copy.mcvNumLimit = mcvNumLimit;
		copy.mysqlExplainPly = mysqlExplainPly;
		copy.mysqlHandlerPly = mysqlHandlerPly;
		copy.mysqlHandlerSample = mysqlHandlerSample;
		copy.policyList = new LinkedList<String>(policyList);
		copy.statBucket = statBucket;
		copy.statColumn = statColumn;
		copy.statTableMemcached = statTableMemcached;
		copy.statDBStmt = statDBStmt;
		copy.statIndex = statIndex;
		copy.statMCV = statMCV;
		copy.statMysqlStmt = statMysqlStmt;
		copy.statOp = statOp;
		return copy;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	
	public List<StatResult> getResults() {
		return results;
	}
	
	
	synchronized public StatResult addResult(long startTime, long produceTime)
	{
		int resultId;
		if(results.size() == 0)
			resultId = 1;
		else
			resultId = results.get(results.size()-1).getResultId()+1;
		StatResult result = new StatResult(this.id, resultId, startTime, produceTime);
		results.add(result);
		return result;
	}

	public void setResults(List<StatResult> results) {
		this.results = results;
	}
	
	public StatResult getResult(int resultId)
	{
		for(StatResult result : results)
			if(result.getResultId() == resultId)
				return result;
		return null;
	}
	
	public List<String> getActions() {
		List<String> actionList = new ArrayList<String>();
		if (isStatDBStmt())
			actionList.add("DDB");
		if (isStatMysqlStmt())
			actionList.add("MYSQL");
		if (isStatOp())
			actionList.add("OPS");
		if (isStatBucket())
			actionList.add("BUCKET");
		if (isStatColumn())
			actionList.add("COLUMN");
		if (isStatIndex())
			actionList.add("INDEX");
		if (isStatMCV())
			actionList.add("MCV");
		if (isStatTableMemcached())
			actionList.add("TABLE_MEM");
		return actionList;
	}
	
	public static String getStatusDesc(int s) {
		switch(s) {
			case STAT_TASK_IDLE:
				return "IDLE";
			case STAT_TASK_RUNNING:
				return "RUNNING";
			case STAT_TASK_END:
				return "FINISH";
			case STAT_TASK_ERROR:
				return "ERROR";
			default:
				return "UNKNOWN";
		}
	}
}
