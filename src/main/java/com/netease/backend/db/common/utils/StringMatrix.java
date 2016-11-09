package com.netease.backend.db.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netease.cli.StringTable;
import com.netease.cli.TableFormatter;


public class StringMatrix implements Serializable, Cloneable {
	
	private static final long				serialVersionUID	= 7121880361307478354L;

	
	private StringTable						proxy;
	
	private final Map<String, StringMatrix>	children;
	
	private Observer						observer;

	
	public static StringMatrix fromStringTable(
		StringTable t)
	{
		return fromStringTable(t, t.getName());
	}

	
	public static StringMatrix fromStringTable(
		StringTable t, String name)
	{
		final StringMatrix m = new StringMatrix(name, t.getHeader());
		m.data().addAll(t.getData());
		return m;
	}

	
	public StringMatrix(
		String name,
		String... header)
	{
		this(new StringTable(name, header));
	}

	
	protected StringMatrix(
		StringTable proxy)
	{
		this.proxy = proxy;
		children = new HashMap<String, StringMatrix>();
		
		
		
		
		
		
		
		
	}

	
	public StringMatrix(
		String name,
		List<String> header)
	{
		this(name, (String[]) header.toArray());
	}

	
	protected StringTable proxy() {
		return proxy;
	}

	
	public StringMatrix[] getChildren() {
		final Collection<StringMatrix> chren = this.children().values();
		return chren.toArray(new StringMatrix[chren.size()]);
	}

	
	protected Map<String, StringMatrix> children() {
		return children;
	}

	
	public void addChild(
		StringMatrix chd)
	{
		this.children().put(chd.getName(), chd);
	}

	
	public void addChildren(
		Collection<StringMatrix> chren)
	{
		for (final StringMatrix m : chren) {
			this.addChild(m);
		}
	}

	
	public boolean hasChild(
		String name, boolean deep)
	{
		return this.getChild(name, deep) != null;
	}

	
	public StringMatrix getChild(
		String name, boolean deep)
	{
		StringMatrix sm = null;
		final Map<String, StringMatrix> chren = this.children();
		if ((sm = chren.get(name)) != null) {
			return sm;
		}
		if (deep && (chren.size() > 0)) {
			for (StringMatrix chd : chren.values()) {
				if ((sm = chd.getChild(name, true)) != null)
					break;
			}
		}
		return sm;
	}

	
	public void dispose() {
		final Map<String, StringMatrix> chren = this.children();
		for (final StringMatrix m : chren.values()) {
			m.dispose();
		}
		chren.clear();
		disposeStringTable(proxy);
	}

	
	protected static void disposeStringTable(
		StringTable t)
	{
		Arrays.fill(t.getHeader(), null);
		final List<String[]> rows;
		if ((rows = t.getData()) != null) {
			for (String[] row : rows) {
				Arrays.fill(row, null);
			}
			rows.clear();
		}
	}

	private static final TableFormatter	FORMATTER;

	static {
		FORMATTER = new TableFormatter();
		
		
	}

	protected static void writeMatrix(
		StringMatrix m, PrintStream s)
	{
		FORMATTER.print(m.proxy(), s);
	}

	protected static String writeAsString(
		StringMatrix m, boolean ignoreChild)
	{
		final StringBuilder ss = new StringBuilder();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		final PrintStream ps = new PrintStream(os);
		writeMatrix(m, ps);
		ps.println();
		ss.append(os.toString());
		if (!ignoreChild) {
			for (final StringMatrix child : m.children().values()) {
				ss.append(child.toString());
			}
		}
		return ss.toString();
	}

	@Override
	public String toString() {
		return writeAsString(this, false);
	}

	
	@Override
	protected StringMatrix clone() {
		StringMatrix copy = null;
		try {
			copy = (StringMatrix) super.clone();
		} catch (final CloneNotSupportedException ex) {
		}
		if (copy != null) {
			copy.proxy = proxy.duplicate();
		}
		copy.addChildren(this.children().values());
		return copy;
	}

	

	public void addRow(
		String[] row)
	{
		this.data().add(row);

		final Observer obr;
		if ((obr = this.getObserver()) != null) {
			obr.onRowAdded(row);
		}
	}

	public void addRows(
		Collection<String[]> rows)
	{
		if ((rows != null) && (rows.size() > 0)) {
			for (final String[] row : rows) {
				this.addRow(row);
			}
		}
	}

	public String[] removeRow(
		int i)
	{
		if ((i < 0) && (i >= this.numRows()))
			throw new IllegalArgumentException();
		final String[] row = this.data().remove(i);

		final Observer obr;
		if ((obr = this.getObserver()) != null) {
			obr.onRowRemoved(row);
		}

		return row;
	}

	public List<String[]> truncate() {
		final List<String[]> data = this.data();
		final List<String[]> old = new ArrayList<String[]>(data.size());
		Collections.copy(old, data);

		data.clear();

		final Observer obr;
		if ((obr = this.getObserver()) != null) {
			obr.onClear(old);
		}

		return old;
	}

	public String[] setRow(
		int i, String[] row)
	{
		if ((i < 0) && (i >= this.numRows()))
			throw new IllegalArgumentException();
		final String[] old = this.data().set(i, row);

		final Observer obr;
		if ((obr = this.getObserver()) != null) {
			obr.onRowChanged(old, row);
		}

		return old;
	}

	
	public String[][] getData() {
		return ArrayUtils.copyOf((String[][]) this.data().toArray());
	}

	
	protected List<String[]> data() {
		return proxy.getData();
	}

	
	public String[] getHeader() {
		final String[] header = this.header();
		return ArrayUtils.copyOf(header, header.length);
	}

	
	protected String[] header() {
		return proxy.getHeader();
	}

	public String getName() {
		return proxy.getName();
	}

	public int numColumns() {
		return this.header().length;
	}

	public int numRows() {
		return this.data().size();
	}

	public StringTable group(
		String[] columns)
	{
		return proxy.group(columns);
	}

	public StringTable filter(
		String column)
	{
		return proxy.filter(column);
	}

	public StringTable sort(
		String[] columns, boolean[] asc)
	{
		return proxy.sort(columns, asc);
	}

	public StringTable addColumn(
		String name, String value, int pos)
	{
		return proxy.addColumn(name, value, pos);
	}

	public Observer getObserver() {
		return observer;
	}

	public void setObserver(
		Observer observer)
	{
		this.observer = observer;
	}

	
	public static interface Observer extends Serializable {
		void onRowAdded(
			String[] row);

		void onRowRemoved(
			String[] rows);

		void onRowChanged(
			String[] oldRow, String[] newRow);

		void onClear(
			List<String[]> rows);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
