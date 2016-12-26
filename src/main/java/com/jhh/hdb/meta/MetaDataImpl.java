package com.jhh.hdb.meta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class MetaDataImpl {

	Map hcolumn_namemap = new HashMap();

	Map hdp_namemap = new HashMap();

	Map hdb_namemap = new HashMap();

	Map hdbconfig_namemap = new HashMap();

	Map hindex_namemap = new HashMap();

	Map htable_namemap = new HashMap();

	public Map nodeinstance_namemap = new HashMap();

	Map nodedb_namemap = new HashMap();

	Map rhindexhcolumn_namemap = new HashMap();

	public static Map nodeinstance_conn_map = new HashMap();
	public static Map allhdb_conn_map = new HashMap();

	/** DDB server configurations */
	private String serverConfigFile = null;
	public static String user = "zabbix";
	public static String password = "zabbix123456";

	public Connection metadataConn = null;

	/**
	 * Empty constructor, use default configuration file.
	 */
	public MetaDataImpl() {
		this(null);
	}

	public MetaDataImpl(String serverConfigFile) {
		this.serverConfigFile = serverConfigFile;
	}

	private String getServerConfigFile() {
		return serverConfigFile;
	}

	Connection getConnection() throws Exception {
		if(metadataConn==null){

			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://10.199.134.41:3306/hdbmeta";
			metadataConn = DriverManager.getConnection(url, user, password);
		}
		return metadataConn;
	}

	public void getAllMeta() throws Exception {
		String[] sql_arr = new String[] { "select * from nodeinstance", "select * from nodedb", "select * from hcolumn",
				"select * from hdb", "select * from hdbconfig", "select * from hdp", "select * from hindex",
				"select * from htable", "select * from rhindexhcolumn", };
		String[] table_arr = new String[] { "nodeinstance", "nodedb", "hcolumn", "hdb", "hdbconfig", "hdp", "hindex",
				"htable", "rhindexhcolumn", };
		String table = null;
		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		for (int i = 0; i < table_arr.length; i++) {

			table = table_arr[i];
			sql = sql_arr[i];
			stmt = metadataConn.createStatement();
			rs = stmt.executeQuery(sql);

			if (table.equalsIgnoreCase("nodeinstance")) {
				while (rs.next()) {
					String nodeinstance_name = rs.getString(1);

					Nodeinstance entry = new Nodeinstance(nodeinstance_name);
					nodeinstance_namemap.put(nodeinstance_name, entry);
				}

			}

			if (table.equalsIgnoreCase("nodedb")) {
				while (rs.next()) {
					String nodedb_name = rs.getString(1);
					String nodeinstance_name = rs.getString(2);
					String hdb_name = rs.getString(3);
					Integer hdb_orderno = rs.getInt(4);

					Nodedb entry = new Nodedb(nodedb_name, nodeinstance_name, hdb_name, hdb_orderno);
					String key = nodeinstance_name + "/" + nodedb_name;
					nodedb_namemap.put(key, entry);
				}

			}

			if (table.equalsIgnoreCase("hcolumn")) {
				while (rs.next()) {
					String hdb_name = rs.getString(1);
					String htable_name = rs.getString(2);
					String hcolumn_name = rs.getString(3);
					String hcolumn_type = rs.getString(4);
					Integer htable_orderno = rs.getInt(5);
					Integer hdc_flag = rs.getInt(6);
					Integer hdc_orderno = rs.getInt(7);

					Hcolumn entry = new Hcolumn(hdb_name, htable_name, hcolumn_name, hcolumn_type, htable_orderno,
							hdc_flag, hdc_orderno);
					String key = hdb_name + "." + htable_name + "." + hcolumn_name;
					hcolumn_namemap.put(key, entry);
				}

			}

			if (table.equalsIgnoreCase("hdbconfig")) {
				while (rs.next()) {
					String config_name = rs.getString(2);
					String config_type = rs.getString(3);
					String config_value = rs.getString(4);

					Hdbconfig entry = new Hdbconfig(config_name, config_type, config_value);
					hdbconfig_namemap.put(config_name, entry);
				}

			}
			if (table.equalsIgnoreCase("hdp")) {
				while (rs.next()) {
					String hdp_name = rs.getString(1);
					String hdp_type = rs.getString(2);
					String hdp_algo = rs.getString(3);
					String hdp_min = rs.getString(4);
					String hdp_max = rs.getString(5);
					String hdp_step = rs.getString(6);

					// public String hdp_name;
					// public Integer hdp_type;
					// public Integer hdp_algo;
					// public Long hdp_min;
					// public Long hdp_max;
					// public Integer hdp_step;

					Hdp entry = new Hdp(hdp_name, hdp_type, hdp_algo, hdp_min, hdp_max, hdp_step);
					hdp_namemap.put(hdp_name, entry);
				}

			}
			if (table.equalsIgnoreCase("hdb")) {
				while (rs.next()) {
					String hdb_name = rs.getString(2);
					Hdb entry = new Hdb(hdb_name);
					hdb_namemap.put(hdb_name, entry);
				}

			}

			if (table.equalsIgnoreCase("htable")) {
				while (rs.next()) {
					String hdb_name = rs.getString(1);
					String htable_name = rs.getString(2);
					String hdp_name = rs.getString(3);
					// public Integer htable_id;
					// public String htable_name;
					// public Integer hdb_id;
					// public Integer hdp_id;

					Htable entry = new Htable(hdb_name, htable_name, hdp_name);
					String key = hdb_name + "." + htable_name;
					htable_namemap.put(key, entry);
				}

			}
			if (table.equalsIgnoreCase("hindex")) {
				while (rs.next()) {
					String hdb_name = rs.getString(1);
					String htable_name = rs.getString(2);
					String hindex_name = rs.getString(3);
					String hindex_type = rs.getString(4);

					Hindex entry = new Hindex(hdb_name, htable_name, hindex_name, hindex_type);
					String key = hdb_name + "." + htable_name + "." + hindex_name;
					hindex_namemap.put(key, entry);
				}

			}
			if (table.equalsIgnoreCase("rhindexhcolumn")) {
				while (rs.next()) {
					String hdb_name = rs.getString(1);
					String htable_name = rs.getString(2);
					String hindex_name = rs.getString(3);
					String hcolumn_name = rs.getString(4);
					Integer hcolumn_orderno = rs.getInt(5);

					// public Integer hindex_id ;
					// public Integer hcolumn_id ;
					// public Integer hcolumn_idxnum;

					Rhindexhcolumn entry = new Rhindexhcolumn(hdb_name, htable_name, hindex_name, hcolumn_name,
							hcolumn_orderno);
					String key = hdb_name + "." + htable_name + "." + hindex_name + "." + hcolumn_name;
					rhindexhcolumn_namemap.put(key, entry);
				}

			}
		}

	}

	public Map get_hdc_list_by_htable(String hdb_name, String htable_name) throws Exception {

		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		sql = "select * from hcolumn where hdb_name='<hdb_name>' and htable_name='<htable_name>' and hdc_flag=1 order by hdc_orderno asc ";
		String exec_sql = sql.replace("<hdb_name>", hdb_name).replace("<htable_name>", htable_name);
		stmt = metadataConn.createStatement();
		rs = stmt.executeQuery(exec_sql);
		Map conn_map = new HashMap();
		Map hcolumn_namemap = new HashMap();
		while (rs.next()) {
			String hcolumn_name = rs.getString(3);
			String hcolumn_type = rs.getString(4);
			Integer htable_orderno = rs.getInt(5);
			Integer hdc_flag = rs.getInt(6);
			Integer hdc_orderno = rs.getInt(7);

			Hcolumn entry = new Hcolumn(hdb_name, htable_name, hcolumn_name, hcolumn_type, htable_orderno, hdc_flag,
					hdc_orderno);
			hcolumn_namemap.put(hcolumn_name, entry);
		}

		rs.close();
		stmt.close();
		return hcolumn_namemap;
	}
	public Map get_hdc_list2_by_htable(String hdb_name, String htable_name) throws Exception {

		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		sql = "select * from hcolumn where hdb_name='<hdb_name>' and htable_name='<htable_name>' and hdc_flag=1 order by hdc_orderno asc ";
		String exec_sql = sql.replace("<hdb_name>", hdb_name).replace("<htable_name>", htable_name);
		stmt = metadataConn.createStatement();
		rs = stmt.executeQuery(exec_sql);
		Map conn_map = new HashMap();
		Map hcolumn_namemap = new HashMap();
		while (rs.next()) {
			String hcolumn_name = rs.getString(3);
			String hcolumn_type = rs.getString(4);
			Integer htable_orderno = rs.getInt(5);
			Integer hdc_flag = rs.getInt(6);
			Integer hdc_orderno = rs.getInt(7);

			Hcolumn entry = new Hcolumn(hdb_name, htable_name, hcolumn_name, hcolumn_type, htable_orderno, hdc_flag,
					hdc_orderno);
			hcolumn_namemap.put(hdc_orderno, hcolumn_name);
		}

		rs.close();
		stmt.close();
		return hcolumn_namemap;
	}
	public Map get_hcolumn_list_by_htable(String hdb_name, String htable_name) throws Exception {

		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		sql = "select * from hcolumn where hdb_name='<hdb_name>' and htable_name='<htable_name>' order by htable_orderno asc ";
		String exec_sql = sql.replace("<hdb_name>", hdb_name).replace("<htable_name>", htable_name);
		stmt = metadataConn.createStatement();
		rs = stmt.executeQuery(exec_sql);
		Map conn_map = new HashMap();
		Map hcolumn_namemap = new HashMap();
		while (rs.next()) {
			String hcolumn_name = rs.getString(3);
			String hcolumn_type = rs.getString(4);
			Integer htable_orderno = rs.getInt(5);
			Integer hdc_flag = rs.getInt(6);
			Integer hdc_orderno = rs.getInt(7);

			Hcolumn entry = new Hcolumn(hdb_name, htable_name, hcolumn_name, hcolumn_type, htable_orderno, hdc_flag,
					hdc_orderno);
			hcolumn_namemap.put(hcolumn_name, entry);
		}

		rs.close();
		stmt.close();
		return hcolumn_namemap;
	}
	public Hdp get_hdp_by_htable(String hdb_name, String htable_name) throws Exception {

		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;
		Hdp entry = null;
		sql = "select t2.* from htable t1 join hdp t2 on (t1.hdp_name=t2.hdp_name and t1.hdb_name='<hdb_name>' and t1.htable_name='<htable_name>') limit 1";
		String exec_sql = sql.replace("<hdb_name>", hdb_name).replace("<htable_name>", htable_name);
		stmt = metadataConn.createStatement();
		rs = stmt.executeQuery(exec_sql);
		if (rs.next()) {

			String hdp_name = rs.getString(1);
			String hdp_type = rs.getString(2);
			String hdp_algo = rs.getString(3);
			String hdp_min = rs.getString(4);
			String hdp_max = rs.getString(5);
			String hdp_step = rs.getString(6);
			entry = new Hdp(hdp_name, hdp_type, hdp_algo, hdp_min, hdp_max, hdp_step);

		}
		rs.close();
		stmt.close();

		return entry;

	}

	public Map open_conn_by_hdb(String hdb_name) throws Exception {

		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		sql = "select * from nodedb where hdb_name='<hdb_name>'";
		String exec_sql = sql.replace("<hdb_name>", hdb_name);
		stmt = metadataConn.createStatement();
		rs = stmt.executeQuery(exec_sql);
		Map conn_map = new HashMap();

		while (rs.next()) {
			String nodedb_name = rs.getString(1);
			String nodeinstance_name = rs.getString(2);

			String nodedb = nodeinstance_name + "/" + nodedb_name;
			Connection conn = DriverManager.getConnection(nodedb, MetaDataImpl.user, MetaDataImpl.password);
			conn.setAutoCommit(true);

			conn_map.put(nodedb, conn);
		}

		return conn_map;
	}

	public Map open_idconnmap_by_hdb(String hdb_name) throws Exception {

		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		sql = "select * from nodedb where hdb_name='<hdb_name>' order by hdb_orderno asc ";
		String exec_sql = sql.replace("<hdb_name>", hdb_name);
		stmt = metadataConn.createStatement();
		rs = stmt.executeQuery(exec_sql);
		Map conn_map = new HashMap();

		while (rs.next()) {
			String nodedb_name = rs.getString(1);
			String nodeinstance_name = rs.getString(2);
			Integer hdb_orderno = rs.getInt(4);

			String nodedb = nodeinstance_name + "/" + nodedb_name;
			Connection conn = DriverManager.getConnection(nodedb, MetaDataImpl.user, MetaDataImpl.password);
			conn.setAutoCommit(true);

			conn_map.put(hdb_orderno, conn);
		}

		return conn_map;
	}

	public List get_hdc_index_by_htable(String hdb_name, String htable_name) throws Exception {

		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		sql = "select htable_orderno from hcolumn where hdb_name='<hdb_name>' and htable_name='<htable_name>' and hdc_flag=1 order by htable_orderno asc ";
		String exec_sql = sql.replace("<hdb_name>", hdb_name).replace("<htable_name>", htable_name);
		stmt = metadataConn.createStatement();
		rs = stmt.executeQuery(exec_sql);
		List ret = new ArrayList();

		while (rs.next()) {
			Integer htable_orderno = rs.getInt(1);
			ret.add(htable_orderno);
		}

		return ret;
	}
}
