package com.netease.backend.db.common.utils;

import java.sql.Types;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.enumeration.SQLType;


public class SQLCommonUtils {
	
	public static SQLType getSqlType(String sql) {
		int i = 0;
		while (i < sql.length() && Character.isWhitespace(sql.charAt(i)))
			i++;
		SQLType type;
		if (sql.regionMatches(true, i, "select", 0, "select".length())) {
			type = SQLType.SELECT;
			i += "select".length();
		} else if (sql.regionMatches(true, i, "insert", 0, "insert".length())) {
			type = SQLType.INSERT;
			i += "insert".length();
		} else if (sql.regionMatches(true, i, "replace", 0, "replace".length())) {
			type = SQLType.INSERT;
			i += "replace".length();
		} else if (sql.regionMatches(true, i, "update", 0, "update".length())) {
			type = SQLType.UPDATE;
			i += "update".length();
		} else if (sql.regionMatches(true, i, "delete", 0, "delete".length())) {
			type = SQLType.DELETE;
			i += "delete".length();
		} else if (sql.regionMatches(true, i, "call", 0, "call".length())) {
			type = SQLType.STOREDPROCEDURE;
			i += "call".length();
		} else
			return SQLType.OTHER;
		if (i < sql.length() && Character.isWhitespace(sql.charAt(i)))
			return type;
		else
			return SQLType.OTHER;
	}

	
	public static DbnType getDbnTypeByUrl(String url) {
		if (url == null || url.equals(""))
            return null;
		if (url.startsWith("jdbc:mysql://")){
			return DbnType.MySQL;
		} else if (url.startsWith("jdbc:oracle:thin:@")) {
			return DbnType.Oracle;
		} else
			return null;
	}
	
	
	public static String getDriverName(DbnType type) {
		if (type == DbnType.MySQL)
			return "com.mysql.jdbc.Driver";
		else if (type == DbnType.Oracle)
			return "oracle.jdbc.driver.OracleDriver";
		else return "";
	}
	
    
    public static String getDBNameByUrl(String url) {
        if (url == null || url.equals(""))
            return "";
        DbnType type = getDbnTypeByUrl(url);
        if (null == type)
        	return "";
        int lastSeparator = (type == DbnType.MySQL ? '/' : ':');
        String dbName = url.substring(url.lastIndexOf(lastSeparator) + 1, url.length());
        if(type == DbnType.MySQL && dbName.indexOf('?')>=0)
        	dbName = dbName.substring(0,dbName.indexOf('?')).trim();
        return dbName;
    }

    
    public static String getHostByUrl(String url) {
        if (url == null || url.equals(""))
            return "";
        DbnType type = getDbnTypeByUrl(url);
        if (null == type)
        	return "";
        String host = url.substring(url.indexOf(type == DbnType.MySQL ? "//" : "@") 
        		+ (type == DbnType.MySQL ? 2 : 1), url.length());
        if (host.contains(":"))
            host = host.substring(0, host.indexOf(':'));
        else
            host = host.substring(0, host.indexOf('/'));

        return host;
    }

    
    public static int getPortByUrl(String url) {
        if (url == null || url.equals(""))
            return 3306;
        DbnType type = getDbnTypeByUrl(url);
        if (null == type)
        	return 3306;

        
        int port;
        String portString =  url.substring(url.indexOf(type == DbnType.MySQL ? "//" : "@") 
        		+ (type == DbnType.MySQL ? 2 : 1), url.length());
        if (!portString.contains(":"))
            port = 3306;
        else {
            portString = portString.substring(portString.indexOf(':') + 1, portString.length());
            portString = portString.substring(0, portString.indexOf(type == DbnType.MySQL ? '/' : ':'));
            port = Integer.valueOf(portString);
        }

        return port;
    }
    
    
    
    public static String getColumnClassNameForType(int type)
    {
		switch (type) {
		case Types.BIT:
		case Types.BOOLEAN:
			return "java.lang.Boolean"; 

		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
			return "java.lang.Integer"; 

		case Types.BIGINT:
			return "java.lang.Long"; 

		case Types.DECIMAL:
		case Types.NUMERIC:
			return "java.math.BigDecimal"; 

		case Types.REAL:
			return "java.lang.Float"; 

		case Types.FLOAT:
		case Types.DOUBLE:
			return "java.lang.Double"; 

		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return "java.lang.String"; 

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return "java.lang.String";

		case Types.DATE:
			return "java.sql.Date"; 

		case Types.TIME:
			return "java.sql.Time"; 

		case Types.TIMESTAMP:
			return "java.sql.Timestamp"; 

		default:
			return "java.lang.Object"; 
		}
    }
}
