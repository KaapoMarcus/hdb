package com.jhh.hdb.meta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLShowTablesStatement;
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
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.alibaba.druid.util.JdbcUtils;
import com.jhh.hdb.proxyserver.define.ServerStatus;
import com.jhh.hdb.visitor.MapReduceOutputVisitor_T2;

@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class Test {

	/*
	 * 得到最大最小id和记录数 ，使用多线程
	 * 
	 * 得到表结构不用多线程，建表不用多线程
	 * 
	 * map shuffle reduce 使用多线程
	 */
	final static String base_create_table_sql = "create table <table> ( <cols_str> <index_str>) ;";
	final static String base_id_sql = "select min(<shuffle_field>) minid , max(<shuffle_field>) maxid, count(1) datacount from <table>  ";
	final static String base_load_sql = "LOAD DATA LOCAL INFILE '<map_outfile>' replace into table <table> fields terminated by '\\t' lines terminated by '\\n' ;";
	final static String finaltab = "final_table";
	final static String base_out_sql = "select * from <table>  ;";

	static int return_type = 0; // 0 直接返回到客户端 , 1 结果存储在某个表中

	static String current_hdb_name = "mytesthdb2";

	public static String COLON = ":";
	public static String COMMA = ",";
	public static String D_QUOTE = "\"";
	public static String EMPTY = "";
	public static String FAILED = "FAILED";
	public static String MINUS = "-";
	public static String NEWLINE = "\n";
	public static String OK = "OK";
	public static String POINT = ".";
	public static String S_QUOTE = "'";
	public static String SEMI = ";";
	public static String SPACE = " ";
	public static String TAB = "	";
	public static String UNDERLINE = "_";
	public static String UNKNOW = "unknow";
	public static String VERTICAL_LINE = "|";
	public static String WAVY = "~";
	static DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	static DateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	static String log_suffix = ".log";

	static String logfile = null;
	static OutputStreamWriter logosw = null;
	static String logstr = null;
	static String pass = "yunjee0515ueopro1234";
	static String replacesql = null;
	static String result_suffix = ".txt";
	static String resultfile = null;
	static OutputStreamWriter resultosw = null;
	static String resultstr = null;
	static DateFormat spacedatetimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// static String sqlstr = null;
	// static String start_str = "start ";
	// static String step = EMPTY;
	// static String stop_str = "stop ";
	static String user = "root";
	static String workDir = "/data/";

	static ExecutorService x = null;

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

		sql = StringTemplateUtils.read_stat("test_select_1");
		test_select(sql);
		
//		sql = StringTemplateUtils.read_stat("test_select_1");
//		test_select(sql);

		// sql = StringTemplateUtils.read_stat("q_drop_table_uinfo");
		// test_drop_table(sql);
		// sql = StringTemplateUtils.read_stat("q_drop_table_ulogin");
		// test_drop_table(sql);
		// sql = StringTemplateUtils.read_stat("q_drop_table_ubank");
		// test_drop_table(sql);
		//
		// sql = StringTemplateUtils.read_stat("q_create_database");
		// test_create_hdb(sql);
		// sql = StringTemplateUtils.read_stat("q_create_hdp_hdp1");
		// test_create_hdp(sql);
		//
		// sql = StringTemplateUtils.read_stat("q_create_table_uinfo");
		// test_create_table(sql);
		// sql = StringTemplateUtils.read_stat("q_create_table_ulogin");
		// test_create_table(sql);
		// sql = StringTemplateUtils.read_stat("q_create_table_ubank");
		// test_create_table(sql);
		// sql = StringTemplateUtils.read_stat("q_load_uinfo");
		// test_load_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_load_ulogin");
		// test_load_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_load_ubank");
		// test_load_htable(sql);
		//
		// sql = StringTemplateUtils.read_stat("q_simpleselect_uinfo");
		// test_simpleselect_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_simpleselect_ulogin");
		// test_simpleselect_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_simpleselect_ubank");
		// test_simpleselect_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_delete_uinfo");
		// test_delete_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_delete_ulogin");
		// test_delete_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_delete_ubank");
		// test_delete_htable(sql);
		//
		// sql = StringTemplateUtils.read_stat("q_simpleselect_uinfo");
		// test_simpleselect_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_simpleselect_ulogin");
		// test_simpleselect_htable(sql);
		// sql = StringTemplateUtils.read_stat("q_simpleselect_ubank");
		// test_simpleselect_htable(sql);
		//

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

			if (return_type == 0) {

				// 单线程执行

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
							columns_str = columns_str + rsmd.getColumnName(i) + "\t";
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

				// 多线程执行
				/*
				 * ExecutorService exec = null; HashMap taskMap = null;
				 * 
				 * exec = Executors.newFixedThreadPool(hdb_conn_map.size());
				 * taskMap = new HashMap<String, Future>(); Iterator stmt_it =
				 * hdb_conn_map.keySet().iterator(); final String sqlstr =
				 * stmt.toString(); while (stmt_it.hasNext()) { final Integer
				 * key = (Integer) stmt_it.next(); final Connection execconn =
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
				 * rsmd.getColumnCount(); String columns_str = "====result for "
				 * + key + "====\n"; for (int i = 1; i <= count; i++) {
				 * columns_str = columns_str + rsmd.getColumnName(i) + "\t"; }
				 * System.out.println(columns_str); while (rs.next()) { String
				 * line = ""; for (int i = 1; i <= count; i++) { line = line +
				 * rs.getString(i) + "\t"; } System.out.println(line); } } catch
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
			if (return_type == 1) {

				// 执行
				String final_tablename = getMapTableName();
				String crt_sql = "";
				Iterator get_crt_it = hdb_conn_map.keySet().iterator();
				final String sqlstr = stmt.toString();
				boolean oneflag = true;
				while (get_crt_it.hasNext() && oneflag) {
					Integer key = (Integer) get_crt_it.next();
					Connection execconn = (Connection) hdb_conn_map.get(key);

					String execsql = sqlstr.replace(alias, htable_name);
					String logstr = "conn=" + key + ",sql=" + execsql;
					// System.out.println(logstr);
					try {
						Statement execstmt = execconn.createStatement();
						ResultSet rs = execstmt.executeQuery(execsql);

						ResultSetMetaData rsmd = rs.getMetaData();

						Map<Integer, ColumnInfo> col_map = get_col_map(rsmd);

						String cols_str = get_col_definition_str(col_map, true);

						crt_sql = base_create_table_sql.replaceAll("<table>", final_tablename)
								.replaceAll("<cols_str>", cols_str).replaceAll("<index_str>", "");
						oneflag = false;
					} catch (SQLException e) {
						System.out.println(e.getMessage());
					}
				}

				// 执行创建表结构
				ExecutorService exec = null;
				HashMap taskMap = null;

				exec = Executors.newFixedThreadPool(hdb_conn_map.size());
				taskMap = new HashMap<String, Future>();
				Iterator crt_it = hdb_conn_map.keySet().iterator();

				while (crt_it.hasNext()) {
					final Integer key = (Integer) crt_it.next();
					final Connection execconn = (Connection) hdb_conn_map.get(key);

					final String execsql = crt_sql;
					Callable call = new Callable() {
						public String call() throws Exception {

							String logstr = "conn=" + key + ",sql=" + execsql; //
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

				Iterator crt_ret_it = taskMap.entrySet().iterator();
				while (crt_ret_it.hasNext()) {
					Map.Entry entry = (Map.Entry) crt_ret_it.next();
					Integer key = (Integer) entry.getKey();
					Future val = (Future) entry.getValue();
					String ret = (String) val.get(); //
					System.out.println(key + "\t" + ret);
				}

				exec.shutdown();

				// 最终执行sql,将结果放入结果表中
				exec = Executors.newFixedThreadPool(hdb_conn_map.size());
				taskMap = new HashMap<String, Future>();
				Iterator final_exec_it = hdb_conn_map.keySet().iterator();

				while (final_exec_it.hasNext()) {
					final Integer key = (Integer) final_exec_it.next();
					final Connection execconn = (Connection) hdb_conn_map.get(key);

					final String execsql = "replace into " + final_tablename + " " + sqlstr.replace(alias, htable_name);
					Callable call = new Callable() {
						public String call() throws Exception {

							String logstr = "conn=" + key + ",sql=" + execsql; //
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

				Iterator iter1 = taskMap.entrySet().iterator();
				while (iter1.hasNext()) {
					Map.Entry entry = (Map.Entry) iter1.next();
					Integer key = (Integer) entry.getKey();
					Future val = (Future) entry.getValue();
					String ret = (String) val.get();
					System.out.println(key + "\t" + ret);
				}

				exec.shutdown();
			}
		}

	}

	static String alphaUp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static String alphaDown = "abcdefghijklmnopqrstuvwxyz";
	static String numbase = "0123456789";

	private static String getMapTableName() {

		Random rndstr = new Random();
		StringBuffer sb = new StringBuffer("map_");
		for (int j = 0; j < 4; j++) {
			int number = rndstr.nextInt(alphaUp.length());
			sb.append(alphaUp.charAt(number));
		}
		sb.append("_");
		for (int j = 0; j < 4; j++) {
			int number = rndstr.nextInt(alphaDown.length());
			sb.append(alphaDown.charAt(number));
		}
		sb.append("_");
		for (int j = 0; j < 4; j++) {
			int number = rndstr.nextInt(numbase.length());
			sb.append(numbase.charAt(number));
		}
		return sb.toString();
	}

	private static String getReduceTableName() {

		Random rndstr = new Random();
		StringBuffer sb = new StringBuffer("reduce_");
		for (int j = 0; j < 4; j++) {
			int number = rndstr.nextInt(alphaUp.length());
			sb.append(alphaUp.charAt(number));
		}
		sb.append("_");
		for (int j = 0; j < 4; j++) {
			int number = rndstr.nextInt(alphaDown.length());
			sb.append(alphaDown.charAt(number));
		}
		sb.append("_");
		for (int j = 0; j < 4; j++) {
			int number = rndstr.nextInt(numbase.length());
			sb.append(numbase.charAt(number));
		}
		return sb.toString();
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

	public static MyResultSetCmd getResultSetCmd(MapReduceNode node) throws Exception {

		Htable ti = node.output;
		Map hdb_conn_map = metadata.open_idconnmap_by_hdb(ti.hdb_name);
		int cnt = hdb_conn_map.size();
		String sqlstr = base_out_sql.replaceAll("<table>", ti.htable_name);
		ResultSetMetaData rsmd;
		MyResultSet mrs = new MyResultSet();

		ResultSet[] rs_arr = new ResultSet[cnt];
		for (int i = 0; i < cnt; i++) {

			Connection conn = (Connection) hdb_conn_map.get(i);

			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sqlstr);
				if (i == 0) {
					rsmd = rs.getMetaData();
					mrs.init(rsmd);
				}
				rs_arr[i] = rs;

			} catch (SQLException e) {
				System.exit(1);
			}

		}

		ResultSetOperator rso = new ResultSetOperator(rs_arr);
		mrs.setResultSetOperator(rso);
		return new MyResultSetCmd(mrs, null, false, ServerStatus.SERVER_STATUS_AUTOCOMMIT);
	}


	public static void test_visit(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			SQLSelectStatement stmt = (SQLSelectStatement) iterator.next();
			SQLSelect sqlSelect = stmt.getSelect();
			MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlSelect.getQuery();

			SQLTableSource from = query.getFrom();

			SQLExpr where = query.getWhere();
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
			
			MapReduceOutputVisitor_T2 visitor = new MapReduceOutputVisitor_T2(null);
			visitor.visit(stmt);
		
		}

	}

	private static void do_nofrom_select(SQLSelectStatement stmt) throws Exception, SQLException {
		// 直接从某个节点上执行
		MetaDataImpl d = new MetaDataImpl();
		Connection conn = d.getConnection();

		Statement execstmt = conn.createStatement();
		ResultSet rs = execstmt.executeQuery(stmt.toString());

		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		String columns_str = "====result===\n";
		for (int i = 1; i <= count; i++) {
			String CatalogName = rsmd.getCatalogName(i);
			String SchemaName = rsmd.getSchemaName(i);

			String TableName = rsmd.getTableName(i);
			String ColumnName = rsmd.getColumnName(i);
			String ColumnLabel = rsmd.getColumnLabel(i);
			String ColumnTypeName = rsmd.getColumnTypeName(i);

			int ColumnType = rsmd.getColumnType(i);
			int ColumnDisplaySize = rsmd.getColumnDisplaySize(i);
			int Scale = rsmd.getScale(i);
			int Precision = rsmd.getPrecision(i);
			boolean isSigned = rsmd.isSigned(i);
			String ColumnClassName = rsmd.getColumnClassName(i);

			// columns_str = columns_str + rsmd.getColumnName(i) + "\t";

			columns_str = columns_str + "[CatalogName=" + CatalogName + ", SchemaName=" + SchemaName + ", TableName="
					+ TableName + ", ColumnName=" + ColumnName + ", ColumnLabel=" + ColumnLabel + ", ColumnTypeName="
					+ ColumnTypeName + ", ColumnClassName=" + ColumnClassName + ", ColumnType=" + ColumnType
					+ ", ColumnDisplaySize=" + ColumnDisplaySize + ", Scale=" + Scale + ", Precision=" + Precision
					+ ", isSigned=" + isSigned + "]" + "\t";
		}
		System.out.println(columns_str);
		while (rs.next()) {
			String line = "";
			for (int i = 1; i <= count; i++) {
				line = line + rs.getString(i) + "\t";
			}
			System.out.println(line);
		}
	}

	private static void do_from_jointable(SQLSelectStatement stmt) throws Exception, SQLException {

		SQLSelect sqlSelect = stmt.getSelect();
		MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlSelect.getQuery();

		SQLTableSource from = query.getFrom();

		SQLExpr where = query.getWhere();

		SQLSelectGroupByClause groupby = query.getGroupBy();
		SQLOrderBy orderby = query.getOrderBy();

		List<SQLSelectItem> item_list = query.getSelectList();
		Limit limit = query.getLimit();

		String fromstr = from.toString();
		SQLJoinTableSource varfrom = (SQLJoinTableSource) from;

		List tablename_list = new ArrayList();
		List tablealias_list = new ArrayList();
		SQLTableSource left = varfrom.getLeft();
		JoinType joinType = varfrom.getJoinType();
		SQLTableSource right = varfrom.getRight();
		SQLExpr condition = varfrom.getCondition();

		String left_alias = left.getAlias();
		String right_alias = right.getAlias();

		if (left instanceof SQLExprTableSource) {
			SQLExprTableSource tmp_tab =  (SQLExprTableSource) left;

			tablename_list.add(tmp_tab.getExpr().toString());
			tablealias_list.add(tmp_tab.getAlias());
		}

		while (right instanceof SQLJoinTableSource) {
			varfrom = (SQLJoinTableSource) right;
			left = varfrom.getLeft();
			joinType = varfrom.getJoinType();
			right = varfrom.getRight();
			condition = varfrom.getCondition();
			if (left instanceof SQLExprTableSource) {
				SQLExprTableSource tmp_tab =  (SQLExprTableSource) left;

				tablename_list.add(tmp_tab.getExpr().toString());
				tablealias_list.add(tmp_tab.getAlias());
			}
		}

		if (right instanceof SQLExprTableSource) {
			SQLExprTableSource tmp_tab =  (SQLExprTableSource) right;
			tablename_list.add(tmp_tab.getExpr().toString());
			tablealias_list.add(tmp_tab.getAlias());
		}

		final String map_tablename = getMapTableName();
		final String reduce_tablename = getReduceTableName();
		MapReduceGroupNode node00 = new MapReduceGroupNode(0, 0, "node00", null, NodeType.GROUP, stmt);

		MySqlSelectQueryBlock map_query = new MySqlSelectQueryBlock();
		map_query.setFrom(from);
		map_query.setWhere(where);
		SQLSelect map_sqlSelect = new SQLSelect(map_query);
		SQLSelectStatement map_stmt = new SQLSelectStatement(map_sqlSelect);

		MySqlSelectQueryBlock reduce_query = new MySqlSelectQueryBlock();
		SQLSelect reduce_sqlSelect = new SQLSelect(reduce_query);
		SQLSelectStatement reduce_stmt = new SQLSelectStatement(reduce_sqlSelect);
		reduce_query.setFrom(from);
		reduce_query.setWhere(null);
		reduce_query.setGroupBy(groupby);

		int inner_alias_orderno = 0;
		for (Iterator iterator2 = item_list.iterator(); iterator2.hasNext();) {
			SQLSelectItem sqlSelectItem = (SQLSelectItem) iterator2.next();

			parse_item(sqlSelectItem);
			// inner_alias_orderno = parse_expr(map_query, reduce_query,
			// inner_alias_orderno, sqlSelectItem, alias, expr);

		}

	}

	private static void parse_item(SQLSelectItem sqlSelectItem) {
		String alias = sqlSelectItem.getAlias();
		SQLExpr expr = sqlSelectItem.getExpr();

		reverse(alias, expr);
	}

	private static void reverse(String alias, SQLExpr expr) {
		if (expr instanceof SQLMethodInvokeExpr) {
			SQLMethodInvokeExpr x = (SQLMethodInvokeExpr) expr;
			String methodName = x.getMethodName();
			List<SQLExpr> params = x.getParameters();

			SQLObject parent = x.getParent();
			SQLExpr owner = x.getOwner();
			for (Iterator iterator = params.iterator(); iterator.hasNext();) {
				SQLExpr sqlExpr = (SQLExpr) iterator.next();
				reverse(null, sqlExpr);
			}
		}
		if (expr instanceof SQLInListExpr) {
			SQLInListExpr top_expr = (SQLInListExpr) expr;
			boolean isnot = top_expr.isNot();
			List<SQLExpr> target_list = top_expr.getTargetList();

		}
		if (expr instanceof SQLIntegerExpr) {
			SQLIntegerExpr x = (SQLIntegerExpr) expr;
		}
		if (expr instanceof SQLCharExpr) {
			SQLCharExpr x = (SQLCharExpr) expr;
		}
		if (expr instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr top_expr = (SQLBinaryOpExpr) expr;
			SQLBinaryOperator top_op = top_expr.getOperator();

			if (top_op == SQLBinaryOperator.BooleanOr) {

				Map<Integer, SQLExpr> or_map = new HashMap<Integer, SQLExpr>();
				Map<Integer, Map<Integer, SQLExpr>> and_map = new HashMap<Integer, Map<Integer, SQLExpr>>();

				int or_idx = 0;
				SQLBinaryOpExpr or_left = top_expr;
				SQLBinaryOpExpr or_right;

				SQLBinaryOperator or_left_op = top_op;
				SQLBinaryOperator or_right_op;

				boolean leftisin = false;
				while (or_left_op == SQLBinaryOperator.BooleanOr) {

					if (or_left.getRight() instanceof SQLBinaryOpExpr) {
						or_right = (SQLBinaryOpExpr) or_left.getRight();
						or_right_op = or_right.getOperator();
						or_map.put(or_idx, or_right);

						if (or_right_op == SQLBinaryOperator.BooleanAnd) {
							deal_and_right(and_map, or_idx, or_right, or_right_op);
						} else {
							Map<Integer, SQLExpr> and_map_ele = new HashMap<Integer, SQLExpr>();
							and_map_ele.put(0, or_right);
							and_map.put(0, and_map_ele);
						}

					} else if (or_left.getRight() instanceof SQLInListExpr) {
						or_map.put(0, or_left.getRight());
					} else {

					}

					if (or_left.getLeft() instanceof SQLBinaryOpExpr) {
						or_left = (SQLBinaryOpExpr) or_left.getLeft();
						or_left_op = or_left.getOperator();
					} else if (or_left.getLeft() instanceof SQLInListExpr) {
						leftisin = true;
						or_left_op = null;
					} else {

					}
					or_idx++;

				}

				// 最左右不是IN
				if (!leftisin) {
					or_map.put(or_idx, or_left);

					if (or_left_op == SQLBinaryOperator.BooleanAnd) {
						deal_and_right(and_map, or_idx, or_left, or_left_op);
					} else {
						Map<Integer, SQLExpr> and_map_ele = new HashMap<Integer, SQLExpr>();
						and_map_ele.put(0, or_left);
						and_map.put(0, and_map_ele);
					}
					or_idx++;
				}

			} else if (top_op == SQLBinaryOperator.BooleanAnd) {

				Map<Integer, SQLExpr> and_map = new HashMap<Integer, SQLExpr>();

				int and_idx = 0;
				SQLBinaryOpExpr and_left = top_expr;
				SQLBinaryOpExpr and_right;
				SQLBinaryOperator and_left_op = top_op;
				SQLBinaryOperator and_right_op;
				boolean leftisin = false;
				while (and_left_op == SQLBinaryOperator.BooleanAnd) {
					if (and_left.getRight() instanceof SQLBinaryOpExpr) {
						and_right = (SQLBinaryOpExpr) and_left.getRight();
						and_right_op = and_right.getOperator();
						and_map.put(and_idx, and_right);

						if (and_right_op.isRelational()) {
							deal_relational(and_right);
						} else {

						}
					} else if (and_left.getRight() instanceof SQLInListExpr) {
						and_map.put(and_idx, and_left.getRight());
					} else {

					}

					if (and_left.getLeft() instanceof SQLBinaryOpExpr) {
						and_left = (SQLBinaryOpExpr) and_left.getLeft();
						and_left_op = and_left.getOperator();
					} else if (and_left.getLeft() instanceof SQLInListExpr) {
						leftisin = true;
						and_map.put(and_idx, and_left.getLeft());
						and_left_op = null;
					} else {

					}
					and_idx++;

				}
				// 最左右不是IN
				if (!leftisin) {

					and_map.put(and_idx, and_left);
					if (and_left_op.isRelational()) {
						deal_relational(and_left);
					} else {

					}
					and_idx++;
				}
			} else {
				// 只有一个条件
				if (top_op.isRelational()) {
					deal_relational(top_expr);
				} else {
					SQLBinaryOpExpr my_left;
					SQLBinaryOpExpr my_right = top_expr;
					SQLBinaryOperator my_left_op;
					SQLBinaryOperator my_right_op = top_op;
					while (my_right_op == SQLBinaryOperator.Add || my_right_op == SQLBinaryOperator.Subtract
							|| my_right_op == SQLBinaryOperator.Multiply || my_right_op == SQLBinaryOperator.Divide
							|| my_right_op == SQLBinaryOperator.Modulus) {
						if (my_right.getRight() instanceof SQLBinaryOpExpr) {
							System.out.println(my_right.getLeft().toString());
							my_right = (SQLBinaryOpExpr) my_right.getRight();
							my_right_op = my_right.getOperator();

						} else {
							System.out.println(my_right.getLeft().toString());
							System.out.println(my_right.getRight().toString());
							my_right_op = null;
						}
					}
					System.out.println(my_right.toString());
				}
			}
		}

		if (expr instanceof SQLPropertyExpr) {
			// 有表名字的字段
			SQLPropertyExpr x = (SQLPropertyExpr) expr;
		}
		if (expr instanceof SQLIdentifierExpr) {
			// 没有指定表名字的字段
			SQLIdentifierExpr x = (SQLIdentifierExpr) expr;
		}
		System.out.println(alias + " " + expr.toString() + "  " + expr.getClass().toGenericString());
	}

	private static void do_from_onetable(SQLSelectStatement stmt) throws Exception, SQLException {

		SQLSelect sqlSelect = stmt.getSelect();
		MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlSelect.getQuery();

		SQLTableSource from = query.getFrom();

		SQLExpr where = query.getWhere();

		SQLSelectGroupByClause groupby = query.getGroupBy();
		SQLOrderBy orderby = query.getOrderBy();

		List<SQLSelectItem> item_list = query.getSelectList();
		Limit limit = query.getLimit();

		String fromstr = from.toString();
		SQLExprTableSource varfrom = (SQLExprTableSource) from;

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
		Map hdb_conn_map = metadata.open_idconnmap_by_hdb(hdb_name);
		Map hcolumn_list_by_htable = metadata.get_hcolumn_list_by_htable(hdb_name, htable_name);
		Map hdc_list_by_htable = metadata.get_hdc_list_by_htable(hdb_name, htable_name);
		Map hdc_list2_by_htable = metadata.get_hdc_list2_by_htable(hdb_name, htable_name);
		Hdp table_hdp = metadata.get_hdp_by_htable(hdb_name, htable_name);

		// 判读 group by 和 order by 是否包含 hdc , 以便可以直接到节点上执行,无需mapreduce
		if (groupby == null && orderby == null) {
			// 直接到各个节点上执行
			if (return_type == 0) {

				// 单线程执行
				Iterator stmt_it = hdb_conn_map.keySet().iterator();
				final String sqlstr = stmt.toString();
				while (stmt_it.hasNext()) {
					Integer key = (Integer) stmt_it.next();
					Connection execconn = (Connection) hdb_conn_map.get(key);

					String execsql = sqlstr.replace(fromstr, htable_name);
					String logstr = "conn=" + key + ",sql=" + execsql;
					// System.out.println(logstr);
					try {
						Statement execstmt = execconn.createStatement();
						ResultSet rs = execstmt.executeQuery(execsql);

						ResultSetMetaData rsmd = rs.getMetaData();
						int count = rsmd.getColumnCount();
						String columns_str = "====result for " + key + "====\n";
						for (int i = 1; i <= count; i++) {
							columns_str = columns_str + rsmd.getColumnName(i) + "\t";
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
			}
		}
		 parse_where(where);
		if (groupby != null && orderby == null) {
			x = Executors.newFixedThreadPool(hdb_conn_map.size());

			List<SQLExpr> groupby_items = groupby.getItems();
			boolean group_hdc_is_same = group_hdc_is_same(hdc_list_by_htable, groupby_items);
			if (group_hdc_is_same) {
				// groupby 字段和hdc 是一致的,直接到各个节点上执行原始sql即可

				if (return_type == 0) {

					// 单线程执行
					Iterator stmt_it = hdb_conn_map.keySet().iterator();
					final String sqlstr = stmt.toString();
					while (stmt_it.hasNext()) {
						Integer key = (Integer) stmt_it.next();
						Connection execconn = (Connection) hdb_conn_map.get(key);

						String execsql = sqlstr.replace(fromstr, htable_name);
						String logstr = "conn=" + key + ",sql=" + execsql;
						// System.out.println(logstr);
						try {
							Statement execstmt = execconn.createStatement();
							ResultSet rs = execstmt.executeQuery(execsql);

							ResultSetMetaData rsmd = rs.getMetaData();
							int count = rsmd.getColumnCount();
							String columns_str = "====result for " + key + "====\n";
							for (int i = 1; i <= count; i++) {
								columns_str = columns_str + rsmd.getColumnName(i) + "\t";
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
				}
			} else {
				final String map_tablename = getMapTableName();
				final String reduce_tablename = getReduceTableName();
				MapReduceGroupNode node00 = new MapReduceGroupNode(0, 0, "node00", null, NodeType.GROUP, stmt);
				MySqlSelectQueryBlock map_query = new MySqlSelectQueryBlock();
				map_query.setFrom(from);
				map_query.setWhere(where);
				SQLSelect map_sqlSelect = new SQLSelect(map_query);
				SQLSelectStatement map_stmt = new SQLSelectStatement(map_sqlSelect);

				MySqlSelectQueryBlock reduce_query = new MySqlSelectQueryBlock();
				SQLSelect reduce_sqlSelect = new SQLSelect(reduce_query);
				SQLSelectStatement reduce_stmt = new SQLSelectStatement(reduce_sqlSelect);
				reduce_query.setFrom(from);
				reduce_query.setWhere(null);
				reduce_query.setGroupBy(groupby);

				parse_item_list(item_list, map_query, reduce_query);

				final String map_sql = map_stmt.toString().replace(fromstr, htable_name);
				final String reduce_sql = reduce_stmt.toString().replace(fromstr, map_tablename);
				node00.sql = stmt.toString();

				int map_num = hdb_conn_map.size();
				x = Executors.newFixedThreadPool(map_num);
				HashMap taskMap = null;

				/*
				 * 查询每个表的洗牌字段的范围
				 */

				taskMap = new HashMap<String, Future>();
				final String shuffle_field = groupby_items.get(0).toString();
				final String final_htable_name = htable_name;
				{
					final String sqlstr = base_id_sql.replaceAll("<table>", htable_name).replaceAll("<shuffle_field>",
							shuffle_field);
					for (int j = 0; j < map_num; j++) {
						final int nodeid = j;
						final Connection conn = (Connection) hdb_conn_map.get(j);

						Callable call = new Callable() {
							public TableIdEntity call() throws Exception {
								logstr = "conn=" + nodeid + ",sql=" + sqlstr;
								printLogStr(logstr);
								try {
									Statement stmt = conn.createStatement();
									ResultSet rs = stmt.executeQuery(sqlstr);
									long minid = Long.MAX_VALUE;
									long maxid = Long.MIN_VALUE;
									long datacount = 0L;
									while (rs.next()) {
										minid = rs.getInt(1);
										maxid = rs.getInt(2);
										datacount = rs.getInt(3);
									}
									TableIdEntity e = new TableIdEntity(nodeid, final_htable_name, shuffle_field, minid,
											maxid, datacount);
									return e;
								} catch (SQLException e) {
									printLogStr(nodeid + COMMA + e.getMessage() + NEWLINE);
									return null;
								}
							}
						};
						Future task = x.submit(call);
						taskMap.put(new Integer(j).toString(), task);
					}
				}
				long minid = Long.MAX_VALUE;
				long maxid = Long.MIN_VALUE;
				long datacount = 0L;
				Iterator iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Future val = (Future) entry.getValue();
					TableIdEntity ret = (TableIdEntity) val.get();
					if (ret.minid < minid) {
						minid = ret.minid;
					}
					if (ret.maxid > maxid) {
						maxid = ret.maxid;
					}
					datacount += ret.datacount;
				}

				/*
				 * 
				 * 根据记录数调整 reduce 数 , 使得每个reduce处理的记录数不会太多
				 */

				int reduce_num = map_num;

				Map reduce_conn_map = new HashMap();
				for (int j = 0; j < reduce_num; j++) {
					reduce_conn_map.put(j, hdb_conn_map.get(j));
				}

				if (reduce_num > 1) {
					x.shutdown();
					x = Executors.newFixedThreadPool(map_num * reduce_num);
				}

				/*
				 * 计算每个区间
				 */
				Long step_num = 0L;
				if (maxid < minid || datacount <= 0) {
					System.exit(1);
				} else {
					printLogStr("minid=" + minid + ",maxid=" + maxid);
					minid = minid - (minid % reduce_num) - reduce_num;
					maxid = maxid + (reduce_num - (maxid) % reduce_num) + reduce_num;
					printLogStr("minid=" + minid + ",maxid=" + maxid);
					step_num = (maxid - minid) / reduce_num;
					if (step_num <= 0) {
						System.exit(1);
					}
					for (int i = 0; i < reduce_num; i++) {
						logstr = "step " + (i) + " is " + (minid + i * step_num) + " <= x < "
								+ (minid + (i + 1) * step_num);
						printLogStr(logstr);
					}
				}

				/*
				 * 得到在reduce上map输出的建表语句
				 */

				String crt_sql = "";
				Map<Integer, ColumnInfo> col_map = null;
				String index_str = " index idx(" + shuffle_field + ")";
				{
					final String sqlstr = del_last_semi(map_sql) + " limit 1";

					for (int j = 0; j < 1; j++) {

						final int nodeid = j;
						final Connection conn = (Connection) hdb_conn_map.get(j);
						logstr = "conn=" + j + ",sql=" + sqlstr;
						printLogStr(logstr);
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(sqlstr);
						ResultSetMetaData rsmd = rs.getMetaData();

						col_map = get_col_map(rsmd);

					}
				}
				String cols_str = get_col_definition_str(col_map, true);

				crt_sql = base_create_table_sql.replaceAll("<table>", map_tablename).replaceAll("<cols_str>", cols_str)
						.replaceAll("<index_str>", index_str);

				/*
				 * 在reduce上建好表map表
				 */
				{
					taskMap.clear();
					final String create_table_sql = crt_sql;
					for (int j = 0; j < reduce_num; j++) {
						final int nodeid = j;
						final Connection conn = (Connection) reduce_conn_map.get(j);

						Callable call = new Callable() {
							public Boolean call() throws Exception {

								try {
									Statement stmt = conn.createStatement();

									String dropsql = "DROP TABLE IF EXISTS " + map_tablename + " ;";
									logstr = "conn=" + nodeid + ",sql=" + dropsql;
									printLogStr(logstr);
									stmt.execute(dropsql);

									logstr = "conn=" + nodeid + ",sql=" + create_table_sql;
									printLogStr(logstr);
									boolean ret = stmt.execute(create_table_sql);
									return ret;
								} catch (SQLException e) {
									printLogStr(e.getMessage() + NEWLINE);
									return false;
								}
							}
						};
						Future task = x.submit(call);
						taskMap.put(new Integer(j).toString(), task);
					}
				}

				iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Future val = (Future) entry.getValue();
					Boolean ret = (Boolean) val.get();
				}

				/*
				 * 
				 * 边执行map，边执行shuffle map的模式 11 一个sql生成多个问 12 多个sql生成多个文件
				 * 
				 * 执行map，然后执行shuffle map的模式 21 一个sql生成多个问 22 多个sql生成多个文件
				 * 
				 * 生成临时文件，导入到reduce中 读取记录，批量插入到reduce中
				 */

				/*
				 * 执行map和shuffle, 一个map节点执行多次sql，每次导出到一个文件，然后reduce节点
				 */
				String[][] map_file_arr = new String[map_num][reduce_num];
				taskMap = new HashMap<String, Future>();
				{

					for (int j = 0; j < map_num; j++) {
						for (int k = 0; k < reduce_num; k++) {
							Long step_minid = minid + k * step_num;
							Long step_maxid = minid + (k + 1) * step_num;
							String sqlstr = "";
							if (where == null) {
								sqlstr = map_sql + " where " + shuffle_field + " >= " + step_minid + " and "
										+ shuffle_field + " < " + step_maxid;
							} else {
								sqlstr = map_sql + " and " + shuffle_field + " >= " + step_minid + " and "
										+ shuffle_field + " < " + step_maxid;
							}
							final String execsql = sqlstr;
							final String map_outfile = "/tmp/" + map_tablename + "_" + j + "_" + k + ".txt";
							map_file_arr[j][k] = map_outfile;
							final int map_nodeid = j;
							final Connection map_conn = (Connection) hdb_conn_map.get(j);
							final int reduce_nodeid = k;
							final Connection reduce_conn = (Connection) reduce_conn_map.get(k);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										Statement map_stmt = map_conn.createStatement();
										Statement reduce_stmt = reduce_conn.createStatement();

										logstr = "conn=" + map_nodeid + ",sql=" + execsql;
										printLogStr(logstr);

										ResultSet rs = map_stmt.executeQuery(execsql);
										ResultSetMetaData rsmda = rs.getMetaData();
										int cols = rsmda.getColumnCount();

										FileOutputStream fos = new FileOutputStream(map_outfile, false);
										OutputStreamWriter osw = new OutputStreamWriter(fos);
										while (rs.next()) {
											String line = "";
											for (int i = 1; i < cols; i++) {
												line += rs.getString(i) + TAB;
											}
											line += rs.getString(cols);
											osw.write(line + NEWLINE);
										}

										osw.flush();
										osw.close();

										String loadsql = base_load_sql.replaceAll("<table>", map_tablename)
												.replaceAll("<map_outfile>", map_outfile);
										logstr = "conn=" + reduce_nodeid + ",sql=" + loadsql;
										printLogStr(logstr);

										reduce_stmt.execute(loadsql);

										return true;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(map_outfile, task);
						}
					}

				}
				iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Future val = (Future) entry.getValue();
					Boolean ret = (Boolean) val.get();
				}

				if (return_type == 0) {

					/*
					 * 执行reduce操作
					 */
					{
						final String final_reduce_sql = reduce_sql;
						taskMap = new HashMap<String, Future>();
						for (int j = 0; j < reduce_num; j++) {
							final int nodeid = j;
							final Connection conn = (Connection) reduce_conn_map.get(j);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										logstr = "conn=" + nodeid + ",sql=" + final_reduce_sql;
										printLogStr(logstr);

										Statement execstmt = conn.createStatement();
										ResultSet rs = execstmt.executeQuery(final_reduce_sql);

										ResultSetMetaData rsmd = rs.getMetaData();
										int count = rsmd.getColumnCount();
										String columns_str = "====result===\n";
										for (int i = 1; i <= count; i++) {
											String CatalogName = rsmd.getCatalogName(i);
											String SchemaName = rsmd.getSchemaName(i);

											String TableName = rsmd.getTableName(i);
											String ColumnName = rsmd.getColumnName(i);
											String ColumnLabel = rsmd.getColumnLabel(i);
											String ColumnTypeName = rsmd.getColumnTypeName(i);

											int ColumnType = rsmd.getColumnType(i);
											int ColumnDisplaySize = rsmd.getColumnDisplaySize(i);
											int Scale = rsmd.getScale(i);
											int Precision = rsmd.getPrecision(i);
											boolean isSigned = rsmd.isSigned(i);
											String ColumnClassName = rsmd.getColumnClassName(i);

											columns_str = columns_str + rsmd.getColumnName(i) + "\t";

											// columns_str = columns_str +
											// "[CatalogName=" + CatalogName +
											// ", SchemaName="
											// + SchemaName + ", TableName=" +
											// TableName + ", ColumnName="
											// + ColumnName + ", ColumnLabel=" +
											// ColumnLabel + ", ColumnTypeName="
											// + ColumnTypeName + ",
											// ColumnClassName=" +
											// ColumnClassName
											// + ", ColumnType=" + ColumnType +
											// ", ColumnDisplaySize="
											// + ColumnDisplaySize + ", Scale="
											// + Scale + ", Precision="
											// + Precision + ", isSigned=" +
											// isSigned + "]" + "\t";
										}
										System.out.println(columns_str);
										while (rs.next()) {
											String line = "";
											for (int i = 1; i <= count; i++) {
												line = line + rs.getString(i) + "\t";
											}
											System.out.println(line);
										}
										return true;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(new Integer(nodeid).toString(), task);
						}
					}

					iter = taskMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String) entry.getKey();
						Future val = (Future) entry.getValue();
						Boolean ret = (Boolean) val.get();
					}

				} else {

					/*
					 * 得到在reduce上reduce输出的建表语句
					 */

					String reduce_crt_sql = "";
					Map<Integer, ColumnInfo> reduce_col_map = null;
					{
						final String sqlstr = del_last_semi(reduce_sql) + " limit 1";

						for (int j = 0; j < 1; j++) {

							final int nodeid = j;
							final Connection conn = (Connection) hdb_conn_map.get(j);
							logstr = "conn=" + j + ",sql=" + sqlstr;
							printLogStr(logstr);
							Statement st = conn.createStatement();
							ResultSet rs = st.executeQuery(sqlstr);
							ResultSetMetaData rsmd = rs.getMetaData();

							reduce_col_map = get_col_map(rsmd);

						}
					}
					String reduce_cols_str = get_col_definition_str(reduce_col_map, false);

					reduce_crt_sql = base_create_table_sql.replaceAll("<table>", reduce_tablename)
							.replaceAll("<cols_str>", reduce_cols_str).replaceAll("<index_str>", "");

					/*
					 * 在reduce上建好表reduce表
					 */
					{
						taskMap.clear();
						final String create_table_sql = reduce_crt_sql;
						for (int j = 0; j < reduce_num; j++) {
							final int nodeid = j;
							final Connection conn = (Connection) reduce_conn_map.get(j);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										Statement stmt = conn.createStatement();

										String dropsql = "DROP TABLE IF EXISTS " + reduce_tablename + " ;";
										logstr = "conn=" + nodeid + ",sql=" + dropsql;
										printLogStr(logstr);
										stmt.execute(dropsql);

										logstr = "conn=" + nodeid + ",sql=" + create_table_sql;
										printLogStr(logstr);
										boolean ret = stmt.execute(create_table_sql);
										return ret;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(new Integer(j).toString(), task);
						}
					}

					iter = taskMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String) entry.getKey();
						Future val = (Future) entry.getValue();
						Boolean ret = (Boolean) val.get();
					}

					/*
					 * 执行reduce操作
					 */
					{
						final String replace_reduce_sql = "replace into <table> ".replaceAll("<table>",
								reduce_tablename) + reduce_sql.replaceAll(fromstr, reduce_tablename);
						taskMap = new HashMap<String, Future>();
						for (int j = 0; j < reduce_num; j++) {
							final int nodeid = j;
							final Connection conn = (Connection) reduce_conn_map.get(j);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										Statement stmt = conn.createStatement();
										logstr = "conn=" + nodeid + ",sql=" + replace_reduce_sql;
										printLogStr(logstr);
										boolean ret = stmt.execute(replace_reduce_sql);
										return ret;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(new Integer(j).toString(), task);
						}
					}

					iter = taskMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String) entry.getKey();
						Future val = (Future) entry.getValue();
						Boolean ret = (Boolean) val.get();
					}
				}

			}

			x.shutdown();
		}
		if (groupby == null && orderby != null) {

			List<SQLSelectOrderByItem> orderby_items = orderby.getItems();
			boolean order_hdc_is_same = order_hdc_is_same(hdc_list_by_htable, orderby_items);

			x = Executors.newFixedThreadPool(hdb_conn_map.size());

			if (order_hdc_is_same) {
				// groupby 字段和hdc 是一致的,直接到各个节点上执行原始sql即可

				if (return_type == 0) {

					// 单线程执行
					Iterator stmt_it = hdb_conn_map.keySet().iterator();
					final String sqlstr = stmt.toString();
					while (stmt_it.hasNext()) {
						Integer key = (Integer) stmt_it.next();
						Connection execconn = (Connection) hdb_conn_map.get(key);

						String execsql = sqlstr.replace(fromstr, htable_name);
						String logstr = "conn=" + key + ",sql=" + execsql;
						// System.out.println(logstr);
						try {
							Statement execstmt = execconn.createStatement();
							ResultSet rs = execstmt.executeQuery(execsql);

							ResultSetMetaData rsmd = rs.getMetaData();
							int count = rsmd.getColumnCount();
							String columns_str = "====result for " + key + "====\n";
							for (int i = 1; i <= count; i++) {
								columns_str = columns_str + rsmd.getColumnName(i) + "\t";
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
				}
			} else {
				final String map_tablename = getMapTableName();
				final String reduce_tablename = getReduceTableName();

				final String shuffle_field = orderby_items.get(0).getExpr().toString();

				MapReduceGroupNode node00 = new MapReduceGroupNode(0, 0, "node00", null, NodeType.GROUP, stmt);
				MySqlSelectQueryBlock map_query = new MySqlSelectQueryBlock();
				map_query.setFrom(from);
				map_query.setWhere(where);
				map_query.setOrderBy(null);
				map_query.getSelectList().addAll(item_list);
				SQLSelect map_sqlSelect = new SQLSelect(map_query);
				SQLSelectStatement map_stmt = new SQLSelectStatement(map_sqlSelect);

				MySqlSelectQueryBlock reduce_query = new MySqlSelectQueryBlock();
				reduce_query.setFrom(from);
				reduce_query.setWhere(null);
				reduce_query.setOrderBy(orderby);
				reduce_query.getSelectList().addAll(item_list);
				SQLSelect reduce_sqlSelect = new SQLSelect(reduce_query);
				reduce_sqlSelect.setOrderBy(orderby);
				SQLSelectStatement reduce_stmt = new SQLSelectStatement(reduce_sqlSelect);

				final String map_sql = map_stmt.toString().replace(fromstr, htable_name);
				final String reduce_sql = reduce_stmt.toString().replace(fromstr, map_tablename);
				node00.sql = stmt.toString();

				int map_num = hdb_conn_map.size();
				x = Executors.newFixedThreadPool(map_num);
				HashMap taskMap = null;

				/*
				 * 查询每个表的洗牌字段的范围
				 */

				taskMap = new HashMap<String, Future>();

				final String final_htable_name = htable_name;
				{
					final String sqlstr = base_id_sql.replaceAll("<table>", htable_name).replaceAll("<shuffle_field>",
							shuffle_field);
					for (int j = 0; j < map_num; j++) {
						final int nodeid = j;
						final Connection conn = (Connection) hdb_conn_map.get(j);

						Callable call = new Callable() {
							public TableIdEntity call() throws Exception {
								logstr = "conn=" + nodeid + ",sql=" + sqlstr;
								printLogStr(logstr);
								try {
									Statement stmt = conn.createStatement();
									ResultSet rs = stmt.executeQuery(sqlstr);
									long minid = Long.MAX_VALUE;
									long maxid = Long.MIN_VALUE;
									long datacount = 0L;
									while (rs.next()) {
										minid = rs.getInt(1);
										maxid = rs.getInt(2);
										datacount = rs.getInt(3);
									}
									TableIdEntity e = new TableIdEntity(nodeid, final_htable_name, shuffle_field, minid,
											maxid, datacount);
									return e;
								} catch (SQLException e) {
									printLogStr(nodeid + COMMA + e.getMessage() + NEWLINE);
									return null;
								}
							}
						};
						Future task = x.submit(call);
						taskMap.put(new Integer(j).toString(), task);
					}
				}
				long minid = Long.MAX_VALUE;
				long maxid = Long.MIN_VALUE;
				long datacount = 0L;
				Iterator iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Future val = (Future) entry.getValue();
					TableIdEntity ret = (TableIdEntity) val.get();
					if (ret.minid < minid) {
						minid = ret.minid;
					}
					if (ret.maxid > maxid) {
						maxid = ret.maxid;
					}
					datacount += ret.datacount;
				}

				/*
				 * 
				 * 根据记录数调整 reduce 数 , 使得每个reduce处理的记录数不会太多
				 */

				int reduce_num = map_num;

				Map reduce_conn_map = new HashMap();
				for (int j = 0; j < reduce_num; j++) {
					reduce_conn_map.put(j, hdb_conn_map.get(j));
				}

				if (reduce_num > 1) {
					x.shutdown();
					x = Executors.newFixedThreadPool(map_num * reduce_num);
				}

				/*
				 * 计算每个区间
				 */
				Long step_num = 0L;
				if (maxid < minid || datacount <= 0) {
					System.exit(1);
				} else {
					printLogStr("minid=" + minid + ",maxid=" + maxid);
					minid = minid - (minid % reduce_num) - reduce_num;
					maxid = maxid + (reduce_num - (maxid) % reduce_num) + reduce_num;
					printLogStr("minid=" + minid + ",maxid=" + maxid);
					step_num = (maxid - minid) / reduce_num;
					if (step_num <= 0) {
						System.exit(1);
					}
					for (int i = 0; i < reduce_num; i++) {
						logstr = "step " + (i) + " is " + (minid + i * step_num) + " <= x < "
								+ (minid + (i + 1) * step_num);
						printLogStr(logstr);
					}
				}

				/*
				 * 得到在reduce上map输出的建表语句
				 */

				String crt_sql = "";
				Map<Integer, ColumnInfo> col_map = null;
				String index_str = " index idx(" + shuffle_field + ")";
				{
					final String sqlstr = del_last_semi(map_sql) + " limit 1";

					for (int j = 0; j < 1; j++) {

						final int nodeid = j;
						final Connection conn = (Connection) hdb_conn_map.get(j);
						logstr = "conn=" + j + ",sql=" + sqlstr;
						printLogStr(logstr);
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(sqlstr);
						ResultSetMetaData rsmd = rs.getMetaData();

						col_map = get_col_map(rsmd);

					}
				}
				String cols_str = get_col_definition_str(col_map, true);

				crt_sql = base_create_table_sql.replaceAll("<table>", map_tablename).replaceAll("<cols_str>", cols_str)
						.replaceAll("<index_str>", index_str);

				/*
				 * 在reduce上建好表map表
				 */
				{
					taskMap.clear();
					final String create_table_sql = crt_sql;
					for (int j = 0; j < reduce_num; j++) {
						final int nodeid = j;
						final Connection conn = (Connection) reduce_conn_map.get(j);

						Callable call = new Callable() {
							public Boolean call() throws Exception {

								try {
									Statement stmt = conn.createStatement();

									String dropsql = "DROP TABLE IF EXISTS " + map_tablename + " ;";
									logstr = "conn=" + nodeid + ",sql=" + dropsql;
									printLogStr(logstr);
									stmt.execute(dropsql);

									logstr = "conn=" + nodeid + ",sql=" + create_table_sql;
									printLogStr(logstr);
									boolean ret = stmt.execute(create_table_sql);
									return ret;
								} catch (SQLException e) {
									printLogStr(e.getMessage() + NEWLINE);
									return false;
								}
							}
						};
						Future task = x.submit(call);
						taskMap.put(new Integer(j).toString(), task);
					}
				}

				iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Future val = (Future) entry.getValue();
					Boolean ret = (Boolean) val.get();
				}

				/*
				 * 
				 * 边执行map，边执行shuffle map的模式 11 一个sql生成多个问 12 多个sql生成多个文件
				 * 
				 * 执行map，然后执行shuffle map的模式 21 一个sql生成多个问 22 多个sql生成多个文件
				 * 
				 * 生成临时文件，导入到reduce中 读取记录，批量插入到reduce中
				 */

				/*
				 * 执行map和shuffle, 一个map节点执行多次sql，每次导出到一个文件，然后reduce节点
				 */
				String[][] map_file_arr = new String[map_num][reduce_num];
				taskMap = new HashMap<String, Future>();
				{

					for (int j = 0; j < map_num; j++) {
						for (int k = 0; k < reduce_num; k++) {
							Long step_minid = minid + k * step_num;
							Long step_maxid = minid + (k + 1) * step_num;
							String sqlstr = "";
							if (where == null) {
								sqlstr = map_sql + " where " + shuffle_field + " >= " + step_minid + " and "
										+ shuffle_field + " < " + step_maxid;
							} else {
								sqlstr = map_sql + " and " + shuffle_field + " >= " + step_minid + " and "
										+ shuffle_field + " < " + step_maxid;
							}
							final String execsql = sqlstr;
							final String map_outfile = "/tmp/" + map_tablename + "_" + j + "_" + k + ".txt";
							map_file_arr[j][k] = map_outfile;
							final int map_nodeid = j;
							final Connection map_conn = (Connection) hdb_conn_map.get(j);
							final int reduce_nodeid = k;
							final Connection reduce_conn = (Connection) reduce_conn_map.get(k);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										Statement map_stmt = map_conn.createStatement();
										Statement reduce_stmt = reduce_conn.createStatement();

										logstr = "conn=" + map_nodeid + ",sql=" + execsql;
										printLogStr(logstr);

										ResultSet rs = map_stmt.executeQuery(execsql);
										ResultSetMetaData rsmda = rs.getMetaData();
										int cols = rsmda.getColumnCount();

										FileOutputStream fos = new FileOutputStream(map_outfile, false);
										OutputStreamWriter osw = new OutputStreamWriter(fos);
										while (rs.next()) {
											String line = "";
											for (int i = 1; i < cols; i++) {
												line += rs.getString(i) + TAB;
											}
											line += rs.getString(cols);
											osw.write(line + NEWLINE);
										}

										osw.flush();
										osw.close();

										String loadsql = base_load_sql.replaceAll("<table>", map_tablename)
												.replaceAll("<map_outfile>", map_outfile);
										logstr = "conn=" + reduce_nodeid + ",sql=" + loadsql;
										printLogStr(logstr);

										reduce_stmt.execute(loadsql);

										return true;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(map_outfile, task);
						}
					}

				}
				iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Future val = (Future) entry.getValue();
					Boolean ret = (Boolean) val.get();
				}

				if (return_type == 0) {

					/*
					 * 执行reduce操作
					 */
					{
						final String final_reduce_sql = reduce_sql;
						taskMap = new HashMap<String, Future>();
						for (int j = 0; j < reduce_num; j++) {
							final int nodeid = j;
							final Connection conn = (Connection) reduce_conn_map.get(j);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										logstr = "conn=" + nodeid + ",sql=" + final_reduce_sql;
										printLogStr(logstr);

										Statement execstmt = conn.createStatement();
										ResultSet rs = execstmt.executeQuery(final_reduce_sql);

										ResultSetMetaData rsmd = rs.getMetaData();
										int count = rsmd.getColumnCount();
										String columns_str = "====result===\n";
										for (int i = 1; i <= count; i++) {

											columns_str = columns_str + rsmd.getColumnName(i) + "\t";

										}
										System.out.println(columns_str);
										while (rs.next()) {
											String line = "";
											for (int i = 1; i <= count; i++) {
												line = line + rs.getString(i) + "\t";
											}
											System.out.println(line);
										}
										return true;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(new Integer(nodeid).toString(), task);
						}
					}

					iter = taskMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String) entry.getKey();
						Future val = (Future) entry.getValue();
						Boolean ret = (Boolean) val.get();
					}

				} else {

					/*
					 * 得到在reduce上reduce输出的建表语句
					 */

					String reduce_crt_sql = "";
					Map<Integer, ColumnInfo> reduce_col_map = null;
					{
						final String sqlstr = del_last_semi(reduce_sql) + " limit 1";

						for (int j = 0; j < 1; j++) {

							final int nodeid = j;
							final Connection conn = (Connection) hdb_conn_map.get(j);
							logstr = "conn=" + j + ",sql=" + sqlstr;
							printLogStr(logstr);
							Statement st = conn.createStatement();
							ResultSet rs = st.executeQuery(sqlstr);
							ResultSetMetaData rsmd = rs.getMetaData();

							reduce_col_map = get_col_map(rsmd);

						}
					}
					String reduce_cols_str = get_col_definition_str(reduce_col_map, false);

					reduce_crt_sql = base_create_table_sql.replaceAll("<table>", reduce_tablename)
							.replaceAll("<cols_str>", reduce_cols_str).replaceAll("<index_str>", "");

					/*
					 * 在reduce上建好表reduce表
					 */
					{
						taskMap.clear();
						final String create_table_sql = reduce_crt_sql;
						for (int j = 0; j < reduce_num; j++) {
							final int nodeid = j;
							final Connection conn = (Connection) reduce_conn_map.get(j);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										Statement stmt = conn.createStatement();

										String dropsql = "DROP TABLE IF EXISTS " + reduce_tablename + " ;";
										logstr = "conn=" + nodeid + ",sql=" + dropsql;
										printLogStr(logstr);
										stmt.execute(dropsql);

										logstr = "conn=" + nodeid + ",sql=" + create_table_sql;
										printLogStr(logstr);
										boolean ret = stmt.execute(create_table_sql);
										return ret;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(new Integer(j).toString(), task);
						}
					}

					iter = taskMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String) entry.getKey();
						Future val = (Future) entry.getValue();
						Boolean ret = (Boolean) val.get();
					}

					/*
					 * 执行reduce操作
					 */
					{
						final String replace_reduce_sql = "replace into <table> ".replaceAll("<table>",
								reduce_tablename) + reduce_sql.replaceAll(fromstr, reduce_tablename);
						taskMap = new HashMap<String, Future>();
						for (int j = 0; j < reduce_num; j++) {
							final int nodeid = j;
							final Connection conn = (Connection) reduce_conn_map.get(j);

							Callable call = new Callable() {
								public Boolean call() throws Exception {

									try {
										Statement stmt = conn.createStatement();
										logstr = "conn=" + nodeid + ",sql=" + replace_reduce_sql;
										printLogStr(logstr);
										boolean ret = stmt.execute(replace_reduce_sql);
										return ret;
									} catch (SQLException e) {
										printLogStr(e.getMessage() + NEWLINE);
										return false;
									}
								}
							};
							Future task = x.submit(call);
							taskMap.put(new Integer(j).toString(), task);
						}
					}

					iter = taskMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String) entry.getKey();
						Future val = (Future) entry.getValue();
						Boolean ret = (Boolean) val.get();
					}
				}

			}

			x.shutdown();

		}
		if (groupby != null && orderby != null) {
			//
			List<SQLExpr> groupby_items = groupby.getItems();
			List<SQLSelectOrderByItem> orderby_items = orderby.getItems();
			int groupby_size = groupby_items.size();
			int orderby_size = orderby_items.size();
			for (Iterator iterator2 = groupby_items.iterator(); iterator2.hasNext();) {
				SQLExpr groupByItem = (SQLExpr) iterator2.next();
			}

			// order 类型是否完全一致
			boolean order_type_issame = true;
			SQLOrderingSpecification all_order_type = null;

			for (int i = 0; i < orderby_size; i++) {
				SQLSelectOrderByItem orderByItem = orderby_items.get(i);
				SQLExpr orderby_expr = orderByItem.getExpr();
				SQLOrderingSpecification order_type = orderByItem.getType();
				if (i == 0) {
					all_order_type = order_type;
				} else {

					if ((all_order_type == order_type) == false) {
						order_type_issame = false;
					}
				}
			}

			// group 和 order 是否一样
			boolean group_order_is_same = false;
			if (order_type_issame && groupby_size == orderby_size) {
				group_order_is_same = true;
				for (int i = 0; i < groupby_size; i++) {
					String groupby_str = groupby_items.get(i).toString();
					String orderby_str = orderby_items.get(i).getExpr().toString();

					if (groupby_str.equalsIgnoreCase(orderby_str) == false) {
						group_order_is_same = false;
					}
				}
			}

		}

	}

	private static void parse_item_list(List<SQLSelectItem> item_list, MySqlSelectQueryBlock map_query,
			MySqlSelectQueryBlock reduce_query) {
		int inner_alias_orderno = 0;
		for (Iterator iterator2 = item_list.iterator(); iterator2.hasNext();) {
			SQLSelectItem sqlSelectItem = (SQLSelectItem) iterator2.next();

			String alias = sqlSelectItem.getAlias();
			SQLExpr expr = sqlSelectItem.getExpr();

			inner_alias_orderno = parse_expr(map_query, reduce_query, inner_alias_orderno, sqlSelectItem, alias, expr);

		}
	}

	/*
	 * 
	 * 
	 * 如果是 SQLIdentifierExpr
	 * 
	 * 
	 */
	private static int parse_expr(MySqlSelectQueryBlock map_query, MySqlSelectQueryBlock reduce_query,
			int inner_alias_orderno, SQLSelectItem sqlSelectItem, String alias, SQLExpr expr) {
		if (expr instanceof SQLIdentifierExpr) {
			SQLSelectItem map_item = new SQLSelectItem();
			String inner_alias = "_inner_column_" + inner_alias_orderno;
			inner_alias_orderno++;
			map_item.setAlias(inner_alias);
			map_item.setExpr(expr);

			SQLSelectItem reduce_item = new SQLSelectItem();
			reduce_item.setExpr(new SQLIdentifierExpr(inner_alias));

			map_query.getSelectList().add(map_item);
			reduce_query.getSelectList().add(reduce_item);
		}
		if (expr instanceof SQLAggregateExpr) {
			SQLAggregateExpr aggr_expr = (SQLAggregateExpr) expr;
			String methodName = aggr_expr.getMethodName();
			List<SQLExpr> args = aggr_expr.getArguments();

			List<SQLExpr> reduce_args = new ArrayList<SQLExpr>();
			for (Iterator iterator3 = args.iterator(); iterator3.hasNext();) {
				SQLExpr sqlExpr = (SQLExpr) iterator3.next();

				String inner_alias = "_inner_column_" + inner_alias_orderno;
				inner_alias_orderno++;
				SQLSelectItem inner_item = new SQLSelectItem(sqlExpr, inner_alias);
				map_query.getSelectList().add(inner_item);

				SQLIdentifierExpr idenExpr = new SQLIdentifierExpr(inner_alias);
				reduce_args.add(idenExpr);
			}

			SQLAggregateExpr reduce_aggr_expr = new SQLAggregateExpr(methodName);
			reduce_aggr_expr.getArguments().addAll(reduce_args);
			SQLSelectItem reduce_sqlSelectItem = new SQLSelectItem(reduce_aggr_expr, alias);
			reduce_query.getSelectList().add(reduce_sqlSelectItem);
		}

		if (expr instanceof SQLMethodInvokeExpr) {

			SQLMethodInvokeExpr methodExpr = (SQLMethodInvokeExpr) expr;
			String methodName = methodExpr.getMethodName();
			List<SQLExpr> param_list = methodExpr.getParameters();
			for (int i = 0; i < param_list.size(); i++) {
				SQLExpr item_expr = param_list.get(i);
			}

			if (alias == null || alias.length() == 0) {
				String inner_alias = "_inner_column_" + inner_alias_orderno;
				inner_alias_orderno++;
				SQLSelectItem inner_item = new SQLSelectItem(expr, inner_alias);
				map_query.getSelectList().add(inner_item);

				SQLIdentifierExpr idenExpr = new SQLIdentifierExpr(inner_alias);
				SQLSelectItem reduce_sqlSelectItem = new SQLSelectItem(idenExpr, alias);
				reduce_query.getSelectList().add(reduce_sqlSelectItem);
			} else {
				map_query.getSelectList().add(sqlSelectItem);

				SQLIdentifierExpr idenExpr = new SQLIdentifierExpr(alias);
				SQLSelectItem reduce_sqlSelectItem = new SQLSelectItem(idenExpr);
				reduce_query.getSelectList().add(reduce_sqlSelectItem);
			}
		}
		return inner_alias_orderno;
	}

	private static void parse_itemlist(List<SQLSelectItem> item_list, MySqlSelectQueryBlock map_query,
			MySqlSelectQueryBlock reduce_query) {
	}

	public static String get_col_definition_str(Map<Integer, ColumnInfo> col_map, boolean hasindex) throws Exception {

		String cols_str = "";
		int colcount = col_map.size();
		for (int i = 1; i <= colcount; i++) {
			ColumnInfo ci = col_map.get(i);

			switch (ci.type) {
			case Types.BIT:

			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:

				cols_str += ci.label + SPACE + ci.typename + SPACE;
				break;
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
				cols_str += ci.label + SPACE + ci.typename + "(" + ci.precision + COMMA + ci.scale + ")" + SPACE;
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				cols_str += ci.label + SPACE + ci.typename + "(" + ci.precision + ")" + SPACE;
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				cols_str += ci.label + SPACE + ci.typename + SPACE;
				break;
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:

			case Types.BLOB:
			case Types.CLOB:
			case Types.BOOLEAN:

			}

			if (i == colcount) {
				if (hasindex) {
					// 还有索引部分
					cols_str += COMMA + NEWLINE;
				} else {
					// 没有索引部分
					cols_str += NEWLINE;
				}
			} else {
				cols_str += COMMA + NEWLINE;
			}
		}

		return cols_str;

	}

	public static String get_col_select_str(Map<Integer, ColumnInfo> col_map) throws Exception {

		String cols_str = "";
		int colcount = col_map.size();
		for (int i = 1; i <= colcount; i++) {
			ColumnInfo ci = col_map.get(i);

			if (i == colcount) {
				cols_str += ci.label;
			} else {
				cols_str += ci.label + COMMA;
			}
		}

		return cols_str;

	}

	public static String get_col_select_str_2(Map<Integer, String> col_map) throws Exception {

		String cols_str = "";
		int colcount = col_map.size();
		for (int i = 0; i < colcount; i++) {
			String ci = col_map.get(i);

			if (i == colcount - 1) {
				cols_str += ci;
			} else {
				cols_str += ci + COMMA;
			}
		}

		return cols_str;

	}

	public static String del_last_semi(String sql) throws Exception {

		int len = sql.length();
		int i = len - 1;
		while (i >= 0) {
			char c = sql.charAt(i);
			if (c == ';' || c == ' ') {
				i--;
			} else {
				return sql.substring(0, i + 1);
			}

		}

		return EMPTY;

	}

	/*
	 * 
	 * 目前只支持一张表只有一个join字段
	 */
	public static Map<Integer, Integer> find_join_table_cols(String[][] join_matrix) throws Exception {

		int num = join_matrix.length;

		Set<String> set = new HashSet<String>();

		for (int i = 0; i < num; i++) {

			for (int j = 0; j < num; j++) {

				String[] s = join_matrix[i][j].trim().split(" *, *");
				if (s.length == 2) {

					Integer left_ci = Integer.valueOf(s[0]);
					Integer right_ci = Integer.valueOf(s[1]);
					String left_tf = i + COMMA + left_ci;
					String right_tf = j + COMMA + right_ci;

					set.add(left_tf);
					set.add(right_tf);

				}
			}

		}

		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			String[] s = string.trim().split(" *, *");
			if (s.length == 2) {
				Integer tabidx = Integer.valueOf(s[0]);
				Integer colidx = Integer.valueOf(s[1]);
				m.put(tabidx, colidx);
			}
		}
		return m;

	}

	/*
	 * 
	 * 目前只支持一张表只有一个join字段
	 */
	public static String find_join_cond(JoinNode node) throws Exception {

		String ret = "";
		int num = node.join_matrix.length;
		String[][] join_matrix = node.join_matrix;

		for (int i = 0; i < num; i++) {

			for (int j = 0; j < num; j++) {

				String[] s = join_matrix[i][j].trim().split(" *, *");
				if (s.length == 2) {

					String left_tab = node.table_alias_map.get(i);
					Integer left_ci = Integer.valueOf(s[0]);
					String left_colname = node.select_field_map.get(i).get(left_ci);
					String right_tab = node.table_alias_map.get(j);
					Integer right_ci = Integer.valueOf(s[1]);
					String right_colname = node.select_field_map.get(j).get(right_ci);

					ret += left_tab + "." + left_colname + " = " + right_tab + "." + right_colname + " and ";
				}
			}

		}
		ret = ret.substring(0, ret.length() - 4);
		return ret;

	}

	public static Map<Integer, Integer> find_join_uniq_cols(String[][] join_matrix) throws Exception {

		int num = join_matrix.length;

		Set<String> left_set = new HashSet<String>();
		Set<String> right_set = new HashSet<String>();

		for (int i = 0; i < num; i++) {

			for (int j = 0; j < num; j++) {

				String[] s = join_matrix[i][j].trim().split(" *, *");
				if (s.length == 2) {

					Integer left_ci = Integer.valueOf(s[0]);
					Integer right_ci = Integer.valueOf(s[1]);
					String left_tf = i + COMMA + left_ci;
					String right_tf = j + COMMA + right_ci;
					if (false == right_set.contains(left_tf)) {

						left_set.add(left_tf);

					}
					right_set.add(right_tf);

				}
			}

		}
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (Iterator iterator = left_set.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			String[] s = string.trim().split(" *, *");
			if (s.length == 2) {
				Integer tabidx = Integer.valueOf(s[0]);
				Integer colidx = Integer.valueOf(s[1]);
				m.put(tabidx, colidx);
			}
		}
		return m;

	}

	public static Htable get_tableinfo(String tablename) throws Exception {

		return null;

	}

	public static Map<Integer, String> get_node_by_cond(Htable ti, String cond) throws Exception {
		Map<Integer, String> ret = null;

		/*
		 * 根据条件和表的分表策略判断，需要到哪些节点执行
		 */
		return ret;
	}

	public static Map<Integer, ColumnInfo> get_col_map(ResultSetMetaData rsmd) throws Exception {

		int colcount = rsmd.getColumnCount();
		Map<Integer, ColumnInfo> col_map = new HashMap<Integer, ColumnInfo>();
		for (int i = 1; i <= colcount; i++) {
			ColumnInfo ci = new ColumnInfo();
			ci.index = i;
			ci.type = rsmd.getColumnType(i);
			ci.label = rsmd.getColumnLabel(i);
			ci.typename = rsmd.getColumnTypeName(i);
			ci.precision = rsmd.getPrecision(i);
			ci.scale = rsmd.getScale(i);
			ci.tablename = rsmd.getTableName(i);

			col_map.put(i, ci);

		}

		return col_map;

	}

	public static void get_order(MapReduceNode x) throws Exception {

		final MapReduceOrderNode node = (MapReduceOrderNode) x;

		// 输入 目前只支持一个
		Htable ti = get_tableinfo(node.htable_name);
		if (node.level > 0) {
			List<MapReduceNode> parents = node.parents;
			if (parents != null && parents.size() > 0) {
				HashMap<MapReduceNode, Future> taskMap = new HashMap<MapReduceNode, Future>();
				for (int i = 0; i < parents.size(); i++) {
					final MapReduceNode tmpNode = parents.get(i);
					ti = tmpNode.output;
				}
			}
		} else {
			ti = get_tableinfo(node.htable_name);
		}

		Map<Integer, String> map_connstr_map = get_node_by_cond(ti, node.where_str);
		int map_num = map_connstr_map.size();

		Map<Integer, Connection> map_conn_map = open_Conns(map_connstr_map);

		ExecutorService exec = null;
		exec = Executors.newFixedThreadPool(map_num);
		HashMap taskMap = null;

		/*
		 * 查询每个表的洗牌字段的范围
		 */

		taskMap = new HashMap<String, Future>();
		{
			final String sqlstr = base_id_sql.replaceAll("<table>", node.htable_name).replaceAll("<shuffle_field>",
					node.shuffle_field_str);

			for (int j = 0; j < map_num; j++) {
				final int nodeid = j;
				final String connstr = map_connstr_map.get(j);
				final Connection conn = map_conn_map.get(j);

				Callable call = new Callable() {
					public TableIdEntity call() throws Exception {
						logstr = "conn=" + connstr + ",sql=" + sqlstr;
						printLogStr(logstr);
						try {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(sqlstr);
							long minid = Long.MAX_VALUE;
							long maxid = Long.MIN_VALUE;
							long datacount = 0L;
							while (rs.next()) {
								minid = rs.getInt(1);
								maxid = rs.getInt(2);
								datacount = rs.getInt(3);
							}
							TableIdEntity e = new TableIdEntity(nodeid, node.htable_name, node.shuffle_field_str, minid,
									maxid, datacount);
							return e;
						} catch (SQLException e) {
							printLogStr(connstr + COMMA + e.getMessage() + NEWLINE);
							return null;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}
		}
		long minid = Long.MAX_VALUE;
		long maxid = Long.MIN_VALUE;
		long datacount = 0L;
		Iterator iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			TableIdEntity ret = (TableIdEntity) val.get();
			if (ret.minid < minid) {
				minid = ret.minid;
			}
			if (ret.maxid > maxid) {
				maxid = ret.maxid;
			}
			datacount += ret.datacount;
		}

		/*
		 * 
		 * 根据记录数调整 reduce 数 , 使得每个reduce处理的记录数不会太多
		 */

		int reduce_num = (int) datacount / 1000 + 1;

		Map<Integer, String> reduce_connstr_map = new HashMap<Integer, String>();
		for (int i = 0; i < reduce_num; i++) {
			reduce_connstr_map.put(i, "jdbc:mysql://192.168.0.151:3306/db" + i);
		}
		Map<Integer, Connection> reduce_conn_map = open_Conns(reduce_connstr_map);

		if (reduce_num > 1) {
			exec.shutdown();
			exec = Executors.newFixedThreadPool(map_num * reduce_num);
		}

		/*
		 * 计算每个区间
		 */
		Long step_num = 0L;
		if (maxid < minid || datacount <= 0) {
			System.exit(1);
		} else {
			printLogStr("minid=" + minid + ",maxid=" + maxid);
			minid = minid - (minid % reduce_num) - reduce_num * 100;
			maxid = maxid + (reduce_num - (maxid) % reduce_num) + reduce_num * 100;
			printLogStr("minid=" + minid + ",maxid=" + maxid);
			step_num = (maxid - minid) / reduce_num;
			if (step_num <= 0) {
				System.exit(1);
			}
			for (int i = 0; i < reduce_num; i++) {
				logstr = "step " + (i) + " is " + (minid + i * step_num) + " <= x < " + (minid + (i + 1) * step_num);
				printLogStr(logstr);
			}
		}

		/*
		 * 得到在reduce上临时表的建表语句
		 */
		String crt_sql = "";
		Map<Integer, ColumnInfo> col_map = null;
		String index_str = " index idx(" + node.shuffle_field_str + ")";
		{
			final String sqlstr = del_last_semi(node.sql) + " limit 1";

			for (int j = 0; j < 1; j++) {

				final int nodeid = j;
				final String connstr = map_connstr_map.get(j);
				final Connection conn = map_conn_map.get(j);

				logstr = "conn=" + connstr + ",sql=" + sqlstr;
				printLogStr(logstr);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sqlstr);
				ResultSetMetaData rsmd = rs.getMetaData();

				col_map = get_col_map(rsmd);

			}
		}
		String cols_str = get_col_definition_str(col_map, true);

		crt_sql = base_create_table_sql.replaceAll("<table>", "tmp_" + node.htable_name)
				.replaceAll("<cols_str>", cols_str).replaceAll("<index_str>", index_str);

		/*
		 * 在reduce上建好表临时表
		 */
		{
			taskMap.clear();
			final String create_table_sql = crt_sql;
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();

							String dropsql = "DROP TABLE IF EXISTS " + "tmp_" + node.htable_name + " ;";
							logstr = "conn=" + connstr + ",sql=" + dropsql;
							printLogStr(logstr);
							stmt.execute(dropsql);

							logstr = "conn=" + connstr + ",sql=" + create_table_sql;
							printLogStr(logstr);
							boolean ret = stmt.execute(create_table_sql);
							return ret;
						} catch (SQLException e) {
							printLogStr(e.getMessage() + NEWLINE);
							return false;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}
		}

		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 
		 * 边执行map，边执行shuffle map的模式 11 一个sql生成多个问 12 多个sql生成多个文件
		 * 
		 * 执行map，然后执行shuffle map的模式 21 一个sql生成多个问 22 多个sql生成多个文件
		 * 
		 * 生成临时文件，导入到reduce中 读取记录，批量插入到reduce中
		 */

		/*
		 * 执行map和shuffle, 一个map节点执行多次sql，每次导出到一个文件，然后reduce节点
		 */
		String[][] map_file_arr = new String[map_num][reduce_num];
		taskMap = new HashMap<String, Future>();
		{

			for (int j = 0; j < map_num; j++) {
				for (int k = 0; k < reduce_num; k++) {
					Long step_minid = minid + k * step_num;
					Long step_maxid = minid + (k + 1) * step_num;
					final String sqlstr = "select " + node.select_field_str + " from " + node.htable_name + " where "
							+ node.where_str + " and " + node.shuffle_field_str + " >= " + step_minid + " and "
							+ node.shuffle_field_str + " < " + step_maxid + " order by " + node.shuffle_field_str;

					final String map_outfile = "/data/" + node.htable_name + "_" + j + "_" + k + ".txt";
					map_file_arr[j][k] = map_outfile;
					final int map_nodeid = j;
					final String map_connstr = map_connstr_map.get(j);
					final Connection map_conn = map_conn_map.get(j);

					final int reduce_nodeid = k;
					final String reduce_connstr = reduce_connstr_map.get(k);
					final Connection reduce_conn = reduce_conn_map.get(k);

					Callable call = new Callable() {
						public Boolean call() throws Exception {

							try {
								Statement map_stmt = map_conn.createStatement();
								Statement reduce_stmt = reduce_conn.createStatement();

								logstr = "conn=" + map_connstr + ",sql=" + sqlstr;
								printLogStr(logstr);

								ResultSet rs = map_stmt.executeQuery(sqlstr);
								ResultSetMetaData rsmda = rs.getMetaData();
								int cols = rsmda.getColumnCount();

								FileOutputStream fos = new FileOutputStream(map_outfile, false);
								OutputStreamWriter osw = new OutputStreamWriter(fos);
								while (rs.next()) {
									String line = "";
									for (int i = 1; i < cols; i++) {
										line += rs.getString(i) + TAB;
									}
									line += rs.getString(cols);
									osw.write(line + NEWLINE);
								}

								osw.flush();
								osw.close();

								String loadsql = base_load_sql.replaceAll("<table>", "tmp_" + node.htable_name)
										.replaceAll("<map_outfile>", map_outfile);
								logstr = "conn=" + reduce_connstr + ",sql=" + loadsql;
								printLogStr(logstr);

								reduce_stmt.execute(loadsql);

								return true;
							} catch (SQLException e) {
								printLogStr(e.getMessage() + NEWLINE);
								return false;
							}
						}
					};
					Future task = exec.submit(call);
					taskMap.put(map_outfile, task);
				}
			}

		}
		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		{
			/*
			 * 得到在reduce上最终表的建表语句
			 */
			final String sqlstr = crt_sql.replaceAll("tmp_" + node.htable_name, "reduce_" + node.htable_name);
			/*
			 * 在reduce上建好最终表
			 */
			taskMap.clear();
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();

							String dropsql = "DROP TABLE IF EXISTS " + "reduce_" + node.htable_name + " ;";
							logstr = "conn=" + connstr + ",sql=" + dropsql;
							printLogStr(logstr);
							stmt.execute(dropsql);

							logstr = "conn=" + connstr + ",sql=" + sqlstr;
							printLogStr(logstr);
							boolean ret = stmt.execute(sqlstr);
							return ret;
						} catch (SQLException e) {
							printLogStr(e.getMessage() + NEWLINE);
							return false;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}

		}
		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 执行reduce操作
		 */
		{
			final String reduce_sql = ("replace into <table> "
					+ node.sql.replaceAll(node.htable_name, "tmp_" + node.htable_name)).replaceAll("<table>",
							"reduce_" + node.htable_name);
			taskMap = new HashMap<String, Future>();
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();
							logstr = "conn=" + connstr + ",sql=" + reduce_sql;
							printLogStr(logstr);
							boolean ret = stmt.execute(reduce_sql);
							return ret;
						} catch (SQLException e) {
							printLogStr(e.getMessage() + NEWLINE);
							return false;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}
		}

		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 结果表信息
		 */
		if (node.level == 0) {

			Htable oti = new Htable();
			// oti.tablename = "tmp_" + node.htable_name;
			// oti.balancetype = BalanceType.RANGE;
			// oti.balancefield = EMPTY;
			// oti.connstr_map = reduce_connstr_map;
			// oti.conn_map = reduce_conn_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

	}

	public static void get_group(MapReduceNode x) throws Exception {

		final MapReduceGroupNode node = (MapReduceGroupNode) x;

		// 输入 目前只支持一个
		Htable ti = get_tableinfo(node.table_name);
		if (node.level > 0) {
			List<MapReduceNode> parents = node.parents;
			if (parents != null && parents.size() > 0) {
				HashMap<MapReduceNode, Future> taskMap = new HashMap<MapReduceNode, Future>();
				for (int i = 0; i < parents.size(); i++) {
					final MapReduceNode tmpNode = parents.get(i);
					ti = tmpNode.output;
				}
			}
		} else {
			ti = get_tableinfo(node.table_name);
		}

		Map<Integer, String> map_connstr_map = get_node_by_cond(ti, node.where_str);
		int map_num = map_connstr_map.size();

		Map<Integer, Connection> map_conn_map = open_Conns(map_connstr_map);
		Map hdb_conn_map = metadata.open_conn_by_hdb("xxx");

		ExecutorService exec = null;
		exec = Executors.newFixedThreadPool(map_num);
		HashMap taskMap = null;

		/*
		 * 查询每个表的洗牌字段的范围
		 */

		taskMap = new HashMap<String, Future>();
		{
			final String sqlstr = base_id_sql.replaceAll("<table>", node.table_name).replaceAll("<shuffle_field>",
					node.shuffle_field_str);

			for (int j = 0; j < map_num; j++) {
				final int nodeid = j;
				final String connstr = map_connstr_map.get(j);
				final Connection conn = map_conn_map.get(j);

				Callable call = new Callable() {
					public TableIdEntity call() throws Exception {
						logstr = "conn=" + connstr + ",sql=" + sqlstr;
						printLogStr(logstr);
						try {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(sqlstr);
							long minid = Long.MAX_VALUE;
							long maxid = Long.MIN_VALUE;
							long datacount = 0L;
							while (rs.next()) {
								minid = rs.getInt(1);
								maxid = rs.getInt(2);
								datacount = rs.getInt(3);
							}
							TableIdEntity e = new TableIdEntity(nodeid, node.table_name, node.shuffle_field_str, minid,
									maxid, datacount);
							return e;
						} catch (SQLException e) {
							printLogStr(connstr + COMMA + e.getMessage() + NEWLINE);
							return null;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}
		}
		long minid = Long.MAX_VALUE;
		long maxid = Long.MIN_VALUE;
		long datacount = 0L;
		Iterator iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			TableIdEntity ret = (TableIdEntity) val.get();
			if (ret.minid < minid) {
				minid = ret.minid;
			}
			if (ret.maxid > maxid) {
				maxid = ret.maxid;
			}
			datacount += ret.datacount;
		}

		/*
		 * 
		 * 根据记录数调整 reduce 数 , 使得每个reduce处理的记录数不会太多
		 */

		int reduce_num = (int) datacount / 1000 + 1;

		Map<Integer, String> reduce_connstr_map = new HashMap<Integer, String>();
		for (int i = 0; i < reduce_num; i++) {
			reduce_connstr_map.put(i, "jdbc:mysql://192.168.0.151:3306/db" + i);
		}
		Map<Integer, Connection> reduce_conn_map = open_Conns(reduce_connstr_map);

		if (reduce_num > 1) {
			exec.shutdown();
			exec = Executors.newFixedThreadPool(map_num * reduce_num);
		}

		/*
		 * 计算每个区间
		 */
		Long step_num = 0L;
		if (maxid < minid || datacount <= 0) {
			System.exit(1);
		} else {
			printLogStr("minid=" + minid + ",maxid=" + maxid);
			minid = minid - (minid % reduce_num) - reduce_num * 100;
			maxid = maxid + (reduce_num - (maxid) % reduce_num) + reduce_num * 100;
			printLogStr("minid=" + minid + ",maxid=" + maxid);
			step_num = (maxid - minid) / reduce_num;
			if (step_num <= 0) {
				System.exit(1);
			}
			for (int i = 0; i < reduce_num; i++) {
				logstr = "step " + (i) + " is " + (minid + i * step_num) + " <= x < " + (minid + (i + 1) * step_num);
				printLogStr(logstr);
			}
		}

		/*
		 * 得到在reduce上临时表的建表语句
		 */

		String crt_sql = "";
		Map<Integer, ColumnInfo> col_map = null;
		String index_str = " index idx(" + node.shuffle_field_str + ")";
		{
			final String sqlstr = del_last_semi(node.sql) + " limit 1";

			for (int j = 0; j < 1; j++) {

				final int nodeid = j;
				final String connstr = map_connstr_map.get(j);
				final Connection conn = map_conn_map.get(j);

				logstr = "conn=" + connstr + ",sql=" + sqlstr;
				printLogStr(logstr);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sqlstr);
				ResultSetMetaData rsmd = rs.getMetaData();

				col_map = get_col_map(rsmd);

			}
		}
		String cols_str = get_col_definition_str(col_map, true);

		crt_sql = base_create_table_sql.replaceAll("<table>", "tmp_" + node.table_name)
				.replaceAll("<cols_str>", cols_str).replaceAll("<index_str>", index_str);

		/*
		 * 在reduce上建好表临时表
		 */
		{
			taskMap.clear();
			final String create_table_sql = crt_sql;
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();

							String dropsql = "DROP TABLE IF EXISTS " + "tmp_" + node.table_name + " ;";
							logstr = "conn=" + connstr + ",sql=" + dropsql;
							printLogStr(logstr);
							stmt.execute(dropsql);

							logstr = "conn=" + connstr + ",sql=" + create_table_sql;
							printLogStr(logstr);
							boolean ret = stmt.execute(create_table_sql);
							return ret;
						} catch (SQLException e) {
							printLogStr(e.getMessage() + NEWLINE);
							return false;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}
		}

		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 
		 * 边执行map，边执行shuffle map的模式 11 一个sql生成多个问 12 多个sql生成多个文件
		 * 
		 * 执行map，然后执行shuffle map的模式 21 一个sql生成多个问 22 多个sql生成多个文件
		 * 
		 * 生成临时文件，导入到reduce中 读取记录，批量插入到reduce中
		 */

		/*
		 * 执行map和shuffle, 一个map节点执行多次sql，每次导出到一个文件，然后reduce节点
		 */
		String[][] map_file_arr = new String[map_num][reduce_num];
		taskMap = new HashMap<String, Future>();
		{

			for (int j = 0; j < map_num; j++) {
				for (int k = 0; k < reduce_num; k++) {
					Long step_minid = minid + k * step_num;
					Long step_maxid = minid + (k + 1) * step_num;
					final String sqlstr = "select " + node.select_field_str + " from " + node.table_name + " where "
							+ node.where_str + " and " + node.shuffle_field_str + " >= " + step_minid + " and "
							+ node.shuffle_field_str + " < " + step_maxid + " group by " + node.shuffle_field_str;

					final String map_outfile = "/data/" + node.table_name + "_" + j + "_" + k + ".txt";
					map_file_arr[j][k] = map_outfile;
					final int map_nodeid = j;
					final String map_connstr = map_connstr_map.get(j);
					final Connection map_conn = map_conn_map.get(j);

					final int reduce_nodeid = k;
					final String reduce_connstr = reduce_connstr_map.get(k);
					final Connection reduce_conn = reduce_conn_map.get(k);

					Callable call = new Callable() {
						public Boolean call() throws Exception {

							try {
								Statement map_stmt = map_conn.createStatement();
								Statement reduce_stmt = reduce_conn.createStatement();

								logstr = "conn=" + map_connstr + ",sql=" + sqlstr;
								printLogStr(logstr);

								ResultSet rs = map_stmt.executeQuery(sqlstr);
								ResultSetMetaData rsmda = rs.getMetaData();
								int cols = rsmda.getColumnCount();

								FileOutputStream fos = new FileOutputStream(map_outfile, false);
								OutputStreamWriter osw = new OutputStreamWriter(fos);
								while (rs.next()) {
									String line = "";
									for (int i = 1; i < cols; i++) {
										line += rs.getString(i) + TAB;
									}
									line += rs.getString(cols);
									osw.write(line + NEWLINE);
								}

								osw.flush();
								osw.close();

								String loadsql = base_load_sql.replaceAll("<table>", "tmp_" + node.table_name)
										.replaceAll("<map_outfile>", map_outfile);
								logstr = "conn=" + reduce_connstr + ",sql=" + loadsql;
								printLogStr(logstr);

								reduce_stmt.execute(loadsql);

								return true;
							} catch (SQLException e) {
								printLogStr(e.getMessage() + NEWLINE);
								return false;
							}
						}
					};
					Future task = exec.submit(call);
					taskMap.put(map_outfile, task);
				}
			}

		}
		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		{
			/*
			 * 得到在reduce上最终表的建表语句
			 */
			final String sqlstr = crt_sql.replaceAll("tmp_" + node.table_name, "reduce_" + node.table_name);
			/*
			 * 在reduce上建好最终表
			 */
			taskMap.clear();
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();

							String dropsql = "DROP TABLE IF EXISTS " + "reduce_" + node.table_name + " ;";
							logstr = "conn=" + connstr + ",sql=" + dropsql;
							printLogStr(logstr);
							stmt.execute(dropsql);

							logstr = "conn=" + connstr + ",sql=" + sqlstr;
							printLogStr(logstr);
							boolean ret = stmt.execute(sqlstr);
							return ret;
						} catch (SQLException e) {
							printLogStr(e.getMessage() + NEWLINE);
							return false;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}

		}
		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 执行reduce操作
		 */
		{

			final String reduce_sql = "replace into " + " reduce_" + node.table_name + " select "
					+ get_col_select_str(col_map) + " from " + "tmp_" + node.table_name + " group by "
					+ node.shuffle_field_str;

			taskMap = new HashMap<String, Future>();
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();
							logstr = "conn=" + connstr + ",sql=" + reduce_sql;
							printLogStr(logstr);
							boolean ret = stmt.execute(reduce_sql);
							return ret;
						} catch (SQLException e) {
							printLogStr(e.getMessage() + NEWLINE);
							return false;
						}
					}
				};
				Future task = exec.submit(call);
				taskMap.put(connstr, task);
			}
		}

		iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		if (node.level == 0) {

			Htable oti = new Htable();
			// oti.tablename = "tmp_" + node.table_name;
			// oti.balancetype = BalanceType.RANGE;
			// oti.balancefield = EMPTY;
			// oti.connstr_map = reduce_connstr_map;
			// oti.conn_map = reduce_conn_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

	}

	public static void execNode(MapReduceNode node) throws Exception {

		switch (node.nodetype) {

		case NodeType.ORDER:
			get_order(node);
			break;

		case NodeType.GROUP:
			get_group(node);
			break;

		// case NodeType.JOIN:
		// get_join(node);
		// break;
		//
		// case NodeType.UNION:
		// get_union(node);
		// break;
		}

	}

	public static void do_root_node(MapReduceNode node, Boolean isp) throws Exception {

		/*
		 * 没有parents的节点，增加线程执行本节点
		 * 
		 * 有parents的节点，增加多个线程并发执行parents,parents执行完成之后，执行本节点
		 */

		List<MapReduceNode> parents = node.parents;

		if (parents != null && parents.size() > 0) {
			HashMap<MapReduceNode, Future> taskMap = new HashMap<MapReduceNode, Future>();
			for (int i = 0; i < parents.size(); i++) {
				final MapReduceNode tmpNode = parents.get(i);
				Callable call = new Callable() {
					public Boolean call() throws Exception {
						do_root_node(tmpNode, true);
						return true;
					}
				};
				Future task = x.submit(call);
				taskMap.put(tmpNode, task);

			}

			Iterator iter = taskMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				MapReduceNode key = (MapReduceNode) entry.getKey();
				Future val = (Future) entry.getValue();
				Boolean o = (Boolean) val.get();
			}
			taskMap.clear();

			/*
			 * 非输入节点，输出节点并且有父节点的情况
			 */
			final MapReduceNode tmpNode = node;
			HashMap<MapReduceNode, Future> taskMap2 = new HashMap<MapReduceNode, Future>();
			Callable call = new Callable() {
				public Boolean call() throws Exception {

					// printLogStr(tmp.toString() + " 3000");
					// Thread.currentThread().sleep(3000);
					execNode(tmpNode);
					return true;
				}
			};
			Future task = x.submit(call);
			taskMap2.put(tmpNode, task);

			Iterator iter2 = taskMap2.entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry entry = (Map.Entry) iter2.next();
				MapReduceNode key = (MapReduceNode) entry.getKey();
				Future val = (Future) entry.getValue();
				Boolean o = (Boolean) val.get();
			}
			taskMap2.clear();
		} else {

			/*
			 * 输入节点执行
			 */
			if (isp) {
				final MapReduceNode tmpNode = node;
				// printLogStr(node.toString() + " 2000");
				// Thread.currentThread().sleep(2000);
				execNode(tmpNode);
			}

			/*
			 * 本节点自己执行，不是父节点传过来的 比如只有一个节点的工作
			 */
			else {
				HashMap<MapReduceNode, Future> taskMap = new HashMap<MapReduceNode, Future>();
				final MapReduceNode tmpNode = node;
				Callable call = new Callable() {
					public Boolean call() throws Exception {
						// printLogStr(tmp.toString() + " 4000");
						// Thread.currentThread().sleep(4000);
						execNode(tmpNode);
						return true;
					}
				};
				Future task = x.submit(call);
				taskMap.put(node, task);

				Iterator iter = taskMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					MapReduceNode key = (MapReduceNode) entry.getKey();
					Future val = (Future) entry.getValue();
					Boolean o = (Boolean) val.get();
				}
			}

		}

	}

	private static boolean order_hdc_is_same(Map hdc_list_by_htable, List<SQLSelectOrderByItem> orderby_items) {
		boolean order_hdc_is_same = true;
		for (int i = 0; i < orderby_items.size(); i++) {
			String groupby_str = orderby_items.get(i).getExpr().toString();
			Hcolumn hcolumn = (Hcolumn) hdc_list_by_htable.get(groupby_str);
			if (hcolumn == null) {
				order_hdc_is_same = false;
			} else {
				if (hcolumn.getHdc_orderno() != i) {
					order_hdc_is_same = false;
				}
			}
		}
		return order_hdc_is_same;
	}

	private static boolean group_hdc_is_same(Map hdc_list_by_htable, List<SQLExpr> groupby_items) {
		boolean group_hdc_is_same = true;
		for (int i = 0; i < groupby_items.size(); i++) {
			String groupby_str = groupby_items.get(i).toString();
			Hcolumn hcolumn = (Hcolumn) hdc_list_by_htable.get(groupby_str);
			if (hcolumn == null) {
				group_hdc_is_same = false;
			} else {
				if (hcolumn.getHdc_orderno() != i) {
					group_hdc_is_same = false;
				}
			}
		}
		return group_hdc_is_same;
	}

	private static void parse_join(SQLExpr where) {
		// 判读where中是否包含 hdc的信息,以便发送到指定的节点

		if (where instanceof SQLInListExpr) {
			SQLInListExpr top_expr = (SQLInListExpr) where;
			boolean isnot = top_expr.isNot();
			List<SQLExpr> target_list = top_expr.getTargetList();
			return;
		}
		if (where instanceof SQLBinaryOpExpr) {

			SQLBinaryOpExpr top_expr = (SQLBinaryOpExpr) where;
			SQLBinaryOperator top_op = top_expr.getOperator();

			if (top_op == SQLBinaryOperator.BooleanOr) {

				Map<Integer, SQLExpr> or_map = new HashMap<Integer, SQLExpr>();
				Map<Integer, Map<Integer, SQLExpr>> and_map = new HashMap<Integer, Map<Integer, SQLExpr>>();

				int or_idx = 0;
				SQLBinaryOpExpr or_left = top_expr;
				SQLBinaryOpExpr or_right;

				SQLBinaryOperator or_left_op = top_op;
				SQLBinaryOperator or_right_op;

				boolean leftisin = false;
				while (or_left_op == SQLBinaryOperator.BooleanOr) {

					if (or_left.getRight() instanceof SQLBinaryOpExpr) {
						or_right = (SQLBinaryOpExpr) or_left.getRight();
						or_right_op = or_right.getOperator();
						or_map.put(or_idx, or_right);

						if (or_right_op == SQLBinaryOperator.BooleanAnd) {
							deal_and_right(and_map, or_idx, or_right, or_right_op);
						} else {
							Map<Integer, SQLExpr> and_map_ele = new HashMap<Integer, SQLExpr>();
							and_map_ele.put(0, or_right);
							and_map.put(0, and_map_ele);
						}

					} else if (or_left.getRight() instanceof SQLInListExpr) {
						or_map.put(0, or_left.getRight());
					} else {

					}

					if (or_left.getLeft() instanceof SQLBinaryOpExpr) {
						or_left = (SQLBinaryOpExpr) or_left.getLeft();
						or_left_op = or_left.getOperator();
					} else if (or_left.getLeft() instanceof SQLInListExpr) {
						leftisin = true;
						or_left_op = null;
					} else {

					}
					or_idx++;

				}

				// 最左右不是IN
				if (!leftisin) {
					or_map.put(or_idx, or_left);

					if (or_left_op == SQLBinaryOperator.BooleanAnd) {
						deal_and_right(and_map, or_idx, or_left, or_left_op);
					} else {
						Map<Integer, SQLExpr> and_map_ele = new HashMap<Integer, SQLExpr>();
						and_map_ele.put(0, or_left);
						and_map.put(0, and_map_ele);
					}
					or_idx++;
				}

			} else if (top_op == SQLBinaryOperator.BooleanAnd) {

				Map<Integer, SQLExpr> and_map = new HashMap<Integer, SQLExpr>();

				int and_idx = 0;
				SQLBinaryOpExpr and_left = top_expr;
				SQLBinaryOpExpr and_right;
				SQLBinaryOperator and_left_op = top_op;
				SQLBinaryOperator and_right_op;
				boolean leftisin = false;
				while (and_left_op == SQLBinaryOperator.BooleanAnd) {
					if (and_left.getRight() instanceof SQLBinaryOpExpr) {
						and_right = (SQLBinaryOpExpr) and_left.getRight();
						and_right_op = and_right.getOperator();
						and_map.put(and_idx, and_right);

						if (and_right_op.isRelational()) {
							deal_relational(and_right);
						} else {

						}
					} else if (and_left.getRight() instanceof SQLInListExpr) {
						and_map.put(and_idx, and_left.getRight());
					} else {

					}

					if (and_left.getLeft() instanceof SQLBinaryOpExpr) {
						and_left = (SQLBinaryOpExpr) and_left.getLeft();
						and_left_op = and_left.getOperator();
					} else if (and_left.getLeft() instanceof SQLInListExpr) {
						leftisin = true;
						and_map.put(and_idx, and_left.getLeft());
						and_left_op = null;
					} else {

					}
					and_idx++;

				}
				// 最左右不是IN
				if (!leftisin) {

					and_map.put(and_idx, and_left);
					if (and_left_op.isRelational()) {
						deal_relational(and_left);
					} else {

					}
					and_idx++;
				}
			} else {
				// 只有一个条件
				if (top_op.isRelational()) {
					deal_relational(top_expr);
				} else {

				}
			}
		}
	}

	private static void parse_where(SQLExpr where) {
		// 判读where中是否包含 hdc的信息,以便发送到指定的节点

		if (where instanceof SQLInListExpr) {
			SQLInListExpr top_expr = (SQLInListExpr) where;
			boolean isnot = top_expr.isNot();
			List<SQLExpr> target_list = top_expr.getTargetList();
			return;
		}
		if (where instanceof SQLBinaryOpExpr) {

			SQLBinaryOpExpr top_expr = (SQLBinaryOpExpr) where;
			SQLBinaryOperator top_op = top_expr.getOperator();

			if (top_op == SQLBinaryOperator.BooleanOr) {

				Map<Integer, SQLExpr> or_map = new HashMap<Integer, SQLExpr>();
				Map<Integer, Map<Integer, SQLExpr>> and_map = new HashMap<Integer, Map<Integer, SQLExpr>>();

				int or_idx = 0;
				SQLBinaryOpExpr or_left = top_expr;
				SQLBinaryOpExpr or_right;

				SQLBinaryOperator or_left_op = top_op;
				SQLBinaryOperator or_right_op;

				boolean leftisin = false;
				while (or_left_op == SQLBinaryOperator.BooleanOr) {

					if (or_left.getRight() instanceof SQLBinaryOpExpr) {
						or_right = (SQLBinaryOpExpr) or_left.getRight();
						or_right_op = or_right.getOperator();
						or_map.put(or_idx, or_right);

						if (or_right_op == SQLBinaryOperator.BooleanAnd) {
							deal_and_right(and_map, or_idx, or_right, or_right_op);
						} else {
							Map<Integer, SQLExpr> and_map_ele = new HashMap<Integer, SQLExpr>();
							and_map_ele.put(0, or_right);
							and_map.put(0, and_map_ele);
						}

					} else if (or_left.getRight() instanceof SQLInListExpr) {
						or_map.put(0, or_left.getRight());
					} else {

					}

					if (or_left.getLeft() instanceof SQLBinaryOpExpr) {
						or_left = (SQLBinaryOpExpr) or_left.getLeft();
						or_left_op = or_left.getOperator();
					} else if (or_left.getLeft() instanceof SQLInListExpr) {
						leftisin = true;
						or_left_op = null;
					} else {

					}
					or_idx++;

				}

				// 最左右不是IN
				if (!leftisin) {
					or_map.put(or_idx, or_left);

					if (or_left_op == SQLBinaryOperator.BooleanAnd) {
						deal_and_right(and_map, or_idx, or_left, or_left_op);
					} else {
						Map<Integer, SQLExpr> and_map_ele = new HashMap<Integer, SQLExpr>();
						and_map_ele.put(0, or_left);
						and_map.put(0, and_map_ele);
					}
					or_idx++;
				}

			} else if (top_op == SQLBinaryOperator.BooleanAnd) {

				Map<Integer, SQLExpr> and_map = new HashMap<Integer, SQLExpr>();

				int and_idx = 0;
				SQLBinaryOpExpr and_left = top_expr;
				SQLBinaryOpExpr and_right;
				SQLBinaryOperator and_left_op = top_op;
				SQLBinaryOperator and_right_op;
				boolean leftisin = false;
				while (and_left_op == SQLBinaryOperator.BooleanAnd) {
					if (and_left.getRight() instanceof SQLBinaryOpExpr) {
						and_right = (SQLBinaryOpExpr) and_left.getRight();
						and_right_op = and_right.getOperator();
						and_map.put(and_idx, and_right);

						if (and_right_op.isRelational()) {
							deal_relational(and_right);
						} else {

						}
					} else if (and_left.getRight() instanceof SQLInListExpr) {
						and_map.put(and_idx, and_left.getRight());
					} else {

					}

					if (and_left.getLeft() instanceof SQLBinaryOpExpr) {
						and_left = (SQLBinaryOpExpr) and_left.getLeft();
						and_left_op = and_left.getOperator();
					} else if (and_left.getLeft() instanceof SQLInListExpr) {
						leftisin = true;
						and_map.put(and_idx, and_left.getLeft());
						and_left_op = null;
					} else {

					}
					and_idx++;

				}
				// 最左右不是IN
				if (!leftisin) {

					and_map.put(and_idx, and_left);
					if (and_left_op.isRelational()) {
						deal_relational(and_left);
					} else {

					}
					and_idx++;
				}
			} else {
				// 只有一个条件
				if (top_op.isRelational()) {
					deal_relational(top_expr);
				} else {

				}
			}
		}
	}

	private static void deal_and_right(Map<Integer, Map<Integer, SQLExpr>> and_map, int or_idx, SQLExpr or_right,
			SQLBinaryOperator or_right_op) {

		if (or_right instanceof SQLInListExpr) {
			SQLInListExpr top_expr = (SQLInListExpr) or_right;
			boolean isnot = top_expr.isNot();
			List<SQLExpr> target_list = top_expr.getTargetList();
			return;
		}
		if (or_right instanceof SQLBinaryOpExpr) {

			int and_idx = 0;
			SQLBinaryOpExpr top_expr = (SQLBinaryOpExpr) or_right;
			SQLBinaryOpExpr and_left = top_expr;
			SQLBinaryOpExpr and_right;
			SQLBinaryOperator and_left_op = or_right_op;
			SQLBinaryOperator and_right_op;

			Map<Integer, SQLExpr> and_map_ele = new HashMap<Integer, SQLExpr>();
			and_map.put(or_idx, and_map_ele);

			boolean leftisin = false;
			while (and_left_op == SQLBinaryOperator.BooleanAnd) {

				if (and_left.getRight() instanceof SQLBinaryOpExpr) {
					and_right = (SQLBinaryOpExpr) and_left.getRight();
					and_right_op = and_right.getOperator();

					and_map_ele.put(and_idx, and_right);

					if (and_right_op.isRelational()) {
						deal_relational(and_right);
					} else {

					}
				} else if (and_left.getRight() instanceof SQLInListExpr) {
					and_map_ele.put(and_idx, and_left.getRight());
				} else {

				}

				if (and_left.getLeft() instanceof SQLBinaryOpExpr) {
					and_left = (SQLBinaryOpExpr) and_left.getLeft();

					and_left_op = and_left.getOperator();
				} else if (and_left.getLeft() instanceof SQLInListExpr) {
					leftisin = true;
					and_map_ele.put(and_idx, and_left.getLeft());
					and_left_op = null;
				} else {

				}
				and_idx++;

			}

			// 最左右不是IN
			if (!leftisin) {
				and_map_ele.put(and_idx, and_left);

				if (and_left_op.isRelational()) {
					deal_relational(and_left);
				} else {

				}
				and_idx++;
			}
		}
	}

	private static void deal_relational(SQLBinaryOpExpr and_left) {
		SQLExpr left = and_left.getLeft();
		SQLExpr right = and_left.getRight();

		SQLBinaryOperator op = and_left.getOperator();

		String left_str = left.toString();
		String right_str = right.toString();
	}

	public static void test_delete_htable(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlDeleteStatement stmt = (MySqlDeleteStatement) iterator.next();

			String alias = stmt.getTableSource().toString();
			SQLExpr sqlExpr = stmt.getWhere();

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
							pst.setInt(4, i);
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

	public static void test_show(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {

			Object obj = iterator.next();

			if (obj instanceof MySqlShowDatabasesStatement) {

				MySqlShowDatabasesStatement stmt = (MySqlShowDatabasesStatement) obj;

				String stmt_sql = stmt.toString();

				String execsql = "select hdb_name from hdb";
				PreparedStatement pst = metadata.metadataConn.prepareStatement(execsql);

				ResultSet rs = pst.executeQuery();

				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String columns_str = "====result====\n";
				for (int i = 1; i <= count; i++) {
					columns_str = columns_str + rsmd.getColumnName(i) + "\t";
				}
				System.out.println(columns_str);
				while (rs.next()) {
					String line = "";
					for (int i = 1; i <= count; i++) {
						line = line + rs.getString(i) + "\t";
					}
					System.out.println(line);
				}

			}
			if (obj instanceof SQLShowTablesStatement) {
				SQLShowTablesStatement stmt = (SQLShowTablesStatement) obj;
				String stmt_sql = stmt.toString();

				String execsql = "select hdb_name,htable_name from htable where hdb_name=?";
				PreparedStatement pst = metadata.metadataConn.prepareStatement(execsql);
				if (stmt.getDatabase() != null) {
					String db_name = stmt.getDatabase().getSimpleName();
					pst.setString(1, db_name);
				} else {
					pst.setString(1, current_hdb_name);
				}

				ResultSet rs = pst.executeQuery();

				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String columns_str = "====result====\n";
				for (int i = 1; i <= count; i++) {
					columns_str = columns_str + rsmd.getColumnName(i) + "\t";
				}
				System.out.println(columns_str);
				while (rs.next()) {
					String line = "";
					for (int i = 1; i <= count; i++) {
						line = line + rs.getString(i) + "\t";
					}
					System.out.println(line);
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

	public static void test_drop_table(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			SQLDropTableStatement stmt = (SQLDropTableStatement) iterator.next();

			String hdb_name = "";
			String htable_name = "";

			String alias = stmt.getTableSources().get(0).getExpr().toString();
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				hdb_name = current_hdb_name;
				htable_name = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				hdb_name = alias_arr[0];
				htable_name = alias_arr[1];
			}

			// check hdb_name和hdp_name 是否存在
			// Htable htable = new Htable(hdb_name, htable_name, hdp_name);

			String htable_sql = "delete from htable where hdb_name='<hdb_name>' and htable_name='<htable_name>'"
					.replaceAll("<hdb_name>", hdb_name).replaceAll("<htable_name>", htable_name);
			String hindex_sql = "delete from hindex where hdb_name='<hdb_name>' and htable_name='<htable_name>'"
					.replaceAll("<hdb_name>", hdb_name).replaceAll("<htable_name>", htable_name);
			String hcolumn_sql = "delete from hcolumn where hdb_name='<hdb_name>' and htable_name='<htable_name>'"
					.replaceAll("<hdb_name>", hdb_name).replaceAll("<htable_name>", htable_name);
			String rhindexhcolumn_sql = "delete from rhindexhcolumn where hdb_name='<hdb_name>' and htable_name='<htable_name>'"
					.replaceAll("<hdb_name>", hdb_name).replaceAll("<htable_name>", htable_name);

			PreparedStatement htable_ps = metadata.metadataConn.prepareStatement(htable_sql);
			PreparedStatement hindex_ps = metadata.metadataConn.prepareStatement(hindex_sql);
			PreparedStatement hcolumn_ps = metadata.metadataConn.prepareStatement(hcolumn_sql);
			PreparedStatement rhindexhcolumn_ps = metadata.metadataConn.prepareStatement(rhindexhcolumn_sql);

			htable_ps.execute();
			htable_ps.close();
			hindex_ps.execute();
			hindex_ps.close();
			hcolumn_ps.execute();
			hcolumn_ps.close();
			rhindexhcolumn_ps.execute();
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
				System.out.println(hdb_name + "." + htable_name + " droped in " + key);

			}
			System.out.println(hdb_name + "." + htable_name + " droped !");
		}
	}

	public static void test_create_table(String sql) throws Exception {

		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) iterator.next();

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

	public static Map<Integer, Connection> open_Conns(Map<Integer, String> conn_str_map) throws Exception {
		int conns_len = conn_str_map.size();
		Map<Integer, Connection> conns = new HashMap<Integer, Connection>(conns_len);
		Class.forName("com.mysql.jdbc.Driver");
		for (int i = 0; i < conns_len; i++) {
			String url = conn_str_map.get(i);
			Connection tmp = DriverManager.getConnection(url, user, pass);
			tmp.setAutoCommit(true);
			conns.put(i, tmp);
			printLogStr(url + " connection opened !");
		}
		return conns;
	}

	public static void openLogFile() throws Exception {
		if (logosw != null) {
			logosw.close();
		}
		FileOutputStream logfos = new FileOutputStream(logfile, true);
		logosw = new OutputStreamWriter(logfos, "UTF8");
	}

	public static void openResultFile() throws Exception {
		if (resultosw != null) {
			resultosw.close();
		}
		FileOutputStream fos = new FileOutputStream(resultfile, true);
		resultosw = new OutputStreamWriter(fos, "UTF8");
	}

	public static void printLogStr(String s) {
		String r = spacedatetimeformat.format(new Date()) + TAB + s;
		System.out.println(r);
		try {
			if (logosw != null) {
				logosw.write(r + NEWLINE);
				logosw.flush();
			}
		} catch (IOException e) {
			System.out.println("write file error :" + r);
		}
	}

	Map<Integer, String> jdbcMappings = getAllJdbcTypeNames();

	public Map<Integer, String> getAllJdbcTypeNames() {

		Map<Integer, String> result = new HashMap<Integer, String>();
		try {

			for (Field field : Types.class.getFields()) {
				result.put((Integer) field.get(null), field.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static void test_select_meta() throws Exception {
		MetaDataImpl d = new MetaDataImpl();
		d.getConnection();
		d.getAllMeta();
	}

	private static MetaDataImpl metadata = new MetaDataImpl();

}
