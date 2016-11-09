package com.netease.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


public class AccurateMCVCollector<T> extends MCVCollector<T> {
	private Map<T, MCVItem<T>> itemMap;
	private Collection<MCVItem<T>> resultCache;
	
	AccurateMCVCollector(int targetItems, double targetFreq) {
		super(targetItems, targetFreq);
		itemMap = new HashMap<T, MCVItem<T>>();
	}
	
	@Override
	public void addItem(T item) {
		MCVItem<T> i = itemMap.get(item);
		if (i == null)
			itemMap.put(item, new MCVItem<T>(item, 1));
		else
			i.repeat++;
		resultCache = null;
		numItems++;
	}


	@Override
	public Collection<MCVItem<T>> getMCVs() {
		if (resultCache != null)
			return resultCache;
		
		BoundedHeap<MCVItem<T>> heap = new BoundedHeap<MCVItem<T>>(targetItems);
		for (MCVItem<T> item : itemMap.values()) {
			if (item.repeat < targetFreq * numItems)
				continue;
			heap.put(item);
		}
		resultCache = new Vector<MCVItem<T>>();
		Iterator<MCVItem<T>> iter = heap.iterator();
		while (iter.hasNext())
			resultCache.add(iter.next());
		
		return resultCache;
	}

}
