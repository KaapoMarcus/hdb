package com.netease.lb;

import java.util.Random;


class DistrCache<T> {
	private Choice<T>[] distr;

	private Random r;

	private int lastChoice;

	DistrCache(Choice<T>[] distr) {
		
		if (distr == null || distr.length == 0) {
			throw new IllegalArgumentException("invalid argument when constructing DistrCache.");
		}
		
		this.distr = distr;
		r = new Random(System.currentTimeMillis());
		lastChoice = r.nextInt(distr.length);
	}

	Choice<T> random() {
		lastChoice = r.nextInt(distr.length);
		return distr[lastChoice];
	}

	Choice<T> roundRobin() {
		
		
		int lastChoiceCopy = lastChoice+1;
		if (lastChoiceCopy >= distr.length){
			lastChoiceCopy = 0;
		}
		lastChoice = lastChoiceCopy;
		
		
		return distr[lastChoiceCopy];
	}
	
	
	LoadBalancerIterator<T> iterator (){
		return new LoadBalancerIterator<T>(distr, lastChoice);
	}
}
