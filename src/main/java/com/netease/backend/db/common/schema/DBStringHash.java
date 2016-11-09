package com.netease.backend.db.common.schema;

import java.io.UnsupportedEncodingException;


public class DBStringHash extends Hash {

	private static final long serialVersionUID = -996774141032172851L;

	
	public int hash(String str) {
		if (null == str)
			return 0;	
		
		int h = 0;
		byte val[];
		try {
			val = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		int len = val.length;
		for (int i = 0; i < len; i++)
			h = 31 * h + val[i];
		return h % bucketCount;
	}
}
