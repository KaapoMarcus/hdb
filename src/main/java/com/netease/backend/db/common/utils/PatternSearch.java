package com.netease.backend.db.common.utils;


public class PatternSearch {
	private char quote = '\'';

	private String target;

	private boolean inQuote = false;

	private int fromIndex = 0;

	public PatternSearch(String target) {
		super();
		this.target = target;
	}

	public char getQuote() {
		return quote;
	}

	public void setQuote(char quote) {
		this.quote = quote;
	}

	public void reset() {
		fromIndex = 0;
		inQuote = false;
	}

	
	public int searchPattern(char pattern) {
		if (pattern == quote) {
			throw new IllegalArgumentException(
					"search pattern can not equal with quote");
		}

		for (; fromIndex < target.length(); fromIndex++) {
			char c = target.charAt(fromIndex);
			if (inQuote) {
				if (c == quote)
					inQuote = false;
				continue;
			} else {
				if (c == quote) {
					inQuote = true;
					continue;
				} else if (c == pattern)
					return fromIndex++;
			}
		}

		return -1;
	}

}
