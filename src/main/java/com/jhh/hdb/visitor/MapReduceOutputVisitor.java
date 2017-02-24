package com.jhh.hdb.visitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDeclareItem;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLKeep;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.SQLPartitionByRange;
import com.alibaba.druid.sql.ast.SQLPartitionValue;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLSubPartition;
import com.alibaba.druid.sql.ast.SQLSubPartitionBy;
import com.alibaba.druid.sql.ast.SQLSubPartitionByHash;
import com.alibaba.druid.sql.ast.SQLSubPartitionByList;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLGroupingSetExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerEvent;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerType;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlForceIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlIgnoreIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUseIndexHint;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey.Option;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCaseStatement.MySqlWhenStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlCursorDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlDeclareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlIterateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlLeaveStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlRepeatStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlWhileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExtractExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlIntervalExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlMatchAgainstExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOrderingExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlUserName;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableCharacter;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCommitStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement.UserSpecification;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDescribeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRollbackStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetCharSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetNamesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetPasswordStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStartTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSubPartitionByList;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateTableSource;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class MapReduceOutputVisitor {

	/*
	 * ======== MySqlOutputVisitor
	 */

	{
		this.dbType = JdbcConstants.MYSQL;
	}

	public boolean visit(SQLSelectQueryBlock select) {
		if (select instanceof MySqlSelectQueryBlock) {
			return visit((MySqlSelectQueryBlock) select);
		}

		return false;
		// super visit return super.visit(select);
	}

	public boolean visit(MySqlSelectQueryBlock x) {
		if (x.getOrderBy() != null) {
			x.getOrderBy().setParent(x);
		}

		print0(ucase ? "SELECT " : "select ");

		for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
			SQLCommentHint hint = x.getHints().get(i);
			// accept this hint.accept(this);
			visit_SQLASTOutputVisitor(hint);
			print(' ');
		}

		if (SQLSetQuantifier.ALL == x.getDistionOption()) {
			print0(ucase ? "ALL " : "all ");
		} else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
			print0(ucase ? "DISTINCT " : "distinct ");
		} else if (SQLSetQuantifier.DISTINCTROW == x.getDistionOption()) {
			print0(ucase ? "DISTINCTROW " : "distinctrow ");
		}

		if (x.isHignPriority()) {
			print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
		}

		if (x.isStraightJoin()) {
			print0(ucase ? "STRAIGHT_JOIN " : "straight_join ");
		}

		if (x.isSmallResult()) {
			print0(ucase ? "SQL_SMALL_RESULT " : "sql_small_result ");
		}

		if (x.isBigResult()) {
			print0(ucase ? "SQL_BIG_RESULT " : "sql_big_result ");
		}

		if (x.isBufferResult()) {
			print0(ucase ? "SQL_BUFFER_RESULT " : "sql_buffer_result ");
		}

		if (x.getCache() != null) {
			if (x.getCache().booleanValue()) {
				print0(ucase ? "SQL_CACHE " : "sql_cache ");
			} else {
				print0(ucase ? "SQL_NO_CACHE " : "sql_no_cache ");
			}
		}

		if (x.isCalcFoundRows()) {
			print0(ucase ? "SQL_CALC_FOUND_ROWS " : "sql_calc_found_rows ");
		}

		printSelectList(x.getSelectList());

		if (x.getInto() != null) {
			println();
			print0(ucase ? "INTO " : "into ");
			// accept this x.getInto().accept(this); 
 
			
		}

		if (x.getFrom() != null) {
			println();
			print0(ucase ? "FROM " : "from ");
			// accept this x.getFrom().accept(this); 

		}

		if (x.getWhere() != null) {
			println();
			print0(ucase ? "WHERE " : "where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this); 

		}

		if (x.getGroupBy() != null) {
			println();
			// accept this x.getGroupBy().accept(this); 

		}

		if (x.getOrderBy() != null) {
			println();
			// accept this x.getOrderBy().accept(this); 

		}

		if (x.getLimit() != null) {
			println();
			// accept this x.getLimit().accept(this); 

		}

		if (x.getProcedureName() != null) {
			print0(ucase ? " PROCEDURE " : " procedure ");
			// accept this x.getProcedureName().accept(this);
			if (!x.getProcedureArgumentList().isEmpty()) {
				print('(');
				printAndAccept(x.getProcedureArgumentList(), ", ");
				print(')');
			}
		}

		if (x.isForUpdate()) {
			println();
			print0(ucase ? "FOR UPDATE" : "for update");
		}

		if (x.isLockInShareMode()) {
			println();
			print0(ucase ? "LOCK IN SHARE MODE" : "lock in share mode");
		}

		return false;
	}

	public boolean visit(SQLColumnDefinition x) {
		// accept this x.getName().accept(this);
		print(' ');
		// accept this x.getDataType().accept(this);

		if (x.getCharsetExpr() != null) {
			print0(ucase ? " CHARSET " : " charset ");
			// accept this x.getCharsetExpr().accept(this);
		}

		for (SQLColumnConstraint item : x.getConstraints()) {
			print(' ');
			// accept this item.accept(this);
		}

		if (x.getDefaultExpr() != null) {
			if (x.getDefaultExpr() instanceof SQLNullExpr) {
				print0(ucase ? " NULL" : " null");
			} else {
				print0(ucase ? " DEFAULT " : " default ");
				// accept this x.getDefaultExpr().accept(this);
			}
		}

		if (x.getStorage() != null) {
			print0(ucase ? " STORAGE " : " storage ");
			// accept this x.getStorage().accept(this);
		}

		if (x.getOnUpdate() != null) {
			print0(ucase ? " ON UPDATE " : " on update ");

			// accept this x.getOnUpdate().accept(this);
		}

		if (x.isAutoIncrement()) {
			print0(ucase ? " AUTO_INCREMENT" : " auto_increment");
		}

		if (x.getComment() != null) {
			print0(ucase ? " COMMENT " : " comment ");
			// accept this x.getComment().accept(this);
		}

		if (x.getAsExpr() != null) {
			print0(ucase ? " AS (" : " as (");
			// accept this x.getAsExpr().accept(this);
			print(')');
		}

		if (x.isSorted()) {
			print0(ucase ? " SORTED" : " sorted");
		}

		return false;
	}

	public boolean visit(MySqlSelectQueryBlock.Limit x) {
		print0(ucase ? "LIMIT " : "limit ");
		if (x.getOffset() != null) {
			// accept this x.getOffset().accept(this); 

			print0(", ");
		}
		// accept this x.getRowCount().accept(this); 


		return false;
	}

	public boolean visit(SQLDataType x) {
		print0(x.getName());
		if (!x.getArguments().isEmpty()) {
			print('(');
			printAndAccept(x.getArguments(), ", ");
			print(')');
		}

		if (Boolean.TRUE == x.getAttribute("UNSIGNED")) {
			print0(ucase ? " UNSIGNED" : " unsigned");
		}

		if (Boolean.TRUE == x.getAttribute("ZEROFILL")) {
			print0(ucase ? " ZEROFILL" : " zerofill");
		}

		if (x instanceof SQLCharacterDataType) {
			SQLCharacterDataType charType = (SQLCharacterDataType) x;
			if (charType.getCharSetName() != null) {
				print0(ucase ? " CHARACTER SET " : " character set ");
				print0(charType.getCharSetName());

				if (charType.getCollate() != null) {
					print0(ucase ? " COLLATE " : " collate ");
					print0(charType.getCollate());
				}
			}
		}
		return false;
	}

	public boolean visit(SQLCharacterDataType x) {
		print0(x.getName());
		if (!x.getArguments().isEmpty()) {
			print('(');
			printAndAccept(x.getArguments(), ", ");
			print(')');
		}

		if (x.isHasBinary()) {
			print0(ucase ? " BINARY " : " binary ");
		}

		if (x.getCharSetName() != null) {
			print0(ucase ? " CHARACTER SET " : " character set ");
			print0(x.getCharSetName());
			if (x.getCollate() != null) {
				print0(ucase ? " COLLATE " : " collate ");
				print0(x.getCollate());
			}
		} else if (x.getCollate() != null) {
			print0(ucase ? " COLLATE " : " collate ");
			print0(x.getCollate());
		}

		return false;
	}

	public boolean visit(MySqlTableIndex x) {
		print0(ucase ? "INDEX" : "index");
		if (x.getName() != null) {
			print(' ');
			// accept this x.getName().accept(this);
		}

		if (x.getIndexType() != null) {
			print0(ucase ? " USING " : " using ");
			print0(x.getIndexType());
		}

		print('(');
		for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
			if (i != 0) {
				print0(", ");
			}
			// accept this x.getColumns().get(i).accept(this);
		}
		print(')');
		return false;
	}

	public boolean visit(MySqlCreateTableStatement x) {

		print0(ucase ? "CREATE " : "create ");

		for (SQLCommentHint hint : x.getHints()) {
			// accept this hint.accept(this);
			print(' ');
		}

		if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
			print0(ucase ? "TEMPORARY TABLE " : "temporary table ");
		} else {
			print0(ucase ? "TABLE " : "table ");
		}

		if (x.isIfNotExiists()) {
			print0(ucase ? "IF NOT EXISTS " : "if not exists ");
		}

		// accept this x.getName().accept(this);

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		int size = x.getTableElementList().size();
		if (size > 0) {
			print0(" (");
			incrementIndent();
			println();
			for (int i = 0; i < size; ++i) {
				if (i != 0) {
					print0(", ");
					println();
				}
				// accept this x.getTableElementList().get(i).accept(this);
			}
			decrementIndent();
			println();
			print(')');
		}

		for (Map.Entry<String, SQLObject> option : x.getTableOptions().entrySet()) {
			String key = option.getKey();

			print(' ');
			print0(ucase ? key : key.toLowerCase());

			if ("TABLESPACE".equals(key)) {
				print(' ');
				// accept this option.getValue().accept(this);
				continue;
			} else if ("UNION".equals(key)) {
				print0(" = (");
				// accept this option.getValue().accept(this);
				print(')');
				continue;
			}

			print0(" = ");

			// accept this option.getValue().accept(this);
		}

		if (x.getPartitioning() != null) {
			println();
			// accept this x.getPartitioning().accept(this);
		}

		if (x.getTableGroup() != null) {
			println();
			print0(ucase ? "TABLEGROUP " : "tablegroup ");
			// accept this x.getTableGroup().accept(this);
		}

		if (x.getSelect() != null) {
			incrementIndent();
			println();
			// accept this x.getSelect().accept(this);
			decrementIndent();
		}

		for (SQLCommentHint hint : x.getOptionHints()) {
			print(' ');
			// accept this hint.accept(this);
		}
		return false;
	}

	public boolean visit(MySqlKey x) {
		if (x.isHasConstaint()) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			if (x.getName() != null) {
				// accept this x.getName().accept(this);
				print(' ');
			}
		}

		print0(ucase ? "KEY" : "key");

		if (x.getIndexName() != null) {
			print(' ');
			// accept this x.getIndexName().accept(this);
		}

		if (x.getIndexType() != null) {
			print0(ucase ? " USING " : " using ");
			print0(x.getIndexType());
		}

		print0(" (");

		for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
			if (i != 0) {
				print0(", ");
			}
			// accept this x.getColumns().get(i).accept(this);
		}
		print(')');

		return false;
	}

	public boolean visit(MySqlPrimaryKey x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}

		print0(ucase ? "PRIMARY KEY" : "primary key");

		if (x.getIndexType() != null) {
			print0(ucase ? " USING " : " using ");
			print0(x.getIndexType());
		}

		print0(" (");

		for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
			if (i != 0) {
				print0(", ");
			}
			// accept this x.getColumns().get(i).accept(this);
		}
		print(')');

		return false;
	}

	public boolean visit(SQLCharExpr x) {
		print('\'');

		String text = x.getText();
		text = text.replaceAll("'", "''");
		text = text.replace("\\", "\\\\");

		print0(text);

		print('\'');
		return false;
	}

	public boolean visit(SQLVariantRefExpr x) {
		{
			int parametersSize = this.getParametersSize();
			int index = x.getIndex();

			if (index >= 0 && index < parametersSize) {
				Object param = this.getParameters().get(index);
				printParameter(param);
				return false;
			}
		}

		String varName = x.getName();
		if (x.isGlobal()) {
			print0("@@global.");
		} else {
			if ((!varName.startsWith("@")) // /
					&& (!varName.equals("?")) //
					&& (!varName.startsWith("#")) //
					&& (!varName.startsWith("$")) //
					&& (!varName.startsWith(":"))) {

				boolean subPartitionOption = false;
				if (x.getParent() != null) {
					subPartitionOption = x.getParent().getParent() instanceof SQLSubPartitionBy;
				}

				if (!subPartitionOption) {
					print0("@@");
				}
			}
		}

		for (int i = 0; i < x.getName().length(); ++i) {
			char ch = x.getName().charAt(i);
			if (ch == '\'') {
				if (x.getName().startsWith("@@") && i == 2) {
					print(ch);
				} else if (x.getName().startsWith("@") && i == 1) {
					print(ch);
				} else if (i != 0 && i != x.getName().length() - 1) {
					print0("\\'");
				} else {
					print(ch);
				}
			} else {
				print(ch);
			}
		}

		String collate = (String) x.getAttribute("COLLATE");
		if (collate != null) {
			print0(ucase ? " COLLATE " : " collate ");
			print0(collate);
		}

		return false;
	}

	public boolean visit(SQLMethodInvokeExpr x) {
		if ("SUBSTRING".equalsIgnoreCase(x.getMethodName())) {
			if (x.getOwner() != null) {
				// accept this x.getOwner().accept(this);
				print('.');
			}
			print0(x.getMethodName());
			print('(');
			printAndAccept(x.getParameters(), ", ");
			SQLExpr from = (SQLExpr) x.getAttribute("FROM");
			if (from != null) {
				print0(ucase ? " FROM " : " from ");
				// accept this from.accept(this);
			}

			SQLExpr forExpr = (SQLExpr) x.getAttribute("FOR");
			if (forExpr != null) {
				print0(ucase ? " FOR " : " for ");
				// accept this forExpr.accept(this);
			}
			print(')');

			return false;
		}

		if ("TRIM".equalsIgnoreCase(x.getMethodName())) {
			if (x.getOwner() != null) {
				// accept this x.getOwner().accept(this);
				print('.');
			}
			print0(x.getMethodName());
			print('(');

			String trimType = (String) x.getAttribute("TRIM_TYPE");
			if (trimType != null) {
				print0(trimType);
				print(' ');
			}

			printAndAccept(x.getParameters(), ", ");

			SQLExpr from = (SQLExpr) x.getAttribute("FROM");
			if (from != null) {
				print0(ucase ? " FROM " : " from ");
				// accept this from.accept(this);
			}

			print(')');

			return false;
		}

		if (("CONVERT".equalsIgnoreCase(x.getMethodName())) || "CHAR".equalsIgnoreCase(x.getMethodName())) {
			if (x.getOwner() != null) {
				// accept this x.getOwner().accept(this);
				print('.');
			}
			print0(x.getMethodName());
			print('(');
			printAndAccept(x.getParameters(), ", ");

			String charset = (String) x.getAttribute("USING");
			if (charset != null) {
				print0(ucase ? " USING " : " using ");
				print0(charset);
			}
			print(')');
			return false;
		}

		return false;
		// super visit return super.visit(x);
	}

	public boolean visit(MySqlIntervalExpr x) {
		print0(ucase ? "INTERVAL " : "interval ");
		// accept this x.getValue().accept(this);
		print(' ');
		print0(ucase ? x.getUnit().name() : x.getUnit().name_lcase);
		return false;
	}

	public boolean visit(MySqlExtractExpr x) {
		print0(ucase ? "EXTRACT(" : "extract(");
		print0(x.getUnit().name());
		print0(ucase ? " FROM " : " from ");
		// accept this x.getValue().accept(this);
		print(')');
		return false;
	}

	public boolean visit(MySqlMatchAgainstExpr x) {
		print0(ucase ? "MATCH (" : "match (");
		printAndAccept(x.getColumns(), ", ");
		print(')');

		print0(ucase ? " AGAINST (" : " against (");
		// accept this x.getAgainst().accept(this);
		if (x.getSearchModifier() != null) {
			print(' ');
			print0(ucase ? x.getSearchModifier().name : x.getSearchModifier().name_lcase);
		}
		print(')');

		return false;
	}

	public boolean visit(MySqlPrepareStatement x) {
		print0(ucase ? "PREPARE " : "prepare ");
		// accept this x.getName().accept(this);
		print0(ucase ? " FROM " : " from ");
		// accept this x.getFrom().accept(this);
		return false;
	}

	public boolean visit(MySqlExecuteStatement x) {
		print0(ucase ? "EXECUTE " : "execute ");
		// accept this x.getStatementName().accept(this);
		if (x.getParameters().size() > 0) {
			print0(ucase ? " USING " : " using ");
			;
			printAndAccept(x.getParameters(), ", ");
		}
		return false;
	}

	public boolean visit(MySqlDeleteStatement x) {
		print0(ucase ? "DELETE " : "delete ");

		for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
			SQLCommentHint hint = x.getHints().get(i);
			// accept this hint.accept(this);
			print(' ');
		}

		if (x.isLowPriority()) {
			print0(ucase ? "LOW_PRIORITY " : "low_priority ");
		}

		if (x.isQuick()) {
			print0(ucase ? "QUICK " : "quick ");
		}

		if (x.isIgnore()) {
			print0(ucase ? "IGNORE " : "ignore ");
		}

		if (x.getFrom() == null) {
			print0(ucase ? "FROM " : "from ");
			// accept this x.getTableSource().accept(this);
		} else {
			// accept this x.getTableSource().accept(this);
			println();
			print0(ucase ? "FROM " : "from ");
			// accept this x.getFrom().accept(this);
		}

		if (x.getUsing() != null) {
			println();
			print0(ucase ? "USING " : "using ");
			// accept this x.getUsing().accept(this);
		}

		if (x.getWhere() != null) {
			println();
			incrementIndent();
			print0(ucase ? "WHERE " : "where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
			decrementIndent();
		}

		if (x.getOrderBy() != null) {
			println();
			// accept this x.getOrderBy().accept(this);
		}

		if (x.getLimit() != null) {
			println();
			// accept this x.getLimit().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlInsertStatement x) {
		print0(ucase ? "INSERT " : "insert ");

		if (x.isLowPriority()) {
			print0(ucase ? "LOW_PRIORITY " : "low_priority ");
		}

		if (x.isDelayed()) {
			print0(ucase ? "DELAYED " : "delayed ");
		}

		if (x.isHighPriority()) {
			print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
		}

		if (x.isIgnore()) {
			print0(ucase ? "IGNORE " : "ignore ");
		}

		print0(ucase ? "INTO " : "into ");

		// accept this x.getTableSource().accept(this);

		if (x.getColumns().size() > 0) {
			incrementIndent();
			print0(" (");
			for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
				if (i != 0) {
					if (i % 5 == 0) {
						println();
					}
					print0(", ");
				}

				// accept this x.getColumns().get(i).accept(this);
			}
			print(')');
			decrementIndent();
		}

		if (!x.getValuesList().isEmpty()) {
			println();
			printValuesList(x);
		}

		if (x.getQuery() != null) {
			println();
			// accept this x.getQuery().accept(this);
		}

		if (x.getDuplicateKeyUpdate().size() != 0) {
			println();
			print0(ucase ? "ON DUPLICATE KEY UPDATE " : "on duplicate key update ");
			for (int i = 0, size = x.getDuplicateKeyUpdate().size(); i < size; ++i) {
				if (i != 0) {
					if (i % 5 == 0) {
						println();
					}
					print0(", ");
				}
				// accept this x.getDuplicateKeyUpdate().get(i).accept(this);
			}
		}

		return false;
	}

	protected void printValuesList(MySqlInsertStatement x) {
		print0(ucase ? "VALUES " : "values ");
		if (x.getValuesList().size() > 1) {
			incrementIndent();
		}
		for (int i = 0, size = x.getValuesList().size(); i < size; ++i) {
			if (i != 0) {
				print(',');
				println();
			}
			// accept this x.getValuesList().get(i).accept(this);
		}
		if (x.getValuesList().size() > 1) {
			decrementIndent();
		}
	}

	public boolean visit(MySqlLoadDataInFileStatement x) {
		print0(ucase ? "LOAD DATA " : "load data ");

		if (x.isLowPriority()) {
			print0(ucase ? "LOW_PRIORITY " : "low_priority ");
		}

		if (x.isConcurrent()) {
			print0(ucase ? "CONCURRENT " : "concurrent ");
		}

		if (x.isLocal()) {
			print0(ucase ? "LOCAL " : "local ");
		}

		print0(ucase ? "INFILE " : "infile ");

		// accept this x.getFileName().accept(this);

		if (x.isReplicate()) {
			print0(ucase ? " REPLACE " : " replace ");
		}

		if (x.isIgnore()) {
			print0(ucase ? " IGNORE " : " ignore ");
		}

		print0(ucase ? " INTO TABLE " : " into table ");
		// accept this x.getTableName().accept(this);

		if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
			print0(ucase ? " COLUMNS" : " columns");
			if (x.getColumnsTerminatedBy() != null) {
				print0(ucase ? " TERMINATED BY " : " terminated by ");
				// accept this x.getColumnsTerminatedBy().accept(this);
			}

			if (x.getColumnsEnclosedBy() != null) {
				if (x.isColumnsEnclosedOptionally()) {
					print0(ucase ? " OPTIONALLY" : " optionally");
				}
				print0(ucase ? " ENCLOSED BY " : " enclosed by ");
				// accept this x.getColumnsEnclosedBy().accept(this);
			}

			if (x.getColumnsEscaped() != null) {
				print0(ucase ? " ESCAPED BY " : " escaped by ");
				// accept this x.getColumnsEscaped().accept(this);
			}
		}

		if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
			print0(ucase ? " LINES" : " lines");
			if (x.getLinesStartingBy() != null) {
				print0(ucase ? " STARTING BY " : " starting by ");
				// accept this x.getLinesStartingBy().accept(this);
			}

			if (x.getLinesTerminatedBy() != null) {
				print0(ucase ? " TERMINATED BY " : " terminated by ");
				// accept this x.getLinesTerminatedBy().accept(this);
			}
		}

		if (x.getIgnoreLinesNumber() != null) {
			print0(ucase ? " IGNORE " : " ignore ");
			// accept this x.getIgnoreLinesNumber().accept(this);
			print0(ucase ? " LINES" : " lines");
		}

		if (x.getColumns().size() != 0) {
			print0(" (");
			printAndAccept(x.getColumns(), ", ");
			print(')');
		}

		if (x.getSetList().size() != 0) {
			print0(ucase ? " SET " : " set ");
			printAndAccept(x.getSetList(), ", ");
		}

		return false;
	}

	public boolean visit(MySqlReplaceStatement x) {
		print0(ucase ? "REPLACE " : "replace ");

		if (x.isLowPriority()) {
			print0(ucase ? "LOW_PRIORITY " : "low_priority ");
		}

		if (x.isDelayed()) {
			print0(ucase ? "DELAYED " : "delayed ");
		}

		print0(ucase ? "INTO " : "into ");

		// accept this x.getTableName().accept(this);

		if (x.getColumns().size() > 0) {
			print0(" (");
			for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
				if (i != 0) {
					print0(", ");
				}
				// accept this x.getColumns().get(i).accept(this);
			}
			print(')');
		}

		if (x.getValuesList().size() != 0) {
			println();
			print0(ucase ? "VALUES " : "values ");
			int size = x.getValuesList().size();
			if (size == 0) {
				print0("()");
			} else {
				for (int i = 0; i < size; ++i) {
					if (i != 0) {
						print0(", ");
					}
					// accept this x.getValuesList().get(i).accept(this);
				}
			}
		}

		if (x.getQuery() != null) {
			// accept this x.getQuery().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlStartTransactionStatement x) {
		print0(ucase ? "START TRANSACTION" : "start transaction");
		if (x.isConsistentSnapshot()) {
			print0(ucase ? " WITH CONSISTENT SNAPSHOT" : " with consistent snapshot");
		}

		if (x.getHints() != null && x.getHints().size() > 0) {
			print(' ');
			printAndAccept(x.getHints(), " ");
		}

		if (x.isBegin()) {
			print0(ucase ? " BEGIN" : " begin");
		}

		if (x.isWork()) {
			print0(ucase ? " WORK" : " work");
		}

		return false;
	}

	public boolean visit(MySqlCommitStatement x) {
		print0(ucase ? "COMMIT" : "commit");

		if (x.isWork()) {
			print0(ucase ? " WORK" : " work");
		}

		if (x.getChain() != null) {
			if (x.getChain().booleanValue()) {
				print0(ucase ? " AND CHAIN" : " and chain");
			} else {
				print0(ucase ? " AND NO CHAIN" : " and no chain");
			}
		}

		if (x.getRelease() != null) {
			if (x.getRelease().booleanValue()) {
				print0(ucase ? " AND RELEASE" : " and release");
			} else {
				print0(ucase ? " AND NO RELEASE" : " and no release");
			}
		}

		return false;
	}

	public boolean visit(MySqlRollbackStatement x) {
		print0(ucase ? "ROLLBACK" : "rollback");

		if (x.getChain() != null) {
			if (x.getChain().booleanValue()) {
				print0(ucase ? " AND CHAIN" : " and chain");
			} else {
				print0(ucase ? " AND NO CHAIN" : " and no chain");
			}
		}

		if (x.getRelease() != null) {
			if (x.getRelease().booleanValue()) {
				print0(ucase ? " AND RELEASE" : " and release");
			} else {
				print0(ucase ? " AND NO RELEASE" : " and no release");
			}
		}

		if (x.getTo() != null) {
			print0(ucase ? " TO " : " to ");
			// accept this x.getTo().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowColumnsStatement x) {
		if (x.isFull()) {
			print0(ucase ? "SHOW FULL COLUMNS" : "show full columns");
		} else {
			print0(ucase ? "SHOW COLUMNS" : "show columns");
		}

		if (x.getTable() != null) {
			print0(ucase ? " FROM " : " from ");
			if (x.getDatabase() != null) {
				// accept this x.getDatabase().accept(this);
				print('.');
			}
			// accept this x.getTable().accept(this);
		}

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(SQLShowTablesStatement x) {
		if (x.isFull()) {
			print0(ucase ? "SHOW FULL TABLES" : "show full tables");
		} else {
			print0(ucase ? "SHOW TABLES" : "show tables");
		}

		if (x.getDatabase() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getDatabase().accept(this);
		}

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowDatabasesStatement x) {
		print0(ucase ? "SHOW DATABASES" : "show databases");

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowWarningsStatement x) {
		if (x.isCount()) {
			print0(ucase ? "SHOW COUNT(*) WARNINGS" : "show count(*) warnings");
		} else {
			print0(ucase ? "SHOW WARNINGS" : "show warnings");
			if (x.getLimit() != null) {
				print(' ');
				// accept this x.getLimit().accept(this);
			}
		}

		return false;
	}

	public boolean visit(MySqlShowStatusStatement x) {
		print0(ucase ? "SHOW " : "show ");

		if (x.isGlobal()) {
			print0(ucase ? "GLOBAL " : "global ");
		}

		if (x.isSession()) {
			print0(ucase ? "SESSION " : "session ");
		}

		print0(ucase ? "STATUS" : "status");

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlLoadXmlStatement x) {
		print0(ucase ? "LOAD XML " : "load xml ");

		if (x.isLowPriority()) {
			print0(ucase ? "LOW_PRIORITY " : "low_priority ");
		}

		if (x.isConcurrent()) {
			print0(ucase ? "CONCURRENT " : "concurrent ");
		}

		if (x.isLocal()) {
			print0(ucase ? "LOCAL " : "local ");
		}

		print0(ucase ? "INFILE " : "infile ");

		// accept this x.getFileName().accept(this);

		if (x.isReplicate()) {
			print0(ucase ? " REPLACE " : " replace ");
		}

		if (x.isIgnore()) {
			print0(ucase ? " IGNORE " : " ignore ");
		}

		print0(ucase ? " INTO TABLE " : " into table ");
		// accept this x.getTableName().accept(this);

		if (x.getCharset() != null) {
			print0(ucase ? " CHARSET " : " charset ");
			print0(x.getCharset());
		}

		if (x.getRowsIdentifiedBy() != null) {
			print0(ucase ? " ROWS IDENTIFIED BY " : " rows identified by ");
			// accept this x.getRowsIdentifiedBy().accept(this);
		}

		if (x.getSetList().size() != 0) {
			print0(ucase ? " SET " : " set ");
			printAndAccept(x.getSetList(), ", ");
		}

		return false;
	}

	public boolean visit(CobarShowStatus x) {
		print0(ucase ? "SHOW COBAR_STATUS" : "show cobar_status");
		return false;
	}

	public boolean visit(MySqlKillStatement x) {
		if (MySqlKillStatement.Type.CONNECTION.equals(x.getType())) {
			print0(ucase ? "KILL CONNECTION " : "kill connection ");
		} else if (MySqlKillStatement.Type.QUERY.equals(x.getType())) {
			print0(ucase ? "KILL QUERY " : "kill query ");
		}
		// accept this x.getThreadId().accept(this);
		return false;
	}

	public boolean visit(MySqlBinlogStatement x) {
		print0(ucase ? "BINLOG " : "binlog ");
		// accept this x.getExpr().accept(this);
		return false;
	}

	public boolean visit(MySqlResetStatement x) {
		print0(ucase ? "RESET " : "reset ");
		for (int i = 0; i < x.getOptions().size(); ++i) {
			if (i != 0) {
				print0(", ");
			}
			print0(x.getOptions().get(i));
		}
		return false;
	}

	public boolean visit(MySqlCreateUserStatement x) {
		print0(ucase ? "CREATE USER " : "create user ");
		printAndAccept(x.getUsers(), ", ");
		return false;
	}

	public boolean visit(UserSpecification x) {
		// accept this x.getUser().accept(this);

		if (x.getPassword() != null) {
			print0(ucase ? " IDENTIFIED BY " : " identified by ");
			if (x.isPasswordHash()) {
				print0(ucase ? "PASSWORD " : "password ");
			}
			// accept this x.getPassword().accept(this);
		}

		if (x.getAuthPlugin() != null) {
			print0(ucase ? " IDENTIFIED WITH " : " identified with ");
			// accept this x.getAuthPlugin().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlPartitionByKey x) {
		if (x.isLinear()) {
			print0(ucase ? "PARTITION BY LINEAR KEY (" : "partition by linear key (");
		} else {
			print0(ucase ? "PARTITION BY KEY (" : "partition by key (");
		}
		printAndAccept(x.getColumns(), ", ");
		print(')');

		printPartitionsCountAndSubPartitions(x);
		return false;
	}

	//

	public boolean visit(MySqlOutFileExpr x) {
		print0(ucase ? "OUTFILE " : "outfile ");
		// accept this x.getFile().accept(this);

		if (x.getCharset() != null) {
			print0(ucase ? " CHARACTER SET " : " character set ");
			print0(x.getCharset());
		}

		if (x.getColumnsTerminatedBy() != null || x.getColumnsEnclosedBy() != null || x.getColumnsEscaped() != null) {
			print0(ucase ? " COLUMNS" : " columns");
			if (x.getColumnsTerminatedBy() != null) {
				print0(ucase ? " TERMINATED BY " : " terminated by ");
				// accept this x.getColumnsTerminatedBy().accept(this);
			}

			if (x.getColumnsEnclosedBy() != null) {
				if (x.isColumnsEnclosedOptionally()) {
					print0(ucase ? " OPTIONALLY" : " optionally");
				}
				print0(ucase ? " ENCLOSED BY " : " enclosed by ");
				// accept this x.getColumnsEnclosedBy().accept(this);
			}

			if (x.getColumnsEscaped() != null) {
				print0(ucase ? " ESCAPED BY " : " escaped by ");
				// accept this x.getColumnsEscaped().accept(this);
			}
		}

		if (x.getLinesStartingBy() != null || x.getLinesTerminatedBy() != null) {
			print0(ucase ? " LINES" : " lines");
			if (x.getLinesStartingBy() != null) {
				print0(ucase ? " STARTING BY " : " starting by ");
				// accept this x.getLinesStartingBy().accept(this);
			}

			if (x.getLinesTerminatedBy() != null) {
				print0(ucase ? " TERMINATED BY " : " terminated by ");
				// accept this x.getLinesTerminatedBy().accept(this);
			}
		}

		return false;
	}

	public boolean visit(MySqlDescribeStatement x) {
		print0(ucase ? "DESC " : "desc ");
		// accept this x.getObject().accept(this);
		if (x.getColName() != null) {
			print(' ');
			// accept this x.getColName().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlUpdateStatement x) {
		print0(ucase ? "UPDATE " : "update ");

		if (x.isLowPriority()) {
			print0(ucase ? "LOW_PRIORITY " : "low_priority ");
		}

		if (x.isIgnore()) {
			print0(ucase ? "IGNORE " : "ignore ");
		}

		// accept this x.getTableSource().accept(this);

		println();
		print0(ucase ? "SET " : "set ");
		for (int i = 0, size = x.getItems().size(); i < size; ++i) {
			if (i != 0) {
				print0(", ");
			}
			// accept this x.getItems().get(i).accept(this);
		}

		if (x.getWhere() != null) {
			println();
			incrementIndent();
			print0(ucase ? "WHERE " : "where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
			decrementIndent();
		}

		if (x.getOrderBy() != null) {
			println();
			// accept this x.getOrderBy().accept(this);
		}

		if (x.getLimit() != null) {
			println();
			// accept this x.getLimit().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlSetTransactionStatement x) {
		if (x.getGlobal() == null) {
			print0(ucase ? "SET TRANSACTION " : "set transaction ");
		} else if (x.getGlobal().booleanValue()) {
			print0(ucase ? "SET GLOBAL TRANSACTION " : "set global transaction ");
		} else {
			print0(ucase ? "SET SESSION TRANSACTION " : "set session transaction ");
		}

		if (x.getIsolationLevel() != null) {
			print0(ucase ? "ISOLATION LEVEL " : "isolation level ");
			print0(x.getIsolationLevel());
		}

		if (x.getAccessModel() != null) {
			print0(ucase ? "READ " : "read ");
			print0(x.getAccessModel());
		}

		return false;
	}

	public boolean visit(MySqlSetNamesStatement x) {
		print0(ucase ? "SET NAMES " : "set names ");
		if (x.isDefault()) {
			print0(ucase ? "DEFAULT" : "default");
		} else {
			print0(x.getCharSet());
			if (x.getCollate() != null) {
				print0(ucase ? " COLLATE " : " collate ");
				print0(x.getCollate());
			}
		}
		return false;
	}

	public boolean visit(MySqlSetCharSetStatement x) {
		print0(ucase ? "SET CHARACTER SET " : "set character set ");
		if (x.isDefault()) {
			print0(ucase ? "DEFAULT" : "default");
		} else {
			print0(x.getCharSet());
			if (x.getCollate() != null) {
				print0(ucase ? " COLLATE " : " collate ");
				print0(x.getCollate());
			}
		}
		return false;
	}

	public boolean visit(MySqlShowAuthorsStatement x) {
		print0(ucase ? "SHOW AUTHORS" : "show authors");
		return false;
	}

	public boolean visit(MySqlShowBinaryLogsStatement x) {
		print0(ucase ? "SHOW BINARY LOGS" : "show binary logs");
		return false;
	}

	public boolean visit(MySqlShowMasterLogsStatement x) {
		print0(ucase ? "SHOW MASTER LOGS" : "show master logs");
		return false;
	}

	public boolean visit(MySqlShowCollationStatement x) {
		print0(ucase ? "SHOW COLLATION" : "show collation");
		if (x.getPattern() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getPattern().accept(this);
		}
		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			// accept this x.getWhere().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlShowBinLogEventsStatement x) {
		print0(ucase ? "SHOW BINLOG EVENTS" : "show binlog events");
		if (x.getIn() != null) {
			print0(ucase ? " IN " : " in ");
			// accept this x.getIn().accept(this);
		}
		if (x.getFrom() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getFrom().accept(this);
		}
		if (x.getLimit() != null) {
			print(' ');
			// accept this x.getLimit().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlShowCharacterSetStatement x) {
		print0(ucase ? "SHOW CHARACTER SET" : "show character set");
		if (x.getPattern() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getPattern().accept(this);
		}
		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			// accept this x.getWhere().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlShowContributorsStatement x) {
		print0(ucase ? "SHOW CONTRIBUTORS" : "show contributors");
		return false;
	}

	public boolean visit(MySqlShowCreateDatabaseStatement x) {
		print0(ucase ? "SHOW CREATE DATABASE " : "show create database ");
		// accept this x.getDatabase().accept(this);
		return false;
	}

	public boolean visit(MySqlShowCreateEventStatement x) {
		print0(ucase ? "SHOW CREATE EVENT " : "show create event ");
		// accept this x.getEventName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowCreateFunctionStatement x) {
		print0(ucase ? "SHOW CREATE FUNCTION " : "show create function ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowCreateProcedureStatement x) {
		print0(ucase ? "SHOW CREATE PROCEDURE " : "show create procedure ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowCreateTableStatement x) {
		print0(ucase ? "SHOW CREATE TABLE " : "show create table ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowCreateTriggerStatement x) {
		print0(ucase ? "SHOW CREATE TRIGGER " : "show create trigger ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowCreateViewStatement x) {
		print0(ucase ? "SHOW CREATE VIEW " : "show create view ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowEngineStatement x) {
		print0(ucase ? "SHOW ENGINE " : "show engine ");
		// accept this x.getName().accept(this);
		print(' ');
		print0(x.getOption().name());
		return false;
	}

	public boolean visit(MySqlShowEventsStatement x) {
		print0(ucase ? "SHOW EVENTS" : "show events");
		if (x.getSchema() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getSchema().accept(this);
		}

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowFunctionCodeStatement x) {
		print0(ucase ? "SHOW FUNCTION CODE " : "show function code ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowFunctionStatusStatement x) {
		print0(ucase ? "SHOW FUNCTION STATUS" : "show function status");
		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowEnginesStatement x) {
		if (x.isStorage()) {
			print0(ucase ? "SHOW STORAGE ENGINES" : "show storage engines");
		} else {
			print0(ucase ? "SHOW ENGINES" : "show engines");
		}
		return false;
	}

	public boolean visit(MySqlShowErrorsStatement x) {
		if (x.isCount()) {
			print0(ucase ? "SHOW COUNT(*) ERRORS" : "show count(*) errors");
		} else {
			print0(ucase ? "SHOW ERRORS" : "show errors");
			if (x.getLimit() != null) {
				print(' ');
				// accept this x.getLimit().accept(this);
			}
		}
		return false;
	}

	public boolean visit(MySqlShowGrantsStatement x) {
		print0(ucase ? "SHOW GRANTS" : "show grants");
		if (x.getUser() != null) {
			print0(ucase ? " FOR " : " for ");
			// accept this x.getUser().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlUserName x) {
		print0(x.getUserName());
		if (x.getHost() != null) {
			print('@');
			print0(x.getHost());
		}
		return false;
	}

	public boolean visit(MySqlShowIndexesStatement x) {
		print0(ucase ? "SHOW INDEX" : "show index");

		if (x.getTable() != null) {
			print0(ucase ? " FROM " : " from ");
			if (x.getDatabase() != null) {
				// accept this x.getDatabase().accept(this);
				print('.');
			}
			// accept this x.getTable().accept(this);
		}

		if (x.getHints() != null && x.getHints().size() > 0) {
			print(' ');
			printAndAccept(x.getHints(), " ");
		}

		return false;
	}

	public boolean visit(MySqlShowKeysStatement x) {
		print0(ucase ? "SHOW KEYS" : "show keys");

		if (x.getTable() != null) {
			print0(ucase ? " FROM " : " from ");
			if (x.getDatabase() != null) {
				// accept this x.getDatabase().accept(this);
				print('.');
			}
			// accept this x.getTable().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlShowMasterStatusStatement x) {
		print0(ucase ? "SHOW MASTER STATUS" : "show master status");
		return false;
	}

	public boolean visit(MySqlShowOpenTablesStatement x) {
		print0(ucase ? "SHOW OPEN TABLES" : "show open tables");

		if (x.getDatabase() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getDatabase().accept(this);
		}

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowPluginsStatement x) {
		print0(ucase ? "SHOW PLUGINS" : "show plugins");
		return false;
	}

	public boolean visit(MySqlShowPrivilegesStatement x) {
		print0(ucase ? "SHOW PRIVILEGES" : "show privileges");
		return false;
	}

	public boolean visit(MySqlShowProcedureCodeStatement x) {
		print0(ucase ? "SHOW PROCEDURE CODE " : "show procedure code ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit(MySqlShowProcedureStatusStatement x) {
		print0(ucase ? "SHOW PROCEDURE STATUS" : "show procedure status");
		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			// accept this x.getWhere().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlShowProcessListStatement x) {
		if (x.isFull()) {
			print0(ucase ? "SHOW FULL PROCESSLIST" : "show full processlist");
		} else {
			print0(ucase ? "SHOW PROCESSLIST" : "show processlist");
		}
		return false;
	}

	public boolean visit(MySqlShowProfileStatement x) {
		print0(ucase ? "SHOW PROFILE" : "show profile");
		for (int i = 0; i < x.getTypes().size(); ++i) {
			if (i == 0) {
				print(' ');
			} else {
				print0(", ");
			}
			print0(x.getTypes().get(i).name);
		}

		if (x.getForQuery() != null) {
			print0(ucase ? " FOR QUERY " : " for query ");
			// accept this x.getForQuery().accept(this);
		}

		if (x.getLimit() != null) {
			print(' ');
			// accept this x.getLimit().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlShowProfilesStatement x) {
		print0(ucase ? "SHOW PROFILES" : "show profiles");
		return false;
	}

	public boolean visit(MySqlShowRelayLogEventsStatement x) {
		print0("SHOW RELAYLOG EVENTS");

		if (x.getLogName() != null) {
			print0(ucase ? " IN " : " in ");
			// accept this x.getLogName().accept(this);
		}

		if (x.getFrom() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getFrom().accept(this);
		}

		if (x.getLimit() != null) {
			print(' ');
			// accept this x.getLimit().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowSlaveHostsStatement x) {
		print0(ucase ? "SHOW SLAVE HOSTS" : "show slave hosts");
		return false;
	}

	public boolean visit(MySqlShowSlaveStatusStatement x) {
		print0(ucase ? "SHOW SLAVE STATUS" : "show slave status");
		return false;
	}

	public boolean visit(MySqlShowTableStatusStatement x) {
		print0(ucase ? "SHOW TABLE STATUS" : "show table status");
		if (x.getDatabase() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getDatabase().accept(this);
		}

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowTriggersStatement x) {
		print0(ucase ? "SHOW TRIGGERS" : "show triggers");

		if (x.getDatabase() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getDatabase().accept(this);
		}

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlShowVariantsStatement x) {
		print0(ucase ? "SHOW " : "show ");

		if (x.isGlobal()) {
			print0(ucase ? "GLOBAL " : "global ");
		}

		if (x.isSession()) {
			print0(ucase ? "SESSION " : "session ");
		}

		print0(ucase ? "VARIABLES" : "variables");

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}

		if (x.getWhere() != null) {
			print0(ucase ? " WHERE " : " where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
		}

		return false;
	}

	public boolean visit(SQLAlterTableStatement x) {
		if (x.isIgnore()) {
			print0(ucase ? "ALTER IGNORE TABLE " : "alter ignore table ");
		} else {
			print0(ucase ? "ALTER TABLE " : "alter table ");
		}
		// accept this x.getName().accept(this);
		incrementIndent();
		for (int i = 0; i < x.getItems().size(); ++i) {
			SQLAlterTableItem item = x.getItems().get(i);
			if (i != 0) {
				print(',');
			}
			println();
			// accept this item.accept(this);
		}

		if (x.isRemovePatiting()) {
			println();
			print0(ucase ? "REMOVE PARTITIONING" : "remove partitioning");
		}

		if (x.isUpgradePatiting()) {
			println();
			print0(ucase ? "UPGRADE PARTITIONING" : "upgrade partitioning");
		}

		decrementIndent();
		return false;
	}

	public boolean visit(SQLAlterTableAddColumn x) {
		print0(ucase ? "ADD COLUMN " : "add column ");

		if (x.getColumns().size() > 1) {
			print('(');
		}
		printAndAccept(x.getColumns(), ", ");
		if (x.getFirstColumn() != null) {
			print0(ucase ? " FIRST " : " first ");
			// accept this x.getFirstColumn().accept(this);
		} else if (x.getAfterColumn() != null) {
			print0(ucase ? " AFTER " : " after ");
			// accept this x.getAfterColumn().accept(this);
		} else if (x.isFirst()) {
			print0(ucase ? " FIRST" : " first");
		}

		if (x.getColumns().size() > 1) {
			print(')');
		}
		return false;
	}

	public boolean visit(MySqlRenameTableStatement.Item x) {
		// accept this x.getName().accept(this);
		print0(ucase ? " TO " : " to ");
		// accept this x.getTo().accept(this);
		return false;
	}

	public boolean visit(MySqlRenameTableStatement x) {
		print0(ucase ? "RENAME TABLE " : "rename table ");
		printAndAccept(x.getItems(), ", ");
		return false;
	}

	public boolean visit(MySqlUnionQuery x) {
		{
			boolean needParen = false;
			if (x.getLeft() instanceof MySqlSelectQueryBlock) {
				MySqlSelectQueryBlock right = (MySqlSelectQueryBlock) x.getLeft();
				if (right.getOrderBy() != null || right.getLimit() != null) {
					needParen = true;
				}
			}
			if (needParen) {
				print('(');
				// accept this x.getLeft().accept(this);
				print(')');
			} else {
				// accept this x.getLeft().accept(this);
			}
		}
		println();
		print0(ucase ? x.getOperator().name : x.getOperator().name_lcase);
		println();

		boolean needParen = false;

		if (x.getOrderBy() != null || x.getLimit() != null) {
			needParen = true;
		} else if (x.getRight() instanceof MySqlSelectQueryBlock) {
			MySqlSelectQueryBlock right = (MySqlSelectQueryBlock) x.getRight();
			if (right.getOrderBy() != null || right.getLimit() != null) {
				needParen = true;
			}
		}

		if (needParen) {
			print('(');
			// accept this x.getRight().accept(this);
			print(')');
		} else {
			// accept this x.getRight().accept(this);
		}

		if (x.getOrderBy() != null) {
			println();
			// accept this x.getOrderBy().accept(this);
		}

		if (x.getLimit() != null) {
			println();
			// accept this x.getLimit().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlUseIndexHint x) {
		print0(ucase ? "USE INDEX " : "use index ");
		if (x.getOption() != null) {
			print0(ucase ? "FOR " : "for ");
			print0(x.getOption().name);
			print(' ');
		}
		print('(');
		printAndAccept(x.getIndexList(), ", ");
		print(')');
		return false;
	}

	public boolean visit(MySqlIgnoreIndexHint x) {
		print0(ucase ? "IGNORE INDEX " : "ignore index ");
		if (x.getOption() != null) {
			print0(ucase ? "FOR " : "for ");
			print0(ucase ? x.getOption().name : x.getOption().name_lcase);
			print(' ');
		}
		print('(');
		printAndAccept(x.getIndexList(), ", ");
		print(')');
		return false;
	}

	public boolean visit(SQLExprTableSource x) {
		// accept this x.getExpr().accept(this);

		if (x.getAlias() != null) {
			print(' ');
			print0(x.getAlias());
		}

		for (int i = 0; i < x.getHintsSize(); ++i) {
			print(' ');
			// accept this x.getHints().get(i).accept(this);
		}

		if (x.getPartitionSize() > 0) {
			print0(ucase ? " PARTITION (" : " partition (");
			printlnAndAccept(x.getPartitions(), ", ");
			print(')');
		}

		return false;
	}

	public boolean visit(MySqlLockTableStatement x) {
		print0(ucase ? "LOCK TABLES " : "lock tables ");
		// accept this x.getTableSource().accept(this);
		if (x.getLockType() != null) {
			print(' ');
			print0(x.getLockType().name);
		}

		if (x.getHints() != null && x.getHints().size() > 0) {
			print(' ');
			printAndAccept(x.getHints(), " ");
		}
		return false;
	}

	public boolean visit(MySqlUnlockTablesStatement x) {
		print0(ucase ? "UNLOCK TABLES" : "unlock tables");
		return false;
	}

	public boolean visit(MySqlForceIndexHint x) {
		print0(ucase ? "FORCE INDEX " : "force index ");
		if (x.getOption() != null) {
			print0(ucase ? "FOR " : "for ");
			print0(x.getOption().name);
			print(' ');
		}
		print('(');
		printAndAccept(x.getIndexList(), ", ");
		print(')');
		return false;
	}

	public boolean visit(MySqlAlterTableChangeColumn x) {
		print0(ucase ? "CHANGE COLUMN " : "change column ");
		// accept this x.getColumnName().accept(this);
		print(' ');
		// accept this x.getNewColumnDefinition().accept(this);
		if (x.getFirstColumn() != null) {
			print0(ucase ? " FIRST " : " first ");
			// accept this x.getFirstColumn().accept(this);
		} else if (x.getAfterColumn() != null) {
			print0(ucase ? " AFTER " : " after ");
			// accept this x.getAfterColumn().accept(this);
		} else if (x.isFirst()) {
			print0(ucase ? " FIRST" : " first");
		}

		return false;
	}

	public boolean visit(MySqlAlterTableModifyColumn x) {
		print0(ucase ? "MODIFY COLUMN " : "modify column ");
		// accept this x.getNewColumnDefinition().accept(this);
		if (x.getFirstColumn() != null) {
			print0(ucase ? " FIRST " : " first ");
			// accept this x.getFirstColumn().accept(this);
		} else if (x.getAfterColumn() != null) {
			print0(ucase ? " AFTER " : " after ");
			// accept this x.getAfterColumn().accept(this);
		} else if (x.isFirst()) {
			print0(ucase ? " FIRST" : " first");
		}

		return false;
	}

	public boolean visit(MySqlAlterTableCharacter x) {
		print0(ucase ? "CHARACTER SET = " : "character set = ");
		// accept this x.getCharacterSet().accept(this);

		if (x.getCollate() != null) {
			print0(ucase ? ", COLLATE = " : ", collate = ");
			// accept this x.getCollate().accept(this);
		}

		return false;
	}

	public boolean visit(MySqlAlterTableOption x) {
		print0(x.getName());
		print0(" = ");
		print0(x.getValue().toString());
		return false;
	}

	public boolean visit(MySqlHelpStatement x) {
		print0(ucase ? "HELP " : "help ");
		// accept this x.getContent().accept(this);
		return false;
	}

	public boolean visit(MySqlCharExpr x) {
		print0(x.toString());
		return false;
	}

	public boolean visit(MySqlUnique x) {
		if (x.isHasConstaint()) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			if (x.getName() != null) {
				// accept this x.getName().accept(this);
				print(' ');
			}
		}

		print0(ucase ? "UNIQUE" : "unique");

		if (x.getIndexName() != null) {
			print(' ');
			// accept this x.getIndexName().accept(this);
		}

		if (x.getIndexType() != null) {
			print0(ucase ? " USING " : " using ");
			;
			print0(x.getIndexType());
		}

		print0(" (");
		printAndAccept(x.getColumns(), ", ");
		print(')');

		return false;
	}

	public boolean visit(MysqlForeignKey x) {
		if (x.isHasConstraint()) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			if (x.getName() != null) {
				// accept this x.getName().accept(this);
				print(' ');
			}
		}

		print0(ucase ? "FOREIGN KEY" : "foreign key");

		if (x.getIndexName() != null) {
			print(' ');
			// accept this x.getIndexName().accept(this);
		}

		print0(" (");
		printAndAccept(x.getReferencingColumns(), ", ");
		print(')');

		print0(ucase ? " REFERENCES " : " references ");
		// accept this x.getReferencedTableName().accept(this);

		print0(" (");
		printAndAccept(x.getReferencedColumns(), ", ");
		print(')');

		MysqlForeignKey.Match match = x.getReferenceMatch();
		if (match != null) {
			print0(ucase ? " MATCH " : " match ");
			print0(ucase ? match.name : match.name_lcase);
		}

		MysqlForeignKey.On on = x.getReferenceOn();
		if (on != null) {
			print0(ucase ? " ON " : " on ");
			print0(ucase ? on.name : on.name_lcase);
			print(' ');
			Option option = x.getReferenceOption();
			if (option != null) {
				print0(ucase ? option.name : option.name_lcase);
			}
		}
		return false;
	}

	public boolean visit(MySqlAlterTableDiscardTablespace x) {
		print0(ucase ? "DISCARD TABLESPACE" : "discard tablespace");
		return false;
	}

	public boolean visit(MySqlAlterTableImportTablespace x) {
		print0(ucase ? "IMPORT TABLESPACE" : "import tablespace");
		return false;
	}

	public boolean visit(SQLAssignItem x) {
		// accept this x.getTarget().accept(this);
		if (!"NAMES".equalsIgnoreCase(x.getTarget().toString())) {
			print0(" = ");
		}
		// accept this x.getValue().accept(this);
		return false;
	}

	public boolean visit(TableSpaceOption x) {
		// accept this x.getName().accept(this);

		if (x.getStorage() != null) {
			print(' ');
			// accept this x.getStorage().accept(this);
		}
		return false;
	}

	protected void visitAggreateRest(SQLAggregateExpr aggregateExpr) {
		{
			SQLOrderBy value = (SQLOrderBy) aggregateExpr.getAttribute("ORDER BY");
			if (value != null) {
				print(' ');
				// accept this ((SQLObject) value).accept(this);
			}
		}
		{
			Object value = aggregateExpr.getAttribute("SEPARATOR");
			if (value != null) {
				print0(ucase ? " SEPARATOR " : " separator ");
				// accept this ((SQLObject) value).accept(this);
			}
		}
	}

	public boolean visit(MySqlAnalyzeStatement x) {
		print0(ucase ? "ANALYZE " : "analyze ");
		if (x.isNoWriteToBinlog()) {
			print0(ucase ? "NO_WRITE_TO_BINLOG " : "no_write_to_binlog ");
		}

		if (x.isLocal()) {
			print0(ucase ? "LOCAL " : "local ");
		}

		print0(ucase ? "TABLE " : "table ");

		printAndAccept(x.getTableSources(), ", ");
		return false;
	}

	public boolean visit(MySqlOptimizeStatement x) {
		print0(ucase ? "OPTIMIZE " : "optimize ");
		if (x.isNoWriteToBinlog()) {
			print0(ucase ? "NO_WRITE_TO_BINLOG " : "No_write_to_binlog ");
		}

		if (x.isLocal()) {
			print0(ucase ? "LOCAL " : "local ");
		}

		print0(ucase ? "TABLE " : "table ");

		printAndAccept(x.getTableSources(), ", ");
		return false;
	}

	public boolean visit(MySqlAlterUserStatement x) {
		print0(ucase ? "ALTER USER" : "alter user");
		for (SQLExpr user : x.getUsers()) {
			print(' ');
			// accept this user.accept(this);
			print0(ucase ? " PASSWORD EXPIRE" : " password expire");
		}
		return false;
	}

	public boolean visit(MySqlSetPasswordStatement x) {
		print0(ucase ? "SET PASSWORD " : "set password ");

		if (x.getUser() != null) {
			print0(ucase ? "FOR " : "for ");
			// accept this x.getUser().accept(this);
			print(' ');
		}

		print0("= ");

		if (x.getPassword() != null) {
			// accept this x.getPassword().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlHintStatement x) {
		List<SQLCommentHint> hints = x.getHints();

		for (SQLCommentHint hint : hints) {
			// accept this hint.accept(this);
		}
		return false;
	}

	public boolean visit(MySqlOrderingExpr x) {
		// accept this x.getExpr().accept(this);
		if (x.getType() != null) {
			print(' ');
			print0(ucase ? x.getType().name : x.getType().name_lcase);
		}

		return false;
	}

	public boolean visit(SQLBlockStatement x) {
		if (x.getLabelName() != null && !x.getLabelName().equals("")) {
			print0(x.getLabelName());
			print0(": ");
		}
		print0(ucase ? "BEGIN" : "begin");
		incrementIndent();
		println();
		for (int i = 0, size = x.getStatementList().size(); i < size; ++i) {
			if (i != 0) {
				println();
			}
			SQLStatement stmt = x.getStatementList().get(i);
			stmt.setParent(x);
			// accept this stmt.accept(this);
			print(';');
		}
		decrementIndent();
		println();
		print0(ucase ? "END" : "end");
		if (x.getLabelName() != null && !x.getLabelName().equals("")) {
			print(' ');
			print0(x.getLabelName());
		}
		return false;
	}

	/**
	 * visit procedure create node
	 */

	public boolean visit(SQLCreateProcedureStatement x) {
		if (x.isOrReplace()) {
			print0(ucase ? "CREATE OR REPLACE PROCEDURE " : "create or replace procedure ");
		} else {
			print0(ucase ? "CREATE PROCEDURE " : "create procedure ");
		}
		// accept this x.getName().accept(this);

		int paramSize = x.getParameters().size();

		print0(" (");
		if (paramSize > 0) {
			incrementIndent();
			println();

			for (int i = 0; i < paramSize; ++i) {
				if (i != 0) {
					print0(", ");
					println();
				}
				SQLParameter param = x.getParameters().get(i);
				// accept this param.accept(this);
			}

			decrementIndent();
			println();
		}
		print(')');

		println();
		x.getBlock().setParent(x);
		// accept this x.getBlock().accept(this);
		return false;
	}

	public boolean visit(MySqlWhileStatement x) {
		if (x.getLabelName() != null && !x.getLabelName().equals("")) {
			print0(x.getLabelName());
			print0(": ");
		}
		print0(ucase ? "WHILE " : "while ");
		// accept this x.getCondition().accept(this);
		print0(ucase ? " DO" : " do");
		println();
		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}
		println();
		print0(ucase ? "END WHILE" : "end while");
		if (x.getLabelName() != null && !x.getLabelName().equals(""))
			print(' ');
		print0(x.getLabelName());
		return false;
	}

	public boolean visit(SQLIfStatement x) {
		print0(ucase ? "IF " : "if ");
		// accept this x.getCondition().accept(this);
		print0(ucase ? " THEN" : " then");
		println();
		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}
		println();
		for (SQLIfStatement.ElseIf iterable_element : x.getElseIfList()) {
			// accept this iterable_element.accept(this);
		}

		// accept this if (x.getElseItem() != null)
		// x.getElseItem().accept(this);

		print0(ucase ? "END IF" : "end if");
		return false;
	}

	public boolean visit(SQLIfStatement.ElseIf x) {
		print0(ucase ? "ELSE IF " : "else if ");
		// accept this x.getCondition().accept(this);
		print0(ucase ? " THEN" : " then");
		println();
		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}
		println();
		return false;
	}

	public boolean visit(SQLIfStatement.Else x) {
		print0(ucase ? "ELSE " : "else ");
		println();
		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}
		println();
		return false;
	}

	public boolean visit(MySqlCaseStatement x) {
		print0(ucase ? "CASE " : "case ");
		// accept this x.getCondition().accept(this);
		println();
		for (int i = 0; i < x.getWhenList().size(); i++) {
			// accept this x.getWhenList().get(i).accept(this);
		}
		// accept this if (x.getElseItem() != null)
		// x.getElseItem().accept(this);
		print0(ucase ? "END CASE" : "end case");
		return false;
	}

	public boolean visit(MySqlDeclareStatement x) {
		print0(ucase ? "DECLARE " : "declare ");
		printAndAccept(x.getVarList(), ", ");
		return false;
	}

	public boolean visit(MySqlSelectIntoStatement x) {
		// accept this x.getSelect().accept(this);
		print0(ucase ? " INTO " : " into ");
		for (int i = 0; i < x.getVarList().size(); i++) {
			// accept this x.getVarList().get(i).accept(this);
			if (i != x.getVarList().size() - 1)
				print0(", ");
		}
		return false;
	}

	public boolean visit(MySqlWhenStatement x) {
		print0(ucase ? "WHEN " : "when ");
		// accept this x.getCondition().accept(this);
		print0(" THEN");
		println();
		for (int i = 0; i < x.getStatements().size(); i++) {
			// accept this x.getStatements().get(i).accept(this);
			if (i != x.getStatements().size() - 1) {
				println();
			}
		}
		println();
		return false;
	}

	public boolean visit(SQLLoopStatement x) {
		if (x.getLabelName() != null && !x.getLabelName().equals("")) {
			print0(x.getLabelName());
			print0(": ");
		}

		print0(ucase ? "LOOP " : "loop ");
		println();
		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}
		println();
		print0(ucase ? "END LOOP" : "end loop");
		if (x.getLabelName() != null && !x.getLabelName().equals("")) {
			print0(" ");
			print0(x.getLabelName());
		}
		return false;
	}

	public boolean visit(MySqlLeaveStatement x) {
		print0(ucase ? "LEAVE " : "leave ");
		print0(x.getLabelName());
		return false;
	}

	public boolean visit(MySqlIterateStatement x) {
		print0(ucase ? "ITERATE " : "iterate ");
		print0(x.getLabelName());
		return false;
	}

	public boolean visit(MySqlRepeatStatement x) {
		// TODO Auto-generated method stub
		if (x.getLabelName() != null && !x.getLabelName().equals("")) {
			print0(x.getLabelName());
			print0(": ");
		}

		print0(ucase ? "REPEAT " : "repeat ");
		println();
		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}
		println();
		print0(ucase ? "UNTIL " : "until ");
		// accept this x.getCondition().accept(this);
		println();
		print0(ucase ? "END REPEAT" : "end repeat");
		if (x.getLabelName() != null && !x.getLabelName().equals("")) {
			print(' ');
			print0(x.getLabelName());
		}
		return false;
	}

	public boolean visit(MySqlCursorDeclareStatement x) {
		print0(ucase ? "DECLARE " : "declare ");
		print0(x.getCursorName());
		print0(ucase ? " CURSOR FOR " : " cursor for ");
		// accept this x.getSelect().accept(this);
		return false;
	}

	public boolean visit(MySqlUpdateTableSource x) {
		MySqlUpdateStatement update = x.getUpdate();
		if (update != null) {
			// accept this update.accept0(this);
		}
		return false;
	}

	public boolean visit(MySqlAlterTableAlterColumn x) {
		print0(ucase ? "ALTER COLUMN " : "alter column ");
		// accept this x.getColumn().accept(this);
		if (x.getDefaultExpr() != null) {
			print0(ucase ? " SET DEFAULT " : " set default ");
			// accept this x.getDefaultExpr().accept(this);
		} else if (x.isDropDefault()) {
			print0(ucase ? " DROP DEFAULT" : " drop default");
		}
		return false;
	}

	public boolean visit(MySqlSubPartitionByKey x) {
		if (x.isLinear()) {
			print0(ucase ? "SUBPARTITION BY LINEAR KEY (" : "subpartition by linear key (");
		} else {
			print0(ucase ? "SUBPARTITION BY KEY (" : "subpartition by key (");
		}
		printAndAccept(x.getColumns(), ", ");
		print(')');

		if (x.getSubPartitionsCount() != null) {
			print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
			// accept this x.getSubPartitionsCount().accept(this);
		}
		return false;
	}

	public boolean visit(MySqlSubPartitionByList x) {
		print0(ucase ? "SUBPARTITION BY LIST " : "subpartition by list ");
		if (x.getExpr() != null) {
			print('(');
			// accept this x.getExpr().accept(this);
			print0(") ");
		} else {
			if (x.getColumns().size() == 1 && Boolean.TRUE.equals(x.getAttribute("ads.subPartitionList"))) {
				print('(');
			} else {
				print0(ucase ? "COLUMNS (" : "columns (");
			}
			printAndAccept(x.getColumns(), ", ");
			print(")");
		}

		if (x.getOptions().size() != 0) {
			println();
			print0(ucase ? "SUBPARTITION OPTIONS (" : "subpartition options (");
			printAndAccept(x.getOptions(), ", ");
			print(')');
		}

		return false;
	}

	

	/*
	 * ======== SQLASTOutputVisitor
	 */

	protected final Appendable appender = null;
	private String indent = "\t";
	private int indentCount = 0;
	private boolean prettyFormat = true;
	protected boolean ucase = true;
	protected int selectListNumberOfLine = 5;

	protected boolean groupItemSingleLine = false;

	private List<Object> parameters;

	protected String dbType;

	public int getParametersSize() {
		if (parameters == null) {
			return 0;
		}

		return this.parameters.size();
	}

	public List<Object> getParameters() {
		if (parameters == null) {
			parameters = new ArrayList<Object>();
		}

		return parameters;
	}

	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	public int getIndentCount() {
		return indentCount;
	}

	public Appendable getAppender() {
		return appender;
	}

	public boolean isPrettyFormat() {
		return prettyFormat;
	}

	public void setPrettyFormat(boolean prettyFormat) {
		this.prettyFormat = prettyFormat;
	}

	public void decrementIndent() {
		this.indentCount -= 1;
	}

	public void incrementIndent() {
		this.indentCount += 1;
	}

	public void print(char value) {
		try {
			this.appender.append(value);
		} catch (IOException e) {
			throw new RuntimeException("println error", e);
		}
	}

	public void print(int value) {
		print0(Integer.toString(value));
	}

	public void print(Date date) {
		SimpleDateFormat dateFormat;
		if (date instanceof java.sql.Timestamp) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		} else {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		}
		print0("'" + dateFormat.format(date) + "'");
	}

	public void print(long value) {
		print0(Long.toString(value));
	}

	public void print(String text) {
		print0(text);
	}

	protected void print0(String text) {
		try {
			this.appender.append(text);
		} catch (IOException e) {
			throw new RuntimeException("println error", e);
		}
	}

	protected void printAlias(String alias) {
		if ((alias != null) && (alias.length() > 0)) {
			print(' ');
			print0(alias);
		}
	}

	protected void printAndAccept(List<? extends SQLObject> nodes, String seperator) {
		for (int i = 0, size = nodes.size(); i < size; ++i) {
			if (i != 0) {
				print0(seperator);
			}
			// accept this nodes.get(i).accept(this);
		}
	}

	protected void printSelectList(List<SQLSelectItem> selectList) {
		incrementIndent();
		for (int i = 0, size = selectList.size(); i < size; ++i) {
			if (i != 0) {
				if (i % selectListNumberOfLine == 0) {
					println();
				}

				print0(", ");
			}

			// accept this selectList.get(i).accept(this);
		}
		decrementIndent();
	}

	protected void printlnAndAccept(List<? extends SQLObject> nodes, String seperator) {
		for (int i = 0, size = nodes.size(); i < size; ++i) {
			if (i != 0) {
				println(seperator);
			}

			// accept this ((SQLObject) nodes.get(i)).accept(this);
		}
	}

	public void printIndent() {
		for (int i = 0; i < this.indentCount; ++i) {
			print0(this.indent);
		}
	}

	public void println() {
		if (!isPrettyFormat()) {
			print(' ');
			return;
		}

		print0("\n");
		printIndent();
	}

	public void println(String text) {
		print(text);
		println();
	}

	protected void println0(String text) {
		print0(text);
		println();
	}

	// ////////////////////

	public boolean visit_SQLASTOutputVisitor(SQLBetweenExpr x) {
		// accept this x.getTestExpr().accept(this);

		if (x.isNot()) {
			print0(ucase ? " NOT BETWEEN " : " not between ");
		} else {
			print0(ucase ? " BETWEEN " : " between ");
		}

		// accept this x.getBeginExpr().accept(this);
		print0(ucase ? " AND " : " and ");
		// accept this x.getEndExpr().accept(this);

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLBinaryOpExpr x) {
		SQLObject parent = x.getParent();
		boolean isRoot = parent instanceof SQLSelectQueryBlock;
		boolean relational = x.getOperator() == SQLBinaryOperator.BooleanAnd || x.getOperator() == SQLBinaryOperator.BooleanOr;

		if (isRoot && relational) {
			incrementIndent();
		}

		List<SQLExpr> groupList = new ArrayList<SQLExpr>();
		SQLExpr left = x.getLeft();
		for (;;) {
			if (left instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) left).getOperator() == x.getOperator()) {
				SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
				groupList.add(binaryLeft.getRight());
				left = binaryLeft.getLeft();
			} else {
				groupList.add(left);
				break;
			}
		}

		for (int i = groupList.size() - 1; i >= 0; --i) {
			SQLExpr item = groupList.get(i);

			if (relational) {
				if (isPrettyFormat() && item.hasBeforeComment()) {
					printlnComments(item.getBeforeCommentsDirect());
				}
			}

			if (isPrettyFormat() && item.hasBeforeComment()) {
				printlnComments(item.getBeforeCommentsDirect());
			}

			visitBinaryLeft(item, x.getOperator());

			if (isPrettyFormat() && item.hasAfterComment()) {
				print(' ');
				printComment(item.getAfterCommentsDirect(), "\n");
			}

			if (i != groupList.size() - 1 && isPrettyFormat() && item.getParent().hasAfterComment()) {
				print(' ');
				printComment(item.getParent().getAfterCommentsDirect(), "\n");
			}

			if (relational) {
				println();
			} else {
				print0(" ");
			}
			print0(ucase ? x.getOperator().name : x.getOperator().name_lcase);
			print0(" ");
		}

		visitorBinaryRight(x);

		if (isRoot && relational) {
			decrementIndent();
		}

		return false;
	}

	private void visitorBinaryRight(SQLBinaryOpExpr x) {
		if (isPrettyFormat() && x.getRight().hasBeforeComment()) {
			printlnComments(x.getRight().getBeforeCommentsDirect());
		}

		if (x.getRight() instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr right = (SQLBinaryOpExpr) x.getRight();
			boolean rightRational = right.getOperator() == SQLBinaryOperator.BooleanAnd || right.getOperator() == SQLBinaryOperator.BooleanOr;

			if (right.getOperator().priority >= x.getOperator().priority) {
				if (rightRational) {
					incrementIndent();
				}

				print('(');
				// accept this right.accept(this);
				print(')');

				if (rightRational) {
					decrementIndent();
				}
			} else {
				// accept this right.accept(this);
			}
		} else {
			// accept this x.getRight().accept(this);
		}

		if (x.getRight().hasAfterComment() && isPrettyFormat()) {
			print(' ');
			printlnComments(x.getRight().getAfterCommentsDirect());
		}
	}

	private void visitBinaryLeft(SQLExpr left, SQLBinaryOperator op) {
		if (left instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
			boolean leftRational = binaryLeft.getOperator() == SQLBinaryOperator.BooleanAnd || binaryLeft.getOperator() == SQLBinaryOperator.BooleanOr;

			if (binaryLeft.getOperator().priority > op.priority) {
				if (leftRational) {
					incrementIndent();
				}
				print('(');
				// accept this left.accept(this);
				print(')');

				if (leftRational) {
					decrementIndent();
				}
			} else {
				// accept this left.accept(this);
			}
		} else {
			// accept this left.accept(this);
		}
	}

	public boolean visit_SQLASTOutputVisitor(SQLCaseExpr x) {
		print0(ucase ? "CASE " : "case ");
		if (x.getValueExpr() != null) {
			// accept this x.getValueExpr().accept(this);
			print0(" ");
		}

		printAndAccept(x.getItems(), " ");

		if (x.getElseExpr() != null) {
			print0(ucase ? " ELSE " : " else ");
			// accept this x.getElseExpr().accept(this);
		}

		print0(ucase ? " END" : " end");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCaseExpr.Item x) {
		print0(ucase ? "WHEN " : "when ");
		// accept this x.getConditionExpr().accept(this);
		print0(ucase ? " THEN " : " then ");
		// accept this x.getValueExpr().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCastExpr x) {
		print0(ucase ? "CAST(" : "cast(");
		// accept this x.getExpr().accept(this);
		print0(ucase ? " AS " : " as ");
		// accept this x.getDataType().accept(this);
		print0(")");

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCharExpr x) {
		if (x.getText() == null) {
			print0(ucase ? "NULL" : "null");
		} else {
			print('\'');
			print0(x.getText().replaceAll("'", "''"));
			print('\'');
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDataType x) {
		print0(x.getName());
		if (x.getArguments().size() > 0) {
			print('(');
			printAndAccept(x.getArguments(), ", ");
			print(')');
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCharacterDataType x) {
		visit_SQLASTOutputVisitor((SQLDataType) x);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLExistsExpr x) {
		if (x.isNot()) {
			print0(ucase ? "NOT EXISTS (" : "not exists (");
		} else {
			print0(ucase ? "EXISTS (" : "exists (");
		}
		incrementIndent();
		println();
		// accept this x.getSubQuery().accept(this);
		println();
		decrementIndent();
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLIdentifierExpr x) {
		print0(x.getName());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLInListExpr x) {
		// accept this x.getExpr().accept(this);

		if (x.isNot()) {
			print0(ucase ? " NOT IN (" : " not in (");
		} else {
			print0(ucase ? " IN (" : " in (");
		}

		final List<SQLExpr> list = x.getTargetList();

		boolean printLn = false;
		if (list.size() > 5) {
			printLn = true;
			for (int i = 0, size = list.size(); i < size; ++i) {
				if (!(list.get(i) instanceof SQLCharExpr)) {
					printLn = false;
					break;
				}
			}
		}

		if (printLn) {
			incrementIndent();
			println();
			for (int i = 0, size = list.size(); i < size; ++i) {
				if (i != 0) {
					print0(", ");
					println();
				}
				// accept this list.get(i).accept(this);
			}
			decrementIndent();
			println();
		} else {
			printAndAccept(x.getTargetList(), ", ");
		}

		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLIntegerExpr x) {
		return false;
		// utils visit return
		// SQLASTOutputVisitorUtils.visit_SQLASTOutputVisitor(this, x);
	}

	public boolean visit_SQLASTOutputVisitor(SQLMethodInvokeExpr x) {
		if (x.getOwner() != null) {
			// accept this x.getOwner().accept(this);
			print('.');
		}
		printFunctionName(x.getMethodName());
		print('(');
		printAndAccept(x.getParameters(), ", ");
		print(')');
		return false;
	}

	protected void printFunctionName(String name) {
		print0(name);
	}

	public boolean visit_SQLASTOutputVisitor(SQLAggregateExpr x) {
		print0(ucase ? x.getMethodName() : x.getMethodName().toLowerCase());
		print('(');

		if (x.getOption() != null) {
			print0(x.getOption().toString());
			print(' ');
		}

		printAndAccept(x.getArguments(), ", ");

		visitAggreateRest(x);

		print(')');

		if (x.getWithinGroup() != null) {
			print0(ucase ? " WITHIN GROUP (" : " within group (");
			// accept this x.getWithinGroup().accept(this);
			print(')');
		}

		if (x.getKeep() != null) {
			print(' ');
			// accept this x.getKeep().accept(this);
		}

		if (x.getOver() != null) {
			print(' ');
			// accept this x.getOver().accept(this);
		}
		return false;
	}

	protected void visitAggreateRest_SQLASTOutputVisitor(SQLAggregateExpr aggregateExpr) {

	}

	public boolean visit_SQLASTOutputVisitor(SQLAllColumnExpr x) {
		print('*');
		return true;
	}

	public boolean visit_SQLASTOutputVisitor(SQLNCharExpr x) {
		if ((x.getText() == null) || (x.getText().length() == 0)) {
			print0(ucase ? "NULL" : "null");
		} else {
			print0(ucase ? "N'" : "n'");
			print0(x.getText().replace("'", "''"));
			print('\'');
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLNotExpr x) {
		print0(ucase ? "NOT " : "not ");
		SQLExpr expr = x.getExpr();

		boolean needQuote = false;

		if (expr instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) expr;
			needQuote = binaryOpExpr.getOperator().isLogical();
		}

		if (needQuote) {
			print('(');
		}
		// accept this expr.accept(this);

		if (needQuote) {
			print(')');
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLNullExpr x) {
		print0(ucase ? "NULL" : "null");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLNumberExpr x) {
		return false;
		// utils visit return SQLASTOutputVisitorUtils.visit(this, x);
	}

	public boolean visit_SQLASTOutputVisitor(SQLPropertyExpr x) {
		// accept this x.getOwner().accept(this);
		print('.');
		print0(x.getName());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLQueryExpr x) {
		SQLObject parent = x.getParent();
		if (parent instanceof SQLSelect) {
			parent = parent.getParent();
		}

		if (parent instanceof SQLStatement) {
			incrementIndent();

			println();
			// accept this x.getSubQuery().accept(this);

			decrementIndent();
		} else if (parent instanceof ValuesClause) {
			println();
			print('(');
			// accept this x.getSubQuery().accept(this);
			print(')');
			println();
		} else {
			print('(');
			incrementIndent();
			println();
			// accept this x.getSubQuery().accept(this);
			println();
			decrementIndent();
			print(')');
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSelectGroupByClause x) {
		int itemSize = x.getItems().size();
		if (itemSize > 0) {
			print0(ucase ? "GROUP BY " : "group by ");
			incrementIndent();
			for (int i = 0; i < itemSize; ++i) {
				if (i != 0) {
					if (groupItemSingleLine) {
						println(", ");
					} else {
						print(", ");
					}
				}
				// accept this x.getItems().get(i).accept(this);
			}
			decrementIndent();
		}

		if (x.getHaving() != null) {
			println();
			print0(ucase ? "HAVING " : "having ");
			// accept this x.getHaving().accept(this);
		}

		if (x.isWithRollUp()) {
			print0(ucase ? " WITH ROLLUP" : " with rollup");
		}

		if (x.isWithCube()) {
			print0(ucase ? " WITH CUBE" : " with cube");
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSelect x) {
		x.getQuery().setParent(x);

		if (x.getWithSubQuery() != null) {
			// accept this x.getWithSubQuery().accept(this);
			println();
		}

		// accept this x.getQuery().accept(this);

		if (x.getOrderBy() != null) {
			println();
			// accept this x.getOrderBy().accept(this);
		}

		if (x.getHintsSize() > 0) {
			printAndAccept(x.getHints(), "");
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSelectQueryBlock x) {
		if (isPrettyFormat() && x.hasBeforeComment()) {
			printComment(x.getBeforeCommentsDirect(), "\n");
		}

		print0(ucase ? "SELECT " : "select ");

		if (SQLSetQuantifier.ALL == x.getDistionOption()) {
			print0(ucase ? "ALL " : "all ");
		} else if (SQLSetQuantifier.DISTINCT == x.getDistionOption()) {
			print0(ucase ? "DISTINCT " : "distinct ");
		} else if (SQLSetQuantifier.UNIQUE == x.getDistionOption()) {
			print0(ucase ? "UNIQUE " : "unique ");
		}

		printSelectList(x.getSelectList());

		if (x.getFrom() != null) {
			println();
			print0(ucase ? "FROM " : "from ");
			// accept this x.getFrom().accept(this);
		}

		if (x.getWhere() != null) {
			println();
			print0(ucase ? "WHERE " : "where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
		}

		if (x.getGroupBy() != null) {
			println();
			// accept this x.getGroupBy().accept(this);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSelectItem x) {
		if (x.isConnectByRoot()) {
			print0(ucase ? "CONNECT_BY_ROOT " : "connect_by_root ");
		}
		// accept this x.getExpr().accept(this);

		String alias = x.getAlias();
		if (alias != null && alias.length() > 0) {
			print0(ucase ? " AS " : " as ");
			if (alias.indexOf(' ') == -1 || alias.charAt(0) == '"' || alias.charAt(0) == '\'') {
				print0(alias);
			} else {
				print('"');
				print0(alias);
				print('"');
			}
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLOrderBy x) {
		if (x.getItems().size() > 0) {
			if (x.isSibings()) {
				print0(ucase ? "ORDER SIBLINGS BY " : "order siblings by ");
			} else {
				print0(ucase ? "ORDER BY " : "order by ");
			}

			printAndAccept(x.getItems(), ", ");
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSelectOrderByItem x) {
		// accept this x.getExpr().accept(this);
		if (x.getType() != null) {
			print(' ');
			SQLOrderingSpecification type = x.getType();
			print0(ucase ? type.name : type.name_lcase);
		}

		if (x.getCollate() != null) {
			print0(ucase ? " COLLATE " : " collate ");
			print0(x.getCollate());
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLExprTableSource x) {
		// accept this x.getExpr().accept(this);

		if (x.getAlias() != null) {
			print(' ');
			print0(x.getAlias());
		}

		if (isPrettyFormat() && x.hasAfterComment()) {
			print(' ');
			printComment(x.getAfterCommentsDirect(), "\n");
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSelectStatement stmt) {
		SQLSelect select = stmt.getSelect();

		// accept this select.accept(this);

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLVariantRefExpr x) {
		int index = x.getIndex();

		if (parameters == null || index >= parameters.size()) {
			print0(x.getName());
			return false;
		}

		Object param = parameters.get(index);
		printParameter(param);
		return false;
	}

	public void printParameter(Object param) {
		if (param == null) {
			print0(ucase ? "NULL" : "null");
			return;
		}

		if (param instanceof Number //
				|| param instanceof Boolean) {
			print0(param.toString());
			return;
		}

		if (param instanceof String) {
			SQLCharExpr charExpr = new SQLCharExpr((String) param);
			visit_SQLASTOutputVisitor(charExpr);
			return;
		}

		if (param instanceof Date) {
			print((Date) param);
			return;
		}

		if (param instanceof InputStream) {
			print0("'<InputStream>");
			return;
		}

		if (param instanceof Reader) {
			print0("'<Reader>");
			return;
		}

		if (param instanceof Blob) {
			print0("'<Blob>");
			return;
		}

		if (param instanceof NClob) {
			print0("'<NClob>");
			return;
		}

		if (param instanceof Clob) {
			print0("'<Clob>");
			return;
		}

		print0("'" + param.getClass().getName() + "'");
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropTableStatement x) {
		if (x.isTemporary()) {
			print0(ucase ? "DROP TEMPORARY TABLE " : "drop temporary table ");
		} else {
			print0(ucase ? "DROP TABLE " : "drop table ");
		}

		if (x.isIfExists()) {
			print0(ucase ? "IF EXISTS " : "if exists ");
		}

		printAndAccept(x.getTableSources(), ", ");

		if (x.isCascade()) {
			printCascade();
		}

		if (x.isRestrict()) {
			print0(ucase ? " RESTRICT" : " restrict");
		}

		if (x.isPurge()) {
			print0(ucase ? " PURGE" : " purge");
		}

		return false;
	}

	protected void printCascade() {
		print0(ucase ? " CASCADE" : " cascade");
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropViewStatement x) {
		print0(ucase ? "DROP VIEW " : "drop view ");

		if (x.isIfExists()) {
			print0(ucase ? "IF EXISTS " : "if exists ");
		}

		printAndAccept(x.getTableSources(), ", ");

		if (x.isCascade()) {
			printCascade();
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLTableElement x) {
		if (x instanceof SQLColumnDefinition) {
			return visit_SQLASTOutputVisitor((SQLColumnDefinition) x);
		}

		throw new RuntimeException("TODO");
	}

	public boolean visit_SQLASTOutputVisitor(SQLColumnDefinition x) {
		// accept this x.getName().accept(this);

		if (x.getDataType() != null) {
			print(' ');
			// accept this x.getDataType().accept(this);
		}

		if (x.getDefaultExpr() != null) {
			visitColumnDefault(x);
		}

		for (SQLColumnConstraint item : x.getConstraints()) {
			boolean newLine = item instanceof SQLForeignKeyConstraint //
					|| item instanceof SQLPrimaryKey //
					|| item instanceof SQLColumnCheck //
					|| item instanceof SQLColumnCheck //
					|| item.getName() != null;
			if (newLine) {
				incrementIndent();
				println();
			} else {
				print(' ');
			}

			// accept this item.accept(this);

			if (newLine) {
				decrementIndent();
			}
		}

		if (x.getEnable() != null) {
			if (x.getEnable().booleanValue()) {
				print0(ucase ? " ENABLE" : " enable");
			}
		}

		if (x.getComment() != null) {
			print0(ucase ? " COMMENT " : " comment ");
			// accept this x.getComment().accept(this);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLColumnDefinition.Identity x) {
		print0(ucase ? "IDENTITY (" : "identity (");
		print(x.getSeed());
		print0(", ");
		print(x.getIncrement());
		print(')');
		return false;
	}

	protected void visitColumnDefault(SQLColumnDefinition x) {
		print0(ucase ? " DEFAULT " : " default ");
		// accept this x.getDefaultExpr().accept(this);
	}

	public boolean visit_SQLASTOutputVisitor(SQLDeleteStatement x) {
		print0(ucase ? "DELETE FROM " : "delete from ");

		// accept this x.getTableName().accept(this);

		if (x.getWhere() != null) {
			println();
			print0(ucase ? "WHERE " : "where ");
			incrementIndent();
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
			decrementIndent();
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCurrentOfCursorExpr x) {
		print0(ucase ? "CURRENT OF " : "current of ");
		// accept this x.getCursorName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLInsertStatement x) {
		print0(ucase ? "INSERT INTO " : "insert into ");

		// accept this x.getTableSource().accept(this);

		if (x.getColumns().size() > 0) {
			incrementIndent();
			println();
			print('(');
			for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
				if (i != 0) {
					if (i % 5 == 0) {
						println();
					}
					print0(", ");
				}
				// accept this x.getColumns().get(i).accept(this);
			}
			print(')');
			decrementIndent();
		}

		if (x.getValues() != null) {
			println();
			print0(ucase ? "VALUES" : "values");
			println();
			// accept this x.getValues().accept(this);
		} else {
			if (x.getQuery() != null) {
				println();
				x.getQuery().setParent(x);
				// accept this x.getQuery().accept(this);
			}
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUpdateSetItem x) {
		// accept this x.getColumn().accept(this);
		print0(" = ");
		// accept this x.getValue().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUpdateStatement x) {
		print0(ucase ? "UPDATE " : "update ");

		// accept this x.getTableSource().accept(this);

		println();
		print0(ucase ? "SET " : "set ");
		for (int i = 0, size = x.getItems().size(); i < size; ++i) {
			if (i != 0) {
				print0(", ");
			}
			// accept this x.getItems().get(i).accept(this);
		}

		if (x.getWhere() != null) {
			println();
			incrementIndent();
			print0(ucase ? "WHERE " : "where ");
			x.getWhere().setParent(x);
			// accept this x.getWhere().accept(this);
			decrementIndent();
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCreateTableStatement x) {
		print0(ucase ? "CREATE TABLE " : "create table ");
		if (SQLCreateTableStatement.Type.GLOBAL_TEMPORARY.equals(x.getType())) {
			print0(ucase ? "GLOBAL TEMPORARY " : "global temporary ");
		} else if (SQLCreateTableStatement.Type.LOCAL_TEMPORARY.equals(x.getType())) {
			print0(ucase ? "LOCAL TEMPORARY " : "local temporary ");
		}

		// accept this x.getName().accept(this);

		int size = x.getTableElementList().size();

		if (size > 0) {
			print0(" (");
			incrementIndent();
			println();
			for (int i = 0; i < size; ++i) {
				if (i != 0) {
					print(',');
					println();
				}
				// accept this x.getTableElementList().get(i).accept(this);
			}
			decrementIndent();
			println();
			print(')');
		}

		if (x.getInherits() != null) {
			print0(ucase ? " INHERITS (" : " inherits (");
			// accept this x.getInherits().accept(this);
			print(')');
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUniqueConstraint x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}

		print0(ucase ? "UNIQUE (" : "unique (");
		for (int i = 0, size = x.getColumns().size(); i < size; ++i) {
			if (i != 0) {
				print0(", ");
			}
			// accept this x.getColumns().get(i).accept(this);
		}
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(NotNullConstraint x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "NOT NULL" : "not null");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUnionQuery x) {
		// accept this x.getLeft().accept(this);
		println();
		print0(ucase ? x.getOperator().name : x.getOperator().name_lcase);
		println();

		boolean needParen = false;

		if (x.getOrderBy() != null) {
			needParen = true;
		}

		if (needParen) {
			print('(');
			// accept this x.getRight().accept(this);
			print(')');
		} else {
			// accept this x.getRight().accept(this);
		}

		if (x.getOrderBy() != null) {
			println();
			// accept this x.getOrderBy().accept(this);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUnaryExpr x) {
		print0(x.getOperator().name);

		SQLExpr expr = x.getExpr();

		switch (x.getOperator()) {
		case BINARY:
		case Prior:
		case ConnectByRoot:
			print(' ');
			// accept this expr.accept(this);
			return false;
		default:
			break;
		}

		if (expr instanceof SQLBinaryOpExpr) {
			print('(');
			// accept this expr.accept(this);
			print(')');
		} else if (expr instanceof SQLUnaryExpr) {
			print('(');
			// accept this expr.accept(this);
			print(')');
		} else {
			// accept this expr.accept(this);
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLHexExpr x) {
		print0("0x");
		print0(x.getHex());

		String charset = (String) x.getAttribute("USING");
		if (charset != null) {
			print0(ucase ? " USING " : " using ");
			print0(charset);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSetStatement x) {
		print0(ucase ? "SET " : "set ");
		printAndAccept(x.getItems(), ", ");

		if (x.getHints() != null && x.getHints().size() > 0) {
			print(' ');
			printAndAccept(x.getHints(), " ");
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAssignItem x) {
		// accept this x.getTarget().accept(this);
		print0(" = ");
		// accept this x.getValue().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCallStatement x) {
		if (x.isBrace()) {
			print('{');
		}
		if (x.getOutParameter() != null) {
			// accept this x.getOutParameter().accept(this);
			print0(" = ");
		}

		print0(ucase ? "CALL " : "call ");
		// accept this x.getProcedureName().accept(this);
		print('(');

		printAndAccept(x.getParameters(), ", ");
		print(')');
		if (x.isBrace()) {
			print('}');
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLJoinTableSource x) {
		// accept this x.getLeft().accept(this);
		incrementIndent();

		if (x.getJoinType() == JoinType.COMMA) {
			print(',');
		} else {
			println();
			printJoinType(x.getJoinType());
		}
		print(' ');
		// accept this x.getRight().accept(this);

		if (x.getCondition() != null) {
			incrementIndent();
			print0(ucase ? " ON " : " on ");
			// accept this x.getCondition().accept(this);
			decrementIndent();
		}

		if (x.getUsing().size() > 0) {
			print0(ucase ? " USING (" : " using (");
			printAndAccept(x.getUsing(), ", ");
			print(')');
		}

		if (x.getAlias() != null) {
			print0(ucase ? " AS " : " as ");
			print0(x.getAlias());
		}

		decrementIndent();

		return false;
	}

	protected void printJoinType(JoinType joinType) {
		print0(ucase ? joinType.name : joinType.name_lcase);
	}

	public boolean visit_SQLASTOutputVisitor(ValuesClause x) {
		print('(');
		incrementIndent();
		for (int i = 0, size = x.getValues().size(); i < size; ++i) {
			if (i != 0) {
				if (i % 5 == 0) {
					println();
				}
				print0(", ");
			}

			SQLExpr expr = x.getValues().get(i);
			expr.setParent(x);
			// accept this expr.accept(this);
		}
		decrementIndent();
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSomeExpr x) {
		print0(ucase ? "SOME (" : "some (");

		incrementIndent();
		// accept this x.getSubQuery().accept(this);
		decrementIndent();
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAnyExpr x) {
		print0(ucase ? "ANY (" : "any (");

		incrementIndent();
		// accept this x.getSubQuery().accept(this);
		decrementIndent();
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAllExpr x) {
		print0(ucase ? "ALL (" : "all (");

		incrementIndent();
		// accept this x.getSubQuery().accept(this);
		decrementIndent();
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLInSubQueryExpr x) {
		// accept this x.getExpr().accept(this);
		if (x.isNot()) {
			print0(ucase ? " NOT IN (" : " not in (");
		} else {
			print0(ucase ? " IN (" : " in (");
		}

		incrementIndent();
		// accept this x.getSubQuery().accept(this);
		decrementIndent();
		print(')');

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLListExpr x) {
		print('(');
		printAndAccept(x.getItems(), ", ");
		print(')');

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSubqueryTableSource x) {
		print('(');
		incrementIndent();
		// accept this x.getSelect().accept(this);
		println();
		decrementIndent();
		print(')');

		if (x.getAlias() != null) {
			print(' ');
			print0(x.getAlias());
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLTruncateStatement x) {
		print0(ucase ? "TRUNCATE TABLE " : "truncate table ");
		printAndAccept(x.getTableSources(), ", ");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDefaultExpr x) {
		print0(ucase ? "DEFAULT" : "default");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCommentStatement x) {
		print0(ucase ? "COMMENT ON " : "comment on ");
		if (x.getType() != null) {
			print0(x.getType().name());
			print(' ');
		}
		// accept this x.getOn().accept(this);

		print0(ucase ? " IS " : " is ");
		// accept this x.getComment().accept(this);

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUseStatement x) {
		print0(ucase ? "USE " : "use ");
		// accept this x.getDatabase().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableAddColumn x) {
		print0(ucase ? "ADD (" : "add (");
		printAndAccept(x.getColumns(), ", ");
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDropColumnItem x) {
		print0(ucase ? "DROP COLUMN " : "drop column ");
		this.printAndAccept(x.getColumns(), ", ");

		if (x.isCascade()) {
			print0(ucase ? " CASCADE" : " cascade");
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropIndexStatement x) {
		print0(ucase ? "DROP INDEX " : "drop index ");
		// accept this x.getIndexName().accept(this);

		SQLExprTableSource table = x.getTableName();
		if (table != null) {
			print0(ucase ? " ON " : " on ");
			// accept this table.accept(this);
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSavePointStatement x) {
		print0(ucase ? "SAVEPOINT " : "savepoint ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLReleaseSavePointStatement x) {
		print0(ucase ? "RELEASE SAVEPOINT " : "release savepoint ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLRollbackStatement x) {
		print0(ucase ? "ROLLBACK" : "rollback");
		if (x.getTo() != null) {
			print0(ucase ? " TO " : " to ");
			// accept this x.getTo().accept(this);
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCommentHint x) {
		print0("/*");
		print0(x.getText());
		print0("*/");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCreateDatabaseStatement x) {
		print0(ucase ? "CREATE DATABASE " : "create database ");
		if (x.isIfNotExists()) {
			print0(ucase ? "IF NOT EXISTS " : "if not exists ");
		}
		// accept this x.getName().accept(this);

		if (x.getCharacterSet() != null) {
			print0(ucase ? " CHARACTER SET " : " character set ");
			print0(x.getCharacterSet());
		}

		if (x.getCollate() != null) {
			print0(ucase ? " COLLATE " : " collate ");
			print0(x.getCollate());
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCreateViewStatement x) {
		print0(ucase ? "CREATE " : "create ");
		if (x.isOrReplace()) {
			print0(ucase ? "OR REPLACE " : "or replace ");
		}
		print0(ucase ? "VIEW " : "view ");

		if (x.isIfNotExists()) {
			print0(ucase ? "IF NOT EXISTS " : "if not exists ");
		}

		// accept this x.getName().accept(this);

		if (x.getColumns().size() > 0) {
			println();
			print('(');
			incrementIndent();
			println();
			for (int i = 0; i < x.getColumns().size(); ++i) {
				if (i != 0) {
					print0(", ");
					println();
				}
				// accept this x.getColumns().get(i).accept(this);
			}
			decrementIndent();
			println();
			print(')');
		}

		if (x.getComment() != null) {
			println();
			print0(ucase ? "COMMENT " : "comment ");
			// accept this x.getComment().accept(this);
		}

		println();
		print0(ucase ? "AS" : "as");
		println();

		// accept this x.getSubQuery().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCreateViewStatement.Column x) {
		// accept this x.getExpr().accept(this);

		if (x.getComment() != null) {
			print0(ucase ? " COMMENT " : " comment ");
			// accept this x.getComment().accept(this);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDropIndex x) {
		print0(ucase ? "DROP INDEX " : "drop index ");
		// accept this x.getIndexName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLOver x) {
		print0(ucase ? "OVER (" : "over (");
		if (x.getPartitionBy().size() > 0) {
			print0(ucase ? "PARTITION BY " : "partition by ");
			printAndAccept(x.getPartitionBy(), ", ");
			print(' ');
		}
		if (x.getOrderBy() != null) {
			// accept this x.getOrderBy().accept(this);
		}
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLKeep x) {
		if (x.getDenseRank() == SQLKeep.DenseRank.FIRST) {
			print0(ucase ? "KEEP (DENSE_RANK FIRST " : "keep (dense_rank first ");
		} else {
			print0(ucase ? "KEEP (DENSE_RANK LAST " : "keep (dense_rank last ");
		}

		// accept this x.getOrderBy().accept(this);
		print(')');

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLColumnPrimaryKey x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "PRIMARY KEY" : "primary key");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLColumnUniqueKey x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "UNIQUE" : "unique");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLColumnCheck x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "CHECK (" : "check (");
		// accept this x.getExpr().accept(this);
		print(')');

		if (x.getEnable() != null) {
			if (x.getEnable().booleanValue()) {
				print0(ucase ? " ENABLE" : " enable");
			} else {
				print0(ucase ? " DISABLE" : " disable");
			}
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLWithSubqueryClause x) {
		print0(ucase ? "WITH" : "with");
		if (x.getRecursive() == Boolean.TRUE) {
			print0(ucase ? " RECURSIVE" : " recursive");
		}
		incrementIndent();
		println();
		printlnAndAccept(x.getEntries(), ", ");
		decrementIndent();
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLWithSubqueryClause.Entry x) {
		// accept this x.getName().accept(this);

		if (x.getColumns().size() > 0) {
			print0(" (");
			printAndAccept(x.getColumns(), ", ");
			print(')');
		}
		println();
		print0(ucase ? "AS" : "as");
		println();
		print('(');
		incrementIndent();
		println();
		// accept this x.getSubQuery().accept(this);
		decrementIndent();
		println();
		print(')');

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableAlterColumn x) {
		print0(ucase ? "ALTER COLUMN " : "alter column ");
		// accept this x.getColumn().accept(this);

		if (x.isSetNotNull()) { // postgresql
			print0(ucase ? " SET NOT NULL" : " set not null");
		}
		if (x.isDropNotNull()) { // postgresql
			print0(ucase ? " DROP NOT NULL" : " drop not null");
		}
		if (x.getSetDefault() != null) { // postgresql
			print0(ucase ? " SET DEFAULT " : " set default ");
			// accept this x.getSetDefault().accept(this);
		}
		if (x.isDropDefault()) { // postgresql
			print0(ucase ? " DROP DEFAULT" : " drop default");
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCheck x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "CHECK (" : "check (");
		incrementIndent();
		// accept this x.getExpr().accept(this);
		decrementIndent();
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDropForeignKey x) {
		print0(ucase ? "DROP FOREIGN KEY " : "drop foreign key ");
		// accept this x.getIndexName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDropPrimaryKey x) {
		print0(ucase ? "DROP PRIMARY KEY" : "drop primary key");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDropKey x) {
		print0(ucase ? "DROP KEY " : "drop key ");
		// accept this x.getKeyName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableEnableKeys x) {
		print0(ucase ? "ENABLE KEYS" : "enable keys");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDisableKeys x) {
		print0(ucase ? "DISABLE KEYS" : "disable keys");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDisableConstraint x) {
		print0(ucase ? "DISABLE CONSTRAINT " : "disable constraint ");
		// accept this x.getConstraintName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableEnableConstraint x) {
		print0(ucase ? "ENABLE CONSTRAINT " : "enable constraint ");
		// accept this x.getConstraintName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDropConstraint x) {
		print0(ucase ? "DROP CONSTRAINT " : "drop constraint ");
		// accept this x.getConstraintName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableStatement x) {
		print0(ucase ? "ALTER TABLE " : "alter table ");
		// accept this x.getName().accept(this);
		incrementIndent();
		for (int i = 0; i < x.getItems().size(); ++i) {
			SQLAlterTableItem item = x.getItems().get(i);
			if (i != 0) {
				print(',');
			}
			println();
			// accept this item.accept(this);
		}
		decrementIndent();
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLExprHint x) {
		// accept this x.getExpr().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCreateIndexStatement x) {
		print0(ucase ? "CREATE " : "create ");
		if (x.getType() != null) {
			print0(x.getType());
			print(' ');
		}

		print0(ucase ? "INDEX " : "index ");

		// accept this x.getName().accept(this);
		print0(ucase ? " ON " : " on ");
		// accept this x.getTable().accept(this);
		print0(" (");
		printAndAccept(x.getItems(), ", ");
		print(')');

		// for mysql
		if (x.getUsing() != null) {
			print0(ucase ? " USING " : " using ");
			;
			print0(x.getUsing());
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUnique x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "UNIQUE (" : "unique (");
		printAndAccept(x.getColumns(), ", ");
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLPrimaryKeyImpl x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "PRIMARY KEY (" : "primary key (");
		printAndAccept(x.getColumns(), ", ");
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableRenameColumn x) {
		print0(ucase ? "RENAME COLUMN " : "rename column ");
		// accept this x.getColumn().accept(this);
		print0(ucase ? " TO " : " to ");
		// accept this x.getTo().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLColumnReference x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}
		print0(ucase ? "REFERENCES " : "references ");
		// accept this x.getTable().accept(this);
		print0(" (");
		printAndAccept(x.getColumns(), ", ");
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLForeignKeyImpl x) {
		if (x.getName() != null) {
			print0(ucase ? "CONSTRAINT " : "constraint ");
			// accept this x.getName().accept(this);
			print(' ');
		}

		print0(ucase ? "FOREIGN KEY (" : "foreign key (");
		printAndAccept(x.getReferencingColumns(), ", ");
		print(')');

		print0(ucase ? " REFERENCES " : " references ");
		// accept this x.getReferencedTableName().accept(this);

		if (x.getReferencedColumns().size() > 0) {
			print0(" (");
			printAndAccept(x.getReferencedColumns(), ", ");
			print(')');
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropSequenceStatement x) {
		print0(ucase ? "DROP SEQUENCE " : "drop sequence ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropTriggerStatement x) {
		print0(ucase ? "DROP TRIGGER " : "drop trigger ");
		// accept this x.getName().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropUserStatement x) {
		print0(ucase ? "DROP USER " : "drop user ");
		printAndAccept(x.getUsers(), ", ");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLExplainStatement x) {
		print0(ucase ? "EXPLAIN" : "explain");
		if (x.getHints() != null && x.getHints().size() > 0) {
			print(' ');
			printAndAccept(x.getHints(), " ");
		}
		println();
		// accept this x.getStatement().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLGrantStatement x) {
		print0(ucase ? "GRANT " : "grant ");
		printAndAccept(x.getPrivileges(), ", ");

		printGrantOn(x);

		if (x.getTo() != null) {
			print0(ucase ? " TO " : " to ");
			// accept this x.getTo().accept(this);
		}

		boolean with = false;
		if (x.getMaxQueriesPerHour() != null) {
			if (!with) {
				print0(ucase ? " WITH" : " with");
				with = true;
			}
			print0(ucase ? " MAX_QUERIES_PER_HOUR " : " max_queries_per_hour ");
			// accept this x.getMaxQueriesPerHour().accept(this);
		}

		if (x.getMaxUpdatesPerHour() != null) {
			if (!with) {
				print0(ucase ? " WITH" : " with");
				with = true;
			}
			print0(ucase ? " MAX_UPDATES_PER_HOUR " : " max_updates_per_hour ");
			// accept this x.getMaxUpdatesPerHour().accept(this);
		}

		if (x.getMaxConnectionsPerHour() != null) {
			if (!with) {
				print0(ucase ? " WITH" : " with");
				with = true;
			}
			print0(ucase ? " MAX_CONNECTIONS_PER_HOUR " : " max_connections_per_hour ");
			// accept this x.getMaxConnectionsPerHour().accept(this);
		}

		if (x.getMaxUserConnections() != null) {
			if (!with) {
				print0(ucase ? " WITH" : " with");
				with = true;
			}
			print0(ucase ? " MAX_USER_CONNECTIONS " : " max_user_connections ");
			// accept this x.getMaxUserConnections().accept(this);
		}

		if (x.isAdminOption()) {
			if (!with) {
				print0(ucase ? " WITH" : " with");
				with = true;
			}
			print0(ucase ? " ADMIN OPTION" : " admin option");
		}

		if (x.getIdentifiedBy() != null) {
			print0(ucase ? " IDENTIFIED BY " : " identified by ");
			// accept this x.getIdentifiedBy().accept(this);
		}

		return false;
	}

	protected void printGrantOn(SQLGrantStatement x) {
		if (x.getOn() != null) {
			print0(ucase ? " ON " : " on ");

			SQLObjectType objectType = x.getObjectType();
			if (objectType != null) {
				print0(ucase ? objectType.name : objectType.name_lcase);
				print(' ');
			}

			// accept this x.getOn().accept(this);
		}
	}

	public boolean visit_SQLASTOutputVisitor(SQLRevokeStatement x) {
		print0(ucase ? "ROVOKE " : "rovoke ");
		printAndAccept(x.getPrivileges(), ", ");

		if (x.getOn() != null) {
			print0(ucase ? " ON " : " on ");

			if (x.getObjectType() != null) {
				print0(x.getObjectType().name());
				print(' ');
			}

			// accept this x.getOn().accept(this);
		}

		if (x.getFrom() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getFrom().accept(this);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropDatabaseStatement x) {
		print0(ucase ? "DROP DATABASE " : "drop databasE ");

		if (x.isIfExists()) {
			print0(ucase ? "IF EXISTS " : "if exists ");
		}

		// accept this x.getDatabase().accept(this);

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropFunctionStatement x) {
		print0(ucase ? "DROP FUNCTION " : "drop function ");

		if (x.isIfExists()) {
			print0(ucase ? "IF EXISTS " : "if exists ");
		}

		// accept this x.getName().accept(this);

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropTableSpaceStatement x) {
		print0(ucase ? "DROP TABLESPACE " : "drop tablespace ");

		if (x.isIfExists()) {
			print0(ucase ? "IF EXISTS " : "if exists ");
		}

		// accept this x.getName().accept(this);

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDropProcedureStatement x) {
		print0(ucase ? "DROP PROCEDURE " : "drop procedure ");

		if (x.isIfExists()) {
			print0(ucase ? "IF EXISTS " : "if exists ");
		}

		// accept this x.getName().accept(this);

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableAddIndex x) {
		print0(ucase ? "ADD " : "add ");
		if (x.getType() != null) {
			print0(x.getType());
			print(' ');
		}

		if (x.isUnique()) {
			print0(ucase ? "UNIQUE " : "unique ");
		}

		if (x.isKey()) {
			print0(ucase ? "KEY " : "key ");
		} else {
			print0(ucase ? "INDEX " : "index ");
		}

		if (x.getName() != null) {
			// accept this x.getName().accept(this);
			print(' ');
		}
		print('(');
		printAndAccept(x.getItems(), ", ");
		print(')');

		if (x.getUsing() != null) {
			print0(ucase ? " USING " : " using ");
			print0(x.getUsing());
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableAddConstraint x) {
		if (x.isWithNoCheck()) {
			print0(ucase ? "WITH NOCHECK " : "with nocheck ");
		}

		print0(ucase ? "ADD " : "add ");

		// accept this x.getConstraint().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCreateTriggerStatement x) {
		print0(ucase ? "CREATE " : "create ");

		if (x.isOrReplace()) {
			print0(ucase ? "OR REPLEACE " : "or repleace ");
		}

		print0(ucase ? "TRIGGER " : "trigger ");

		// accept this x.getName().accept(this);

		incrementIndent();
		println();
		if (TriggerType.INSTEAD_OF.equals(x.getTriggerType())) {
			print0(ucase ? "INSTEAD OF" : "instead of");
		} else {
			String triggerTypeName = x.getTriggerType().name();
			print0(ucase ? triggerTypeName : triggerTypeName.toLowerCase());
		}

		for (TriggerEvent event : x.getTriggerEvents()) {
			print(' ');
			print0(event.name());
		}
		println();
		print0(ucase ? "ON " : "on ");
		// accept this x.getOn().accept(this);

		if (x.isForEachRow()) {
			println();
			print0(ucase ? "FOR EACH ROW" : "for each row");
		}
		decrementIndent();
		println();
		// accept this x.getBody().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLBooleanExpr x) {
		print0(x.getValue() ? "true" : "false");

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLUnionQueryTableSource x) {
		print('(');
		incrementIndent();
		println();
		// accept this x.getUnion().accept(this);
		decrementIndent();
		println();
		print(')');

		if (x.getAlias() != null) {
			print(' ');
			print0(x.getAlias());
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLTimestampExpr x) {
		print0(ucase ? "TIMESTAMP " : "timestamp ");

		if (x.isWithTimeZone()) {
			print0(ucase ? " WITH TIME ZONE " : " with time zone ");
		}

		print('\'');
		print0(x.getLiteral());
		print('\'');

		if (x.getTimeZone() != null) {
			print0(ucase ? " AT TIME ZONE '" : " at time zone '");
			print0(x.getTimeZone());
			print('\'');
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLBinaryExpr x) {
		print0("b'");
		print0(x.getValue());
		print('\'');

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableRename x) {
		print0(ucase ? "RENAME TO " : "rename to ");
		// accept this x.getTo().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLShowTablesStatement x) {
		print0(ucase ? "SHOW TABLES" : "show tables");
		if (x.getDatabase() != null) {
			print0(ucase ? " FROM " : " from ");
			// accept this x.getDatabase().accept(this);
		}

		if (x.getLike() != null) {
			print0(ucase ? " LIKE " : " like ");
			// accept this x.getLike().accept(this);
		}
		return false;
	}

	protected void printComment(List<String> comments, String seperator) {
		if (comments != null) {
			for (int i = 0; i < comments.size(); ++i) {
				if (i != 0) {
					print0(seperator);
				}
				String comment = comments.get(i);
				print0(comment);
			}
		}
	}

	protected void printlnComments(List<String> comments) {
		if (comments != null) {
			for (int i = 0; i < comments.size(); ++i) {
				String comment = comments.get(i);
				print0(comment);
				println();
			}
		}
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterViewRenameStatement x) {
		print0(ucase ? "ALTER VIEW " : "alter view ");
		// accept this x.getName().accept(this);
		print0(ucase ? " RENAME TO " : " rename to ");
		// accept this x.getTo().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableAddPartition x) {
		print0(ucase ? "ADD " : "add ");
		if (x.isIfNotExists()) {
			print0(ucase ? "IF NOT EXISTS " : "if not exists ");
		}

		if (x.getPartitionCount() != null) {
			print0(ucase ? "PARTITION PARTITIONS " : "partition partitions ");
			// accept this x.getPartitionCount().accept(this);
		}

		if (x.getPartitions().size() > 0) {
			print0(ucase ? "PARTITION (" : "partition (");
			printAndAccept(x.getPartitions(), ", ");
			print(')');
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableReOrganizePartition x) {
		print0(ucase ? "REORGANIZE " : "reorganize ");

		printAndAccept(x.getNames(), ", ");

		print0(ucase ? " INTO (" : " into (");
		printAndAccept(x.getPartitions(), ", ");
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDropPartition x) {
		print0(ucase ? "DROP " : "drop ");
		if (x.isIfNotExists()) {
			print0(ucase ? "IF NOT EXISTS " : "if not exists ");
		}
		print0(ucase ? "PARTITION " : "partition ");

		if (x.getPartitions().size() == 1 && x.getPartitions().get(0) instanceof SQLName) {
			// accept this x.getPartitions().get(0).accept(this);
		} else {
			print('(');
			printAndAccept(x.getPartitions(), ", ");
			print(')');
		}

		if (x.isPurge()) {
			print0(ucase ? " PURGE" : " purge");
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableRenamePartition x) {
		print0(ucase ? "PARTITION (" : "partition (");
		printAndAccept(x.getPartition(), ", ");
		print0(ucase ? ") RENAME TO PARTITION(" : ") rename to partition(");
		printAndAccept(x.getTo(), ", ");
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableSetComment x) {
		print0(ucase ? "SET COMMENT " : "set comment ");
		// accept this x.getComment().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableSetLifecycle x) {
		print0(ucase ? "SET LIFECYCLE " : "set lifecycle ");
		// accept this x.getLifecycle().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableEnableLifecycle x) {
		if (x.getPartition().size() != 0) {
			print0(ucase ? "PARTITION (" : "partition (");
			printAndAccept(x.getPartition(), ", ");
			print0(") ");
		}

		print0(ucase ? "ENABLE LIFECYCLE" : "enable lifecycle");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDisableLifecycle x) {
		if (x.getPartition().size() != 0) {
			print0(ucase ? "PARTITION (" : "partition (");
			printAndAccept(x.getPartition(), ", ");
			print0(") ");
		}

		print0(ucase ? "DISABLE LIFECYCLE" : "disable lifecycle");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableTouch x) {
		print0(ucase ? "TOUCH" : "touch");
		if (x.getPartition().size() != 0) {
			print0(ucase ? " PARTITION (" : " partition (");
			printAndAccept(x.getPartition(), ", ");
			print(')');
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLArrayExpr x) {
		// accept this x.getExpr().accept(this);
		print('[');
		printAndAccept(x.getValues(), ", ");
		print(']');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLOpenStatement x) {
		print0(ucase ? "OPEN " : "open ");
		print0(x.getCursorName());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLFetchStatement x) {
		print0(ucase ? "FETCH " : "fetch ");
		// accept this x.getCursorName().accept(this);
		print0(ucase ? " INTO " : " into ");
		printAndAccept(x.getInto(), ", ");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLCloseStatement x) {
		print0(ucase ? "CLOSE " : "close ");
		print0(x.getCursorName());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLGroupingSetExpr x) {
		print0(ucase ? "GROUPING SETS" : "grouping sets");
		print0(" (");
		printAndAccept(x.getParameters(), ", ");
		print(')');
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLIfStatement x) {
		print0(ucase ? "IF " : "if ");
		// accept this x.getCondition().accept(this);
		incrementIndent();
		println();
		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}
		decrementIndent();

		for (SQLIfStatement.ElseIf elseIf : x.getElseIfList()) {
			println();
			// accept this elseIf.accept(this);
		}

		if (x.getElseItem() != null) {
			println();
			// accept this x.getElseItem().accept(this);
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLIfStatement.Else x) {
		print0(ucase ? "ELSE" : "else");
		incrementIndent();
		println();

		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			if (i != 0) {
				println();
			}
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
		}

		decrementIndent();
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLIfStatement.ElseIf x) {
		print0(ucase ? "ELSE IF" : "else if");
		// accept this x.getCondition().accept(this);
		print0(ucase ? " THEN" : " then");
		incrementIndent();
		println();

		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			if (i != 0) {
				println();
			}
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
		}

		decrementIndent();
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLLoopStatement x) {
		print0(ucase ? "LOOP" : "loop");
		incrementIndent();
		println();

		for (int i = 0, size = x.getStatements().size(); i < size; ++i) {
			SQLStatement item = x.getStatements().get(i);
			item.setParent(x);
			// accept this item.accept(this);
			if (i != size - 1) {
				println();
			}
		}

		decrementIndent();
		println();
		print0(ucase ? "END LOOP" : "end loop");
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLParameter x) {
		if (x.getDataType().getName().equalsIgnoreCase("CURSOR")) {
			print0(ucase ? "CURSOR " : "cursor ");
			// accept this x.getName().accept(this);
			print0(ucase ? " IS" : " is");
			incrementIndent();
			println();
			SQLSelect select = ((SQLQueryExpr) x.getDefaultValue()).getSubQuery();
			// accept this select.accept(this);
			decrementIndent();

		} else {

			if (x.getParamType() == SQLParameter.ParameterType.IN) {
				print0(ucase ? "IN " : "in ");
			} else if (x.getParamType() == SQLParameter.ParameterType.OUT) {
				print0(ucase ? "OUT " : "out ");
			} else if (x.getParamType() == SQLParameter.ParameterType.INOUT) {
				print0(ucase ? "INOUT " : "inout ");
			}
			// accept this x.getName().accept(this);
			print(' ');

			// accept this x.getDataType().accept(this);

			if (x.getDefaultValue() != null) {
				print0(" := ");
				// accept this x.getDefaultValue().accept(this);
			}
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLDeclareItem x) {
		// accept this x.getName().accept(this);

		if (x.getType() == SQLDeclareItem.Type.TABLE) {
			print0(ucase ? " TABLE" : " table");
			int size = x.getTableElementList().size();

			if (size > 0) {
				print0(" (");
				incrementIndent();
				println();
				for (int i = 0; i < size; ++i) {
					if (i != 0) {
						print(',');
						println();
					}
					// accept this x.getTableElementList().get(i).accept(this);
				}
				decrementIndent();
				println();
				print(')');
			}
		} else if (x.getType() == SQLDeclareItem.Type.CURSOR) {
			print0(ucase ? " CURSOR" : " cursor");
		} else {
			if (x.getDataType() != null) {
				print(' ');
				// accept this x.getDataType().accept(this);
			}
			if (x.getValue() != null) {
				if (JdbcConstants.MYSQL.equals(getDbType())) {
					print0(ucase ? " DEFAULT " : " default ");
				} else {
					print0(" = ");
				}
				// accept this x.getValue().accept(this);
			}
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLPartitionValue x) {
		if (x.getOperator() == SQLPartitionValue.Operator.LessThan //
				&& (!JdbcConstants.ORACLE.equals(getDbType())) && x.getItems().size() == 1 //
				&& x.getItems().get(0) instanceof SQLIdentifierExpr) {
			SQLIdentifierExpr ident = (SQLIdentifierExpr) x.getItems().get(0);
			if ("MAXVALUE".equalsIgnoreCase(ident.getName())) {
				print0(ucase ? "VALUES LESS THAN MAXVALUE" : "values less than maxvalue");
				return false;
			}
		}

		if (x.getOperator() == SQLPartitionValue.Operator.LessThan) {
			print0(ucase ? "VALUES LESS THAN (" : "values less than (");
		} else if (x.getOperator() == SQLPartitionValue.Operator.In) {
			print0(ucase ? "VALUES IN (" : "values in (");
		} else {
			print(ucase ? "VALUES (" : "values (");
		}
		printAndAccept(x.getItems(), ", ");
		print(')');
		return false;
	}

	public String getDbType() {
		return dbType;
	}

	public boolean isUppCase() {
		return ucase;
	}

	public void setUppCase(boolean val) {
		this.ucase = val;
	}

	public boolean visit_SQLASTOutputVisitor(SQLPartition x) {
		print0(ucase ? "PARTITION " : "partition ");
		// accept this x.getName().accept(this);
		if (x.getValues() != null) {
			print(' ');
			// accept this x.getValues().accept(this);
		}

		if (x.getDataDirectory() != null) {
			incrementIndent();
			println();
			print0(ucase ? "DATA DIRECTORY " : "data directory ");
			// accept this x.getDataDirectory().accept(this);
			decrementIndent();
		}

		if (x.getIndexDirectory() != null) {
			incrementIndent();
			println();
			print0(ucase ? "INDEX DIRECTORY " : "index directory ");
			// accept this x.getIndexDirectory().accept(this);
			decrementIndent();
		}

		if (x.getTableSpace() != null) {
			print0(ucase ? " TABLESPACE " : " tablespace ");
			// accept this x.getTableSpace().accept(this);
		}

		if (x.getEngine() != null) {
			print0(ucase ? " STORAGE ENGINE " : " storage engine ");
			// accept this x.getEngine().accept(this);
		}

		if (x.getMaxRows() != null) {
			print0(ucase ? " MAX_ROWS " : " max_rows ");
			// accept this x.getMaxRows().accept(this);
		}

		if (x.getMinRows() != null) {
			print0(ucase ? " MIN_ROWS " : " min_rows ");
			// accept this x.getMinRows().accept(this);
		}

		if (x.getComment() != null) {
			print0(ucase ? " COMMENT " : " comment ");
			// accept this x.getComment().accept(this);
		}

		if (x.getSubPartitionsCount() != null) {
			incrementIndent();
			println();
			print0(ucase ? "SUBPARTITIONS " : "subpartitions ");
			// accept this x.getSubPartitionsCount().accept(this);
			decrementIndent();
		}

		if (x.getSubPartitions().size() > 0) {
			println();
			print('(');
			incrementIndent();
			for (int i = 0; i < x.getSubPartitions().size(); ++i) {
				if (i != 0) {
					print(',');
				}
				println();
				// accept this x.getSubPartitions().get(i).accept(this);
			}
			decrementIndent();
			println();
			print(')');
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLPartitionByRange x) {
		print0(ucase ? "PARTITION BY RANGE" : "partition by range");
		if (x.getExpr() != null) {
			print0(" (");
			// accept this x.getExpr().accept(this);
			print(')');
		} else {
			if (JdbcConstants.MYSQL.equals(getDbType())) {
				print0(ucase ? " COLUMNS (" : " columns (");
			} else {
				print0(" (");
			}
			printAndAccept(x.getColumns(), ", ");
			print(')');
		}

		if (x.getInterval() != null) {
			print0(ucase ? " INTERVAL " : " interval ");
			// accept this x.getInterval().accept(this);
		}

		printPartitionsCountAndSubPartitions(x);

		println();
		print('(');
		incrementIndent();
		for (int i = 0, size = x.getPartitions().size(); i < size; ++i) {
			if (i != 0) {
				print(',');
			}
			println();
			// accept this x.getPartitions().get(i).accept(this);
		}
		decrementIndent();
		println();
		print(')');

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLPartitionByList x) {
		print0(ucase ? "PARTITION BY LIST " : "partition by list ");
		if (x.getExpr() != null) {
			print('(');
			// accept this x.getExpr().accept(this);
			print0(")");
		} else {
			print0(ucase ? "COLUMNS (" : "columns (");
			printAndAccept(x.getColumns(), ", ");
			print0(")");
		}

		printPartitionsCountAndSubPartitions(x);

		List<SQLPartition> partitions = x.getPartitions();
		int partitionsSize = partitions.size();
		if (partitionsSize > 0) {
			println();
			incrementIndent();
			print('(');
			for (int i = 0; i < partitionsSize; ++i) {
				println();
				// accept this partitions.get(i).accept(this);
				if (i != partitionsSize - 1) {
					print0(", ");
				}
			}
			decrementIndent();
			println();
			print(')');
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLPartitionByHash x) {
		if (x.isLinear()) {
			print0(ucase ? "PARTITION BY LINEAR HASH " : "partition by linear hash ");
		} else {
			print0(ucase ? "PARTITION BY HASH " : "partition by hash ");
		}

		if (x.isKey()) {
			print0(ucase ? "KEY" : "key");
		}

		print('(');
		// accept this x.getExpr().accept(this);
		print(')');

		printPartitionsCountAndSubPartitions(x);

		return false;
	}

	protected void printPartitionsCountAndSubPartitions(SQLPartitionBy x) {
		if (x.getPartitionsCount() != null) {

			if (Boolean.TRUE.equals(x.getAttribute("ads.partition"))) {
				print0(ucase ? " PARTITION NUM " : " partition num ");
			} else {
				print0(ucase ? " PARTITIONS " : " partitions ");
			}

			// accept this x.getPartitionsCount().accept(this);
		}

		if (x.getSubPartitionBy() != null) {
			println();
			// accept this x.getSubPartitionBy().accept(this);
		}

		if (x.getStoreIn().size() > 0) {
			println();
			print0(ucase ? "STORE IN (" : "store in (");
			printAndAccept(x.getStoreIn(), ", ");
			print(')');
		}
	}

	public boolean visit_SQLASTOutputVisitor(SQLSubPartitionByHash x) {
		if (x.isLinear()) {
			print0(ucase ? "SUBPARTITION BY LINEAR HASH " : "subpartition by linear hash ");
		} else {
			print0(ucase ? "SUBPARTITION BY HASH " : "subpartition by hash ");
		}

		if (x.isKey()) {
			print0(ucase ? "KEY" : "key");
		}

		print('(');
		// accept this x.getExpr().accept(this);
		print(')');

		if (x.getSubPartitionsCount() != null) {
			print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
			// accept this x.getSubPartitionsCount().accept(this);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSubPartitionByList x) {
		if (x.isLinear()) {
			print0(ucase ? "SUBPARTITION BY LINEAR HASH " : "subpartition by linear hash ");
		} else {
			print0(ucase ? "SUBPARTITION BY HASH " : "subpartition by hash ");
		}

		print('(');
		// accept this x.getColumn().accept(this);
		print(')');

		if (x.getSubPartitionsCount() != null) {
			print0(ucase ? " SUBPARTITIONS " : " subpartitions ");
			// accept this x.getSubPartitionsCount().accept(this);
		}

		if (x.getSubPartitionTemplate().size() > 0) {
			incrementIndent();
			println();
			print0(ucase ? "SUBPARTITION TEMPLATE (" : "subpartition template (");
			incrementIndent();
			println();
			printlnAndAccept(x.getSubPartitionTemplate(), ",");
			decrementIndent();
			println();
			print(')');
			decrementIndent();
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLSubPartition x) {
		print0(ucase ? "SUBPARTITION " : "subpartition ");
		// accept this x.getName().accept(this);

		if (x.getValues() != null) {
			print(' ');
			// accept this x.getValues().accept(this);
		}

		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterDatabaseStatement x) {
		print0(ucase ? "ALTER DATABASE " : "alter database ");
		// accept this x.getName().accept(this);
		if (x.isUpgradeDataDirectoryName()) {
			print0(ucase ? " UPGRADE DATA DIRECTORY NAME" : " upgrade data directory name");
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableConvertCharSet x) {
		print0(ucase ? "CONVERT TO CHARACTER SET " : "convert to character set ");
		// accept this x.getCharset().accept(this);

		if (x.getCollate() != null) {
			print0(ucase ? "COLLATE " : "collate ");
			// accept this x.getCollate().accept(this);
		}
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableCoalescePartition x) {
		print0(ucase ? "COALESCE PARTITION " : "coalesce partition ");
		// accept this x.getCount().accept(this);
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableTruncatePartition x) {
		print0(ucase ? "TRUNCATE PARTITION " : "truncate partition ");
		printPartitions(x.getPartitions());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableDiscardPartition x) {
		print0(ucase ? "DISCARD PARTITION " : "discard partition ");
		printPartitions(x.getPartitions());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableImportPartition x) {
		print0(ucase ? "IMPORT PARTITION " : "import partition ");
		printPartitions(x.getPartitions());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableAnalyzePartition x) {
		print0(ucase ? "ANALYZE PARTITION " : "analyze partition ");

		printPartitions(x.getPartitions());
		return false;
	}

	protected void printPartitions(List<SQLName> partitions) {
		if (partitions.size() == 1 && "ALL".equalsIgnoreCase(partitions.get(0).getSimpleName())) {
			print0(ucase ? "ALL" : "all");
		} else {
			printAndAccept(partitions, ", ");
		}
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableCheckPartition x) {
		print0(ucase ? "CHECK PARTITION " : "check partition ");
		printPartitions(x.getPartitions());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableOptimizePartition x) {
		print0(ucase ? "OPTIMIZE PARTITION " : "optimize partition ");
		printPartitions(x.getPartitions());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableRebuildPartition x) {
		print0(ucase ? "REBUILD PARTITION " : "rebuild partition ");
		printPartitions(x.getPartitions());
		return false;
	}

	public boolean visit_SQLASTOutputVisitor(SQLAlterTableRepairPartition x) {
		print0(ucase ? "REPAIR PARTITION " : "repair partition ");
		printPartitions(x.getPartitions());
		return false;
	}

	/*
	 * ======== SQLASTVisitorAdapter
	 */

	public void postVisit_SQLASTVisitorAdapter(SQLObject astNode) {
	}

	public void preVisit_SQLASTVisitorAdapter(SQLObject astNode) {
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAllColumnExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLBetweenExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLBinaryOpExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCaseExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCaseExpr.Item x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCastExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCharExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLExistsExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLIdentifierExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLInListExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLIntegerExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLNCharExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLNotExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLNullExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLNumberExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLPropertyExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSelectGroupByClause x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSelectItem x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSelectStatement astNode) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAggregateExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLVariantRefExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLQueryExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSelect x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSelectQueryBlock x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLExprTableSource x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLOrderBy x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSelectOrderByItem x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropTableStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCreateTableStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLColumnDefinition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLColumnDefinition.Identity x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDataType x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDeleteStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCurrentOfCursorExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLInsertStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLUpdateSetItem x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLUpdateStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCreateViewStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCreateViewStatement.Column x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(NotNullConstraint x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLMethodInvokeExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLUnionQuery x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLUnaryExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLHexExpr x) {
		return false;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSetStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAssignItem x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCallStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLJoinTableSource x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(ValuesClause x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSomeExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAnyExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAllExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLInSubQueryExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLListExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSubqueryTableSource x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLTruncateStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDefaultExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCommentStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLUseStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableAddColumn x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDropColumnItem x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropIndexStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropViewStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSavePointStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLRollbackStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLReleaseSavePointStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCommentHint x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCreateDatabaseStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDropIndex x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLOver x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLKeep x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLColumnPrimaryKey x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLColumnUniqueKey x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLWithSubqueryClause x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLWithSubqueryClause.Entry x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCharacterDataType x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableAlterColumn x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCheck x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDropForeignKey x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDropPrimaryKey x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDisableKeys x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableEnableKeys x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDisableConstraint x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableEnableConstraint x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLColumnCheck x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLExprHint x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDropConstraint x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLUnique x) {
		for (SQLExpr column : x.getColumns()) {
			// accept this column.accept(this);
		}
		return false;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCreateIndexStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLPrimaryKeyImpl x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableRenameColumn x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLColumnReference x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLForeignKeyImpl x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropSequenceStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropTriggerStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropUserStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLExplainStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLGrantStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropDatabaseStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableAddIndex x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableAddConstraint x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCreateTriggerStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropFunctionStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropTableSpaceStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDropProcedureStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLBooleanExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLUnionQueryTableSource x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLTimestampExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLRevokeStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLBinaryExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableRename x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterViewRenameStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLShowTablesStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableAddPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDropPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableRenamePartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableSetComment x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableSetLifecycle x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableEnableLifecycle x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDisableLifecycle x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableTouch x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLArrayExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLOpenStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLFetchStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCloseStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLGroupingSetExpr x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLIfStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLIfStatement.Else x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLIfStatement.ElseIf x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLLoopStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLParameter x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLCreateProcedureStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLBlockStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDropKey x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLDeclareItem x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLPartitionValue x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLPartitionByRange x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLPartitionByHash x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLPartitionByList x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSubPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSubPartitionByHash x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLSubPartitionByList x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterDatabaseStatement x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableConvertCharSet x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableReOrganizePartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableCoalescePartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableTruncatePartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableDiscardPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableImportPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableAnalyzePartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableCheckPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableOptimizePartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableRebuildPartition x) {
		return true;
	}

	public boolean visit_SQLASTVisitorAdapter(SQLAlterTableRepairPartition x) {
		return true;
	}

}
