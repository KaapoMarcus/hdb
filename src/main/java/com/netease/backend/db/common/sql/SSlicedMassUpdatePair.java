package com.netease.backend.db.common.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SSlicedMassUpdatePair implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Long> values = new ArrayList<Long>();

	public SSlicedMassUpdatePair() {
		super();
	}

	public SSlicedMassUpdatePair(List<Long> values) {
		super();
		this.values = values;
	}

	public SSlicedMassUpdatePair(int elementcount) {
		for (int i = 0; i < elementcount; i++) {
			values.add(new Long(0));
		}
	}

	
	public static SSlicedMassUpdatePair parse(String valuestr) {
		SSlicedMassUpdatePair ret = new SSlicedMassUpdatePair();
		try {
			ret.values.add(Long.parseLong(valuestr));
		} catch (NumberFormatException e) {
			String parsestr = valuestr.trim();
			if (parsestr.startsWith("(") && parsestr.endsWith(")")) {
				parsestr = parsestr.substring(1, parsestr.length() - 1);
				String[] splits = parsestr.split(",");
				for (int i = 0; i < splits.length; i++) {
					ret.values.add(Long.parseLong(splits[i].trim()));
				}
			} else {
				throw new IllegalArgumentException("�����ַ�����ʽ����:" + valuestr);
			}
		}
		return ret;
	}

	
	public int compare(SSlicedMassUpdatePair other) {
		if (this.values.size() != other.values.size()) {
			throw new IllegalArgumentException("�޷�����Ԫ��Ƚϣ�����Ԫ�ظ�������");
		}

		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) > other.values.get(i)) {
				return 1;
			} else if (values.get(i) < other.values.get(i)) {
				return -1;
			} else {
				continue;
			}
		}

		return 0;
	}

	public List<Long> getValues() {
		return values;
	}

	public void setValues(List<Long> values) {
		this.values = values;
	}

	public String toString() {
		if (values.size() == 0) {
			return "";
		} else if (values.size() == 1) {
			return values.get(0).toString();
		} else {
			String ret = "(";
			for (int i = 0; i < values.size(); i++) {
				ret += values.get(i).toString()
						+ (i != values.size() - 1 ? "," : "");
			}
			return ret + ")";
		}
	}

	public boolean morethan(SSlicedMassUpdatePair other) {
		return this.compare(other) == 1;
	}

	public boolean lessthan(SSlicedMassUpdatePair other) {
		return this.compare(other) == -1;
	}

	public boolean equalto(SSlicedMassUpdatePair other) {
		return this.compare(other) == 0;
	}

	
	public void add(long value) {
		values.set(values.size() - 1, values.get(values.size() - 1) + value);
	}

	
	public String getPrepareStatementSyntax() {
		if (values.size() == 0) {
			return "";
		} else if (values.size() == 1) {
			return "?";
		} else {
			String ret = "(";
			for (int i = 0; i < values.size(); i++) {
				ret += "?" + (i != values.size() - 1 ? "," : "");
			}
			return ret + ")";
		}
	}
}
