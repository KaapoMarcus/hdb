package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.List;





public class MySQLStmtStat extends StmtStat  implements Serializable {
	private static final long serialVersionUID = -6297056411119997390L;
	
	private Explain explain;
	private MySQLHandlerStat handlerStat;
	private long Handler_sample_count;
	private ColumnUsage columnUsage;
	
	public MySQLStmtStat(StatSig sig, Explain explain, String originalSql) {
		super(sig);
		tableList = sig.getTables();
		this.explain = explain;
		columnUsage = new ColumnUsage();
	}

	@Override
	public String getTypeStr() {
		return "MYSQL";
	}

	public long getNext() {
		if(handlerStat != null)
			return handlerStat.read_next;
		else
			return 0;
	}

	public double getAvgNext() {
		if (Handler_sample_count == 0 || handlerStat == null)
			return Double.NaN;
		else
			return (double)handlerStat.read_next / Handler_sample_count;
	}
	
	public void setNext(long handler_read_next) {
		if (handlerStat != null)
			handlerStat.read_next = handler_read_next;
	}

	public long getRndNext() {
		if (handlerStat != null)
			return handlerStat.rnd_next;
		else
			return 0;
	}

	public double getAvgRndNext() {
		if (Handler_sample_count == 0 || handlerStat == null)
			return Double.NaN;
		else
			return (double)handlerStat.rnd_next / Handler_sample_count;
	}
	
	public void setRndNext(long handler_read_rnd_next) {
		if (handlerStat != null)
			handlerStat.rnd_next = handler_read_rnd_next;
	}

	public long getPrev() {
		if (handlerStat != null)
			return handlerStat.read_prev;
		else
			return 0;
	}
	
	public double getAvgPrev() {
		if (Handler_sample_count == 0 || handlerStat == null)
			return Double.NaN;
		else
			return (double)handlerStat.read_prev / Handler_sample_count;
	}
	
	public void setPrev(long handler_read_prev) {
		if (handlerStat != null)
			handlerStat.read_prev = handler_read_prev;
	}

	public long getFirst() {
		if (handlerStat != null)
			return handlerStat.read_first;
		else
			return 0;
	}
	
	public double getAvgFirst() {
		if (Handler_sample_count == 0 || handlerStat == null)
			return Double.NaN;
		else
			return (double)handlerStat.read_first / Handler_sample_count;
	}
	
	public void setFirst(long handler_read_first) {
		if (handlerStat != null)
			handlerStat.read_first = handler_read_first;
	}
	
	public long getKey() {
		if (handlerStat != null)
			return handlerStat.read_key;
		else
			return 0;
	}
	
	public double getAvgKey() {
		if (Handler_sample_count == 0 || handlerStat == null)
			return Double.NaN;
		else
			return (double)handlerStat.read_key / Handler_sample_count;
	}
	
	public void setKey(long handler_read_key) {
		if (handlerStat != null)
			handlerStat.read_key = handler_read_key;
	}

	public long getRnd() {
		if (handlerStat != null)
			return handlerStat.read_rnd;
		return 0;
	}
	
	public double getAvgRnd() {
		if (Handler_sample_count == 0 || handlerStat == null)
			return Double.NaN;
		else
			return (double)handlerStat.read_rnd / Handler_sample_count;
	}
	
	public void setRnd(long handler_read_rnd) {
		if (handlerStat != null)
			handlerStat.read_rnd = handler_read_rnd;
	}
	
	public long getHandler_sample_count() {
		if (handlerStat != null)
			return Handler_sample_count;
		else
			return 0;
	}

	public void setHandler_sample_count(long handler_sample_count) {
		if (handlerStat != null)
			Handler_sample_count = handler_sample_count;
	}

	public void setHandlerStat(MySQLHandlerStat handlerStat) {
		if (handlerStat != null)
			this.handlerStat = handlerStat;
	}
	
	public void merge(MySQLStmtStat s) {
		super.merge(s);
		if(handlerStat == null)
		{
			handlerStat = s.handlerStat;
			Handler_sample_count = s.Handler_sample_count;
		}else if (s.Handler_sample_count != 0 && s.handlerStat != null) {
			handlerStat.read_first += s.handlerStat.read_first;
			handlerStat.read_key += s.handlerStat.read_key;
			handlerStat.read_next += s.handlerStat.read_next;
			handlerStat.read_prev += s.handlerStat.read_prev;
			handlerStat.read_rnd += s.handlerStat.read_rnd;
			handlerStat.rnd_next += s.handlerStat.rnd_next;
			Handler_sample_count += s.Handler_sample_count;
		}
		if(explain == null)
			explain = s.getExplain();
		else 
			explain.merge(s.getExplain());
	}
	
	
	public void setStatSig(StatSig sig)
	{
		super.setStatSig(sig);
	}

	public MySQLHandlerStat getHandlerStat() {
		return handlerStat;
	}

	public List<String> getColumns() {
		return columnUsage.getColumnList();
	}

	public void setColumns(List<String> columns) {
		columnUsage.setColumnList(columns);
	}

	public Explain getExplain() {
		return explain;
	}

	public List<String> getSelectColumnList() {
		return columnUsage.getSelectColumnList();
	}

	public List<String> getJoinColumnList() {
		return columnUsage.getJoinColumnList();
	}

	public List<String> getEqualColumnList() {
		return columnUsage.getEqualColumnList();
	}

	public List<String> getGroupByColumnList() {
		return columnUsage.getGroupByColumnList();
	}

	public List<String> getOrderByColumnList() {
		return columnUsage.getOrderByColumnList();
	}

	public List<String> getScopeColumnList() {
		return columnUsage.getRangeColumnList();
	}
	
	public void setColumnUsage(ColumnUsage columnUsage) {
		this.columnUsage = columnUsage;
	}
}
