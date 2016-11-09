package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class DDBAnalysisOperation implements Serializable {
	private static final long serialVersionUID = -7738927591276736443L;
	
	
	public static final int FIND_MISSING_CONDITION = 0;
	
	public static final int FIND_TABLE_NO_INDEX_ON_BF = 1;
	
	public static final int FIND_UNUSED_INDEX = 2;
	
	public static final int FIND_TABLE_NEED_INDEX = 3;
	
	public static final int FIND_LOW_CARDINALITY_INDEX = 4;
	
	public static final int FIND_NEGLECTED_INDEX = 5;
	
	public static final int FIND_EXTENDED_INDEX = 6;
	
	public static final int FIND_DEADLOCK_DDBSTMT = 7;
	
	public static final int FIND_DEADLOCK_MYSQLSTMT = 8;
	
	public static final int DIFFER_STATS = 9;
	
	private int operation;
	
	private int rowsPerValueLimit;
	private int columnCountLimit;
	private int lengthLimit;
	private int indexLengthLimit;
	
	public DDBAnalysisOperation(int op) {
		this.operation = op;
	}
	
	public int getOperation() {
		return this.operation;
	}
	
	public void setOperation(int op) {
		this.operation = op;
	}
	
	public int getCardinalityLimit() {
		return rowsPerValueLimit;
	}
	
	public void setCardinalityLimit(int cardinalityLimit) {
		this.rowsPerValueLimit = cardinalityLimit;
	}
	
	public int getColumnCountLimit() {
		return columnCountLimit;
	}
	
	public void setColumnCountLimit(int columnCountLimit) {
		this.columnCountLimit = columnCountLimit;
	}
	
	public int getLengthLimit() {
		return lengthLimit;
	}
	
	public void setLengthLimit(int lengthLimit) {
		this.lengthLimit = lengthLimit;
	}
	
	public int getIndexLengthLimit() {
		return indexLengthLimit;
	}
	
	public void setIndexLengthLimit(int limit) {
		this.indexLengthLimit = limit;
	}
	
	public String toString() {
		String result = "";
		switch (operation) {
		case FIND_MISSING_CONDITION:
			result = "�������ǼӾ����ֶ�������ִ�����";
			break;
		case FIND_TABLE_NO_INDEX_ON_BF:
			result = "���������ֶ����Ǽ������ı�";
			break;
		case FIND_UNUSED_INDEX:
			result = "�����Ӳ���ʹ�õ�����";
			break;
		case FIND_TABLE_NEED_INDEX:
			String indexLimitStr = "��������������";
			if (indexLengthLimit > 0)
				indexLimitStr = "������������Ϊ" + indexLengthLimit;
			result = "������Ҫ���������ı�" + indexLimitStr;
			break;
		case FIND_LOW_CARDINALITY_INDEX:
			result = "�������ֶȲ��ߵ�������RowsPerValue����" + rowsPerValueLimit;
			break;
		case FIND_NEGLECTED_INDEX:
			result = "ͳ����Ϊ��ѡ����ȴ��Mysql���Ե�����";
			break;
		case FIND_EXTENDED_INDEX:
			result = "�������Խ���Using Index�Ż���Ǳ���������ֶ�������" + columnCountLimit + "���ֶ��ܳ�������" + lengthLimit + "byte";
			break;
		case FIND_DEADLOCK_DDBSTMT:
			result = "��������������DDBִ�����";
			break;
		case FIND_DEADLOCK_MYSQLSTMT:
			result = "��������������Mysqlִ�����";
			break;
		case DIFFER_STATS:
			result = "�Ƚ�����ͳ�ƽ��";
			break;
		default:
			result = "����ʶ��ķ�������";
		}
		return result;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof DDBAnalysisOperation) {
			DDBAnalysisOperation op = (DDBAnalysisOperation) obj;
			if (op.getOperation() == operation) {
				if (operation == FIND_TABLE_NEED_INDEX) {
					if (indexLengthLimit == op.getIndexLengthLimit())
						return true;
					if (indexLengthLimit <= 0 && op.getIndexLengthLimit() <= 0)
						return true;
					return false;
				}
				if (operation == FIND_LOW_CARDINALITY_INDEX)
					return rowsPerValueLimit == op.getCardinalityLimit();
				if (operation == FIND_EXTENDED_INDEX)
					return columnCountLimit == op.getColumnCountLimit()
					&& lengthLimit == op.getLengthLimit();
				return true;
			}
		}
		return false;
	}
}
