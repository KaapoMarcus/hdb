package com.netease.backend.db.common.management;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DumpConfig implements Serializable{

	private static final long serialVersionUID = -433686485746117357L;
	
	
	private List<String> tableList = new ArrayList<String>();
	
	
	private String condition = "";
	
	
	private boolean isSql = true;
	
	
	private String fieldTerminate = "\\t";
	
	
	private String lineTerminate = "\\n";
	
	
	private String fieldEnclose = "\\'";
	
	
	private boolean skipOpt = false;
	
	
	private boolean quick = true;
	
	
	private boolean allowKeywords = true;
	
	
	private boolean disableKeys = true;
	
	
	private boolean extendInsert = true;
	
	
	private boolean flushLogs = true;
	
	
	private boolean skipTZUTC = true;
	
	
	private boolean skipLockTable = true;
	
	
	private int masterData = 2;
	
	
	private boolean singleTran = true;
	
	
	private boolean compress = false;
	
	
	private boolean remoteCopy = false;
	
	
	private boolean delExpiredData = true;
	
	
	private int expiredDays = 4;
	
	
	private boolean ignoreCheckResult = true;
	
	
	private boolean ignoreDumpResult = false;
	
	
	
	
	public DumpConfig()
	{
		
	}


	public boolean isAllowKeywords() {
		return allowKeywords;
	}


	public void setAllowKeywords(boolean allowKeywords) {
		this.allowKeywords = allowKeywords;
	}


	public String getCondition() {
		return condition;
	}


	public void setCondition(String condition) {
		this.condition = condition;
	}


	public boolean isDisableKeys() {
		return disableKeys;
	}


	public void setDisableKeys(boolean disableKeys) {
		this.disableKeys = disableKeys;
	}


	public boolean isExtendInsert() {
		return extendInsert;
	}


	public void setExtendInsert(boolean extendInsert) {
		this.extendInsert = extendInsert;
	}


	public String getFieldEnclose() {
		return fieldEnclose;
	}


	public void setFieldEnclose(String fieldEnclose) {
		this.fieldEnclose = fieldEnclose;
	}


	public String getFieldTerminate() {
		return fieldTerminate;
	}


	public void setFieldTerminate(String fieldTerminate) {
		this.fieldTerminate = fieldTerminate;
	}


	public boolean isFlushLogs() {
		return flushLogs;
	}


	public void setFlushLogs(boolean flushLogs) {
		this.flushLogs = flushLogs;
	}


	public boolean isSql() {
		return isSql;
	}


	public void setSql(boolean isSql) {
		this.isSql = isSql;
	}


	public String getLineTerminate() {
		return lineTerminate;
	}


	public void setLineTerminate(String lineTerminate) {
		this.lineTerminate = lineTerminate;
	}


	public boolean isQuick() {
		return quick;
	}


	public void setQuick(boolean quick) {
		this.quick = quick;
	}


	public List<String> getTableList() {
		return tableList;
	}


	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}


	public boolean isSkipOpt() {
		return skipOpt;
	}


	public void setSkipOpt(boolean skipOpt) {
		this.skipOpt = skipOpt;
	}


	public boolean isSkipTZUTC() {
		return skipTZUTC;
	}


	public void setSkipTZUTC(boolean skipTZUTC) {
		this.skipTZUTC = skipTZUTC;
	}


	public int getMasterData() {
		return masterData;
	}


	public void setMasterData(int masterData) {
		this.masterData = masterData;
	}


	public boolean isSkipLockTable() {
		return skipLockTable;
	}


	public void setSkipLockTable(boolean skipLockTable) {
		this.skipLockTable = skipLockTable;
	}


	public boolean isSingleTran() {
		return singleTran;
	}


	public void setSingleTran(boolean singleTran) {
		this.singleTran = singleTran;
	}


	public boolean isCompress() {
		return compress;
	}


	public void setCompress(boolean compress) {
		this.compress = compress;
	}


	public boolean isRemoteCopy() {
		return remoteCopy;
	}


	public void setRemoteCopy(boolean remoteCopy) {
		this.remoteCopy = remoteCopy;
	}


	public boolean isIgnoreCheckResult() {
		return ignoreCheckResult;
	}


	public void setIgnoreCheckResult(boolean ignoreCheckResult) {
		this.ignoreCheckResult = ignoreCheckResult;
	}


	public boolean isDelExpiredData() {
		return delExpiredData;
	}


	public void setDelExpiredData(boolean delExpiredData) {
		this.delExpiredData = delExpiredData;
	}


	public int getExpiredDays() {
		return expiredDays;
	}


	public void setExpiredDays(int expiredDays) {
		this.expiredDays = expiredDays;
	}


	public boolean isIgnoreDumpResult() {
		return ignoreDumpResult;
	}


	public void setIgnoreDumpResult(boolean ignoreDumpResult) {
		this.ignoreDumpResult = ignoreDumpResult;
	}
	
	

}
