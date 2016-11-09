package com.netease.backend.db.common.sql;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.management.Cluster;
import com.netease.backend.db.common.schema.ColumnInfo;
import com.netease.backend.db.common.schema.Policy;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.schema.dbengine.ColumnTypeMySQL;
import com.netease.backend.db.common.schema.dbengine.ColumnTypeOracle;
import com.netease.backend.db.common.utils.Validator;



public class SCreateView extends Statement {
    private static final long serialVersionUID = -969235436794937670L;
    
    
    private String viewName;
    
    private List<String> specifiedColumnNames;
    
    private List<SimpleExpression> selectColumns;
    
    private List<SimpleExpression> tables;
    
    private List<String> conditions;
    
    
    private String type;
    
    private String policyName;
    
    private List<String> balanceFields;
    
    private String viewSql;
    
    private DbnType dbnType;
    
    public SCreateView(String name, List<String> speColumnNames,
            List<SimpleExpression> selectCols, List<SimpleExpression> tables,
            List<String> conditions, DbnType dbntype) {
        this.viewName = name;
        this.specifiedColumnNames = speColumnNames == null ? new ArrayList<String>()
                : speColumnNames;
        this.selectColumns = selectCols == null ? new ArrayList<SimpleExpression>()
                : selectCols;
        this.tables = tables == null ? new ArrayList<SimpleExpression>()
                : tables;
        this.conditions = conditions == null ? new ArrayList<String>()
                : conditions;
        this.dbnType = dbntype;
    }
    
    public TableInfo checkValid(Cluster cluster) throws SQLException {
        
        Policy p = null;
        List<String> baseTableNames = new ArrayList<String>();
        int bfCount = 0;
        for (SimpleExpression e : tables) {
            TableInfo ti = cluster.getTableInfo(e.name);
            if (null == ti)
                throw new SQLException("Table '" + e.name + "' not found.");
            baseTableNames.add(e.name);
            
            if (null != p) {
                if (!ti.getBalancePolicy().getName().equals(p.getName()))
                    throw new SQLException("Table " + e.name + "'s policy '" + ti.getBalancePolicy().getName() + "' not matched with others.");
            } else {
                p = ti.getBalancePolicy();
                if (null != policyName && !p.getName().equals(policyName))
                    throw new SQLException("Policy '" + policyName + "' not matched with tables' policy '" + p.getName() + "'.");
            }
            
            
            int currCount = ti.getBalanceFieldColumns().size();
            if (bfCount != 0 && currCount != bfCount)
            	throw new SQLException("Table " + e.name 
            			+ "'s balance field count not matched with others.");
            bfCount = currCount;
        }
        
        
        if (tables.size() > 1  && p.getBucketCount() > 1 && conditions.size() > 0) {
        	List<List<List<String>>> bfConds = new ArrayList<List<List<String>>>(bfCount);
        	for (int i = 0; i < bfCount; i++)
        		bfConds.add(new ArrayList<List<String>>());
        	
            
        	for (SimpleExpression e : tables) {
                TableInfo ti = cluster.getTableInfo(e.name);
                String balanceFieldName = null;
	            List<String> balanceFields = ti.getBalanceFields();
	            for (String condition : conditions) {
	                condition = condition.replaceAll("(\\s)*=(\\s)*", "="); 
	                
	                int index = -1;
	                int bfIndex = 0;
					for (; bfIndex < balanceFields.size(); bfIndex++) {
						if ((index = condition.indexOf(balanceFields
								.get(bfIndex) + "=")) != -1) {
							balanceFieldName = balanceFields.get(bfIndex);
							break;
						}
					}
	                if (-1 == index)
	                    continue;
	                if (index > 0 && condition.charAt(index - 1) == '.') {
	                    
	                    String str = condition.substring(0, index - 1).trim();
	                    int pos = str.lastIndexOf("\\s");
	                    if (-1 == pos)
	                        pos = 0;
	                    String tname = str.substring(pos).trim();
	                    if (!tname.equals(e.name) && !tname.equals(e.alias))
	                        continue;
	                }
	                int start = index + balanceFieldName.length() + 1;
	                int end = condition.indexOf("\\s", start);
	                if (-1 == end)
	                    end = condition.length();
	                
	                String value = condition.substring(start, end).trim();
	                index = value.indexOf(".");
	                if (index != -1) {
	                	String t2 = value.substring(0, index).trim();
	                	if (t2.equals(e.name) || t2.equals(e.alias))
	                		continue;
	                	
	                	boolean isFound = false;
	                	for (SimpleExpression e2 : tables) {
	                		if (t2.equals(e2.name) || t2.equals(e2.alias)) {
	                			t2 = e2.name;
	                			isFound = true;
	                			break;
	                		}
	                	}
	                	if (!isFound)
	                		throw new SQLException("Table " + t2 + " can not found in view base tables");
	                	
	                	
	                	TableInfo ti2 = cluster.getTableInfo(t2);
	                	if (value.substring(index + 1, value.length()).trim().equals(ti2.getBalanceFields().get(bfIndex))) {
	                		List<String> bfs = new ArrayList<String>();
	                		bfs.add(e.name);
	                		bfs.add(t2);
	                		boolean existed = false;
	                		for (List<String> strList : bfConds.get(bfIndex)) {
	                			if (Validator.isStrSetEquals(strList, bfs)) {
	                				existed = true;
	                				break;
	                			}
	                		}
	                		if (!existed)
	                			bfConds.get(bfIndex).add(bfs);
	                	}
	                }
	            }
        	}
        	
        	for (List<List<String>> bfConditions1 : bfConds) {
	        	if (bfConditions1.size() < tables.size() - 1)
	                throw new SQLException("If policy having more than one bucket and view not depending on only one table, " 
	                		+ "sql should have equal conditions on tables' all balanceFields.");
	        	}
        }
        
        
        
        List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
        
        String varchar = dbnType == DbnType.MySQL ? ColumnTypeMySQL.VARCHAR.name() : ColumnTypeOracle.VARCHAR2.name();
        String decimal = dbnType == DbnType.MySQL ? ColumnTypeMySQL.DECIMAL.name() : ColumnTypeOracle.DECIMAL.name();
        if (specifiedColumnNames.size() > 0) {
            int columnCounts = 0;
            for (SimpleExpression e : selectColumns) {
                if (e.name.equals("*")) {
                    for (SimpleExpression t : tables) {
                        for (ColumnInfo ci : cluster.getTableInfo(t.name).getColumns()) {
                            if (columnCounts >= specifiedColumnNames.size())
                                throw new SQLException(
                                        "View's SELECT and view's field list have different column counts!");
                            ci = new ColumnInfo(specifiedColumnNames
                                    .get(columnCounts), ci.getTypeName(), ci
                                    .getLength(), ci.isUnique(), dbnType);
                            columns.add(ci);
                            ++columnCounts;
                        }
                    }
                } else {
                    if (columnCounts >= specifiedColumnNames.size())
                        throw new SQLException(
                                "View's SELECT and view's field list have different column counts!");
                    ColumnInfo c = null;
                    int pos = e.name.indexOf('.');
                    if (pos > 0) {
                        String tname = e.name.substring(0, pos).trim();
                        String cname = e.name.substring(pos + 1).trim();
                        for (SimpleExpression t : tables) {
                            if (tname.equals(t.alias) || tname.equals(t.name)) {
                                tname = t.name;
                                break;
                            }
                        }
                        TableInfo ti = cluster.getTableInfo(tname);
                        if (null != ti)
                            c = ti.getColumnInfo(cname);
                    } else {
                        for (SimpleExpression t : tables) {
                            for (ColumnInfo ci : cluster.getTableInfo(t.name).getColumns()) {
                                if (e.name.equals(ci.getName())) {
                                    c = ci;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (null == c) {
                    	if (dbnType == DbnType.MySQL && (e.name.startsWith("!")
                                || e.name.toUpperCase().startsWith("NOT"))) {
                            columns.add(new ColumnInfo(specifiedColumnNames.get(columnCounts), 
                            ColumnTypeMySQL.BOOLEAN.name(), false, dbnType));
                    	}
                        else if (e.name.toUpperCase().startsWith("FORMAT")
                        		|| e.name.indexOf('-') > 0
                                || e.name.indexOf('+') > 0
                                || e.name.indexOf('*') > 0
                                || e.name.indexOf('/') > 0
                                || e.name.toUpperCase().indexOf("DIV") > 0
                                || e.name.toUpperCase().indexOf("MOD") > 0)
                            columns.add(new ColumnInfo(specifiedColumnNames.get(columnCounts), 
                            decimal, false, dbnType));
                        else
                            columns.add(new ColumnInfo(specifiedColumnNames.get(columnCounts), 
                            varchar, false, dbnType));
                    } else
                        columns.add(new ColumnInfo(specifiedColumnNames
                                .get(columnCounts), c.getType(), c.isUnique(), dbnType));
                    ++columnCounts;
                }
            }
        } else {
            for (SimpleExpression e : selectColumns) {
                if (e.name.equals("*")) {
                    for (SimpleExpression t : tables) 
                        columns.addAll(cluster.getTableInfo(t.name).getColumns());
                } else {
                    ColumnInfo c = null;
                    int pos = e.name.indexOf('.');
                    if (pos > 0) {
                        String tname = e.name.substring(0, pos).trim();
                        String cname = e.name.substring(pos + 1).trim();
                        for (SimpleExpression t : tables) {
                            if (tname.equals(t.alias) || tname.equals(t.name)) {
                                tname = t.name;
                                break;
                            }
                        }
                        TableInfo ti = cluster.getTableInfo(tname);
                        if (null != ti) {
                            c = ti.getColumnInfo(cname);
                            e.setName(cname);
                        }
                    } else {
                        for (SimpleExpression t : tables) {
                            for (ColumnInfo ci : cluster.getTableInfo(t.name).getColumns()) {
                                if (e.name.equalsIgnoreCase(ci.getName())) {
                                    c = ci;
                                    break;
                                }
                            }
                        }
                    }
                    if (null == c) {
                    	if (dbnType == DbnType.MySQL && (e.name.startsWith("!")
                                || e.name.toUpperCase().startsWith("NOT"))) {
                            columns.add(new ColumnInfo(e.getAlias(),
                                    ColumnTypeMySQL.BOOLEAN.name(), false, dbnType));
                    	} else if (e.name.toUpperCase().startsWith("FORMAT")
                    			|| e.name.indexOf('-') >= 0
                                || e.name.indexOf('+') > 0
                                || e.name.indexOf('*') > 0
                                || e.name.indexOf('/') > 0
                                || e.name.toUpperCase().indexOf("DIV") > 0
                                || e.name.toUpperCase().indexOf("MOD") > 0)
                            columns.add(new ColumnInfo(e.getAlias(), decimal, false, dbnType));
                        else
                            columns.add(new ColumnInfo(e.getAlias(), varchar, false, dbnType));
                    } else
                        columns.add(new ColumnInfo(e.getAlias(), c.getType(), c
                                .isUnique(), dbnType));
                }
            }
        }
        
        byte balanceType = 0; 
        TableInfo t = new TableInfo(viewName, type, columns, balanceFields, balanceType, 
            p, baseTableNames, viewSql, dbnType);
        return t;
    }
    
    public String getViewName() {
        return viewName;
    }
    
    public List<String> getSpecifiedColumnNames() {
        return specifiedColumnNames;
    }
    
    public List<SimpleExpression> getSelectColumns() {
        return selectColumns;
    }
    
    public List<SimpleExpression> getTables() {
        return tables;
    }
    
    public List<String> getConditions() {
        return conditions;
    }

    public String getPolicyName() {
        return policyName;
    }
    
    public void setPolicyName(String ply) {
        this.policyName = ply;
    }
    
    public void setBalanceFields(List<String> fields) {
    	this.balanceFields = fields;
    }
    
    public List<String> getBalanceFields() {
    	return this.balanceFields;
    }
    
    public String getViewSql() {
        return viewSql;
    }
    
    public void setViewSql(String sql) {
        this.viewSql = sql;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String t) {
        this.type = t;
    }
    
    public DbnType getDbnType() {
    	return this.dbnType;
    }
    



}

class SimpleExpression implements Serializable {
    private static final long serialVersionUID = -3116357493049171552L;
    String name;
    String alias;
    
    SimpleExpression(String name) {
        this.name = name;
    }
    
    void setAlias(String alias) {
        this.alias = alias;
    }
    
    String getName() {
        return name;
    }
    
    void setName(String n) {
    	this.name = n;
    }
    
    String getAlias() {
        return alias == null ? name : alias;
    }
    
    public String toString() {
        return null != alias ? name + " AS " + alias : name;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof SimpleExpression))
            return false;
        return ((SimpleExpression) obj).getAlias().equals(getAlias());
    }
}
