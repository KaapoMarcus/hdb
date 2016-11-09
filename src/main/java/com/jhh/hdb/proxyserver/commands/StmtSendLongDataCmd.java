package com.jhh.hdb.proxyserver.commands;

/**
 * 发送preparestatement的长类型参数，blog等
 * 
 *
 */
public class StmtSendLongDataCmd extends CommandCmd {

	private int statementID;
	private short parameterNumber;
	private byte[] payload;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.stmtLongdata;
	}

	public int getStatementID() {
		return statementID;
	}

	public void setStatementID(int statementID) {
		this.statementID = statementID;
	}

	public short getParameterNumber() {
		return parameterNumber;
	}

	public void setParameterNumber(short parameterNumber) {
		this.parameterNumber = parameterNumber;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[StmtSendLongDataCmd] ");
		sb.append("StatementId:").append(this.statementID);
		sb.append(", ParameterNumber:").append(this.parameterNumber);
		sb.append(", Payload length:").append(
				this.payload == null ? 0 : this.payload.length);
		return sb.toString();
	}
}
