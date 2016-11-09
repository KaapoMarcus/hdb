package com.netease.backend.db.common.exceptions;

import java.sql.SQLException;


public class SQLExceptionWithCause extends SQLException {

	private static final long serialVersionUID = 1L;
	
	
	public SQLExceptionWithCause(Throwable cause) {
		super();
		this.initCause(cause);
	}
	
	
	public SQLExceptionWithCause(String msg, Throwable cause) {
		super(msg);
		this.initCause(cause);
	}
	
	
	public SQLExceptionWithCause() {
		super();
	}
	
	
	public SQLExceptionWithCause(String msg) {
		super(msg);
	}
	
	
	public SQLExceptionWithCause(String reason, String SQLState) {
		super(reason, SQLState);
	}
	
	
	public SQLExceptionWithCause(String reason, String SQLState, int vendorCode) {
		super(reason, SQLState, vendorCode);
	}
}
