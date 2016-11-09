package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class XAQueryCond extends QueryCond implements Serializable {
	private static final long serialVersionUID = -7467606215701595392L;
	
	
	private Operation[] globalID;
	
	
	private Operation[] branchID;
	
	
	private Operation[] formatID;
	
	
	private Operation[] asID;
	
	
	private Operation[] operation;
	
	
	private Operation[] status;
	
	
	private Operation[] dbURL;
	
	
	private Operation[] time;
   
	
    public XAQueryCond() {
        super();
    }

	public Operation[] getAsID() {
		return asID;
	}

	public void setAsID(Operation[] asID) {
		this.asID = asID;
	}

	public Operation[] getBranchID() {
		return branchID;
	}

	public void setBranchID(Operation[] branchID) {
		this.branchID = branchID;
	}

	public Operation[] getDbURL() {
		return dbURL;
	}

	public void setDbURL(Operation[] dbURL) {
		this.dbURL = dbURL;
	}

	public Operation[] getFormatID() {
		return formatID;
	}

	public void setFormatID(Operation[] formatID) {
		this.formatID = formatID;
	}

	public Operation[] getGlobalID() {
		return globalID;
	}

	public void setGlobalID(Operation[] globalID) {
		this.globalID = globalID;
	}

	public Operation[] getOperation() {
		return operation;
	}

	public void setOperation(Operation[] operation) {
		this.operation = operation;
	}

	public Operation[] getStatus() {
		return status;
	}

	public void setStatus(Operation[] status) {
		this.status = status;
	}

	public Operation[] getTime() {
		return time;
	}

	public void setTime(Operation[] time) {
		this.time = time;
	}
}
