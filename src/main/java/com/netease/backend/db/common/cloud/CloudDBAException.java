package com.netease.backend.db.common.cloud;

import com.netease.backend.db.common.cloud.Definition.ExceptionType;


public class CloudDBAException extends Exception {

	private static final long serialVersionUID = 1L;

	
	private ExceptionType exceptionType;

	
	private String errorCode;

	
	public CloudDBAException(ExceptionType type, String errorCode,
			String message) {
		super(message);
		this.exceptionType = type;
		this.errorCode = errorCode;
	}

	
	public CloudDBAException(String errorCode, String message) {
		this(ExceptionType.ERROR, errorCode, message);
	}

	
	public CloudDBAException(ExceptionType type, String errorCode,
			String message, Throwable cause) {
		super(message, cause);
		this.exceptionType = type;
		this.errorCode = errorCode;
	}

	
	public CloudDBAException(String errorCode, String message, Throwable cause) {
		this(ExceptionType.ERROR, errorCode, message, cause);
	}

	public ExceptionType getExceptionType() {
		return exceptionType;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
