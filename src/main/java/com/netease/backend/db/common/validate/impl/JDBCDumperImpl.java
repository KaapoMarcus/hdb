package com.netease.backend.db.common.validate.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.netease.backend.db.common.validate.SQLDumper;
import com.netease.cli.StringTable;


public class JDBCDumperImpl
	implements SQLDumper
{
	
	private static final char	DEFAULT_DELIMITER			= ',';
	private static final char	DEFAULT_QUOTER				= '"';

	
	private static final int	DEFAULT_FETCH_ROWS_LIMIT	= 10000;

	
	private final String		dataSourceURL;
	private final String		user;
	private final String		password;
	private final String		charset;

	
	public JDBCDumperImpl(String dataSourceURL, String user, String password) {
		this(dataSourceURL, user, password, Charset.defaultCharset().name());
	}

	
	public JDBCDumperImpl(String dataSourceURL, String user, String password, String charset) {
		if ((dataSourceURL == null) || (user == null) || (password == null)
				|| (charset == null))
			throw new NullPointerException();
		this.dataSourceURL = dataSourceURL;
		this.password = password;
		this.user = user;
		this.charset = charset;
	}

	
	public Connection connect()
		throws SQLException
	{
		final Properties properties = new Properties();
		properties.put("user", this.getUser());
		properties.put("password", this.getPassword());
		properties.put("characterEncoding", this.getCharset());
		properties.put("useUnicode", "true");
		return DriverManager.getConnection(this.getDataSourceURL(), this.getUser(), this.getPassword());
	}

	public int dumpByQuery(
		String sql, String filePath)
		throws SQLException, IOException
	{
		return this.dumpByQuery(sql, 0, DEFAULT_FETCH_ROWS_LIMIT, filePath, Charset.defaultCharset().name(), false, DEFAULT_DELIMITER, DEFAULT_QUOTER);
	}

	public int dumpByQuery(
		String sql, int offset, int limit, String outPath, String charset,
		boolean writeHeader, char delimiter, char quoter)
		throws SQLException, IOException
	{
		final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outPath), charset);
		return this.dumpByQuery(sql, offset, limit, writer, writeHeader, delimiter, quoter);
	}

	public int dumpByQuery(
		String sql, int offset, int limit, Writer writer, boolean writeHeader,
		char delimiter, char quoter)
		throws SQLException, IOException
	{
		final StringTable st = this.dumpByQuery(sql, offset, limit);
		return RSFormatter.writeStringTable(writer, st, !writeHeader, delimiter, quoter);
	}

	public StringTable dumpByQuery(
		String sql, int offset, int limit)
		throws SQLException
	{
		if ((sql == null))
			throw new NullPointerException();

		if (limit <= 0) {
			limit = DEFAULT_FETCH_ROWS_LIMIT;
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			conn = this.connect();
			conn.setAutoCommit(false);
			
			stmt = conn.createStatement();
			stmt.setFetchSize(1); 
			rs = stmt.executeQuery(sql);
			
			return RSFormatter.convertResultSet(rs, offset, limit);
		} finally {
			dispose(conn, stmt, rs);
		}
	}

	
	private static void dispose(
		Connection conn, Statement stmt, ResultSet rs)
	{
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}
	}

	
	private String getDataSourceURL()
	{
		return dataSourceURL;
	}

	
	private String getUser()
	{
		return user;
	}

	
	private String getPassword()
	{
		return password;
	}

	
	private String getCharset()
	{
		return charset;
	}

}
