package com.netease.backend.db.common.exceptions;



public class NotConnectedException extends DBAException {

	private static final long serialVersionUID = -6461535374289643551L;

	public NotConnectedException() {
		super();
	}

	public NotConnectedException(String message) {
		super(message);
	}

	public NotConnectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotConnectedException(Throwable cause) {
		super(cause);
	}
}
