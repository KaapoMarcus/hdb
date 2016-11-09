package com.netease.backend.db.common.utils;

import java.util.List;


public class RelatedStringMatrix
	extends StringMatrix
{
	private static final long	serialVersionUID	= -5099947401935003073L;

	public RelatedStringMatrix(String name, List<String> header) {
		super(name, header);
	}

	public RelatedStringMatrix(String name, String... header) {
		super(name, header);
	}

	@Override
	public void addChild(
		StringMatrix child)
	{
		final StringMatrix chd = child;
		try {
			super.addChild(chd);
			for (final String[] row : chd.data()) {
				this.onChildRowAdded(chd.getName(), chd.getHeader(), row);
			}
		} finally {
			chd.setObserver(new Observer() {
				private static final long	serialVersionUID	= 4422628526443304980L;

				public void onRowAdded(
					String[] row)
				{
					RelatedStringMatrix.this.onChildRowAdded(chd.getName(), chd.header(), row);
				}

				public void onRowChanged(
					String[] oldRow, String[] newRow)
				{
					RelatedStringMatrix.this.onChildRowChanged(chd.getName(), chd.header(), oldRow, newRow);
				}

				public void onRowRemoved(
					String[] row)
				{
					RelatedStringMatrix.this.onChildRowRemoved(chd.getName(), chd.header(), row);
				}

				public void onClear(
					List<String[]> rows)
				{
					RelatedStringMatrix.this.onChildDataCleared(chd.getName(), chd.header(), rows);
				}
			});
		}
	}

	protected void onChildRowAdded(
		String name, String[] header, String[] row)
	{
		
	}

	protected void onChildRowRemoved(
		String name, String[] header, String[] rows)
	{
		
	}

	protected void onChildRowChanged(
		String name, String[] header, String[] oldRow, String[] newRow)
	{
		
	}

	protected void onChildDataCleared(
		String name, String[] header, List<String[]> rows)
	{
		
	}
}
