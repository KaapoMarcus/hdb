package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.List;


public abstract class Hash implements Serializable {
	private static final long serialVersionUID = 4219532860623315903L;
	protected int bucketCount;

	
	public int hash(long value) {

		throw new IllegalArgumentException(
				"Hash for long type has not been implemented");
	}

	
	public int hash(String value) {

		throw new IllegalArgumentException(
				"Hash for String type has not been implemented");
	}

	
	public int hash(List<Object> values) {

		throw new IllegalArgumentException(
				"Hash for List type has not been implemented");
	}

	
	

	
	public void setBucketCount(int count) {
		this.bucketCount = count;
	}
}
