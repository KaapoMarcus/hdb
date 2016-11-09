
package com.netease.backend.db.common.utils;

import java.util.Collection;


public class Validator {
	
	public static boolean isIpAddress(String str) {
		String[] segs = str.split("\\.");
		if (segs.length != 4)
			return false;
		try {
			for (int i = 0; i < segs.length; i++) {
				int a = Integer.parseInt(segs[i]);
				if (a < 0 || a > 255)
					return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	
	public static boolean isIpWildcard(String str) {
		str = str.trim();
		if (str.equals("%"))
			return true;
		else {
			String[] segs = str.split("\\.");
			if (segs.length > 4)
				return false;
			try {
				for (int i = 0; i < segs.length - 1; i++) {
					if (!isIpAddressSegment(segs[i]))
						return false;
				}
				if (segs[segs.length - 1].equals("%")
						|| (segs.length == 4 && isIpAddressSegment(segs[segs.length - 1])))
					return true;
				return false;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	private static boolean isIpAddressSegment(String seg) {
		int a = Integer.parseInt(seg);
		return (a >=0 && a <= 255);
	}
	
	
	public static boolean isHostname(String host) {
		for (int i = 0; i < host.length(); i++) {
			char ch = host.charAt(i);
			if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '-' || ch == '_'
					|| ch == '.')
				continue;
			return false;
		}
		return true;
	}

	
	public static boolean isPort(int port) {
		return (port >= 0 && port < 65535);
	}
	
	
	public static int isPort(String portStr) {
		try {
			int port = Integer.parseInt(portStr);
			if (isPort(port))
				return port;
			else
				return -1;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	
	public static boolean isIpWithOptionalPort(String s) {
		int pos = s.indexOf(':');
		if (pos > 0) {
			return isIpAddress(s.substring(0, pos)) && isPort(s.substring(pos + 1)) >= 0;
		} else
			return isIpAddress(s);
	}
	
    
    public static boolean isStrSetEquals(Collection<?> a, Collection<?> b) {
		if (null == a && null == b) 
			return true;
		if ((null == a && null != b) || (null != a && null == b)) 
			return false;
		if (a.size() != b.size()) 
			return false;
		for (Object o : a) {
			if (!b.contains(o)) 
				return false;
		}
		for (Object o : b) {
			if (!a.contains(o)) 
				return false;
		}
		return true;
	}
}
