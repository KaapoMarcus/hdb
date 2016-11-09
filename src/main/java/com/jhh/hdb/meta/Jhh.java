package com.jhh.hdb.meta;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Jhh {

	static String[] test_dates = {};
	static String teststr = null;
	static String[] args = null;

	static DateFormat partitiondatetimeformat = new SimpleDateFormat("yyyyMM");
	static DateFormat datetimeformat = new SimpleDateFormat(
			"yyyy-MM-dd_HH:mm:ss");
	static DateFormat spacedatetimeformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	public static String OK = "OK";
	public static String FAILED = "FAILED";
	public static String COMMA = ",";
	public static String TAB = "	";
	public static String COLON = ":";
	public static String SPACE = " ";
	public static String POINT = ".";
	public static String EMPTY = "";
	public static String UNDERLINE = "_";
	public static String NEWLINE = "\n";
	public static String WAVY = "~";
	public static String SEMI = ";";
	public static String S_QUOTE = "'";
	public static String D_QUOTE = "\"";
	public static String VERTICAL_LINE = "|";
	public static String MINUS = "-";
	public static String UNKOWN = "unkown";
	static String ZERO_MINSEC = ":00:00";
	static String ZERO_HOURMINSEC = "00:00:00";
	static String[] steps = {};

	static String step = EMPTY;
	static String sub_step = EMPTY;
	static OutputStreamWriter logosw = null;
	static OutputStreamWriter tmposw = null;
	static OutputStreamWriter resultosw = null;
	static String logfile = null;
	static String tmpfile = null;
	static String resultfile = null;
	static String user = "root";
	static String pass = "yunjee0515ueopro1234";
	static Connection[] conns;
	static Connection conn;
	static String ALLSTEPS = "";

	static Statement stmt = null;
	static ResultSet rs = null;
	static String result_suffix = ".txt";
	static String log_suffix = ".log";
	static String tmp_suffix = ".tmp";
	static String load_suffix = ".load";
	static String start_str = "start ";
	static String stop_str = "stop ";

	static String logstr = null;
	static String tmpstr = null;
	static String resultstr = null;
	static String basesql = null;
	static String sqlstr = null;
	static String replacesql = null;

	static String start_timestamp = null;
	static String stop_timestamp = null;

	static String[] register_dates = {};
	static String[] action_dates = {};

	static String WEEKLY = "0";
	static String MONTHLY = "1";
	static int[] ad_ids = {};
	static String workDir = "/data/";
	static int ad_id_limit_cnt = 50;
	static String tmpDir = null;
	static String register_date = null;
	static String action_date = null;
	static String action_hour = null;
	static String other_args = null;
	static int register_day_cnt = 0;
	static int ad_id = -1;
	static String action_table = null;
	static String action_desc = null;
	static String action_type = null;

	// static String tmp_tablename_userlevel = EMPTY;
	static String tmp_tab_pre = "test.tmp_";
	static String merge_tab_pre = "test.merge_";
	static int date_cnt = 0;

	public static void closeLogFile() throws Exception {
		if (logosw != null) {
			logosw.close();
		}
	}

	public static void closeResultFile() throws Exception {
		if (resultosw != null) {
			resultosw.close();
		}
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

	public static void close_Conns(Map<Integer, Connection> conn_map)
			throws Exception {
		int conns_len = conn_map.size();

		for (int i = 0; i < conns_len; i++) {
			Connection tmp = conn_map.get(i);
			if (tmp != null) {
				tmp.close();
			}
			printLogStr(i + " connection closed !");
		}
	}

	public static Map<Integer, Connection> open_Conns(
			Map<Integer, String> conn_str_map) throws Exception {
		int conns_len = conn_str_map.size();
		Map<Integer, Connection> conns = new HashMap<Integer, Connection>(
				conns_len);
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



	public static void main(String[] args) throws Exception {
		get_xxx();
	}

	/*
	 * select from user_info join user_info_etc using
	 * user_info.id=user_info_etc.user_id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_xxx() throws Exception {
		step = "get_xxx";
		logfile = workDir + step + log_suffix;
		openLogFile();
		resultfile = workDir + step + result_suffix;
		openResultFile();

		logstr = start_str + step;
		printLogStr(logstr);

		int map_num = 4;
		int reduce_num = 3;
		Map<Integer, String> map_connstr_map = new HashMap<Integer, String>();
		Map<Integer, String> reduce_connstr_map = new HashMap<Integer, String>();

		for (int i = 0; i < map_num; i++) {
			map_connstr_map.put(i, "jdbc:mysql://192.168.0.151:3306/db" + i);
		}
		for (int i = 0; i < reduce_num; i++) {
			reduce_connstr_map.put(i, "jdbc:mysql://192.168.0.151:3306/db" + i);
		}

		Map<Integer, Connection> map_conn_map = open_Conns(map_connstr_map);
		Map<Integer, Connection> reduce_conn_map = open_Conns(reduce_connstr_map);

		ExecutorService exec = null;
		exec = Executors.newFixedThreadPool(map_num * reduce_num);
		HashMap taskMap = null;

		final String[] table_arr = new String[] { "user_info", "user_info_etc" };
		final String[] balance_field_arr = new String[] { "id", "user_id" };

		final String base_id_sql = "select min(<balance_field>) , max(<balance_field>) from <table>  ;";

		final String[] tmp_tablename_arr = new String[] { "tmp_user_info",
				"tmp_user_info_etc" };
		final String[] tmp_table_fields_arr = new String[] { "ID,NICK",
				"USER_ID,QQ" };
		final String[] tmp_tablecrtsql_arr = new String[] {
				"CREATE TABLE tmp_user_info (    `ID` bigint(20) NOT NULL,    `NICK` varchar(32) NOT NULL DEFAULT '',    PRIMARY KEY (`ID`),    KEY `idx_nick` (`NICK`)  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;  ",

				"CREATE TABLE tmp_user_info_etc (    `USER_ID` bigint(20) NOT NULL,    `QQ` varchar(20) NOT NULL DEFAULT '',    PRIMARY KEY (`USER_ID`)  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;  " };

		final String base_map_sql = "select * from <table> where <balance_field> >= <step_minid> and <balance_field> < <step_maxid> ;";
		final String base_load_sql = "LOAD DATA LOCAL INFILE '<map_outfile>' replace into table <table> fields terminated by '\\t' lines terminated by '\\n' (<tmp_table_fields>);";

		final String finaltab = "final_user_info";
		final String base_final_crttab_sql = "CREATE TABLE `final_user_info` (    `ID` bigint(20) NOT NULL,    `NICK` varchar(32) NOT NULL DEFAULT '',    `QQ` varchar(20) NOT NULL DEFAULT '',    PRIMARY KEY (`ID`)  ) ENGINE=InnoDB DEFAULT CHARSET=utf8   ; ";

		final String base_reduce_sql = "replace into <table> SELECT t1.ID,t1.NICK,t2.QQ from user_info t1  join user_info_etc t2 on (t1.id=t2.USER_ID) ;";
		final String base_out_sql = "select * from <table>  ;";

		/*
		 * 查询每个表的洗牌字段的范围
		 */

		taskMap = new HashMap<String, Future>();
		for (int i = 0; i < table_arr.length; i++) {
			final String tablename = table_arr[i];
			final String balancefield = balance_field_arr[i];
			final String sqlstr = base_id_sql.replaceAll("<table>", tablename)
					.replaceAll("<balance_field>", balancefield);

			for (int j = 0; j < map_num; j++) {
				final int nodeid = j;
				final String connstr = map_connstr_map.get(j);
				final Connection conn = map_conn_map.get(j);

				Callable call = new Callable() {
					public IdEntity call() throws Exception {
						logstr = "conn=" + connstr + ",sql=" + sqlstr;
						printLogStr(logstr);
						try {
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(sqlstr);
							long minid = Long.MAX_VALUE;
							long maxid = Long.MIN_VALUE;
							while (rs.next()) {
								minid = rs.getInt(1);
								maxid = rs.getInt(2);
							}
							IdEntity e = new IdEntity(nodeid, tablename,
									balancefield, minid, maxid);
							return e;
						} catch (SQLException e) {
							printLogStr(connstr + COMMA + e.getMessage()
									+ NEWLINE);
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
		Iterator iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			IdEntity ret = (IdEntity) val.get();
			if (ret.minid < minid) {
				minid = ret.minid;
			}
			if (ret.maxid > maxid) {
				maxid = ret.maxid;
			}
		}

		Long step_num = 0L;
		if (maxid < minid) {
			System.exit(1);
		} else {
			printLogStr("minid=" + minid + ",maxid=" + maxid);
			minid = minid - (minid % reduce_num) - reduce_num * 100;
			maxid = maxid + (reduce_num - (maxid) % reduce_num) + reduce_num
					* 100;
			printLogStr("minid=" + minid + ",maxid=" + maxid);
			step_num = (maxid - minid) / reduce_num;
			if (step_num <= 0) {
				System.exit(1);
			}
			for (int i = 0; i < reduce_num; i++) {
				logstr = "step " + (i) + " is " + (minid + i * step_num)
						+ " <= x < " + (minid + (i + 1) * step_num);
				printLogStr(logstr);
			}
		}

		/*
		 * 在reduce上建好临时表
		 */

		taskMap = new HashMap<String, Future>();
		for (int i = 0; i < tmp_tablecrtsql_arr.length; i++) {
			final String tmp_tablename = tmp_tablename_arr[i];
			final String sqlstr = tmp_tablecrtsql_arr[i];
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();

							String dropsql = "DROP TABLE IF EXISTS "
									+ tmp_tablename + " ;";
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
		 * 执行map和shuffle
		 */
		String[][] map_file_arr = new String[map_num][reduce_num];
		taskMap = new HashMap<String, Future>();
		for (int i = 0; i < table_arr.length; i++) {
			final String tablename = table_arr[i];
			final String tmp_tablename = tmp_tablename_arr[i];
			final String balancefield = balance_field_arr[i];
			final String tmp_table_fields = tmp_table_fields_arr[i];

			for (int j = 0; j < map_num; j++) {
				for (int k = 0; k < reduce_num; k++) {
					Long step_minid = minid + k * step_num;
					Long step_maxid = minid + (k + 1) * step_num;
					final String sqlstr = base_map_sql
							.replaceAll("<table>", tablename)
							.replaceAll("<balance_field>", balancefield)
							.replaceAll("<step_minid>",
									String.valueOf(step_minid))
							.replaceAll("<step_maxid>",
									String.valueOf(step_maxid));
					final String map_outfile = "/data/" + tablename + "_" + j
							+ "_" + k + ".txt";
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
								Statement reduce_stmt = reduce_conn
										.createStatement();

								logstr = "conn=" + map_connstr + ",sql="
										+ sqlstr;
								printLogStr(logstr);

								ResultSet rs = map_stmt.executeQuery(sqlstr);
								ResultSetMetaData rsmda = rs.getMetaData();
								int cols = rsmda.getColumnCount();

								FileOutputStream fos = new FileOutputStream(
										map_outfile, false);
								OutputStreamWriter loadosw = new OutputStreamWriter(
										fos);
								while (rs.next()) {
									String resultRow = "";
									for (int i = 1; i < cols; i++) {
										resultRow += rs.getString(i) + TAB;
									}
									resultRow += rs.getString(cols);
									loadosw.write(resultRow + NEWLINE);
								}

								loadosw.flush();
								loadosw.close();

								String loadsql = base_load_sql
										.replaceAll("<table>", tmp_tablename)
										.replaceAll("<tmp_table_fields>",
												tmp_table_fields)
										.replaceAll("<map_outfile>",
												map_outfile);
								logstr = "conn=" + reduce_connstr + ",sql="
										+ loadsql;
								printLogStr(logstr);

								boolean ret = reduce_stmt.execute(loadsql);

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

		/*
		 * 在reduce上建好最终表
		 */

		taskMap = new HashMap<String, Future>();

		for (int j = 0; j < reduce_num; j++) {
			final int nodeid = j;
			final String connstr = reduce_connstr_map.get(j);
			final Connection conn = reduce_conn_map.get(j);

			Callable call = new Callable() {
				public Boolean call() throws Exception {

					try {
						Statement stmt = conn.createStatement();
						String dropsql = "DROP TABLE IF EXISTS " + finaltab
								+ " ;";
						logstr = "conn=" + connstr + ",sql=" + dropsql;
						printLogStr(logstr);
						stmt.execute(dropsql);

						logstr = "conn=" + connstr + ",sql="
								+ base_final_crttab_sql;
						printLogStr(logstr);
						boolean ret = stmt.execute(base_final_crttab_sql);
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
			final String sqlstr = base_reduce_sql.replaceAll("<table>",
					finaltab);
			taskMap = new HashMap<String, Future>();
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							Statement stmt = conn.createStatement();
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
		 * 按照顺序读取结果，放到同�?个文�?
		 */

		/*
		 * String base_out_sql = "select * from <table>  ;"; sqlstr =
		 * base_out_sql.replaceAll("<table>", finaltab); taskMap = new
		 * HashMap<String, Future>(); for (int j = 0; j < reduce_num; j++) {
		 * final int nodeid = j; final String connstr =
		 * reduce_connstr_map.get(j); final Connection conn =
		 * reduce_conn_map.get(j);
		 * 
		 * logstr = "conn=" + connstr + ",sql=" + sqlstr; printLogStr(logstr);
		 * 
		 * try { Statement stmt = conn.createStatement(); ResultSet rs =
		 * stmt.executeQuery(sqlstr);
		 * 
		 * ResultSetMetaData rsmda = rs.getMetaData(); int cols =
		 * rsmda.getColumnCount(); while (rs.next()) { String resultRow = "";
		 * for (int i = 1; i < cols; i++) { resultRow += rs.getString(i) + TAB;
		 * } resultRow += rs.getString(cols); resultosw.write(resultRow +
		 * NEWLINE); } } catch (SQLException e) { step_ret = false;
		 * System.exit(1); }
		 * 
		 * }
		 */

		/*
		 * 
		 * 并发读取结果，放到不同文�?
		 */

		{
			String[] reduce_file_arr = new String[reduce_num];
			taskMap = new HashMap<String, Future>();
			final String sqlstr = base_out_sql.replaceAll("<table>", finaltab);
			for (int j = 0; j < reduce_num; j++) {
				final int nodeid = j;
				final String reduce_file = "/data/" + finaltab + "_" + nodeid
						+ ".txt";
				reduce_file_arr[j] = reduce_file;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				Callable call = new Callable() {
					public Boolean call() throws Exception {

						try {
							logstr = "conn=" + connstr + ",sql=" + sqlstr;
							printLogStr(logstr);

							Statement stmt = conn.createStatement();
							ResultSet rs = stmt.executeQuery(sqlstr);

							ResultSetMetaData rsmda = rs.getMetaData();
							int cols = rsmda.getColumnCount();

							FileOutputStream fos = new FileOutputStream(
									reduce_file, false);
							OutputStreamWriter loadosw = new OutputStreamWriter(
									fos);
							while (rs.next()) {
								String resultRow = "";
								for (int i = 1; i < cols; i++) {
									resultRow += rs.getString(i) + TAB;
								}
								resultRow += rs.getString(cols);
								loadosw.write(resultRow + NEWLINE);
							}

							loadosw.flush();
							loadosw.close();

							return true;
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

		exec.shutdown();

		close_Conns(map_conn_map);
		close_Conns(reduce_conn_map);
		logstr = stop_str + step;
		printLogStr(logstr);
		closeLogFile();
		closeResultFile();

	}
}
