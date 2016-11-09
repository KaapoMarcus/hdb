package com.jhh.hdb.proxyserver.qssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.netease.backend.db.DBConnection;
import com.netease.backend.db.common.management.Cluster;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.Policy;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.sql.DDLParser;
import com.netease.backend.db.common.sql.SShowCreateTable;
import com.netease.cli.StringTable;
import com.netease.ddb.exec.plugin.Dbi;
import com.netease.exec.Command;
import com.netease.exec.Executor;
import com.netease.exec.Plugin;
import com.netease.exec.option.Option;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * 查询服务器的Isql命令处理插件
 * 
 *
 */
class QSPlugin extends Plugin {
	
	//连接上下文信息
	SessionContext sessionContext;
	
	/** 支持的命令列表 */
	private Command cShowTables;
	private Command cShowTablesForPolicy;
	private Command cShowTablesForDbn;
	private Command cShowViews;
	private Command cShowCreateTable;
	private Command cShowIndexForTable;
	
	private Command cShowDbns;
	private Command cShowDbnsForPolicy;
	private Command cShowDbnsForTable;
	private Command cShowPolicies;
	
	private Command cShowDdb;
	private Command cShowCurrentDdb;

	private Command cShowTriggers;
	private Command cShowProcedures;
	
	private Command cDesc;
	private Command cDescPolicy;


	/**
	 * 构造函数
	 * @param context
	 * @param executor
	 */
	public QSPlugin(SessionContext context, Executor executor) {
		super("qs", "isql cmd plugin for qs", executor);
		this.sessionContext = context;
		
		cShowTables = new QSCommand(this, "show tables", "Show all tables in the system.", "SHOW TABLES", "TODO");
		cShowTablesForPolicy = new QSCommand(this, "show tables for policy", "Show tables for the specified policy.", "SHOW TABLES FOR POLICY policy_name", "TODO");
		cShowTablesForDbn = new QSCommand(this, "show tables for dbn", "Show tables which use the specified database node.", "SHOW TABLES FOR DBN name", "TODO");
		cShowViews = new QSCommand(this, "show views", "Show all views in the system.", "SHOW VIEWS", "TODO");
		cShowCreateTable = new QSCommand(this, "show create table", "Show definition of a table", "SHOW CREATE TABLE table", "TODO");
		cShowIndexForTable = new QSCommand(this, "show index for", "Show indexes for one table.", "SHOW INDEX FOR tbl_name", "TODO");
		cShowDbns = new QSCommand(this, "show dbns", "Show all database nodes in the system.", "SHOW DBNS", "TODO");
		cShowDbnsForPolicy = new QSCommand(this, "show dbns for policy", "Show dbns for the specified policy.", "SHOW DBNS FOR POLICY policy_name", "TODO");
		cShowDbnsForTable = new QSCommand(this, "show dbns for table", "Show dbns which have the specified table.", "SHOW DBNS FOR TABLE table_name", "TODO");
		cShowPolicies = new QSCommand(this, "show policies", "Show all policies in the system.", "SHOW POLICIES", "TODO");
		cShowDdb = new QSCommand(this, "show ddbs", "Show ddb informations in system.", "SHOW DDBS", "TODO");
		cShowCurrentDdb = new QSCommand(this, "show current ddb", "Show current ddb information.", "SHOW CURRENT DDB", "TODO");
		cShowTriggers = new QSCommand(this, "show triggers", "Show all triggers or specified triggers.", "SHOW TRIGGERS [LIKE tbl_name] | [FOR trigger_name]", "TODO");
		cShowProcedures = new QSCommand(this, "show procedures", "Show all procedures or specified procedures.", "SHOW PROCEDURES [FOR sp_name]", "TODO");
		cDesc = new QSCommand(this, "desc", "Describe a table.", "DESC name", "TODO");
		cDescPolicy = new QSCommand(this, "desc policy", "Describe a policy.", "DESC POLICY name", "TODO");
	}

	@Override
	public Collection<Command> getCommands() {
		List<Command> qsCommands = Arrays.asList(new Command[] { 
				cShowTables,
				cShowTablesForPolicy,
				cShowTablesForDbn,
				cShowViews,
				cShowCreateTable,
				cShowIndexForTable,
				cShowDbns,
				cShowDbnsForPolicy,
				cShowDbnsForTable,
				cShowPolicies,
				cShowDdb,
				cShowCurrentDdb,
				cShowTriggers,
				cShowProcedures,
				cDesc,
				cDescPolicy});
		return qsCommands;
	}
	
	@Override
	public Collection<String> showAutoCompletes(Command commandObject, String args) {
		return Collections.emptyList();
	}
	
	@Override
	public Collection<Option> getOptions() {
		return Collections.emptyList();
	}
	
	protected class QSCommand extends Command {

		public QSCommand(Plugin plugin, String idString, String description,
				String syntax, String help) {
			super(plugin, idString, description, syntax, help);
		}

		@Override
		public Object execute(String args) throws Exception {
			return doCommand(this, hint + idString + " " + args);
		}
	}
	
	private StringTable doCommand(Command cmd, String sql) throws Exception {
		DBConnection connection = (DBConnection)sessionContext.getDbConnection();
		Map<String, Cluster> clusters = connection.getClusterMap();
		Cluster defaultCluster = connection.getDefaultCluster();
		
		if (cmd == cShowTables) {
			return Dbi.showTables(clusters, sql, false, defaultCluster);
		} else if (cmd == cShowTablesForPolicy) {
			return Dbi.showTablesForPolicy(clusters, sql, false, defaultCluster);
		} else if (cmd == cShowTablesForDbn) {
			return Dbi.showTablesForDbn(clusters, sql, false, defaultCluster);
		} else if (cmd == cShowViews) {
			return Dbi.showViews(clusters, sql, defaultCluster);
		} else if (cmd == cShowCreateTable) {
			return showCreateTable(sql, defaultCluster);
		} else if (cmd == cShowIndexForTable) {
			return Dbi.showIndexForTable(clusters, sql, defaultCluster);
		} else if (cmd == cShowDbns) {
			return Dbi.showDbns(clusters, sql, defaultCluster);
		} else if (cmd == cShowDbnsForPolicy) {
			return Dbi.showDbnsForPolicy(clusters, sql, defaultCluster);
		} else if (cmd == cShowDbnsForTable) {
			return Dbi.showDbnsForTable(clusters, sql, defaultCluster);
		} else if (cmd == cShowPolicies) {
			return Dbi.showPolicies(clusters, sql, defaultCluster);
		} else if (cmd == cShowDdb) {
			return Dbi.showDdbs(clusters);
		} else if (cmd == cShowCurrentDdb) {
			return Dbi.showCurrentDdb(defaultCluster);
		} else if (cmd == cShowTriggers) {
			return Dbi.showTriggers(clusters, sql, defaultCluster);
		} else if (cmd == cShowProcedures) {
			return Dbi.showProcedures(clusters, sql, defaultCluster);
		} else if (cmd == cDesc) {
			return Dbi.desc(clusters, sql, defaultCluster);
		} else if (cmd == cDescPolicy) {
			return Dbi.descPolicy(clusters, sql, defaultCluster);
		} else
			throw new Exception("Unsupported Command: " + cmd.getIdString());
	}
	
	/*
	 * show create table涉及到用户名和密码，需要特殊处理
	 */
	private StringTable showCreateTable(String sql, Cluster cluster) throws SQLException {
		SShowCreateTable s = (SShowCreateTable) new DDLParser().parse(sql);
		TableInfo t = cluster.getTableInfo(s.getTableName());
		if (t == null)
			throw new SQLException("Table '" + s.getTableName()	+ "' not found.");
		Policy p = t.getBalancePolicy();
		Database oneDb = p.getDbList().get(0);
		Connection conn = oneDb.getDataSource(0, 0).getConnection(
				sessionContext.getUsername(), sessionContext.getPassword());
		StringBuilder sb = new StringBuilder();
		Dbi.doShowCreateTable(p, t, conn, oneDb.getDomainSchemaName(), sb);

		StringTable stringTable = new StringTable("Show_Create_Table", new String[] {"Table", "Create Table"});
		stringTable.addRow(new String[] {s.getTableName(), sb.toString()});
		return stringTable;
	}
	
	StringTable showCommands() {
		StringTable stringTable = new StringTable("Qs Commands", new String[]{"NAME", "SYNTAX", "DESCRIPTION"});
		for (Command cmd : this.getCommands()) {
			stringTable.addRow(new String[] { cmd.getIdString(),
					cmd.getSyntax(), cmd.getDescription() });
		}
		return stringTable;
	}
}
