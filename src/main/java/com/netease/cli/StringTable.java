package com.netease.cli;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.Map.Entry;

import org.nfunk.jep.JEP;


public final class StringTable implements Serializable {
	
	private static final long serialVersionUID = -3370210994843463703L;
	
	private int numColumns;
	
	private List<String[]> data;
	
	private String name;
	
	private String[] header;

	
	public StringTable(String name, String[] header) {
		if (header.length == 0)
			throw new IllegalArgumentException("Must have at least one column.");
		this.name = name;
		this.numColumns = header.length;
		this.header = header.clone();
		data = new LinkedList<String[]>();
	}

	
	public void addRow(String[] aRow) {
		if (aRow.length != numColumns)
			throw new IllegalArgumentException("Invalid column number, should be: " + numColumns
				+ ", but was: " + aRow.length);
		String[] rowCopy = aRow.clone();
		for (int i = 0; i < rowCopy.length; i++) {
			if (rowCopy[i] == null)
				rowCopy[i] = "\\N";
		}
		data.add(rowCopy);
	}

	
	public void addRows(Collection<String[]> rows) {
		for (String[] row : rows)
			addRow(row);
	}

	public List<String[]> getData() {
		return data;
	}

	public String[] getHeader() {
		return header;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public String getName() {
		return name;
	}

	
	public StringTable duplicate() {
		StringTable newTable = new StringTable(name, header.clone());
		newTable.addRows(data);
		return newTable;
	}

	
	public StringTable addColumn(String name, String value, int pos) {
		if (pos > header.length)
			pos = header.length;
		header = addCell(header, name, pos);
		ListIterator<String[]> iter = data.listIterator();
		while (iter.hasNext()) {
			String[] row = iter.next();
			iter.set(addCell(row, value, pos));
		}
		numColumns++;
		return this;
	}

	
	public StringTable sort(String[] columns, boolean[] asc) {
		int[] columnIndice = mapColumns(columns);
		Collections.sort(data, new TableComparator(columnIndice, asc));
		return this;
	}

	private int[] mapColumns(String[] columns) {
		int[] columnIndice = new int[columns.length];
		for (int c = 0; c < columns.length; c++) {
			int i;
			for (i = 0; i < header.length; i++)
				if (header[i].equalsIgnoreCase(columns[c])) {
					columnIndice[c] = i;
					break;
				}
			if (i == header.length)
				throw new IllegalArgumentException("Column '" + columns[c] + "' not found.");
		}
		return columnIndice;
	}

	
	public StringTable filter(String cond) {
		StringTable result = new StringTable(name, header);
		
		for (String[] row : data) {
			JEP ep = new JEP();
			ep.addStandardConstants();
			ep.addStandardFunctions();
			for (int c = 0; c < row.length; c++) {
				try {
					double v = Double.parseDouble(row[c]);
					ep.addVariable(header[c], v);
				} catch (NumberFormatException e) {
					ep.addVariable(header[c], row[c]);
				}
			}
			ep.parseExpression(cond);
			if (ep.hasError())
				throw new IllegalArgumentException("Invalid condition(" + cond + "): "
					+ ep.getErrorInfo());
			boolean isTrue = ep.getValue() != 0;
			if (ep.hasError())
				throw new RuntimeException("Error in evaluating condition(" + cond + "): "
					+ ep.getErrorInfo());
			if (isTrue)
				result.addRow(row);
		}
		return result;
	}
	
	
	public StringTable group(String[] columns) {
		HashMap<Vector<Object>, Integer> grpCntMap = new HashMap<Vector<Object>, Integer>();
		int[] columnIndice = mapColumns(columns);
		for (String[] row : data) {
			Vector<Object> key = new Vector<Object>(columns.length);
			for (int i = 0; i < columnIndice.length; i++) {
				String c = row[columnIndice[i]];
				try {
					try {
						long v = Long.parseLong(c);
						key.add(v);
					} catch (NumberFormatException e) {
						double v = Double.parseDouble(c);
						key.add(v);
					}
				} catch (NumberFormatException e) {
					key.add(c);
				}
			}
			Integer oldCnt = grpCntMap.get(key);
			if (oldCnt == null)
				grpCntMap.put(key, 1);
			else
				grpCntMap.put(key, oldCnt + 1);
		}
		String[] newColumns = new String[columns.length + 1];
		for (int i = 0; i < columnIndice.length; i++)
			newColumns[i] = header[columnIndice[i]];
		newColumns[newColumns.length - 1] = "COUNT";
		
		StringTable result = new StringTable(name, newColumns);
		for (Entry<Vector<Object>, Integer> e : grpCntMap.entrySet()) {
			String[] row = new String[newColumns.length];
			Vector<Object> v = e.getKey();
			for (int i = 0; i < columnIndice.length; i++)
				row[i] = v.get(i).toString();
			row[row.length - 1] = e.getValue().toString();
			result.addRow(row);
		}
		return result;
	}
	
	private String[] addCell(String[] row, String cell, int pos) {
		String[] newRow = new String[row.length + 1];
		for (int i = 0; i < pos; i++)
			newRow[i] = row[i];
		newRow[pos] = cell;
		for (int i = pos; i < row.length; i++)
			newRow[i + 1] = row[i];
		return newRow;
	}

	class TableComparator implements Comparator<String[]> {
		private int[] columns;
		private boolean[] asc;

		TableComparator(int[] columns, boolean[] asc) {
			this.columns = columns;
			this.asc = asc;
		}

		public int compare(String[] r1, String[] r2) {
			for (int i = 0; i < columns.length; i++) {
				int r = r1[columns[i]].compareTo(r2[columns[i]]) * (asc[i] ? 1 : -1);
				if (r != 0)
					return r;
			}
			return 0;
		}
	}
}
