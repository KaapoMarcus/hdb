package com.netease.backend.db.common.sql;

import java.io.Serializable;


public class DLOption implements Serializable {
	private static final long serialVersionUID = -4958008059545377358L;

	private String lineSeparator;
	private String attrDelimiter;
	private char attrQuoter;
	private String charset;

	public DLOption() {
		lineSeparator = "\n";
		attrDelimiter = "\t";
		attrQuoter = '\0';
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public String getAttrDelimiter() {
		return attrDelimiter;
	}

	public void setAttrDelimiter(String attrDelimiter) {
		this.attrDelimiter = attrDelimiter;
	}

	public char getAttrQuoter() {
		return attrQuoter;
	}

	public void setAttrQuoter(char attrQuoter) {
		this.attrQuoter = attrQuoter;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
