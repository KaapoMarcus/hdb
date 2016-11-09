package com.netease.backend.db.common.utils;

import java.io.Serializable;
import java.util.Collection;



public class OneBasedArray<E> implements Serializable {
	private static final long serialVersionUID = -4282870464649976240L;

	private E[] elementData;

	private int size;

	@SuppressWarnings("unchecked")
	public OneBasedArray(int initialCapacity) {
		super();
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		this.elementData = (E[]) new Object[initialCapacity];
	}

	public OneBasedArray() {
		this(10);
	}

	@SuppressWarnings("unchecked")
	public OneBasedArray(Collection<? extends E> c) {
		size = c.size();
		
		elementData = (E[]) new Object[(int) Math.min((size * 110L) / 100, Integer.MAX_VALUE)];
		c.toArray(elementData);
	}

	@SuppressWarnings("unchecked")
	public void trimToSize() {
		int oldCapacity = elementData.length;
		if (size < oldCapacity) {
			Object oldData[] = elementData;
			elementData = (E[]) new Object[size];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	@SuppressWarnings("unchecked")
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object oldData[] = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = (E[]) new Object[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean contains(Object elem) {
		return indexOf(elem) >= 1;
	}

	public int indexOf(Object elem) {
		if (elem == null) {
			for (int i = 0; i < size; i++)
				if (elementData[i] == null)
					return (i + 1);
		} else {
			for (int i = 0; i < size; i++)
				if (elem.equals(elementData[i]))
					return (i + 1);
		}
		return -1;
	}

	public int lastIndexOf(Object elem) {
		if (elem == null) {
			for (int i = size - 1; i >= 0; i--)
				if (elementData[i] == null)
					return (i + 1);
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (elem.equals(elementData[i]))
					return (i + 1);
		}
		return -1;
	}

	public Object[] toArray() {
		Object[] result = new Object[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		System.arraycopy(elementData, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	public E get(int index) {
		RangeCheck(index);

		return elementData[index - 1];
	}

	public E set(int index, E element) {
		RangeCheck(index);

		E oldValue = elementData[index - 1];
		elementData[index - 1] = element;
		return oldValue;
	}

	public boolean add(E o) {
		ensureCapacity(size + 1); 
		elementData[size++] = o;
		return true;
	}

	public void add(int index, E element) {
		index--;
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

		ensureCapacity(size + 1); 
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
		elementData[index] = element;
		size++;
	}

	public E remove(int index) {
		RangeCheck(index);

		index--;

		E oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		elementData[--size] = null; 

		return oldValue;
	}

	public boolean remove(Object o) {
		if (o == null) {
			for (int index = 0; index < size; index++)
				if (elementData[index] == null) {
					fastRemove(index);
					return true;
				}
		} else {
			for (int index = 0; index < size; index++)
				if (o.equals(elementData[index])) {
					fastRemove(index);
					return true;
				}
		}
		return false;
	}

	private void fastRemove(int index) {
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		elementData[--size] = null; 
	}

	public void clear() {

		
		for (int i = 0; i < size; i++)
			elementData[i] = null;

		size = 0;
	}

	public boolean addAll(OneBasedArray<? extends E> c) {
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew); 
		System.arraycopy(a, 0, elementData, size, numNew);
		size += numNew;
		return numNew != 0;
	}

	public boolean addAll(Collection<? extends E> c) {
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew); 
		System.arraycopy(a, 0, elementData, size, numNew);
		size += numNew;
		return numNew != 0;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		int realIndex = index - 1;

		if (realIndex > size || realIndex < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew); 

		int numMoved = size - realIndex;
		if (numMoved > 0)
			System.arraycopy(elementData, realIndex, elementData, realIndex + numNew, numMoved);

		System.arraycopy(a, 0, elementData, realIndex, numNew);
		size += numNew;
		return numNew != 0;
	}

	private void RangeCheck(int index) {
		if (index > size || index < 1)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
	}

	public static void main(String[] args) {
		OneBasedArray<Integer> iarray = new OneBasedArray<Integer>();
		iarray.add(111);
		iarray.add(222);
		iarray.add(333);
		iarray.add(111);
		iarray.add(222);
		iarray.add(333);
		iarray.add(111);
		iarray.add(222);
		iarray.add(333);

		iarray.add(4, 444);
		iarray.add(5, 0);
		iarray.set(5, 555);
		for (int i = 1; i <= iarray.size; i++) {
			System.out.println(iarray.get(i));
		}
	}
}
