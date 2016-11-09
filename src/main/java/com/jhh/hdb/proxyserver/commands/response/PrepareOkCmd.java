package com.jhh.hdb.proxyserver.commands.response;

import java.sql.ResultSetMetaData;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmdType;

/**
 * prepare�ɹ����ذ�
 * OK for Prepared Statement Initialization Packet 
From server to client, in response to prepared statement initialization packet. 

It is made up of: 

a PREPARE_OK packet 
if "number of parameters" > 0 
(field packets) as in a Result Set Header Packet 
(EOF packet) 
if "number of columns" > 0 
(field packets) as in a Result Set Header Packet 
(EOF packet) 
The PREPARE_OK packet is: 

 Bytes              Name
 -----              ----
 1                  0 - marker for OK packet
 4                  statement_handler_id
 2                  number of columns in result set
 2                  number of parameters in query
 1                  filler (always 0)
 2                  warning count

 * 
 *
 */
public class PrepareOkCmd extends MySQLCmd {
	private int statementId;
	private short parameterCount;
	private ResultSetMetaData resultSetMetaData;
	private short serverStatus;
	private short warningCount = 0;

	public MySQLCmdType getType() {
		return MySQLCmdType.prepareok;
	}

	public PrepareOkCmd(int statementId, short parameterCount, ResultSetMetaData rsMetaData, short serverStatus) {
		super();
		this.statementId = statementId;
		this.resultSetMetaData = rsMetaData;
		this.parameterCount = parameterCount;
		this.serverStatus = serverStatus;
	}

	public int getStatementId() {
		return statementId;
	}

	public void setStatementId(int statementId) {
		this.statementId = statementId;
	}

	public short getParameterCount() {
		return parameterCount;
	}

	public void setParameterCount(short parameterCount) {
		this.parameterCount = parameterCount;
	}

	public short getWarningCount() {
		return warningCount;
	}

	public void setWarningCount(short warningCount) {
		this.warningCount = warningCount;
	}
	
	public short getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(short serverStatus) {
		this.serverStatus = serverStatus;
	}
	
	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}

	public void setResultSetMetaData(ResultSetMetaData rsMetaData) {
		resultSetMetaData = rsMetaData;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[PrepareOkCmd] ");
		sb.append("StatementId:").append(this.statementId);
		sb.append(", ResultSetMetaData:").append(this.resultSetMetaData == null ? "null" : "not null");
		sb.append(", ParameterCount:").append(this.parameterCount);
		sb.append(", ServerStatus:").append(this.serverStatus);
		return sb.toString();
	}
}
