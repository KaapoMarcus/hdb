
package com.netease.backend.db.common.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.netease.cli.StringTable;
import com.netease.cli.TableFormatter;


public class ResultSetFormatter {
	
	
	public static final String NULL_VALUE = "\\N";
	
	public static StringTable resultSetToTable(ResultSet rs)  throws SQLException {
		ResultSetMetaData rsm = rs.getMetaData();
		int columnCount = rsm.getColumnCount();
		
		String[] columnNames = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			columnNames[i] = rsm.getColumnName(i + 1);
		}
		StringTable table = new StringTable("RS", columnNames);
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                int type = rsm.getColumnType(i + 1);
                row[i] = formatDatem(rs, i + 1, type, "<not printable", false, '\0');
            }
            table.addRow(row);
        }
        return table;
	}
	
	public static int format(ResultSet rs, PrintStream out) throws SQLException {
		StringTable table = resultSetToTable(rs);
		new TableFormatter().setWithCaption(false).print(table, out);
		return table.getData().size();
	}
	
	
	public static StringBuilder getInsertSQL(String tableName, ResultSet row) throws SQLException {
		ResultSetMetaData rsm = row.getMetaData();
		String columnList = null;
		int[] columnTypes = new int[rsm.getColumnCount()];
		for (int i = 0; i < columnTypes.length; i++) {
			columnTypes[i] = rsm.getColumnType(i + 1);
			if (columnList == null)
				columnList = rsm.getColumnName(i + 1);
			else
				columnList += "," + rsm.getColumnName(i + 1); 
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(tableName);
		sb.append("(").append(columnList).append(")");
		sb.append(" values(");
			
		int columnCount = rsm.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			String value = ResultSetFormatter.formatDatem(row, i + 1, columnTypes[i], null, true, '\'');
			if (value == null)
				throw new SQLException("�޷�������������Ϊ'" + rsm.getColumnTypeName(i + 1) + "'������");
			if (i != 0)
				sb.append(",");
			sb.append(value);
		}
		sb.append(");");
		return sb;
	}
	
	
	public static String getBatchInsertSQL(String tableName, ResultSet row, boolean skipBucketNo) throws SQLException {
		if(!row.next())
			return null;
		
		ResultSetMetaData rsm = row.getMetaData();
		String columnList = null;
		int[] columnTypes = new int[rsm.getColumnCount()];
		int bucketNoIdx = -1;
		for (int i = 0; i < columnTypes.length; i++) {
			String name = rsm.getColumnName(i + 1);
			if (skipBucketNo && name.equalsIgnoreCase("bucketno")) {
				bucketNoIdx = i;
				continue;
			}
			columnTypes[i] = rsm.getColumnType(i + 1);
			if (columnList == null)
				columnList = "`" + rsm.getColumnName(i + 1) + "`";
			else
				columnList += ", `" + rsm.getColumnName(i + 1) + "`"; 
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(tableName);
		sb.append("(").append(columnList).append(")");
		sb.append(" values(");

		int columnCount = rsm.getColumnCount();
		
		boolean first = true;
		do {
			if (!first)
				sb.append(",(");
			first = false;
			for (int i = 0; i < columnCount; i++) {
				if (i == bucketNoIdx)
					continue;
				String value = ResultSetFormatter.formatDatem(row, i + 1, columnTypes[i], null, true, '\'');
				if (value == null)
					throw new SQLException("�޷�������������Ϊ'" + rsm.getColumnTypeName(i + 1) + "'������");
				if (i != 0)
					sb.append(", ");
				sb.append(value);
			}
			sb.append(")");
		} while (row.next());
		return sb.toString();
	}
	
	
	public static String getBatchInsertPreSQL(String tableName, ResultSet row, boolean skipBucketNo) throws SQLException {
		if(!row.next())
			return null;
		
		ResultSetMetaData rsm = row.getMetaData();
		String columnList = null;
		int[] columnTypes = new int[rsm.getColumnCount()];
		int bucketNoIdx = -1;
		for (int i = 0; i < columnTypes.length; i++) {
			String name = rsm.getColumnName(i + 1);
			if (skipBucketNo && name.equalsIgnoreCase("bucketno")) {
				bucketNoIdx = i;
				continue;
			}
			columnTypes[i] = rsm.getColumnType(i + 1);
			if (columnList == null)
				columnList = "`" + rsm.getColumnName(i + 1) + "`";
			else
				columnList += ", `" + rsm.getColumnName(i + 1) + "`"; 
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(tableName);
		sb.append("(").append(columnList).append(")");
		sb.append(" values(");

		int columnCount = rsm.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			if (i == bucketNoIdx)
				continue;
			String value = "?";
			if (i != 0)
				sb.append(", ");
			sb.append(value);
		}
		sb.append(")");
		
		while(row.next())
		{
			sb.append(",(");
			for (int i = 0; i < columnCount; i++) {
				if (i == bucketNoIdx)
					continue;
				String value = "?";
				if (i != 0)
					sb.append(", ");
				sb.append(value);
			}
			sb.append(")");
		}
		return sb.toString();
	}
	
	
	public static void writeCsvRow(ResultSet row, DataOutputStream out, String charset, String attrDelimiter, char attrQuoter) throws SQLException, IOException {
		ResultSetMetaData rsm = row.getMetaData();
		for (int i = 0; i < rsm.getColumnCount(); i++) {
			if (i != 0)
				out.write(attrDelimiter.getBytes());
			
			int columnType = rsm.getColumnType(i + 1);
			if (columnType == Types.BINARY || columnType == Types.VARBINARY 
				|| columnType == Types.BLOB || columnType == Types.LONGVARBINARY) {
				if (attrQuoter != '\0')
					out.writeByte(attrQuoter);
				byte[] value = row.getBytes(i + 1);
				out.write(value == null ? NULL_VALUE.getBytes() : value);
				if (attrQuoter != '\0')
					out.writeByte(attrQuoter);
			} else {
				String value = ResultSetFormatter.formatDatem(row, i + 1, columnType, null, true, false, attrQuoter);
				if (value == null)
					throw new SQLException("Can not dump data of type '" + rsm.getColumnTypeName(i + 1) + "'");
				out.write(value.getBytes(charset));
			}
		}
	}
	
	
	public static String formatDatem(ResultSet rs, int columnIndex, int columnType,
			String notPrintable, boolean escape, char quoter) throws SQLException {
		return formatDatem(rs, columnIndex, columnType, notPrintable, escape, true, quoter);
	}
	
	
	public static String formatDatem(ResultSet rs, int columnIndex, int columnType,
			String notPrintable, boolean escape, boolean doubleSingleQuote, 
			char quoter) throws SQLException {
		Object value;
		switch(columnType) {
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.BIGINT:
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.REAL:
		case Types.BOOLEAN:
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.BIT:
			
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.DATE:
		case Types.TIMESTAMP:
		case Types.TIME:
			
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.BLOB:
		case Types.LONGVARBINARY:
			
		case Types.NULL:
			value = rs.getObject(columnIndex);
			break;
		default:
			return notPrintable;
		}
		if (value == null)
			return NULL_VALUE;
		
		String r;
		if (value instanceof byte[])
			r = new String((byte[])value);
		else
			r = value.toString();
		if (escape && (columnType == Types.CHAR 
			|| columnType == Types.VARCHAR
			|| columnType == Types.LONGVARCHAR)) 
			r = escapeSQLString(r, doubleSingleQuote);
		if (quoter != '\0' && !isNumberType(columnType))
			r = quoter + r + quoter;
		return r;
	}

	
	public static String escapeSQLString(String value) {
		return escapeSQLString(value, true);
	}
	
	
	public static String escapeSQLString(String value, boolean doubleSingleQuote) {
		StringBuilder sb = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (ch == '\'') {
				if (doubleSingleQuote)
					sb.append(ch).append(ch);
				else
					sb.append(ch);
			} else if (ch == '\\')
				sb.append(ch).append(ch);
			else if (ch == '\n')
				sb.append("\\n");
			else if (ch == '\r')
				sb.append("\\r");
			else if (ch == '\t')
				sb.append("\\t");
			else if (ch == '\0')
				sb.append("\\0");
			else
				sb.append(ch);
		}
		return sb.toString();
	}
	
	private static boolean isNumberType(int jdbcType) {
		return jdbcType == Types.INTEGER || jdbcType == Types.SMALLINT
			|| jdbcType == Types.TINYINT || jdbcType == Types.BIGINT
			|| jdbcType == Types.FLOAT || jdbcType == Types.DOUBLE
			|| jdbcType == Types.REAL || jdbcType == Types.BOOLEAN
			|| jdbcType == Types.NUMERIC || jdbcType == Types.DECIMAL
			|| jdbcType == Types.BIT;
	}
}
