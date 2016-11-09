package com.netease.backend.db.common.utils;


public class MultiElementChecker {
	
	private long elementMark = 0;
	
	
	private long allElementsMarked = 0; 
	
	public MultiElementChecker(int elementSize) {
		marckAllElements(elementSize);
	}
	
	
	public boolean isElementMarked(int index) {
		if ((elementMark & (1 << index)) != 0)
			return true;
		return false;
	}
	
	
	public boolean isAllElementsMarked() {
		if (elementMark == allElementsMarked)
			return true;
		return false;
	}
	
	
	public void markElement(int index) {
		if (!isElementMarked(index))
			elementMark += 1 << index;
	}
	
	private void marckAllElements(int elementSize) {
		for (int n = 0; n < elementSize; n++)
			allElementsMarked += 1 << n; 
	}
	
	public void initAllElements() {
		elementMark = 0;
	}
}
