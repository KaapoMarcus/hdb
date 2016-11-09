package com.netease.lb;


public class LoadBalancerIterator<T> {
	
	Choice<T>[] list;

	
	int startPos;

	
	int currentPos;

	boolean retrieved;

	
	LoadBalancerIterator(Choice<T>[] list, int startPos) {
		if (list == null || list.length == 0) {
			throw new IllegalArgumentException("can't create LoadBalancerIterator.");
		}
		this.list = list;
		this.startPos = startPos;
		this.currentPos = startPos;
		retrieved = false;
	}

	
	public boolean hasNext() {
		return !(currentPos == startPos && retrieved);
	}

	
	public T next() {
		retrieved = true;
		currentPos = (currentPos + 1) % list.length;
		return list[currentPos].get();
	}
}
