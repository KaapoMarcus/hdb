package com.netease.stat;


public class HistogramImpl implements Histogram {
	private String name;
	private String unitX;
	private String unitY;
	private Bucket[] buckets;
	
	public HistogramImpl(String name, String unitX, String unitY, Bucket[] buckets) {
		this.name = name;
		this.unitX = unitX;
		this.unitY = unitY;
		this.buckets = buckets;
	}

	
	public Bucket[] getBuckets() {
		return buckets;
	}

	
	public String getName() {
		return name;
	}


	public String getUnitX() {
		return unitX;
	}


	public String getUnitY() {
		return unitY;
	}


	public void setName(String name) {
		this.name = name;		
	}


	public void setUnitX(String unitX) {
		this.unitX = unitX;		
	}


	public void setUnitY(String unitY) {
		this.unitY = unitY;
	}


	public int getNumBuckets() {
		return buckets.length;
	}

}
