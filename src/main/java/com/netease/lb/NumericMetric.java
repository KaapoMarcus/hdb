package com.netease.lb;


public class NumericMetric implements Metric {
	private double value;
	
	public NumericMetric(double value) {
		this.value = value;
	}

	public double normalize() {
		return value;
	}

	public Metric sub(Metric another) {
		return new NumericMetric(value - another.normalize());
	}
}
