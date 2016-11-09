package com.netease.stat;

import java.util.Iterator;
import java.util.Vector;

public class HistogramFactory {
	private static HistogramFactory instance = new HistogramFactory();
	
	public static HistogramFactory getInstance() {
		return instance;
	}
	
	
	public Histogram build(Distribution data, HistogramType type, SourceParameter sourceParameter, int numBuckets) {
		if (type == HistogramType.EQUI_WIDTH)
			return buildEquiWidthHistogram(data, sourceParameter, numBuckets);
		else
			return buildEquiDepthHistogram(data, sourceParameter, numBuckets);
	}

	private Histogram buildEquiDepthHistogram(Distribution data, SourceParameter sourceParameter, int numBuckets) {
		long areaPerBucket = (long)Math.ceil((double)data.getTotal(sourceParameter) / numBuckets);
		if (areaPerBucket < 1)
			throw new IllegalArgumentException("�޷�������״ͼ����ͨ��������ָ����buckets������");
		Vector<Bucket> bucketList = new Vector<Bucket>();
		
		Iterator<Pair> iter = data.iterator(sourceParameter);
		
		long low = 0;
		long distinct = 0;
		long area = 0;
		while (iter.hasNext()) {
			Pair entry = iter.next();
			low = entry.x;
			area = 0;
			distinct = 0;
			
			while(true) {
				distinct++;
				area += entry.y;
				if (area >= areaPerBucket) {
					Bucket b = new Bucket(low, entry.x, area, distinct);
					bucketList.add(b);
					area  = 0;
					break;
				}
				if (!iter.hasNext())
					break;
				entry = iter.next();
			} 
		}
		
		if (area != 0) {
			Bucket b = new Bucket(low, data.getMax(SourceParameter.VALUE), area, distinct);
			bucketList.add(b);
		}
		
		Bucket[] buckets = new Bucket[bucketList.size()];
		bucketList.toArray(buckets);
		return new HistogramImpl(data.getName(), data.getUnit(), getUnitY(data, sourceParameter), buckets);
	}

	private Histogram buildEquiWidthHistogram(Distribution data, SourceParameter sourceParameter, int numBuckets) {
		
		long min = data.getMin(SourceParameter.VALUE);
		long max = data.getMax(SourceParameter.VALUE);
		long bucketWidth = (max - min) / numBuckets; 
		if (bucketWidth < 1)
			throw new IllegalArgumentException("�޷�������״ͼ����ͨ��������ָ����buckets������");
		
		Vector<Bucket> bucketList = new Vector<Bucket>();
		
		Iterator<Pair> iter = data.iterator(sourceParameter);
		
		long low = 0;
		long distinct = 0;
		long area = 0;
		while (iter.hasNext()) {
			Pair entry = iter.next();
			low = entry.x;
			area = 0;
			distinct = 0;
			
			while(true) {
				distinct++;
				area += entry.y;
				if (entry.x - low >= bucketWidth) {
					Bucket b = new Bucket(low, entry.x, area, distinct);
					bucketList.add(b);
					break;
				}
				if (!iter.hasNext())
					break;
				entry = iter.next();
			} 
		}
		
		if (area != 0) {
			Bucket b = new Bucket(low, data.getMax(SourceParameter.VALUE), area, distinct);
			bucketList.add(b);
		}
		
		Bucket[] buckets = new Bucket[bucketList.size()];
		bucketList.toArray(buckets);
		return new HistogramImpl(data.getName(), data.getUnit(), getUnitY(data, sourceParameter), buckets);
	}

	private String getUnitY(Distribution data, SourceParameter sourceParameter) {
		String unitY;
		if (sourceParameter == SourceParameter.REPEAT)
			unitY = "COUNT";
		else
			unitY = data.getUnit();
		return unitY;
	}
}
