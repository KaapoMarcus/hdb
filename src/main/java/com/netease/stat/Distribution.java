
package com.netease.stat;

import java.util.Iterator;


public class Distribution {
	
	private String name;
	
	private String unit;
	
	private long resolution;
	
	private boolean multiThreadSafe;
	
	private long[] countArray;
	
	private long[] sumArray;
	
	private long countArraySize;
	
	private long validUnit;
	
	private long maxValue;
	
	private long minValue;
	
	private long maxRepeat;
	
	private long totalSum;
	
	private long totalCount;

	
	public Distribution(String name, String unit, long resolution, boolean multiThreadSafe) {
		assert resolution > 0 : "Resolution must be greater than 0";

		this.name = name;
		this.unit = unit;
		this.resolution = resolution;
		this.multiThreadSafe = multiThreadSafe;
		countArray = new long[200];
		sumArray = new long[200];
		countArraySize = countArray.length;
		maxValue = Integer.MIN_VALUE;
		minValue = Integer.MAX_VALUE;
	}

	
	public void addResult(long value) {
		addResult(value, 1);
	}

	
	public void addResult(long value, long count) {
		if (value / resolution > Integer.MAX_VALUE || value / resolution < Integer.MIN_VALUE)
			throw new IllegalArgumentException("Value out of range: " + value);

		int index = (int) (value / resolution);
		synchronized (this) {
			if (index >= countArraySize) {
				int newLength = countArray.length * 2;
				while (newLength <= index)
					newLength *= 2;
				long[] newCountArray = new long[newLength];
				System.arraycopy(countArray, 0, newCountArray, 0, countArray.length);
				long[] newSumArray = new long[newLength];
				System.arraycopy(sumArray, 0, newSumArray, 0, countArray.length);
				countArray = newCountArray;
				sumArray = newSumArray;
				countArraySize = countArray.length;
			}
		}
		if (multiThreadSafe) {
			synchronized (this) {
				doAddResult(index, value, count);
			}
		} else
			doAddResult(index, value, count);
	}

	private void doAddResult(int index, long value, long count) {
		countArray[index] += count;
		sumArray[index] += value * count;
		if (value > maxValue)
			maxValue = value;
		if (value < minValue)
			minValue = value;
		if (index > validUnit)
			validUnit = index;
		if (countArray[index] > maxRepeat)
			maxRepeat = countArray[index];
		totalCount += count;
		totalSum += value * count;
	}

	
	public String getUnit() {
		return unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMultiThreadSafe() {
		return multiThreadSafe;
	}

	public void setMultiThreadSafe(boolean multiThreadSafe) {
		this.multiThreadSafe = multiThreadSafe;
	}

	public long getResolution() {
		return resolution;
	}

	
	public long getPercentThreshold(double percentage, SourceParameter sourceParameter) {
		if (percentage <= 0.0 || percentage >= 1.0)
			throw new IllegalArgumentException("Percentage out of range: " + percentage
				+ ", must in (0, 1)");
		if (sourceParameter == SourceParameter.REPEAT) {
			long count = 0;
			for (int i = 0; i < countArray.length; i++) {
				count += countArray[i];
				if (count >= totalCount * percentage)
					return i * resolution;
			}
		} else {
			long sum = 0;
			for (int i = 0; i < countArray.length; i++) {
				sum += countArray[i] * resolution * i;
				if (sum >= totalSum * percentage)
					return i * resolution;
			}
		}
		assert false;
		return -1;
	}

	
	public double getAverage(SourceParameter sourceParameter) {
		if (sourceParameter == SourceParameter.VALUE) {
			if (totalCount == 0)
				return -1;
			return (double) totalSum / totalCount;
		} else {
			int distinctValues = 0;
			for (int i = 0; i <= validUnit; i++) {
				if (countArray[i] > 0)
					distinctValues++;
			}
			return (double) totalCount / distinctValues;
		}
	}

	
	public long getMin(SourceParameter sourceParameter) {
		if (sourceParameter == SourceParameter.VALUE)
			return minValue;
		else {
			long minRepeat = Long.MAX_VALUE;
			for (int i = 0; i <= validUnit; i++) {
				if (countArray[i] > 0 && countArray[i] < minRepeat)
					minRepeat = countArray[i];
			}
			if (minRepeat == Long.MAX_VALUE)
				minRepeat = 0;
			return minRepeat;
		}
	}

	
	public long getMax(SourceParameter sourceParameter) {
		if (sourceParameter == SourceParameter.VALUE)
			return maxValue;
		else
			return maxRepeat;
	}

	
	public long getTotal(SourceParameter sourceParameter) {
		if (sourceParameter == SourceParameter.VALUE)
			return totalSum;
		else
			return totalCount;
	}

	
	public Distribution slice(SourceParameter sourceParameter, long low, long high) {
		Distribution d = new Distribution(name, unit, resolution, multiThreadSafe);
		for (int i = 0; i <= validUnit; i++) {
			if (countArray[i] > 0) {
				if (sourceParameter == SourceParameter.VALUE) {
					if (i * resolution >= low && i * resolution <= high)
						d.addResult(i * resolution, countArray[i]);
				} else {
					if (countArray[i] >= low && countArray[i] <= high)
						d.addResult(i * resolution, countArray[i]);
				}
			}
		}
		return d;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (resolution ^ (resolution >>> 32));
		for (int i = 0; i <= validUnit; i++) {
			result = PRIME * result + i;
			result = PRIME * result + (int) (countArray[i] ^ (countArray[i] >>> 32));
		}
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
		final Distribution other = (Distribution) obj;
		if (resolution != other.resolution)
			return false;
		if (totalSum != other.totalSum)
			return false;
		if (totalCount != other.totalCount)
			return false;
		if (validUnit != other.validUnit)
			return false;
		if (minValue != other.minValue)
			return false;
		if (maxValue != other.maxValue)
			return false;
		if (maxRepeat != other.maxRepeat)
			return false;
		for (int i = 0; i <= validUnit; i++)
			if (countArray[i] != other.countArray[i])
				return false;
		return true;
	}

	public Iterator<Pair> iterator(SourceParameter sourceParameter) {
		return new DistIterator(sourceParameter);
	}

	private class DistIterator implements Iterator<Pair> {
		private SourceParameter sourceParameter;
		private int currentIndex = -1;

		DistIterator(SourceParameter sourceParameter) {
			this.sourceParameter = sourceParameter;
		}

		public boolean hasNext() {
			for (int i = currentIndex + 1; i <= validUnit; i++)
				if (countArray[i] > 0)
					return true;
			return false;
		}

		public Pair next() {
			for (int i = currentIndex + 1; i <= validUnit; i++)
				if (countArray[i] > 0) {
					currentIndex = i;
					if (sourceParameter == SourceParameter.VALUE)
						return new Pair(i * resolution, i * resolution * countArray[i]);
					else
						return new Pair(i * resolution, countArray[i]);
				}
			return null;
		}

		public void remove() {
			countArray[currentIndex] = 0;
		}

	}
}
