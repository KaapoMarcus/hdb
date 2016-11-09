package com.netease.stat;

import java.io.Serializable;
import java.util.Collection;


public abstract class MCVCollector<T> {
	public static final int TARGET_ITEMS_UNSPECIFIED = Integer.MAX_VALUE;
	public static final double TARGET_FREQ_UNSPECIFIED = 0;

	
	protected int targetItems;
	
	
	protected double targetFreq;
	
	
	protected long numItems;
	
	
	public static class MCVItem<T> implements Comparable<MCVItem<T>>, Serializable {
		private static final long serialVersionUID = -3538881094937118514L;
		
		protected T item;
		protected long repeat;
		
		public MCVItem(T item, long repeat) {
			this.item = item;
			this.repeat = repeat;
		}

		public T getItem() {
			return item;
		}

		public long getRepeat() {
			return repeat;
		}

		public int compareTo(MCVItem<T> another) {
			if (repeat > another.repeat)
				return 1;
			else if (repeat == another.repeat) {
				return 0;
			} else
				return -1;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((item == null) ? 0 : item.hashCode());
			result = PRIME * result + (int) (repeat ^ (repeat >>> 32));
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
			final MCVItem<?> other = (MCVItem<?>) obj;
			if (item == null) {
				if (other.item != null)
					return false;
			} else if (!item.equals(other.item))
				return false;
			if (repeat != other.repeat)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "" + item + ":" + repeat;
		}
	}
	
	protected MCVCollector(int targetItems, double targetFreq) {
		this.targetItems = targetItems;
		this.targetFreq = targetFreq;
	}
	
	
	public final static <T> MCVCollector<T> getInstance(int targetItems, double targetFreq) {
		return new LossyCountingMCVCollector<T>(targetItems, targetFreq);
	}


	
	public final static <T> MCVCollector<T> getAccurateInstance(int targetItems, double targetFreq) {
		return new AccurateMCVCollector<T>(targetItems, targetFreq);
	}
	
	
	public abstract void addItem(T item); 
	
	
	public abstract Collection<MCVItem<T>> getMCVs();
	
	
	public long getNumItems() {
		return numItems;
	}
}
