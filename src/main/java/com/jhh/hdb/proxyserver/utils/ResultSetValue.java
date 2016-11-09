package com.jhh.hdb.proxyserver.utils;

/**
 * 用于存放从ResultSet中获取到的值
 * 
 *
 */
public class ResultSetValue {
	public byte byteBinding;

	public short shortBinding;

	public int intBinding;

	public long longBinding;

	public float floatBinding;

	public double doubleBinding;

	public Object value;

	public boolean isNull = false; //default not null

}