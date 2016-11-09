
package com.netease.backend.db.common.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.management.PIDConfig;
import com.netease.backend.db.common.schema.ColumnInfo;
import com.netease.backend.db.common.schema.IndexInfo;
import com.netease.backend.db.common.schema.OnlineAlterTaskInfo;
import com.netease.backend.db.common.schema.TableInfo;


public class DDLParserMySQL {

	private SQLLexParser parser;

	private String tableName;

	private List<ColumnInfo> columns = new LinkedList<ColumnInfo>();

	private IndexInfo primaryKey = null;

	private HashMap<String, IndexInfo> indexMap = new HashMap<String, IndexInfo>();

	private boolean useBucketNo = false;
	
	
	public Statement parseMySQL(String sql) throws SQLException {
		if (!sql.endsWith(";"))
			sql = sql + ";";
		
		
		sql = sql.replaceAll("`", "");

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
			if (parser.getCurrentToken().equalsIgnoreCase("TABLE")) {
				
				r = parseCreateTable();
			} else if (parser.getCurrentToken().equalsIgnoreCase("INDEX")
					|| parser.getCurrentToken().equalsIgnoreCase("KEY"))
				
				
				r = parseCreateIndex(false, sql);
			else if (parser.getCurrentToken().equalsIgnoreCase("UNIQUE")) {
				
				
				parser.read();
				if (!parser.getCurrentToken().equalsIgnoreCase("INDEX")
						&& !parser.getCurrentToken().equalsIgnoreCase("KEY"))
					throw new SQLException("Expect INDEX/KEY but was " + parser.getCurrentToken());
				r = parseCreateIndex(true, sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("VIEW")) {
				parser.read();
				r = parseCreateView(sql);
			} else if (parser.getCurrentToken().equalsIgnoreCase("PROCEDURE")
					|| parser.getCurrentToken().equalsIgnoreCase("TRIGGER"))
				return parseCreateTrProc(sql);
			else {
				
				while (true) {
					if (parser.parseOver())
						throw new SQLException("Unknown command: " + sql + ".");
					parser.read();
					
					String currToken = parser.getCurrentToken();
					if (currToken.equalsIgnoreCase("VIEW")) {
						parser.read();
						r = parseCreateView(sql);
						break;
					} else if (currToken.equalsIgnoreCase("TRIGGER")
							|| currToken.equalsIgnoreCase("PROCEDURE")) {
						return parseCreateTrProc(sql);
					}
				}
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("ALTER")) {
			parser.read();
			boolean ignore = parser.readIf("IGNORE");
			if (parser.getCurrentToken().equalsIgnoreCase("TABLE")) {
				
				parser.read();
				r = parseAlterTable(sql);
				if (r instanceof SAlterTable) {
					((SAlterTable) r).setIgnore(ignore);
				} else {
					((OnlineAlterTaskInfo) r).setIgnore(ignore);
				}
			} else if (!ignore && parser.getCurrentToken().equalsIgnoreCase("VIEW")) {
				parser.read();
				r = parseCreateView(sql);
			} else if (!ignore) {
				
				while (true) {
					if (parser.parseOver())
						throw new SQLException("Unknown command: " + sql + ".");
					parser.read();
					
					String currToken = parser.getCurrentToken();
					if (currToken.equalsIgnoreCase("VIEW")) {
						parser.read();
						r = parseCreateView(sql);
						break;
					}
				}
			} else
				
				throw new SQLException("Unknown command: " + sql + ".");
		}
		
		DDLParserUtils.match(parser, ";");
		return r;
	}

	private SCreateTable parseCreateTable() throws SQLException {
		parser.read();
		tableName = DDLParserUtils.readIdentifier(parser, "table name");
		DDLParserUtils.match(parser, "(");
		
		boolean autoIncrementColumnExist = false;
		
		
		while (true) {
			ColumnInfo lastColumn = null;
			if (parser.readIf("primary")) {
				readPrimaryKey();
			} else if (parser.getCurrentToken().equalsIgnoreCase("foreign")) {
			} else if (parser.getCurrentToken().equalsIgnoreCase("index")
					|| parser.getCurrentToken().equalsIgnoreCase("key")
					|| parser.getCurrentToken().equalsIgnoreCase("unique")) {
				readIndex();
			} else {
				lastColumn = readColumn();
			}

			
			int lp = 0;
			boolean end = false;
			boolean hasNumber = false;
			String lastToken = "";
			while (true) {
				parser.read();
				String t = DDLParserUtils.getTokenOrValue(parser, true);
				if (t.equals(",") && lp == 0) {
					break;
				} else if (t.equals("(")) {
					lp++;
				} else if (t.equals(")")) {
					if (lp == 0) {
						end = true;
						break;
					} else
						lp--;
				} else if (t.equalsIgnoreCase("key")) {
					if (!lastToken.equalsIgnoreCase("primary") && !lastToken.equalsIgnoreCase("unique")) {
						if (primaryKey != null)
							throw new SQLException("Table '" + tableName
									+ "' has more than one primary key difinitions.");
						ArrayList<String> keys = new ArrayList<String>();
						keys.add(lastColumn.getName());
						primaryKey = new IndexInfo("PRIMARY", tableName, keys, true, true);
						indexMap.put("PRIMARY", primaryKey);
					}
				} else if (t.equalsIgnoreCase("primary") && lastColumn != null) {
					if (primaryKey != null)
						throw new SQLException("Table '" + tableName
								+ "' has more than one primary key difinitions.");
					ArrayList<String> keys = new ArrayList<String>();
					keys.add(lastColumn.getName());
					primaryKey = new IndexInfo("PRIMARY", tableName, keys, true, true);
					indexMap.put("PRIMARY", primaryKey);
				} else if (t.equalsIgnoreCase("unique") && lastColumn != null) {
					ArrayList<String> keys = new ArrayList<String>();
					keys.add(lastColumn.getName());
					IndexInfo index = new IndexInfo(lastColumn.getName(), tableName, keys, false, true);
					if (indexMap.get(index.getIndexName()) != null)
						throw new SQLException("Index redifined: " + index.getIndexName());
					indexMap.put(index.getIndexName(), index);
				} else if (t.equalsIgnoreCase("COMMENT") && lastColumn != null) {
					
					parser.read();
					if (!parser.getCurrentToken().equals("'"))
						throw new SQLException("Column '" + lastColumn.getName() + "' has null comment");
					String comment = DDLParserUtils.getTokenOrValue(parser, false);
					lastColumn.setComment(comment);
				} else if (t.equalsIgnoreCase("AUTO_INCREMENT") && lastColumn!= null) {
					lastColumn.setAutoIncrement(true);
					
					
					if (autoIncrementColumnExist)
						throw new SQLException("There can be only one AUTO_INCREMENT column per table.");
					else
						autoIncrementColumnExist = true;
				}
				
				if (lastColumn != null && lp == 1 && !hasNumber) {
					try {
						int v = Integer.parseInt(t);
						if (v > 0) {
							v = adjustColumnLength(v, lastColumn.getTypeName());
							lastColumn.setLength(v);
						}
						hasNumber = true;
					} catch (NumberFormatException e) {
					}
				}
				lastToken = t;
			}

			if (end)
				break;
			parser.read();
		}
		
		TableInfo table = new TableInfo(tableName, columns, indexMap, DbnType.MySQL);
		table.setUseBucketNo(useBucketNo);
		
		
		table.setAssignIdType(-1);
		
		
		String token;
		String storageEngine = TableInfo.TYPE_INNODB;
		String tableComment = null;
		String policyName = null;
		String ntseParam = null;
		int bucketCount = -1;
		parser.read();
		while (!(token = parser.getCurrentToken()).equals(";")) {
			if (token.equalsIgnoreCase("TYPE") || token.equalsIgnoreCase("ENGINE")) {
				DDLParserUtils.readAndMatch(parser, "=");
				String se = parser.getCurrentToken();
				
				storageEngine = se.toUpperCase();
				table.setType(storageEngine);
			} else if (token.equalsIgnoreCase("COMMENT")) {
				
				parser.read();
				if(parser.getCurrentToken().equals("="))
					parser.read();
				if (!parser.getCurrentToken().equals("'"))
					throw new SQLException("Table comment has null string");
				tableComment = DDLParserUtils.readTokenOrValue(parser, false);
				table.setComment(tableComment);
			} else if (token.equalsIgnoreCase("AUTO_INCREMENT")) {
				
				throw new SQLException("It's not allowed to specified AUTO_INCREMENT start value");
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
			} else if (token.equalsIgnoreCase("withPersist")) {
				
				table.setPersist(true);
			} else if (token.equalsIgnoreCase("NTSE_PARAM")) {
				
				parser.read();
				if(parser.getCurrentToken().equals("="))
					parser.read();
				if (!parser.getCurrentToken().equals("'"))
					throw new SQLException("NTSE parameter should be NTSE_PARAM='string'");
				ntseParam = DDLParserUtils.readTokenOrValue(parser, false);
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
		SCreateTable s = new SCreateTable(table, policyName, bucketCount);
		if (null != ntseParam)
			s.setNtseParam(ntseParam);
		return s;
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
					|| parser.getCurrentToken().equalsIgnoreCase("DISTINCTROW"))
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
			
			while (parser.readIf("LEFT") || parser.readIf("RIGHT") || parser.readIf("INNER")
					|| parser.readIf("CROSS")) {
				DDLParserUtils.match(parser, "JOIN");
				for (SimpleExpression t : readTable())
					if (!tables.contains(t))
						tables.add(t);
				if (parser.readIf("ON")) {
					conditions.addAll(readCondition(sql));
				}
			}
			while (parser.readIf("JOIN")) {
				for (SimpleExpression t : readTable())
					if (!tables.contains(t))
						tables.add(t);
				if (parser.readIf("ON")) {
					conditions.addAll(readCondition(sql));
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
		String storageEngine = TableInfo.TYPE_INNODB;
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
		SCreateView view = new SCreateView(viewName, specifiedColumnNames, columns, tables, conditions, DbnType.MySQL);
		view.setType(storageEngine);
		view.setPolicyName(policyName);
		view.setBalanceFields(balanceFields);
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
		while (token.equals("-") || token.equals("!") || token.equalsIgnoreCase("NOT")) {
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
		if (parser.readIf("+") || parser.readIf("-") || parser.readIf("*") || parser.readIf("/")
				|| parser.readIf("DIV") || parser.readIf("MOD"))
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
			if (parser.readIf("FORCE")) {
				DDLParserUtils.match(parser, "INDEX");
				DDLParserUtils.match(parser, "(");
				while (!parser.readIf(")")) {
					if (parser.parseOver())
						throw new SQLException("Syntax error, no matching )");
					parser.read();
				}
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
					&& !t.equalsIgnoreCase("LIMIT") && !t.equalsIgnoreCase("FOR")
					&& !t.equalsIgnoreCase("LOCK") && !t.equalsIgnoreCase("PROCEDURE")
					&& !t.equalsIgnoreCase("JOIN") && !t.equalsIgnoreCase("LEFT")
					&& !t.equalsIgnoreCase("RIGHT") && !t.equalsIgnoreCase("CROSS")) {
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

	private Statement parseCreateIndex(boolean unique, String sql) throws SQLException {
		parser.read();
		String indexName = DDLParserUtils.readIdentifier(parser, "index name");
		int type = IndexInfo.INDEX_TYPE_BTREE;
		if (parser.readIf("USING")) {
			String typeStr = parser.readIdentifier();
			if (typeStr.equalsIgnoreCase("BTREE"))
				type = IndexInfo.INDEX_TYPE_BTREE;
			else if (typeStr.equalsIgnoreCase("HASH"))
				type = IndexInfo.INDEX_TYPE_HASH;
			else
				throw new SQLException("Syntax err, expected 'BTREE' or 'HASH' after 'USING' but was " + typeStr);
		}
		DDLParserUtils.match(parser, "ON");
		tableName = DDLParserUtils.readIdentifier(parser, "table name");
		int beforeFieldPos = parser.getParseIndex();
		DDLParserUtils.match(parser, "(");
		ArrayList<String> fields = readIndexFields(parser);
		int afterFieldPos = parser.getParseIndex();
		IndexInfo index = new IndexInfo(indexName, tableName, fields, false, unique);
		index.setIndexType(type);

		SAlterTableOp op = new SAlterTableAddIndex(index, false);
		int endIndex = parser.getParseIndex();
		op.setSql(sql.substring(0, endIndex - 1));
		op.setClauseSql("ADD INDEX " + indexName + "("
				+ sql.substring(beforeFieldPos, afterFieldPos - 1));
		List<SAlterTableOp> ops = new LinkedList<SAlterTableOp>();
		ops.add(op);
		SAlterTable alterTable = new SAlterTable(tableName, ops);
		alterTable.setDbnType(DbnType.MySQL);
		if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
			parser.read();
			alterTable.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
		}

		OnlineAlterTaskInfo oat = null;
		if (parser.getCurrentToken().equals("/"))
			oat = DDLParserUtils.parseAlterTableOptions(parser, alterTable);
		if (oat == null) {
			return alterTable;
		} else {
			return oat;
		}

	}


	private Statement parseAlterTable(String sql) throws SQLException {
		tableName = DDLParserUtils.readIdentifier(parser, "table name");

		boolean checkDupKey = false;
		List<SAlterTableOp> ops = new LinkedList<SAlterTableOp>();
		boolean isOp = true;
		while (true) {
			if (isOp) {
				SAlterTableOp op = null;
				int beginIndex = parser.getLastParseIndex();
				if (parser.getCurrentToken().equalsIgnoreCase("ADD")) {
					parser.read();
					if (parser.readIf("INDEX") || parser.readIf("KEY")) {
						
						String indexName = DDLParserUtils.readIdentifier(parser, "index name");
						if (parser.readIf("USING"))
							parser.read();
						DDLParserUtils.match(parser, "(");
						ArrayList<String> fields = readIndexFields(parser);
						IndexInfo index = new IndexInfo(indexName, tableName, fields, false, false);
						op = new SAlterTableAddIndex(index, false);
					} else if (parser.readIf("PRIMARY")) {
						
						DDLParserUtils.match(parser, "KEY");
						if (parser.readIf("(")) {
							ArrayList<String> fields = readIndexFields(parser);
							IndexInfo index = new IndexInfo("PRIMARY", tableName, fields, true, true);
							op = new SAlterTableAddIndex(index, false);
						} else {
							throw new SQLException("No columns of primary key");
						}
					} else if (parser.readIf("UNIQUE")) {
						
						if (parser.getCurrentToken().equalsIgnoreCase("KEY")
								|| parser.getCurrentToken().equalsIgnoreCase("INDEX"))
							parser.read();
						String indexName = DDLParserUtils.readIdentifier(parser, "index name");
						if (parser.readIf("USING"))
							parser.read();
						DDLParserUtils.match(parser, "(");
						ArrayList<String> fields = readIndexFields(parser);
						IndexInfo index = new IndexInfo(indexName, tableName, fields, false, true);
						op = new SAlterTableAddIndex(index, false);
					} else if (parser.readIf("PARTITION")) {
						
						
						DDLParserUtils.match(parser, "(");
						DDLParserUtils.match(parser, "PARTITION");
						DDLParserUtils.readIdentifier(parser, "name of partition");
						DDLParserUtils.match(parser, "VALUES");
						DDLParserUtils.match(parser, "LESS");
						DDLParserUtils.match(parser, "THAN");
						if (!parser.readIf("MAXVALUE"))
							readStringInBrackets(sql);
						DDLParserUtils.match(parser, ")");
						op = new SAlterTableAddPartition();
					} else { 
						
						String tmp = parser.getCurrentToken();
						if ("COLUMN".equalsIgnoreCase(tmp)) {
							parser.read();
						}
						String columnName = "";
						String curStr = DDLParserUtils.readIdentifier(parser, "column name");
						try {
							ColumnInfo.parseType(curStr, DbnType.MySQL);
							
							throw new SQLException("No column name found.");
						} catch (IllegalArgumentException e) {
							
							columnName = curStr;
						}
						String columnTypeStr = parseColumnType();
						parser.read();
						int colLength = readColumnLength(parser, columnTypeStr);
						if (0 == colLength && ("VARCHAR".equalsIgnoreCase(columnTypeStr))) {
							throw new SQLException("Set the length of column please: " + columnName);
						}
						ColumnOption cop = this.readColumnOptions(columnName);
						ColumnInfo col = new ColumnInfo(columnName, columnTypeStr,
								colLength, cop.isUnique, DbnType.MySQL);
						col.setComment(cop.columnComment);
						col.setAutoIncrement(cop.isAutoIncrement);
						op = new SAlterTableAddColumn(col, cop.specifiedPosition, cop.preColumnName);
					}
				} else if (parser.getCurrentToken().equalsIgnoreCase("MODIFY")) {
					
					parser.read();
					String tmp = parser.getCurrentToken();
					if ("COLUMN".equalsIgnoreCase(tmp)) {
						parser.read();
					}
					String columnName = DDLParserUtils.readIdentifier(parser, "column name");
					String columnTypeStr = parseColumnType();
					parser.read();
					int colLength = readColumnLength(parser, columnTypeStr);
					if (0 == colLength && ("VARCHAR".equalsIgnoreCase(columnTypeStr))) {
						throw new SQLException("Set the length of column please: " + columnName);
					}
					ColumnOption cop = this.readColumnOptions(columnName);
					ColumnInfo newColumn = new ColumnInfo(columnName, columnTypeStr, 
							colLength, cop.isUnique, DbnType.MySQL);
					newColumn.setComment(cop.columnComment);
					newColumn.setAutoIncrement(cop.isAutoIncrement);
					op = new SAlterTableModifyColumn(columnName, newColumn,
							cop.specifiedPosition, cop.preColumnName);
				} else if (parser.getCurrentToken().equalsIgnoreCase("CHANGE")) {
					
					parser.read();
					String tmp = parser.getCurrentToken();
					if ("COLUMN".equalsIgnoreCase(tmp)) {
						parser.read();
					}
					String oldColumnName = DDLParserUtils.readIdentifier(parser, "old column name");
					String newColumnName = DDLParserUtils.readIdentifier(parser, "new column name");
					String columnTypeStr = parseColumnType();

					parser.read();
					int colLength = readColumnLength(parser, columnTypeStr);
					if (0 == colLength && ("VARCHAR".equalsIgnoreCase(columnTypeStr))) {
						throw new SQLException("Set the length of column please: " + newColumnName);
					}
					ColumnOption cop = this.readColumnOptions(newColumnName);
					ColumnInfo newColumn = new ColumnInfo(newColumnName, columnTypeStr, 
							colLength, cop.isUnique, DbnType.MySQL);
					newColumn.setComment(cop.columnComment);
					newColumn.setAutoIncrement(cop.isAutoIncrement);
					op = new SAlterTableChangeColumn(oldColumnName, newColumn,
							cop.specifiedPosition, cop.preColumnName);
				} else if (parser.getCurrentToken().equalsIgnoreCase("DROP")) {
					parser.read();
					String str = parser.getCurrentToken();
					if ("INDEX".equalsIgnoreCase(str) || "KEY".equalsIgnoreCase(str)) {
						
						parser.read();
						String indexName = DDLParserUtils.readIdentifier(parser, "index name");
						op = new SAlterTableDropIndex(indexName, false);
					} else if ("PRIMARY".equalsIgnoreCase(str)) {
						
						parser.read();
						DDLParserUtils.match(parser, "KEY");
						op = new SAlterTableDropIndex("PRIMARY", false);
					} else if ("FOREIGN".equalsIgnoreCase(str)) {
						throw new SQLException("Foreign key is unsupported.");
					} else if ("PARTITION".equalsIgnoreCase(str)) {
						parser.read();
						List<String> pnames = readStringList(",", new String[] { ";" });
						op = new SAlterTableDropPartition(pnames);
					} else {
						
						if ("COLUMN".equalsIgnoreCase(str)) {
							parser.read();
						}
						String columnName = DDLParserUtils.readIdentifier(parser, "column name");
						op = new SAlterTableDropColumn(columnName);
					}
				} else if (parser.getCurrentToken().equalsIgnoreCase("SET")) {
					DDLParserUtils.readAndMatch(parser, "CHECKDUPKEY");
					checkDupKey = DDLParserUtils.readBoolean(parser);
				} else if (parser.getCurrentToken().equalsIgnoreCase("RENAME")) {
					parser.read();
					parser.readIf("TO");
					if (parser.getCurrentToken().equals(";") || parser.getCurrentToken().equals(","))
						throw new SQLException("Syntax error, you should specify table name.");
					op = new SAlterTableRename(DDLParserUtils.readIdentifier(parser, "table name"));
				} else if(parser.getCurrentToken().equalsIgnoreCase("COMMENT")) {
					
					parser.read();
					if(parser.getCurrentToken().equalsIgnoreCase("="))
						parser.read();
					if((!parser.getCurrentToken().equals("'")) || parser.getCurrentToken().equals(";")
							|| parser.getCurrentToken().equals(","))
						throw new SQLException("Syntax error, you should specify the comment like COMMENT [=] 'string'.");
					String comment = DDLParserUtils.getTokenOrValue(parser, false);
					parser.read();
					op = new SAlterTableComment(comment);
				} else if(parser.getCurrentToken().equalsIgnoreCase("ENGINE")
						|| parser.getCurrentToken().equalsIgnoreCase("ROW_FORMAT")
						|| parser.getCurrentToken().equalsIgnoreCase("KEY_BLOCK_SIZE")
						|| parser.getCurrentToken().equalsIgnoreCase("AUTO_INCREMENT")
						|| parser.getCurrentToken().equalsIgnoreCase("AVG_ROW_LENGTH")
						|| parser.getCurrentToken().equalsIgnoreCase("DEFAULT")
						|| parser.getCurrentToken().equalsIgnoreCase("CHARACTER")
						|| parser.getCurrentToken().equalsIgnoreCase("CHECKSUM")
						|| parser.getCurrentToken().equalsIgnoreCase("COLLATE")
						|| parser.getCurrentToken().equalsIgnoreCase("CONNECTION")
						|| parser.getCurrentToken().equalsIgnoreCase("DATA")
						|| parser.getCurrentToken().equalsIgnoreCase("DELAY_KEY_WRITE")
						|| parser.getCurrentToken().equalsIgnoreCase("INDEX")
						|| parser.getCurrentToken().equalsIgnoreCase("INSERT_METHOD")
						|| parser.getCurrentToken().equalsIgnoreCase("MAX_ROWS")
						|| parser.getCurrentToken().equalsIgnoreCase("MIN_ROWS")
						|| parser.getCurrentToken().equalsIgnoreCase("PACK_KEYS")
						|| parser.getCurrentToken().equalsIgnoreCase("PASSWORD")
						|| parser.getCurrentToken().equalsIgnoreCase("TABLESPACE")
						|| parser.getCurrentToken().equalsIgnoreCase("UNION")) {
					
					int lp = 0;
					parser.read();
					while (true) {
						if (parser.getCurrentToken().equals("(")) {
							lp++;
						} else if (parser.getCurrentToken().equals(")")) {
							lp--;
						} else if ((parser.getCurrentToken().equals(",") && lp == 0)
								|| parser.getCurrentToken().equals(";") 
								|| parser.getCurrentToken().equals("/")) {
							op = new SAlterTableMySQLOption();
							break;
						}
						parser.read();
					}
					
				} else
					throw new SQLException(
							"Syntax error, the right syntax is: ALTER TABLE tbl_name ADD|DROP|MODIFY|CHANGE ...");
				if (null != op) {
					int endIndex = parser.getParseIndex();
					op.setSql("alter table " + tableName + " "
									+ sql.substring(beginIndex, endIndex - 1));
					op.setClauseSql(sql.substring(beginIndex, endIndex - 1));
					ops.add(op);
				}
			}
			if (parser.getCurrentToken().equals(";") || parser.getCurrentToken().equals("/"))
				break;
			if (parser.getCurrentToken().equals(","))
				isOp = true;
			else
				isOp = false;
			parser.read();
		}
		SAlterTable alterTable = new SAlterTable(tableName, ops);
		alterTable.setDupKeyChk(checkDupKey);
		alterTable.setDbnType(DbnType.MySQL);
		if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
			parser.read();
			alterTable.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
		}
		OnlineAlterTaskInfo oat = null;
		if (parser.getCurrentToken().equals("/"))
			oat = DDLParserUtils.parseAlterTableOptions(parser, alterTable);
		if (oat == null) {
			return alterTable;
		} else {
			return oat;
		}
	}

	
	private Statement parseCreateTrProc(String sql) throws SQLException {
		StringTokenizer tokenizer = new StringTokenizer(sql);
		if (!"CREATE".equalsIgnoreCase(DDLParserUtils.getNextToken(tokenizer)))
			throw new SQLException("Syntax error, correct sql should be 'CREATE DEFINER=...'.");

		boolean isCreateTr = false;
		boolean isCreateProc = false;
		while (tokenizer.hasMoreTokens()) {
			String str = DDLParserUtils.getNextToken(tokenizer);
			if ("trigger".equalsIgnoreCase(str)) {
				isCreateTr = true;
				break;
			} else if ("procedure".equalsIgnoreCase(str)) {
				isCreateProc = true;
				break;
			}
		}
		if (isCreateTr) {
			String triggerName = DDLParserUtils.getNextToken(tokenizer, "trigger name");
			if (!triggerName.startsWith("DDB_"))
				throw new SQLException("Syntax error, trigger name should start with 'DDB_'.");
			String time = DDLParserUtils.getNextToken(tokenizer, "trigger time");
			if (!time.equalsIgnoreCase("before") && !time.equalsIgnoreCase("after"))
				throw new SQLException("Syntax error, trigger_time should be 'BEFORE' or 'AFTER'.");
			String event = DDLParserUtils.getNextToken(tokenizer, "trigger event");
			if (!event.equalsIgnoreCase("insert") && !event.equalsIgnoreCase("update")
					&& !event.equalsIgnoreCase("delete"))
				throw new SQLException(
						"Syntax error, trigger_event should be 'INSERT' or 'UPDATE' or 'DELETE'.");
			DDLParserUtils.matchNextToken(tokenizer, "on");
			String tableName = DDLParserUtils.getNextToken(tokenizer, "table name");
			if (sql.endsWith(";"))
				sql = sql.substring(0, sql.length() - 1);
			return new SCreateTrigger(triggerName, tableName, sql, DbnType.MySQL, true);
		} else if (isCreateProc) {
			String spName = DDLParserUtils.getNextToken(tokenizer, "procedure name");
			int index = spName.indexOf("(");
			if (index > -1)
				spName = spName.substring(0, index).trim();
			if (!spName.startsWith("DDB_"))
				throw new SQLException("Syntax error, procedure name should start with 'DDB_'.");
			if (sql.endsWith(";"))
				sql = sql.substring(0, sql.length() - 1);
			return new SCreateProcedure(spName, sql, DbnType.MySQL);
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
		String columnTypeStr = parseColumnType();
		
		if (columnName.equalsIgnoreCase("bucketno")) {
			if (!columnTypeStr.equalsIgnoreCase("smallint"))
				throw new SQLException("bucketno column should be of type SMALLINT.");
			useBucketNo = true;
			return null;
		} else {
			ColumnInfo c = new ColumnInfo(columnName, columnTypeStr, false, DbnType.MySQL);
			columns.add(c);
			return c;
		}
	}

	private void readIndex() throws SQLException {
		boolean isUnique = false;

		if (parser.getCurrentToken().equalsIgnoreCase("unique")) {
			isUnique = true;
			parser.read();
			if (!parser.getCurrentToken().equalsIgnoreCase("key")
					&& !parser.getCurrentToken().equalsIgnoreCase("index"))
				throw new SQLException("Syntax error��Should be INDEX/KEY, but was '"
						+ parser.getCurrentToken() + "'");
		}
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "index name");
		ArrayList<String> indexKeys = readIndexKeys(name);
		IndexInfo index = new IndexInfo(name, tableName, indexKeys, false, isUnique);

		if (indexMap.get(index.getIndexName()) != null)
			throw new SQLException("Index redifined: " + index.getIndexName());
		indexMap.put(name, index);
	}

	private void readPrimaryKey() throws SQLException {
		if (primaryKey != null)
			throw new SQLException("Table '" + tableName
					+ "' has more than one primary key difinitions.");

		parser.read("key");
		ArrayList<String> indexKeys = readIndexKeys("PRIMARY");

		primaryKey = new IndexInfo("PRIMARY", tableName, indexKeys, true, true);
		indexMap.put("PRIMARY", primaryKey);
	}

	private ArrayList<String> readIndexKeys(String indexName) throws SQLException {
		ArrayList<String> indexKeys = new ArrayList<String>();

		parser.read("(");
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
			if (parser.getCurrentToken().equals("(")) {
				parser.read();
				DDLParserUtils.readInt(parser, "length of index prefix");
				DDLParserUtils.match(parser, ")");
			}
			
			if (parser.getCurrentToken().equalsIgnoreCase("ASC") 
					|| parser.getCurrentToken().equalsIgnoreCase("DESC"))
				parser.read();
			
			if (parser.getCurrentToken().equals(")"))
				break;
			
			if (!parser.getCurrentToken().equals(","))
				throw new SQLException("Syntax error for index '" + indexName + "'");
			parser.read();
		}
		return indexKeys;
	}

	private ArrayList<String> readIndexFields(SQLLexParser parser) throws SQLException {
		ArrayList<String> fields = new ArrayList<String>();
		while (true) {
			String field = DDLParserUtils.readIdentifier(parser, "name of index column");
			fields.add(field);
			
			if (parser.getCurrentToken().equals("(")) {
				parser.read();
				DDLParserUtils.readInt(parser, "length of index prefix");
				DDLParserUtils.match(parser, ")");
			}

			if (parser.getCurrentToken().equalsIgnoreCase("ASC") 
					|| parser.getCurrentToken().equalsIgnoreCase("DESC"))
				parser.read();
			
			if (parser.getCurrentToken().equals(")"))
				break;
			
			if (!parser.getCurrentToken().equals(","))
				throw new SQLException("Syntax error near '" + parser.getCurrentToken() + "'");
			parser.read();
		}
		parser.read();
		return fields;
	}

	private int readColumnLength(SQLLexParser parser, String columnTypeStr) throws SQLException {
		int colLength = 0;
		boolean isNoLengthType = false;
		
		
		if (columnTypeStr.equalsIgnoreCase("ENUM") || columnTypeStr.equalsIgnoreCase("SET"))
			isNoLengthType = true;
		
		if (parser.readIf("(")) {
			String lengthStr = parser.getCurrentToken();
			try {
				colLength = Integer.parseInt(lengthStr);
			} catch (NumberFormatException e) {
				if (!isNoLengthType)
					throw new SQLException("length of column must be a positive number.");
			}
			if (colLength <= 0 && !isNoLengthType)
				throw new SQLException("length of column must be a positive number.");
			parser.read();
			while (!parser.readIf(")")) {
				parser.read();
			}
		}
		return adjustColumnLength(colLength, columnTypeStr);
	}


	
	private class ColumnOption {
		boolean isUnique;
		String columnName;
		String columnComment;
		boolean specifiedPosition;
		String preColumnName;
		boolean isAutoIncrement;

		ColumnOption(String name) {
			columnName = name;
			isUnique = false;
			columnComment = null;
			specifiedPosition = false;
			preColumnName = null;
			isAutoIncrement = false;
		}
	}

	
	private ColumnOption readColumnOptions(String columnName) throws SQLException {
		ColumnOption copn = new ColumnOption(columnName);
		boolean hasComment = false;
		boolean hasDefault = false;
		boolean hasNull = false;
		
		while (true) {
			if (parser.getCurrentToken().equalsIgnoreCase("UNIQUE")) {
				if (copn.isUnique)
					throw new SQLException("Column '" + copn.columnName
							+ "' has double 'UNIQUE'");
				else {
					copn.isUnique = true;
				}
			} else if (parser.readIf("NOT")) {
				if(hasNull)
					throw new SQLException("Column '" + copn.columnName
							+ "' has double 'NOT'");
				else {
					if(parser.getCurrentToken().equalsIgnoreCase("NULL"))
						hasNull = true;
					else
						throw new SQLException("Column '" + copn.columnName
								+ "' has no NULL after NOT");
				}
			} else if(parser.readIf("DEFAULT")) {
				if(hasDefault)
					throw new SQLException("Column '" + copn.columnName
							+ "' has double 'DEFAULT'");
				else {
					String str = parser.getCurrentToken();
					if (",".equals(str) || ";".equals(str) || "unique".equalsIgnoreCase(str) ||
							"comment".equalsIgnoreCase(str)) {
						throw new SQLException("Column '" + copn.columnName
								+ "' doesn't have a default value.");
					}
					hasDefault = true;
				}
			} else if(parser.readIf("COMMENT")) {
				if(hasComment)
					throw new SQLException("Column '" + copn.columnName
							+ "' has double 'COMMENT'");
				else {
					if (!parser.getCurrentToken().equals("'"))
						throw new SQLException("Column '" + copn.columnName
								+ "' doesn't have a comment string");
					else {
						hasComment = true;
						copn.columnComment = DDLParserUtils.getTokenOrValue(parser, false);
					}
				}
			} else if (parser.getCurrentToken().equalsIgnoreCase("AFTER")
					|| parser.getCurrentToken().equalsIgnoreCase("FIRST")) {
				
				copn.specifiedPosition = true;
				if (parser.readIf("AFTER")) {
					String preColumnName = DDLParserUtils.readIdentifier(parser, "previous column name");
					if (preColumnName.equals(columnName))
						throw new SQLException("It's not allowed to specify column's position just after itself");
					copn.preColumnName = preColumnName;
				}
				else
					parser.read();
				
				
				if (!(parser.getCurrentToken().equals(";")
						|| parser.getCurrentToken().equals(",")
						|| parser.getCurrentToken().equals("/")))
					throw new SQLException("Column position definition must be at the end of column definition");
			} else if (parser.getCurrentToken().equalsIgnoreCase("AUTO_INCREMENT")) {
				copn.isAutoIncrement = true;
			}
			
			if (parser.getCurrentToken().equals(";")
					|| parser.getCurrentToken().equals(",")
					|| parser.getCurrentToken().equals("/"))
				break;
			parser.read();
		}
		return copn;
	}


	private List<String> readStringList(String delimiter, String[] stops) throws SQLException {
		List<String> l = new LinkedList<String>();
		Set<String> stopsSet = new HashSet<String>();
		for (String stop : stops)
			stopsSet.add(stop.toLowerCase());
		String[] partStops = new String[stops.length + 1];
		partStops[0] = delimiter;
		System.arraycopy(stops, 0, partStops, 1, stops.length);
		while (true) {
			String s = DDLParserUtils.readString(parser, " ", partStops);
			l.add(s);
			if (parser.getCurrentToken().equalsIgnoreCase(delimiter)) {
				parser.read();
				continue;
			}
			if (stopsSet.contains(parser.getCurrentToken().toLowerCase()))
				break;
		}
		return l;
	}
	
	
	private String parseColumnType() throws SQLException {
		String columnTypeStr = parser.getCurrentToken();
		if (columnTypeStr.equalsIgnoreCase("LONG")) {
			parser.read();
			String nextToken = parser.getCurrentToken();
			if (nextToken.equalsIgnoreCase("VARCHAR"))
				columnTypeStr = "LONGVARCHAR";
			else if (nextToken.equalsIgnoreCase("VARBINARY"))
				columnTypeStr = "LONGVARBINARY";
			else
				throw new SQLException("column type 'LONG " + nextToken + "' doesn't exist");
		} else if (columnTypeStr.equalsIgnoreCase("LONGVARCHAR")) {
			throw new SQLException("column type 'LONGVARCHAR' doesn't exist, it should be 'LONG VARCHAR'");
		} else if (columnTypeStr.equalsIgnoreCase("LONGVARBINARY")) {
			throw new SQLException("column type 'LONGVARBINARY' doesn't exist, it should be 'LONG VARBINARY'");
		} else if (parser.parseOver()) {
			throw new SQLException("Syntax error, column type must be specified");
		}
		return columnTypeStr;
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
	
	
	private static int adjustColumnLength(int length, String typeStr) {
		if (length > 0 && "DECIMAL".equalsIgnoreCase(typeStr)) {
			length += 2;
		}
		return length;
	}
}	
