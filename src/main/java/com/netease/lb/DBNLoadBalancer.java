package com.netease.lb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


public class DBNLoadBalancer<T> implements LoadBalancer<T> {
	
	protected String name;

	
	protected Map<T, Choice<T>> allChoices;

	
	protected DistrCache<T> distrCache;

	
	protected boolean ignoreWeight;

	
	protected int policy;

	
	public DBNLoadBalancer(String name, Collection<T> resources) {
		if (resources == null) {
			throw new IllegalArgumentException("can't create DBNLoadBalancer.");
		}
		this.name = name;
		allChoices = new HashMap<T, Choice<T>>();
		if (resources != null) {
			for (T r : resources)
				addResource(r, true);
		}
		ignoreWeight = false;
		policy = ROUND_ROBIN;
	}

	
	public DBNLoadBalancer(String name, Collection<T> resources, Collection<Integer> weights) {
		if (resources.size() != weights.size()) {
			throw new IllegalArgumentException(
					"can't create DBNLoadBalancer, resources and weights not equals");
		}
		if (resources == null) {
			throw new IllegalArgumentException("can't create DBNLoadBalancer.");
		}
		this.name = name;
		allChoices = new HashMap<T, Choice<T>>();
		if (resources != null) {
			for (T r : resources)
				addResource(r, true);
		}
		ignoreWeight = false;
		policy = ROUND_ROBIN;
		Iterator<T> iter = resources.iterator();
		for (int weight : weights) {
			T element = iter.next();
			setWeight(element, weight);
		}
	}

	
	public String getName() {
		return name;
	}

	
	synchronized public void add(T r, boolean enabled) {
		addResource(r, enabled);
		if (enabled) {
			distrCache = null;
		}
	}

	
	synchronized public void remove(T r) {
		allChoices.remove(r);
		distrCache = null;
	}

	
	synchronized public Collection<T> getResources() {
		Collection<Choice<T>> choices = allChoices.values();
		Vector<T> resVec = new Vector<T>();
		for (Choice<T> c : choices) {
			resVec.add(c.get());
		}
		return resVec;
	}

	
	synchronized public int getNumResources() {
		return allChoices.size();
	}

	
	synchronized public Collection<T> getEnabledResources() {
		Collection<Choice<T>> choices = allChoices.values();
		Vector<T> resVec = new Vector<T>();
		for (Choice<T> c : choices) {
			if (c.isEnabled()) {
				resVec.add(c.get());
			}
		}
		return resVec;
	}

	
	synchronized public int getNumEnabledResources() {
		int num = 0;
		Collection<Choice<T>> choices = allChoices.values();
		for (Choice<T> c : choices) {
			if (c.isEnabled()) {
				num++;
			}
		}
		return num;
	}

	
	synchronized public void disable(T r) {
		Choice<T> c = getChoice(r);
		if (!c.isEnabled()) {
			return;
		}
		c.disable();
		distrCache = null;
	}

	
	synchronized public void enable(T r) {
		Choice<T> c = getChoice(r);
		if (c.isEnabled())
			return;
		c.enable();
		distrCache = null;
	}

	
	public boolean isEnabled(T r) {
		return getChoice(r).isEnabled();
	}

	
	public Metric getPerfMetrics(T r) {
		return null;
	}

	
	synchronized public void setPerfMetics(T r, Metric m) {
		return;
	}

	
	public double getWeight(T r) {
		return getChoice(r).getWeight();
	}

	
	public void setWeight(T r, double w) {
		Choice<T> c = getChoice(r);
		if (w == c.getWeight())
			return;
		getChoice(r).setWeight(w);
		if (!ignoreWeight) {
			distrCache = null;
		}
	}

	
	public T choose(Metric perfRequest) throws LoadBalancerException {
		
		DistrCache<T> localCache = distrCache;

		if (localCache == null) {
			localCache = buildDistrCache();
			distrCache = localCache;
		}

		if (policy == RANDOM)
			return localCache.random().get();
		else
			return localCache.roundRobin().get();
	}

	
	public boolean getIgnoreMetrics() {
		return true;
	}

	
	public boolean getIgnoreWeight() {
		return ignoreWeight;
	}

	
	public int getPolicy() {
		return policy;
	}

	
	public void setIgnoreMetrics(boolean ignoreMetrics) {
		return;
	}

	
	public void setIgnoreWeight(boolean ignoreWeight) {
		if (this.ignoreWeight == ignoreWeight)
			return;
		this.ignoreWeight = ignoreWeight;
		distrCache = null;
	}

	
	public void setPolicy(int policy) {
		if (this.policy == policy)
			return;
		if (policy != RANDOM && policy != ROUND_ROBIN)
			throw new IllegalArgumentException("Invalid policy: " + policy);
		this.policy = policy;

	}

	
	@SuppressWarnings("unchecked")
	synchronized private DistrCache<T> buildDistrCache() throws LoadBalancerException {
		Collection<Choice<T>> choices = allChoices.values();
		if (choices.size() == 0) {
			throw new LoadBalancerException("�޷�����DistrCache�� �ڵ��б�Ϊ��");
		}

		Iterator<Choice<T>> iter;
		Choice[] distr;
		int length = 0; 

		if (ignoreWeight) {
			
			ArrayList<Choice> availableChoices = new ArrayList<Choice>();
			iter = choices.iterator();
			while (iter.hasNext()) {
				Choice<T> element = iter.next();
				if (element.isEnabled()) {
					availableChoices.add(element);
				}
			}

			distr = new Choice[availableChoices.size()];
			availableChoices.toArray(distr);
		} else {
			
			iter = choices.iterator();
			Choice<T> element = iter.next();
			int factor = (int) element.getWeight(); 
			while (iter.hasNext()) {
				element = iter.next();
				if (element.isEnabled()) {
					factor = gcd(factor, (int) element.getWeight());
				}
			}

			
			iter = choices.iterator();
			while (iter.hasNext()) {
				element = iter.next();
				if (element.isEnabled()) {
					length += (int) element.getWeight() / factor;
				}
			}

			distr = new Choice[length];

			
			SortListType sortList[] = new SortListType[length];
			iter = choices.iterator();
			int listIndex = 0;
			while (iter.hasNext()) {
				element = iter.next();
				if (element.isEnabled()) {
					int count = (int) element.getWeight() / factor;
					int stepLength = length / (int) element.getWeight();
					for (int i = 0, step = 1; i < count; i++, step += stepLength) {
						sortList[listIndex++] = new SortListType<Choice>(element, step);
					}
				}
			}

			Arrays.sort(sortList, 0, length);

			for (int i = 0; i < length; i++) {
				distr[i] = (Choice) sortList[i].element;
			}
		}
		
		return new DistrCache<T>(distr);
	}

	
	private int gcd(int a, int b) {
		if (b != 0) {
			return gcd(b, a % b);
		} else {
			return a;
		}
	}

	
	protected Choice<T> getChoice(T r) {
		Choice<T> c = allChoices.get(r);
		if (c == null)
			throw new IllegalArgumentException("Resource '" + r.toString() + "' not found");
		return c;
	}

	
	protected void addResource(T r, boolean enabled) {
		Choice<T> c = new Choice<T>(r, enabled);
		allChoices.put(r, c);
	}

	
	public LoadBalancerIterator<T> iterator() throws LoadBalancerException {
		
		DistrCache<T> localCache = distrCache;

		if (localCache == null) {
			localCache = buildDistrCache();
			distrCache = localCache;
		}
		return localCache.iterator();
	}
}


class SortListType<T> implements Comparable {

	
	int weight;

	T element;

	public SortListType(T element, int weight) {
		this.element = element;
		this.weight = weight;
	}

	public int compareTo(Object o) {
		SortListType s = (SortListType) o;
		return weight < s.weight ? -1 : (weight == s.weight) ? 0 : 1;
	}
}