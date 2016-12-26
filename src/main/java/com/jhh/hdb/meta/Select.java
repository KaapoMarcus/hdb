package com.jhh.hdb.meta;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;
import org.apache.hadoop.hive.ql.parse.ASTErrorNode;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveLexer;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseError;

import com.jhh.hdb.proxyserver.define.ServerStatus;
import com.test.stats.parser.Analyser;
import com.test.stats.parser.QB;
import com.test.stats.parser.MySemanticAnalyzer;
import com.test.stats.parser.SemanticException;

/*
 * 
 * 1 在一个独立的分布式数据库中
 * 
 * 2 在多个独立的分布式数据库中
 * 
 * 3 在多个独立的数据库中
 * 
 */
public class Select {

	/*
	 * 得到最大最小id和记录数 ，使用多线程
	 * 
	 * 得到表结构不用多线程，建表不用多线程
	 * 
	 * map shuffle reduce 使用多线程
	 */
	final static String base_create_tmp_table_sql = "create table <table> ( <cols_str> <index_str>) ;";
	final static String base_id_sql = "select min(<shuffle_field>) minid , max(<shuffle_field>) maxid, count(1) datacount from <table>  ";
	final static String base_load_sql = "LOAD DATA LOCAL INFILE '<map_outfile>' replace into table <table> fields terminated by '\\t' lines terminated by '\\n' ;";
	final static String finaltab = "final_table";
	final static String base_out_sql = "select * from <table>  ;";

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
	static DateFormat datetimeformat = new SimpleDateFormat(
			"yyyy-MM-dd_HH:mm:ss");
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
	static DateFormat spacedatetimeformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// static String sqlstr = null;
	static String start_str = "start ";
	static String step = EMPTY;
	static String stop_str = "stop ";
	static String user = "root";
	static String workDir = "/data/";

	static ExecutorService x = null;

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

	public static void execNode(Node node) throws Exception {

		switch (node.nodetype) {

		case NodeType.NONFROM:
			get_nonfrom(node);
			break;

		case NodeType.SIMPLE:
			get_simple(node);
			break;

		case NodeType.ORDER:
			get_order(node);
			break;

		case NodeType.GROUP:
			get_group(node);
			break;

		case NodeType.JOIN:
			get_join(node);
			break;

		case NodeType.UNION:
			get_union(node);
			break;
		}

	}

	public static Map<Integer, ColumnInfo> get_col_map(ResultSetMetaData rsmd)
			throws Exception {

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
			// String classname = rsmd.getColumnClassName(i);
			// int displaysize = rsmd.getColumnDisplaySize(i);
			// int nullable = rsmd.isNullable(i);
			// cols_str += label + "\t" + typename + "\t"
			// + classname + "\t" + type + "\t"
			// + precision + "\t" + displaysize + "\t"
			// + tablename + "\t" + nullable;

		}

		return col_map;

	}

	public static String get_col_definition_str(
			Map<Integer, ColumnInfo> col_map, boolean hasindex)
			throws Exception {

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
				cols_str += ci.label + SPACE + ci.typename + "(" + ci.precision
						+ COMMA + ci.scale + ")" + SPACE;
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				cols_str += ci.label + SPACE + ci.typename + "(" + ci.precision
						+ ")" + SPACE;
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

	public static String get_col_select_str(Map<Integer, ColumnInfo> col_map)
			throws Exception {

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

	public static String get_col_select_str_2(Map<Integer, String> col_map)
			throws Exception {

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
	@SuppressWarnings("rawtypes")
	public static Map<Integer, Integer> find_join_table_cols(
			String[][] join_matrix) throws Exception {

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
	@SuppressWarnings("rawtypes")
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
					String left_colname = node.select_field_map.get(i).get(
							left_ci);
					String right_tab = node.table_alias_map.get(j);
					Integer right_ci = Integer.valueOf(s[1]);
					String right_colname = node.select_field_map.get(j).get(
							right_ci);

					ret += left_tab + "." + left_colname + " = " + right_tab
							+ "." + right_colname + " and ";
				}
			}

		}
		ret = ret.substring(0, ret.length() - 4);
		return ret;

	}

	@SuppressWarnings("rawtypes")
	public static Map<Integer, Integer> find_join_uniq_cols(
			String[][] join_matrix) throws Exception {

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

	public static MyResultSetCmd getResultSetCmd(Node node) throws Exception {

		TableInfo ti = node.output;
		Map<Integer, String> connstr_map = ti.connstr_map;
		Map<Integer, Connection> conn_map = open_Conns(connstr_map);
		int cnt = conn_map.size();
		String sqlstr = base_out_sql.replaceAll("<table>", ti.tablename);
		ResultSetMetaData rsmd;
		MyResultSet mrs = new MyResultSet();

		ResultSet[] rs_arr = new ResultSet[cnt];
		for (int i = 0; i < cnt; i++) {

			String connstr = connstr_map.get(i);
			Connection conn = conn_map.get(i);

			logstr = "conn=" + connstr + ",sql=" + sqlstr;
			printLogStr(logstr);

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
		return new MyResultSetCmd(mrs, null, false,
				ServerStatus.SERVER_STATUS_AUTOCOMMIT);
	}

	public static final TreeAdaptor adaptor = new CommonTreeAdaptor() {
		/**
		 * Creates an ASTNode for the given token. The ASTNode is a wrapper
		 * around antlr's CommonTree class that implements the Node interface.
		 *
		 * @param payload
		 *            The token.
		 * @return Object (which is actually an ASTNode) for the token.
		 */
		@Override
		public Object create(Token payload) {
			return new ASTNode(payload);
		}

		@Override
		public Object dupNode(Object t) {

			return create(((CommonTree) t).token);
		};

		@Override
		public Object errorNode(TokenStream input, Token start, Token stop,
				RecognitionException e) {
			return new ASTErrorNode(input, start, stop, e);
		};
	};

	public static class ANTLRNoCaseStringStream extends ANTLRStringStream {

		public ANTLRNoCaseStringStream(String input) {
			super(input);
		}

		@Override
		public int LA(int i) {

			int returnChar = super.LA(i);
			if (returnChar == CharStream.EOF) {
				return returnChar;
			} else if (returnChar == 0) {
				return returnChar;
			}

			return Character.toUpperCase((char) returnChar);
		}
	}

	/**
	 * HiveLexerX.
	 *
	 */
	public static class MyHiveLexer extends HiveLexer {

		private final ArrayList<ParseError> errors;

		public MyHiveLexer() {
			super();
			errors = new ArrayList<ParseError>();
		}

		public MyHiveLexer(CharStream input) {
			super(input);
			errors = new ArrayList<ParseError>();
		}

		@Override
		public void displayRecognitionError(String[] tokenNames,
				RecognitionException e) {

			// errors.add(new ParseError(this, e, tokenNames));
		}

		@Override
		public String getErrorMessage(RecognitionException e,
				String[] tokenNames) {
			String msg = null;

			if (e instanceof NoViableAltException) {
				@SuppressWarnings("unused")
				NoViableAltException nvae = (NoViableAltException) e;
				// for development, can add
				// "decision=<<"+nvae.grammarDecisionDescription+">>"
				// and "(decision="+nvae.decisionNumber+") and
				// "state "+nvae.stateNumber
				msg = "character " + getCharErrorDisplay(e.c)
						+ " not supported here";
			} else {
				msg = super.getErrorMessage(e, tokenNames);
			}

			return msg;
		}

		public ArrayList<ParseError> getErrors() {
			return errors;
		}

	}

   

	// boolean genResolvedParseTree(ASTNode ast, PlannerContext plannerCtx)
	// throws SemanticException {
	// ASTNode child = ast;
	//
	// // 4. continue analyzing from the child ASTNode.
	// Phase1Ctx ctx_1 = initPhase1Ctx();
	//
	// if (!doPhase1(child, qb, ctx_1, plannerCtx)) {
	// // if phase1Result false return
	// return false;
	// }
	//
	// plannerCtx.setParseTreeAttr(child, ctx_1);
	//
	// return true;
	// }

	public static Phase1Ctx initPhase1Ctx() {

		Phase1Ctx ctx_1 = new Phase1Ctx();
		ctx_1.nextNum = 0;
		ctx_1.dest = "reduce";

		return ctx_1;
	}

	static class MyANTLRStringStream extends ANTLRStringStream {

		public MyANTLRStringStream(String input) {
			super(input);
		}

		@Override
		public int LA(int i) {

			int returnChar = super.LA(i);
			if (returnChar == CharStream.EOF) {
				return returnChar;
			} else if (returnChar == 0) {
				return returnChar;
			}

			return Character.toUpperCase((char) returnChar);
		}
	}
	public static MyResultSetCmd exec(String sql) throws Exception {

		
		MyANTLRStringStream ancss = new MyANTLRStringStream(sql);
		MyHiveLexer lexer = new MyHiveLexer(ancss);
		TokenRewriteStream tokens = new TokenRewriteStream(lexer);

		HiveParser parser = new HiveParser(tokens);

		parser.setTreeAdaptor(adaptor);
		HiveParser.statement_return r = null;

		r = parser.statement();

		if (lexer.getErrors().size() > 0) {

			return null;
		}
		ASTNode tree = (ASTNode) r.getTree();
		tree.setUnknownTokenBoundaries();

		while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
			tree = (ASTNode) tree.getChild(0);
		}

		System.out.println("========");
		System.out.println(sql);
		System.out.println("");
		System.out.println(tree.dump());
		System.out.println("========");
		
		
		parseTree(tree);


		/*
		 * 单独查询只有where的处理比较简单 单独join,group,order的处理比较简单
		 * 
		 * 可以合并为一个步骤处理 join+group join字段=group字段 join+group+order
		 * join字段=group字段=order字段
		 * 
		 * 其他情况需要分多个步骤执行，可能还需要考虑union
		 */

		x = Executors.newFixedThreadPool(10);
		init_tableinfo();
		/*
		 * 例子： 单表只有where
		 */
		/*
		 * sql = "select id,nick from user_info where id>=10000 and id<20000 ;";
		 * SimpleNode node00 = new SimpleNode(0, 0, "node00", null,
		 * NodeType.SIMPLE); node00.select_field_str = "id,nick";
		 * node00.where_str = "id>=10 and id<20"; node00.table_name =
		 * "user_info"; node00.sql = sql;
		 * 
		 * do_root_node(node00, false); x.shutdown();
		 * 
		 * printLogStr(" finish ");
		 */

		/*
		 * 例子： order
		 */

		/*
		 * sql = "select id,nick from user_info where id%8<4 order by id;";
		 * OrderNode node00 = new OrderNode(0, 0, "node00", null,
		 * NodeType.ORDER); node00.select_field_str = "id,nick";
		 * node00.where_str = "id%8<4"; node00.table_name = "user_info";
		 * node00.shuffle_field_str = "id"; // node00.map_sql=
		 * "select id,nick from user_info where id>=10000 and id<20000 and <step_clause> order by id"
		 * ; // node00.reduce_sql="select id,nick from <table>  order by id";
		 * node00.sql = sql;
		 * 
		 * do_root_node(node00, false); x.shutdown();
		 * 
		 * printLogStr(" finish ");
		 */

		/*
		 * 例子： group
		 */

		/*
		 * sql =
		 * "select USER_ID,count(ORDER_NUMBER) order_count , max(GMT_CREATE) max_dt from trade_order where USER_ID%8<4 group by USER_ID;"
		 * ; GroupNode node00 = new GroupNode(0, 0, "node00", null,
		 * NodeType.GROUP); node00.select_field_str =
		 * "USER_ID,count(ORDER_NUMBER) order_count , max(GMT_CREATE) max_dt";
		 * node00.where_str = " USER_ID%8<4"; node00.table_name = "trade_order";
		 * node00.shuffle_field_str = "USER_ID"; // node00.map_sql=
		 * "select id,nick from user_info where id>=10000 and id<20000 and <step_clause> order by id"
		 * ; // node00.reduce_sql="select id,nick from <table>  order by id";
		 * node00.sql = sql;
		 * 
		 * do_root_node(node00, false); x.shutdown();
		 * 
		 * printLogStr(" finish ");
		 */
		/*
		 * int cnt = 5; String[][] jm = new String[cnt][cnt]; // 不考虑两种表的多个join条件
		 * , 0,0表示字段的index for (int i = 0; i < cnt; i++) { for (int j = 0; j <
		 * cnt; j++) { jm[i][j] = "0"; } } jm[0][1] = "0,0"; jm[0][2] = "0,0";
		 * 
		 * jm[1][1] = "1,1";
		 * 
		 * Set<String> ret = find_join(jm);
		 * System.out.println(Arrays.toString(ret.toArray()));
		 */

		/*
		 * 例子： join
		 */

		JoinNode node00 = new JoinNode(0, 0, "node00", null, NodeType.JOIN);
		// node00.select_field_str = " t1.ID,t1.NICK,t2.QQ ";
		node00.where_str = " length(t2.QQ)>2 ";

		// 有别名的情况
		Map<Integer, String> table_alias_map = new HashMap<Integer, String>();
		table_alias_map.put(0, "t1");
		table_alias_map.put(1, "t2");
		node00.table_alias_map = table_alias_map;

		// on里面的非join条件
		// key 为表，value为过滤条件
		Map<Integer, String> table_cond_map = new HashMap<Integer, String>();
		table_cond_map.put(0, "id%7<5");
		node00.table_cond_map = table_cond_map;

		// 每个表的index映射
		Map<Integer, String> table_map = new HashMap<Integer, String>();
		table_map.put(0, "user_info");
		table_map.put(1, "user_info_etc");
		node00.table_map = table_map;

		Map<Integer, Map<Integer, String>> select_field_map = new HashMap<Integer, Map<Integer, String>>();
		String s1 = "ID,NICK";
		String[] sa1 = s1.split(" *, *");
		Map<Integer, String> m0 = new HashMap<Integer, String>();
		for (int i = 0; i < sa1.length; i++) {
			m0.put(i, sa1[i]);
		}

		String s2 = "USER_ID,QQ";
		String[] sa2 = s2.split(" *, *");
		Map<Integer, String> m1 = new HashMap<Integer, String>();
		for (int i = 0; i < sa2.length; i++) {
			m1.put(i, sa2[i]);
		}
		select_field_map.put(0, m0);
		select_field_map.put(1, m1);
		node00.select_field_map = select_field_map;

		int tablecount = table_map.size();
		// 不考虑两种表的多个join条件 , 0,0表示字段的index
		String[][] join_matrix = new String[tablecount][tablecount];
		for (int i = 0; i < tablecount; i++) {
			for (int j = 0; j < tablecount; j++) {
				join_matrix[i][j] = "";
			}
		}
		join_matrix[0][1] = "0,0";
		node00.join_matrix = join_matrix;
		sql = "SELECT t0.ID,t0.NICK,t1.USER_ID,t1.QQ from user_info t0  join user_info_etc t1 on (t0.id=t1.USER_ID and t0.id%7<5 ) where length(t1.QQ)>2 ";
		node00.sql = sql;

		do_root_node(node00, false);

		MyResultSetCmd cmd = getResultSetCmd(node00);
		x.shutdown();

		printLogStr(" finish ");

		return cmd;
	
		
		/*
		 * 例子： join+group join字段=group字段
		 */

		/*
		 * 例子： join+order join字段=order字段
		 */

		/*
		 * 例子： join+group+order join字段=group字段=order字段
		 */

		/*
		 * 例子： group+order group字段=order字段
		 */

		/*
		 * 其他例子： group+order group字段 != order字段
		 */
		//
		// List<Node> parent = new ArrayList<Node>();
		// Node node = new GroupNode(0, 0, "node00", parent, NodeType.GROUP);
		// int map_num = 4;
		// int reduce_num = 3;
		//
		// final String table_name = "trade_order";
		// final String shuffle_field = "USER_ID";
		//
		// Map<String, String> create_tmp_table_map = new HashMap<String,
		// String>();
		//
		// final String base_map_sql =
		// "select * from <table> where <shuffle_field> >= <step_minid> and <shuffle_field> < <step_maxid> <limit_clause>;";
		// final String base_reduce_sql =
		// "SELECT user_id , ORDER_NUMBER,GMT_CREATE from trade_order order by user_id <limit_clause> ;";

		/*
		 * 树结构
		 */

		/*
		 * 多个节点
		 */
		/*
		 * Node node30 = new Node(3, 0, "30", null); Node node31 = new Node(3,
		 * 1, "31", null); List<Node> p_node20 = new ArrayList<Node>();
		 * p_node20.add(node30); p_node20.add(node31);
		 * 
		 * Node node20 = new Node(2, 0, "20", p_node20); Node node21 = new
		 * Node(2, 1, "21", null); List<Node> p_node10 = new ArrayList<Node>();
		 * p_node10.add(node20); p_node10.add(node21);
		 * 
		 * Node node10 = new Node(1, 0, "10", p_node10); Node node11 = new
		 * Node(1, 1, "11", null);
		 * 
		 * List<Node> p_node00 = new ArrayList<Node>(); p_node00.add(node10);
		 * p_node00.add(node11); Node node00 = new Node(0, 0, "00", p_node00);
		 * 
		 * x = Executors.newFixedThreadPool(10); System.out.println(node00);
		 * do_root_node(node00, false); x.shutdown();
		 * 
		 * printLogStr(" finish ");
		 */

		/*
		 * 只有一个节点
		 */

		/*
		 * Node node00 = new Node(0, 0, "00", null); System.out.println(node00);
		 * get_doNode(node00, false);
		 */
	}

	private static void parseTree(ASTNode tree) throws SemanticException {
		
		QB qb = new QB(null, null, false);		
		Phase1Ctx ctx_1 = new Phase1Ctx();
		ctx_1.nextNum = 0;
		ctx_1.dest = "reduce";

		Analyser analyser = new Analyser();
		analyser.analyze(tree, qb, ctx_1);
		
		MySemanticAnalyzer semx = new MySemanticAnalyzer();
		semx.genPlan(qb,false);
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public static void do_root_node(Node node, Boolean isp) throws Exception {

		/*
		 * 没有parents的节点，增加线程执行本节点
		 * 
		 * 有parents的节点，增加多个线程并发执行parents,parents执行完成之后，执行本节点
		 */

		List<Node> parents = node.parents;

		if (parents != null && parents.size() > 0) {
			HashMap<Node, Future> taskMap = new HashMap<Node, Future>();
			for (int i = 0; i < parents.size(); i++) {
				final Node tmpNode = parents.get(i);
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
				Node key = (Node) entry.getKey();
				Future val = (Future) entry.getValue();
				Boolean o = (Boolean) val.get();
			}
			taskMap.clear();

			/*
			 * 非输入节点，输出节点并且有父节点的情况
			 */
			final Node tmpNode = node;
			HashMap<Node, Future> taskMap2 = new HashMap<Node, Future>();
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
				Node key = (Node) entry.getKey();
				Future val = (Future) entry.getValue();
				Boolean o = (Boolean) val.get();
			}
			taskMap2.clear();
		} else {

			/*
			 * 输入节点执行
			 */
			if (isp) {
				final Node tmpNode = node;
				// printLogStr(node.toString() + " 2000");
				// Thread.currentThread().sleep(2000);
				execNode(tmpNode);
			}

			/*
			 * 本节点自己执行，不是父节点传过来的 比如只有一个节点的工作
			 */
			else {
				HashMap<Node, Future> taskMap = new HashMap<Node, Future>();
				final Node tmpNode = node;
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
					Node key = (Node) entry.getKey();
					Future val = (Future) entry.getValue();
					Boolean o = (Boolean) val.get();
				}
			}

		}

	}

	// public static void get_xxx() throws Exception {
	// 打开文件（日志文件，临时文件，结果文件）
	// 变量初始化赋值
	// 打开数据库连接
	// 创建临时表
	// 执行sql语句，这里可能有临时结果集，需要有多步
	// 关闭文件
	// 关闭连接
	// }

	/*
	 * SELECT user_id , count(ORDER_NUMBER),max(GMT_CREATE) from trade_order
	 * group by user_id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_group(Node x) throws Exception {

		final GroupNode node = (GroupNode) x;

		// 输入 目前只支持一个
		TableInfo ti = get_tableinfo(node.table_name);
		if (node.level > 0) {
			List<Node> parents = node.parents;
			if (parents != null && parents.size() > 0) {
				HashMap<Node, Future> taskMap = new HashMap<Node, Future>();
				for (int i = 0; i < parents.size(); i++) {
					final Node tmpNode = parents.get(i);
					ti = tmpNode.output;
				}
			}
		} else {
			ti = get_tableinfo(node.table_name);
		}

		Map<Integer, String> map_connstr_map = get_node_by_cond(ti,
				node.where_str);
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
			final String sqlstr = base_id_sql.replaceAll("<table>",
					node.table_name).replaceAll("<shuffle_field>",
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
							TableIdEntity e = new TableIdEntity(nodeid,
									node.table_name, node.shuffle_field_str,
									minid, maxid, datacount);
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

		crt_sql = base_create_tmp_table_sql
				.replaceAll("<table>", "tmp_" + node.table_name)
				.replaceAll("<cols_str>", cols_str)
				.replaceAll("<index_str>", index_str);

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

							String dropsql = "DROP TABLE IF EXISTS " + "tmp_"
									+ node.table_name + " ;";
							logstr = "conn=" + connstr + ",sql=" + dropsql;
							printLogStr(logstr);
							stmt.execute(dropsql);

							logstr = "conn=" + connstr + ",sql="
									+ create_table_sql;
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
					final String sqlstr = "select " + node.select_field_str
							+ " from " + node.table_name + " where "
							+ node.where_str + " and " + node.shuffle_field_str
							+ " >= " + step_minid + " and "
							+ node.shuffle_field_str + " < " + step_maxid
							+ " group by " + node.shuffle_field_str;

					final String map_outfile = "/data/" + node.table_name + "_"
							+ j + "_" + k + ".txt";
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
								OutputStreamWriter osw = new OutputStreamWriter(
										fos);
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

								String loadsql = base_load_sql.replaceAll(
										"<table>", "tmp_" + node.table_name)
										.replaceAll("<map_outfile>",
												map_outfile);
								logstr = "conn=" + reduce_connstr + ",sql="
										+ loadsql;
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
			final String sqlstr = crt_sql.replaceAll("tmp_" + node.table_name,
					"reduce_" + node.table_name);
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

							String dropsql = "DROP TABLE IF EXISTS "
									+ "reduce_" + node.table_name + " ;";
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

			final String reduce_sql = "replace into " + " reduce_"
					+ node.table_name + " select "
					+ get_col_select_str(col_map) + " from " + "tmp_"
					+ node.table_name + " group by " + node.shuffle_field_str;

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

			TableInfo oti = new TableInfo();
			oti.tablename = "tmp_" + node.table_name;
			oti.balancetype = BalanceType.RANGE;
			oti.balancefield = EMPTY;
			oti.connstr_map = reduce_connstr_map;
			oti.conn_map = reduce_conn_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

		close_Conns(map_conn_map);
		close_Conns(reduce_conn_map);
		logstr = stop_str + step;
		printLogStr(logstr);
		closeLogFile();
		closeResultFile();

	}

	/*
	 * SELECT t1.ID,t1.NICK,t2.QQ from user_info t1 join user_info_etc t2 on
	 * (t1.id=t2.USER_ID)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_join(Node x) throws Exception {
		JoinNode node = (JoinNode) x;

		// 输入 目前只支持多个
		Map<Integer, TableInfo> in_ti_map = new HashMap<Integer, TableInfo>();

		if (node.level > 0) {
			List<Node> parents = node.parents;
			if (parents != null && parents.size() > 0) {
				for (int i = 0; i < parents.size(); i++) {
					final Node tmpNode = parents.get(i);
					in_ti_map.put(i, tmpNode.output);
				}
			}
		} else {

		}

		Map<Integer, Integer> join_table_cols = find_join_table_cols(node.join_matrix);
		// Map<Integer, Integer> join_uniq_cols =
		// find_join_uniq_cols(node.join_matrix);

		Map<Integer, String> table_map = node.table_map;
		int tablecount = table_map.size();

		Map<Integer, Integer> table_map_num_map = new HashMap<Integer, Integer>();// 每个表的map数

		// 每张表的连接信息
		Map<Integer, Map<Integer, String>> table_map_connstr_map = new HashMap<Integer, Map<Integer, String>>();
		Map<Integer, Map<Integer, Connection>> table_map_conn_map = new HashMap<Integer, Map<Integer, Connection>>();

		int all_map_num = 0;
		for (int i = 0; i < tablecount; i++) {
			String tablename = table_map.get(i);
			TableInfo ti = get_tableinfo(tablename);
			Map<Integer, String> map_connstr_map = get_node_by_cond(ti,
					node.where_str);
			Map<Integer, Connection> map_conn_map = open_Conns(map_connstr_map);
			table_map_num_map.put(i, map_connstr_map.size());
			all_map_num += map_connstr_map.size();
			table_map_connstr_map.put(i, map_connstr_map);
			table_map_conn_map.put(i, map_conn_map);
		}

		ExecutorService exec = null;
		exec = Executors.newFixedThreadPool(all_map_num);
		HashMap taskMap = null;

		/* 查询每个表的洗牌字段的范围 */

		taskMap = new HashMap<String, Future>();
		for (int i = 0; i < tablecount; i++) {
			final String tablename = table_map.get(i);
			final int balancefieldidx = join_table_cols.get(i);
			final String balancefield = node.select_field_map.get(i).get(
					balancefieldidx);

			String cond_str = node.table_cond_map.get(i);
			String where_str = cond_str == null ? "" : " where " + cond_str;
			final String sqlstr = base_id_sql.replaceAll("<table>", tablename)
					.replaceAll("<shuffle_field>", balancefield) + where_str;

			int map_num = table_map_num_map.get(i);
			for (int j = 0; j < map_num; j++) {
				final int nodeid = j;
				final String connstr = table_map_connstr_map.get(i).get(j);
				final Connection conn = table_map_conn_map.get(i).get(j);

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
							TableIdEntity e = new TableIdEntity(nodeid,
									tablename, balancefield, minid, maxid,
									datacount);
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
			if (ret.datacount > datacount) {
				datacount = ret.datacount;
			}
		}

		/*
		 * 
		 * 根据记录数调整 reduce 数 , 使得每个reduce处理的记录数不会太多
		 */

		int reduce_num = (int) datacount / 200 + 1;

		Map<Integer, String> reduce_connstr_map = new HashMap<Integer, String>();
		for (int i = 0; i < reduce_num; i++) {
			reduce_connstr_map.put(i, "jdbc:mysql://192.168.0.151:3306/db" + i);
		}
		Map<Integer, Connection> reduce_conn_map = open_Conns(reduce_connstr_map);

		if (reduce_num > 1) {
			exec.shutdown();
			exec = Executors.newFixedThreadPool(all_map_num * reduce_num);
		}

		Long step_num = 0L;
		if (maxid < minid || datacount <= 0) {
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

		/* 得到在reduce上临时表的建表语句 */

		Map<Integer, Map<Integer, ColumnInfo>> table_colinfo_map = new HashMap<Integer, Map<Integer, ColumnInfo>>();

		taskMap = new HashMap<String, Future>();
		Map<String, String> create_tmp_table_map = new HashMap<String, String>();
		for (int i = 0; i < tablecount; i++) {
			final int tableid = i;
			final String tablename = table_map.get(i);
			final int balancefieldidx = join_table_cols.get(i);
			final String balancefield = node.select_field_map.get(i).get(
					balancefieldidx);

			Map<Integer, String> col_map = node.select_field_map.get(i);
			final String tmp_table_fields = get_col_select_str_2(col_map);

			for (int j = 0; j < 1; j++) {

				String cond_str = node.table_cond_map.get(i);
				String where_str = cond_str == null ? "" : " where " + cond_str;

				final String sqlstr = "select " + tmp_table_fields + " from "
						+ tablename + where_str + " limit 1 ";

				final int nodeid = j;
				final String connstr = table_map_connstr_map.get(i).get(j);
				final Connection conn = table_map_conn_map.get(i).get(j);

				try {

					logstr = "conn=" + connstr + ",sql=" + sqlstr;
					printLogStr(logstr);
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sqlstr);

					String index_str = "index idx(" + balancefield + ")";
					ResultSetMetaData rsmd = rs.getMetaData();
					Map<Integer, ColumnInfo> colinfo_map = get_col_map(rsmd);

					String cols_str = get_col_definition_str(colinfo_map, true);
					table_colinfo_map.put(tableid, colinfo_map);
					String ret = base_create_tmp_table_sql
							.replaceAll("<table>", "tmp_" + tablename)
							.replaceAll("<cols_str>", "")
							.replaceAll("<index_str>", index_str);

				} catch (SQLException e) {
					printLogStr(e.getMessage() + NEWLINE);

				}
			}
		}
		iter = taskMap.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			String ret = (String) val.get();
			create_tmp_table_map.put(key, ret);
		}

		/* 在reduce上建好临时表 */

		iter = create_tmp_table_map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			final String tmp_tablename = (String) entry.getKey();
			final String sqlstr = (String) entry.getValue();

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
		 * 边执行map，边执行shuffle map的模式 11 一个sql生成多个问 12 多个sql生成多个文件
		 * 
		 * 执行map，然后执行shuffle map的模式 21 一个sql生成多个问 22 多个sql生成多个文件
		 * 
		 * 生成临时文件，导入到reduce中 读取记录，批量插入到reduce中
		 */

		/*
		 * 执行map和shuffle, 一个map节点执行多次sql ，每次导出到一个文件，然后reduce节点
		 */

		taskMap = new HashMap<String, Future>();

		for (int i = 0; i < tablecount; i++) {
			final String tablename = table_map.get(i);
			final int balancefieldidx = join_table_cols.get(i);
			final String balancefield = node.select_field_map.get(i).get(
					balancefieldidx);

			Map<Integer, String> col_map = node.select_field_map.get(i);
			final String tmp_table_fields = get_col_select_str_2(col_map);

			int map_num = table_map_num_map.get(i);
			String[][] map_file_arr = new String[map_num][reduce_num];
			for (int j = 0; j < map_num; j++) {
				final int nodeid = j;
				for (int k = 0; k < reduce_num; k++) {

					Long step_minid = minid + k * step_num;
					Long step_maxid = minid + (k + 1) * step_num;
					final String sqlstr = "select " + tmp_table_fields
							+ " from " + tablename + " where "
							+ node.table_cond_map.get(i) + " and "
							+ balancefield + " >= " + step_minid + " and "
							+ balancefield + " < " + step_maxid;

					final String map_outfile = "/data/" + tablename + "_" + j
							+ "_" + k + ".txt";
					map_file_arr[j][k] = map_outfile;
					final int map_nodeid = j;

					final String map_connstr = table_map_connstr_map.get(i)
							.get(j);
					final Connection map_conn = table_map_conn_map.get(i)
							.get(j);

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
								OutputStreamWriter osw = new OutputStreamWriter(
										fos);
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

								String loadsql = base_load_sql
										.replaceAll("<table>",
												"tmp_" + tablename)
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

		String cols_def_str = "";
		String cols_str = "";
		String join_table_str = "";
		for (int i = 0; i < tablecount; i++) {
			final String tablename = table_map.get(i);
			final String ta = node.table_alias_map.get(i);

			Map<Integer, ColumnInfo> col_map = table_colinfo_map.get(i);
			cols_def_str += get_col_definition_str(col_map, true);

			if (i == tablecount - 1) {
				cols_str += get_col_select_str(col_map);
				join_table_str += "tmp_" + tablename + " " + ta;
			} else {
				cols_str += get_col_select_str(col_map) + COMMA;
				join_table_str += "tmp_" + tablename + " " + ta + " join ";
			}

		}

		/* 在reduce上建好最终表 */

		final String tablename = table_map.get(0);
		{

			final int balancefieldidx = join_table_cols.get(0);
			final String balancefield = node.select_field_map.get(0).get(
					balancefieldidx);
			String index_str = " index idx(" + balancefield + ")";
			String sqlstr = base_create_tmp_table_sql
					.replaceAll("<table>", "reduce_" + tablename)
					.replaceAll("<cols_str>", cols_def_str)
					.replaceAll("<index_str>", index_str);

			for (int j = 0; j < reduce_num; j++) {

				final int nodeid = j;
				final String connstr = reduce_connstr_map.get(j);
				final Connection conn = reduce_conn_map.get(j);

				try {
					Statement stmt = conn.createStatement();

					String dropsql = "DROP TABLE IF EXISTS " + "reduce_"
							+ tablename + " ;";
					logstr = "conn=" + connstr + ",sql=" + dropsql;
					printLogStr(logstr);
					stmt.execute(dropsql);

					logstr = "conn=" + connstr + ",sql=" + sqlstr;
					printLogStr(logstr);
					boolean ret = stmt.execute(sqlstr);
				} catch (SQLException e) {
					printLogStr(e.getMessage() + NEWLINE);
				}
			}

		}

		/* 执行reduce操作 */

		{
			String join_cond_str = find_join_cond(node);
			final String reduce_sql = "replace into <table> ".replaceAll(
					"<table>", "reduce_" + tablename)
					+ " select "
					+ cols_str
					+ " from "
					+ join_table_str
					+ " on  "
					+ join_cond_str
					+ " where " + node.where_str;

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

			TableInfo oti = new TableInfo();
			oti.tablename = "reduce_" + tablename;
			oti.balancetype = BalanceType.RANGE;
			oti.balancefield = EMPTY;
			oti.connstr_map = reduce_connstr_map;
			oti.conn_map = reduce_conn_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

		for (int i = 0; i < tablecount; i++) {
			Map<Integer, Connection> tab_conn_map = table_map_conn_map.get(i);
			close_Conns(tab_conn_map);
		}

		close_Conns(reduce_conn_map);
		logstr = stop_str + step;
		printLogStr(logstr);
		closeLogFile();
		closeResultFile();

	}

	/*
	 * SELECT user_id , ORDER_NUMBER,GMT_CREATE from trade_order order by
	 * user_id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_order(Node x) throws Exception {

		final OrderNode node = (OrderNode) x;

		// 输入 目前只支持一个
		TableInfo ti = get_tableinfo(node.table_name);
		if (node.level > 0) {
			List<Node> parents = node.parents;
			if (parents != null && parents.size() > 0) {
				HashMap<Node, Future> taskMap = new HashMap<Node, Future>();
				for (int i = 0; i < parents.size(); i++) {
					final Node tmpNode = parents.get(i);
					ti = tmpNode.output;
				}
			}
		} else {
			ti = get_tableinfo(node.table_name);
		}

		Map<Integer, String> map_connstr_map = get_node_by_cond(ti,
				node.where_str);
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
			final String sqlstr = base_id_sql.replaceAll("<table>",
					node.table_name).replaceAll("<shuffle_field>",
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
							TableIdEntity e = new TableIdEntity(nodeid,
									node.table_name, node.shuffle_field_str,
									minid, maxid, datacount);
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

		crt_sql = base_create_tmp_table_sql
				.replaceAll("<table>", "tmp_" + node.table_name)
				.replaceAll("<cols_str>", cols_str)
				.replaceAll("<index_str>", index_str);

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

							String dropsql = "DROP TABLE IF EXISTS " + "tmp_"
									+ node.table_name + " ;";
							logstr = "conn=" + connstr + ",sql=" + dropsql;
							printLogStr(logstr);
							stmt.execute(dropsql);

							logstr = "conn=" + connstr + ",sql="
									+ create_table_sql;
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
					final String sqlstr = "select " + node.select_field_str
							+ " from " + node.table_name + " where "
							+ node.where_str + " and " + node.shuffle_field_str
							+ " >= " + step_minid + " and "
							+ node.shuffle_field_str + " < " + step_maxid
							+ " order by " + node.shuffle_field_str;

					final String map_outfile = "/data/" + node.table_name + "_"
							+ j + "_" + k + ".txt";
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
								OutputStreamWriter osw = new OutputStreamWriter(
										fos);
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

								String loadsql = base_load_sql.replaceAll(
										"<table>", "tmp_" + node.table_name)
										.replaceAll("<map_outfile>",
												map_outfile);
								logstr = "conn=" + reduce_connstr + ",sql="
										+ loadsql;
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
			final String sqlstr = crt_sql.replaceAll("tmp_" + node.table_name,
					"reduce_" + node.table_name);
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

							String dropsql = "DROP TABLE IF EXISTS "
									+ "reduce_" + node.table_name + " ;";
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
			final String reduce_sql = ("replace into <table> " + node.sql
					.replaceAll(node.table_name, "tmp_" + node.table_name))
					.replaceAll("<table>", "reduce_" + node.table_name);
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

			TableInfo oti = new TableInfo();
			oti.tablename = "tmp_" + node.table_name;
			oti.balancetype = BalanceType.RANGE;
			oti.balancefield = EMPTY;
			oti.connstr_map = reduce_connstr_map;
			oti.conn_map = reduce_conn_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

		close_Conns(map_conn_map);
		close_Conns(reduce_conn_map);
		logstr = stop_str + step;
		printLogStr(logstr);
		closeLogFile();
		closeResultFile();

	}

	static Map<String, TableInfo> ti_map = new HashMap<String, TableInfo>();

	public static void init_tableinfo() throws Exception {

		Map<Integer, String> map = new HashMap<Integer, String>();
		for (int i = 0; i < 4; i++) {
			map.put(i, "jdbc:mysql://192.168.0.151:3306/db" + i);
		}

		TableInfo ti1 = new TableInfo();
		ti1.tablename = "user_info";
		ti1.balancefield = "ID";
		ti1.balancetype = BalanceType.HASH_RANGE;
		ti1.connstr_map = map;
		ti_map.put("user_info", ti1);

		TableInfo ti2 = new TableInfo();
		ti2.tablename = "user_info_etc";
		ti2.balancefield = "USER_ID";
		ti2.balancetype = BalanceType.HASH_RANGE;
		ti2.connstr_map = map;
		ti_map.put("user_info_etc", ti2);

		TableInfo ti3 = new TableInfo();
		ti3.tablename = "trade_order";
		ti3.balancefield = "USER_ID";
		ti3.balancetype = BalanceType.HASH_RANGE;
		ti3.connstr_map = map;
		ti_map.put("trade_order", ti3);

	}

	public static TableInfo get_tableinfo(String tablename) throws Exception {

		return ti_map.get(tablename);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<Integer, String> get_node_by_cond(TableInfo ti,
			String cond) throws Exception {
		Map<Integer, String> ret = null;

		/*
		 * 根据条件和表的分表策略判断，需要到哪些节点执行
		 */
		ret = ti.connstr_map;
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_simple(Node x) throws Exception {
		final SimpleNode node = (SimpleNode) x;

		// 输入 目前只支持一个
		TableInfo ti = get_tableinfo(node.table_name);
		if (node.level > 0) {
			List<Node> parents = node.parents;
			if (parents != null && parents.size() > 0) {
				HashMap<Node, Future> taskMap = new HashMap<Node, Future>();
				for (int i = 0; i < parents.size(); i++) {
					final Node tmpNode = parents.get(i);
					ti = tmpNode.output;
				}
			}
		} else {
			ti = get_tableinfo(node.table_name);
		}

		Map<Integer, String> map_connstr_map = get_node_by_cond(ti,
				node.where_str);
		int map_num = map_connstr_map.size();

		Map<Integer, Connection> map_conn_map = open_Conns(map_connstr_map);
		Map<Integer, String> reduce_connstr_map = get_node_by_cond(ti,
				node.where_str);
		Map<Integer, Connection> reduce_conn_map = open_Conns(reduce_connstr_map);

		ExecutorService exec = null;
		exec = Executors.newFixedThreadPool(map_num);
		HashMap taskMap = null;

		/*
		 * 什么也不干， 最后在拉取到结果文件的时候，再统一执行
		 */

		/*
		 * 放到最终表里， 不进行shuffle和reduce
		 */

		/*
		 * 得到在reduce上临时表的建表语句
		 */

		String crt_sql = "";
		Map<Integer, ColumnInfo> col_map = null;
		String index_str = EMPTY;
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
		String cols_str = get_col_definition_str(col_map, false);

		crt_sql = base_create_tmp_table_sql
				.replaceAll("<table>", "tmp_" + node.table_name)
				.replaceAll("<cols_str>", cols_str)
				.replaceAll("<index_str>", index_str);

		/*
		 * 在节点上建好临时表 , 直接执行
		 */

		final String create_table_sql = crt_sql;
		final String sql = ("replace into <table> " + node.sql).replaceAll(
				"<table>", "tmp_" + node.table_name);
		taskMap.clear();
		for (int j = 0; j < map_num; j++) {
			final int nodeid = j;
			final String connstr = map_connstr_map.get(j);
			final Connection conn = map_conn_map.get(j);

			Callable call = new Callable() {
				public Boolean call() throws Exception {

					try {
						Statement stmt = conn.createStatement();

						String dropsql = "DROP TABLE IF EXISTS " + "tmp_"
								+ node.table_name + " ;";
						logstr = "conn=" + connstr + ",sql=" + dropsql;
						printLogStr(logstr);
						stmt.execute(dropsql);

						logstr = "conn=" + connstr + ",sql=" + create_table_sql;
						printLogStr(logstr);
						stmt.execute(create_table_sql);

						logstr = "conn=" + connstr + ",sql=" + sql;
						printLogStr(logstr);
						stmt.execute(sql);

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

		Iterator iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 放到最终表里， 进行shuffle，不进行reduce
		 */

		/*
		 * 结果表信息
		 */
		if (node.level == 0) {

			TableInfo oti = new TableInfo();
			oti.tablename = "tmp_" + node.table_name;
			oti.balancetype = BalanceType.RANGE;
			oti.balancefield = EMPTY;
			oti.connstr_map = reduce_connstr_map;
			oti.conn_map = reduce_conn_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

		close_Conns(map_conn_map);
		logstr = stop_str + step;
		printLogStr(logstr);
		closeLogFile();
		closeResultFile();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_nonfrom(Node x) throws Exception {
		final SimpleNode node = (SimpleNode) x;

		// 输入 目前只支持一个
		TableInfo ti = get_tableinfo(node.table_name);

		Map<Integer, String> map_connstr_map = get_node_by_cond(ti,
				node.where_str);
		int map_num = map_connstr_map.size();

		Map<Integer, Connection> map_conn_map = open_Conns(map_connstr_map);
		Map<Integer, String> reduce_connstr_map = get_node_by_cond(ti,
				node.where_str);
		Map<Integer, Connection> reduce_conn_map = open_Conns(reduce_connstr_map);

		ExecutorService exec = null;
		exec = Executors.newFixedThreadPool(map_num);
		HashMap taskMap = null;

		String crt_sql = "";
		Map<Integer, ColumnInfo> col_map = null;
		String index_str = EMPTY;
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
		String cols_str = get_col_definition_str(col_map, false);

		crt_sql = base_create_tmp_table_sql
				.replaceAll("<table>", "tmp_" + node.table_name)
				.replaceAll("<cols_str>", cols_str)
				.replaceAll("<index_str>", index_str);

		/*
		 * 在节点上建好临时表 , 直接执行
		 */

		final String create_table_sql = crt_sql;
		final String sql = ("replace into <table> " + node.sql).replaceAll(
				"<table>", "tmp_" + node.table_name);
		taskMap.clear();
		for (int j = 0; j < map_num; j++) {
			final int nodeid = j;
			final String connstr = map_connstr_map.get(j);
			final Connection conn = map_conn_map.get(j);

			Callable call = new Callable() {
				public Boolean call() throws Exception {

					try {
						Statement stmt = conn.createStatement();

						String dropsql = "DROP TABLE IF EXISTS " + "tmp_"
								+ node.table_name + " ;";
						logstr = "conn=" + connstr + ",sql=" + dropsql;
						printLogStr(logstr);
						stmt.execute(dropsql);

						logstr = "conn=" + connstr + ",sql=" + create_table_sql;
						printLogStr(logstr);
						stmt.execute(create_table_sql);

						logstr = "conn=" + connstr + ",sql=" + sql;
						printLogStr(logstr);
						stmt.execute(sql);

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

		Iterator iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 结果表信息
		 */
		if (node.level > 0) {

			TableInfo oti = new TableInfo();
			oti.tablename = "tmp_" + node.table_name;
			oti.balancetype = BalanceType.RANGE;
			oti.balancefield = EMPTY;
			oti.connstr_map = reduce_connstr_map;
			oti.conn_map = reduce_conn_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

		close_Conns(map_conn_map);
		logstr = stop_str + step;
		printLogStr(logstr);
		closeLogFile();
		closeResultFile();

	}

	public static void get_example(Node x) throws Exception {
		final SimpleNode node = (SimpleNode) x;

		// 输入 目前只支持一个
		TableInfo ti = get_tableinfo(node.table_name);
		if (node.level > 0) {
			List<Node> parents = node.parents;
			if (parents != null && parents.size() > 0) {
				HashMap<Node, Future> taskMap = new HashMap<Node, Future>();
				for (int i = 0; i < parents.size(); i++) {
					final Node tmpNode = parents.get(i);
					ti = tmpNode.output;
				}
			}
		} else {
			ti = get_tableinfo(node.table_name);
		}

		Map<Integer, String> map_connstr_map = get_node_by_cond(ti,
				node.where_str);
		int map_num = map_connstr_map.size();

		Map<Integer, Connection> map_conn_map = open_Conns(map_connstr_map);
		Map<Integer, String> reduce_connstr_map = get_node_by_cond(ti,
				node.where_str);
		Map<Integer, Connection> reduce_conn_map = open_Conns(reduce_connstr_map);

		ExecutorService exec = null;
		exec = Executors.newFixedThreadPool(map_num);
		HashMap taskMap = null;

		/*
		 * 什么也不干， 最后在拉取到结果文件的时候，再统一执行
		 */

		/*
		 * 放到最终表里， 不进行shuffle和reduce
		 */

		/*
		 * 得到在reduce上临时表的建表语句
		 */

		String crt_sql = "";
		Map<Integer, ColumnInfo> col_map = null;
		String index_str = EMPTY;
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
		String cols_str = get_col_definition_str(col_map, false);

		crt_sql = base_create_tmp_table_sql
				.replaceAll("<table>", "tmp_" + node.table_name)
				.replaceAll("<cols_str>", cols_str)
				.replaceAll("<index_str>", index_str);

		/*
		 * 在节点上建好临时表 , 直接执行
		 */

		final String create_table_sql = crt_sql;
		final String sql = ("replace into <table> " + node.sql).replaceAll(
				"<table>", "tmp_" + node.table_name);
		taskMap.clear();
		for (int j = 0; j < map_num; j++) {
			final int nodeid = j;
			final String connstr = map_connstr_map.get(j);
			final Connection conn = map_conn_map.get(j);

			Callable call = new Callable() {
				public Boolean call() throws Exception {

					try {
						Statement stmt = conn.createStatement();

						String dropsql = "DROP TABLE IF EXISTS " + "tmp_"
								+ node.table_name + " ;";
						logstr = "conn=" + connstr + ",sql=" + dropsql;
						printLogStr(logstr);
						stmt.execute(dropsql);

						logstr = "conn=" + connstr + ",sql=" + create_table_sql;
						printLogStr(logstr);
						stmt.execute(create_table_sql);

						logstr = "conn=" + connstr + ",sql=" + sql;
						printLogStr(logstr);
						stmt.execute(sql);

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

		Iterator iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Future val = (Future) entry.getValue();
			Boolean ret = (Boolean) val.get();
		}

		/*
		 * 放到最终表里， 进行shuffle，不进行reduce
		 */

		/*
		 * 结果表信息
		 */
		if (node.level == 0) {

			TableInfo oti = new TableInfo();
			oti.tablename = "tmp_" + node.table_name;
			oti.balancetype = BalanceType.RANGE;
			oti.balancefield = EMPTY;
			oti.connstr_map = reduce_connstr_map;
			node.output = oti;
		} else {
			// 把结果放到目标地方去
		}

		exec.shutdown();

		close_Conns(map_conn_map);
		logstr = stop_str + step;
		printLogStr(logstr);
		closeLogFile();
		closeResultFile();

	}

	/*
	 * SELECT t1.ID,t1.NICK,t2.QQ from user_info t1 join user_info_etc t2 on
	 * (t1.id=t2.USER_ID)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_union(Node node) throws Exception {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void get_xxx(Node node) throws Exception {
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
}
