package com.netease.backend.db.common.utils;

import java.lang.reflect.Array;

public class ArrayUtils
{

	public static boolean contains(
		long[] array, long value)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(
		int[] array, int value)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T[] copyOf(
		T[] original, int newLength)
	{
		return (T[]) copyOf(original, newLength, original.getClass());
	}

	
	public static <T> T[][] copyOf(
		T[][] original)
	{
		final T[][] copy = copyOf(original, original.length);
		for (int i = 0; i < copy.length; i++) {
			copy[i] = copyOf(original[i], original[i].length);
		}
		return copy;
	}

	
	@SuppressWarnings("unchecked")
	public static <T, U> T[] copyOf(
		U[] original, int newLength, Class<? extends T[]> newType)
	{
		T[] array = (T[]) Array.newInstance(newType.getComponentType(), newLength);
		T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength] : array;
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

}
