package com.jhh.hdb.meta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Utils {

	static String[] test_dates = {};
	static String teststr = null;
	static String[] args = null;

	static DateFormat monthformat = new SimpleDateFormat("yyyy-MM");
	static DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	static DateFormat datetimeformat = new SimpleDateFormat(
			"yyyy-MM-dd_HH:mm:ss");
	// 1 �ո� 2 ���� 3 |
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

	static String[] all_ips = null;

	static String[] ips = null;
	static String loginhostportstr = null;
	// login_ips[0];
	static String hivehostportstr = null;
	static String ddburl = "192.168.12.220:8888?key=src/secret.key";
	// "jdbc:hive://183.136.237.178:10030";
	static Connection loginConn = null;
	static Connection hiveConn = null;
	static Connection ddbConn = null;
	static String localhostportstr = "127.0.0.1:3306";
	static Connection localConn = null;
	static String resulthostportstr = null;
	// "106.3.35.124:3306";
	static Connection resultConn = null;

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

	static String workDir = EMPTY;
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

	static String register_year = EMPTY;

	static String register_month = EMPTY;

	static String register_day = EMPTY;

	static String start_year = EMPTY;

	static String start_month = EMPTY;

	static String start_day = EMPTY;

	static String stop_year = EMPTY;

	static String stop_month = EMPTY;

	static String stop_day = EMPTY;

	static String start_date = EMPTY;

	static String stop_date = EMPTY;

	static String start_time = EMPTY;

	static String stop_time = EMPTY;

	static String start_datetime = EMPTY;

	static String stop_datetime = EMPTY;

	static String action_year = EMPTY;

	static String action_month = EMPTY;

	static String action_day = EMPTY;

	// static String tmp_tablename_userlevel = EMPTY;
	static String tmp_tab_pre = "test.tmp_";
	static String merge_tab_pre = "test.merge_";
	static int date_cnt = 0;

	public Utils() {
		super();
	}

	public static String read_stat(String key) {
		String ret = EMPTY;
		try {
			Properties p = new Properties();
			String user_dir = System.getProperty("user.dir");
			String file = EMPTY;

				file = user_dir + "\\src\\com\\mydb\\common.properties";
			// System.out.println(file);
			InputStream is = null;

			is = new BufferedInputStream(new FileInputStream(file));
			p.load(is);

			if (p.containsKey(key)) {
				ret = p.getProperty(key).replaceAll(D_QUOTE, EMPTY);
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public static String addDay(String action_day, int i) throws ParseException {
		Calendar cal = Calendar.getInstance();
		Date tmpdate = dateformat.parse(action_day);
		cal.setTime(tmpdate);
		cal.add(Calendar.DAY_OF_MONTH, i);
		tmpdate = cal.getTime();
		return dateformat.format(tmpdate);
	}

	public static String addMonth(String action_day, int i)
			throws ParseException {
		Calendar cal = Calendar.getInstance();
		Date tmpdate = dateformat.parse(action_day);
		cal.setTime(tmpdate);
		cal.add(Calendar.MONTH, i);
		tmpdate = cal.getTime();
		return dateformat.format(tmpdate);
	}

	public static int cal_DiffDay(String d1, String d2) throws ParseException {
		Date de = dateformat.parse(d1);
		Date ds = dateformat.parse(d2);

		int ret = (int) ((de.getTime() - ds.getTime()) / 86400000L);
		return ret;
	}

	public static String addHour(String datetime, int i) throws ParseException {
		Calendar cal = Calendar.getInstance();
		Date tmpdate = datetimeformat.parse(datetime);
		cal.setTime(tmpdate);
		cal.add(Calendar.HOUR_OF_DAY, i);
		tmpdate = cal.getTime();
		return datetimeformat.format(tmpdate);
	}

	public static String cal_LastDayOfBeforeMonth(String d)
			throws ParseException {
		Calendar cale = Calendar.getInstance();
		Date tmpdate = dateformat.parse(d);
		cale.setTime(tmpdate);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		tmpdate = cale.getTime();
		String result = dateformat.format(tmpdate);
		return result;
	}

	public static String cal_FirstDayOfBeforeMonth(String d)
			throws ParseException {
		Calendar cale = Calendar.getInstance();
		Date tmpdate = dateformat.parse(d);
		cale.setTime(tmpdate);
		cale.add(Calendar.MONTH, -1);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		tmpdate = cale.getTime();
		String result = dateformat.format(tmpdate);
		return result;
	}

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

	public static void closeTmpFile() throws Exception {
		if (tmposw != null) {
			tmposw.close();
		}
	}

	public static void initDir(String dir) {
		File b = new File(dir);
		if (b.exists() == false) {
			b.mkdirs();
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

	public static void openTmpFile() throws Exception {
		if (tmposw != null) {
			tmposw.close();
		}
		FileOutputStream fos = new FileOutputStream(tmpfile, true);
		tmposw = new OutputStreamWriter(fos, "UTF8");
	}

	public static String reg_str(String s) {
		return s.replace("$", "\\$").replace("{", "\\{").replace("}", "\\}");
	}

	public static void printLogStr(String s) {
		String r = datetimeformat.format(new Date()) + TAB + s;
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

	public static String repfile(String f) {
		if (f != null) {
			// return f.replaceAll("\\\\", "fxg").replaceAll(".", "dian")
			// .replaceAll("\\?", "wh").replaceAll("\t", "tab")
			// .replaceAll("<", "xyh").replaceAll(">", "dyh")
			// .replaceAll("\"", "syh").replaceAll("|", "sx")
			// .replaceAll("\\*", "xh").replaceAll(":", "mh")
			// .replaceAll(" ", "kg").replaceAll("\n", "gn")
			// .replaceAll("\r", "gr");
			return f.replaceAll(" ", "kg");
		} else {
			return EMPTY;
		}
	}

	public static String repfilename(String f) {
		if (f != null) {
			return f.replaceAll("\t", "gt").replaceAll(" ", "kg")
					.replaceAll("\\[", "lz").replaceAll("\\]", "rz")
					.replaceAll("\n", "gn").replaceAll("\r", "gr");
		} else {
			return EMPTY;
		}
	}

	public static String repstr(String f) {
		if (f != null) {
			return f.replaceAll("\\s", EMPTY);
		} else {
			return EMPTY;
		}
	}

	public static boolean steps_contain(String a) throws Exception {

		for (int i = 0; i < steps.length; i++) {
			if (a.equals(steps[i].trim())) {
				step = a;
				return true;
			}
		}
		step = EMPTY;
		return false;
	}

	public static void close_Conns() throws Exception {
		int conns_len = ips.length;
		for (int i = 0; i < conns_len; i++) {
			if (conns[i] != null) {
				conns[i].close();
				printLogStr(ips[i] + " connection closed !");
			}
		}
	}

	public static void close_LocalConn() throws Exception {
		if (localConn != null) {
			localConn.close();
			printLogStr(localhostportstr + " connection closed !");
		}
	}

	public static void close_LoginConn() throws Exception {
		if (loginConn != null) {
			loginConn.close();
			printLogStr(loginhostportstr + " connection closed !");
		}
	}

	public static void close_HiveConn() throws Exception {
		if (hiveConn != null) {
			hiveConn.close();
			printLogStr(hivehostportstr + " connection closed !");
		}
	}

	public static void close_ResultConn() throws Exception {
		if (resultConn != null) {
			resultConn.close();
			printLogStr(resulthostportstr + " connection closed !");
		}
	}

	public static void create_merge_table(String tablename, String lastdate,
			int day_cnt) throws Exception {

		logstr = "start create_merge_table";
		printLogStr(logstr);

		String start_date = addDay(lastdate, 0 - day_cnt);
		String end_date = lastdate;
		logstr = start_date + WAVY + end_date;
		printLogStr(logstr);

		String template_date = lastdate;

		for (int i = 0; i < 25; i++) {

			String template_yearmonth = template_date.substring(0, 7);
			String template_day = template_date.substring(8, 10);

			String retsql = " show create table `par_table_par_yearmonth`.`par_day` ;";
			retsql = retsql.replaceAll("par_table", tablename)
					.replaceAll("par_yearmonth", template_yearmonth)
					.replaceAll("par_day", template_day);

			String sql = EMPTY;
			logstr = "conn=" + loginhostportstr + ",sql=" + retsql;
			printLogStr(logstr);

			try {
				stmt = loginConn.createStatement();
				rs = stmt.executeQuery(retsql);

				if (rs.next()) {
					sql = rs.getString(2);
				}
			} catch (SQLException e) {
				template_date = addDay(template_date, -15);
				printLogStr(loginhostportstr + COMMA + e.getMessage() + NEWLINE);
				continue;
			}
			printLogStr(sql + SEMI);

			tmpstr = "CREATE TABLE IF NOT EXISTS " + merge_tab_pre + tablename;
			sql = sql.replaceAll("CREATE TABLE `" + template_day + "`", tmpstr);
			sql = sql.replaceAll("ENGINE=[^ ]+ ", "ENGINE=MRG_MYISAM ");

			String s = EMPTY;
			for (int n = 0; n < day_cnt; n++) {
				tmpstr = addDay(end_date, 0 - n);

				s += "`" + tablename + UNDERLINE + tmpstr.substring(0, 4) + "-"
						+ tmpstr.substring(5, 7) + "`.`"
						+ tmpstr.substring(8, 10) + "`" + COMMA;
			}
			s = s.substring(0, s.length() - 1);

			sql = sql + " INSERT_METHOD=LAST UNION=(" + s + ") ;";

			for (int n2 = 0; n2 < conns.length; n2++) {
				logstr = "conn=" + ips[n2] + ",sql=" + sql;
				printLogStr(logstr);
				conn = conns[n2];
				stmt = conn.createStatement();
				stmt.execute(sql);
			}

			break;
		}

		logstr = "stop create_merge_table";
		printLogStr(logstr);

	}

	public static void create_login_tmp_table(String t) throws SQLException {
		tmpstr = "start create_login_tmp_table";
		printLogStr(tmpstr);

		String sql = EMPTY;
		if (t.matches("test.tmp_userid.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		}
		if (t.matches("test.tmp_beforeuid.*")
				|| t.matches("test.tmp_afteruid.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		}
		if (t.matches("test.tmp_useronedata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned , `result_value` int(11) , KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_usertwodata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned , `value1` int(11) , `value2` int(11) , KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_userstr2data.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned , `value1` varchar(128) , `value2` varchar(128) , KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_userstr4data.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned , `value1` varchar(128) , `value2` varchar(128)  , `value3` varchar(128) , `value4` varchar(128), KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_userstr8data.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned , `value1` varchar(128) , `value2` varchar(128)  , `value3` varchar(128) , `value4` varchar(128), `value5` varchar(128) , `value6` varchar(128)  , `value7` varchar(128) , `value8` varchar(128), KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_user_comsumeuserlevel.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `ad_id` int(11)  , `action_day` varchar(32)  , `register_day` varchar(32)  , `zoneId` int(11) DEFAULT '-1' , `iscomsumer` int(11) , `iuin` int(11) unsigned , `result_value` int(11)  , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";
		}

		if ("test.tmp_m3gcn_lostuserlevel".equalsIgnoreCase(t)) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , `result_value` int(11)  , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";

		}
		if ("test.tmp_m3gcn_userlevel_bytimerange".equalsIgnoreCase(t)) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , `result_value` int(11)  , KEY idx1(`iuin`)) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if ("test.tmp_m3gcn_userlevel_byuid".equalsIgnoreCase(t)) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , `result_value` int(11)  , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}

		if ("test.tmp_m3gcn_money_byuid".equalsIgnoreCase(t)) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";

		}
		if ("test.tmp_user_login_count".equalsIgnoreCase(t)) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , `login_count` int(11) unsigned , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if ("test.tmp_user_logintimeandscore".equalsIgnoreCase(t)) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , `login_time` int(11) unsigned ,`score` int(11) unsigned , KEY idx1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_corp_topdata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `corpid` int(11) unsigned , `src_iuin` int(11) unsigned , `dest_iuin` int(11) unsigned ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_corp_onedata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `corpid` int(11) unsigned , `value` int(11) unsigned , KEY idx1(`corpid`)) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if ("test.tmp_uidscore".equalsIgnoreCase(t)) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , `result_value` int(11)  ,KEY idx1(`iuin`)) ENGINE=MyISAM DEFAULT CHARSET=utf8;";
		}

		logstr = "conn=" + loginhostportstr + ",sql=" + sql;
		printLogStr(logstr);
		Statement crtstmt = loginConn.createStatement();
		crtstmt.execute(sql);
		tmpstr = "stop create_login_tmp_table";
		printLogStr(tmpstr);
	}

	public static void create_tmp_table(String t) throws SQLException {
		tmpstr = "start create_tmp_table";
		printLogStr(tmpstr);
		String sql = EMPTY;
		if (t.matches("test.tmp_userid.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , PRIMARY KEY (`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";

		}
		if (t.matches("test.tmp_userdata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `iuin` int(11) unsigned , `result_value` int(11)  , KEY key1(`iuin`) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";

		}
		if (t.matches("test.tmp_useronedata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned , `result_value` int(11) , KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_usertwodata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned , `value1` int(11) , `value2` int(11) , KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_userthreedata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned ,  `value1` int(11) , `value2` int(11) ,`value3` int(11) , KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_userfourdata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " (`iuin` int(11) unsigned ,  `value1` int(11) , `value2` int(11) ,`value3` int(11) , `value4` int(11) , KEY idx1(`iuin`)  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_corp_onedata.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `corpid` int(11) unsigned , `value` int(11) unsigned , KEY idx1(`value`)) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		if (t.matches("test.tmp_ad_pid.*")) {
			sql = " CREATE TABLE if not exists "
					+ t
					+ " ( `ad_id` int(11) , `p_id` bigint(20) ) ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		}
		for (int i = 0; i < conns.length; i++) {
			logstr = "conn=" + ips[i] + ",sql=" + sql;
			printLogStr(logstr);
			conn = conns[i];
			stmt = conn.createStatement();
			stmt.execute(sql);
		}
		tmpstr = "stop create_tmp_table";
		printLogStr(tmpstr);
	}

	public static void drop_login_merge_table(String tn) throws Exception {

		tmpstr = "start drop_login_merge_table";
		printLogStr(tmpstr);

		String sql = "DROP TABLE IF EXISTS test.merge_" + tn + " ;";
		tmpstr = "conn=" + loginhostportstr + ",sql=" + sql;
		printLogStr(tmpstr);

		Statement stmt = loginConn.createStatement();
		stmt.execute(sql);

		tmpstr = "stop drop_login_merge_table";
		printLogStr(tmpstr);

	}

	public static void create_login_merge_table(String tablename,
			String lastdate, int day_cnt) throws Exception {

		tmpstr = "start create_login_merge_table";
		printLogStr(tmpstr);

		String start_date = addDay(lastdate, 0 - day_cnt);
		String end_date = lastdate;
		tmpstr = start_date + WAVY + end_date;
		printLogStr(tmpstr);

		String template_date = lastdate;

		for (int i = 0; i < 25; i++) {

			String template_yearmonth = template_date.substring(0, 7);
			String template_day = template_date.substring(8, 10);

			String retsql = " show create table `par_table_par_yearmonth`.`par_day` ;";
			retsql = retsql.replaceAll("par_table", tablename)
					.replaceAll("par_yearmonth", template_yearmonth)
					.replaceAll("par_day", template_day);

			String sql = EMPTY;
			tmpstr = "conn=" + loginhostportstr + ",sql=" + retsql;
			printLogStr(tmpstr);

			try {
				Statement stmt = loginConn.createStatement();
				ResultSet rs = stmt.executeQuery(retsql);

				if (rs.next()) {
					sql = rs.getString(2);
				}
			} catch (SQLException e) {
				template_date = addDay(template_date, -15);
				printLogStr(loginhostportstr + COMMA + e.getMessage() + NEWLINE);
				continue;
			}
			printLogStr(sql + SEMI);

			tmpstr = "CREATE TABLE IF NOT EXISTS " + merge_tab_pre + tablename;
			sql = sql.replaceAll("CREATE TABLE `" + template_day + "`", tmpstr);
			sql = sql.replaceAll("ENGINE=[^ ]+ ", "ENGINE=MRG_MYISAM ");

			String s = EMPTY;
			for (int n = 0; n < day_cnt; n++) {
				tmpstr = addDay(end_date, 0 - n);

				s += "`" + tablename + UNDERLINE + tmpstr.substring(0, 4) + "-"
						+ tmpstr.substring(5, 7) + "`.`"
						+ tmpstr.substring(8, 10) + "`" + COMMA;
			}
			s = s.substring(0, s.length() - 1);

			sql = sql + " INSERT_METHOD=LAST UNION=(" + s + ") ;";

			tmpstr = "conn=" + loginhostportstr + ",sql=" + sql;
			printLogStr(tmpstr);

			stmt = loginConn.createStatement();
			stmt.execute(sql);

			break;

		}

		tmpstr = "stop create_login_merge_table";
		printLogStr(tmpstr);

	}

	public static void drop_tmp_table(String tn) throws Exception {

		logstr = "start drop_tmp_table";
		printLogStr(logstr);

		String sql = "DROP TABLE IF EXISTS " + tn + ";";

		for (int n2 = 0; n2 < conns.length; n2++) {
			logstr = "conn=" + ips[n2] + ",sql=" + sql;
			printLogStr(logstr);
			Connection conn = conns[n2];
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		}

		logstr = "stop drop_tmp_table";
		printLogStr(logstr);

	}

	public static void drop_merge_table(String tn) throws Exception {

		logstr = "start drop_merge_table";
		printLogStr(logstr);

		String sql = "DROP TABLE IF EXISTS test.merge_" + tn + " ;";

		for (int n2 = 0; n2 < conns.length; n2++) {
			logstr = "conn=" + ips[n2] + ",sql=" + sql;
			printLogStr(logstr);
			Connection conn = conns[n2];
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		}

		logstr = "stop drop_merge_table";
		printLogStr(logstr);

	}

	public static void drop_login_tmp_table(String tn) throws SQLException {
		String sql = " drop table if exists " + tn + " ;";
		logstr = "conn=" + loginhostportstr + ",sql=" + sql;
		printLogStr(logstr);
		Statement trustmt = loginConn.createStatement();
		trustmt.execute(sql);
	}

	public static void open_LocalConn() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + localhostportstr;
		localConn = DriverManager.getConnection(url, user, pass);
		printLogStr(localhostportstr + " connection opened !");
	}

	public static void open_LoginConn() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + loginhostportstr;
		loginConn = DriverManager.getConnection(url, user, pass);
		printLogStr(loginhostportstr + " connection opened !");
	}

	public static void open_HiveConn() throws Exception {
		Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
		String url = hivehostportstr;
		hiveConn = DriverManager.getConnection(url, user, pass);
		printLogStr(hivehostportstr + " connection opened !");

		stmt = hiveConn.createStatement();
		stmt.execute("set mapred.job.queue.name=hive");
	}

	public static void open_DdbConn() throws Exception {
		Class.forName("com.netease.backend.db.DBDriver");
		ddbConn = DriverManager.getConnection(ddburl, user, pass);
		printLogStr(ddburl + " connection opened !");

	}

	public static void close_DdbConn() throws Exception {
		if (ddbConn != null) {
			ddbConn.close();
			printLogStr(ddburl + " connection closed !");
		}
	}

	public static void open_ResultConn() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + resulthostportstr;
		resultConn = DriverManager.getConnection(url, user, pass);
		printLogStr(resulthostportstr + " connection opened !");
	}

	public static void open_Conns() throws Exception {
		int conns_len = ips.length;
		conns = new Connection[conns_len];
		for (int i = 0; i < conns_len; i++) {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + ips[i];
			conns[i] = DriverManager.getConnection(url, user, pass);
			printLogStr(ips[i] + " connection opened !");
		}
	}

	public static void renameDirFile(String dir) throws Exception {
		File parentF = new File(dir);
		File fs[] = parentF.listFiles();
		for (int i = 0; i < fs.length; i++) {
			File f = fs[i];
			String fn = f.getName();
			String nfn = dir + repfilename(fn);
			File nf = new File(nfn);
			boolean r = f.renameTo(nf);
			System.out.println(fn + " " + nfn + " " + r);
		}
	}

	public static String getStrEncoding(String str) {

		String[] encodes = new String[] { "GB2312", "ISO-8859-1", "UTF-8",
				"GBK", "BIG5" };
		for (int i = 0; i < encodes.length; i++) {
			String encode = encodes[i];
			try {
				if (str.equals(new String(str.getBytes(encode), encode))) {
					return encode;
				}
			} catch (Exception exception) {
			}
		}
		return EMPTY;
	}

	public static void do_sql() throws Exception {
		logfile = workDir + "do_sql.log";
		openLogFile();

		logstr = "start do_sql";
		printLogStr(logstr);

		// ips = all_ips;
		// open_Conns();

		open_LoginConn();

		String stats_stopdate = "2013-12-29";
		date_cnt = 6;
		String merge_tab_login = "tab_login";
		drop_login_merge_table(merge_tab_login);
		create_login_merge_table(merge_tab_login, stats_stopdate, date_cnt);

		String sql = EMPTY;

		// sql =
		// " CREATE TABLE if not exists test.tab_userinfo (`ip` varchar(32) , `iuin` int(10) unsigned NOT NULL, `logtime` varchar(32) , par4 varchar(32) , par5 varchar(32) ,key idx_iuin(iuin) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";
		// sql =
		// " CREATE TABLE if not exists test.tmp_x1 ( `cardid` int(10) unsigned ,key idx1(cardid) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";
		// sql =
		// " CREATE TABLE if not exists test.tmp_x2 ( `iuin` int(10) unsigned ,key idx1(iuin) ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;";
		// logstr = "conn=" + loginhostportstr + ",sql=" + sql;
		// printLogStr(logstr);
		// stmt = loginConn.createStatement();
		// stmt.execute(sql);

		// sql =
		// "LOAD DATA LOCAL INFILE 'D:/temp/newusercard.txt' REPLACE INTO TABLE test.tmp_x1;";
		// sql =
		// "LOAD DATA LOCAL INFILE 'D:/temp/aa.txt' REPLACE INTO TABLE test.tmp_x2;";
		// logstr = "conn=" + loginhostportstr + ",sql=" + sql;
		// printLogStr(logstr);
		// stmt = loginConn.createStatement();
		// stmt.execute(sql);

		/*
		 * String sql1 =
		 * " CREATE TABLE if not exists test.tab_logined_uid ( `iuin` int(10) unsigned NOT NULL ) ENGINE=MyISAM DEFAULT CHARSET=utf8 ;"
		 * ; sql = sql1; for (int n2 = 0; n2 < conns.length; n2++) { logstr =
		 * "conn=" + ips[n2] + ",sql=" + sql; printLogStr(logstr); Connection
		 * conn = conns[n2]; Statement stmt = conn.createStatement(); boolean
		 * ret = stmt.execute(sql); if (ret) { logstr = "conn=" + ips[n2] +
		 * "====success===="; printLogStr(logstr); } }
		 * 
		 * String sql0 = " truncate table test.tab_logined_uid ;"; sql = sql0;
		 * for (int n2 = 0; n2 < conns.length; n2++) { logstr = "conn=" +
		 * ips[n2] + ",sql=" + sql; printLogStr(logstr); Connection conn =
		 * conns[n2]; Statement stmt = conn.createStatement(); boolean ret =
		 * stmt.execute(sql); if (ret) { logstr = "conn=" + ips[n2] +
		 * "====success===="; printLogStr(logstr); } }
		 * 
		 * String sql2 =
		 * "LOAD DATA LOCAL INFILE 'D:/temp/um.txt' REPLACE INTO TABLE test.tab_logined_uid;"
		 * ; sql = sql2; for (int n2 = 0; n2 < conns.length; n2++) { logstr =
		 * "conn=" + ips[n2] + ",sql=" + sql; printLogStr(logstr); Connection
		 * conn = conns[n2]; Statement stmt = conn.createStatement(); boolean
		 * ret = stmt.execute(sql); if (ret) { logstr = "conn=" + ips[n2] +
		 * "====success===="; printLogStr(logstr); } }
		 */

		// close_Conns();

		close_LoginConn();

		logstr = "stop do_sql";
		printLogStr(logstr);

		closeLogFile();
	}

	public static void do_ddbsql() throws Exception {
		logfile = workDir + "do_ddbsql.log";
		openLogFile();

		logstr = "start do_ddbsql";
		printLogStr(logstr);
		open_DdbConn();

		String sql = "select iuin,dtlogtime from tab_login where dtlogtime >='2014-09-02 00:00:00' and dtlogtime <'2014-09-02 00:05:00' limit 10";
		Statement stmt = ddbConn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			System.out.println(rs.getInt(1) + "\t" + rs.getString(2));
		}

		close_DdbConn();

		logstr = "stop do_ddbsql";
		printLogStr(logstr);

		closeLogFile();
	}

	public static void do_spark_jdbc() throws Exception {
		logfile = workDir + "do_spark_jdbc.log";
		openLogFile();

		logstr = "start do_spark_jdbc";
		printLogStr(logstr);
		Class.forName("org.apache.hive.jdbc.HiveDriver");
		String url = "jdbc:hive2://192.168.12.220:10000";
		conn = DriverManager.getConnection(url, "", "");
		printLogStr(url + " connection opened !");
		String sql;
		Statement stmt = conn.createStatement();

		sql = "create temporary function userlevel as 'com.udf.UserLevel' ";
		stmt.execute(sql);
		sql = "select playerid , score , userlevel(score) ul ,recordtime from m3gcn.tab_town_leave where par_dt>='20141001' and par_dt<='20141010' limit 10 ";
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			System.out.println(rs.getInt(1) + "\t" + rs.getInt(2) + "\t"
					+ rs.getInt(3) + "\t" + rs.getString(4));

		}

		rs.close();
		stmt.close();
		conn.close();

		logstr = "stop do_ddbsql";
		printLogStr(logstr);

		closeLogFile();
	}

	public static String long2str(long t) {

		char[] result = new char[4];
		result[0] = (char) ((t >> 24) & 0xFF);
		result[1] = (char) ((t >> 16) & 0xFF);
		result[2] = (char) ((t >> 8) & 0xFF);
		result[3] = (char) (t & 0xFF);
		String v = String.valueOf(result);
		return v;
	}

	public static long str2long(byte[] bytes) {

		long value = 0;
		// 由高位到低位
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// �??高位�??
		}
		return value;
	}

	public static String listToString(List<String> stringList) {

		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append(COMMA);
			} else {
				flag = true;
			}
			result.append(string);
		}
		return result.toString();

	}

}