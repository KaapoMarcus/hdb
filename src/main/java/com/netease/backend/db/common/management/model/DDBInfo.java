
package com.netease.backend.db.common.management.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.utils.DbUtils;


public class DDBInfo {

	
	private String name;
	
	private String aliasName;
	
	private String cName;
	
	private String ip = "127.0.0.1";
	
	private int port = 8888;
	
	private int dbaPort = 7777;
	
	private String hostName;
	
	private String desc;

	
	public DDBInfo(String name) {
		this.name = name;
		this.aliasName = name;
		this.cName = name;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getCName() {
		return cName;
	}

	public void setCName(String cName) {
		this.cName = cName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getDbaPort() {
		return dbaPort;
	}

	public void setDbaPort(int dbaPort) {
		this.dbaPort = dbaPort;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	
	public String toString() {
		return (cName == null ? "" : cName) + "[" + ip + ":" + dbaPort + "]";
	}

	
	public static List<DDBInfo> getDDBInfosFromConfigDB(String url) 
	throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<DDBInfo> result = new ArrayList<DDBInfo>();
		try {
			conn = DbUtils.getMysqlConnection(url, 3);
			pst = conn.prepareStatement("SELECT Name,Alias,CName,IP,MasterPort,DbaPort," +
										"HostName,Detail FROM DatabaseInfo WHERE DDB=1 ORDER BY Alias");
			rs = pst.executeQuery();
			while (rs.next()) {
				DDBInfo info = new DDBInfo(rs.getString(1));
				info.setAliasName(rs.getString(2));
				info.setCName(rs.getString(3));
				info.setIp(rs.getString(4));
				info.setPort(rs.getInt(5));
				info.setDbaPort(rs.getInt(6));
				info.setHostName(rs.getString(7));
				info.setDesc(rs.getString(8));
				result.add(info);
			}
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != pst)
					pst.close();
				if (null != conn)
					conn.close();
			} catch (SQLException e) {}
		}
	}
}
