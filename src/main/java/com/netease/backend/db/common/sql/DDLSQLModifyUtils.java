package com.netease.backend.db.common.sql;


public class DDLSQLModifyUtils {
	
	
	public static final int AUTO_INCREMENT = 0x02;
	
	
	public static final int BACKTICK = 0x04;
	
	
	public static String remove(String sql, int flags) {
		String newSql = sql;
		if ((flags & AUTO_INCREMENT) > 0)
			newSql = removeAutoIncrement(newSql);
		
		if ((flags & BACKTICK) > 0)
			newSql = removeBacktick(newSql);
		
		return newSql;
	}
	
	
	private static String removeAutoIncrement(String sql) {
		if (sql != null)
			sql = sql.replaceAll("(?i)\\s+AUTO_INCREMENT\\s*", " ");
		return sql;
	}
	
	
	private static String removeBacktick(String sql) {
		sql = sql.replaceAll("`", "");
		return sql;
	}
}
