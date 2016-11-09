package com.netease.backend.db.common.sql;


public class SShowStatAnalysis extends Statement {
	private static final long serialVersionUID = 1L;
	
	private boolean hasForgetBF;
	private boolean hasForgetIndexOnBF;
	private boolean hasUnusedIndex;
	private boolean hasIndexAdvice;
	private boolean hasLowCardIndex;
	private boolean hasUsingIndex;
	private boolean hasDeadlockDdb;
	private boolean hasDeadlockMysql;
	private boolean hasIgnoredIndex;
	private int indexAdviceLengthLimit;
	private int lowCard;
	private int usingIndexMaxAttrs;
	private int usingIndexMaxLength;
	
	public boolean isHasIndexAdvice() {
		return hasIndexAdvice;
	}
	
	public void setHasIndexAdvice(boolean hasIndexAdvice) {
		this.hasIndexAdvice = hasIndexAdvice;
	}
	
	public boolean isHasDeadlockDdb() {
		return hasDeadlockDdb;
	}
	
	public void setHasDeadlockDdb(boolean hasDeadlockDdb) {
		this.hasDeadlockDdb = hasDeadlockDdb;
	}
	
	public boolean isHasDeadlockMysql() {
		return hasDeadlockMysql;
	}
	
	public void setHasDeadlockMysql(boolean hasDeadlockMysql) {
		this.hasDeadlockMysql = hasDeadlockMysql;
	}
	
	public boolean isHasForgetBF() {
		return hasForgetBF;
	}
	
	public void setHasForgetBF(boolean hasForgetBF) {
		this.hasForgetBF = hasForgetBF;
	}
	
	public boolean isHasForgetIndexOnBF() {
		return hasForgetIndexOnBF;
	}
	
	public void setHasForgetIndexOnBF(boolean b) {
		this.hasForgetIndexOnBF = b;
	}
	
	public boolean isHasIgnoredIndex() {
		return hasIgnoredIndex;
	}
	
	public void setHasIgnoredIndex(boolean hasIgnoredIndex) {
		this.hasIgnoredIndex = hasIgnoredIndex;
	}
	
	public boolean isHasLowCardIndex() {
		return hasLowCardIndex;
	}
	
	public void setHasLowCardIndex(boolean hasLowCardIndex) {
		this.hasLowCardIndex = hasLowCardIndex;
	}
	
	public boolean isHasUnusedIndex() {
		return hasUnusedIndex;
	}
	
	public void setHasUnusedIndex(boolean hasUnusedIndex) {
		this.hasUnusedIndex = hasUnusedIndex;
	}
	
	public boolean isHasUsingIndex() {
		return hasUsingIndex;
	}
	
	public void setHasUsingIndex(boolean hasUsingIndex) {
		this.hasUsingIndex = hasUsingIndex;
	}
	
	public int getUsingIndexMaxAttrs() {
		return usingIndexMaxAttrs;
	}
	
	public void setUsingIndexMaxAttrs(int usingIndexMaxAttrs) {
		this.usingIndexMaxAttrs = usingIndexMaxAttrs;
	}
	
	public int getUsingIndexMaxLength() {
		return usingIndexMaxLength;
	}
	
	public void setUsingIndexMaxLength(int usingIndexMaxLength) {
		this.usingIndexMaxLength = usingIndexMaxLength;
	}
	
	public int getLowCard() {
		return lowCard;
	}
	
	public void setLowCard(int cardinality) {
		this.lowCard = cardinality;
	}
	
	public int getIndexAdviceLengthLimit() {
		return indexAdviceLengthLimit;
	}
	
	public void setIndexAdviceLengthLimit(int limit) {
		this.indexAdviceLengthLimit = limit;
	}
}
