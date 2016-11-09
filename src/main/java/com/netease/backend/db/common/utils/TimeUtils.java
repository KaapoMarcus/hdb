package com.netease.backend.db.common.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TimeUtils {
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss");

	
	public static String checkTimeFormat2String(String strTime) {
		if (null == strTime) {
			return "";
		}

		strTime = strTime.trim();

		
		String dateExp = "(\\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\\d|3[01]))"
			+ "((\\s)(\\d|[01]\\d|2[0-3]):(\\d|[0-5]\\d):(\\d|[0-5]\\d))?";
		if (!strTime.matches(dateExp)) {
			return "";
		}

		String[] times = strTime.split("\\s");

		
		String[] tmp = times[0].split("-");
		int[] date = new int[3];
		for (int i = 0; i < tmp.length; i++) {
			date[i] = Integer.parseInt(tmp[i]);
		}
		switch (date[1]) {
		case 1 :
		case 3 :
		case 5 :
		case 7 :
		case 8 :
		case 10 :
		case 12 :
			if (31 < date[2]) {
				return "";
			}
			break;
		case 4 :
		case 6 :
		case 9 :
		case 11 :
			if (30 < date[2]) {
				return "";
			}
			break;
		case 2 :
			final int daysInMonth = 
				(0 == date[0] % 400) ? 29 : 
					(0 == date[0] % 100) ? 28 : 
						(0 == date[0] % 4) ? 29 : 28;
			if (daysInMonth < date[2]) {
				return "";
			}
			break;
		default :
			return "";
		}

		
		if (times.length == 1) {
			return strTime + " 00:00:00";
		}

		return strTime;
	}

	
	public static long formatTime2Long(String strTime) {
		Date date = formatTime2Date(strTime);
		if (date != null)
			return date.getTime();
		else
			return -1;
	}

	
	public static Date formatTime2Date(String strTime) {
		Date result = null;
		try {
			result = sdf.parse(strTime);
		} catch(ParseException e1) {
		}
		if (result == null) {
			try {
				result = stf.parse(strTime);
				Calendar c = Calendar.getInstance();
				Calendar now = Calendar.getInstance();
				c.setTimeInMillis(result.getTime());
				c.set(Calendar.YEAR, now.get(Calendar.YEAR));
				c.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR));
				result = new Date(c.getTimeInMillis());
			} catch(ParseException e2) {
			}
		}
		return result;
	}

	
	public static String formatTime(long time) {
		return formatTime(new Date(time));
	}

	
	public static String formatTime(Date time) {
		if(time!=null)
			return sdf.format(time);
		return "NULL";
	}

	
	public static String formatTimeConcise(long time) {
		Calendar thatTime = Calendar.getInstance();
		thatTime.setTimeInMillis(time);
		Calendar now = Calendar.getInstance();
		if (thatTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) 
				&& thatTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR))
			return stf.format(new Date(time));
		else
			return sdf.format(new Date(time));
	}

	
	public static String formatTimeInMS(double nanoTime) {
		if (nanoTime > 100000000)
			return "" + (long) nanoTime / 1000000;
		else if (nanoTime > 10000000) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			return nf.format(nanoTime / 1000000);
		} else {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			return nf.format(nanoTime / 1000000);
		}
	}

	
	public static String formatTimeInReadable(long millisec) {
		String timeStr = millisec + " ms";
		if (millisec > 60000) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			timeStr = nf.format((double)millisec / 60000) + " min";
		} else if (millisec > 1000) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			timeStr = nf.format((double)millisec / 1000) + " s";
		}
		return timeStr;
	}

	
	public static String getTimeFromInt(int time) {
		if (time < 0 || time >= 86400)
			throw new IllegalArgumentException("Invalid time '" + time + "'.");
		int hour = time / 3600;
		int mod = time % 3600;
		int min = mod / 60;
		String hourStr = hour + "";
		if (hour < 10)
			hourStr = "0" + hour;
		String minStr = min + "";
		if (min < 10)
			minStr = "0" + min;
		return hourStr + ":" + minStr;
	}

	
	public static int getTimeFromStr(String time, boolean addSeconds) {
		String[] strs = time.split(":");
		if (strs.length < 2)
			throw new IllegalArgumentException("Invalid time '" + time + "'.");
		try {
			int hour = Integer.parseInt(strs[0]);
			int min = Integer.parseInt(strs[1]);
			int result = hour * 3600 + min * 60;
			if (addSeconds)
				result += 59;
			if (hour >=0 && hour <= 24 && min >= 0 && min < 60 && result >= 0 && result < 86400)
				return result;
			throw new IllegalArgumentException();
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid time '" + time + "'.");
		}
	}

}
