package com.netease.backend.db.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class RandUtils {
	

	
	private static Random randGen = null;
	private static char[] numbersAndLetters = null;

	
	public static int randInt(int i, int j) throws IllegalArgumentException
	{
		if(i>=j)
			throw new IllegalArgumentException("The first Argument should less than the second.");
		j = (int)(Math.random()*(j-i+1));
		return i+j;
	}
	
	
	
	public static String randStr(int i, int j) throws IllegalArgumentException
	{
		return String.valueOf(randInt(i,j));
	}
	
	
	public static int randGsInt(int i, int j) throws IllegalArgumentException
	{
		return (int)(new Random().nextGaussian()*(j*0.25))+i;
	}
	
	
	
	public static String randGsStr(int i, int j) throws IllegalArgumentException
	{
		return String.valueOf(randGsInt(i,j));
	}
	
	
	public static long randLong(long i, long j) throws IllegalArgumentException
	{
		if(i>=j)
			throw new IllegalArgumentException("The first Argument should less than the second.");
		j = (long)(Math.random()*(j-i+1));
		return i+j;
	}
	
	
	public static String randString(int length) {

		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			randGen = new Random();
			numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
					+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}
	
	
	public static List<String> randStringList(int count, int length) {
		if (count < 1 || length < 1)
			return null;
		ArrayList<String> strList = new ArrayList<String>();
		for (int i = 0; i < count; i++)
			strList.add(randString(length));
		
		Collections.sort(strList, String.CASE_INSENSITIVE_ORDER);
		return strList;
	}
	
	
	public static List<String> randLongStrList(int count, long begin, long end) {
		if (count < 1)
			return null;
		ArrayList<String> longStrList = new ArrayList<String>();
		long[] longArray = new long[count];
		for (int i = 0; i < count; i++)
			longArray[i] = randLong(begin, end);
		
		Arrays.sort(longArray);
		for (int i = 0; i < count; i++)
			longStrList.add(String.valueOf(longArray[i]));
		
		return longStrList;
	}
	
	public class Stat 
	{
		public int index;
		public int count;
		public Stat(int index, int count)
		{
			this.index = index;
			this.count = count;
		}
	}
	
	
	public static void main(String[] args) {

		HashMap<Integer, Stat> map = new HashMap<Integer, Stat>();
		for(int i=0; i<100000 ;i++)
		{

			int randnum = randGsInt(0,10000);
			Stat stat = map.get(new Integer(randnum));
			if(stat == null)
				map.put(new Integer(randnum), (new RandUtils()).new Stat(randnum,1));
			else
				stat.count++;
		}
		
		ArrayList<Stat> statList = new ArrayList<Stat>();
		statList.addAll(map.values());
		
		Collections.sort(statList, new Comparator<Stat>() {
			public int compare(Stat stat1, Stat stat2) {
					return stat2.count -stat1.count;
		      }
		    });
		
		for(Stat stat : statList)
			System.out.println(stat.index+"= "+stat.count);
		
		System.out.println("Rand String:");
		List<String> strList = randLongStrList(13, 10, 200000000000000000L);
		for (String str : strList) {
			System.out.println(str);
		}
	}

}
