package com.jhh.hdb.proxyserver.commands;

/**
 * Fetch result rows from a prepared statement
 * 
 *
 */
public class StmtFetchCmd extends CommandCmd {

	private int statementId;
	private int fetchRow;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.stmtFetch;
	}

	public int getStatementId() {
		return statementId;
	}

	public void setStatementId(int statementId) {
		this.statementId = statementId;
	}

	public int getFetchRow() {
		return fetchRow;
	}

	public void setFetchRow(int fetchRow) {
		this.fetchRow = fetchRow;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[StmtFetchCmd] ");
		sb.append("StatementId:").append(this.statementId);
		sb.append(", FecthRow:").append(this.fetchRow);
		return sb.toString();
	}
}
