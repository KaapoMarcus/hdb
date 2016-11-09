
package com.netease.backend.db.common.ha;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class ZooKeeperDefinition {

	public static final String ZK_PROTOCOL_HEADER = "zookeeper:
	public static final String ZK_FULLPATH_PREFIX = "/ddb";
	public static final String MASTER_TOKEN = "/master";

	public static String getMasterPath(String ddbName) {
		return ZK_FULLPATH_PREFIX + ("/" + ddbName) + MASTER_TOKEN;
	}

	public static String getDDBName(String nodePath) throws HAException {
		if (!nodePath.matches("^" + ZK_FULLPATH_PREFIX + "/.*" + MASTER_TOKEN
				+ "/.*")) {
			throw new HAException("invalid node path: " + nodePath);
		}

		int startIndex = nodePath.indexOf("/", 1);
		int endIndex = nodePath.indexOf("/", startIndex + 1);
		String ddbName = nodePath.substring(startIndex + 1, endIndex);

		return ddbName;
	}

	
	public static String generateNodeData(String ip, int dbaPort, int dbiPort) {
		String data = ip + ":" + dbiPort + "-" + dbaPort;
		return data;
	}

	
	public static String getDBIConnStr(String rawURL) throws HAException {
		rawURL = rawURL.trim();

		if (!rawURL.matches("^.*:\\d{1,5}-\\d{1,5}$")) {
			throw new HAException("Invalid new ddb master string: '" + rawURL
					+ "'!");
		}

		String[] segs = rawURL.split(":");
		String ip = segs[0];
		
		String[] ports = segs[1].split("-");
		String dbiPort = ports[0];
		String url = ip + ":" + dbiPort;

		return url;
	}

	
	public static String getDBAConnStr(String rawURL) throws HAException {
		rawURL = rawURL.trim();

		if (!rawURL.matches("^.*:\\d{1,5}-\\d{1,5}$")) {
			throw new HAException("Invalid new ddb master string: '" + rawURL
					+ "'!");
		}

		String[] segs = rawURL.split(":");
		String ip = segs[0];
		
		String[] ports = segs[1].split("-");
		String dbaPort = ports[1];
		String url = ip + ":" + dbaPort;

		return url;
	}

	public static MasterInfo getMasterInfo(String rawURL) throws HAException {
		rawURL = rawURL.trim();

		if (!rawURL.matches("^.*:\\d{1,5}-\\d{1,5}$")) {
			throw new HAException("Invalid new ddb master string: '" + rawURL
					+ "'!");
		}

		String[] segs = rawURL.split(":");
		String ip = segs[0];

		String[] ports = segs[1].split("-");

		return new MasterInfo(ip, Integer.parseInt(ports[0]), Integer
				.parseInt(ports[1]));
	}

	public static String getGeneralServerListPath() {
		return "/serverlist";
	}

	
	public static String electLeader(List<String> children) {
		if (children.size() == 0) {
			return null;
		} else if (children.size() == 1) {
			return children.get(0);
		}
		
		List<String> localChildren = new LinkedList<String>();
		localChildren.addAll(children);
		Collections.sort(localChildren);
		return localChildren.get(0);
	}
}
