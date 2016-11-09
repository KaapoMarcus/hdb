package com.netease.util;

import java.util.Collection;
import java.util.NoSuchElementException;


public class DList<E> {
	 
	private DLink<E> header;
	
	private int size;

	
	public DList() {
		header = new DLink<E>(null);
		header.prev = header.next = header;
		header.list = this;
	}
	
	
	public DLink<E> getHeader() {
		return header;
	}
	
	
	public DLink<E> addLast(E v) {
		DLink<E> e = new DLink<E>(v);
		addLast(e);
		return e;
	}
	
	
	public DLink<E> addFirst(E v) {
		DLink<E> e = new DLink<E>(v);
		addFirst(e);
		return e;
	}
	
	
	public void addLast(DLink<E> e) {
		header.addBefore(e);
	}
	
	
	public void addFirst(DLink<E> e) {
		header.addAfter(e);
	}
	
	
	public void moveToFirst(DLink<E> e) {
		e.unLink();
		addFirst(e);
	}
	
	
	public void moveToLast(DLink<E> e) {
		e.unLink();
		addLast(e);
	}
	
	
	public DLink<E> removeLast() throws NoSuchElementException {
		return remove(header.prev);
	}
	
	
	public DLink<E> removeFirst() {
		return remove(header.next);
	}
	
	
	public boolean isEmpty() {
		return header.next == header;
	}
	
	
	public int size() {
		return size;
	}
	
	
	public void addToColleciton(Collection<E> c) {
		for (DLink<E> e = header.next; e != header; e = e.next)
			c.add(e.v);
	}
	
	
	public void clear() {
		header.prev = header.next = header;
		size = 0;
	}
	
	private DLink<E> remove(DLink<E> e) {
		if (e == header)
			throw new NoSuchElementException();
		e.unLink();
		return e;
	}
	
	
	public static class DLink<E> {
		
		private DLink<E> next;
		
		private DLink<E> prev;
		
		private DList<E> list;
		
		private E v;
		
		
		public DLink(E v) {
			next = prev = null;
			this.v = v;
		}
		
		
		public E get() {
			return v;
		}

		
		public DLink<E> getNext() {
			return next;
		}

		
		public DLink<E> getPrev() {
			return prev;
		}
		
		
		public void addBefore(DLink<E> e) {
			e.checkFree();
			add(this.prev, e, this);
		}
		
		
		public void addAfter(DLink<E> e) {
			e.checkFree();
			add(this, e, this.next);
		}
		
		
		public void unLink() {
			if (list == null)
				return;
			if (this == list.header)
				throw new IllegalArgumentException("Can not unlink header entry");
			prev.next = next;
			next.prev = prev;
			prev = next = null;
			list.size--;
			list = null;
		}
		
		
		public DList<E> getList() {
			return list;
		}
		
		private static <E> void add(DLink<E> prev, DLink<E> e, DLink<E> next) {
			prev.next = e;
			e.prev = prev;
			next.prev = e;
			e.next = next;
			e.list = prev.list;
			e.list.size++;
		}
		
		private void checkFree() {
			if (list != null)
				throw new IllegalArgumentException("Can not add entry already in a list."); 
		}
	}
}
