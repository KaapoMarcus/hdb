package com.jhh.hdb.meta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceTool {

	private String hexToString(byte[] output) {
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < output.length; j++) {
			buf.append(hexDigit[(output[j] >> 4) & 0x0f]);
			buf.append(hexDigit[output[j] & 0x0f]);
		}
		return buf.toString();

	}

	private static int hexStringToInt(String s) {

		int ret = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c >= '0' && c <= '9') {
				ret += (c - '0');
			} else if (c >= 'A' && c <= 'Z') {
				ret += (c - 'A' + 10);
			} else if (c >= 'a' && c <= 'z') {
				ret += (c - 'a' + 10);
			} else {

			}
			
			if (i < s.length() - 1) {
				ret = ret * 16;
			}
		}
		return ret;

	}

	public static void main(String[] args) throws Exception {
		List<ColumnInfo> columninfo_list = new ArrayList<ColumnInfo>();
		// ColumnInfo ci1 = new ColumnInfo();
		// ci1.tablename = "user_info";
		// ci1.label = "ID";
		// ci1.type = Types.INTEGER;
		// columninfo_list.add(ci1);

		ColumnInfo ci2 = new ColumnInfo();
		ci2.tablename = "user_info";
		ci2.label = "NICK";
		ci2.type = Types.VARCHAR;
		columninfo_list.add(ci2);

		// ColumnInfo ci3 = new ColumnInfo();
		// ci3.tablename = "user_info";
		// ci3.label = "GMT_CREATE";
		// ci3.type = Types.TIMESTAMP;
		// columninfo_list.add(ci3);

		xx(columninfo_list);
	}

	public static void xx(List<ColumnInfo> columninfo_list) throws Exception {
		Map<Integer, String> connstr_map = new HashMap<Integer, String>();
		for (int i = 0; i < 1; i++) {
			connstr_map.put(i, "jdbc:mysql://172.16.1.19:3308/user");
		}

		Map<Integer, Connection> conn_map = Select.open_Conns(connstr_map);

		int column_size = columninfo_list.size();

		String string_sql = "select min(HEX(CONVERT(SUBSTR(<field>,1,1) USING 'utf16'))) min_field, max(HEX(CONVERT(SUBSTR(<field>,1,1) USING 'utf16'))) max_field, count(1) record_count from <table> where ID>1 and ID<10000";
		String int_sql = "select min(<field>) min_field, max(<field>) max_field, count(1) record_count from <table> where ID>1 and ID<10000";
		String timestamp_sql = "select min(<field>) min_field, max(<field>) max_field, count(1) record_count from <table> where ID>1 and ID<10000";
		for (int i = 0; i < column_size; i++) {
			ColumnInfo ci = columninfo_list.get(i);

			String execsql;

			switch (ci.type) {

			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:

				execsql = int_sql.replaceAll("<table>", ci.tablename)
						.replaceAll("<field>", ci.label);
				for (int j = 0; j < 1; j++) {
					String connstr = connstr_map.get(j);
					Connection conn = conn_map.get(j);

					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(execsql);
					Long min_field = null;
					Long max_field = null;
					long record_count = 0L;
					while (rs.next()) {
						min_field = rs.getLong(1);
						max_field = rs.getLong(2);
						record_count = rs.getInt(3);
					}

				}

				break;

			case Types.CHAR:
			case Types.VARCHAR:

				execsql = string_sql.replaceAll("<table>", ci.tablename)
						.replaceAll("<field>", ci.label);
				for (int j = 0; j < 1; j++) {
					String connstr = connstr_map.get(j);
					Connection conn = conn_map.get(j);

					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(execsql);
					String min_field = null;
					String max_field = null;
					long record_count = 0L;
					while (rs.next()) {
						min_field = rs.getString(1);
						max_field = rs.getString(2);
						int min = hexStringToInt(min_field);
						int max = hexStringToInt(max_field);
						record_count = rs.getInt(3);
					}

				}

				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				execsql = timestamp_sql.replaceAll("<table>", ci.tablename)
						.replaceAll("<field>", ci.label);
				for (int j = 0; j < 1; j++) {
					String connstr = connstr_map.get(j);
					Connection conn = conn_map.get(j);

					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(execsql);
					Timestamp min_field = null;
					Timestamp max_field = null;
					long record_count = 0L;
					while (rs.next()) {
						min_field = rs.getTimestamp(1);
						max_field = rs.getTimestamp(2);
						Long min = min_field.getTime();
						Long max = max_field.getTime();
						record_count = rs.getInt(3);
					}

				}

				break;

			}
		}
	}

}
