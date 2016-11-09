package com.netease.stat;

public interface Histogram {
	
	String getName();
	
	
	void setName(String name);
	
	
	String getUnitX();
	
	
	void setUnitX(String unitX);
	
	
	String getUnitY();
	
	
	void setUnitY(String unitY);
	
	
	Bucket[] getBuckets();
	
	
	int getNumBuckets();
}
