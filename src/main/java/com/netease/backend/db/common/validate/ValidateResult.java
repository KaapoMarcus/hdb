
package com.netease.backend.db.common.validate;

import com.netease.backend.db.common.validate.impl.CompareResult;


public class ValidateResult {
	private static final long	serialVersionUID	= 1669376971417954981L;

	private CompareResult		lastCompareResult;
	private int					rows;
	private Exception			error;

	public ValidateResult() {
	}

	
	public ValidateResult(CompareResult cr) {
		this(cr, null);
	}

	
	public ValidateResult(Exception error) {
		this(null, error);
	}

	
	public ValidateResult(CompareResult cr, Exception err) {
		lastCompareResult = cr;
		error = err;
	}

	
	public int getRows() {
		return rows;
	}

	
	public void setRows(int rows) {
		this.rows = rows;
	}

	
	public void setLastCompareResult(CompareResult compareResult) {
		lastCompareResult = compareResult;
	}

	
	public void setError(Exception error) {
		this.error = error;
	}

	
	public CompareResult getLastCompareResult() {
		return lastCompareResult;
	}

	
	public Exception getError() {
		return error;
	}

	
	public int getLastRow() {
		final CompareResult r;
		return (r = this.getLastCompareResult()) != null ? r.getRow() : -1;
	}

	
	public String getLastRowKey() {
		final CompareResult r;
		return (r = this.getLastCompareResult()) != null ? r.getRowKey() : null;
	}

	
	public boolean isLastMatched() {
		final CompareResult r;
		return ((r = this.getLastCompareResult()) != null) && r.isMatched();
	}

	@Override
	public String toString() {
		final String SEP = ", ";

		final StringBuilder ss = new StringBuilder();
		ss.append("ValidateResult [");
		ss.append("rows=").append(rows);
		ss.append(SEP).append("lastCompareResult=").append(lastCompareResult);
		ss.append(SEP).append("error=").append(error);

		ss.append("]");

		return ss.toString();
	}

}
