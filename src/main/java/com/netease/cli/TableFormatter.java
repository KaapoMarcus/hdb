
package com.netease.cli;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Vector;


public class TableFormatter {
	public static final int WITH_BOX_YES = 1;
	public static final int WITH_BOX_NO = 2;
	public static final int WITH_BOX_AUTO = 3;
	
	public static final int ALIGN_LEFT = 1;
	
	public static final int ALIGN_RIGHT = 2;
	
	public static final int ALIGN_MIDDLE = 3;
	
	
	private char columnDelimiter = '|';
	
	private char rowDelimiter = '-';
	
	private char rowColumnCross = '+';
	
	private char lineContinuation = '\\';
	
	private boolean withCaption = true;
	
	private boolean withHeader = true;
	
	private int withBox = WITH_BOX_YES;
	
	private ArrayList<Integer> aligns;
	
	private boolean vertical = false;
	
	public TableFormatter() {
		aligns = new ArrayList<Integer>(8);
		for (int i = 0; i < 8; i++)
			aligns.add(ALIGN_LEFT);
	}
	
	public char getColumnDelimiter() {
		return columnDelimiter;
	}

	public TableFormatter setColumnDelimiter(char columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
		return this;
	}

	public char getRowColumnCross() {
		return rowColumnCross;
	}

	public TableFormatter setRowColumnCross(char rowColumnCross) {
		this.rowColumnCross = rowColumnCross;
		return this;
	}

	public char getRowDelimiter() {
		return rowDelimiter;
	}

	public TableFormatter setRowDelimiter(char rowDelimiter) {
		this.rowDelimiter = rowDelimiter;
		return this;
	}

	public char getLineContinuation() {
		return lineContinuation;
	}

	public TableFormatter setLineContinuation(char lineContinuation) {
		this.lineContinuation = lineContinuation;
		return this;
	}

	public int getWithBox() {
		return withBox;
	}

	public TableFormatter setWithBox(int withBox) {
		this.withBox = withBox;
		return this;
	}

	public boolean isWithCaption() {
		return withCaption;
	}

	public TableFormatter setWithCaption(boolean withCaption) {
		this.withCaption = withCaption;
		return this;
	}

	public boolean isWithHeader() {
		return withHeader;
	}

	public TableFormatter setWithHeader(boolean withHeader) {
		this.withHeader = withHeader;
		return this;
	}
	
	
	public int getAlignment(int column) {
		if (column >= aligns.size())
			return ALIGN_LEFT;
		else
			return aligns.get(column);
	}
	
	
	public TableFormatter setAlignment(int column, int align) {
		if (align != ALIGN_LEFT && align != ALIGN_MIDDLE && align != ALIGN_RIGHT)
			throw new IllegalArgumentException("Invalid value for alignment: " + align);
		if (column >= aligns.size()) {
			int newSize = aligns.size() * 2;
			while (newSize <= column)
				newSize *= 2;
			aligns.ensureCapacity(newSize);
			for (int i = aligns.size(); i < newSize; i++)
				aligns.add(ALIGN_LEFT);
		}
		aligns.set(column, align);
		return this;
	}

	public boolean isVertical() {
		return vertical;
	}

	public TableFormatter setVertical(boolean vertical) {
		this.vertical = vertical;
		return this;
	}

	public void print(StringTable t, PrintStream out) {
		if (!vertical) {
			int[] maxWidth = calcMaxWidth(t);
			if (withCaption)
				printCaption(t, out, t.getName(), maxWidth);
			if (withBox == WITH_BOX_YES)
				printHLine(t, out, maxWidth);
			if (withHeader)
				printRow(t, out, t.getHeader(), maxWidth);
			if (withBox == WITH_BOX_YES)
				printHLine(t, out, maxWidth);
			for (String[] aRow : t.getData()) 
				printRow(t, out, aRow, maxWidth);
			if (withBox == WITH_BOX_YES)
				printHLine(t, out, maxWidth);
		} else {
			String[] header = t.getHeader();
			TableFormatter tf = new TableFormatter().setWithBox(WITH_BOX_NO).setWithCaption(false).setWithHeader(false);
			tf.setAlignment(0, ALIGN_RIGHT);
			int n = 0;
			for (String[] aRow: t.getData()) {
				StringTable s = new StringTable("", new String[]{"Name", "Value"});
				for (int i = 0; i < header.length; i++)
					s.addRow(new String[]{header[i] + ":", aRow[i]});
				
				if (n > 0)
					out.println();
				out.println("== Row " + (n + 1) + " ==");
				tf.print(s, out);
				n++;
			}
		}
	}
	
	public String printToString(StringTable t) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(stream);
		print(t, ps);
		ps.close();
		return stream.toString();
	}
	
	private void printRow(StringTable t, PrintStream out, String[] row, int[] maxWidth) {
		int numColumns = t.getNumColumns();
		String[][] colLines = new String[row.length][];
		int maxLines = 1;
		for (int c = 0; c < numColumns; c++) {
			colLines[c] = row[c].split("\n");
			if (colLines[c].length > maxLines)
				maxLines = colLines[c].length;
		}
		for (int l = 0; l < maxLines; l++) {
			for (int c = 0; c < numColumns; c++) {
				if (withBox == WITH_BOX_YES)
					out.print(columnDelimiter);
				out.print(' ');
				String line = "";
				if (colLines[c].length > l)
					line = colLines[c][l];
				int align = getAlignment(c);
				if (align == ALIGN_LEFT) {
					out.print(line);
					for (int i = getPrintLength(line); i < maxWidth[c]; i++)
						out.print(' ');
				} else if (align == ALIGN_RIGHT) {
					int padding = maxWidth[c] - getPrintLength(line);
					for (int i = 0; i < padding; i++)
						out.print(' ');
					out.print(line);
				} else {	
					int padding = maxWidth[c] - getPrintLength(line);
					int leftPadding = padding / 2;
					int rightPadding = padding - leftPadding;
					for (int i = 0; i < leftPadding; i++)
						out.print(' ');
					out.print(line);
					for (int i = 0; i < rightPadding; i++)
						out.print(' ');
				}
				out.print(' ');
			}
			if (withBox == WITH_BOX_YES) {
				if (l < maxLines - 1)
					out.print(lineContinuation);
				else
					out.print(columnDelimiter);
			}
			out.println();
		}
	}

	private void printHLine(StringTable t, PrintStream out, int[] maxWidth) {
		int numColumns = t.getNumColumns();
		for (int c = 0; c < numColumns; c++) {
			out.print(rowColumnCross);
			for (int i = 0; i < maxWidth[c] + 2; i++)
				out.print(rowDelimiter);
		}
		out.println(rowColumnCross);
	}
	
	private void printCaption(StringTable t, PrintStream out, String caption, int[] maxWidth) {
		if (withBox == WITH_BOX_YES) {
			int totalWidth = 0;
			int numColumns = t.getNumColumns();
			for (int c = 0; c < numColumns; c++) {
				if (c == 0)
					out.print(rowColumnCross);
				else
					out.print(rowDelimiter);
				totalWidth++;
				for (int i = 0; i < maxWidth[c] + 2; i++) {
					out.print(rowDelimiter);
					totalWidth++;
				}
			}
			out.println(rowColumnCross);
			totalWidth++;
			
			out.print(columnDelimiter);
			out.print(' ');
			out.print(caption);
			for (int i = getPrintLength(caption) + 2; i < totalWidth - 1; i++)
				out.print(' ');
			out.println(columnDelimiter);
		} else {
			out.print(' ');
			out.print(caption);
		}
	}
	
	private int getPrintLength(String s) {
		int r = s.length();
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) > 256)	
				r++;
		return r;
	}
	
	private int[] calcMaxWidth(StringTable t) {
		int numColumns = t.getNumColumns();
		int[] maxWidth = new int[numColumns];
		for (int i = 0; i < maxWidth.length; i++) {
			maxWidth[i] = 0;
			String[] a = t.getHeader()[i].split("\n");
			for (String line: a) {
				int width = getPrintLength(line);
				if (width > maxWidth[i])
					maxWidth[i] = width;
			}
		}
		for (String[] aRow : t.getData()) {
			for (int i = 0; i < aRow.length; i++) {
				String[] a = aRow[i].split("\n");
				for (String line: a) {
					int width = getPrintLength(line);
					if (width > maxWidth[i])
						maxWidth[i] = width;
				}
			}
		}
		return maxWidth;
	}
	
	public StringTable parse(String str) throws IOException {
		StringTable t = null;
		
		BufferedReader r = new BufferedReader(new StringReader(str));
		t = parse(r);
		r.close();
		
		return t;
	}

	public StringTable parseFile(String fileName) throws IOException {
		StringTable t = null;
		
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		t = parse(r);
		r.close();
		
		return t;
	}

	private StringTable parse(BufferedReader r) throws IOException {
		String caption;
		if (withCaption)
			caption = readCaption(r);
		else
			caption = "NO NAME";
		
		String[] row = null;
		String[] headers;
		if (withHeader) {
			headers = readHeader(r);
			if (withBox == WITH_BOX_YES)
				r.readLine();
		} else {
			row = readRow(r);
			headers = new String[row.length];
			for (int i = 0; i < headers.length; i++)
				headers[i] = "COL " + (i + 1);
		}
		
		StringTable t = new StringTable(caption, headers);
		if (row != null)
			t.addRow(row);
		
		while ((row = readRow(r)) != null)
			t.addRow(row);
		
		return t;
	}
	
	private String[] readRow(BufferedReader r) throws IOException {
		while (true) {
			String line = readContentLine(r);
			if (line == null)
				return null;
			return splitRow(line);
		}
	}

	private String[] readHeader(BufferedReader r) throws IOException {
		String line = readContentLine(r);
		return splitRow(line);
	}

	private String readCaption(BufferedReader r) throws IOException {
		String line = readContentLine(r);
		if (line.startsWith("" + columnDelimiter))
			return line.substring(1, line.length() - 2).trim();
		else
			return line.trim();
	}

	private String readContentLine(BufferedReader r) throws IOException {
		String line = r.readLine();
		if (line == null)
			return null;
		if (line.startsWith("" + rowColumnCross)) {
			if (withBox == WITH_BOX_YES	|| withBox == WITH_BOX_AUTO)  
				line = r.readLine();
			else
				throw new IOException("Invalid input: " + line);
		}
		return line;
	}

	private String[] splitRow(String line) throws IOException {
		Vector<String> rowV = new Vector<String>();
		
		if (line.startsWith("" + columnDelimiter)) {
			int p1 = line.indexOf(columnDelimiter);
			if (p1 < 0)
				throw new IOException("�Ҳ����зָ���");
			int p2;
			while (true) {
				p2 = line.indexOf(columnDelimiter, p1 + 1);
				if (p2 < 0)
					break;
				rowV.add(line.substring(p1 + 1, p2).trim());
				p1 = p2;
			}
		} else {
			String[] a = line.split("  ");
			for (String c : a) {
				String ct = c.trim();
				if (ct.length() > 0)
					rowV.add(ct);
			}
		}
		
		if (rowV.size() == 0)
			throw new IOException("������Ҫһ��");
		String[] row = new String[rowV.size()];
		rowV.toArray(row);
		return row;
	}
}
