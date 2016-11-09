package com.netease.backend.db.common.management.dbi.memcached;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.netease.backend.db.common.codec.DecodeException;
import com.netease.backend.db.common.codec.EncodeException;


public interface MemcachedConnection {

	
	public long set(String key, int[] fieldNumbers, List<Object> record)
			throws EncodeException, MemExecutionException, TimeoutException;

	
	public long setMulti(List<String> keys, int[] fieldNumbers,
			List<List<Object>> records) throws EncodeException,
			MemExecutionException, TimeoutException;

	
	public long setMulti(List<String> keys, List<int[]> fieldNumbers,
			List<List<Object>> records) throws EncodeException,
			MemExecutionException, TimeoutException;

	
	public List<Object> get(String key, int[] fieldNumbers)
			throws DecodeException, MemExecutionException, TimeoutException;

	
	public Map<String, List<Object>> getMulti(List<String> keys, int[] fieldNumbers)
			throws DecodeException, MemExecutionException, TimeoutException;

	
	public Map<String, List<Object>> getMulti(List<String> keys, List<int[]> fieldNumbers)
			throws DecodeException, MemExecutionException, TimeoutException;

	
	public void delete(String key) throws MemExecutionException,
			TimeoutException;

	
	public void deleteMulti(List<String> keys) throws MemExecutionException,
			TimeoutException;
}
