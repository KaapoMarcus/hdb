package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.netease.stat.MCVCollector;
import com.netease.stat.MCVCollector.MCVItem;



public class DDBStmtStat extends StmtStat implements Serializable {
	private static final long serialVersionUID = -6780262147658716418L;
	
	public static final String ATT_MYSQL_COUNT = "mysql_count";
	public static final String ATT_MYSQL_TIME = "mysql_time";
	public static final String ATT_MYSQL_AVG_TIME = "mysql_avg_time";
	public static final String ATT_DBN_COUNT = "dbn_count";
	public static final String ATT_AVG_DBN_COUNT = "avg_dbn_count";
	public static final String ATT_ROWS = "rows";
	public static final String ATT_AVG_ROWS = "avg_rows";
	
	public static final String ATT_VALID_DBN_COUNT = "valid_dbn_count";
	public static final String ATT_SINGLE_DBN_COUNT = "single_dbn_count";
	public static final String ATT_AVG_VALID_DBN_COUNT = "avg_valid_dbn_count";
	public static final String ATT_LW_TIMEOUT_EXCEPTION = "lock_wait_timeout_exception";
	public static final String ATT_DEADLOCK_EXCEPTION = "lock_deadlock_exception";
	
	public static final int MAX_MCV_ITEM_LENGTH = 64;
	
	
	private int mysqlCount = 0;
	
	private long mysqlTime = 0;
	
	private long maxMysqlTime = 0;	
	
	private int dbnCount = 0;
	
	private long nonIntrinsicTime = 0;
	
	private int rows = 0;
	
	
	private int validDbnCount = 0;
	
	private int singleDbnCount = 0;
	
	
	private transient List<MCVCollector<Object>> collectorList;
	
	
	private List<Collection<MCVItem<Object>>> mcvResults;
	
	
	private long[] itemCounts;
	
	
	private MemcachedStat memcachedStat;
		
	public DDBStmtStat(StatSig sig) {
		super(sig);
		this.memcachedStat = new MemcachedStat();
	}

	public void merge(DDBStmtStat s) {
		super.merge(s);
		mysqlCount += s.mysqlCount;
		mysqlTime += s.mysqlTime;
		maxMysqlTime += s.maxMysqlTime;
		nonIntrinsicTime += s.nonIntrinsicTime;
		dbnCount += s.dbnCount;
		rows += s.rows;
		validDbnCount += s.validDbnCount;
		singleDbnCount += s.singleDbnCount;
		memcachedStat.merge(s.memcachedStat);
	}
	
	public long getDbnCount() {
		return dbnCount;
	}
	
	public void setDbnCount(int dbnCount) {
		this.dbnCount = dbnCount;
	}

	synchronized public void addMysqlTime(long time) {
		if (mysqlCount == -1) 
			mysqlTime = mysqlCount = 0;
		mysqlCount++;
		mysqlTime += time;
		if(maxMysqlTime < time)
			maxMysqlTime=time;
	}
	
	public double getAvgDbnCount() {
		if (count == 0)
			return 0;
		else
			return (double)dbnCount / count;
	}

	public long getMysqlCount() {
		return mysqlCount;
	}
	
	public long getMysqlTime() {
		return mysqlTime;
	}
	
	public double getAvgMysqlTime() {
		if (count == 0)
			return 0;
		else
			return (double)mysqlTime / count;
	}
	
	public double getAvgMysqlCount() {
		if (count == 0)
			return 0;
		else
			return (double)mysqlCount / count;
	}
	
	public long getNonIntrinsicTime() {
		return nonIntrinsicTime;
	}
	
	public double getAvgNonIntrinsicTime() {
		if (mysqlCount == 0)
			return 0;
		else
			return (double)nonIntrinsicTime / count;
	}
	
	public void addNonIntrinsicTime(long time) {
		if (nonIntrinsicTime == -1)
			nonIntrinsicTime = 0;
		nonIntrinsicTime += time;
	}

	public long getRows() {
		return rows;
	}

	public double getAvgRows() {
		if (count == 0)
			return 0;
		else
			return (double)rows / count;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}

	public void addRows(int rows) {
		if (this.rows == -1)
			this.rows = 0;
		this.rows += rows;
	}
	
	public void calcNetTime() {
		time -= nonIntrinsicTime;
	}
	
	@Override
	public String getTypeStr() {
		return "DDB";
	}

	public long getSingleDbnCount() {
		return singleDbnCount;
	}
	
	public void addSingleDbnCount() {
		singleDbnCount++;
	}

	public long getValidDbnCount() {
		return validDbnCount;
	}
	
	public void addValidDbnCount(int count) {
		validDbnCount += count;
	}
	public double getAvgValidDbnCount() {
		if (count == 0)
			return 0;
		else
			return (double)validDbnCount / count;
	}

	public void setSingleDbnCount(int singleDbnCount) {
		this.singleDbnCount = singleDbnCount;
	}

	public void setValidDbnCount(int validDbnCount) {
		this.validDbnCount = validDbnCount;
	}

	public long getMaxMysqlTime() {
		if (count == 0)
			return 0;
		else
			return maxMysqlTime/count;
	}
	
	
	public void initMCVs(int mcvItems, double mcvFreq)
	{
		collectorList = new ArrayList<MCVCollector<Object>>();
		int size = getSignature().getValues().size();
		for(int i=0;i< size;i++)
		{
			MCVCollector<Object> collector = MCVCollector.getInstance(mcvItems, mcvFreq);
			collectorList.add(collector);
		}
		addValues(this);
	}
	
	
	public void addValues(DDBStmtStat stat)
	{
		assert(collectorList.size() == stat.getSignature().getValues().size());
		
		for(int i=0;i<collectorList.size();i++)
		{
			if(i<stat.getSignature().getValues().size())
			{
				MCVCollector<Object> collector = collectorList.get(i);
				List<Object> valueList = stat.getSignature().getValues().get(i);
				for(Object obj : valueList)
					collector.addItem(obj);
				valueList.clear();
			}
		}
		stat.getSignature().getValues().clear();
	}

	public List<MCVCollector<Object>> getCollectorList() {
		return collectorList;
	}
	
	
    public void calcMcvResult() {
        if (this.mcvResults == null)
            mcvResults = new LinkedList<Collection<MCVItem<Object>>>();
        mcvResults.clear();
        int size = collectorList.size();
        if (size > 0)
            itemCounts = new long[size];
        int i = 0;
        for (MCVCollector<Object> collector : this.collectorList) {
            mcvResults.add(collector.getMCVs());
            itemCounts[i] = collector.getNumItems();
            i++;
        }
    }

	public long[] getItemCounts() {
		return itemCounts;
	}

	public List<Collection<MCVItem<Object>>> getMcvResults() {
		return mcvResults;
	}
	
	public MemcachedStat getMemcachedStat() {
		return memcachedStat;
	}

}
