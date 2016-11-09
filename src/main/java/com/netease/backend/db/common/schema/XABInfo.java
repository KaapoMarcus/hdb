package com.netease.backend.db.common.schema;

import java.io.Serializable;

import javax.transaction.xa.Xid;

import com.mysql.jdbc.jdbc2.optional.MysqlXid;



public class XABInfo implements Serializable {
    private static final long serialVersionUID = 8777577883317474364L;

    
	public static final byte XAB_STATUS_UNKNOWN = 0;
	
	
	public static final byte XAB_STATUS_SUSPEND = 1;
	
	
	public static final byte XAB_STATUS_AUTO_COMMIT = 2;
	
	
	public static final byte XAB_STATUS_AUTO_ROLLBACK = 3;
	
	
	public static final byte XAB_STATUS_MANU_COMMIT = 4;
	
	
	public static final byte XAB_STATUS_MANU_ROLLBACK = 5;
	
	
	public static final byte XAB_STATUS_TIMEOUT = 6;
	
	
	public static final byte XAB_STATUS_TIMEOUT_ROLLBACK = 7;
	
	
	public static final byte XAB_STATUS_ERROR = 8;
	
	
	public static final byte XAB_STATUS_NOT_FOUND = 9;

	
	
	
	public static final byte XAB_OPT_COMMIT = 1;
	
	
	public static final byte XAB_OPT_ROLLBACK = 0;
	
	
	public static final byte XAB_OPT_UNKNOWN = -1;
	
	
	
	
	private byte[] gid = null;
	
	
	private byte[] bid = null;
	
	
	private int formateId;
	
	
	private Database db;
	
	
	private byte operation;
	
	
	long firstTime;
	
	
	long lastTime;
	
	
	byte status;
	
	
	boolean isExist = false;
	

	
	public XABInfo(Xid xid, Database mdb, long firstTime, byte status, byte opt)
	{
		if(xid != null)
		{
			this.gid = xid.getGlobalTransactionId();
			this.bid = xid.getBranchQualifier();
			this.formateId = xid.getFormatId();
		}
		this.db = mdb;
		this.firstTime = firstTime;
		this.lastTime = firstTime;
		this.status = status;
		this.operation = opt;
	}
	
	
	public static String getStatusDesc(byte status)
	{
		switch(status)
		{
			case XAB_STATUS_SUSPEND:
				return "����";
			case XAB_STATUS_AUTO_COMMIT:
				return "�Զ��ύ";
			case XAB_STATUS_AUTO_ROLLBACK:
				return "�Զ��ع�";
			case XAB_STATUS_MANU_COMMIT:
				return "�ֶ��ύ";
			case XAB_STATUS_MANU_ROLLBACK:
				return "�ֶ��ع�";
			case XAB_STATUS_TIMEOUT:
				return "��ʱ";
			case XAB_STATUS_TIMEOUT_ROLLBACK:
				return "��ʱ�ع�";
			case XAB_STATUS_UNKNOWN:
				return "δ֪״̬";
			case XAB_STATUS_NOT_FOUND:
				return "������";
			default:
				return "����״̬";
		}
	}


	public Database getDb() {
		return db;
	}


	public void setDb(Database db) {
		this.db = db;
	}


	public long getFirstTime() {
		return firstTime;
	}


	public void setFirstTime(long firstTime) {
		this.firstTime = firstTime;
	}
	
	public long getLastTime() {
		return lastTime;
	}


	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}


	public Xid getXid() {
		if(gid != null && bid != null)
			return new MysqlXid(gid, bid, formateId);
		else
			return null;
	}
	

	public void setXid(Xid xid) {
		if(xid != null)
		{
			this.gid = xid.getGlobalTransactionId();
			this.bid = xid.getBranchQualifier();
			this.formateId = xid.getFormatId();
		}
	}


	public byte getStatus() {
		return status;
	}


	public void setStatus(byte status) {
		this.status = status;
	}


	public byte getOperation() {
		return operation;
	}


	public void setOperation(byte operation) {
		this.operation = operation;
	}


	public boolean isExist() {
		return isExist;
	}


	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}
	
	

}
