package com.netease.lb;

import java.util.Collection;


public interface LoadBalancer<T> {
	static final int RANDOM = 1;
	static final int ROUND_ROBIN = 2;
	
	
	String getName();
	
	
	void add(T r, boolean enabled);
	
	
	
	void remove(T r);
	

	
	Collection<T> getResources();
	
	
	
	int getNumResources();
	
	
	
	Collection<T> getEnabledResources();
	
	
	
	int getNumEnabledResources();
	
	
	
	void enable(T r);
	
	
	
	void disable(T r);
	
	
	
	boolean isEnabled(T r);
	
	
	
	void setWeight(T r, double w);
	
	
	
	double getWeight(T r);
	
	
	
	void setPerfMetics(T r, Metric m);
	
	
	Metric getPerfMetrics(T r);
	
	
	T choose(Metric perfRequest) throws LoadBalancerException;
	
	
	int getPolicy();
	
	
	void setPolicy(int policy);
	
	
	boolean getIgnoreWeight();
	
	
	void setIgnoreWeight(boolean ignoreWeight);
	
	
	boolean getIgnoreMetrics();
	
	
	void setIgnoreMetrics(boolean ignoreMetrics);
	
	
	LoadBalancerIterator<T> iterator () throws LoadBalancerException;
}
