package com.netease.stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;


public class BoundedHeap<T extends Comparable<T>> {
	private int size;
	private PriorityQueue<T> heap;
	private T min;
	
	public BoundedHeap(int size) {
		this.size = size;
		heap = new PriorityQueue<T>();
	}
	
	public void put(T v) {
		if (heap.size() == size && min != null && v.compareTo(min) <= 0)
			return;
		heap.add(v);
		if (heap.size() > size)
			heap.poll();
		min = heap.peek();
	}
	
	public Iterator<T> iterator() {
		PriorityQueue<T> heapBak = new PriorityQueue<T>(heap);
		ArrayList<T> list = new ArrayList<T>(heapBak.size());
		while (!heapBak.isEmpty())
			list.add(heapBak.poll());
		Collections.reverse(list);
		return list.iterator();
	}
}
