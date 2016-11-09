package com.jhh.hdb.meta;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

public class MydbTest {

	ExecutorService pool = Executors.newCachedThreadPool();
	Map<Integer, DbEntry> dbid_map = new HashMap<Integer, DbEntry>();
	Map<String, DbEntry> dbname_map = new HashMap<String, DbEntry>();

	Map<Integer, DbEntry> tmpdbid_map = new HashMap<Integer, DbEntry>();
	Map<String, DbEntry> tmpdbname_map = new HashMap<String, DbEntry>();

	// 系统中有哪些�?
	Map<Integer, String> mydb_map = new HashMap<Integer, String>();

	Map<Integer, TableEntry> tabid_map = new HashMap<Integer, TableEntry>();
	Map<String, TableEntry> tabname_map = new HashMap<String, TableEntry>();

	Map<Integer, BucketEntry> bucketid_map = new HashMap<Integer, BucketEntry>();
	Map<String, List<BucketEntry>> policy2bucket_map = new HashMap<String, List<BucketEntry>>();

	private Connection metaConn = null;
	String metaUrl = "jdbc:mysql://192.168.0.213:3306/test";

	public MydbTest() {

	}

	public void init_metaconn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			metaConn = DriverManager.getConnection(metaUrl, Utils.user,
					Utils.pass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void runSql(String sql_str) throws Exception {

		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		StringReader stringReader = new StringReader(sql_str);
		Select s = (Select) parserManager.parse(stringReader);

		doSelectBody(s.getSelectBody());

	}

	public void doSelectBody(SelectBody sb) throws Exception {

		if (sb instanceof PlainSelect) {
			PlainSelect ps = (PlainSelect) sb;
			doPlainSelect(ps);
		}

		if (sb instanceof Union) {
			Union u = (Union) sb;

			for (int i = 0; i < u.getPlainSelects().size(); i++) {
				PlainSelect ps = (PlainSelect) u.getPlainSelects().get(i);
				doPlainSelect(ps);
			}
		}

	}
	// 是否夸节�?
	@SuppressWarnings("rawtypes")
	public void isInDbJoin(PlainSelect ps) throws Exception {
		
	}
	@SuppressWarnings("rawtypes")
	public void doPlainSelect(PlainSelect ps) throws Exception {

		FromItem fi = ps.getFromItem();
		// Table intotab = ps.getInto();
		List sis = ps.getSelectItems();
		Expression whereexpr = ps.getWhere();
		List joins = ps.getJoins();
		List orderbys = ps.getOrderByElements();
		List groupbys = ps.getGroupByColumnReferences();
		Limit limit = ps.getLimit();
		Distinct distinct = ps.getDistinct();
		Expression havingexpr = ps.getHaving();

		// FromItem
		if (fi instanceof Table) {
			Table tab = (Table) fi;
			System.out.println(tab.toString());
			String bf = tabname_map.get(tab.getName()).balancefield;
			String policy = tabname_map.get(tab.getName()).policyname;
			
			Map<Integer, Connection> conn_map = policy2conn_map(policy);
			
			doJoins(joins);
		} else if (fi instanceof SubSelect) {
			SubSelect ss = (SubSelect) fi;
			SelectBody sb = ss.getSelectBody();
			doSelectBody(sb);

			doJoins(joins);

		} else if (fi instanceof SubJoin) {
			SubJoin sj = (SubJoin) fi;
			FromItem left_fi = sj.getLeft();
			parseFromItem(left_fi);
			Join j = sj.getJoin();
			FromItem right_fi = j.getRightItem();
			parseFromItem(right_fi);

			doJoins(joins);
		} else {

		}

		// getSelectItems
		for (Object object : sis) {
			SelectItem si = (SelectItem) object;

			if (si instanceof AllColumns) {

			} else if (si instanceof AllTableColumns) {

			} else if (si instanceof SelectExpressionItem) {

				System.out.println(si.toString());
			} else {

			}
		}

		// whereexpr
		if (whereexpr != null) {
			ExpressionDeParser whereexpr_edp = new ExpressionDeParser();
			StringBuffer whereexpr_sb = new StringBuffer();
			whereexpr_edp.setBuffer(whereexpr_sb);
			whereexpr.accept(whereexpr_edp);
			System.out.println(whereexpr_sb.toString());
		}
		// limit
		if (limit != null) {
			System.out.println(limit.toString());
		}
		// distinct
		if (distinct != null) {
			System.out.println(distinct.toString());
		}
		// havingexpr
		if (havingexpr != null) {
			ExpressionDeParser having_edp = new ExpressionDeParser();
			StringBuffer having_sb = new StringBuffer();
			having_edp.setBuffer(having_sb);
			whereexpr.accept(having_edp);
			System.out.println(having_sb.toString());
		}

		// orderbys OrderByElement
		if (orderbys != null && orderbys.size() > 0) {
			for (Object object : orderbys) {
				OrderByElement o = (OrderByElement) object;
				System.out.println(o.toString());
			}
		}

		// groupbys Expression
		if (groupbys != null && groupbys.size() > 0) {
			for (Object object : groupbys) {
				Expression o = (Expression) object;
				System.out.println(o.toString());
			}
		}
	}

	private Map<Integer, Connection> policy2conn_map(String policy) {
		List<BucketEntry> bl = policy2bucket_map.get(policy);
		Map<Integer, Connection> conn_map = new HashMap<Integer, Connection>();
		for (Iterator iterator = bl.iterator(); iterator.hasNext();) {
			BucketEntry bucketEntry = (BucketEntry) iterator.next();
			
			Connection conn = data_conn_map.get(bucketEntry.db_id);
			conn_map.put(bucketEntry.db_id, conn);
		}
		
		return conn_map;
	}

	private void doJoins(List joins) throws Exception {
		if (joins != null && joins.size() > 0) {
			for (Iterator iterator = joins.iterator(); iterator.hasNext();) {
				Join j = (Join) iterator.next();
				FromItem right_fi = j.getRightItem();
				parseFromItem(right_fi);
				Expression e = j.getOnExpression();
				System.out.println(e.toString());
				System.out.println(j.toString());
			}
		}

	}

	private void parseFromItem(FromItem fi) throws Exception {
		if (fi instanceof Table) {
			Table tab = (Table) fi;
			System.out.println(tab.toString());
		} else if (fi instanceof SubSelect) {
			SubSelect ss = (SubSelect) fi;
			SelectBody sb = ss.getSelectBody();
			doSelectBody(sb);
		} else if (fi instanceof SubJoin) {
			SubJoin sj = (SubJoin) fi;
			FromItem left_fi = sj.getLeft();
			parseFromItem(left_fi);
		} else {

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			MydbTest d = new MydbTest();

			d.init_metaconn();
			d.get_meta_info();

			String sql_str = StringTemplateUtils.read_stat("q2");
			d.runSql(sql_str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String data_ip_str = Utils.read_stat("data_ip_str");
	Map<Integer, String> data_connstr_map = new HashMap<Integer, String>();
	Map<Integer, Connection> data_conn_map = new HashMap<Integer, Connection>();
	int data_conns_len = 0;

	String tmp_ip_str = Utils.read_stat("tmp_ip_str");
	Map<Integer, String> tmp_connstr_map = new HashMap<Integer, String>();
	Map<Integer, Connection> tmp_conn_map = new HashMap<Integer, Connection>();
	int tmp_conns_len = 0;

	Long min_id = Long.MAX_VALUE;
	Long max_id = Long.MIN_VALUE;
	Map<Integer, Long> min_map = new HashMap<Integer, Long>();
	Map<Integer, Long> max_map = new HashMap<Integer, Long>();
	String sql_str = Utils.EMPTY;

	String[] tab_name_arr = new String[] { "user_info", "user_info_etc" };
	String[] tab_field_arr = new String[] { "id", "USER_ID" };
	String[] tab_condition_arr = new String[] { "GMT_CREATE<'2012-09-09'",
			"BIRTHDAY<'1990-01-01'" };

	// public void init_conn() throws Exception {
	//
	// String[] data_ip_arr = data_ip_str.split(" +");
	// data_conns_len = data_ip_arr.length;
	//
	// for (int i = 0; i < data_conns_len; i++) {
	// String[] par_arr = data_ip_arr[i].split(",");
	// Integer idx = Integer.valueOf(par_arr[0]);
	// String url = par_arr[1];
	// Utils.printLogStr("opening conn for " + idx + " " + url);
	// Connection conn = DriverManager.getConnection(url, Utils.user,
	// Utils.pass);
	// data_connstr_map.put(idx, url);
	// data_conn_map.put(idx, conn);
	// }
	//
	// String[] tmp_ip_arr = tmp_ip_str.split(" +");
	// tmp_conns_len = tmp_ip_arr.length;
	//
	// for (int i = 0; i < tmp_conns_len; i++) {
	// String[] par_arr = tmp_ip_arr[i].split(",");
	// Integer idx = Integer.valueOf(par_arr[0]);
	// String url = par_arr[1];
	// Utils.printLogStr("opening conn for " + idx + " " + url);
	// Connection conn = DriverManager.getConnection(url, Utils.user,
	// Utils.pass);
	// tmp_connstr_map.put(idx, url);
	// tmp_conn_map.put(idx, conn);
	// }
	// }

	public void init_dataconn() throws Exception {

		Iterator<Integer> it = dbid_map.keySet().iterator();
		while (it.hasNext()) {

			Integer key = it.next();
			DbEntry dbentry = dbid_map.get(key);
			String url = "jdbc:mysql://" + dbentry.db_ip + ":"
					+ dbentry.db_port + "/" + dbentry.db_name;
			Utils.printLogStr("opening conn for data " + key + " " + url);
			Connection conn = DriverManager.getConnection(url, Utils.user,
					Utils.pass);
			data_connstr_map.put(key, url);
			data_conn_map.put(key, conn);
		}

		data_conns_len = dbid_map.size();

	}

	public void init_tmpconn() throws Exception {

		Iterator<Integer> it = tmpdbid_map.keySet().iterator();
		while (it.hasNext()) {

			Integer key = it.next();
			DbEntry dbentry = tmpdbid_map.get(key);
			String url = "jdbc:mysql://" + dbentry.db_ip + ":"
					+ dbentry.db_port + "/" + dbentry.db_name;
			Utils.printLogStr("opening conn for tmp " + key + " " + url);
			Connection conn = DriverManager.getConnection(url, Utils.user,
					Utils.pass);
			tmp_connstr_map.put(key, url);
			tmp_conn_map.put(key, conn);
		}

		tmp_conns_len = tmpdbid_map.size();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init_id() throws Exception {

		String minmaxid_sql_str = "select min(<tab_field>), max(<tab_field>) from <tab>  where <condition> ";

		for (int i = 0; i < tab_name_arr.length; i++) {

			String tab_name = tab_name_arr[i];
			String tab_field = tab_field_arr[i];
			String tab_condition = tab_condition_arr[i];

			sql_str = minmaxid_sql_str.replaceAll("<tab_field>", tab_field)
					.replaceAll("<tab_name>", tab_name)
					.replaceAll("<condition>", tab_condition);

			Set<Future<Integer>> set = new HashSet<Future<Integer>>();
			IdGetter_Input in = new IdGetter_Input(sql_str);
			Callable callable = new IdGetter(in);
			Future future = pool.submit(callable);

			set.add(future);

			for (Future f : set) {
				try {
					IdGetter_Output out = (IdGetter_Output) f.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init_tmpfile() throws Exception {

		String minmaxid_sql_str = "select min(<tab_field>), max(<tab_field>) from <tab>  where <condition> ";

		for (int i = 0; i < tab_name_arr.length; i++) {

			String tab_name = tab_name_arr[i];
			String tab_field = tab_field_arr[i];
			String tab_condition = tab_condition_arr[i];

			sql_str = minmaxid_sql_str.replaceAll("<tab_field>", tab_field)
					.replaceAll("<tab_name>", tab_name)
					.replaceAll("<condition>", tab_condition);

			Set<Future<Integer>> set = new HashSet<Future<Integer>>();
			IdGetter_Input in = new IdGetter_Input(sql_str);
			Callable callable = new IdGetter(in);
			Future future = pool.submit(callable);

			set.add(future);

			for (Future f : set) {
				try {
					IdGetter_Output out = (IdGetter_Output) f.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void test_runsql() throws Exception {

		for (int i = 0; i < tab_name_arr.length; i++) {
			String tab_name = tab_name_arr[i];
			String tab_field = tab_field_arr[i];
			String tab_condition = tab_condition_arr[i];

			String min_sql_str = "select min(<tab_field>) from <tab>";
			String max_sql_str = "select max(<tab_field>) from <tab>";

			Iterator<Integer> it = data_conn_map.keySet().iterator();
			while (it.hasNext()) {

				Integer key = it.next();
				String url = data_connstr_map.get(key);
				Connection conn = data_conn_map.get(key);

				Statement stmt = conn.createStatement();
				sql_str = min_sql_str.replaceAll("<tab_field>", tab_field)
						.replaceAll("<tab>", tab_name);
				Utils.printLogStr(url + Utils.TAB + sql_str);
				ResultSet min_rs = stmt.executeQuery(sql_str);
				while (min_rs.next()) {
					min_map.put(key, min_rs.getLong(1));
				}
				sql_str = max_sql_str.replaceAll("<tab_field>", tab_field)
						.replaceAll("<tab>", tab_name);
				Utils.printLogStr(url + Utils.TAB + sql_str);
				ResultSet max_rs = stmt.executeQuery(sql_str);
				while (max_rs.next()) {
					max_map.put(key, max_rs.getLong(1));
				}
				stmt.close();
			}

			it = data_conn_map.keySet().iterator();
			while (it.hasNext()) {

				Integer key = it.next();
				Long min = min_map.get(key);
				Long max = max_map.get(key);

				if (min < min_id) {
					min_id = min;
				}
				if (max > max_id) {
					max_id = max;
				}
			}
			Utils.printLogStr(" min max is " + Utils.TAB + min_id + Utils.TAB
					+ max_id);

			int part_num = tmp_connstr_map.size();
			max_id = max_id + (part_num - max_id % part_num);
			min_id = min_id - min_id % part_num;
			Long interval = (max_id - min_id) / part_num;
			Utils.printLogStr(" min max part_num interval is " + Utils.TAB
					+ min_id + Utils.TAB + max_id + Utils.TAB + part_num
					+ Utils.TAB + interval);

			Map<Integer, String> tmp_fn_map = new HashMap<Integer, String>();
			Map<Integer, OutputStreamWriter> tmp_osw_map = new HashMap<Integer, OutputStreamWriter>();
			for (int j = 0; j < part_num; j++) {
				String tab_fn = "/data/" + tab_name + Utils.UNDERLINE + j
						+ ".txt";
				FileOutputStream fos = new FileOutputStream(tab_fn, false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
				tmp_fn_map.put(j, tab_fn);
				tmp_osw_map.put(j, osw);
			}

			String select_sql = "select * from <tab> where <condition>  ";
			String drop_sql = null;
			String create_sql = null;
			String drop_table = StringTemplateUtils.read_stat("drop_table");
			String create_table = StringTemplateUtils.read_stat("create_table");
			String tmp_tab_name = null;
			String col_def_str = Utils.EMPTY;
			it = data_conn_map.keySet().iterator();
			while (it.hasNext()) {

				Integer key = it.next();
				String url = data_connstr_map.get(key);
				Connection conn = data_conn_map.get(key);

				Statement stmt = conn.createStatement();
				sql_str = select_sql.replaceAll("<condition>", tab_condition)
						.replaceAll("<tab>", tab_name);
				Utils.printLogStr(url + Utils.TAB + sql_str);
				ResultSet rs = stmt.executeQuery(sql_str);

				ResultSetMetaData rsmd = rs.getMetaData();

				int col_cnt = rsmd.getColumnCount();

				// 生成删除表和建表语句

				tmp_tab_name = "tmp_" + rsmd.getTableName(1);

				if (Utils.EMPTY.equalsIgnoreCase(col_def_str)) {
					for (int j = 0; j < col_cnt; j++) {
						String cl = rsmd.getColumnLabel(j + 1);
						int ct = rsmd.getColumnType(j + 1);
						String ctn = rsmd.getColumnTypeName(j + 1);

						if (ct == Types.BIGINT || ct == Types.INTEGER
								|| ct == Types.TINYINT || ct == Types.SMALLINT
								|| ct == Types.BIT) {
							int cds = rsmd.getColumnDisplaySize(j + 1);
							col_def_str += cl + " " + ctn + "(" + cds + ") ,"
									+ Utils.NEWLINE;
						}
						if (ct == Types.VARCHAR || ct == Types.CHAR) {
							int cds = rsmd.getColumnDisplaySize(j + 1);
							col_def_str += cl + " " + ctn + "(" + cds + ") ,"
									+ Utils.NEWLINE;
						}
						if (ct == Types.DOUBLE || ct == Types.FLOAT) {
							int precision = rsmd.getPrecision(j + 1);
							int scale = rsmd.getScale(j + 1);
							col_def_str += cl + " " + ctn + "(" + precision
									+ "," + scale + ") ," + Utils.NEWLINE;
						}
						if (ct == Types.DATE || ct == Types.TIMESTAMP) {
							col_def_str += cl + " " + ctn + " ,"
									+ Utils.NEWLINE;
						}
					}
					col_def_str = col_def_str.substring(0,
							col_def_str.length() - 2);
					drop_sql = drop_table
							.replaceAll("<tab_name>", tmp_tab_name);
					create_sql = create_table.replaceAll("<tab_name>",
							tmp_tab_name).replaceAll("<col_def_str>",
							col_def_str);
				}

				// 把数据写入到文件�?
				while (rs.next()) {
					Long fv = rs.getLong(tab_field);
					int idx = (int) ((fv - min_id) / interval);

					String l = Utils.EMPTY;
					for (int j = 0; j < col_cnt; j++) {
						String cv = rs.getString(j + 1);
						l += cv + Utils.TAB;
					}
					l = l.substring(0, l.length() - 1);
					tmp_osw_map.get(idx).write(l + Utils.NEWLINE);
				}
				stmt.close();

				for (int j = 0; j < part_num; j++) {
					tmp_osw_map.get(j).flush();
				}
			}

			// 在临时数据库上建�?

			it = tmp_conn_map.keySet().iterator();
			while (it.hasNext()) {

				Integer key = it.next();
				String url = tmp_connstr_map.get(key);
				Connection conn = tmp_conn_map.get(key);

				Statement stmt = conn.createStatement();

				Utils.printLogStr(url + Utils.TAB + drop_sql);
				stmt.execute(drop_sql);
				Utils.printLogStr(url + Utils.TAB + create_sql);
				stmt.execute(create_sql);
				stmt.close();

			}

			// 向临时数据库load数据

			it = tmp_conn_map.keySet().iterator();
			while (it.hasNext()) {

				Integer key = it.next();
				String filename = tmp_fn_map.get(key);
				String url = tmp_connstr_map.get(key);
				Connection conn = tmp_conn_map.get(key);

				Statement stmt = conn.createStatement();
				sql_str = " LOAD DATA LOCAL INFILE '<filename>' REPLACE INTO TABLE <tab_name> fields terminated by '\\t' lines terminated by '\\n' ";
				sql_str = sql_str.replaceAll("<filename>", filename)
						.replaceAll("<tab_name>", tmp_tab_name);
				Utils.printLogStr(url + Utils.TAB + sql_str);
				stmt.execute(sql_str);
				stmt.close();

			}

			// �?终在临时数据库上执行
			it = tmp_conn_map.keySet().iterator();
			sql_str = " select t1.ID,t1.EMAIL , t2.BIRTHDAY,t2.REGISTER_DATE from tmp_user_info t1 join tmp_user_info_etc t2 on (t1.ID=t2.USER_ID)";
			sql_str = sql_str.replaceAll("<result_tab_name>", "tmp_result");
			while (it.hasNext()) {

				Integer key = it.next();
				String url = tmp_connstr_map.get(key);
				Connection conn = tmp_conn_map.get(key);
				Statement stmt = conn.createStatement();
				Utils.printLogStr(url + Utils.TAB + sql_str);
				stmt.execute(sql_str);
				stmt.close();

			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void get_meta_info() throws Exception {
		Statement stmt = metaConn.createStatement();
		ResultSet rs = null;

		// 读取存储�?
		sql_str = "select id,mydb_name,db_ip,db_port,db_name from db_info ";
		Utils.printLogStr(metaUrl + Utils.TAB + sql_str);
		rs = stmt.executeQuery(sql_str);

		while (rs.next()) {
			Integer id = rs.getInt(1);
			String mydb_name = rs.getString(2);
			String db_ip = rs.getString(3);
			Integer db_port = rs.getInt(4);
			String db_name = rs.getString(5);
			DbEntry e = new DbEntry(id, mydb_name, db_ip, db_port, db_name);
			dbid_map.put(id, e);
			dbname_map.put(db_ip + db_port + db_name, e);
		}
		stmt.close();
		// 读取计算�?
		sql_str = "select id,mydb_name,db_ip,db_port,db_name from tmpdb_info ";
		Utils.printLogStr(metaUrl + Utils.TAB + sql_str);
		rs = stmt.executeQuery(sql_str);

		while (rs.next()) {
			Integer id = rs.getInt(1);
			String mydb_name = rs.getString(2);
			String db_ip = rs.getString(3);
			Integer db_port = rs.getInt(4);
			String db_name = rs.getString(5);
			DbEntry e = new DbEntry(id, mydb_name, db_ip, db_port, db_name);
			tmpdbid_map.put(id, e);
			tmpdbname_map.put(db_ip + db_port + db_name, e);
		}
		stmt.close();
		// 初始化连�?
		init_dataconn();
		init_tmpconn();

		// 读取�?
		sql_str = "select id,name,tab_type,startid,policyname,balancefield from table_info ";
		Utils.printLogStr(metaUrl + Utils.TAB + sql_str);
		rs = stmt.executeQuery(sql_str);

		while (rs.next()) {

			Integer id = rs.getInt(1);
			String name = rs.getString(2);
			String tab_type = rs.getString(3);
			Long startid = rs.getLong(4);
			String policyname = rs.getString(5);
			String balancefield = rs.getString(6);
			TableEntry e = new TableEntry(id, name, tab_type, startid,
					policyname, balancefield);
			tabid_map.put(id, e);
			tabname_map.put(name, e);

		}
		stmt.close();

		// 读取bucket
		sql_str = "select id,bucket_num,db_id,policyname from bucket_info ";
		Utils.printLogStr(metaUrl + Utils.TAB + sql_str);
		rs = stmt.executeQuery(sql_str);

		while (rs.next()) {
			Integer id = rs.getInt(1);
			Integer bucket_num = rs.getInt(2);
			Integer db_id = rs.getInt(3);
			String policyname = rs.getString(4);
			BucketEntry e = new BucketEntry(id, bucket_num, db_id, policyname);
			bucketid_map.put(id, e);
			List pl = policy2bucket_map.get(policyname);
			if (pl == null) {
				pl = new ArrayList();
				pl.add(e);
				policy2bucket_map.put(policyname, pl);
			} else {
				pl.add(e);
			}

		}
		stmt.close();
	}

	public void get_meta_db() throws Exception {

	}

	public void get_meta_tables() throws Exception {

	}

	public void get_meta_table_info(String tabname) throws Exception {

	}

	class IdGetter implements Callable {
		private IdGetter_Input in;

		public IdGetter(IdGetter_Input in) {
			super();
			this.in = in;
		}

		public IdGetter_Output call() throws Exception {

			for (int i = 0; i < data_conns_len; i++) {
				Connection conn = data_conn_map.get(in);
				String connstr = data_connstr_map.get(in);
				String sql_str = in.getSql_str();
				Statement stmt = conn.createStatement();

				Utils.printLogStr(connstr + Utils.TAB + sql_str);
				ResultSet rs = stmt.executeQuery(sql_str);
				while (rs.next()) {
					min_map.put(i, rs.getLong(1));
					max_map.put(i, rs.getLong(2));
				}
				stmt.close();
			}

			for (int i = 0; i < data_conns_len; i++) {

				Long min = min_map.get(i);
				Long max = max_map.get(i);

				if (min < min_id) {
					min_id = min;
				}
				if (max > max_id) {
					max_id = max;
				}
			}

			Utils.printLogStr(" min max is " + Utils.TAB + min_id + Utils.TAB
					+ max_id);
			IdGetter_Output out = new IdGetter_Output(min_id, max_id);

			return out;
		}
	}
}

class IdGetter_Input {
	String sql_str;

	public IdGetter_Input(String sql_str) {
		super();
		this.sql_str = sql_str;
	}

	public String getSql_str() {
		return sql_str;
	}

	public void setSql_str(String sql_str) {
		this.sql_str = sql_str;
	}

}

class IdGetter_Output {
	Long minid;
	Long maxid;

	public IdGetter_Output(Long minid, Long maxid) {

		this.minid = minid;
		this.maxid = maxid;
	}
}
