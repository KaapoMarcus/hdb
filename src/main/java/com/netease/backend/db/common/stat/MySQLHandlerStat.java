package com.netease.backend.db.common.stat;

import java.io.Serializable;


public class MySQLHandlerStat implements Serializable {
	private static final long serialVersionUID = 8060592532987157741L;
	
	public static final String HANDLER_READ_FIRST_NAME = "Handler_read_first";
	public static final String HANDLER_READ_KEY_NAME = "Handler_read_key";
	public static final String HANDLER_READ_NEXT_NAME = "Handler_read_next";
	public static final String HANDLER_READ_PREV_NAME = "Handler_read_prev";
	public static final String HANDLER_READ_RND_NAME = "Handler_read_rnd";
	public static final String HANDLER_READ_RND_NEXT_NAME = "Handler_read_rnd_next";
	public static final String SHOW_STATUS_CMD = "show status like 'Handler_read_%'";
	public static final int SHOW_SELF_RND_NEXT = 0;	
	public static final int HANDLER_READ_STATUS_COUNT = 6;
	
	
	public long read_first;
	
	public long read_key;
	
	public long read_next;
	
	public long read_prev;
	
	public long read_rnd;
	
	public long rnd_next;
	
	public MySQLHandlerStat diff(MySQLHandlerStat another) {
		MySQLHandlerStat diff = new MySQLHandlerStat();
		diff.read_first = read_first - another.read_first;
		diff.read_key = read_key - another.read_key;
		diff.read_next = read_next - another.read_next;
		diff.read_prev = read_prev - another.read_prev;
		diff.read_rnd = read_rnd - another.read_rnd;
		diff.rnd_next = rnd_next - another.rnd_next;
		
		
		if(diff.rnd_next < MySQLHandlerStat.HANDLER_READ_STATUS_COUNT+1)
			diff.rnd_next = 0;
		else
			diff.rnd_next = diff.rnd_next -(MySQLHandlerStat.HANDLER_READ_STATUS_COUNT+1);
		return diff;
	}
}
