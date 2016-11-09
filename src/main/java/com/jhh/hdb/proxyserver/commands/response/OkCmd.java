package com.jhh.hdb.proxyserver.commands.response;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmdType;

/**
 * ִ�гɹ����ذ�
 * field_count:     always = 0
 
 affected_rows:   = number of rows affected by INSERT/UPDATE/DELETE
 
 insert_id:       If the statement generated any AUTO_INCREMENT number, 
                  it is returned here. Otherwise this field contains 0.
                  Note: when using for example a multiple row INSERT the
                  insert_id will be from the first row inserted, not from
                  last.
 
 server_status:   = The client can use this to check if the
                  command was inside a transaction.
 
 warning_count:   number of warnings
 
 message:         For example, after a multi-line INSERT, message might be
                  "Records: 3 Duplicates: 0 Warnings: 0"
 

 * 
 *
 */
public class OkCmd extends MySQLCmd {
	private short fieldCount;
	private long affectedRows;
	private long insertId;
	private short serverStatus;
	private short warningCount;
	private String message;

	public OkCmd(short fieldCount, long affectedRows, long insertId,
			short serverStatus, short warningCount, String message) {
		super();
		this.fieldCount = fieldCount;
		this.affectedRows = affectedRows;
		this.insertId = insertId;
		this.serverStatus = serverStatus;
		this.warningCount = warningCount;
		this.message = message;
	}

	/**
	 * �򻯰湹�캯��
	 * 	this.fieldCount = 0;
	 * 	this.affectedRows = 0;
	 * 	this.insertId = 0;

	 * @param serverStatus
	 * @param message
	 */
	public OkCmd(short serverStatus, String message) {
		super();
		this.fieldCount = 0;
		this.affectedRows = 0;
		this.insertId = 0;
		this.warningCount = 0;
		this.serverStatus = serverStatus;
		this.message = message;
	}

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.ok;
	}

	public short getFieldCount() {
		return fieldCount;
	}

	public void setFieldCount(short fieldCount) {
		this.fieldCount = fieldCount;
	}

	public long getAffectedRows() {
		return affectedRows;
	}

	public void setAffectedRows(long affectedRows) {
		this.affectedRows = affectedRows;
	}

	public long getInsertId() {
		return insertId;
	}

	public void setInsertId(long insertId) {
		this.insertId = insertId;
	}

	public short getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(short serverStatus) {
		this.serverStatus = serverStatus;
	}

	public short getWarningCount() {
		return warningCount;
	}

	public void setWarningCount(short warningCount) {
		this.warningCount = warningCount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[OkCmd] AffectedRows:");
		sb.append(this.affectedRows);
		sb.append(", InsertId:");
		sb.append(this.insertId);
		sb.append(", serverStatus:");
		sb.append(this.serverStatus);
		sb.append(", Message:");
		sb.append(this.message);
		return sb.toString();
	}

}
