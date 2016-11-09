package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class QueryCond implements Serializable {
	private static final long serialVersionUID = 51967387309895715L;
	
	
	public static final String DIRECTION_ASC = "ASC";
	
	
    public static final String DIRECTION_DESC = "DESC";
    
    
    public static final String AND = "AND";
    
    
    public static final String OR = "OR";
    
    
    public static final int ALLROW = -1;
    
    
    private String orderBy;
    
    
    private String orderDirection;
    
    
    private String[] groupBy;
    
    
    private int rowCounts;
    
    
    private int fromNo;
    
    
    private int toNo;
    
    
    public QueryCond() {
        orderBy = "";
        orderDirection = DIRECTION_ASC;
        rowCounts = ALLROW;
        fromNo = 0;
        toNo = rowCounts;
    }
    
    
    public String generateClause(String colName, Operation[] ops) {
    	return generateClause(colName, ops, AND);



















    }
    
    
    public String generateClause(String colName, Operation[] ops, String andOr) {
        if ((null == ops) || (ops.length == 0)) {
            return null;
        }
        
        int j;
        String result = null;
        for(j = 0; j < ops.length; j++) {
        	if(null != ops[j]) {
        		result = colName + ops[j];
        		break;
        	}
        }
        for (int i = j + 1; i < ops.length; i++) {
        	if(null != ops[i]) {
        		result = result + " " + andOr + " " + colName + ops[i];
        	}
        }

        return result;
    }
    
    
    public String generateOrderby() {
    	if(null == orderBy || "".equals(orderBy.trim())) {
    		return null;
    	}
    	String result = "ORDER BY " + orderBy;
    	if(null == orderDirection) {
    		return result;
    	}
    	if(DIRECTION_DESC.equalsIgnoreCase(orderDirection)) {
    		return result + " " + DIRECTION_DESC;
    	} else {
    		return result + " " + DIRECTION_ASC;
    	}
    }
    
    
    public String generateGroupBy() {
    	if(null == groupBy || groupBy.length == 0) {
    		return null;
    	}
    	
    	int j;
    	String result = null;
        for(j = 0; j < groupBy.length; j++) {
        	if(null != groupBy[j] && !"".equals(groupBy[j])) {
        		result = "GROUP BY " + groupBy[j];
        		break;
        	}
        }
        
    	for(int i = j + 1; i < groupBy.length; i++) {
    		if(null != groupBy[i] && !"".equals(groupBy[i])) {
    			result = result + "," + groupBy[i];
    		}
    	}
    	return result;
    }

	public int getFromNo() {
		return fromNo;
	}

	public void setFromNo(int fromNo) {
		this.fromNo = fromNo;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	
	public String[] getGroupBy() {
		return this.groupBy;
	}
	
	public void setGroupBy(String[] str) {
		this.groupBy = str;
	}

	public int getRowCounts() {
		return rowCounts;
	}

	public void setRowCounts(int rowCounts) {
		this.rowCounts = rowCounts;
	}

	public int getToNo() {
		return toNo;
	}

	public void setToNo(int toNo) {
		this.toNo = toNo;
	}
    
}
