package com.netease.backend.db.common.sql.plan;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class CronExpression implements Serializable {
	private static final long serialVersionUID = -8269150353691869206L;
	
	private static final int TYPE_SECOND = 0;
	private static final int TYPE_MINUTE = 1;
	private static final int TYPE_HOUR = 2;
	private static final int TYPE_DAY_OF_MONTH = 3;
	private static final int TYPE_MONTH = 4;
	private static final int TYPE_DAY_OF_WEEK = 5;
	private static final int TYPE_YEAR = 6;
	private static final int VALUE_STAR = 100;     
	private static final int VALUE_QUESTION = 101; 
	private static Map<String, Integer> monthMap = new HashMap<String, Integer>(20);
	private static Map<String, Integer> dayMap = new HashMap<String, Integer>(12);
	static {
		monthMap.put("JAN", 1);
		monthMap.put("FEB", 2);
		monthMap.put("MAR", 3);
		monthMap.put("APR", 4);
		monthMap.put("MAY", 5);
		monthMap.put("JUN", 6);
		monthMap.put("JUL", 7);
		monthMap.put("AUG", 8);
		monthMap.put("SEP", 9);
		monthMap.put("OCT", 10);
		monthMap.put("NOV", 11);
		monthMap.put("DEC", 12);
		
		dayMap.put("SUN", 1);
		dayMap.put("MON", 2);
		dayMap.put("TUE", 3);
		dayMap.put("WED", 4);
		dayMap.put("THU", 5);
		dayMap.put("FRI", 6);
		dayMap.put("SAT", 7);
	}
	
	private String cronString;
	private transient boolean lastDayOfMonth;
	private transient boolean lastDayOfWeek;
	private transient int nthDayOfWeek;
	
	private transient TreeSet<Integer> seconds;
	private transient TreeSet<Integer> minutes;
	private transient TreeSet<Integer> hours;
	private transient TreeSet<Integer> daysOfMonth;
	private transient TreeSet<Integer> months;
	private transient TreeSet<Integer> daysOfWeek;
	private transient TreeSet<Integer> years;
	
	public CronExpression(String cronString) throws ParseException {
		this.cronString = cronString;
		parseCronString();
	}
	
	public String getCronString() {
		return cronString;
	}
	
	protected Date getDateAfter(Date dateAfter) {
		Calendar cl = Calendar.getInstance();
		dateAfter = new Date(dateAfter.getTime() + 1000);
		
		cl.setTime(dateAfter);
		cl.set(Calendar.MILLISECOND, 0);
		
		boolean result = false;
		while (!result) {
			if(cl.get(Calendar.YEAR) > 2099) 
				return null;
			
			SortedSet<Integer> st = null;
			
			
			int sec = cl.get(Calendar.SECOND);
			st = seconds.tailSet(sec);
			if (null != st && st.size() != 0) 
				sec = st.first();
			else {
				sec = seconds.first();
				cl.add(Calendar.MINUTE, 1);
			}
			cl.set(Calendar.SECOND, sec);
			
			
			int min = cl.get(Calendar.MINUTE);
			int hr = cl.get(Calendar.HOUR_OF_DAY);
			int tmp = -1;
			st = minutes.tailSet(min);
			if (null != st && st.size() != 0) {
				tmp = min;
				min = st.first();
			} else {
				min = minutes.first();
				hr++;
			}
			if (min != tmp) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, min);
				cl.set(Calendar.HOUR_OF_DAY, hr);
				continue;
			}
			cl.set(Calendar.MINUTE, min);
			
			
			hr = cl.get(Calendar.HOUR_OF_DAY);
			int day = cl.get(Calendar.DAY_OF_MONTH);
			tmp = -1;
			st = hours.tailSet(hr);
			if (null != st && st.size() != 0) {
				tmp = hr;
				hr = st.first();
			} else {
				hr = hours.first();
				day++;
			}
			if (hr != tmp) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, 0);
				cl.set(Calendar.DAY_OF_MONTH, day);
				cl.set(Calendar.HOUR_OF_DAY, hr);
				continue;
			}
			cl.set(Calendar.HOUR_OF_DAY, hr);
			
			
			day = cl.get(Calendar.DAY_OF_MONTH);
			int mon = cl.get(Calendar.MONTH) + 1; 
			tmp = -1;
			boolean isDayOfMonth = !daysOfMonth.contains(VALUE_QUESTION);
			boolean isDayOfWeek = !daysOfWeek.contains(VALUE_QUESTION);
			if (isDayOfMonth && !isDayOfWeek) { 
				st = daysOfMonth.tailSet(day);
				if (lastDayOfMonth) { 
					tmp = day;
					day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
				} else if (null != st && st.size() != 0) {
					tmp = day;
					day = st.first();
				} else {
					day = daysOfMonth.first();
					mon++;
				}
				
				if (day != tmp) {
					cl.set(Calendar.SECOND, 0);
					cl.set(Calendar.MINUTE, 0);
					cl.set(Calendar.HOUR_OF_DAY, 0);
					cl.set(Calendar.DAY_OF_MONTH, day);
					cl.set(Calendar.MONTH, mon - 1);
					continue;
				}
			} else if (isDayOfWeek && !isDayOfMonth) { 
				if (lastDayOfWeek) { 
					int dow = daysOfWeek.first();
					int cDow = cl.get(Calendar.DAY_OF_WEEK); 
					int daysToAdd = 0;
					if (cDow < dow) 
						daysToAdd = dow - cDow;
					else if (cDow > dow) 
						daysToAdd = dow + (7 - cDow);
					
					int lastDom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
					if (day + daysToAdd > lastDom) { 
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, 1);
						cl.set(Calendar.MONTH, mon); 
						continue;
					}
					while ((day + daysToAdd + 7) <= lastDom) 
						daysToAdd += 7;
					day += daysToAdd;   
					if (daysToAdd > 0) {
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, day);
						cl.set(Calendar.MONTH, mon - 1);
						continue;
					}
				} else if (nthDayOfWeek != 0) { 
					int dow = daysOfWeek.first();
					int cDow = cl.get(Calendar.DAY_OF_WEEK); 
					int daysToAdd = 0;
					if (cDow < dow) 
						daysToAdd = dow - cDow;
					else if (cDow > dow) 
						daysToAdd = dow + (7 - cDow);
					
					boolean dayShifted = false;
					if (daysToAdd > 0) 
						dayShifted = true;
					
					day += daysToAdd;
					int weekOfMonth = day / 7;
					if (day % 7 > 0) 
						weekOfMonth++;
					
					daysToAdd = (nthDayOfWeek - weekOfMonth) * 7;
					day += daysToAdd;
					if (daysToAdd < 0
							|| day > getLastDayOfMonth(mon, cl
									.get(Calendar.YEAR))) {
						
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, 1);
						cl.set(Calendar.MONTH, mon); 
						continue;
					} else if (daysToAdd > 0 || dayShifted) {
						
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, day);
						cl.set(Calendar.MONTH, mon - 1);
						continue;
					}
				} else {
					int cDow = cl.get(Calendar.DAY_OF_WEEK); 
					int dow = daysOfWeek.first();
					st = daysOfWeek.tailSet(cDow);
					if (null != st && st.size() > 0) 
						dow = st.first();
					
					int daysToAdd = 0;
					if (cDow < dow) 
						daysToAdd = dow - cDow;
					if (cDow > dow) 
						daysToAdd = dow + (7 - cDow);
					
					int lastDom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
					if (day + daysToAdd > lastDom) { 
						
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, 1);
						cl.set(Calendar.MONTH, mon); 
						continue;
					} else if (daysToAdd > 0) {
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, day + daysToAdd);
						cl.set(Calendar.MONTH, mon - 1);
						continue;
					}
				}
			} else 
				throw new IllegalArgumentException("cronʱ����ʽ���պ����ڱ�����һ������Ϊ'?'��");
			cl.set(Calendar.DAY_OF_MONTH, day);
			
			
			mon = cl.get(Calendar.MONTH) + 1;
			int year = cl.get(Calendar.YEAR);
			if (year > 2099) 
				return null;
			tmp = -1;
			st = months.tailSet(mon);
			if (null != st && st.size() != 0) {
				tmp = mon;
				mon = st.first();
			} else {
				mon = months.first();
				year++;
			}
			if (mon != tmp) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, 0);
				cl.set(Calendar.HOUR_OF_DAY, 0);
				cl.set(Calendar.DAY_OF_MONTH, 1);
				cl.set(Calendar.MONTH, mon - 1);
				cl.set(Calendar.YEAR, year);
				continue;
			}
			cl.set(Calendar.MONTH, mon - 1);
			
			
			year = cl.get(Calendar.YEAR);
			tmp = -1;
			st = years.tailSet(year);
			if (null != st && st.size() != 0) {
				tmp = year;
				year = st.first();
			} else 
				return null; 
			if (year != tmp) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, 0);
				cl.set(Calendar.HOUR_OF_DAY, 0);
				cl.set(Calendar.DAY_OF_MONTH, 1);
				cl.set(Calendar.MONTH, 0);
				cl.set(Calendar.YEAR, year);
				continue;
			}
			cl.set(Calendar.YEAR, year);
			
			result = true;
		} 
		
		return cl.getTime();
	}
	
	private void parseCronString() throws ParseException {
		if (null == seconds)
			seconds = new TreeSet<Integer>();
		if (null == minutes)
			minutes = new TreeSet<Integer>();
		if (null == hours)
			hours = new TreeSet<Integer>();
		if (null == daysOfMonth)
			daysOfMonth = new TreeSet<Integer>();
		if (null == months)
			months = new TreeSet<Integer>();
		if (null == daysOfWeek)
			daysOfWeek = new TreeSet<Integer>();
		if (null == years)
			years = new TreeSet<Integer>();
		try {
			String[] crons = cronString.split(" +");
			if (crons.length < 6 || crons.length > 7)
				throw new ParseException("cronʱ����ʽ����Ԫ�ظ���Ϊ6����7����", cronString.length());
			for (int i = 0; i < crons.length; i++) {
				String[] e = crons[i].split(",");
				for (int j = 0; j < e.length; j++) {
					parseToken(e[j].toUpperCase(), i);
				}
			}
			if (crons.length == 6)
				storeToSet(VALUE_STAR, -1, 1, TYPE_YEAR);
			if (daysOfMonth.contains(VALUE_QUESTION) == daysOfWeek.contains(VALUE_QUESTION))
				throw new ParseException("cronʱ����ʽ�����պ�����ì�ܣ����ҽ�һ��ָ��'?'��", cronString.length());
		} catch(ParseException e) {
			throw e;
		} catch(Exception e) {
			throw new ParseException("cronʱ����ʽ��ʽ����" + e.toString(), 0);
		}
	}
	
	private void parseToken(String expr, int type) throws ParseException {
		int i = 0;
		char c = expr.charAt(i);
		if (c == '*') {
			if (expr.length() > i + 1) {
				i++;
				c = expr.charAt(i);
				if (c != '/')
					throw new ParseException("cron���ʽ����" + expr, i);
				i++;
				int incr = Integer.parseInt(expr.substring(i));
				storeToSet(VALUE_STAR, -1, incr, type);
			} else {
				storeToSet(VALUE_STAR, VALUE_STAR, 1, type);
			}
		} else if (c >= '0' && c <= '9') {
			int start = -1;
			int end = -1;
			int incr = 1;
			int p = 0;
			i++;
			while (i < expr.length()) {
				c = expr.charAt(i);
				if (c >= '0' && c <= '9') {
					i++;
					continue;
				} else if (c == '-') {
					start = Integer.parseInt(expr.substring(0, i));
					i++;
					p = i;
					continue;
				} else if (c == '/') {
					if (p != 0)
						end = Integer.parseInt(expr.substring(p, i));
					else {
						start = Integer.parseInt(expr.substring(0, i));
						end = getMaxValue(type);
					}
					i++;
					incr = Integer.parseInt(expr.substring(i));
				} else if (c == '#') {
					if (type != TYPE_DAY_OF_WEEK)
						throw new ParseException("'#'ʹ������" + expr, i);
					if (p != 0)
						throw new ParseException("'#'ʹ�����󣬲�ͬʱ֧��'-'��" + expr, i);
					else {
						start = Integer.parseInt(expr.substring(0, i));
						end = start;
					}
					i++;
					nthDayOfWeek = Integer.parseInt(expr.substring(i));
					if (nthDayOfWeek > 5)
						throw new ParseException("���ڸ�������" + expr, i);
				} else if (c == 'L') {
					if (type == TYPE_DAY_OF_WEEK)
						lastDayOfWeek = true;
					else
						throw new ParseException("'L'ʹ������" + expr, i);
					if (p != 0)
						end = Integer.parseInt(expr.substring(p, i));
					else {
						start = Integer.parseInt(expr.substring(0, i));
						end = start;
					}
					i++;
					if (expr.length() != i)
						throw new ParseException("'L'ʹ������" + expr, i);
				} else
					throw new ParseException("cronʱ����ʽ����" + expr, i);
			}
			if (-1 == start)
				start = Integer.parseInt(expr);
			if (-1 == end) {
				if (p > 0)
					end = Integer.parseInt(expr.substring(p));
				else
					end = start;
			}
			storeToSet(start, end, incr, type);
		} else if (c >= 'A' && c <= 'Z') {
			if (expr.equals("L")) {
				if (type == TYPE_DAY_OF_MONTH) {
					lastDayOfMonth = true;
					storeToSet(1, 1, 1, TYPE_DAY_OF_MONTH);
				} else if (type == TYPE_DAY_OF_WEEK) {
					lastDayOfWeek = true;
					storeToSet(1, 1, 1, TYPE_DAY_OF_WEEK);
				} else
					throw new ParseException("'L'ֻ�������ջ�����Ԫ�أ�" + expr, 0);
			} else {
				int start, end;
				int incr = 1;
				i = i + 3;
				String sStr = expr.substring(0, i);
				if (type == TYPE_MONTH) {
					start = getMonthNumber(sStr);
					if (-1 == start)
						throw new ParseException("������·�" + expr, i);
				} else if (type == TYPE_DAY_OF_WEEK) {
					start = getDayOfWeekNumber(sStr);
					if (-1 == start)
						throw new ParseException("���������" + expr, i);
				} else 
					throw new ParseException("'" + expr + "'ʹ������'", 0);
				end = start;
				if (expr.length() == i) {
					storeToSet(start, end, incr, type);
					return;
				}
				c = expr.charAt(i);
				if (c == '-') {
					i++;
					String eStr = expr.substring(i, i + 3);
					if (type == TYPE_MONTH) {
						end = getMonthNumber(eStr);
						if (-1 == end)
							throw new ParseException("������·�" + expr, i);
					} else {
						end = getDayOfWeekNumber(eStr);
						if (-1 == end)
							throw new ParseException("���������" + expr, i);
					}
					i = i + 3;
					if (expr.length() > i)
						c = expr.charAt(i);
				}
				if (c == 'L') {
					if (type == TYPE_DAY_OF_WEEK)
						lastDayOfWeek = true;
					else
						throw new ParseException("'L'ֻ�������ջ�����Ԫ�أ�" + expr, 0);
					if (expr.length() > i + 1)
						throw new ParseException("'" + expr + "'ʹ������'" + expr,
								0);
				}
				if (c == '/') {
					i++;
					incr = Integer.parseInt(expr.substring(i));
				}
				if (c == '#') {
					if (type != TYPE_DAY_OF_WEEK)
						throw new ParseException("'#'ʹ������" + expr, i);
					i++;
					nthDayOfWeek = Integer.parseInt(expr.substring(i));
					if (nthDayOfWeek > 5)
						throw new ParseException("���ڸ�������" + expr, i);
				}
				storeToSet(start, end, incr, type);
			}
		} else if (c == '?') {
			checkValue(VALUE_QUESTION, type);
			if (expr.length() > 1)
				throw new ParseException("?ʹ������" + expr, 0);
			storeToSet(VALUE_QUESTION, VALUE_QUESTION, 1, type);
		} else {
			throw new ParseException("cronʱ����ʽ����" + expr, 0);
		}
	}
	
	private void storeToSet(int start, int end, int incr, int type) throws ParseException {
		Set<Integer> storeSet = getStoreSet(type);
		if (storeSet == null)
			throw new ParseException("�Ƿ�����" + type + "��", 0);
		checkIncrement(incr, type);

		if (start == VALUE_STAR) {
			start = getMinValue(type);
			end = getMaxValue(type);

		}
		if (storeSet.size() > 0) {
			if ((type == TYPE_DAY_OF_MONTH && lastDayOfMonth) 
					|| (type == TYPE_DAY_OF_WEEK && lastDayOfWeek))
				throw new ParseException("cronʱ����ʽ����'L'ʹ������", 0);
			if (type == TYPE_DAY_OF_WEEK && nthDayOfWeek > 0)
				throw new ParseException("cronʱ����ʽ��������Ԫ������", 0);
		}
		if (-1 == end)
			end = getMaxValue(type);
		if (start > end)
			throw new ParseException("cronʱ����ʽ���󣬷�Χ��С����ȷ��", 0);
		for (int i = start; i <= end; i += incr) {
			checkValue(i, type);

			if (storeSet.contains(VALUE_QUESTION))
				throw new ParseException("cronʱ����ʽ���󣬶���ͨ��������ָ��ʱ��ì�ܣ�", 0);
			storeSet.add(i);
		}
	}
	
	private Set<Integer> getStoreSet(int type) {
		switch (type) {
		case TYPE_SECOND:
			return seconds;
		case TYPE_MINUTE:
			return minutes;
		case TYPE_HOUR:
			return hours;
		case TYPE_DAY_OF_MONTH:
			return daysOfMonth;
		case TYPE_MONTH:
			return months;
		case TYPE_DAY_OF_WEEK:
			return daysOfWeek;
		case TYPE_YEAR:
			return years;
		default:
			return null;
		}
	}
	
	private void checkValue(int value, int type) throws ParseException {
		if (value == VALUE_QUESTION && (type != TYPE_DAY_OF_MONTH && type != TYPE_DAY_OF_WEEK))
			throw new ParseException("?ֻ�ܳ�����Ԫ���ջ��������ϣ�", 0);
		if (value == VALUE_STAR || value == VALUE_QUESTION) 
			return;
		if (type == TYPE_SECOND || type == TYPE_MINUTE) {
			if (value < 0 || value > 59)
				throw new ParseException("�롢�ֵ�ȡֵΪ0-59��", 0);
		} else if (type == TYPE_HOUR) {
			if (value < 0 || value > 23)
				throw new ParseException("Сʱ��ȡֵΪ0-23!", 0);
		} else if (type == TYPE_DAY_OF_MONTH) {
			if (value < 1 || value > 31)
				throw new ParseException("����ȡֵΪ1-31��", 0);
		} else if (type == TYPE_MONTH) {
			if (value < 1 || value > 12)
				throw new ParseException("�·�ȡֵΪ1-12��", 0);
		} else if (type == TYPE_DAY_OF_WEEK) {
			if (value < 1 || value > 7)
				throw new ParseException("����ȡֵΪ1-7��", 0);
		} else if (type == TYPE_YEAR) {
			if (value < 1970 || value > 2099)
				throw new ParseException("���ȡֵΪ1970-2099��", 0);
		} else
			throw new ParseException("�Ƿ�����" + type + "��", 0);
	}
	
	private void checkIncrement(int incr, int type) throws ParseException {
		if (type == TYPE_SECOND || type == TYPE_MINUTE) {
			if (incr < 1 || incr > 59)
				throw new ParseException("������ȡֵΪ1-59��", 0);
		} else if (type == TYPE_HOUR) {
			if (incr < 1 || incr > 23)
				throw new ParseException("������ȡֵΪ1-23!", 0);
		} else if (type == TYPE_DAY_OF_MONTH) {
			if (incr < 1 || incr > 31)
				throw new ParseException("������ȡֵΪ1-31��", 0);
		} else if (type == TYPE_MONTH) {
			if (incr < 1 || incr > 12)
				throw new ParseException("������ȡֵΪ1-12��", 0);
		} else if (type == TYPE_DAY_OF_WEEK) {
			if (incr < 1 || incr > 7)
				throw new ParseException("������ȡֵΪ1-7��", 0);
		}
	}
	
	private int getMinValue(int type) {
		switch (type) {
		case TYPE_SECOND:
		case TYPE_MINUTE:
		case TYPE_HOUR:
			return 0;
		case TYPE_DAY_OF_MONTH:
		case TYPE_MONTH:
		case TYPE_DAY_OF_WEEK:
			return 1;
		case TYPE_YEAR:
			return 1970;
		default:
			return -1;
		}
	}
	
	private int getMaxValue(int type) {
		switch (type) {
		case TYPE_SECOND:
		case TYPE_MINUTE:
			return 59;
		case TYPE_HOUR:
			return 23;
		case TYPE_DAY_OF_MONTH:
			return 31;
		case TYPE_MONTH:
			return 12;
		case TYPE_DAY_OF_WEEK:
			return 7;
		case TYPE_YEAR:
			return 2099;
		default:
			return -1;
		}
	}
	
	private int getMonthNumber(String str) {
		Integer i = monthMap.get(str);
		if (null == i)
			return -1;
		return i.intValue();
	}
	
	private int getDayOfWeekNumber(String str) {
		Integer i = dayMap.get(str);
		if (null == i)
			return -1;
		return i.intValue();
	}
	












	
	private int getLastDayOfMonth(int month, int year) {
		switch(month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		case 2:
			return isLeapYear(year) ? 29 : 28;
		default:
			throw new IllegalArgumentException("������·ݣ�");
		}
	}
	
	private boolean isLeapYear(int y) {
		return ((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0);
	}
	

























































	
	private void readObject(java.io.ObjectInputStream stream)
	throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		try {
			parseCronString();
		} catch (ParseException e) {
		}
	}
	
	public static void main(String[] args) {
		try {
			CronExpression cron = new CronExpression("10 10 13 3 2 ?");
			System.out.println(cron.getDateAfter(new Date()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
