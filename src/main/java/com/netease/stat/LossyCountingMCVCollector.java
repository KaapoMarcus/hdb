package com.netease.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


public final class LossyCountingMCVCollector<T> extends MCVCollector<T> {
	
	private double s;

	
	private double e;

	
	private int w;

	private Map<T, Entry> itemMap;

	private Collection<MCVItem<T>> resultCache;

	class Entry {
		T item;
		long repeat;
		long delta;

		public Entry(T item, long repeat, long delta) {
			this.item = item;
			this.repeat = repeat;
			this.delta = delta;
		}
	}

	LossyCountingMCVCollector(int targetItems, double targetFreq) {
		super(targetItems, targetFreq);
		if (targetFreq != TARGET_FREQ_UNSPECIFIED)
			s = targetFreq;
		else
			s = (double) 1 / targetItems / 10;
		e = s / 20;
		w = (int) Math.ceil(1 / e);
		itemMap = new HashMap<T, Entry>();
	}

	@Override
	public void addItem(T item) {
		numItems++;
		resultCache = null;

		Entry entry = itemMap.get(item);
		if (entry == null)
			itemMap.put(item, new Entry(item, 1, (int) (Math.ceil((double) numItems / w)) - 1));
		else
			entry.repeat++;

		if (numItems % w == 0) {
			int bCurrent = (int) (Math.ceil((double) numItems / w));
			Collection<Entry> entries = new Vector<Entry>();
			entries.addAll(itemMap.values());
			for (Entry ent : entries) {
				if (ent.repeat + ent.delta < bCurrent)
					itemMap.remove(ent.item);
			}
		}
	}

	@Override
	public Collection<MCVItem<T>> getMCVs() {
		if (resultCache != null)
			return resultCache;

		BoundedHeap<MCVItem<T>> heap = new BoundedHeap<MCVItem<T>>(targetItems);
		for (Entry entry : itemMap.values()) {
			if (entry.repeat < (s - e) * numItems)
				continue;
			heap.put(new MCVItem<T>(entry.item, entry.repeat));
		}

		resultCache = new Vector<MCVItem<T>>();
		Iterator<MCVItem<T>> iter = heap.iterator();
		while (iter.hasNext())
			resultCache.add(iter.next());

		return resultCache;
	}

}
