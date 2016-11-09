package com.jhh.hdb.proxyserver.utils;

import java.util.Arrays;
import java.util.List;

import com.netease.backend.db.DBDriver;

public class ServerUtil {

	/**同时访问多个DDB，则需要用双竖线'$$'进行分隔*/
	public static final String DB_SEPARATOR = "\\$\\$";

	/**
	 * 返回适用于查询服务器的url
	 * @param url
	 * @param clientIp
	 * @param seed
	 * @param qsport
	 * @return
	 */
	public static String getQsConnectUrl(String url, String clientIp,
			String seed, int qsport) {
		StringBuilder sb = new StringBuilder(url);

		sb.append(url.contains("?") ? "&" : "?");
		sb.append(DBDriver.DRIVER_PROP_QS_CLIENT);
		sb.append("=");
		sb.append(clientIp);
		sb.append("&");
		sb.append(DBDriver.DRIVER_PROP_QS_SEED);
		sb.append("=");
		sb.append(seed);
		sb.append("&");
		sb.append(DBDriver.DRIVER_PROP_QS_PORT);
		sb.append("=");
		sb.append(qsport);

		return sb.toString();
	}

	/**
	 * 将一组url转成一个适用于查询服务器的url
	 * @param urls
	 * @param clientIp
	 * @param seed
	 * @param qsPort
	 * @return
	 */
	public static String getQsConnectUrl(List<String> urls, String clientIp,
			String seed, int qsPort) {
		StringBuilder sb = new StringBuilder();
		for (String url : urls) {
			sb.append(getQsConnectUrl(url, clientIp, seed, qsPort));
			sb.append(";");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 把合在一块的数据库名称拆分开
	 * @param dbName
	 * @return
	 */
	public static List<String> separateDatabase(String dbName) {
		if (dbName == null)
			return null;
		String[] dbNames = dbName.split(DB_SEPARATOR);
		return Arrays.asList(dbNames);
	}

	/**
	 * 去除纳秒值
	 * @param time
	 * @return
	 */
	public static String trimNano(String time) {
		int pos = time.indexOf(".");
		if (pos != -1)
			return time.substring(0, pos);
		else
			return time;
	}
}
