package com.jhh.hdb.meta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcUtils;

@SuppressWarnings("rawtypes")
public class Test {

	public Test() {

	}

	public static void main(String[] args) throws Exception {

		test();

		System.out.println("========end========");
	}

	public static void test_create_database(MetaDataImpl metadata) throws Exception {

		String sql;
		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		sql = StringTemplateUtils.read_stat("q_create_database");
		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			SQLCreateDatabaseStatement stmt = (SQLCreateDatabaseStatement) iterator.next();
			List<SQLCommentHint> hints = stmt.getHints();
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
						for (int i = 0; i < instance_arr.length; i++) {
							String nodeinstance_name = instance_arr[i];

							int portidx = nodeinstance_name.lastIndexOf(":");
							String nodeinstance_ip = nodeinstance_name.substring(13, portidx);
							String nodeinstance_port = nodeinstance_name.substring(portidx + 1);

							Connection conn = DriverManager.getConnection(nodeinstance_name, MetaDataImpl.user,
									MetaDataImpl.password);
							conn.setAutoCommit(true);

							String replacesql = "insert into nodeinstance(nodeinstance_ip,nodeinstance_port) values (?,?)";
							PreparedStatement pst = metadata.metadataConn.prepareStatement(replacesql,
									Statement.RETURN_GENERATED_KEYS);

							pst.setString(1, nodeinstance_ip);
							pst.setString(2, nodeinstance_port);
							pst.execute();
							ResultSet rs = pst.getGeneratedKeys(); // 获取

							if (rs.next()) {

								int nodeinstance_id = rs.getInt(1);
								InstanceConnManager.conn_idmap.put(nodeinstance_id, conn);
								InstanceConnManager.conn_namemap.put(nodeinstance_name, conn);
								System.out.println(nodeinstance_id + "," + nodeinstance_name + " connection opened !");

							}
						}
					}
				}
			}
		}
	}

	public static void test_create_table(MetaDataImpl metadata) throws Exception {

		String sql;
		String dbType = JdbcUtils.MYSQL;
		List<SQLStatement> stmtList;

		sql = StringTemplateUtils.read_stat("q_create_table_1");
		stmtList = SQLUtils.parseStatements(sql, dbType);
		for (Iterator iterator = stmtList.iterator(); iterator.hasNext();) {
			MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) iterator.next();

			String current_dbname = "current_db";
			String dbname = "";
			String tabname = "";

			String alias = stmt.getTableSource().getExpr().toString();
			String[] alias_arr = alias.split("\\.");
			if (alias_arr.length == 1) {
				dbname = current_dbname;
				tabname = alias_arr[0];
			}
			if (alias_arr.length == 2) {
				dbname = alias_arr[0];
				tabname = alias_arr[1];
			}

			List<SQLCommentHint> hints = stmt.getHints();
			List<SQLTableElement> element_list = stmt.getTableElementList();

			for (Iterator iterator2 = element_list.iterator(); iterator2.hasNext();) {
				SQLTableElement sqlTableElement = (SQLTableElement) iterator2.next();

				
				Map hcolumn_namemap = new HashMap();
				Map hindex_namemap = new HashMap();
				if (sqlTableElement instanceof SQLColumnDefinition) {
					SQLColumnDefinition ele = (SQLColumnDefinition) sqlTableElement;
					String hcolumn_name = ele.getName().getSimpleName();
					String hcolumn_type = ele.getDataType().getName();

					Hcolumn h = new Hcolumn();
					h.setHcolumn_name(hcolumn_name);
					h.setHcolumn_type(hcolumn_type);
					hcolumn_namemap.put(hcolumn_name,h);
					System.out.println(ele.toString());
				}

				if (sqlTableElement instanceof MySqlPrimaryKey) {
					MySqlPrimaryKey ele = (MySqlPrimaryKey) sqlTableElement;
					String hindex_name = ele.getName().getSimpleName();
					List<SQLExpr> columns = ele.getColumns();

					Hindex h = new Hindex();
					h.setHindex_name(hindex_name);
					
					for (int i =0; i<columns.size();i++) {
						SQLExpr sqlExpr = (SQLExpr) columns.get(i);
						
						R_hindex_hcolumn r = new R_hindex_hcolumn();
						r.setHindex_id(hindex_id);
						r.setHcolumn_id(hcolumn_id);
						r.setHcolumn_idxnum(i);
					}
					
					hindex_namemap.put(hindex_name,h);

					System.out.println(ele.getColumns());
				}

				if (sqlTableElement instanceof MySqlUnique) {
					MySqlUnique ele = (MySqlUnique) sqlTableElement;
					String hindex_name = ele.getName().getSimpleName();
					List<SQLExpr> columns = ele.getColumns();
					// Hindex h = new Hindex(hindex_id, htable_id, hindex_name,
					// hindex_type);

					System.out.println(ele.getColumns());
				}
				if (sqlTableElement instanceof MySqlKey) {
					MySqlKey ele = (MySqlKey) sqlTableElement;
					String elename = ele.getName().getSimpleName();
					List<SQLExpr> columns = ele.getColumns();
					System.out.println(ele.getColumns());
				}

				// if (sqlTableElement instanceof MySqlTableIndex) {
				// MySqlTableIndex ele = (MySqlTableIndex) sqlTableElement;
				// ele.getColumns();
				// System.out.println(ele.toString());
				// }

			}

			if (hints.size() == 2) {
				String hintstr = hints.get(0).getText().trim().substring(1).trim();
				String[] hintoption_arr = hintstr.split(" +");
				if (hintoption_arr.length >= 1) {
					String hintoption = hintoption_arr[0];
					String hintkey = hintoption.split("=")[0];
					String hintvalue = hintoption.split("=")[1];
					String[] instance_arr = hintvalue.split(",");
					if (hintkey.equalsIgnoreCase("nodeinstance_arr") && instance_arr.length >= 1) {

						System.out.println(Arrays.toString(instance_arr));
						for (int i = 0; i < instance_arr.length; i++) {
							String nodeinstance_name = instance_arr[i];

							int portidx = nodeinstance_name.lastIndexOf(":");
							String nodeinstance_ip = nodeinstance_name.substring(13, portidx);
							String nodeinstance_port = nodeinstance_name.substring(portidx + 1);

							Connection conn = DriverManager.getConnection(nodeinstance_name, MetaDataImpl.user,
									MetaDataImpl.password);
							conn.setAutoCommit(true);

							String replacesql = "insert into nodeinstance(nodeinstance_ip,nodeinstance_port) values (?,?)";
							PreparedStatement pst = metadata.metadataConn.prepareStatement(replacesql,
									Statement.RETURN_GENERATED_KEYS);

							pst.setString(1, nodeinstance_ip);
							pst.setString(2, nodeinstance_port);
							pst.execute();
							ResultSet rs = pst.getGeneratedKeys(); // 获取

							if (rs.next()) {

								int nodeinstance_id = rs.getInt(1);
								InstanceConnManager.conn_idmap.put(nodeinstance_id, conn);
								InstanceConnManager.conn_namemap.put(nodeinstance_name, conn);
								System.out.println(nodeinstance_id + "," + nodeinstance_name + " connection opened !");

							}
						}
					}
				}
			}

		}
	}

	private static void test_select_meta() throws Exception {
		MetaDataImpl d = new MetaDataImpl();
		d.getConnection();
		d.getAllMeta();
	}

	// 联调
	private static void test() throws Exception {
		MetaDataImpl d = new MetaDataImpl();
		d.getConnection();
		d.getAllMeta();

		// InstanceConnManager.open_instance_conn(d);

		test_create_table(d);
	}
}
