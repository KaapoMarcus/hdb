package com.jhh.hdb.meta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.define.ServerStatus;

/*
 * 
 * 1 在一个独立的分布式数据库中
 * 
 * 2 在多个独立的分布式数据库中
 * 
 * 3 在多个独立的数据库中
 * 
 */
public class Main {

	public static MySQLCmd get_do(String sql) throws Exception {

		// final String sql = "select ";
		int sqltype = get_sqltype(sql);

		switch (sqltype) {
		case SqlType.NONFROMSELECT:
			
		case SqlType.SELECT:
			return Select.exec(sql);

		case SqlType.INSERT:
			DmlSql.insert(sql);
			break;

		case SqlType.UPDATE:
			DmlSql.update(sql);
			break;

		case SqlType.DELETE:
			DmlSql.delete(sql);
			break;

		case SqlType.UNKNOW:

			MyResultSetCmd cmd = new MyResultSetCmd(null, null, false,
					ServerStatus.SERVER_STATUS_AUTOCOMMIT);
			break;
		}

		return null;
	}

	public static int get_sqltype(String sql) throws Exception {

		int ret = SqlType.UNKNOW;
		String regEx = " *([a-zA-Z]*) *.+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(sql);
		while (m.find()) {
			String first_token = m.group(1).toLowerCase();

			if ("select".equals(first_token)) {
				if (sql.toLowerCase().contains("from")) {
					ret = SqlType.SELECT;
				} else {
					ret = SqlType.NONFROMSELECT;
				}
			}
			if ("insert".equals(first_token)) {
				ret = SqlType.INSERT;
			}
			if ("delete".equals(first_token)) {
				ret = SqlType.DELETE;
			}
			if ("update".equals(first_token)) {
				ret = SqlType.UPDATE;
			}
		}

		return ret;
	}

	public static void main(String[] args) throws Exception {
		get_do("");
	}
}
