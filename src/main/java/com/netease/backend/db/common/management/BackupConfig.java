package com.netease.backend.db.common.management;

import java.io.Serializable;
import java.util.List;

import com.netease.backend.db.common.utils.StringUtils;


public class BackupConfig implements Serializable{

	private static final long serialVersionUID = 4411925667760797999L;
	
	
	private List<String> tableNameList = null;
	
	
	private boolean backupMyisam = true;
	
	
	private boolean synch = false;
	
	
	private boolean ignoreConnErr = true;
	
	
	private boolean compress = false;
	
	
	private boolean remoteCopy = false;
	
	
	private boolean delExpiredData = true;
	
	
	private int expiredDays = 4;
	
	
	public BackupConfig()
	{	
	}
	
	public String toString()
	{
		return "��-'"+StringUtils.convertToString(tableNameList, ',')+"', ͬ��-"+synch+
		", ѹ��-"+compress+", Զ�̱���-"+remoteCopy+", ɾ����������-"+delExpiredData+", ��������-"+expiredDays;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
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

	public boolean isIgnoreConnErr() {
		return ignoreConnErr;
	}

	public void setIgnoreConnErr(boolean ignoreConnErr) {
		this.ignoreConnErr = ignoreConnErr;
	}

	public boolean isRemoteCopy() {
		return remoteCopy;
	}

	public void setRemoteCopy(boolean remoteCopy) {
		this.remoteCopy = remoteCopy;
	}

	public boolean isSynch() {
		return synch;
	}

	public void setSynch(boolean synch) {
		this.synch = synch;
	}

	public List<String> getTableNameList() {
		return tableNameList;
	}

	public void setTableNameList(List<String> tableNameList) {
		this.tableNameList = tableNameList;
	}

	public boolean isBackupMyisam() {
		return backupMyisam;
	}

	public void setBackupMyisam(boolean backupMyisam) {
		this.backupMyisam = backupMyisam;
	}

}
