
package com.netease.backend.db.common.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.management.PIDConfig;
import com.netease.backend.db.common.schema.ColumnInfo;
import com.netease.backend.db.common.schema.DbnCluster;
import com.netease.backend.db.common.schema.DbnClusterColumn;
import com.netease.backend.db.common.schema.IndexColumn;
import com.netease.backend.db.common.schema.IndexInfo;
import com.netease.backend.db.common.schema.TableInfo;


public class DDLParserOracle {

	private SQLLexParser parser;

	private String tableName;

	private List<ColumnInfo> columns = new LinkedList<ColumnInfo>();

	private IndexInfo primaryKey = null;

	private HashMap<String, IndexInfo> indexMap = new HashMap<String, IndexInfo>();

	private boolean useBucketNo = false;
	
	private boolean columnDefinitionEnd = false;
	
	private boolean isEnd = false; 
	
	
	public Statement parseOracle(String sql) throws SQLException {
		if (!sql.endsWith(";"))
			sql = sql + ";";
		Statement r = null;
		try {
			parser = SQLLexParser.getLexParser(sql);
		} catch (SQLException e) {
			
			StringTokenizer tokenizer = new StringTokenizer(sql);
			if ("CREATE".equalsIgnoreCase(DDLParserUtils.getNextToken(tokenizer)))
				r = parseCreateTrProc(sql);
			if (r != null)
				return r;
			else
				throw e;
		}
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("CREATE")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("OR"))
				DDLParserUtils.readAndMatch(parser, "REPLACE");
			if (parser.getCurrentToken().equalsIgnoreCase("TABLE")) {
				r = parseCreateTable(sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("CLUSTER")) {
				r = parseCreateDbnCluster(sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("INDEX")) {
				parser.read();
				String indexName = DDLParserUtils.readIdentifier(parser, "index name");
				DDLParserUtils.match(parser, "ON");
				String name = DDLParserUtils.readIdentifier(parser, "table name");
				if (name.equalsIgnoreCase("CLUSTER")) {
					
					r = parserCreateClusterIndex(name, indexName, sql);
				} else {
					
					tableName = name;
					r = parseCreateIndex(indexName, IndexInfo.INDEX_TYPE_BTREE, false, sql);
				}
			} else if (parser.getCurrentToken().equalsIgnoreCase("BITMAP")) {
				
				DDLParserUtils.readAndMatch(parser, "INDEX");
				String indexName = DDLParserUtils.readIdentifier(parser, "index name");
				DDLParserUtils.match(parser, "ON");
				tableName = DDLParserUtils.readIdentifier(parser, "table name");
				r = parseCreateIndex(indexName, IndexInfo.INDEX_TYPE_BITMAP, false, sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("UNIQUE")) {
				
				parser.read();
				if (!parser.getCurrentToken().equalsIgnoreCase("INDEX"))
					throw new SQLException("Expect INDEX but was " + parser.getCurrentToken());
				parser.read();
				String indexName = DDLParserUtils.readIdentifier(parser, "index name");
				DDLParserUtils.match(parser, "ON");
				tableName = DDLParserUtils.readIdentifier(parser, "table name");
				r = parseCreateIndex(indexName, IndexInfo.INDEX_TYPE_BTREE, true, sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("VIEW")) {
				parser.read();
				r = parseCreateView(sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("OR")) {
				
				DDLParserUtils.readAndMatch(parser, "REPLACE");
				String token = parser.getCurrentToken();
				if (!(token.equalsIgnoreCase("TRIGGER") || token.equalsIgnoreCase("PROCEDURE")))
					throw new SQLException("Syntax err, expected 'TRIGGER'or'PROCEDURE' after 'OR REPLACE'");
				return parseCreateTrProc(sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("TRIGGER")
					|| parser.getCurrentToken().equalsIgnoreCase("PROCEDURE")) {
				
				return parseCreateTrProc(sql);
			} else
				throw new SQLException("Unknown command: " + sql + ".");
		} else if (parser.getCurrentToken().equalsIgnoreCase("ALTER")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("TABLE")) {
				parser.read();
				r = parseAlterTable(sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("CLUSTER")) {
				
				parser.read();
				r = parserAlterDbnCluster(sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("VIEW")) { 
				throw new SQLException("Syntax err, 'ALTER VIEW' is not support in Oracle, 'CREATE OR REPLACE VIEW' is suggested");
			} else
				throw new SQLException("Unknown command: " + sql + ".");
		} else if (parser.getCurrentToken().equalsIgnoreCase("COMMENT")) { 
			DDLParserUtils.readAndMatch(parser, "ON");
			if (parser.readIf("TABLE")) {
				
				tableName = DDLParserUtils.readIdentifier(parser, "table name");
				DDLParserUtils.match(parser, "IS");
				if((!parser.getCurrentToken().equals("'")) || parser.getCurrentToken().equals(";")
						|| parser.getCurrentToken().equals(","))
					throw new SQLException("Syntax error, you should specify the comment like IS 'string'.");
				String comment = DDLParserUtils.getTokenOrValue(parser, false);
				parser.read();
				
				SAlterTableComment sCom = new SAlterTableComment(comment);
				sCom.setClauseSql(null);
				sCom.setSql(sql.substring(0, sql.length() - 1));
				List<SAlterTableOp> ps = new LinkedList<SAlterTableOp>();
				ps.add(sCom);
				r = new SAlterTable(tableName, ps);
			} else if (parser.readIf("COLUMN")) { 
				
				tableName = DDLParserUtils.readIdentifier(parser, "table name");
				DDLParserUtils.match(parser, ".");
				String column = DDLParserUtils.readIdentifier(parser, "column name");
				DDLParserUtils.match(parser, "IS");
				if((!parser.getCurrentToken().equals("'")) || parser.getCurrentToken().equals(";")
						|| parser.getCurrentToken().equals(","))
					throw new SQLException("Syntax error, you should specify the comment like IS 'string'.");
				String comment = DDLParserUtils.getTokenOrValue(parser, false);
				parser.read();
				
				SAlterTableCommentColumn sComClm = new SAlterTableCommentColumn(column, comment);
				sComClm.setClauseSql(null);
				sComClm.setSql(sql.substring(0, sql.length() - 1));
				List<SAlterTableOp> ps = new LinkedList<SAlterTableOp>();
				ps.add(sComClm);
				r = new SAlterTable(tableName, ps);
			} else
				throw new SQLException("Syntax err, expected 'TABLE' or 'COLUMN' but was '" 
						+ parser.getCurrentToken() + "'");
			((SAlterTable)r).setDbnType(DbnType.Oracle);
		}
		DDLParserUtils.match(parser, ";");
		return r;
	}

	private SCreateTable parseCreateTable(String sql) throws SQLException {
		parser.read();
		tableName = DDLParserUtils.readIdentifier(parser, "table name");
		DDLParserUtils.match(parser, "(");
		
		while (true) {
			ColumnInfo lastColumn = null;
			if (parser.getCurrentToken().equalsIgnoreCase("REFERENCES")) {
				throw new SQLException("Foreign key is not supported in DDB");
			} else if (parser.getCurrentToken().equalsIgnoreCase("INDEX")
					|| parser.getCurrentToken().equalsIgnoreCase("KEY")) {
				throw new SQLException("Syntax err, 'INDEX' or 'KEY' is not supported in Oracle outline_constraint");
			} else if (parser.getCurrentToken().equalsIgnoreCase("CHECK")) {
				throw new SQLException("CHECK constraint is not supported in DDB");
			} else if (parser.getCurrentToken().equalsIgnoreCase("UNIQUE")) {
				readUnique();
			} else if (parser.readIf("PRIMARY")) {
				readPrimaryKey();
			} else {
				lastColumn = readColumn();
				
				
				int lp = 0;
				while (!this.columnDefinitionEnd) {
					String t = DDLParserUtils.getTokenOrValue(parser, true);
					if (t.equals(",") && lp == 0) {
						break;
					} else if (t.equals("(")) {
						lp++;
					} else if (t.equals(")")) {
						if (lp == 0) {
							this.columnDefinitionEnd = true;
							break;
						} else
							lp--;
					} else if (t.equalsIgnoreCase("primary") && lastColumn != null) {
						if (primaryKey != null)
							throw new SQLException("Table '" + tableName
									+ "' has more than one primary key difinitions.");
						parser.read();
						if (!"KEY".equalsIgnoreCase(parser.getCurrentToken()))
							throw new SQLException("Syntax error, expect 'KEY', but was '"
									+ parser.getCurrentToken() + "'.");
						
						String tblSp = readConstraintState();
						
						ArrayList<String> keys = new ArrayList<String>();
						keys.add(lastColumn.getName());
						primaryKey = new IndexInfo("PRIMARY", tableName, keys, true, true);
						primaryKey.setTableSpace(tblSp);
						indexMap.put("PRIMARY", primaryKey);
						break;
					} else if (t.equalsIgnoreCase("unique") && lastColumn != null) {
						
						String tblSp = readConstraintState();
						
						ArrayList<String> keys = new ArrayList<String>();
						keys.add(lastColumn.getName());
						IndexInfo index = new IndexInfo(lastColumn.getName(), tableName, keys, false, true);
						index.setTableSpace(tblSp);
						index.setOracleUniqueConstraint(true);
						if (indexMap.get(index.getIndexName()) != null)
							throw new SQLException("Index redifined: " + index.getIndexName());
						indexMap.put(index.getIndexName(), index);
						break;
					} else if ((t.equalsIgnoreCase("GENERATED") || t.equalsIgnoreCase("AS"))
							&& lastColumn != null) {
						
						
						if (t.equalsIgnoreCase("GENERATED")) {
							DDLParserUtils.readAndMatch(parser, "ALWAYS");
							DDLParserUtils.match(parser, "AS");
						} else {
							parser.read();
						}
						
						String expr = readStringInBrackets(sql);
						if (null == expr || (null != expr && expr.trim().equals("")))
							throw new SQLException("Syntax err, virtual column definition is null");
						lastColumn.setVirtual(true);
						lastColumn.setExpression(expr); 
						continue;
					}
					parser.read();
				}
			}

			if (this.columnDefinitionEnd)
				break;
			parser.read();
		}

		TableInfo table = new TableInfo(tableName, columns, indexMap, DbnType.Oracle);
		table.setUseBucketNo(useBucketNo);
		
		
		table.setAssignIdType(-1);
		
		
		String token;
		String policyName = null;
		int bucketCount = -1;
		String tableSpace = null;
		String clusterName = null;
		
		ArrayList<String> columnNames = null;
		String tableType = TableInfo.TYPE_HEAP_ORGANIZED;
		parser.read();
		int lp = 0;
		while (!(token = parser.getCurrentToken()).equals(";")) {
			if (token.equals("(")) {
				lp++;
			} else if (token.equals(")")) {
				lp--;
			} else if (token.equalsIgnoreCase("TABLESPACE") && lp == 0) {
				
				parser.read();
				tableSpace = DDLParserUtils.readIdentifier(parser, "table space");
				
				if (null != clusterName)
					throw new SQLException("TABLESPACE option is invalid for a clustered table");
				table.setTableSpace(tableSpace);
				continue;
			} else if (token.equalsIgnoreCase("ORGANIZATION")) {
				parser.read();
				String tableTypeStr = DDLParserUtils.readIdentifier(parser, "table type");
				if (tableTypeStr.equalsIgnoreCase("INDEX")) {
					tableType = TableInfo.TYPE_INDEX_ORGANIZED;
					if (null == primaryKey)
						throw new SQLException("A primary constrain must be definied for a table with index organization");
					if (null != clusterName)
						throw new SQLException("A table with index organization can't be a clustered table");
				} else if (tableTypeStr.equalsIgnoreCase("HEAP")) {
					tableType = TableInfo.TYPE_HEAP_ORGANIZED;
				} else if (tableTypeStr.equalsIgnoreCase("EXTERNAL"))
					throw new SQLException("DDB doesn't support table with external organization");
				else 
					throw new SQLException("Expected a table organization type, but was '" + tableTypeStr + "'");
				continue;
			} else if (token.equalsIgnoreCase("CLUSTER")) {
				parser.read();
				
				if (null != tableSpace)
					throw new SQLException("TABLESPACE option is invalid for a clustered table");
				
				if (tableType.equalsIgnoreCase(TableInfo.TYPE_INDEX_ORGANIZED))
					throw new SQLException("A table with index organization can't be a clustered table");
				clusterName = DDLParserUtils.readIdentifier(parser, "cluster name");
				
				if (!parser.getCurrentToken().equals("("))
					throw new SQLException("You should specify cluster column(s) for cluster table");
				columnNames = readIndexColumns(clusterName);
			} else if (token.equalsIgnoreCase("bf") 
					|| token.equalsIgnoreCase("partitionkey")) {
				DDLParserUtils.readAndMatch(parser, "=");
				List<String> fields = readBalanceFields();
				table.setBalanceFields(fields);
			} else if (token.equalsIgnoreCase("policy") 
					|| token.equalsIgnoreCase("tablegroup")) {
				DDLParserUtils.readAndMatch(parser, "=");
				policyName = parser.readIdentifier();
				if (parser.getCurrentToken().equals("(")) {
					parser.read();
					bucketCount = Integer.parseInt(parser.getCurrentToken());
					parser.read();
					if (!parser.getCurrentToken().equals(")"))
						throw new SQLException("Invalid policy definition.");
				} else
					continue;
			} else if (token.equalsIgnoreCase("model")) {
				
				DDLParserUtils.readAndMatch(parser, "=");
				table.setModelName(parser.getCurrentToken());
			} else if (token.equalsIgnoreCase("startid")) {
				DDLParserUtils.readAndMatch(parser, "=");
				Long startId = Long.parseLong(parser.getCurrentValue() + "");
				table.setStartID(startId);
			} else if (token.equalsIgnoreCase("remainid")) {
				DDLParserUtils.readAndMatch(parser, "=");
				Long remainId = Long.parseLong(parser.getCurrentValue() + "");
				table.setRemainIDCount(remainId);
			} else if (token.equalsIgnoreCase("assignidtype")) {
				
				DDLParserUtils.readAndMatch(parser, "=");
				String idAssignTypeStr = parser.getCurrentToken();
				if (idAssignTypeStr.equalsIgnoreCase("msb"))
					table.setAssignIdType(PIDConfig.TYPE_SIMPLE);
				else if (idAssignTypeStr.equalsIgnoreCase("tsb"))
					table.setAssignIdType(PIDConfig.TYPE_TIMEBASED);
				else
					throw new SQLException("idassigntype '" + idAssignTypeStr 
							+ "' doesn't exist. It should be 'msb' or 'tsb'");
			}
			parser.read();
		}
		table.setType(tableType);
		SCreateTable s = new SCreateTable(table, policyName, bucketCount);
		if (null != clusterName) {
			
			s.setDbnClusterName(clusterName);
			s.setClusterColumns(columnNames);
		}
		
		for (IndexInfo index :indexMap.values()) {
			if (null == index.getTableSpace()) {
				index.setTableSpace(tableSpace);
			}
		}
		return s;
	}

	
	
	private SCreateDbnCluster parseCreateDbnCluster(String sql) throws SQLException {
		parser.read();
		String dbnClusterName = DDLParserUtils.readIdentifier(parser, "cluster name");
		DDLParserUtils.match(parser, "(");
		int lp = 0;
		
		List<DbnClusterColumn> columns = new ArrayList<DbnClusterColumn>();
		while (true) {
			DbnClusterColumn column = readDbnClusterColumn(sql);
			String curStr = parser.getCurrentToken();
			if (curStr.equalsIgnoreCase("SORT")) {
				column.setTypeStr(column.getTypeStr() + " SORT");
				DDLParserUtils.readAndMatch(parser, ",");
			} else if (curStr.equals(","))
				parser.read();
			else if (curStr.equals(")")) {
				columns.add(column);
				break;
			}
			else
				throw new SQLException("Syntax err, excpcted ','|')'|'SORT' , but was '" + curStr + "'");
			columns.add(column);
		}
		parser.read();
		
		
		String token;
		String tblSp = null;
		String plyName = null;
		int dbnClusterType = DbnCluster.CLUSTER_TYPE_INDEX;
		int size = 0;
		lp = 0;
		while (!(token = parser.getCurrentToken()).equals(";")) {
			if (token.equals("(")) {
				lp++;
			} else if (token.equals(")")) {
				lp--;
			} else if (token.equalsIgnoreCase("TABLESPACE") && lp == 0) {
				parser.read();
				tblSp = DDLParserUtils.readIdentifier(parser, "table space");
				continue;
			} else if (token.equalsIgnoreCase("INDEX") && lp == 0) {
				dbnClusterType = DbnCluster.CLUSTER_TYPE_INDEX;
			} else if (token.equalsIgnoreCase("HASHKEYS") && lp == 0) {
				
				parser.read();
				String hashKey = parser.getCurrentToken();
				try {
					int keys = Integer.parseInt(hashKey);
					if (keys <= 0)
						throw new SQLException("Syntax err, expected an positive integer as HASHKEYS, but was " + keys);
				} catch (NumberFormatException e) {
					throw new SQLException("Syntax err, expected an integer as HASHKEYS, but was '" + hashKey + "'");
				}
				dbnClusterType = DbnCluster.CLUSTER_TYPE_HASH;
			} else if (token.equalsIgnoreCase("SIZE") && lp == 0) {
				
				parser.read();
				String sizeStr = parser.getCurrentToken();
				try {
					size = Integer.parseInt(sizeStr);
				} catch (NumberFormatException e) {
					throw new SQLException("Syntax err, expected a positive integer as size, but was '" + sizeStr + "'");
				}
				parser.read();
				String unit = parser.getCurrentToken();
				if (unit.length() == 1 && Character.isLetter(unit.charAt(0))) { 
					size = parseClusterSize(size + unit);
				} else 
					continue;
			} else if (token.equalsIgnoreCase("policy")
					|| token.equalsIgnoreCase("tablegroup")) {
				DDLParserUtils.readAndMatch(parser, "=");
				plyName = parser.readIdentifier();
				continue;
			}
			parser.read();
		}
		
		if (size == 0)
			throw new SQLException("Syntax err, the cluster KEY_SIZE must be specified by 'SIZE integer [unit]'");
		DbnCluster dbnCluster = new DbnCluster(dbnClusterName, null, dbnClusterType, 
				null, null, tblSp, size, columns, false);
		return new SCreateDbnCluster(dbnClusterName, plyName, dbnCluster, sql.substring(0, sql.length() - 1));
	}
	
	
	private DbnClusterColumn readDbnClusterColumn(String sql) throws SQLException {
		String columnName = "";
		String curStr = DDLParserUtils.readIdentifier(parser, "column name");
		try {
			ColumnInfo.parseType(curStr, DbnType.Oracle);
			
			throw new SQLException("No column name found.");
		} catch (IllegalArgumentException e) {
			
			columnName = curStr;
		}
		String columnTypeStr = parser.getCurrentToken();
		if (columnTypeStr.equalsIgnoreCase("DOUBLE")) { 
			
			parser.read();
			if (!parser.getCurrentToken().equalsIgnoreCase("PRECISION"))
				throw new SQLException("Syntax error, expect 'PRECISION' after 'DOUBLE'");
			columnTypeStr = "DOUBLE PRECISION";
		}
		parser.read();
		if (parser.getCurrentToken().equals("(")) {
			String str = readStringInBrackets(sql);
			columnTypeStr = columnTypeStr + "(" + str + ")";
		}
		return new DbnClusterColumn(columnName, columnTypeStr);
	}
	
	private SCreateView parseCreateView(String sql) throws SQLException {
		
		String viewName = DDLParserUtils.readIdentifier(parser, "name of view");
		List<String> specifiedColumnNames = new ArrayList<String>();
		if (parser.readIf("(")) {
			
			while (!parser.readIf(")")) {
				if (parser.parseOver())
					throw new SQLException("Syntax error, no matching )");
				String c = parser.readIdentifier();
				specifiedColumnNames.add(c);
				if (parser.getCurrentToken().equals(","))
					parser.read();
			}
		}
		DDLParserUtils.match(parser, "AS");
		DDLParserUtils.match(parser, "SELECT");
		List<SimpleExpression> columns = new ArrayList<SimpleExpression>();
		
		do {
			
			if (parser.getCurrentToken().equalsIgnoreCase("ALL")
					|| parser.getCurrentToken().equalsIgnoreCase("DISTINCT")
					|| parser.getCurrentToken().equalsIgnoreCase("UNIQUE"))
				parser.read();
			if (parser.readIf("*"))
				columns.add(new SimpleExpression("*"));
			else
				columns.add(readExpression(sql));
		} while (parser.readIf(","));
		DDLParserUtils.match(parser, "FROM");
		
		List<SimpleExpression> tables = new ArrayList<SimpleExpression>();
		tables.addAll(readTable());
		List<String> conditions = new ArrayList<String>();
		
		if (parser.readIf("WHERE")) {
			
			conditions.addAll(readCondition(sql));
		} else {
			
			while (parser.readIf("INNER") || parser.readIf("CROSS") || parser.readIf("NATURAL")) {
				parser.readIf("INNER");
				DDLParserUtils.match(parser, "JOIN");
				for (SimpleExpression t : readTable())
					if (!tables.contains(t))
						tables.add(t);
				if (parser.readIf("ON")) {
					conditions.addAll(readCondition(sql));
				} else if (parser.readIf("USING")) { 
					
					String lTable = tables.get(tables.size() - 2).getName();
					String rTable = tables.get(tables.size() - 1).getName();
					conditions.addAll(readUsingConditions(lTable, rTable));
				}
			}
			while (parser.readIf("JOIN")) {
				for (SimpleExpression t : readTable())
					if (!tables.contains(t))
						tables.add(t);
				if (parser.readIf("ON")) {
					conditions.addAll(readCondition(sql));
				} else if (parser.readIf("USING")) {
					
					String lTable = tables.get(tables.size() - 2).getName();
					String rTable = tables.get(tables.size() - 1).getName();
					conditions.addAll(readUsingConditions(lTable, rTable));
				}
			}
		}
		while (!parser.getCurrentToken().equals(";") && !parser.getCurrentToken().equals("/")) {
			if (parser.readIf("HAVING")) {
				conditions.addAll(readCondition(sql));
			} else
				parser.read();
		}

		
		String token;
		String policyName = null;
		List<String> balanceFields = null;
		while (!(token = parser.getCurrentToken()).equals(";")) {
			if (token.equalsIgnoreCase("bf") 
					|| token.equalsIgnoreCase("partitionkey")) {
				DDLParserUtils.readAndMatch(parser, "=");
				balanceFields = readBalanceFields();
			} else if (token.equalsIgnoreCase("policy") 
					|| token.equalsIgnoreCase("tablegroup")) {
				DDLParserUtils.readAndMatch(parser, "=");
				policyName = parser.getCurrentToken();
			}
			parser.read();
		}
		SCreateView view = new SCreateView(viewName, specifiedColumnNames, columns, tables, conditions, DbnType.Oracle);
		view.setPolicyName(policyName);
		view.setBalanceFields(balanceFields);
		view.setType(TableInfo.TYPE_HEAP_ORGANIZED);	
		view.setViewSql(sql.substring(0, sql.length() - 1));
		return view;
	}

	private SimpleExpression readExpression(String sql) throws SQLException {
		SimpleExpression c = new SimpleExpression(readExpression2(sql));
		if (parser.readIf("AS")) {
			
			String alias = DDLParserUtils.readIdentifier(parser, "name of alias");
			c.setAlias(alias);
		}
		return c;
	}

	private String readExpression2(String sql) throws SQLException {
		
		int start = parser.getLastParseIndex();
		boolean isLp = parser.readIf("(");

		String token = parser.getCurrentToken();
		while (token.equals("-")) {
			parser.read();
			token = parser.getCurrentToken();
		}
		parser.read();
		token = parser.getCurrentToken();
		if (token.equals("(")) {
			int lp = 1;
			parser.read();
			while (!parser.parseOver()) {
				token = parser.getCurrentToken();
				if (token.equals("("))
					lp++;
				else if (token.equals(")"))
					lp--;
				if (0 == lp)
					break;
				parser.read();
			}
			if (0 != lp)
				throw new SQLException("Syntax error, no matching )");
			parser.read();
		}
		if (parser.readIf("."))
			parser.read();
		
		if (parser.readIf("+") || parser.readIf("-") || parser.readIf("*") || parser.readIf("/"))
			readExpression2(sql);
		if (isLp)
			DDLParserUtils.match(parser, ")");
		int end = parser.getLastParseIndex();
		token = sql.substring(start, end).trim();
		if (token.startsWith("(") && token.endsWith(")"))
			token = token.substring(1, token.length() - 1);
		return token;
	}

	private List<SimpleExpression> readTable() throws SQLException {
		List<SimpleExpression> result = new ArrayList<SimpleExpression>();
		boolean isLp = parser.readIf("(");
		do {
			String t = DDLParserUtils.readIdentifier(parser, "table name");
			SimpleExpression e = new SimpleExpression(t);
			if (parser.readIf("AS")) {
				String alias = DDLParserUtils.readIdentifier(parser, "alias of table");
				e.setAlias(alias);
			}










			if (!result.contains(e))
				result.add(e);
		} while (parser.readIf(","));
		if (isLp)
			DDLParserUtils.match(parser, ")");
		return result;
	}

	private List<String> readCondition(String sql) throws SQLException {
		List<String> result = new ArrayList<String>();
		do {
			int start = parser.getLastParseIndex();
			String t = parser.getCurrentToken();
			int lp = 0;
			while (!t.equals(";") && !t.equals("/") && !t.equalsIgnoreCase("AND")
					&& !t.equalsIgnoreCase("OR") && !t.equalsIgnoreCase("GROUP")
					&& !t.equalsIgnoreCase("HAVING") && !t.equalsIgnoreCase("ORDER")
					&& !t.equalsIgnoreCase("FOR")
					&& !t.equalsIgnoreCase("LOCK") && !t.equalsIgnoreCase("PROCEDURE")
					&& !t.equalsIgnoreCase("JOIN") && !t.equalsIgnoreCase("INNER")
					&& !t.equalsIgnoreCase("NATURAL") && !t.equalsIgnoreCase("CROSS")) {
				if (t.equals("("))
					lp++;
				else if (t.equals(")")) {
					if (lp == 0)
						break;
					lp--;
				}
				parser.read();
				t = parser.getCurrentToken();
			}
			if (lp != 0)
				throw new SQLException("Syntax error, ( and ) doesn't match.");
			int end = parser.getLastParseIndex();
			String condition = sql.substring(start, end).trim();
			if ("".equals(condition))
				throw new SQLException("Condition can not be empty.");
			if (condition.startsWith("(") && condition.endsWith(")"))
				condition = condition.substring(1, condition.length() - 1);
			result.add(condition);
		} while (parser.readIf("AND") || parser.readIf("OR"));
		return result;
	}
	
	
	public List<String> readUsingConditions(String ltable, String rtable) throws SQLException {
		DDLParserUtils.match(parser, "(");
		List<String>clms = new ArrayList<String>();
		while (true) {
			clms.add(DDLParserUtils.readIdentifier(parser, "column name"));
			if (parser.parseOver())
				throw new SQLException("Syntax err, no match ')'");
			if (parser.getCurrentToken().equals(")"))
				break;
			else if (parser.getCurrentToken().equals(","))
				parser.read();
			else
				throw new SQLException("Syntax err, expected ','or')' , but was " 
						+ parser.getCurrentToken());
		}
		return makeConditions(ltable, rtable, clms);
	}
	
	
	public List<String> makeConditions(String ltable, String rtable, List<String>columns) 
		throws SQLException {
		if (null == ltable || null == rtable || null == columns)
			throw new SQLException("table for join can not be null");
		if (columns.size() == 0)
			throw new SQLException("size of columns for 'JOIN USING' must be positive");
		List<String> conditions = new ArrayList<String>();
		for (String column : columns) {
			conditions.add(ltable + "." + column + "=" + rtable + "." + column);
		}
		return conditions;
	}

	
	private Statement parseCreateIndex(String indexName, int indexType, 
			boolean unique, String sql) throws SQLException {
		DDLParserUtils.match(parser, "(");
		
		ArrayList<IndexColumn> columns = readCreateIndexColumns(indexName);
		IndexInfo index = new IndexInfo(indexName, tableName, false, unique, columns);
		index.setIndexType(indexType);
		
		String token;
		while (!(token = parser.getCurrentToken()).equals(";")) {
			if (token.equalsIgnoreCase("TABLESPACE")) {
				parser.read();
				index.setTableSpace(parser.readIdentifier());
				continue;
			} else if (token.equalsIgnoreCase("COMPRESS")) {
				
				if (indexType == IndexInfo.INDEX_TYPE_BITMAP)
					throw new SQLException("BitMap indexes can't not be compressed");
				parser.read();
				index.setCompressNum(DDLParserUtils.readInt(parser, "compress number"));
				index.setIndexType(IndexInfo.INDEX_TYPE_BTREE_COMPRESS);
				continue;
			} else if (token.equalsIgnoreCase("REVERSE")) {
				
				if (indexType == IndexInfo.INDEX_TYPE_BITMAP)
					throw new SQLException("BitMap indexes can't not be reversed");
				index.setReverse(true);
			}
			parser.read();
		}
		SAlterTableOp op = new SAlterTableAddIndex(index, true);
		op.setSql(sql.substring(0, sql.length() - 1));
		op.setClauseSql(null);
		List<SAlterTableOp> ops = new LinkedList<SAlterTableOp>();
		ops.add(op);
		SAlterTable alterTable = new SAlterTable(tableName, ops);
		alterTable.setDbnType(DbnType.Oracle);
		




		
		return alterTable;
	}

	private Statement parserCreateClusterIndex(String clusterName, String indexName, String sql) throws SQLException {
		String dbnClusterName = parser.readIdentifier();
		SCreateClusterIndex st = new SCreateClusterIndex(indexName, dbnClusterName, false, 
				null, sql.substring(0, sql.length() - 1 ));
		String token;
		while(!(token = parser.getCurrentToken()).equals(";")) {
			if (token.equalsIgnoreCase("TABLESPACE")) {
				parser.read();
				st.setTableSpace(DDLParserUtils.readIdentifier(parser, "tablespace name"));
				continue;
			} else if (token.equalsIgnoreCase("REVERSE")) {
				st.setReverse(true);
			}
			parser.read();
		}
		return st;
	}

	
	private Statement parseAlterTable(String sql) throws SQLException {
		tableName = DDLParserUtils.readIdentifier(parser, "table name");
		boolean checkDupKey = false;
		List<SAlterTableOp> ops = new LinkedList<SAlterTableOp>();
		while (!isEnd) {
			SAlterTableOp op = null;
			int beginIndex = parser.getLastParseIndex();
			if (parser.getCurrentToken().equalsIgnoreCase("ADD")) {
				parser.read();
				if (parser.readIf("PRIMARY")) {
					
					DDLParserUtils.match(parser, "KEY");
					DDLParserUtils.match(parser, "(");
					ArrayList<String> fields = readIndexFields(parser);
					
					String tabSp = readConstraintStateInAlter();
					IndexInfo index = new IndexInfo("PRIMARY", tableName, fields, true, true);
					index.setTableSpace(tabSp);
					op = new SAlterTableAddIndex(index, false);
				} else if (parser.readIf("UNIQUE")) {
					
					DDLParserUtils.match(parser, "(");
					ArrayList<String> fields = readIndexFields(parser);
					
					String tabSp = readConstraintStateInAlter();
					IndexInfo index = new IndexInfo(fields.get(0), tableName, fields, false, true);
					index.setTableSpace(tabSp);
					index.setOracleUniqueConstraint(true);
					op = new SAlterTableAddIndex(index, false);
				} else if (parser.readIf("PARTITION")) {
					
					
					DDLParserUtils.readIdentifier(parser, "name of partition");
					DDLParserUtils.match(parser, "VALUES");
					DDLParserUtils.match(parser, "LESS");
					DDLParserUtils.match(parser, "THAN");
					readStringInBrackets(sql);
					op = new SAlterTableAddPartition();
					if (ops.size() > 0)
						throw new SQLException("Syntax err, can not combine DROP PARTITION with other operations");
					parseEndInAlter(false);
				} else { 
					List<SAlterTableOp> addOps = readColumnsDefInAlter(sql, true);
					ops.addAll(addOps);
				}
			} else if (parser.getCurrentToken().equalsIgnoreCase("MODIFY")) {
				
				parser.read();
				List<SAlterTableOp> modifyOps = readColumnsDefInAlter(sql, false);
				ops.addAll(modifyOps);
			} else if (parser.getCurrentToken().equalsIgnoreCase("DROP")) {
				parser.read();
				String str = parser.getCurrentToken();
				if ("PRIMARY".equalsIgnoreCase(str)) {
					
					DDLParserUtils.readAndMatch(parser, "KEY");
					op = new SAlterTableDropIndex("PRIMARY", false);
					
					parseEndInAlter(true);
				} else if ("UNIQUE".equalsIgnoreCase(str)) {
					
					DDLParserUtils.readAndMatch(parser, "(");
					
					String name = DDLParserUtils.readIdentifier(parser, "column name");
					op = new SAlterTableDropIndex(name, false);
					
					parseEndInAlter(true);
				} else if ("PARTITION".equalsIgnoreCase(str)) {
					
					parser.read();
					List<String> pnames = new ArrayList<String>();
					pnames.add(DDLParserUtils.readIdentifier(parser, "partition name"));
					op = new SAlterTableDropPartition(pnames);
					if (ops.size() > 0)
						throw new SQLException("Syntax err, can not combine DROP PARTITION with other operations");
					parseEndInAlter(false);
				} else if ("COLUMN".equalsIgnoreCase(str)){
					
					
					if (ops.size() > 0)
						throw new SQLException("Syntax err, can not combine DROP with other operation");
					parser.read();
					String columnName = DDLParserUtils.readIdentifier(parser, "column name");
					op = new SAlterTableDropColumn(columnName);
					parseEndInAlter(false);
					isEnd = true;
				} else if ("(".equals(str)) {
					
					
					if (ops.size() > 0)
						throw new SQLException("Syntax err, can not combine DROP with other operation");
					List<SAlterTableOp> dropOps = readDropColumns();
					parseEndInAlter(false);
					ops.addAll(dropOps);
					isEnd = true;
				} else
					throw new SQLException("Syntax err, Unknown Command: " + sql );
			} else if (parser.getCurrentToken().equalsIgnoreCase("RENAME")) {
				parser.read();
				
				if (ops.size() > 0)
					throw new SQLException("Syntax err, can not combine RENAME with other operation");
				if (parser.readIf("TO")) {
					
					
					op = new SAlterTableRename(DDLParserUtils.readIdentifier(parser, "table name"));
					
				} else if (parser.readIf("COLUMN")) {
					
					
					String oldName = DDLParserUtils.readIdentifier(parser, "old column name");
					DDLParserUtils.match(parser, "TO");
					String newName = DDLParserUtils.readIdentifier(parser, "new column name");
					op = new SAlterTableRenameColumn(oldName, newName);
				} else 
					throw new SQLException("Syntax err, expected 'TO' or 'COLUMN' after 'RENAEM', but was " 
							+ parser.getCurrentToken());
				
				parseEndInAlter(false);
			} else if (parser.getCurrentToken().equalsIgnoreCase("SET")) {
				DDLParserUtils.readAndMatch(parser, "CHECKDUPKEY");
				checkDupKey = DDLParserUtils.readBoolean(parser);
			} else
				throw new SQLException(
						"Syntax error, the right syntax is: ALTER TABLE tbl_name ADD|DROP|MODIFY|RENAME ...");
			
			if (null != op) {
				int endIndex = parser.getParseIndex() - 1;
				if (parser.getCurrentToken().equalsIgnoreCase("ADD") 
						|| parser.getCurrentToken().equalsIgnoreCase("MODIFY")
						|| parser.getCurrentToken().equalsIgnoreCase("DROP")
						|| parser.getCurrentToken().equalsIgnoreCase("RENAME")
						|| parser.getCurrentToken().equalsIgnoreCase("SET")) {
					endIndex = parser.getLastParseIndex();
				}
				op.setSql("alter table " + tableName + " "
								+ sql.substring(beginIndex, endIndex).trim());
				op.setClauseSql(sql.substring(beginIndex, endIndex).trim());
				ops.add(op);
			}
			if (parser.getCurrentToken().equals(";") || parser.getCurrentToken().equals("/"))
				break;
		}
		SAlterTable alterTable = new SAlterTable(tableName, ops);
		alterTable.setDupKeyChk(checkDupKey);
		alterTable.setDbnType(DbnType.Oracle);
		return alterTable;
	}

	
	private ColumnInfo readColumnInAlter(String sql, boolean isInBracket) throws SQLException {
		
		String columnName = "";
		String curStr = DDLParserUtils.readIdentifier(parser, "column name");
		try {
			ColumnInfo.parseType(curStr, DbnType.Oracle);
			
			throw new SQLException("No column name found.");
		} catch (IllegalArgumentException e) {
			
			columnName = curStr;
		}
		String columnTypeStr = parser.getCurrentToken();
		if (columnTypeStr.equalsIgnoreCase("DOUBLE")) { 
			
			parser.read();
			if (!parser.getCurrentToken().equalsIgnoreCase("PRECISION"))
				throw new SQLException("Syntax error, expect 'PRECISION' after 'DOUBLE'");
		}
		ColumnInfo c = new ColumnInfo(columnName, columnTypeStr, false, DbnType.Oracle);
		parser.read();
		
		if (parser.readIf("(")) {
			String l = DDLParserUtils.getTokenOrValue(parser, true);
			try {
				int v = Integer.parseInt(l);
				if (v > 0)
					c.setLength(v);
			} catch (NumberFormatException e) {
				throw new SQLException("The length of column '" + columnName 
						+ "' is '" + l + "' not an integer");
			}
			
			parser.read();
			while (true) {
				if (parser.getCurrentToken().equalsIgnoreCase(")")) {
					
					parser.read();
					break;
				}
				else if (parser.getCurrentToken().equalsIgnoreCase("("))
					throw new SQLException("Syntax err, ')' is expected, not '('");
				parser.read();
			}
		}
		
		int lp = 1;
		while (true) {
			String token = parser.getCurrentToken();
			if (!isInBracket && (token.equalsIgnoreCase("ADD")
					|| token.equalsIgnoreCase("MODIFY") 
					|| token.equalsIgnoreCase("DROP")
					|| token.equalsIgnoreCase("RENAME")
					|| token.equalsIgnoreCase("SET"))) {
				break;
			} else if (token.equals("(")) {
				lp++;
			} else if (token.equals(")")) {
				if (lp == 1 && isInBracket)
					break;
				lp--;
			} else if (token.equals(",") && lp == 1 && isInBracket) {
				break;
			} else if (token.equals(";") || token.equals("/")) {
				if (isInBracket)
					throw new SQLException("Syntax err, expected ')' before the end");
				isEnd = true;
				break;
			} else if (token.equalsIgnoreCase("GENERATED") || token.equalsIgnoreCase("AS")) {
				
				if (token.equalsIgnoreCase("GENERATED")) {
					DDLParserUtils.readAndMatch(parser, "ALWAYS");
					DDLParserUtils.match(parser, "AS");
				} else {
					parser.read();
				}
				
				String expr = readStringInBrackets(sql);
				if (null == expr || (null != expr && expr.trim().equals("")))
					throw new SQLException("Syntax err, virtual column definition is null");
				c.setVirtual(true);
				c.setExpression(expr); 
				continue;
			}
			parser.read();
		}
		return c;
	}
	
	
	
	private List<SAlterTableOp> readColumnsDefInAlter(String sql, boolean isAdd) throws SQLException {
		List<SAlterTableOp> ops = new LinkedList<SAlterTableOp>();
		if (parser.getCurrentToken().equals("(")) {
			
			
			parser.read();
			while (true) {
				int beginIndex = parser.getLastParseIndex();
				ColumnInfo c = readColumnInAlter(sql, true);
				int endIndex = parser.getParseIndex();
				SAlterTableOp p = isAdd ? new SAlterTableAddColumn(c) : new SAlterTableModifyColumn(c.getName(), c);
				String opName = isAdd ? "add" : "modify";
				p.setClauseSql(opName + " " + sql.substring(beginIndex, endIndex - 1));
				p.setSql("alter table " + tableName + " " + opName + " " 
						+ sql.substring(beginIndex, endIndex - 1));
				ops.add(p);
				if (parser.getCurrentToken().equals(")")) {
					parser.read();
					break;
				}
				parser.read();
			}
		
		} else {
			
			
			int beginIndex = parser.getLastParseIndex();
			ColumnInfo c = readColumnInAlter(sql, false);
			int endIndex = parser.getParseIndex() - 1;
			if (parser.getCurrentToken().equalsIgnoreCase("ADD") 
					|| parser.getCurrentToken().equalsIgnoreCase("MODIFY")
					|| parser.getCurrentToken().equalsIgnoreCase("DROP")
					|| parser.getCurrentToken().equalsIgnoreCase("RENAME")
					|| parser.getCurrentToken().equalsIgnoreCase("SET")) {
				endIndex = parser.getLastParseIndex();
			}
			SAlterTableOp p = isAdd ? new SAlterTableAddColumn(c) : new SAlterTableModifyColumn(c.getName(), c);
			String opName = isAdd ? "add" : "modify";
			p.setClauseSql(opName + " " + sql.substring(beginIndex + 1, endIndex));
			p.setSql("alter table " + tableName + " " + opName + " " + sql.substring(beginIndex + 1, endIndex));
			ops.add(p);
		}
		return ops;
	}
	
	
	private void parseEndInAlter(boolean isContinue) throws SQLException {
		while (true) {
			if (parser.getCurrentToken().equalsIgnoreCase("ADD")
					|| parser.getCurrentToken().equalsIgnoreCase("MODIFY")
					|| parser.getCurrentToken().equalsIgnoreCase("RENAME")
					|| parser.getCurrentToken().equalsIgnoreCase("DROP")
					|| parser.getCurrentToken().equalsIgnoreCase("SET")) {
				if (isContinue) {
					break;
				} else
					throw new SQLException("Syntax err, RENAME, column DROP and PARTITION can not combine with other operations ");
			} else if (parser.getCurrentToken().equals(";") || parser.getCurrentToken().equals("/")) {
				break;
			}
			parser.read();
		}
	}
	
	
	private List<SAlterTableOp> readDropColumns() throws SQLException {
		List<SAlterTableOp> dropOps = new LinkedList<SAlterTableOp>();
		DDLParserUtils.match(parser, "(");
		while (true) {
			String column = parser.readIdentifier();
			SAlterTableDropColumn p = new SAlterTableDropColumn(column);
			p.setClauseSql("DROP COLUMN " + column);
			p.setSql("alter table " + tableName + " DROP COLUMN " + column);
			dropOps.add(p);
			if (parser.getCurrentToken().equals(")"))
					break;
			else if (!parser.getCurrentToken().equals(","))
				throw new SQLException("Syntax err, expected ')' or ',', but was '" 
						+ parser.getCurrentToken());
			parser.read();
		}
		parser.read();
		return dropOps;
	}
	
	
	private Statement parserAlterDbnCluster(String sql) throws SQLException {
		String dbnClusterName = DDLParserUtils.readIdentifier(parser, "dbncluster name");
		if (parser.getCurrentToken().equalsIgnoreCase("SIZE")) {
			
			int size = 0;
			parser.read();
			String sizeStr = parser.getCurrentToken();
			try {
				size = Integer.parseInt(sizeStr);
			} catch (NumberFormatException e) {
				throw new SQLException("Syntax err, expected a positive integer as size, but was '" + sizeStr + "'");
			}
			parser.read();
			String str = parser.getCurrentToken();
			if (str.length() == 1 && Character.isLetter(str.charAt(0))) { 
				size = parseClusterSize(size + str);
				parser.read();
				str = parser.getCurrentToken();
			}
			if (str.equalsIgnoreCase("NOPARALLEL")) {
				parser.read();
			} else if (str.equalsIgnoreCase("PARALLEL")) {
				
				parser.read();
				String p = parser.getCurrentToken();
				if (!p.equals(";")) {
					try {
						Integer.parseInt(p);
					} catch (NumberFormatException e) {
						throw new SQLException("Syntax err, expected an integer after 'PARALLEL', but was '" + p + "'");
					}
					parser.read();
				}
				
			} else if (!str.equals(";"))
				throw new SQLException("Syntax err, invalid token '" + str + "'");
			SAlterDbnClusterSize s =  new SAlterDbnClusterSize(dbnClusterName, size);
			s.setSql(sql.substring(0, sql.length() - 1));
			return s;
		} else
			throw new SQLException("DDB supported alter cluster size only");
	}
	
	
	private Statement parseCreateTrProc(String sql) throws SQLException {
		StringTokenizer tokenizer = new StringTokenizer(sql);
		if (!"CREATE".equalsIgnoreCase(DDLParserUtils.getNextToken(tokenizer)))
			throw new SQLException("Syntax error, correct sql should be 'CREATE ...'.");

		boolean isCreateTr = false;
		boolean isCreateProc = false;
		String str = DDLParserUtils.getNextToken(tokenizer);
		if (str.equalsIgnoreCase("OR")) {
			String replace = DDLParserUtils.getNextToken(tokenizer, "'REPLACE'");
			if (! replace.equalsIgnoreCase("REPLACE"))
				throw new SQLException("Syntax error, expected 'REPLACE' after 'OR'");
			str = DDLParserUtils.getNextToken(tokenizer);
		}
		if ("TRIGGER".equalsIgnoreCase(str)) {
			isCreateTr = true;
		} else if ("PROCEDURE".equalsIgnoreCase(str)) {
			isCreateProc = true;
		}
		if (isCreateTr) {
			
			
			
			
			
			String triggerName = DDLParserUtils.getNextToken(tokenizer, "trigger name/ OR REPLACE");
			if (!triggerName.startsWith("DDB_"))
				throw new SQLException("Syntax error, trigger name should start with 'DDB_'.");
			String time = DDLParserUtils.getNextToken(tokenizer, "trigger time");
			if (time.equalsIgnoreCase("INSTEAD")) {
				String of = DDLParserUtils.getNextToken(tokenizer, "of");
				if (!of.equalsIgnoreCase("OF"))
					throw new SQLException("Syntax error, excpected 'OF' after 'INSTEAD'");
			} else 	if (!(time.equalsIgnoreCase("BEFORE") || time.equalsIgnoreCase("AFTER") 
					|| time.equalsIgnoreCase("FOR")))
				throw new SQLException("Syntax error, expected 'BEFORE','AFTER', 'FOR' or 'INSTEAD OF'. but was '" 
						+ time + "'");
			String event = DDLParserUtils.getNextToken(tokenizer, "trigger event");
			
			boolean isDML = (event.equalsIgnoreCase("UPDATE") || event.equalsIgnoreCase("DELETE") 
					|| event.equalsIgnoreCase("INSERT"));
			while (true) {
				if (isDML && !(event.equalsIgnoreCase("UPDATE") || event.equalsIgnoreCase("DELETE") 
						|| event.equalsIgnoreCase("INSERT")))
					throw new SQLException("Only DML(INSERT, UPDATE, DELETE) event clause is supported in DML trigger"); 
				String token = DDLParserUtils.getNextToken(tokenizer, "token");
				if (token.equalsIgnoreCase("ON")) {
					break;
				} else if (token.equalsIgnoreCase("OR")) {
					event = DDLParserUtils.getNextToken(tokenizer, "event");
				} else {
					DDLParserUtils.getNextToken(tokenizer, "");
				}
			}
			String name = DDLParserUtils.getNextToken(tokenizer, "table name");
			if (!isDML) 
				name = "";
			if (sql.endsWith(";"))
				sql = sql.substring(0, sql.length() - 1);
			return new SCreateTrigger(triggerName, name, sql, DbnType.Oracle, isDML);
		} else if (isCreateProc) {
			
			String spName = DDLParserUtils.getNextToken(tokenizer, "procedure name");
			int index = spName.indexOf("(");
			if (index > -1)
				spName = spName.substring(0, index).trim();
			if (!spName.startsWith("DDB_"))
				throw new SQLException("Syntax error, procedure name should start with 'DDB_'.");
			if (sql.endsWith(";"))
				sql = sql.substring(0, sql.length() - 1);
			return new SCreateProcedure(spName, sql, DbnType.Oracle);
		} else
			return null;
	}


	private String readStringInBrackets(String str) throws SQLException {
		DDLParserUtils.match(parser, "(");
		int start = parser.getLastParseIndex();
		int end = -1;
		int p = 1;
		while (p > 0) {
			if (parser.getCurrentToken().equals("("))
				++p;
			else if (parser.getCurrentToken().equals(")"))
				--p;
			else if (parser.getCurrentToken().equals(";"))
				throw new SQLException("Syntax error, ( and ) doesn't match.");
			parser.read();
		}
		end = parser.getLastParseIndex();
		String field = str.substring(start, end).trim();
		if (field.endsWith(")"))
			field = field.substring(0, field.length() - 1);
		return field;
	}

	
	
	
 
	
	private ColumnInfo readColumn() throws SQLException {
		String columnName = parser.readIdentifier();
		String columnTypeStr = parser.getCurrentToken();
		
		
		if (columnTypeStr.equalsIgnoreCase("DOUBLE")) {
			parser.read();
			if (!parser.getCurrentToken().equalsIgnoreCase("PRECISION"))
				throw new SQLException("Syntax error, expect 'PRECISION' after 'DOUBLE'");
		}
		if (columnName.equalsIgnoreCase("bucketno")) {
			
			if (!(columnTypeStr.equalsIgnoreCase("smallint") 
					|| columnTypeStr.equalsIgnoreCase("integer")))
				throw new SQLException("bucketno column should be of type INT.");
			useBucketNo = true;
			return null;
		} else {
			ColumnInfo c = new ColumnInfo(columnName, columnTypeStr, false, DbnType.Oracle);
			columns.add(c);
			
			parser.read();
			if (parser.readIf("(")) {
				String l = DDLParserUtils.getTokenOrValue(parser, true);
				try {
					int v = Integer.parseInt(l);
					if (v > 0)
						c.setLength(v);
				} catch (NumberFormatException e) {
					throw new SQLException("The length of column '" + columnName 
							+ "' is '" + l + "' not an integer");
				}
				
				parser.read();
				while (true) {
					if (parser.getCurrentToken().equalsIgnoreCase(")")) {
						
						parser.read();
						break;
					}
					else if (parser.getCurrentToken().equalsIgnoreCase("("))
						throw new SQLException("Syntax err, ')' is expected, not '('");
					parser.read();
				}
			}
			return c;
		}
	}


	private void readPrimaryKey() throws SQLException {
		if (primaryKey != null)
			throw new SQLException("Table '" + tableName
					+ "' has more than one primary key difinitions.");

		parser.read("key");
		ArrayList<String> indexKeys = readIndexColumns("PRIMARY");
		
		String tabSp = readConstraintState();
		primaryKey = new IndexInfo("PRIMARY", tableName, indexKeys, true, true);
		primaryKey.setTableSpace(tabSp);
		indexMap.put("PRIMARY", primaryKey);
	}
	
	private void readUnique() throws SQLException {
		parser.read();
		ArrayList<String> indexKeys = readIndexColumns(null);
		
		String tabSp = readConstraintState();
		IndexInfo unique = new IndexInfo(indexKeys.get(0), tableName, indexKeys, false, true);
		unique.setTableSpace(tabSp);
		unique.setOracleUniqueConstraint(true);
		if (indexMap.get(unique.getIndexName()) != null)
			throw new SQLException("Index redifined: " + unique.getIndexName());
		indexMap.put(indexKeys.get(0), unique);
	}
	
	
	private ArrayList<String> readIndexColumns(String indexName) 
		throws SQLException {
		ArrayList<String> indexKeys = new ArrayList<String>();
		DDLParserUtils.match(parser, "(");
		while (true) {
			String key = DDLParserUtils.readIdentifier(parser, "name of index column");
			boolean found = false;
			
			for (ColumnInfo c : columns) {
				if (c.getName().equalsIgnoreCase(key)) {
					found = true;
					indexKeys.add(key);
					break;
				}
			}
			if (!found)
				throw new SQLException("Column '" + key + "' doesn't exist.");
			String token = parser.getCurrentToken();
			if (token.equals(")"))
				break;
			else if (!token.equals(",")) {
				String errMsg = "";
				if (null != indexName)
					errMsg = "Syntax error for index '" + indexName + "'";
				else 
					errMsg = "Syntax error for UNIQUE in outline constraint";
				throw new SQLException(errMsg);
			}
			parser.read();
		}
		return indexKeys;
	}
	
	
	
	private String readConstraintState() throws SQLException {
		
		parser.read();
		int lp = 0;
		String tblSp = null;
		while (true) {
			if (parser.readIf("USING")) {
				DDLParserUtils.match(parser, "INDEX");
				
				
				if (parser.readIf("("))
					throw new SQLException("DDB does not  support to creat" 
							+ " an index with 'USING INDEX(..)' while creating table ");
			}
			
			if (parser.getCurrentToken().equals("(")) {
				lp++;
			} else if (parser.getCurrentToken().equals(")")) {
				if (lp == 0) {
					this.columnDefinitionEnd = true;
					break; 
				}
				lp--;
			} else if (parser.getCurrentToken().equalsIgnoreCase("TABLESPACE") && lp == 0) {
				parser.read();
				tblSp = DDLParserUtils.readIdentifier(parser, "tablespace");
				continue;
			} else if (parser.getCurrentToken().equals(",") && lp == 0)
				break;
			parser.read();
		}
		return tblSp;
	}
	
	
	private String readConstraintStateInAlter() throws SQLException {
		
		String tblSp = null;
		while (true) {
			if (parser.readIf("USING")) {
				DDLParserUtils.match(parser, "INDEX");
				
				if (parser.readIf("("))
					throw new SQLException("DDB does not support to create" 
							+ " an index with 'USING INDEX(..)' while creating table ");
			}
			if (parser.getCurrentToken().equalsIgnoreCase("TABLESPACE")) {
				parser.read();
				tblSp = DDLParserUtils.readIdentifier(parser, "tablespace");
				continue;
			} else if (parser.getCurrentToken().equalsIgnoreCase("ADD")
					|| parser.getCurrentToken().equalsIgnoreCase("MODIFY")
					|| parser.getCurrentToken().equalsIgnoreCase("DROP")
					|| parser.getCurrentToken().equalsIgnoreCase("RENAME")
					|| parser.getCurrentToken().equalsIgnoreCase("SET")) {
				
				break;
			} else if (parser.getCurrentToken().equals(";")) {
				
				isEnd = true;
				break;
			}
			parser.read();
		}
		return tblSp;
	}


	private ArrayList<String> readIndexFields(SQLLexParser parser) throws SQLException {
		ArrayList<String> fields = new ArrayList<String>();
		while (true) {
			String column = parser.readIdentifier();
			fields.add(column);
			if (parser.getCurrentToken().equals(")"))
					break;
			else if (!parser.getCurrentToken().equals(","))
				throw new SQLException("Syntax err, expected ')' or ',', but was '" 
						+ parser.getCurrentToken());
			parser.read();
		}
		parser.read();
		return fields;
	}
	
	private ArrayList<IndexColumn> readCreateIndexColumns(String idxName) throws SQLException {
		int lp = 0;
		ArrayList<IndexColumn> fields = new ArrayList<IndexColumn>();
		boolean isDesc = false;
		String currField = "";
		int seq = 1;
		while (true) {
			String t = parser.getCurrentToken();
			if (t.equals("("))
				lp++;
			else if (t.equals(")")) {
				if (lp == 0) {
					fields.add(new IndexColumn(tableName, idxName,currField, seq, 0, 0, false, isDesc));
					break;
				}
				else
					lp--;
			} else if (t.equals(",") && lp == 0) {
				fields.add(new IndexColumn(tableName, idxName,currField, seq, 0, 0, false, isDesc));
				isDesc = false;
				seq++;
				currField = "";
				parser.read();
				continue;
			} else if (t.equalsIgnoreCase("ASC")) {
				isDesc = false;
				parser.read();
				continue;
			} else if (t.equalsIgnoreCase("DESC")) {
				isDesc = true;
				parser.read();
				continue;
			}
			
			currField += t;
			parser.read();
		}
		parser.read();
		return fields;
	}

	
	
	private int parseClusterSize(String s) throws SQLException {
		String unit = s.substring(s.length() - 1);
		String sizeStr = s.substring(0, s.length() - 1);
		int size = 0;
		try {
			size = Integer.parseInt(sizeStr);
		} catch (NumberFormatException e) {
			throw new SQLException("Syntax err, size of cluster should be 'integer[K|M|G|T|P|E]', not '" + s + "'");
		}
		if (unit.equalsIgnoreCase("K") )
			return 1024*size;
		else if (unit.equalsIgnoreCase("M"))
			return (1<<20)*size;
		else if (unit.equalsIgnoreCase("G"))
			return (1<<30)*size;
		else if (unit.equalsIgnoreCase("T") || unit.equalsIgnoreCase("P")
				|| unit.equalsIgnoreCase("E")) {
			
			throw new SQLException("Error! Unit '" + unit.toUpperCase() + "' is too large for application");
		} else
			throw new SQLException ("Syntax err, expected [K|M|G|T|P|E], but was '" + unit + "'");
	}
	
	private List<String> readBalanceFields() throws SQLException {
		List<String> fields = new ArrayList<String>();
		if (!parser.getCurrentToken().equals("(")) {
			fields.add(parser.getCurrentToken());
		} else {
			
			while (true) {
				parser.read();
				String field = DDLParserUtils.readIdentifier(parser,
						"balance field");
				if (fields.contains(field))
					throw new SQLException("Syntax error, '" + field
							+ "' is set to be balance field twice");
				fields.add(field);
				
				String currToken = parser.getCurrentToken();
				if (currToken.equals(")"))
					break;
				else if (currToken.equals(";"))
					throw new SQLException(
							"Syntax error, expected ')' after balance fields");
				else if (!currToken.equals(","))
					throw new SQLException(
							"Syntax error, expected ',' between balance fields");
			}
			if (fields.size() == 0)
				throw new SQLException(
						"Syntax error, no balance fields has been specified");
		}
		return fields;
	}
}	
