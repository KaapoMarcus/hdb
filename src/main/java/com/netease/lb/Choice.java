package com.netease.lb;




class Choice<T> {
	
	private T ref;
	
	private boolean enabled;
	private Metric perfMetric;
	private double weight;
	private double pmNormalization = 1.0;
	
	int availablePos;
	
	Choice(T ref, boolean enabled) {
		this.ref = ref;
		this.enabled = enabled;
		availablePos = -1;
		weight = 1.0;
	}
	
	T get() {
		return ref;
	}
	
	void enable() {
		this.enabled = true;
	}
	
	void disable() {
		this.enabled = false;
	}

	boolean isEnabled() {
		return enabled;
	}
	
	public Metric getPerfMetric() {
		return perfMetric;
	}

	public void setPerfMetrics(Metric m) {
		this.perfMetric = m;
		pmNormalization = m.normalize();
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double w) {
		weight = w;
	}
	
	public double getNormalizedMetric() {
		return pmNormalization;
	}
}
