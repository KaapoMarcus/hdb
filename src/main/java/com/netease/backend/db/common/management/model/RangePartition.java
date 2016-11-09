package com.netease.backend.db.common.management.model;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;


public class RangePartition implements Serializable {
	
	private static final long serialVersionUID = 4675031130236921247L;
	
	
	private String sql;
	
	
	private String partitionField;
	
	
	private TreeMap<String, String> partitions;
	
	public RangePartition(String sql, String field, TreeMap<String, String> partitions) {
		this.sql = sql;
		this.partitionField = field;
		this.partitions = partitions;

	}
	
	public String getSql() {
		return this.sql;
	}
	
	public String getPartitionField() {
		return this.partitionField;
	}
	
	
	public TreeMap<String, String> getPartitions() {
		return this.partitions;
	}
	
	
	public TreeMap<String, String> needAddPartitions(int count) throws ParseException {
		
		if (partitions == null || partitions.size() < 1)
			throw new IllegalArgumentException("No partitions!");
		
		if (partitions.size() < 2) {
			throw new IllegalArgumentException("Table's partition count should be >= 2");
		}
		int index = 0;
		long day = 24 * 3600000l;
		long first = -1;
		long interval = -1;
		long firstName = 0;

		int nameInterval = 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		String currentPartition = "p" + sdf.format(new Date());
		Iterator<String> it = partitions.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String nameValue = partitions.get(key);
			long p = 0L;
			try {
				p = Long.parseLong(key);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("invalid range value: " + key);
			}
			
			if (-1 == first) {
				first = p;

				firstName = sdf.parse(nameValue.substring(1)).getTime();
			} else if (-1 == interval) {
				interval = p - first;
				nameInterval = (int) ((sdf.parse(nameValue.substring(1)).getTime() - firstName) / day);

			}
			
			String pName = partitions.get(key);
			if (currentPartition.compareTo(pName) >= 0) 
				++index;
			if (currentPartition.compareTo(pName) < 0 && -1 != interval)
				break;
		}
		
		int hasCount = partitions.size() - index;	
		if (hasCount >= count)
			return null;
		else {
			String lastKey = partitions.lastKey();
			long last = Long.parseLong(lastKey);
			String lastName = partitions.get(lastKey).substring(1);
			TreeMap<String, String> results = new TreeMap<String, String>();
			while (hasCount < count) {
				last = last + interval;
				Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(lastName));
				c.add(Calendar.DAY_OF_MONTH, nameInterval);
				lastName = sdf.format(c.getTime());
				results.put("" + last, "p" + lastName);
				if (currentPartition.compareTo("p" + lastName) < 0)
					hasCount++;
			}
			return results;
		}
	}
	
	
	public List<String> needDeletePartitions(int count) throws ParseException {
		
		if (partitions == null || partitions.size() < 1)
			throw new IllegalArgumentException("No partitions!");
		
		if (partitions.size() < 2) {
			throw new IllegalArgumentException("Table's partition count should be >= 2");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentPartition = "p" + sdf.format(new Date());
		
		String before = null;
		List<String> names = new ArrayList<String>();
		Iterator<String> it = partitions.values().iterator();
		while (it.hasNext()) {
			if (names.size() >= count)
				break;
			String p = it.next();
			if (before == null) {
				before = p;
			} else if (currentPartition.compareTo(p) >= 0) {
				names.add(before);
				before = p;
			} else {
				break;
			}
		}
		return names;
	}
	












































	
	
	public static boolean checkValidPartitionNames(Collection<String> pnames) {
		Iterator<String> it = pnames.iterator();
		while (it.hasNext()) {
			String name = it.next();
			if (checkValidPartitionName(name)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	public static boolean checkValidPartitionName(String name) {
		if (name.length() != 9) {
			return false;
		}
		if (name.startsWith("p")) {
			String tmp = name.substring(1);
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				sdf.parse(tmp);
			} catch (ParseException e) {
				return false;
			}
			return true;
		}
		return false;
	}
}
