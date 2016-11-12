package com.jhh.hdb.meta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class MetaDataImpl {

	Map hcolumn_idmap = new HashMap();
	Map hcolumn_namemap = new HashMap();

	Map hdp_idmap = new HashMap();
	Map hdp_namemap = new HashMap();

	Map hdb_idmap = new HashMap();
	Map hdb_namemap = new HashMap();

	Map hdbconfig_idmap = new HashMap();
	Map hdbconfig_namemap = new HashMap();

	Map hindex_idmap = new HashMap();
	Map hindex_namemap = new HashMap();

	Map htable_idmap = new HashMap();
	Map htable_namemap = new HashMap();

	public Map nodeinstance_idmap = new HashMap();
	public Map nodeinstance_namemap = new HashMap();

	Map nodedb_idmap = new HashMap();
	Map nodedb_namemap = new HashMap();

	Map r_hindex_hcolumn_idmap = new HashMap();
	Map r_hindex_hcolumn_namemap = new HashMap();

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

		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://10.199.134.41:3306/hdb_metadb";
		metadataConn = DriverManager.getConnection(url, user, password);
		return metadataConn;
	}

	public void getAllMeta() throws Exception {
		String[] sql_arr = new String[] { "select * from hcolumn", "select * from hdb", "select * from hdbconfig",
				"select * from hdp", "select * from hindex", "select * from htable", "select * from nodedb",
				"select * from nodeinstance", "select * from r_hindex_hcolumn", };
		String[] table_arr = new String[] { "hcolumn", "hdb", "hdbconfig", "hdp", "hindex", "htable", "nodedb",
				"nodeinstance", "r_hindex_hcolumn", };
		String table = null;
		String sql = null;
		Statement stmt = null;
		ResultSet rs = null;

		for (int i = 0; i < table_arr.length; i++) {

			table = table_arr[i];
			sql = sql_arr[i];
			stmt = metadataConn.createStatement();
			rs = stmt.executeQuery(sql);
			if (table.equalsIgnoreCase("hcolumn")) {
				while (rs.next()) {
					Integer hcolumn_id = rs.getInt(1);
					String hcolumn_name = rs.getString(2);
					String hcolumn_type = rs.getString(3);
					Integer htable_id = rs.getInt(4);
					Integer hdc_flag = rs.getInt(5);
					Integer hdc_idxnum = rs.getInt(6);
					// public Integer hcolumn_id;
					// public String hcolumn_name;
					// public Integer hcolumn_type;
					// public Integer htable_id;
					// public Integer hdc_flag;
					// public Integer hdc_idxnum;

					Hcolumn entry = new Hcolumn(hcolumn_id, hcolumn_name, hcolumn_type, htable_id, hdc_flag,
							hdc_idxnum);
					hcolumn_idmap.put(hcolumn_id, entry);
					hcolumn_namemap.put(hcolumn_id, entry);
				}

			}
			if (table.equalsIgnoreCase("hdb")) {
				while (rs.next()) {
					Integer hdb_id = rs.getInt(1);
					String hdb_name = rs.getString(2);
					// public Integer hdb_id;
					// public String hdb_name;

					Hdb entry = new Hdb(hdb_id, hdb_name);
					hdb_idmap.put(hdb_id, entry);
					hdb_namemap.put(hdb_id, entry);
				}

			}
			if (table.equalsIgnoreCase("hdbconfig")) {
				while (rs.next()) {
					Integer config_id = rs.getInt(1);
					String config_name = rs.getString(2);
					String config_type = rs.getString(3);
					String config_value = rs.getString(4);

					// public Integer config_id;
					// public String config_name;
					// public Integer config_type;
					// public String config_value;

					Hdbconfig entry = new Hdbconfig(config_id, config_name, config_type, config_value);
					hdbconfig_idmap.put(config_id, entry);
					hdbconfig_namemap.put(config_id, entry);
				}

			}
			if (table.equalsIgnoreCase("hdp")) {
				while (rs.next()) {
					Integer hdp_id = rs.getInt(1);
					String hdp_name = rs.getString(2);
					Integer hdp_type = rs.getInt(3);
					Integer hdp_algo = rs.getInt(4);
					long hdp_min = rs.getLong(5);
					long hdp_max = rs.getLong(6);
					Integer hdp_step = rs.getInt(7);

					// public Integer hdp_id;
					// public String hdp_name;
					// public Integer hdp_type;
					// public Integer hdp_algo;
					// public Long hdp_min;
					// public Long hdp_max;
					// public Integer hdp_step;

					Hdp entry = new Hdp(hdp_id, hdp_name, hdp_type, hdp_algo, hdp_min, hdp_max, hdp_step);
					hdp_idmap.put(hdp_id, entry);
					hdp_namemap.put(hdp_id, entry);
				}

			}
			if (table.equalsIgnoreCase("hindex")) {
				while (rs.next()) {
					Integer hindex_id = rs.getInt(1);
					Integer htable_id = rs.getInt(2);
					Integer htable_type = rs.getInt(3);
					String hindex_name = rs.getString(4);

					// public Integer hindex_id;
					// public Integer htable_id;
					// public String hindex_name;

					Hindex entry = new Hindex(hindex_id, htable_id, htable_type, hindex_name);
					hindex_idmap.put(hindex_id, entry);
					hindex_namemap.put(hindex_id, entry);
				}

			}
			if (table.equalsIgnoreCase("htable")) {
				while (rs.next()) {
					Integer htable_id = rs.getInt(1);
					String htable_name = rs.getString(2);
					Integer hdb_id = rs.getInt(3);
					Integer hdp_id = rs.getInt(4);
					// public Integer htable_id;
					// public String htable_name;
					// public Integer hdb_id;
					// public Integer hdp_id;

					Htable entry = new Htable(htable_id, htable_name, hdb_id, hdp_id);
					htable_idmap.put(htable_id, entry);
					htable_namemap.put(htable_id, entry);
				}

			}
			if (table.equalsIgnoreCase("nodedb")) {
				while (rs.next()) {
					Integer nodedb_id = rs.getInt(1);
					String nodedb_name = rs.getString(2);
					Integer nodeinstance_id = rs.getInt(3);
					Integer hdb_id = rs.getInt(4);

					//
					// public Integer nodedb_id;
					// public String nodedb_name;
					// public Integer nodeinstance_id;
					// public Integer hdb_id;
					//
					NodeDb entry = new NodeDb(nodedb_id, nodedb_name, nodeinstance_id, hdb_id);
					nodedb_idmap.put(nodedb_id, entry);
					nodedb_namemap.put(nodedb_id, entry);
				}

			}
			if (table.equalsIgnoreCase("nodeinstance")) {
				while (rs.next()) {
					Integer nodeinstance_id = rs.getInt(1);
					String nodeinstance_ip = rs.getString(2);
					Integer nodeinstance_port = rs.getInt(3);
					// public Integer nodeinstance_id;
					// public String nodeinstance_ip;
					// public Integer nodeinstance_port;

					NodeInstance entry = new NodeInstance(nodeinstance_id, nodeinstance_ip, nodeinstance_port);
					nodeinstance_idmap.put(nodeinstance_id, entry);
					String nodeinstance_name = "jdbc:mysql://" + nodeinstance_ip + ":" + nodeinstance_port;
					nodeinstance_namemap.put(nodeinstance_name, entry);
				}

			}
			if (table.equalsIgnoreCase("r_hindex_hcolumn")) {
				while (rs.next()) {

					Integer hindex_id = rs.getInt(1);
					Integer hcolumn_id = rs.getInt(2);
					Integer hcolumn_idxnum = rs.getInt(3);

					// public Integer hindex_id ;
					// public Integer hcolumn_id ;
					// public Integer hcolumn_idxnum;

					R_hindex_hcolumn entry = new R_hindex_hcolumn(hindex_id, hcolumn_id, hcolumn_idxnum);
					r_hindex_hcolumn_idmap.put(hindex_id, entry);
					r_hindex_hcolumn_namemap.put(hindex_id, entry);
				}

			}
		}

	}

}
