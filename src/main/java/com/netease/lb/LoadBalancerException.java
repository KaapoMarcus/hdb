package com.netease.lb;

public class LoadBalancerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	
	public LoadBalancerException(String msg) {
		super(msg);
	}

	
	public LoadBalancerException(String msg, Throwable cause) {
		super(msg, cause);
	}

	
	public LoadBalancerException(Throwable cause) {
		super(cause);
	}

}
