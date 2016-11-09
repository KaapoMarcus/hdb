package com.jhh.hdb.proxyserver.codec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComBufferCache {
	private static final Map<String, List<byte[]>> CACHE_MAP = new ConcurrentHashMap<String, List<byte[]>>();
	
	public static void put(String key, List<byte[]> buffer) {
		//统一用大写
		CACHE_MAP.put(key.toUpperCase(), buffer);
	}
	
	public static List<byte[]> get(String key) {
		//统一用大写
		return CACHE_MAP.get(key.toUpperCase());
	}
}
