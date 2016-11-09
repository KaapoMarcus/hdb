package com.netease.backend.db.common.validate.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import com.netease.backend.db.common.utils.ResultSetFormatter;
import com.netease.cli.StringTable;


public class RSFormatter
{

	
	public static String writeStringTable(
		StringTable st, boolean ignoreHead, char delimiter, char quoter)
	{
		final StringWriter out = new StringWriter(512);
		try {
			writeStringTable(out, st, ignoreHead, delimiter, quoter);
		} catch (final IOException ex ) {
		}
		return out.toString();
	}

	
	public static String retrieveColumnValue(
		StringTable st, String columnName, int row)
	{
		if ((st == null) || (columnName == null))
			throw new NullPointerException("String table or column name should not be null");
		if ((row < 0) || (row >= countRows(st)))
			throw new IllegalArgumentException("Illegal row number");

		String v = null;

		final int i;
		if ((i = getColumnIndex(st, columnName)) >= 0) {
			final String[] r;
			if ((r = st.getData().get(row)).length > i) {
				v = r[i];
			}
		}

		return v;
	}

	
	public static int getColumnIndex(
		StringTable st, String columnName)
	{
		if ((st == null) || (columnName == null))
			throw new NullPointerException("String table or column name should not be null");
		int index = -1;
		final String[] h = st.getHeader();
		for (int i = 0; i < st.getNumColumns(); i++) {
			if (columnName.equals(ColumnHeader.fromHeaderStr(h[i]).columnName)) {
				index = i;
				break;
			}
		}
		return index;
	}

	
	public static int countRows(
		StringTable st)
	{
		final List<?> d;
		return (d = st.getData()) != null ? d.size() : 0;
	}

	
	public static void disposeStringTable(
		StringTable st)
	{
		final String[] h = st.getHeader();
		for (int i = 0; i < st.getNumColumns(); i++) {
			h[i] = null;
		}
		if (countRows(st) > 0) {
			final List<String[]> d = st.getData();
			final Iterator<String[]> i = d.iterator();
			while (i.hasNext()) {
				final String[] r = i.next();
				for (int j = 0; j < r.length; j++) {
					r[j] = null;
				}
				
			}
			d.clear();
		}
	}

	
	public static CompareResult compareStringTable(
		StringTable m, StringTable n, int type, String key)
	{
		boolean matched = false;
		String rowKey = null;
		final int p;
		if ((p = compareStringTable(m, n, type)) == -1) {
			
		} else if (p < countRows(m)) {
			
			rowKey = retrieveColumnValue(m, key, p);
		} else if (p < countRows(n)) {
			
			rowKey = retrieveColumnValue(n, key, p);
		} else {
			
			matched = true;
			
			if (p > 0) {
				rowKey = retrieveColumnValue(m, key, p - 1);
			}
		}
		return new CompareResult(matched, matched ? (p - 1) : p, rowKey);
	}

	
	public static int compareStringTable(
		StringTable m, StringTable n, int type)
	{
		
		final HeaderDiff diff = compareStringTableHeader(m.getHeader(), n
				.getHeader(), type);
		if (!diff.matched)
			return -1;
		
		final Iterator<String[]> im = m.getData().iterator();
		final Iterator<String[]> in = n.getData().iterator();
		int row = 0;
		CORE: for (;;) {
			if (im.hasNext()) {
				if (!in.hasNext()
						|| !compareRow(im.next(), in.next(), diff.meta,
								diff.count)) {
					break CORE;
				}
			} else {
				
				
				break CORE;
			}
			row++;
		}
		return row;
	}

	private static boolean compareRow(String[] a, String[] b, int[][] meta,
			int count) {
		if (a.length <= meta[0][count - 1] || b.length <= meta[1][count - 1])
			throw new IndexOutOfBoundsException();

		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		
		for (int i = 0; i < count; i++) {
			final String va = a[meta[0][i]];
			final String vb = b[meta[1][i]];
			if (!(va == null ? vb == null : va.equals(vb)))
				return false;
		}

		return true;
	}
	
	private static final class HeaderDiff {
		final boolean	matched;
		final int		count;
		final int[][]	meta;
		public HeaderDiff(boolean matched, int[][] meta, int count) {
			this.matched = matched;
			this.meta = meta;
			this.count = count;
		}
	}

	
	private static HeaderDiff compareStringTableHeader(
			String[] hm, String[] hn, int type) 
	{
		final int[][] meta = new int[2][hm.length];
		int p = 0;

		boolean matched = false;

		OUTER: for (;;) {
			
			if (hm.length == 0 || hn.length == 0) {
				break OUTER;
			}

			
			final boolean intersected = CompareType.isIntersectType(type);

			
			if (!intersected && (hm.length != hn.length)) {
				break OUTER;
			}

			
			int i = 0, j = 0;
			FIRSTROUND: for (; i < hm.length; i++) {
				final ColumnHeader hcm = ColumnHeader.fromHeaderStr(hm[i]);
				j = 0;
				for (; j < hn.length; j++) {
					if (hcm.columnName
							.equals(ColumnHeader.fromHeaderStr(hn[j]).columnName))
						break FIRSTROUND;
				}
			}

			if (i == hm.length || j == hn.length) {
				
				matched = intersected;
				break OUTER;
			}

			
			COMPARE: for (; i < hm.length && j < hn.length; i++, j++) {
				final ColumnHeader hcm = ColumnHeader.fromHeaderStr(hm[i]);
				ColumnHeader hcn;
				while (!hcm.columnName.equals((hcn = ColumnHeader
						.fromHeaderStr(hn[j])).columnName)) {
					
					if (!intersected)
						break OUTER;
					j++;
					
					if (j == hn.length)
						break COMPARE;
				}

				switch (CompareType.getBaseOption(type)) {
				case CompareType.STRICT:
					if (hcm.columnType != hcn.columnType)
						break OUTER;
				case CompareType.BASE_TYPE:
					if (hcm.columnRefinedType != hcn.columnRefinedType)
						break OUTER;
				case CompareType.COLUMN_NAME_ONLY:
					
					meta[0][p] = i;
					meta[1][p] = j;
					p++;
					continue COMPARE;
					
					
				default:
					break OUTER;
				}
			}

			matched = true;
			break OUTER;
		}

		return new HeaderDiff(matched, meta, p);
	}

	
	public static int writeStringTable(
		Writer out, StringTable st, boolean ignoreHead, char delimiter,
		char quoter)
		throws IOException
	{
		
		if (!ignoreHead) {
			final String[] h = st.getHeader();
			for (int i = 0; i < st.getNumColumns(); i++) {
				if (i != 0)
					out.write(delimiter);
				out.write(h[i]);
			}
			out.write('\n');
			out.flush();
		}

		
		int rows = 0;
		for (final String[] r : st.getData()) {
			for (int i = 0; i < st.getNumColumns(); i++) {
				if (i != 0)
					out.write(delimiter);
				final ColumnHeader h = ColumnHeader.fromHeaderStr(st.getHeader()[i]);
				final boolean quote = (h.columnRefinedType != 0 )
						&& (quoter != '\0');
				if (quote) {
					out.write(quoter);
				}
				out.write(r[i]);
				if (quote) {
					out.write(quoter);
				}
			}
			out.write('\n');
			out.flush();
			rows++;
		}
		return rows;
	}

	
	public static String writeResultSet(
		ResultSet rs, boolean ignoreHeader, int skipRows, int limit,
		char delimiter, char quoter)
		throws SQLException
	{
		return writeStringTable(convertResultSet(rs, skipRows, limit), ignoreHeader, delimiter, quoter);
	}

	
	public static int writeResultSet(
		Writer out, ResultSet rs, boolean ignoreHeader, int skipRows,
		int limit, char delimiter, char quoter)
		throws SQLException, IOException
	{
		final StringTable st = convertResultSet(rs, skipRows, limit);
		writeStringTable(out, st, ignoreHeader, delimiter, quoter);
		return st.getData() != null ? st.getData().size() : 0;
	}

	
	public static StringTable convertResultSet(
		ResultSet rs, int skipRows, int limit)
		throws SQLException
	{
		final StringBuilder ss = new StringBuilder(256);
		
		final ResultSetMetaData meta = rs.getMetaData();

		
		final String[] columns = new String[meta.getColumnCount()];
		
		for (int i = 0; i < meta.getColumnCount(); i++) {
			
			ss.setLength(0);

			
			ss.append(meta.getColumnName(i + 1));
			
			final int type = meta.getColumnType(i + 1);
			ss.append(':').append(type);
			
			ss.append(':').append(isNumberType(type) ? 0 : (!isBinaryType(type) ? 1 : 2));
			columns[i] = ss.toString();
		}
		final StringTable st = new StringTable("RS", columns);

		
		int k = 0, rows = 0;
		while (rs.next()) {
			if (k++ < skipRows) {
				
				continue;
			}
			final String[] r = new String[st.getNumColumns()];
			for (int i = 0; i < st.getNumColumns(); i++) {
				final ColumnHeader h = ColumnHeader.fromHeaderStr(st.getHeader()[i]);

				if (h.columnRefinedType == 2 ) {
					
					try {
						final byte[] b;
						r[i] = ((b = rs.getBytes(i + 1)) != null) ? new String(b, "ISO-8859-1") : "\\N";
					} catch (final UnsupportedEncodingException ex ) {
					}
				} else {
					
					r[i] = ResultSetFormatter.formatDatem(rs, i + 1, h.columnType, null, true, '\0');
					if (r == null )
						throw new SQLException("invalid data type of '"
								+ h.columnName + "'");
				}
			}
			st.addRow(r);

			if ((limit > 0) && (++rows >= limit)) {
				
				break;
			}
		}

		return st;
	}

	
	public static final class ColumnHeader
	{
		final String	columnName;
		final int		columnType;
		final int		columnRefinedType;
		boolean			ignored	= false;

		ColumnHeader(String columnName, int columnType, int columnRefinedType) {
			super();
			this.columnName = columnName;
			this.columnType = columnType;
			this.columnRefinedType = columnRefinedType;
		}

		
		static ColumnHeader fromHeaderStr(
			String headerStr)
		{
			int i, p;
			
			p = -1;
			p = headerStr.indexOf(':', (i = p + 1));
			if (p < 0)
				throw new IllegalArgumentException();
			final String columnName = headerStr.substring(i, p);
			
			p = headerStr.indexOf(':', (i = p + 1));
			if (p < 0)
				throw new IllegalArgumentException();
			final int columnType = Integer.valueOf(headerStr.substring(i, p));
			
			final int columnRefinedType = Integer.valueOf(headerStr
					.substring(i = p + 1));
			return new ColumnHeader(columnName, columnType, columnRefinedType);
		}
	}

	
	protected static boolean isBinaryType(
		int columnType)
	{
		return (columnType == Types.BINARY) || (columnType == Types.VARBINARY)
				|| (columnType == Types.BLOB)
				|| (columnType == Types.LONGVARBINARY);
	}

	
	protected static boolean isNumberType(
		int type)
	{
		return (type == Types.INTEGER) || (type == Types.SMALLINT)
				|| (type == Types.TINYINT) || (type == Types.BIGINT)
				|| (type == Types.FLOAT) || (type == Types.DOUBLE)
				|| (type == Types.REAL) || (type == Types.BOOLEAN)
				|| (type == Types.NUMERIC) || (type == Types.DECIMAL)
				|| (type == Types.BIT);
	}

}
