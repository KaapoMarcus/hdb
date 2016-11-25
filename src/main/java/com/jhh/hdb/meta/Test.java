package com.jhh.hdb.meta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.util.JdbcUtils;
import com.jhh.hdb.sql.SqlType;

@SuppressWarnings("rawtypes")
public class Test {

	public Test() {

	}

	public static void main(String[] args) throws Exception {

		test();

		System.out.println("========end========");
	}

	// 联调
	public static void test() throws Exception {

		metadata.getConnection();
		String sql;

		sql = StringTemplateUtils.read_stat("q_delete_uinfo");
		test_delete_htable(sql);
		sql = StringTemplateUtils.read_stat("q_delete_ulogin");
		test_delete_htable(sql);
		sql = StringTemplateUtils.read_stat("q_delete_ubank");
		test_delete_htable(sql);

		// sql = StringTemplateUtils.read_stat("q_simpleselect_uinfo");
		// test_simpleselect_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_simpleselect_ulogin");
		// test_simpleselect_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_simpleselect_ubank");
		// test_simpleselect_htable(sql);

		sql = StringTemplateUtils.read_stat("q_load_uinfo");
		test_load_htable(sql);
		sql = StringTemplateUtils.read_stat("q_load_ulogin");
		test_load_htable(sql);
		sql = StringTemplateUtils.read_stat("q_load_ubank");
		test_load_htable(sql);

		// sql = StringTemplateUtils.read_stat("test_select_1");
		// test_select(sql);

		// sql = StringTemplateUtils.read_stat("q_create_database");
		// test_create_hdb(sql);

		// sql = StringTemplateUtils.read_stat("q_create_hdp");
		// test_create_hdp(sql);

		// sql = StringTemplateUtils.read_stat("q_create_table_1");
		// test_create_table(sql);
		// sql = StringTemplateUtils.read_stat("q_create_table_2");
		// test_create_table(sql);
		// sql = StringTemplateUtils.read_stat("q_create_table_3");
		// test_create_table(sql);

		// sql = StringTemplateUtils.read_stat("q_replace_uinfo");
		// test_replace_htable(sql);

		// sql = StringTemplateUtils.read_stat("q_insert_uinfo");
		// test_insert_htable(sql);

	}

	public static int find_record_nodedb(List hdc_value_list, Hdp table_hdp) throws Exception {

		int ret = -1;
		if ("hash".equalsIgnoreCase(table_hdp.getHdp_type())) {
			int step = new Integer(table_hdp.getHdp_step());
			int x = new Integer(hdc_value_list.get(0).toString());
			ret = x % step;

		}
		if ("range".equalsIgnoreCase(table_hdp.getHdp_type())) {
			int step = new Integer(table_hdp.getHdp_step());
			int minid = new Integer(table_hdp.getHdp_min());
			int maxid = new Integer(table_hdp.getHdp_max());
			int x = new Integer(hdc_value_list.get(0).toString());

			if (x >= minid && x < maxid) {
				minid = minid - (minid % step);
				maxid = (maxid) % step == 0 ? maxid : maxid + (step - (maxid) % step);
				ret = (x - minid) / step;
			}
		}

		return ret;
	}

	public static void test_load_htable(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		// String line_terminate = System.getProperty("line.separator");
		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlLoadDataInFileStatement stmt = (MySqlLoadDataInFileStatement) iterator.next();
			List<SQLExpr> columns = stmt.getColumns();
			String alias = stmt.getTableName().toString();
			String fileName = stmt.getFileName().toString().replace("'", "");

			SQLLiteralExpr colTer = stmt.getColumnsTerminatedBy();
			String column_terminate = colTer.toString().replace("'", "");
			SQLLiteralExpr lineTer = stmt.getLinesTerminatedBy();
			String line_terminate = lineTer.toString().replace("'", "");

			String current_hdb_name = "current_db";
			String hdb_name = "";
			String htable_name = "";
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				hdb_name = current_hdb_name;
				htable_name = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				hdb_name = alias_arr[0];
				htable_name = alias_arr[1];
			}

			// hdc
			Map get_hdc_list_by_htable = metadata.get_hdc_list_by_htable(hdb_name, htable_name);
			Hdp table_hdp = metadata.get_hdp_by_htable(hdb_name, htable_name);
			int hdc_found = 0;
			// hdc 在 插入字段中的索引
			List<Integer> hdc_index_list = new ArrayList();
			if (columns.size() == 0) {
				hdc_found = get_hdc_list_by_htable.size();
				hdc_index_list = metadata.get_hdc_index_by_htable(hdb_name, htable_name);
			} else {
				Iterator hdc_it = get_hdc_list_by_htable.keySet().iterator();
				while (hdc_it.hasNext()) {
					String key = (String) hdc_it.next();
					for (int i = 0; i < columns.size(); i++) {
						SQLExpr sqlExpr = (SQLExpr) columns.get(i);
						String hcolumn_name = sqlExpr.toString();
						if (key.equalsIgnoreCase(hcolumn_name)) {
							hdc_found++;
							hdc_index_list.add(i);
						}
					}
				}
			}

			if (hdc_found == get_hdc_list_by_htable.size()) {
				Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);
				// 每个value该插入到那个节点的映射关系
				Map<Integer, String> loadfile_map = new HashMap<Integer, String>();
				Map<Integer, BufferedWriter> bufwriter_map = new HashMap<Integer, BufferedWriter>();
				Map<Integer, MySqlLoadDataInFileStatement> stmt_map = new HashMap<Integer, MySqlLoadDataInFileStatement>();

				File inputFile = new File(fileName);
				System.out.println(inputFile.getAbsolutePath());
				if (!inputFile.exists()) {
					return;
				}
				FileReader fr = new FileReader(inputFile);
				BufferedReader br = new BufferedReader(fr);

				String line_str = null;
				while ((line_str = br.readLine()) != null) {
					String[] field_arr = line_str.split(column_terminate);
					List hdc_value_list = new ArrayList();
					for (Integer idx : hdc_index_list) {
						hdc_value_list.add(field_arr[idx]);
					}

					int nodedb_idx = find_record_nodedb(hdc_value_list, table_hdp);
					if (nodedb_idx >= 0 && nodedb_idx < hdb_conn_map.size()) {

						BufferedWriter bufwriter = bufwriter_map.get(nodedb_idx);
						String load_file = loadfile_map.get(nodedb_idx);
						MySqlLoadDataInFileStatement nodedb_stmt = stmt_map.get(nodedb_idx);
						if (load_file == null && bufwriter == null && nodedb_stmt == null) {
							load_file = fileName + "_" + nodedb_idx;
							File nodedb_loadfile = new File(load_file);
							if (nodedb_loadfile.exists() == false) {
								nodedb_loadfile.createNewFile();
							}
							FileWriter fw = new FileWriter(nodedb_loadfile, false);
							bufwriter = new BufferedWriter(fw);

							nodedb_stmt = new MySqlLoadDataInFileStatement();
							nodedb_stmt.setReplicate(stmt.isReplicate());
							nodedb_stmt.setIgnore(stmt.isIgnore());
							nodedb_stmt.setLocal(stmt.isLocal());
							nodedb_stmt.setColumns(columns);
							nodedb_stmt.setColumnsTerminatedBy(colTer);
							nodedb_stmt.setLinesTerminatedBy(stmt.getLinesTerminatedBy());
							nodedb_stmt.setFileName(new MySqlCharExpr(load_file));

							nodedb_stmt.setTableName(stmt.getTableName());
							nodedb_stmt.setDbType(dbType);

							loadfile_map.put(nodedb_idx, load_file);
							bufwriter_map.put(nodedb_idx, bufwriter);
							stmt_map.put(nodedb_idx, nodedb_stmt);

						}
						bufwriter.write(line_str + line_terminate);
					}
				}
				// 关闭文件
				fr.close();
				br.close();

				Iterator bufwriter_it = bufwriter_map.keySet().iterator();
				while (bufwriter_it.hasNext()) {
					Integer key = (Integer) bufwriter_it.next();
					BufferedWriter bufwriter = (BufferedWriter) bufwriter_map.get(key);
					bufwriter.close();
				}

				// 执行

				ExecutorService exec = null;
				HashMap taskMap = null;

				exec = Executors.newFixedThreadPool(stmt_map.size());
				taskMap = new HashMap<String, Future>();
				Iterator stmt_it = stmt_map.keySet().iterator();
				while (stmt_it.hasNext()) {
					final Integer key = (Integer) stmt_it.next();
					final MySqlLoadDataInFileStatement execstmt = (MySqlLoadDataInFileStatement) stmt_map.get(key);
					final String sqlstr = execstmt.toString();
					final String execsql = sqlstr.replace(alias, htable_name);
					final Connection execconn = (Connection) hdb_conn_map.get(key);
					Callable call = new Callable() {
						public String call() throws Exception {

							String logstr = "conn=" + key + ",sql=" + execsql;
							System.out.println(logstr);
							try {
								Statement stmt = execconn.createStatement();
								stmt.execute(execsql);
							} catch (SQLException e) {
								System.out.println(e.getMessage());
								return "FAIL";
							}
							return "OK";
						}
					};
					Future task = exec.submit(call);
					taskMap.put(key, task);
				}

				Iterator iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Integer key = (Integer) entry.getKey();
					Future val = (Future) entry.getValue();
					String ret = (String) val.get();
					System.out.println(key + "\t" + ret);
				}

				exec.shutdown();
			}

		}

	}

	public static void test_insert_htable(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlInsertStatement stmt = (MySqlInsertStatement) iterator.next();
			List<SQLExpr> columns = stmt.getColumns();
			List<ValuesClause> values = stmt.getValuesList();
			String alias = stmt.getTableSource().getExpr().toString();

			String current_hdb_name = "current_db";
			String hdb_name = "";
			String htable_name = "";
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				hdb_name = current_hdb_name;
				htable_name = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				hdb_name = alias_arr[0];
				htable_name = alias_arr[1];
			}

			// hdc
			Map get_hdc_list_by_htable = metadata.get_hdc_list_by_htable(hdb_name, htable_name);
			Hdp table_hdp = metadata.get_hdp_by_htable(hdb_name, htable_name);
			Iterator hdc_it = get_hdc_list_by_htable.keySet().iterator();
			int hdc_found = 0;
			// hdc 在 插入字段中的索引
			List<Integer> hdc_index_list = new ArrayList();
			while (hdc_it.hasNext()) {
				String key = (String) hdc_it.next();
				for (int i = 0; i < columns.size(); i++) {
					SQLExpr sqlExpr = (SQLExpr) columns.get(i);
					String hcolumn_name = sqlExpr.toString();
					if (key.equalsIgnoreCase(hcolumn_name)) {
						hdc_found++;
						hdc_index_list.add(i);
					}
				}
			}

			if (hdc_found == get_hdc_list_by_htable.size()) {
				Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);
				// 每个value该插入到那个节点的映射关系
				Map<Integer, List> value_list_map = new HashMap<Integer, List>();
				Map<Integer, MySqlInsertStatement> stmt_map = new HashMap<Integer, MySqlInsertStatement>();

				for (int i = 0; i < values.size(); i++) {
					ValuesClause vc = (ValuesClause) values.get(i);
					List<SQLExpr> onevalues = vc.getValues();
					List hdc_value_list = new ArrayList();
					for (Integer idx : hdc_index_list) {
						hdc_value_list.add(onevalues.get(idx).toString());
					}
					int nodedb_idx = find_record_nodedb(hdc_value_list, table_hdp);
					if (nodedb_idx >= 0 && nodedb_idx < hdb_conn_map.size()) {

						List value_list = value_list_map.get(nodedb_idx);
						MySqlInsertStatement nodedb_stmt = stmt_map.get(nodedb_idx);
						if (value_list == null && nodedb_stmt == null) {
							value_list = new ArrayList();
							nodedb_stmt = new MySqlInsertStatement();

							value_list_map.put(nodedb_idx, value_list);
							stmt_map.put(nodedb_idx, nodedb_stmt);

							nodedb_stmt.setTableSource(stmt.getTableSource());
							nodedb_stmt.setDbType(dbType);
							nodedb_stmt.getColumns().addAll(columns);
						}
						nodedb_stmt.getValuesList().add(vc);
					}

				}
				// 执行插入

				ExecutorService exec = null;
				HashMap taskMap = null;

				exec = Executors.newFixedThreadPool(stmt_map.size());
				taskMap = new HashMap<String, Future>();
				Iterator stmt_it = stmt_map.keySet().iterator();
				while (stmt_it.hasNext()) {
					final Integer key = (Integer) stmt_it.next();
					final MySqlInsertStatement execstmt = (MySqlInsertStatement) stmt_map.get(key);
					final String sqlstr = execstmt.toString();
					final String execsql = sqlstr.replace(alias, htable_name);
					final Connection execconn = (Connection) hdb_conn_map.get(key);
					Callable call = new Callable() {
						public String call() throws Exception {

							String logstr = "conn=" + key + ",sql=" + execsql;
							System.out.println(logstr);
							try {
								Statement stmt = execconn.createStatement();
								stmt.execute(execsql);
							} catch (SQLException e) {
								System.out.println(e.getMessage());
								return "FAIL";
							}
							return "OK";
						}
					};
					Future task = exec.submit(call);
					taskMap.put(key, task);
				}

				Iterator iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Integer key = (Integer) entry.getKey();
					Future val = (Future) entry.getValue();
					String ret = (String) val.get();
					System.out.println(key + "\t" + ret);
				}

				exec.shutdown();
			}

		}

	}

	public static void test_replace_htable(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlReplaceStatement stmt = (MySqlReplaceStatement) iterator.next();
			List<SQLExpr> columns = stmt.getColumns();
			List<ValuesClause> values = stmt.getValuesList();
			String alias = stmt.getTableSource().getExpr().toString();

			String current_hdb_name = "current_db";
			String hdb_name = "";
			String htable_name = "";
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				hdb_name = current_hdb_name;
				htable_name = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				hdb_name = alias_arr[0];
				htable_name = alias_arr[1];
			}

			// hdc
			Map get_hdc_list_by_htable = metadata.get_hdc_list_by_htable(hdb_name, htable_name);
			Hdp table_hdp = metadata.get_hdp_by_htable(hdb_name, htable_name);
			Iterator hdc_it = get_hdc_list_by_htable.keySet().iterator();
			int hdc_found = 0;
			// hdc 在 插入字段中的索引
			List<Integer> hdc_index_list = new ArrayList();
			while (hdc_it.hasNext()) {
				String key = (String) hdc_it.next();
				for (int i = 0; i < columns.size(); i++) {
					SQLExpr sqlExpr = (SQLExpr) columns.get(i);
					String hcolumn_name = sqlExpr.toString();
					if (key.equalsIgnoreCase(hcolumn_name)) {
						hdc_found++;
						hdc_index_list.add(i);
					}
				}
			}

			if (hdc_found == get_hdc_list_by_htable.size()) {
				Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);
				// 每个value该插入到那个节点的映射关系
				Map<Integer, List> value_list_map = new HashMap<Integer, List>();
				Map<Integer, MySqlReplaceStatement> stmt_map = new HashMap<Integer, MySqlReplaceStatement>();

				for (int i = 0; i < values.size(); i++) {
					ValuesClause vc = (ValuesClause) values.get(i);
					List<SQLExpr> onevalues = vc.getValues();
					List hdc_value_list = new ArrayList();
					for (Integer idx : hdc_index_list) {
						hdc_value_list.add(onevalues.get(idx).toString());
					}
					int nodedb_idx = find_record_nodedb(hdc_value_list, table_hdp);

					if (nodedb_idx >= 0 && nodedb_idx < hdb_conn_map.size()) {

						List value_list = value_list_map.get(nodedb_idx);
						MySqlReplaceStatement nodedb_stmt = stmt_map.get(nodedb_idx);
						if (value_list == null && nodedb_stmt == null) {
							value_list = new ArrayList();
							nodedb_stmt = new MySqlReplaceStatement();

							value_list_map.put(nodedb_idx, value_list);
							stmt_map.put(nodedb_idx, nodedb_stmt);

							nodedb_stmt.setTableSource(stmt.getTableSource());
							nodedb_stmt.setDbType(dbType);
							nodedb_stmt.getColumns().addAll(columns);
						}
						nodedb_stmt.getValuesList().add(vc);
					}
				}
				// 执行插入

				ExecutorService exec = null;
				HashMap taskMap = null;

				exec = Executors.newFixedThreadPool(stmt_map.size());
				taskMap = new HashMap<String, Future>();
				Iterator stmt_it = stmt_map.keySet().iterator();
				while (stmt_it.hasNext()) {
					final Integer key = (Integer) stmt_it.next();
					final MySqlReplaceStatement execstmt = (MySqlReplaceStatement) stmt_map.get(key);
					final String sqlstr = execstmt.toString();
					final String execsql = sqlstr.replace(alias, htable_name);
					final Connection execconn = (Connection) hdb_conn_map.get(key);
					Callable call = new Callable() {
						public String call() throws Exception {

							String logstr = "conn=" + key + ",sql=" + execsql;
							System.out.println(logstr);
							try {
								Statement stmt = execconn.createStatement();
								stmt.execute(execsql);
							} catch (SQLException e) {
								System.out.println(e.getMessage());
								return "FAIL";
							}
							return "OK";
						}
					};
					Future task = exec.submit(call);
					taskMap.put(key, task);
				}

				Iterator iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Integer key = (Integer) entry.getKey();
					Future val = (Future) entry.getValue();
					String ret = (String) val.get();
					System.out.println(key + "\t" + ret);
				}

				exec.shutdown();
			}

		}

	}

	public static void test_simpleselect_htable(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			SQLSelectStatement stmt = (SQLSelectStatement) iterator.next();
			SQLSelect sqlSelect = stmt.getSelect();
			MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlSelect.getQuery();

			String alias = query.getFrom().toString();
			SQLExpr where = query.getWhere();
			List<SQLSelectItem> item_list = query.getSelectList();
			Limit limit = query.getLimit();

			String current_hdb_name = "current_db";
			String hdb_name = "";
			String htable_name = "";
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				hdb_name = current_hdb_name;
				htable_name = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				hdb_name = alias_arr[0];
				htable_name = alias_arr[1];
			}

			Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);

			// 执行

			Iterator stmt_it = hdb_conn_map.keySet().iterator();
			final String sqlstr = stmt.toString();
			while (stmt_it.hasNext()) {
				Integer key = (Integer) stmt_it.next();
				Connection execconn = (Connection) hdb_conn_map.get(key);

				String execsql = sqlstr.replace(alias, htable_name);
				String logstr = "conn=" + key + ",sql=" + execsql;
				// System.out.println(logstr);
				try {
					Statement execstmt = execconn.createStatement();
					ResultSet rs = execstmt.executeQuery(execsql);

					ResultSetMetaData rsmd = rs.getMetaData();
					int count = rsmd.getColumnCount();
					String columns_str = "====result for " + key + "====\n";
					for (int i = 1; i <= count; i++) {
						columns_str = columns_str + rsmd.getColumnName(i) + rsmd.getColumnType(i) + "\t";
					}
					System.out.println(columns_str);
					while (rs.next()) {
						String line = "";
						for (int i = 1; i <= count; i++) {
							line = line + rs.getString(i) + "\t";
						}
						System.out.println(line);
					}
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}

			/*
			 * ExecutorService exec = null; HashMap taskMap = null;
			 * 
			 * exec = Executors.newFixedThreadPool(hdb_conn_map.size()); taskMap
			 * = new HashMap<String, Future>(); Iterator stmt_it =
			 * hdb_conn_map.keySet().iterator(); final String sqlstr =
			 * stmt.toString(); while (stmt_it.hasNext()) { final Integer key =
			 * (Integer) stmt_it.next(); final Connection execconn =
			 * (Connection) hdb_conn_map.get(key);
			 * 
			 * final String execsql = sqlstr.replace(alias, htable_name);
			 * Callable call = new Callable() { public String call() throws
			 * Exception {
			 * 
			 * String logstr = "conn=" + key + ",sql=" + execsql; //
			 * System.out.println(logstr); try { Statement stmt =
			 * execconn.createStatement(); ResultSet rs =
			 * stmt.executeQuery(execsql);
			 * 
			 * ResultSetMetaData rsmd = rs.getMetaData(); int count =
			 * rsmd.getColumnCount(); String columns_str = "====result for " +
			 * key + "====\n"; for (int i = 1; i <= count; i++) { columns_str =
			 * columns_str + rsmd.getColumnName(i) + rsmd.getColumnType(i) +
			 * "\t"; } System.out.println(columns_str); while (rs.next()) {
			 * String line = ""; for (int i = 1; i <= count; i++) { line = line
			 * + rs.getString(i) + "\t"; } System.out.println(line); } } catch
			 * (SQLException e) { System.out.println(e.getMessage()); return
			 * "FAIL"; } return "OK"; } }; Future task = exec.submit(call);
			 * taskMap.put(key, task); }
			 * 
			 * Iterator iter = taskMap.entrySet().iterator(); while
			 * (iter.hasNext()) { Map.Entry entry = (Map.Entry) iter.next();
			 * Integer key = (Integer) entry.getKey(); Future val = (Future)
			 * entry.getValue(); String ret = (String) val.get(); //
			 * System.out.println(key + "\t" + ret); }
			 * 
			 * exec.shutdown();
			 */
		}

	}

	// 只有一个hdc
	public static void recursive_expr(SQLExpr where, Map hdc_list_by_htable) throws Exception {
		SQLBinaryOpExpr var_where = (SQLBinaryOpExpr) where;
		SQLBinaryOperator binop = var_where.getOperator();

		SQLExpr left = var_where.getLeft();
		SQLExpr right = var_where.getRight();

		if (binop.isLogical()) {
			recursive_expr(left, hdc_list_by_htable);
			recursive_expr(right, hdc_list_by_htable);
		} else if (binop.isRelational()) {
			String left_str = left.toString();
			String right_str = left.toString();

			Iterator hdc_it = hdc_list_by_htable.keySet().iterator();
			while (hdc_it.hasNext()) {
				String key = (String) hdc_it.next();
				Hcolumn hc = (Hcolumn) hdc_list_by_htable.get(key);

				String tabcolname = hc.getHtable_name() + "." + hc.getHcolumn_name();
				String fullname = hc.getHdb_name() + "." + tabcolname;
				if (tabcolname.equalsIgnoreCase(left_str) || tabcolname.equalsIgnoreCase(right_str)) {

				}
			}

		} else {

		}

	}

	/*
	 * group 只能只字段 , order 只能只字段 或者 group 的聚合字段
	 * 
	 * 条件只考虑 = in > < <> like regexp
	 */
	public static void test_select(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			SQLSelectStatement stmt = (SQLSelectStatement) iterator.next();
			SQLSelect sqlSelect = stmt.getSelect();
			MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlSelect.getQuery();

			SQLTableSource from = query.getFrom();

			String fromstr = from.toString();
			SQLExpr where = query.getWhere();

			SQLSelectGroupByClause groupby = query.getGroupBy();
			SQLOrderBy orderby = query.getOrderBy();

			List<SQLSelectItem> item_list = query.getSelectList();
			Limit limit = query.getLimit();

			if (from == null) {
				// 直接从某个节点上执行
			}

			if (from instanceof SQLExprTableSource) {
				SQLExprTableSource varfrom = (SQLExprTableSource) from;

				String current_hdb_name = "current_db";
				String hdb_name = "";
				String htable_name = "";
				String[] alias_arr = fromstr.split("\\.");
				if (alias_arr.length == 1) {
					hdb_name = current_hdb_name;
					htable_name = alias_arr[0];
				}
				if (alias_arr.length == 2) {
					hdb_name = alias_arr[0];
					htable_name = alias_arr[1];
				}

				//
				Map hcolumn_list_by_htable = metadata.get_hcolumn_list_by_htable(hdb_name, htable_name);
				Map hdc_list_by_htable = metadata.get_hdc_list_by_htable(hdb_name, htable_name);
				Hdp table_hdp = metadata.get_hdp_by_htable(hdb_name, htable_name);

				// 判读where中是否包含 hdc的信息,以便发送到指定的节点
				SQLBinaryOpExpr var_where = (SQLBinaryOpExpr) where;
				SQLBinaryOperator binop = var_where.getOperator();

				while (binop.isLogical()) {
					SQLExpr left = var_where.getLeft();
					SQLExpr right = var_where.getRight();
				}
				if (binop.isLogical()) {

				} else if (binop.isRelational()) {
					SQLExpr left = var_where.getLeft();
					SQLExpr right = var_where.getRight();
					String left_str = left.toString();
					String right_str = left.toString();
				} else {

				}

				// 判读 group by 和 order by 是否包含 hdc , 以便可以直接到节点上执行,无需mapreduce

				//
				//
				// int hdc_found = 0;
				// // hdc 在 插入字段中的索引
				// List<Integer> hdc_index_list = new ArrayList();
				// Iterator hdc_it = hdc_list_by_htable.keySet().iterator();
				// while (hdc_it.hasNext()) {
				// String key = (String) hdc_it.next();
				// for (int i = 0; i < columns.size(); i++) {
				// SQLExpr sqlExpr = (SQLExpr) columns.get(i);
				// String hcolumn_name = sqlExpr.toString();
				// if (key.equalsIgnoreCase(hcolumn_name)) {
				// hdc_found++;
				// hdc_index_list.add(i);
				// }
				// }
				// }
				//
				// if (hdc_found == hdc_list_by_htable.size()) {
				// Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);
				// // 每个value该插入到那个节点的映射关系
				// Map<Integer, List> value_list_map = new HashMap<Integer,
				// List>();
				// Map<Integer, MySqlReplaceStatement> stmt_map = new
				// HashMap<Integer, MySqlReplaceStatement>();
				//
				// for (int i = 0; i < values.size(); i++) {
				// ValuesClause vc = (ValuesClause) values.get(i);
				// List<SQLExpr> onevalues = vc.getValues();
				// List hdc_value_list = new ArrayList();
				// for (Integer idx : hdc_index_list) {
				// hdc_value_list.add(onevalues.get(idx).toString());
				// }
				// int nodedb_idx = find_record_nodedb(hdc_value_list,
				// table_hdp);
				//
				// if (nodedb_idx >= 0 && nodedb_idx < hdb_conn_map.size()) {
				//
				// List value_list = value_list_map.get(nodedb_idx);
				// MySqlReplaceStatement nodedb_stmt = stmt_map.get(nodedb_idx);
				// if (value_list == null && nodedb_stmt == null) {
				// value_list = new ArrayList();
				// nodedb_stmt = new MySqlReplaceStatement();
				//
				// value_list_map.put(nodedb_idx, value_list);
				// stmt_map.put(nodedb_idx, nodedb_stmt);
				//
				// nodedb_stmt.setTableSource(stmt.getTableSource());
				// nodedb_stmt.setDbType(dbType);
				// nodedb_stmt.getColumns().addAll(columns);
				// }
				// nodedb_stmt.getValuesList().add(vc);
				// }
				// }
				//
				// Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);
			}
			if (from instanceof SQLJoinTableSource) {
				SQLJoinTableSource varfrom = (SQLJoinTableSource) from;
			}
			if (from instanceof SQLSubqueryTableSource) {
				SQLSubqueryTableSource varfrom = (SQLSubqueryTableSource) from;
			}
			if (from instanceof SQLUnionQueryTableSource) {
				SQLUnionQueryTableSource varfrom = (SQLUnionQueryTableSource) from;
			}

			// 执行
			//
			// ExecutorService exec = null;
			// HashMap taskMap = null;
			//
			// exec = Executors.newFixedThreadPool(hdb_conn_map.size());
			// taskMap = new HashMap<String, Future>();
			// Iterator stmt_it = hdb_conn_map.keySet().iterator();
			// final String sqlstr = stmt.toString();
			// while (stmt_it.hasNext()) {
			// final Integer key = (Integer) stmt_it.next();
			// final Connection execconn = (Connection) hdb_conn_map.get(key);
			//
			// final String execsql = sqlstr.replace(fromstr, htable_name);
			// Callable call = new Callable() {
			// public String call() throws Exception {
			//
			// String logstr = "conn=" + key + ",sql=" + execsql;
			// System.out.println(logstr);
			// try {
			// Statement stmt = execconn.createStatement();
			// ResultSet rs = stmt.executeQuery(execsql);
			//
			// ResultSetMetaData rsmd = rs.getMetaData();
			// int count = rsmd.getColumnCount();
			// String columns_str = "";
			// for (int i = 1; i <= count; i++) {
			// columns_str = columns_str + rsmd.getColumnName(i) +
			// rsmd.getColumnType(i) + "\t";
			// }
			// System.out.println(columns_str);
			// while (rs.next()) {
			// String line = "";
			// for (int i = 1; i <= count; i++) {
			// line = line + rs.getString(i) + "\t";
			// }
			// System.out.println(line);
			// }
			// } catch (SQLException e) {
			// System.out.println(e.getMessage());
			// return "FAIL";
			// }
			// return "OK";
			// }
			// };
			// Future task = exec.submit(call);
			// taskMap.put(key, task);
			// }
			//
			// Iterator iter = taskMap.entrySet().iterator();
			// while (iter.hasNext()) {
			// Map.Entry entry = (Map.Entry) iter.next();
			// Integer key = (Integer) entry.getKey();
			// Future val = (Future) entry.getValue();
			// String ret = (String) val.get();
			// System.out.println(key + "\t" + ret);
			// }
			//
			// exec.shutdown();
		}

	}

	public static void test_delete_htable(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlDeleteStatement stmt = (MySqlDeleteStatement) iterator.next();

			String alias = stmt.getTableSource().toString();
			SQLExpr sqlExpr = stmt.getWhere();
			String current_hdb_name = "current_db";
			String hdb_name = "";
			String htable_name = "";
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				hdb_name = current_hdb_name;
				htable_name = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				hdb_name = alias_arr[0];
				htable_name = alias_arr[1];
			}

			Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);

			// 执行

			ExecutorService exec = null;
			HashMap taskMap = null;

			exec = Executors.newFixedThreadPool(hdb_conn_map.size());
			taskMap = new HashMap<String, Future>();
			Iterator stmt_it = hdb_conn_map.keySet().iterator();
			final String sqlstr = stmt.toString();
			while (stmt_it.hasNext()) {
				final Integer key = (Integer) stmt_it.next();
				final Connection execconn = (Connection) hdb_conn_map.get(key);

				final String execsql = sqlstr.replace(alias, htable_name);
				Callable call = new Callable() {
					public String call() throws Exception {

						String logstr = "conn=" + key + ",sql=" + execsql;
						System.out.println(logstr);
						try {
							Statement stmt = execconn.createStatement();
							stmt.execute(execsql);
						} catch (SQLException e) {
							System.out.println(e.getMessage());
							return "FAIL";
						}
						return "OK";
					}
				};
				Future task = exec.submit(call);
				taskMap.put(key, task);
			}

			Iterator iter = taskMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Integer key = (Integer) entry.getKey();
				Future val = (Future) entry.getValue();
				String ret = (String) val.get();
				System.out.println(key + "\t" + ret);
			}

			exec.shutdown();
		}

	}

	public static void test_create_hdb(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			SQLCreateDatabaseStatement stmt = (SQLCreateDatabaseStatement) iterator.next();
			List<SQLCommentHint> hints = stmt.getHints();
			String hdb_name = stmt.getName().getSimpleName();
			String create_hdb_sql = stmt.toString();
			if (hints.size() == 1) {
				String hintstr = hints.get(0).getText().trim().substring(1).trim();
				String[] hintoption_arr = hintstr.split(" +");
				if (hintoption_arr.length >= 1) {
					String hintoption = hintoption_arr[0];
					String hintkey = hintoption.split("=")[0];
					String hintvalue = hintoption.split("=")[1];
					String[] instance_arr = hintvalue.split(",");
					if (hintkey.equalsIgnoreCase("nodeinstance_arr") && instance_arr.length >= 1) {

						System.out.println(Arrays.toString(instance_arr));
						Map hdb_conn_map = new HashMap();
						for (int i = 0; i < instance_arr.length; i++) {
							String nodedb = instance_arr[i];

							int lastslashidx = nodedb.lastIndexOf("/");
							String nodeinstance_name = nodedb.substring(0, lastslashidx);
							String nodedb_name = nodedb.substring(lastslashidx + 1);
							Connection conn = (Connection) MetaDataImpl.nodeinstance_conn_map.get(nodeinstance_name);
							if (conn == null) {
								conn = DriverManager.getConnection(nodeinstance_name, MetaDataImpl.user,
										MetaDataImpl.password);
								conn.setAutoCommit(true);

								String replacesql = "replace into nodeinstance(nodeinstance_name) values (?)";
								PreparedStatement pst = metadata.metadataConn.prepareStatement(replacesql);

								pst.setString(1, nodeinstance_name);
								pst.execute();

								MetaDataImpl.nodeinstance_conn_map.put(nodeinstance_name, conn);
								System.out.println(nodeinstance_name + " connection opened !");
							}

							String exec_sql = create_hdb_sql.replace(hdb_name, nodedb_name);
							PreparedStatement exec_pst = conn.prepareStatement(exec_sql);
							exec_pst.execute();
							exec_pst.close();

							Connection nodedb_conn = DriverManager.getConnection(nodedb, MetaDataImpl.user,
									MetaDataImpl.password);
							nodedb_conn.setAutoCommit(true);

							String replacesql = "replace into nodedb(nodedb_name,nodeinstance_name,hdb_name,hdb_orderno) values (?,?,?,?)";
							PreparedStatement pst = metadata.metadataConn.prepareStatement(replacesql);

							pst.setString(1, nodedb_name);
							pst.setString(2, nodeinstance_name);
							pst.setString(3, hdb_name);
							pst.execute();
							pst.close();

							hdb_conn_map.put(nodedb, nodedb_conn);
						}

						MetaDataImpl.allhdb_conn_map.put(hdb_name, hdb_conn_map);
						System.out.println(hdb_name + " created !");
					}
				}
			}
		}
	}

	public static String str_create_hdc = "create\\s+hdp\\s+([A-Za-z][A-Za-z0-9]+)\\s+type\\s+([A-Za-z][A-Za-z0-9]+)\\s+algo\\s+([A-Za-z][A-Za-z0-9]+)\\s+min\\s+([0-9]+)\\s+max\\s+([0-9]+)\\s+step\\s+([0-9]+)";
	public static Pattern pat_create_hdc = Pattern.compile(str_create_hdc);

	public static void test_create_hdp(String sql) throws Exception {

		Matcher m = pat_create_hdc.matcher(sql);
		if (m.find()) {
			String hdp_name = m.group(1);
			String hdp_type = m.group(2);
			String hdp_algo = m.group(3);
			String hdp_min = m.group(4);
			String hdp_max = m.group(5);
			String hdp_step = m.group(6);
			String replacesql = "replace into hdp(hdp_name,hdp_type,hdp_algo,hdp_min,hdp_max,hdp_step) values (?,?,?,?,?,?)";
			PreparedStatement pst = metadata.metadataConn.prepareStatement(replacesql);

			pst.setString(1, hdp_name);
			pst.setString(2, hdp_type);
			pst.setString(3, hdp_algo);
			pst.setString(4, hdp_min);
			pst.setString(5, hdp_max);
			pst.setString(6, hdp_step);
			pst.execute();

			System.out.println(hdp_name + " created !");
		}
	}

	public static void test_create_table(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) iterator.next();

			String current_hdb_name = "current_db";
			String hdb_name = "";
			String htable_name = "";
			String hdp_name = "";

			String alias = stmt.getTableSource().getExpr().toString();
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				hdb_name = current_hdb_name;
				htable_name = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				hdb_name = alias_arr[0];
				htable_name = alias_arr[1];
			}

			List<SQLCommentHint> hints = stmt.getHints();
			List<SQLTableElement> element_list = stmt.getTableElementList();
			Map hcolumn_namemap = new HashMap();
			Map hindex_namemap = new HashMap();
			List rhindexhcolumn_list = new ArrayList();
			int htable_orderno = 0;

			for (Iterator iterator2 = element_list.iterator(); iterator2.hasNext();) {
				SQLTableElement sqlTableElement = (SQLTableElement) iterator2.next();

				if (sqlTableElement instanceof SQLColumnDefinition) {
					SQLColumnDefinition ele = (SQLColumnDefinition) sqlTableElement;
					String hcolumn_name = ele.getName().getSimpleName();
					String hcolumn_type = ele.getDataType().getName();

					Hcolumn h = new Hcolumn();
					h.setHdb_name(hdb_name);
					h.setHtable_name(htable_name);
					h.setHcolumn_name(hcolumn_name);
					h.setHcolumn_type(hcolumn_type);
					h.setHtable_orderno(htable_orderno);
					h.setHdc_flag(0);
					h.setHdc_orderno(0);
					hcolumn_namemap.put(hcolumn_name, h);
					htable_orderno++;
				}

				if (sqlTableElement instanceof MySqlPrimaryKey) {
					MySqlPrimaryKey ele = (MySqlPrimaryKey) sqlTableElement;
					String hindex_name = "PrimaryKey";
					List<SQLExpr> columns = ele.getColumns();

					Hindex h = new Hindex();
					h.setHdb_name(hdb_name);
					h.setHtable_name(htable_name);
					h.setHindex_name(hindex_name);
					h.setHindex_type("P");

					for (int i = 0; i < columns.size(); i++) {
						SQLExpr sqlExpr = (SQLExpr) columns.get(i);

						Rhindexhcolumn r = new Rhindexhcolumn();
						r.setHdb_name(hdb_name);
						r.setHtable_name(htable_name);
						r.setHindex_name(hindex_name);
						r.setHcolumn_name(sqlExpr.toString());
						r.setHcolumn_orderno(i);
						rhindexhcolumn_list.add(r);
					}

					hindex_namemap.put(hindex_name, h);
				} else if (sqlTableElement instanceof MySqlUnique) {
					MySqlUnique ele = (MySqlUnique) sqlTableElement;

					String hindex_name = ele.getIndexName().getSimpleName();
					List<SQLExpr> columns = ele.getColumns();

					Hindex h = new Hindex();
					h.setHdb_name(hdb_name);
					h.setHtable_name(htable_name);
					h.setHindex_name(hindex_name);
					h.setHindex_type("U");

					for (int i = 0; i < columns.size(); i++) {
						SQLExpr sqlExpr = (SQLExpr) columns.get(i);

						Rhindexhcolumn r = new Rhindexhcolumn();
						r.setHdb_name(hdb_name);
						r.setHtable_name(htable_name);
						r.setHindex_name(hindex_name);
						r.setHcolumn_name(sqlExpr.toString());
						r.setHcolumn_orderno(i);
						rhindexhcolumn_list.add(r);
					}

					hindex_namemap.put(hindex_name, h);
				} else if (sqlTableElement instanceof MySqlKey) {
					MySqlKey ele = (MySqlKey) sqlTableElement;

					String hindex_name = ele.getIndexName().getSimpleName();
					List<SQLExpr> columns = ele.getColumns();

					Hindex h = new Hindex();
					h.setHdb_name(hdb_name);
					h.setHtable_name(htable_name);
					h.setHindex_name(hindex_name);
					h.setHindex_type("K");

					for (int i = 0; i < columns.size(); i++) {
						SQLExpr sqlExpr = (SQLExpr) columns.get(i);

						Rhindexhcolumn r = new Rhindexhcolumn();
						r.setHdb_name(hdb_name);
						r.setHtable_name(htable_name);
						r.setHindex_name(hindex_name);
						r.setHcolumn_name(sqlExpr.toString());
						r.setHcolumn_orderno(i);
						rhindexhcolumn_list.add(r);
					}

					hindex_namemap.put(hindex_name, h);
				} else {

				}

			}

			if (hints.size() == 1) {
				String hintstr = hints.get(0).getText().trim().substring(1).trim();
				String[] hintoption_arr = hintstr.split(" +");
				if (hintoption_arr.length >= 2) {
					for (int i = 0; i < hintoption_arr.length; i++) {

						String hintoption = hintoption_arr[i];
						String hintkey = hintoption.split("=")[0];
						String hintvalue = hintoption.split("=")[1];
						if (hintkey.equalsIgnoreCase("hdp")) {
							hdp_name = hintvalue;
						}
						if (hintkey.equalsIgnoreCase("hdc")) {
							String[] hdc_arr = hintvalue.split(",");
							System.out.println(Arrays.toString(hdc_arr));
							for (int j = 0; j < hdc_arr.length; j++) {
								String hdc = hdc_arr[j];
								Hcolumn h = (Hcolumn) hcolumn_namemap.get(hdc);
								h.setHdc_flag(1);
								h.setHdc_orderno(j);
							}
						}

					}
				}
			}

			// check hdb_name和hdp_name 是否存在
			// Htable htable = new Htable(hdb_name, htable_name, hdp_name);

			String htable_sql = "replace into htable(hdb_name,htable_name,hdp_name) values (?,?,?)";
			String hindex_sql = "replace into hindex(hdb_name,htable_name,hindex_name,hindex_type) values (?,?,?,?)";
			String hcolumn_sql = "replace into hcolumn(hdb_name,htable_name,hcolumn_name,hcolumn_type,htable_orderno,hdc_flag,hdc_orderno) values (?,?,?,?,?,?,?)";
			String rhindexhcolumn_sql = "replace into rhindexhcolumn(hdb_name,htable_name,hindex_name,hcolumn_name,hcolumn_orderno) values (?,?,?,?,?)";

			PreparedStatement htable_ps = metadata.metadataConn.prepareStatement(htable_sql);
			PreparedStatement hindex_ps = metadata.metadataConn.prepareStatement(hindex_sql);
			PreparedStatement hcolumn_ps = metadata.metadataConn.prepareStatement(hcolumn_sql);
			PreparedStatement rhindexhcolumn_ps = metadata.metadataConn.prepareStatement(rhindexhcolumn_sql);

			htable_ps.setString(1, hdb_name);
			htable_ps.setString(2, htable_name);
			htable_ps.setString(3, hdp_name);
			htable_ps.execute();
			htable_ps.close();

			Iterator hindex_it = hindex_namemap.keySet().iterator();
			while (hindex_it.hasNext()) {
				String key = (String) hindex_it.next();
				Hindex object = ((Hindex) hindex_namemap.get(key));
				hindex_ps.setString(1, object.getHdb_name());
				hindex_ps.setString(2, object.getHtable_name());
				hindex_ps.setString(3, object.getHindex_name());
				hindex_ps.setString(4, object.getHindex_type());
				hindex_ps.addBatch();
			}
			hindex_ps.executeBatch();
			hindex_ps.close();

			Iterator hcolumn_it = hcolumn_namemap.keySet().iterator();
			while (hcolumn_it.hasNext()) {
				String key = (String) hcolumn_it.next();
				Hcolumn object = ((Hcolumn) hcolumn_namemap.get(key));
				hcolumn_ps.setString(1, object.getHdb_name());
				hcolumn_ps.setString(2, object.getHtable_name());
				hcolumn_ps.setString(3, object.getHcolumn_name());
				hcolumn_ps.setString(4, object.getHcolumn_type());
				hcolumn_ps.setInt(5, object.getHtable_orderno());
				hcolumn_ps.setInt(6, object.getHdc_flag());
				hcolumn_ps.setInt(7, object.getHdc_orderno());
				hcolumn_ps.addBatch();
			}
			hcolumn_ps.executeBatch();
			hcolumn_ps.close();

			for (Iterator iterator2 = rhindexhcolumn_list.iterator(); iterator2.hasNext();) {
				Rhindexhcolumn object = (Rhindexhcolumn) iterator2.next();
				rhindexhcolumn_ps.setString(1, object.getHdb_name());
				rhindexhcolumn_ps.setString(2, object.getHtable_name());
				rhindexhcolumn_ps.setString(3, object.getHindex_name());
				rhindexhcolumn_ps.setString(4, object.getHcolumn_name());
				rhindexhcolumn_ps.setInt(5, object.getHcolumn_orderno());
				rhindexhcolumn_ps.addBatch();

			}
			rhindexhcolumn_ps.executeBatch();
			rhindexhcolumn_ps.close();

			String exec_sql = stmt.toString().replace(alias, htable_name);
			Map hdb_conn_map = metadata.open_conn_by_hdb(hdb_name);
			Iterator conn_it = hdb_conn_map.keySet().iterator();
			while (conn_it.hasNext()) {
				String key = (String) conn_it.next();
				Connection conn = ((Connection) hdb_conn_map.get(key));
				PreparedStatement exec_pst = conn.prepareStatement(exec_sql);
				exec_pst.execute();
				exec_pst.close();
				System.out.println(hdb_name + "." + htable_name + " created in " + key);

			}
			System.out.println(hdb_name + "." + htable_name + " created !");
		}
	}

	private static void test_select_meta() throws Exception {
		MetaDataImpl d = new MetaDataImpl();
		d.getConnection();
		d.getAllMeta();
	}

	private static MetaDataImpl metadata = new MetaDataImpl();

}
