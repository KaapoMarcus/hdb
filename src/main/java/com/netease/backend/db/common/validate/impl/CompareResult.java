package com.netease.backend.db.common.validate.impl;

import java.io.Serializable;


public class CompareResult
	implements Serializable
{
	private static final long	serialVersionUID	= -2652445384453589471L;

	private final boolean		matched;
	private final int			row;
	private final String		rowKey;

	
	public CompareResult(boolean matched, int row, String rowKey) {
		this.matched = matched;
		this.row = row;
		this.rowKey = rowKey;
	}

	
	public boolean isMatched()
	{
		return matched;
	}

	
	public String getRowKey()
	{
		return rowKey;
	}

	
	public int getRow()
	{
		return row;
	}

	@Override
	public String toString()
	{
		final String SEP = ", ";

		final StringBuilder retValue = new StringBuilder();
		retValue.append("CompareResult [");

		retValue.append("matched=").append(matched);
		retValue.append(SEP).append("row=").append(row);
		retValue.append(SEP).append("rowKey=").append(rowKey);

		retValue.append("]");

		return retValue.toString();
	}

}
