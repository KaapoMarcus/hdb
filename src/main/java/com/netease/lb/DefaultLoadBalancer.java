package com.netease.lb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.netease.util.DList;

public class DefaultLoadBalancer<T> implements LoadBalancer<T> {
	
	protected String name;
	
	protected Map<T, Choice<T>> allChoices;
	
	protected ArrayList<Choice<T>> availableChoices;
	
	protected Map<Metric, DList.DLink<DistrCache<T>>> distrCaches;
	
	protected DList<DistrCache<T>> dcLru;
	
	protected int maxCacheSize = 100;
	
	protected boolean ignoreWeight;
	
	protected boolean ignoreMetircs;
	
	protected int policy;
	
	public DefaultLoadBalancer(String name, Collection<T> resources) {
		this.name = name;
		allChoices = new HashMap<T, Choice<T>>();
		availableChoices = new ArrayList<Choice<T>>();
		for (T r : resources)
			addResource(r, true);
		distrCaches = new HashMap<Metric, DList.DLink<DistrCache<T>>>();
		dcLru = new DList<DistrCache<T>>();
		policy = RANDOM;
	}
	
	public String getName() {
		return name;
	}

	synchronized public void add(T r, boolean enabled) {
		addResource(r, enabled);
		if (enabled && (!ignoreWeight || !ignoreMetircs))
			clearDistrCaches();
	}

	synchronized public void remove(T r) {
		Choice<T> c = getChoice(r);
		allChoices.remove(r);
		if (c.availablePos != -1) {
			removeFromAvailList(c);
			if (!ignoreWeight || !ignoreMetircs)
				clearDistrCaches();
		}
	}

	synchronized public Collection<T> getResources() {
		Collection<Choice<T>> choices = allChoices.values();
		Vector<T> resVec = new Vector<T>();
		for (Choice<T> c : choices)
			resVec.add(c.get());
		return resVec;
	}

	synchronized public int getNumResources() {
		return allChoices.size();
	}
	
	synchronized public Collection<T> getEnabledResources() {
		Vector<T> resVec = new Vector<T>();
		for (Choice<T> c : availableChoices)
			resVec.add(c.get());
		return resVec;
	}

	synchronized public int getNumEnabledResources() {
		return availableChoices.size();
	}
	
	synchronized public void disable(T r) {
		Choice<T> c = getChoice(r);
		if (!c.isEnabled())
			return;
		c.disable();
		removeFromAvailList(c);
		clearDistrCaches();
	}

	synchronized public void enable(T r) {
		Choice<T> c = getChoice(r);
		if (c.isEnabled())
			return;
		c.enable();
		c.availablePos = availableChoices.size();
		availableChoices.add(c);
		clearDistrCaches();
	}

	public boolean isEnabled(T r) {
		return getChoice(r).isEnabled();
	}

	public Metric getPerfMetrics(T r) {
		return getChoice(r).getPerfMetric();
	}

	synchronized public void setPerfMetics(T r, Metric m) {
		Choice<T> c = getChoice(r);
		if (metricEquals(m, c.getPerfMetric()))
			return;
		c.setPerfMetrics(m);
		if (!ignoreMetircs)
			clearDistrCaches();
	}

	public double getWeight(T r) {
		return getChoice(r).getWeight();
	}

	public void setWeight(T r, double w) {
		Choice<T> c = getChoice(r);
		if (w == c.getWeight())
			return;
		getChoice(r).setWeight(w);
		if (!ignoreWeight)
			clearDistrCaches();
	}

	public T choose(Metric perfRequest) {
		if (ignoreMetircs)
			perfRequest = null;
		DistrCache<T> dc = chooseDistrCache(perfRequest);
		if (dc == null)
			return null;
		if (policy == RANDOM)
			return dc.random().get();
		else
			return dc.roundRobin().get();
	}

	public boolean getIgnoreMetrics() {
		return ignoreMetircs;
	}

	public boolean getIgnoreWeight() {
		return ignoreWeight;
	}

	public int getPolicy() {
		return policy;
	}

	public void setIgnoreMetrics(boolean ignoreMetrics) {
		if (this.ignoreMetircs == ignoreMetrics)
			return;
		this.ignoreMetircs = ignoreMetrics;
		clearDistrCaches();
	}

	public void setIgnoreWeight(boolean ignoreWeight) {
		if (this.ignoreWeight == ignoreWeight)
			return;
		this.ignoreWeight = ignoreWeight;
		clearDistrCaches();
	}

	public void setPolicy(int policy) {
		if (this.policy == policy)
			return;
		if (policy != RANDOM && policy != ROUND_ROBIN)
			throw new IllegalArgumentException("Invalid policy: " + policy);
		this.policy = policy;
		
	}
	
	private void clearDistrCaches() {
		distrCaches.clear();
		dcLru.clear();
	}

	@SuppressWarnings("unchecked")
	private DistrCache<T> buildDistrCache(Metric perfRequest) {
		
		List<Choice<T>> myAvailChoices = new LinkedList<Choice<T>>();
		Vector<Double> normalizedWeights = new Vector<Double>();
		double totalNormWeight = 0.0;
		for (Choice<T> c : availableChoices) {
			double normalizedWeight;
			if (ignoreMetircs) {
				if (ignoreWeight)
					normalizedWeight = 1.0;
				else
					normalizedWeight = c.getWeight();
			} else {
				if (perfRequest == null) 
					normalizedWeight = c.getNormalizedMetric();
				else {
					Metric sub = c.getPerfMetric().sub(perfRequest);
					normalizedWeight = sub.normalize();
				}
				if (!ignoreWeight)
					normalizedWeight *= c.getWeight();
			}
			if (normalizedWeight > 0) {
				myAvailChoices.add(c);
				normalizedWeights.add(normalizedWeight);
				totalNormWeight += normalizedWeight;
			}
		}
		
		if (myAvailChoices.size() == 0)
			return null;
		
		int distrSize;
		if (ignoreWeight && ignoreMetircs)
			distrSize = myAvailChoices.size();
		else {
			if (myAvailChoices.size() > 20)
				distrSize = myAvailChoices.size() * 10;
			else
				distrSize = 100;
			
		}
		Choice distr[] = new Choice[distrSize];
		int assigned = 0;
		Choice<T> lastChoice = null;
		double accWeight = 0.0;	
		int i = 0;
		for (Choice<T> c : myAvailChoices) {
			accWeight += normalizedWeights.get(i);
			int assignTarget = (int)(accWeight / totalNormWeight * distrSize);
			while (assigned < assignTarget) {
				distr[assigned++] = c;
				lastChoice = c;
			}
			i++;
		}
		while (assigned < distrSize)
			distr[assigned++] = lastChoice;
		
		return new DistrCache<T>((Choice<T>[])distr);
	}
	
	protected DistrCache<T> chooseDistrCache(Metric perfRequest) {
		DList.DLink<DistrCache<T>> dcLink = distrCaches.get(perfRequest);
		if (dcLink == null) {
			DistrCache<T> distrCache = buildDistrCache(perfRequest);
			if (distrCache == null)
				return null;
			if (dcLru.size() >= maxCacheSize)
				dcLru.removeFirst();
			dcLink = dcLru.addLast(distrCache);
			distrCaches.put(perfRequest, dcLink);
		}
		DistrCache<T> dc = dcLink.get();
		return dc;
	}
	
	protected Choice<T> getChoice(T r) {
		Choice<T> c = allChoices.get(r);
		if (c == null)
			throw new IllegalArgumentException("Resource '" + r.toString() + "' not found");
		return c;
	}
	
	private void removeFromAvailList(Choice<T> c) {
		availableChoices.remove(c.availablePos);
		for (int i = c.availablePos; i < availableChoices.size(); i++) {
			assert availableChoices.get(i).availablePos == i + 1;
			availableChoices.get(i).availablePos--;
		}
		c.availablePos = -1;
	}
	
	private boolean metricEquals(Metric m1, Metric m2) {
		if (m1 == null && m2 == null)
			return true;
		else if (m1 != null)
			return m1.equals(m2);
		else
			return false;
	}
	
	protected void addResource(T r, boolean enabled) {
		Choice<T> c = new Choice<T>(r, enabled);
		allChoices.put(r, c);
		if (enabled) {
			c.availablePos = availableChoices.size();
			availableChoices.add(c);
		}
	}
	
	
	public LoadBalancerIterator<T> iterator(){
		return null;
	}

}
