package com.jhh.hdb.meta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

@SuppressWarnings("rawtypes")
public class InstanceConnManager {

	public static Map conn_idmap = new HashMap();
	public static Map conn_namemap = new HashMap();

	public static void open_all_instance_conn(MetaDataImpl metadata) throws Exception {

		Iterator iter = metadata.nodeinstance_idmap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			NodeInstance val = (NodeInstance) entry.getValue();
			Integer nodeinstance_id = val.getNodeinstance_id();
			String nodeinstance_ip = val.getNodeinstance_ip();
			Integer nodeinstance_port = val.getNodeinstance_port();

			String nodeinstance_name = "jdbc:mysql://" + nodeinstance_ip + ":" + nodeinstance_port;
			Connection conn = DriverManager.getConnection(nodeinstance_name, MetaDataImpl.user, MetaDataImpl.password);
			conn.setAutoCommit(true);
			conn_idmap.put(nodeinstance_id, conn);
			conn_idmap.put(nodeinstance_name, conn);
			System.out.println(nodeinstance_id + "," + nodeinstance_name + " connection opened !");
		}
	}

	public static void open_instance_conn(MetaDataImpl metadata, String url) throws Exception {
	}
}
