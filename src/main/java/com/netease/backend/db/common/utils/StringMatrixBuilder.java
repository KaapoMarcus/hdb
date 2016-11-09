package com.netease.backend.db.common.utils;


public class StringMatrixBuilder
{
	
	private StringMatrix	root;

	
	public static StringMatrix emptyStringMatrix(
		String name)
	{
		return new StringMatrix(name, new String[] { "" });
	}

	public static StringMatrix singleStringMatrix(
		String name, String column, String... rows)
	{
		final String[] header = new String[] { column };
		final StringMatrix m = new StringMatrix(name, header);
		if (rows != null) {
			for (final String s : rows) {
				final String[] row = new String[] { s };
				m.addRow(row);
			}
		}
		return m;
	}

	
	public StringMatrixBuilder() {
		root = null;
	}

	
	public StringMatrixBuilder(StringMatrix root) {
		this.root = root;
	}

	
	public StringMatrixBuilder add(
		StringMatrix sm)
	{
		if (root == null) {
			root = sm;
		} else {
			root.addChild(sm);
		}
		return this;
	}

	
	public StringMatrixBuilder add(
		StringMatrix sm, StringMatrix parent)
	{
		parent.addChild(sm);
		if (!this.isRoot(parent) && !this.contains(parent)) {
			this.add(parent);
		}
		return this;
	}

	
	private boolean contains(
		StringMatrix sm)
	{
		return (root != null) && root.hasChild(sm.getName(), true);
	}

	
	protected boolean isRoot(
		StringMatrix sm)
	{
		return (root != null)
				&& ((root == sm) || root.getName().equals(sm.getName()));
	}

	public StringMatrix toStringMatrix()
	{
		return root;
	}

}
