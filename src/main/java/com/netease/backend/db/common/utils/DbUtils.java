package com.netease.backend.db.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.netease.backend.db.common.schema.Database;



public class DbUtils {

    public static Connection getDatabaseConnection(Database database,
            String user, String password) throws SQLException {
        Properties info = new Properties();
        info.put("user", user);
        info.put("password", password);
        Connection conn = getDatabaseConnection(database, info);

        return conn;
    }

    public static Connection getDatabaseConnection(Database database,
            Properties info) throws SQLException {
        
        Connection conn = DriverManager.getConnection(database.getURL(), info);
        
        Statement statement = conn.createStatement();
        for (String initStatement : database.getConnectionInitStatements()) {
            statement.addBatch(initStatement);
        }
        statement.executeBatch();
        statement.close();
        return conn;
    }

	
	public static Connection getMysqlConnection(String url, String user, String password) throws SQLException {
		try	{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			return DriverManager.getConnection(url, user, password);
		}catch(SQLException sqle)
		{
			throw new SQLException("Connect '"+url+"' failed: "+sqle.getMessage());
		}
	}

	
	public static Connection getMysqlConnection(String url, int retryTimes)
	throws SQLException {
		if (retryTimes <= 0)
			return null;
		int i = retryTimes;
		SQLException e = null;
		if (url.indexOf('?') > 0)
			url = url + "&connectTimeout=1000";
		else
			url += "?connectTimeout=1000";
		while (i > 0) {
			try {
				DriverManager.registerDriver(new com.mysql.jdbc.Driver());
				return DriverManager.getConnection(url);
			} catch (SQLException sqle) {
				i--;
				e = sqle;
			}
		}
		throw new SQLException("Connect '" + url + "' failed: "
				+ e.getMessage());
	}

	
	public static boolean isDbAvailable(Connection conn, String dbName) throws SQLException {
		Statement stmt = null;
		ResultSet rs =  null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SHOW DATABASES LIKE '"+dbName+"'");
			return rs.next();
		}finally
		{
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
			}catch(SQLException sqle){}
		}
	}

	
	
	public static String getDataDir(Connection conn) throws SQLException
	{
		Statement stmt = null;
		ResultSet rs = null;
		String path = "";
		try
		{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SHOW VARIABLES LIKE 'datadir'");
			if(rs.next())
				path = rs.getString(2);
		}catch(SQLException sqle)
		{
			throw new SQLException("Cannot found datadir: "+sqle.getMessage());
		}finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(stmt != null)
					stmt.close();
			}catch(SQLException sqle){}
		}
		if(path.equals(""))
			throw new SQLException("Cannot found datadir.");
		else
			return StringUtils.trimPathWithoutSlash(path);
	}

}
