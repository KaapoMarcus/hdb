package com.netease.lb;


public interface Metric {
	
	double normalize();
	
	
	Metric sub(Metric another);
}
