package com.netease.stat;


public class Bucket {
	
	private long low;
	
	private long high;
	
	private long area;
	
	private long distinct;
	
	public Bucket(long low, long high, long area, long distinct) {
		this.low = low;
		this.high = high;
		this.area = area;
		this.distinct = distinct;
	}
	
	public long getDistinct() {
		return distinct;
	}
	
	public void setDistinct(long distinct) {
		this.distinct = distinct;
	}
	
	public long getArea() {
		return area;
	}
	
	public void setArea(long area) {
		this.area = area;
	}
	
	public long getHigh() {
		return high;
	}
	
	public void setHigh(long high) {
		this.high = high;
	}
	
	public long getLow() {
		return low;
	}
	
	public void setLow(long low) {
		this.low = low;
	}
	
	public boolean hasDistinct() {
		return distinct >= 0;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (area ^ (area >>> 32));
		result = PRIME * result + (int) (distinct ^ (distinct >>> 32));
		result = PRIME * result + (int) (high ^ (high >>> 32));
		result = PRIME * result + (int) (low ^ (low >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Bucket other = (Bucket) obj;
		if (area != other.area)
			return false;
		if (distinct != other.distinct)
			return false;
		if (high != other.high)
			return false;
		if (low != other.low)
			return false;
		return true;
	}
}
