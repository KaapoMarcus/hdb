package com.netease.backend.db.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.netease.backend.db.common.management.dbi.memcached.MemcachedConfig;


public class MemcachedUtils {

	
	public static List<InetSocketAddress> parseMemcachedServerAddress(
			String addrListStr) throws IllegalArgumentException {

		List<Pair<String, Integer>> addrList = MemcachedConfig
				.checkServerAddrList(addrListStr);
		List<InetSocketAddress> soAddrList = new ArrayList<InetSocketAddress>(
				addrList.size());
		try {
			for (Pair<String, Integer> pair : addrList) {
				soAddrList.add(new InetSocketAddress(pair.getFirst(), pair
						.getSecond()));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"parse memcached server addrsss failed.", e);
		}
		return soAddrList;
	}

	
	public static void setSpyLogLevel(Level level) {
		Properties systemProperties = System.getProperties();
		systemProperties.put("net.spy.log.LoggerImpl",
				"net.spy.memcached.compat.log.Log4JLogger");
		System.setProperties(systemProperties);
		Logger.getLogger("net.spy.memcached").setLevel(level);
	}
	
	
	public static Object getObjectFromString(String stringVal, int jdbcType)
			throws NumberFormatException, IllegalArgumentException {
		if (stringVal == null)
			return null;
		
		switch (jdbcType) {
		case Types.BOOLEAN:
		case Types.BIT:
			return new Boolean(Short.parseShort(stringVal) != 0);
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
			return new Integer(Integer.parseInt(stringVal));
		case Types.BIGINT:
			return new BigInteger(stringVal);
		case Types.FLOAT:
		case Types.DOUBLE:
			return new Double(stringVal);
		case Types.REAL:
			return new Float(stringVal);
		case Types.DECIMAL:
		case Types.NUMERIC:
			return new BigDecimal(stringVal);
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return stringVal;
		case Types.DATE:
			return new Date(Long.parseLong(stringVal));
		case Types.TIMESTAMP:
			return new Timestamp(Long.parseLong(stringVal));
		case Types.TIME:
			return new Time(Long.parseLong(stringVal));
		




		default:
			throw new IllegalArgumentException("unsupported jdbc type:" + jdbcType);
		}
	}
	
	
	public static String getStringValueFromResultSet(ResultSet rs,
			String columnName, int jdbcType) throws SQLException {
		if (rs == null)
			throw new NullPointerException("resultset is null.");

		switch (jdbcType) {
		case Types.DATE:
			Date date = rs.getDate(columnName);
			if (date == null)
				return null;
			return String.valueOf(date.getTime());
		case Types.TIMESTAMP:
			Timestamp ts = rs.getTimestamp(columnName);
			if (ts == null)
				return null;
			return String.valueOf(ts.getTime());
		case Types.TIME:
			Time t = rs.getTime(columnName);
			if (t == null)
				return null;
			return String.valueOf(t.getTime());
		default:
			return rs.getString(columnName);
		}
	}
}
