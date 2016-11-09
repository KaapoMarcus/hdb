
package com.netease.backend.db.common.sql;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.enumeration.ResourceStatusType;
import com.netease.backend.db.common.enumeration.ResourceType;
import com.netease.backend.db.common.management.BackupConfig;
import com.netease.backend.db.common.management.Cluster;
import com.netease.backend.db.common.management.DumpConfig;
import com.netease.backend.db.common.management.model.LogFileReadDescriptor;
import com.netease.backend.db.common.management.model.RangePartition;
import com.netease.backend.db.common.schema.DBAPrivilege;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnCluster;
import com.netease.backend.db.common.schema.EntityPrivilege;
import com.netease.backend.db.common.schema.OnlineAlterTaskInfo;
import com.netease.backend.db.common.schema.Policy;
import com.netease.backend.db.common.schema.Routine;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.schema.Trigger;
import com.netease.backend.db.common.schema.User;
import com.netease.backend.db.common.schema.dbengine.DatabaseFactory;
import com.netease.backend.db.common.schema.type.EntityPrivilegeType;
import com.netease.backend.db.common.sql.SUse.UseType;
import com.netease.backend.db.common.sql.plan.CronExpression;
import com.netease.backend.db.common.sql.plan.PlanJob;
import com.netease.backend.db.common.sql.plan.PlanTime;
import com.netease.backend.db.common.sql.plan.PlanType;
import com.netease.backend.db.common.sql.plan.SAddPlanJob;
import com.netease.backend.db.common.sql.plan.SAlterPlan;
import com.netease.backend.db.common.sql.plan.SDropPlan;
import com.netease.backend.db.common.sql.plan.SDropPlanJob;
import com.netease.backend.db.common.sql.plan.SPausePlan;
import com.netease.backend.db.common.sql.plan.SPlan;
import com.netease.backend.db.common.sql.plan.SResumePlan;
import com.netease.backend.db.common.sql.plan.SSetPlanNeedMail;
import com.netease.backend.db.common.sql.plan.SSetPlanTime;
import com.netease.backend.db.common.sql.plan.SShowPlans;
import com.netease.backend.db.common.stat.StatTask;
import com.netease.backend.db.common.utils.SQLCommonUtils;
import com.netease.backend.db.common.utils.StringUtils;
import com.netease.backend.db.common.utils.TimeUtils;
import com.netease.backend.db.common.utils.Validator;
import com.netease.cli.CmdHelper;
import com.netease.cli.CmdWordReader;
import com.netease.util.Pair;


public class DDLParser {
	private static List<String> planOptions = Arrays.asList(new String[] { "force", "quiet",
			"delimiter", "ignore_error", "charset", "autocommit", "prepare", "debug", "execute_on_dbn",
			"ignore_failed_clients", "ignore_unreachable_clients", "safeupdate" });

	private static List<String> blockOptions = Arrays.asList(new String[] { "try", "catch", "finally",
			"end" });

	private SQLLexParser parser;

	private Cluster clusterInfo = null;

	private PlanType planType = PlanType.INVALID; 

	
	public DDLParser() {}


	
	public DDLParser(Cluster cluster) {
		if (null == cluster)
			throw new IllegalArgumentException("Cluster instance should not be null. "
					+ "Or the constructor without arguments is better");
		this.clusterInfo = cluster;
	}


	
	public Statement parse(String sql) throws SQLException {
		if (!sql.endsWith(";"))
			sql = sql + ";";
		
		
		sql = sql.replaceAll("`", "");

		Statement r = null;
		try {
			parser = SQLLexParser.getLexParser(sql);
		} catch (SQLException e) {
			
			StringTokenizer tokenizer = new StringTokenizer(sql);
			String token = getNextToken(tokenizer);
			if ("CREATE".equalsIgnoreCase(token))
				r = parseCreateTrProc(sql);
			else if ("ALTER".equalsIgnoreCase(token))
				r = parseAlterTrProcSql(sql);
			if (r != null)
				return r;
			else
				throw e;
		}
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("CREATE")) {
			planType = PlanType.SCHEMA_MODIFY;
			parser.read();
			String token = parser.getCurrentToken();
			
			if (token.equalsIgnoreCase("TABLE")	|| token.equalsIgnoreCase("CLUSTER")
					|| token.equalsIgnoreCase("INDEX") || token.equalsIgnoreCase("KEY")
					|| token.equalsIgnoreCase("UNIQUE") || token.equalsIgnoreCase("BITMAP")
					|| token.equalsIgnoreCase("VIEW") || token.equalsIgnoreCase("TRIGGER")
					|| token.equalsIgnoreCase("PROCEDURE")) {
				DbnType dbnType = parseDbnTypeByCreate(token, sql);
				r = parseByDbn(dbnType, sql);
				return r;
			} else if (token.equalsIgnoreCase("POLICY")) {
				r = parseCreatePolicy();
			} else if (token.equalsIgnoreCase("STAT")) {
				planType = PlanType.STATISTICS;
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("TASK"))
					r = parseCreateStatTask();
			} else {
				
				while (true) {
					if (parser.parseOver())
						throw new SQLException("Unknown command: " + sql + ".");
					parser.read();
					token = parser.getCurrentToken();
					if (token.equalsIgnoreCase("VIEW")
							|| token.equalsIgnoreCase("TRIGGER")
							|| token.equalsIgnoreCase("PROCEDURE")) { 
						break;
					}
				}
				DbnType dbnType = parseDbnTypeByCreate(token, sql);
				r = parseByDbn(dbnType, sql);
				return r;
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("ALTER")) {
			parser.read();
			boolean ignore = parser.readIf("IGNORE");
			String token = parser.getCurrentToken();
			
			if (token.equalsIgnoreCase("TABLE")
					|| token.equalsIgnoreCase("VIEW")
					|| token.equalsIgnoreCase("CLUSTER")) {
				planType = PlanType.SCHEMA_MODIFY;
				DbnType dbnType = parseDbnTypeByAlter(token, sql);
				r = parseByDbn(dbnType, sql);
				return r;
			} else if (!ignore && parser.getCurrentToken().equalsIgnoreCase("PLAN")) {
				parser.read();
				String planName = DDLParserUtils.readIdentifier(parser, "name of plan");
				if (parser.getCurrentToken().equalsIgnoreCase("ADD"))
					
					return new SAlterPlan(planName, parseAddPlanJob(sql));
				else if (parser.getCurrentToken().equalsIgnoreCase("DROP"))
					r = new SAlterPlan(planName, parseDropPlanJob());
				else if (parser.getCurrentToken().equalsIgnoreCase("SET")) {
					parser.read();
					if (parser.getCurrentToken().equalsIgnoreCase("TIME")) {
						parser.read();
						r = new SAlterPlan(planName, parseSetPlanTime());
					} else if (parser.getCurrentToken().equalsIgnoreCase("NEEDMAIL"))
						r = new SAlterPlan(planName, parseSetPlanNeedMail());
				}
			} else if (!ignore && parser.getCurrentToken().equalsIgnoreCase("STAT"))
				r = parseAlterStatResult();
			else if (!ignore && parser.readIf("GLOBAL_PS_CACHE"))
				r = parseAlterGlobalPsCache();
			else if (!ignore && parser.readIf("CLIENT_PS_CACHE"))
				r = parseAlterClientPsCache();
			else if (!ignore && parser.getCurrentToken().equalsIgnoreCase("TRIGGER")) {
				r = parseAlterTrigger(sql);
				planType = PlanType.SCHEMA_MODIFY;
				if (r instanceof SAlterTriggerSetSQL)
					return r;
			} else if (!ignore && parser.getCurrentToken().equalsIgnoreCase("PROCEDURE")) {
				r = parseAlterProcedure(sql);
				planType = PlanType.SCHEMA_MODIFY;
				if (r instanceof SAlterProcedureSetSQL)
					return r;
			} else if (!ignore){
				
				while (true) {
					if (parser.parseOver())
						throw new SQLException("Unknown command: " + sql + ".");
					parser.read();
					token = parser.getCurrentToken();
					if (token.equalsIgnoreCase("VIEW"))
						break;
				}
				DbnType dbnType = parseDbnTypeByAlter(token, sql);
				if (dbnType == DbnType.Oracle) 
					throw new SQLException("Syntax err for Oracle: " + sql + ".");
				planType = PlanType.SCHEMA_MODIFY;
				r = parseByDbn(dbnType, sql);
				return r;
			} else
				throw new SQLException("Unknown command: " + sql + ".");

		} else if (parser.getCurrentToken().equalsIgnoreCase("DROP")) {
			parser.read();
			String token = parser.getCurrentToken();
			if (token.equalsIgnoreCase("TABLE")	|| token.equalsIgnoreCase("VIEW")
					||token.equalsIgnoreCase("CLUSTER")	|| token.equalsIgnoreCase("INDEX")
					||token.equalsIgnoreCase("TRIGGER")	|| token.equalsIgnoreCase("PROCEDURE")) {
				planType = PlanType.SCHEMA_MODIFY;
				r = parseSqlByDrop(token, sql);
			}
			if (parser.getCurrentToken().equalsIgnoreCase("USER")) {
				
				r = parseDropUser();
				planType = PlanType.USER_MANAGEMENT;
			} else if (parser.getCurrentToken().equalsIgnoreCase("POLICY")) {
				r = parseDropPolicy();
				planType = PlanType.SCHEMA_MODIFY;
			} else if (parser.getCurrentToken().equalsIgnoreCase("DBN")) {
				r = parseDropDbn();
				planType = PlanType.SCHEMA_MODIFY;
			} else if (parser.getCurrentToken().equalsIgnoreCase("STAT")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("TASK")) {
					r = parseDropStatTask();
					planType = PlanType.STATISTICS;
				}
			} else if (parser.getCurrentToken().equalsIgnoreCase("PLAN"))
				r = parseDropPlan();
			else if (parser.getCurrentToken().equalsIgnoreCase("MIGRATE"))
				r = parseDropMigrate();
			else if (parser.getCurrentToken().equalsIgnoreCase("PARTITIONS")) {
				r = parseDropPartitions();
				planType = PlanType.PARTITION;
			} else if (parser.getCurrentToken().equalsIgnoreCase("POLICY"))
				r = parseDropPolicy();
			else if (parser.getCurrentToken().equalsIgnoreCase("TABLE")
					|| parser.getCurrentToken().equalsIgnoreCase("VIEW"))
				r = parseDropTable();
			else if (parser.readIf("ONLINE")) {
				if (parser.readIf("MIGRATE"))
					r = parseDropOnlineMigrate();
				else if (parser.readIf("ALTER") && parser.readIf("TABLE"))
					r = parseDropOnlineAlterTable();
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("ADD")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("DBN"))
				r = parseAddDbn();
			else if (parser.getCurrentToken().equalsIgnoreCase("USER")) {
				r = parseAddUser();
				planType = PlanType.USER_MANAGEMENT;
			} else if (parser.getCurrentToken().equalsIgnoreCase("HOST")) {
				
				r = parseAddHost();
				planType = PlanType.USER_MANAGEMENT;
			} else if (parser.getCurrentToken().equalsIgnoreCase("MONITOR")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("CLIENTS"))
					r = parseAddMonitorClients();
			} else if (parser.getCurrentToken().equalsIgnoreCase("BUCKETNO")) {
				r = parseAddBucketno();
				planType = PlanType.BUCKETNO_ADD;
			} else if (parser.getCurrentToken().equalsIgnoreCase("PLAN"))
				
				return parseAddPlan(sql);
			else if (parser.getCurrentToken().equalsIgnoreCase("PARTITIONS")) {
				r = parseAddPartitions();
				planType = PlanType.PARTITION;
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("STOP")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("DBN"))
				r = parseStopDbn();
			else if (parser.getCurrentToken().equalsIgnoreCase("CLIENTS"))
				r = parseStopClients();
			else if (parser.getCurrentToken().equalsIgnoreCase("STAT")) {
				r = parseStopStatTask();
				planType = PlanType.STATISTICS;
			} else if (parser.readIf("SINGLE") && parser.readIf("MIGRATE")) {
				r = new SStopSingleMigrate();
				planType = PlanType.DATA_MIGRATION;
			} else if (parser.readIf("CONCURRENT") && parser.readIf("MIGRATE")) {
				r = new SStopConcurrentMigrate();
				planType = PlanType.DATA_MIGRATION;
			} else if (parser.getCurrentToken().equalsIgnoreCase("SLAVES")) {
				r = parseStopSlaves();
				planType = PlanType.SCHEMA_MODIFY;
			} else if (parser.readIf("ONLINE")) {
				if (parser.readIf("MIGRATE"))
					r = parseStopOnlineMigrate();
				else if (parser.readIf("ALTER") && parser.readIf("TABLE"))
					r = parseStopOnlineAlterTable();
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("SHOW")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("SCHEMA"))
				r = parseShowSchema();
			else if (parser.getCurrentToken().equalsIgnoreCase("GRANTS"))
				r = parseShowGrants();
			else if (parser.getCurrentToken().equalsIgnoreCase("MONITOR")) {
				parser.read();
				if (parser.readIf("CLIENTS"))
					r = new SShowMonitorClients();
			} else if (parser.readIf("USERS"))
				r = new SShowUsers();
			else if (parser.readIf("CLIENTS"))
				r = new SShowClients();
			else if (parser.readIf("DBNCLUSTERS")) 
				r = new SShowClusters();
			else if (parser.readIf("HASHFUNCTIONS")) 
				r = new SShowHashFunctions();
			else if (parser.readIf("PLANS"))
				r = new SShowPlans();
			else if (parser.getCurrentToken().equalsIgnoreCase("OPS"))
				r = parseShowOps();
			else if (parser.getCurrentToken().equalsIgnoreCase("RESOURCE")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("STATUS"))
					r = parseShowResourceStatus();
			} else if (parser.getCurrentToken().equalsIgnoreCase("ACTIVE")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("OF"))
					r = parseShowActive();
				else if (parser.getCurrentToken().equalsIgnoreCase("DBN")) {
					parser.read();
					if (parser.getCurrentToken().equalsIgnoreCase("CONN"))
						r = parseShowActiveDbnConn();
				} else if (parser.getCurrentToken().equalsIgnoreCase("CONN"))
					r = parseShowClientActiveConnTrace();
			} else if (parser.getCurrentToken().equalsIgnoreCase("IC"))
				r = parseShowIc();
			else if (parser.getCurrentToken().equalsIgnoreCase("HC"))
				r = parseShowHc();
			else if (parser.getCurrentToken().equalsIgnoreCase("DBN")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("CONN"))
					r = parseShowDbnConn();
			} else if (parser.getCurrentToken().equalsIgnoreCase("CREATE")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("TABLE"))
					r = parseShowCreateTable();
			} else if (parser.getCurrentToken().equalsIgnoreCase("STAT")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("TASKS"))
					r = new SShowStatTasks();
				else if (parser.getCurrentToken().equalsIgnoreCase("ANALYSIS"))
					r = parseShowStatAnalysis();
				else if (parser.getCurrentToken().equalsIgnoreCase("RECommandSULTS"))
					r = parseShowStatResults();
				else
					r = parseShowStat();
			} else if (parser.getCurrentToken().equalsIgnoreCase("QUOTA"))
				r = parseShowQuota();
			else if (parser.getCurrentToken().equalsIgnoreCase("TABLE")) {
				parser.read();
				if (parser.readIf("STAT"))
					r = parseShowTableStat();
			} else if (parser.getCurrentToken().equalsIgnoreCase("STATUS"))
				r = parseShowClientStatus();
			else if (parser.getCurrentToken().equalsIgnoreCase("TRACE"))
				r = parseShowClientTrace();
			else if (parser.getCurrentToken().equalsIgnoreCase("MIGRATE"))
				r = parseShowMigrate();
			else if (parser.getCurrentToken().equalsIgnoreCase("PARTITIONS"))
				r = parseShowPartitions();
			else if (parser.getCurrentToken().equalsIgnoreCase("LOG"))
				r = parseShowLog();
			else if (parser.getCurrentToken().equalsIgnoreCase("TABLES")) {
				parser.read();
				r = new SShowTables();
				if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
					parser.read();
					if (parser.getCurrentToken().equalsIgnoreCase("POLICY"))
						r = parseShowTablesForPolicy();
					else if (parser.getCurrentToken().equalsIgnoreCase("DBN"))
						r = parseShowTablesForDbn();
					else if (parser.getCurrentToken().equalsIgnoreCase("MODEL"))
						r = parseShowTablesForModel();
				}
			} else if (parser.getCurrentToken().equalsIgnoreCase("INDEX")) {
				parser.read();
				if (parser.readIf("FOR"))
					r = new SShowIndexForTable(DDLParserUtils.readIdentifier(parser, "tablename"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("DBNS")) {
				parser.read();
				r = new SShowDbns();
				if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
					parser.read();
					if (parser.getCurrentToken().equalsIgnoreCase("POLICY"))
						r = parseShowDbnsForPolicy();
					else if (parser.getCurrentToken().equalsIgnoreCase("TABLE"))
						r = parseShowDbnsForTable();
					else if (parser.getCurrentToken().equalsIgnoreCase("RECORD"))
						r = parseShowDbnsForRecord();
				}
			} else if (parser.readIf("SLAVE") && parser.readIf("DBNS"))
				r = parseShowSlaveDbns();
			else if (parser.readIf("MASTER") && parser.readIf("DBNS"))
				r = new SShowMasterDbns();
			else if (parser.getCurrentToken().equalsIgnoreCase("TRIGGERS"))
				r = parseShowTriggers();
			else if (parser.getCurrentToken().equalsIgnoreCase("PROCEDURES"))
				r = parseShowProcedures();
			else if (parser.readIf("VIEWS"))
				r = new SShowViews();
			else if (parser.readIf("POLICIES"))
				r = new SShowPolicies();
			else if (parser.readIf("ONLINE")) {
				if (parser.readIf("MIGRATE"))
					r = parseShowOnlineMigrate();
				else if (parser.readIf("ALTER") && parser.readIf("TABLE")) {
					r = parseShowOnlineAlterTable();
				}
			} else if (parser.readIf("SLOWLOG")){
			    r = parseShowSlowlogHistory();
			} else if (parser.readIf("NTSE")) {
				r = parseShowNtseParam();
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("DUMP"))
			r = parseDump();
		else if (parser.getCurrentToken().equalsIgnoreCase("SOURCE"))
			r = parseSource();
		else if (parser.getCurrentToken().equalsIgnoreCase("LOAD"))
			r = parseLoad();
		else if (parser.getCurrentToken().equalsIgnoreCase("GRANT")) {
			r = parseGrant();
			planType = PlanType.USER_MANAGEMENT;
		} else if (parser.getCurrentToken().equalsIgnoreCase("REVOKE")) {
			r = parseRevoke();
			planType = PlanType.USER_MANAGEMENT;
		} else if (parser.getCurrentToken().equalsIgnoreCase("REMOVE")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("HOST")) {
				
				r = parseRemoveHost();
				planType = PlanType.USER_MANAGEMENT;
			} else if (parser.getCurrentToken().equalsIgnoreCase("MONITOR")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("CLIENTS"))
					r = parseRemoveMonitorClients();
			} else if (parser.getCurrentToken().equalsIgnoreCase("STAT"))
				r = parseRemoveStatResult();
		} else if (parser.getCurrentToken().equalsIgnoreCase("SET")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("PASSWORD")) {
				
				r = parseSetPassword();
				planType = PlanType.USER_MANAGEMENT;
			} else if (parser.getCurrentToken().equalsIgnoreCase("SHOW")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("STAT")) {
					parser.read();
					if (parser.getCurrentToken().equalsIgnoreCase("RESULTS"))
						r = parseSetShowStatResults();
				}
			} else if (parser.getCurrentToken().equalsIgnoreCase("QUOTA")) {
				
				r = parseSetQuota();
				planType = PlanType.USER_MANAGEMENT;
			} else if (parser.getCurrentToken().equalsIgnoreCase("DESC")) {
				
				r = parseSetDesc();
				planType = PlanType.USER_MANAGEMENT;
			} else if (parser.getCurrentToken().equalsIgnoreCase("ROLE")) {
				
				r = parseSetRole();
				planType = PlanType.USER_MANAGEMENT;
			} else if (planOptions.contains(parser.getCurrentToken().toLowerCase())) {
				parser.read();
				parser.read(); 
				planType = PlanType.OPTION;
			} else if (parser.readIf("TABLE") && parser.readIf("IDASSIGNMENT"))
				r = parseSetTableIdAssignment();
			else if (parser.readIf("DBN") && parser.readIf("DIRTY"))
				r = parseSetDbnDirty();
			else if (parser.readIf("ONLINE") && parser.readIf("ALTER") && parser.readIf("TABLE"))
				r = parseSetOnlineAlterTable();
			else if (parser.readIf("SLAVE") && parser.readIf("AUTO") && parser.readIf("SWITCH"))
				r = parseSetSlaveAutoSwitch();
		} else if (parser.getCurrentToken().equalsIgnoreCase("REFRESH")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("CLUSTER"))
				r = new SRefreshCluster();
			if (parser.readIf("TABLE") && parser.readIf("STARTID")) {
				r = new SRefreshTblStartID();
				planType = PlanType.SCHEMA_MODIFY;
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("RESET")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("OPS"))
				r = parseResetOps();
		} else if (parser.getCurrentToken().equalsIgnoreCase("CANCEL")) {
			parser.read();
			if (parser.readIf("ONLINE") && parser.readIf("ALTER") && parser.readIf("TABLE"))
				r = parseCancelOnlineAlterTable();
		} else if (parser.getCurrentToken().equalsIgnoreCase("KILL")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("HANGING")) {
				parser.read();
				if (parser.getCurrentToken().equalsIgnoreCase("CONN"))
					r = parseKillHangingConn();
			} else if (parser.readIf("BY"))
				r = parseKillThread();
		} else if (parser.getCurrentToken().equalsIgnoreCase("DISABLE")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("DBN"))
				r = parseDisableDbn();
			else if (parser.getCurrentToken().equalsIgnoreCase("WRITE")) {
				parser.read();
				if (parser.readIf("TABLE")) {
					String tblName = DDLParserUtils.readIdentifier(parser, "table name");
					r = new SDisableWriteTable(tblName);
				} else if (parser.readIf("POLICY")) {
					String plyName = DDLParserUtils.readIdentifier(parser, "policy name");
					r = new SDiableWritePolicy(plyName);
				}
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("ENABLE")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("DBN"))
				r = parseEnableDbn();
			else if (parser.getCurrentToken().equalsIgnoreCase("WRITE")) {
				parser.read();
				if (parser.readIf("TABLE")) {
					String tblName = DDLParserUtils.readIdentifier(parser, "table name");
					r = new SEnableWriteTable(tblName);
				} else if (parser.readIf("POLICY")) {
					String plyName = DDLParserUtils.readIdentifier(parser, "policy name");
					r = new SEnableWritePolicy(plyName);
				}
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("START")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("DBN"))
				r = parseStartDbn();
			else if (parser.getCurrentToken().equalsIgnoreCase("CLIENTS"))
				r = parseStartClients();
			else if (parser.getCurrentToken().equalsIgnoreCase("STAT")) {
				r = parseStartStatTask();
				planType = PlanType.STATISTICS;
			} else if (parser.getCurrentToken().equalsIgnoreCase("SLAVES")) {
				r = parseStartSlaves();
				planType = PlanType.SCHEMA_MODIFY;
			} else if (parser.readIf("ONLINE")) {
				if (parser.readIf("MIGRATE"))
					r = parseStartOnlineMigrate();
				else if (parser.readIf("ALTER") && parser.readIf("TABLE"))
					r = parseStartOnlineAlterTable();
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("DIFF")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("STAT"))
				r = parseDiffStat();
		} else if (parser.getCurrentToken().equalsIgnoreCase("COLLECT")) {
			parser.read();
			if (parser.readIf("TABLE")) {
				if (parser.getCurrentToken().equalsIgnoreCase("STAT")) {
					r = parseCollectTableStat();
					planType = PlanType.TABLE_STAT_COLLECT;
				}
			} else if (parser.readIf("SLOWLOG")){
			    r = parseCollectSlowlog();
			        planType = PlanType.STATISTICS;
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("PAUSE"))
			r = parsePausePlan();
		else if (parser.getCurrentToken().equalsIgnoreCase("RESUME"))
			r = parseResumePlan();
		else if (parser.getCurrentToken().equalsIgnoreCase("BACKUP")) {
			r = parseBackup();
			planType = PlanType.DATA_BACKUP;
		} else if (parser.getCurrentToken().equalsIgnoreCase("EXPORT")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("SYSDB"))
				r = parseExportSysdb();
			else
				r = parseExport();
			planType = PlanType.DATA_EXPORT;
		} else if (parser.getCurrentToken().equalsIgnoreCase("GET")) {
			parser.read();
			if (parser.readIf("STAT")) {
				if (parser.getCurrentToken().equalsIgnoreCase("RESULT")) {
					r = parseGetStatResult();
					planType = PlanType.STATISTICS;
				}
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("CLEAN")) {
			parser.read();
			if (parser.readIf("SYS")) {
				if (parser.getCurrentToken().equalsIgnoreCase("TABLES")) {
					r = parseCleanSysTables();
					planType = PlanType.SYSDB_CLEAN;
				}
			}
		} else if (parser.readIf("SINGLE") && parser.readIf("MIGRATE")) {
			r = parseSingleMigrate();
			planType = PlanType.DATA_MIGRATION;
		} else if (parser.getCurrentToken().equalsIgnoreCase("CONCURRENT")) {
			parser.read();
			if (parser.readIf("MIGRATE")) {
				r = parseConcurrentMigrate();
				planType = PlanType.DATA_MIGRATION;
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("DESC")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("POLICY"))
				r = parseDescPolicy();
			else if (parser.getCurrentToken().equalsIgnoreCase("DBNCLUSTER"))
				r = parseDescCluster();
			else if (parser.getCurrentToken().equalsIgnoreCase("HASHFUNCTION"))
				r = parseDescHashFunction();
			else
				r = parseDesc();
		} else if (parser.getCurrentToken().equalsIgnoreCase("USE")) {
			r = parseUse();
			if (((SUse) r).getType() == UseType.DBA || ((SUse) r).getType() == UseType.DBI
					|| ((SUse) r).getType() == UseType.DDB)
				planType = PlanType.OPTION;
			else
				planType = PlanType.USE_DBNS;
		} else if (parser.getCurrentToken().equalsIgnoreCase("SWITCH")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("SLAVE")) {
				r = parseSwitchSlave();
				planType = PlanType.SCHEMA_MODIFY;
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("BUILD")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("SLAVES")) {
				r = parseBuildSlaves();
				planType = PlanType.SCHEMA_MODIFY;
			}
		} else if (parser.getCurrentToken().equalsIgnoreCase("SLICEDMASSUPDATE")) {
			r = parseSliceMassUpdate(sql);
			planType = PlanType.OTHER;
		} else if (parser.readIf("CHANGE")) {
			if (parser.readIf("TABLE")) {
				if (parser.readIf("POLICY"))
					r = parseChangeTablePly();
				else if (parser.readIf("MODEL"))
					r = parseChangeTableModel();
			}
			else if (parser.readIf("COLUMN") && parser.readIf("COMMENT"))
				r = parseChangeColumnComment();
			else if (parser.readIf("POLICY") && parser.readIf("COMMENT"))
				r = parseChangePolicyComment();
		}
		else if (parser.getCurrentToken().equalsIgnoreCase("BASH")) {
			r = parseBash();
			planType = PlanType.OTHER;
		} else if (blockOptions.contains(parser.getCurrentToken().toLowerCase())) {
			parser.read();
			planType = PlanType.OTHER;
		} else if (parser.readIf("ONLINE") && parser.readIf("MIGRATE")) {
			r = parseOnlineMigrate();
		} else if (parser.readIf("COMMENT")) {
			
			DDLParserUtils.match(parser, "ON");
			parser.read();
			String tblName = DDLParserUtils.readIdentifier(parser, "table name");
			if (getDbnTypeByTable(tblName) != DbnType.Oracle)
				throw new SQLException("Syntax err, 'COMMENT ON' only use in Oracle not MySQL");
			r = parseByDbn(DbnType.Oracle, sql);
			return r;
		}

		if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
			parser.read();
			if (r != null)
				r.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
		}
		DDLParserUtils.match(parser, ";");
		if (r != null) {
			if (planType == PlanType.INVALID)
				planType = PlanType.OTHER;
			return r;
		}
		return null;
	}


	private SCreatePolicy parseCreatePolicy() throws SQLException {
		parser.read();
		String policyName = DDLParserUtils.readIdentifier(parser, "policy name");
		
		String dbnTypeStr = DDLParserUtils.readIdentifier(parser, "dbnType");
		if (!(dbnTypeStr.equalsIgnoreCase("oracle") || dbnTypeStr.equalsIgnoreCase("mysql")))
			throw new SQLException("Invalid DBN type:" + dbnTypeStr);

		String bucketLayoutMethod = parser.getCurrentToken();
		List<String> srcDbns = new LinkedList<String>();
		String lookupPolicy = null;
		int bucketCount = 0;
		if (bucketLayoutMethod.equalsIgnoreCase("rr") || bucketLayoutMethod.equalsIgnoreCase("sr")) {
			if (nextToken(parser).equals("(")) {
				while (!nextToken(parser).equals(")"))
					srcDbns.add(parser.getCurrentToken());
				parser.read();
			}
			bucketCount = Integer.parseInt(parser.getCurrentToken());
		} else if (bucketLayoutMethod.equalsIgnoreCase("man")) {
			parser.read();
			DDLParserUtils.match(parser, "(");
			while (!parser.getCurrentToken().equals(")")) {
				srcDbns.add(parser.getCurrentToken());
				parser.read();
			}
			bucketCount = srcDbns.size();
		} else if (bucketLayoutMethod.equalsIgnoreCase("lookup")) {
			lookupPolicy = nextToken(parser);
		} else
			throw new IllegalArgumentException("Invalid bucket layout algorithm:" + bucketLayoutMethod);
		parser.read();

		
		String comment = "";
		if (parser.getCurrentToken().equalsIgnoreCase("COMMENT")) {
			parser.read();
			if (!parser.getCurrentToken().equals("'"))
				throw new SQLException("There is no comment string after 'COMMENT'");
			comment = DDLParserUtils.getTokenOrValue(parser, false);
			parser.read();
		}
		return new SCreatePolicy(policyName, dbnTypeStr, bucketLayoutMethod, srcDbns, bucketCount, lookupPolicy, comment);
	}


	
	private SAddDbn parseAddDbn() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readTokenOrValue(parser, false);
		String url = DDLParserUtils.readTokenOrValue(parser, false);
		DbnType dbnType = SQLCommonUtils.getDbnTypeByUrl(url);
		if (null == dbnType)
			throw new SQLException("Url definition is not correct");
		
		String user = "";
		String password = "";
		boolean initUsers = true;
		String sshUser = "";
		String domainSchema = "";
		String tableSpace = "";
		int sshPort = 22;
		boolean isMaster = true;
		int masterDb = 0;
		String cnfFile = null;
		
		if (!parser.getCurrentToken().equalsIgnoreCase("without")
				&& !parser.getCurrentToken().equalsIgnoreCase("sshuser")
				&& !parser.getCurrentToken().equalsIgnoreCase("sshport")
				&& !parser.getCurrentToken().equalsIgnoreCase("parentid")
				&& !parser.getCurrentToken().equalsIgnoreCase("configfile")
				&& !parser.getCurrentToken().equalsIgnoreCase("schema")
				&& !parser.getCurrentToken().equalsIgnoreCase("tablespace")
				&& !parser.getCurrentToken().equals(";")
				&& !parser.getCurrentToken().equalsIgnoreCase("IN")) {
			user = DDLParserUtils.readTokenOrValue(parser, false);
			password = DDLParserUtils.readTokenOrValue(parser, false);
		}
		while (!parser.getCurrentToken().equals(";") && !parser.getCurrentToken().equalsIgnoreCase("IN")) {
			if (parser.getCurrentToken().equalsIgnoreCase("sshuser")) {
				parser.read();
				sshUser = DDLParserUtils.readTokenOrValue(parser, false);
			} else if (parser.getCurrentToken().equalsIgnoreCase("sshport")) {
				parser.read();
				sshPort = DDLParserUtils.readInt(parser, "SSH port");
			} else if (parser.getCurrentToken().equalsIgnoreCase("without")) {
				DDLParserUtils.readAndMatch(parser, "init");
				DDLParserUtils.match(parser, "user");
				initUsers = false;
			} else if (parser.getCurrentToken().equalsIgnoreCase("parentid")) {
				parser.read();
				isMaster = false;
				masterDb = DDLParserUtils.readInt(parser, "parent id");
			} else if (parser.getCurrentToken().equalsIgnoreCase("configfile")) {
				parser.read();
				cnfFile = DDLParserUtils.readTokenOrValue(parser, false);
			} else if (parser.getCurrentToken().equalsIgnoreCase("schema")) { 
				parser.read();
				domainSchema = DDLParserUtils.readTokenOrValue(parser, false);
			} else if (parser.getCurrentToken().equalsIgnoreCase("tablespace")) { 
				parser.read();
				tableSpace = DDLParserUtils.readTokenOrValue(parser, false);
			}
		}
		
		DatabaseFactory databaseFactory = new DatabaseFactory();
		Database database = databaseFactory.newDatabase(url, name, domainSchema, tableSpace, 0);
		database.setSshUser(sshUser);
		database.setSshPort(sshPort);
		database.setMaster(isMaster);
		database.setMasterDb(masterDb);
		database.setConfigFile(cnfFile);
		
		SAddDbn s = new SAddDbn(database, user, password, initUsers);
		if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
			parser.read();
			s.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
		}
		return s;
	}

	private SDropDbn parseDropDbn() throws SQLException {
		parser.read();

		String dbn = DDLParserUtils.readTokenOrValue(parser, false);
		return new SDropDbn(dbn);
	}

	private SStopDbn parseStopDbn() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "name of database node");
		String reason = "";
		if (!parser.getCurrentToken().equals(";") && !parser.getCurrentToken().equalsIgnoreCase("IN"))
			reason = DDLParserUtils.readTokenOrValue(parser, false);
		return new SStopDbn(name, reason);
	}

	private SStartDbn parseStartDbn() throws SQLException {
		parser.read();
		String dbn = DDLParserUtils.readIdentifier(parser, "name of database node");
		return new SStartDbn(dbn);
	}

	private SDisableDbn parseDisableDbn() throws SQLException {
		parser.read();

		String dbn = DDLParserUtils.readTokenOrValue(parser, false);
		return new SDisableDbn(dbn);
	}

	private SEnableDbn parseEnableDbn() throws SQLException {
		parser.read();

		String dbn = DDLParserUtils.readTokenOrValue(parser, false);
		return new SEnableDbn(dbn);
	}

	private SStartSlaves parseStartSlaves() throws SQLException {
		parser.read();
		List<String> names = readNameList(",", new String[] { ";", "WITHOUT" });
		SStartSlaves s = new SStartSlaves(names);
		if (parser.getCurrentToken().equalsIgnoreCase("WITHOUT")) {
			parser.read();
			DDLParserUtils.match(parser, "MODIFY");
			DDLParserUtils.match(parser, "CONFIG");
			s.setModifyCnf(false);
		}
		return s;
	}

	private SStopSlaves parseStopSlaves() throws SQLException {
		parser.read();
		List<String> names = readNameList(",", new String[] { ";", "WITHOUT" });
		SStopSlaves s = new SStopSlaves(names);
		if (parser.getCurrentToken().equalsIgnoreCase("WITHOUT")) {
			parser.read();
			DDLParserUtils.match(parser, "MODIFY");
			DDLParserUtils.match(parser, "CONFIG");
			s.setModifyCnf(false);
		}
		return s;
	}

	private SSwitchSlave parseSwitchSlave() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readTokenOrValue(parser, false);
		SSwitchSlave s = new SSwitchSlave(name);
		DDLParserUtils.match(parser, "EXPIRED");
		int time = DDLParserUtils.readInt(parser, "wait time");
		s.setWaitTime(time);
		return s;
	}

	private SBuildSlaves parseBuildSlaves() throws SQLException {
		List<String> names = new ArrayList<String>();
		List<String> linkDirs = null;
		while (true) {
			parser.read();
			String name = DDLParserUtils.readTokenOrValue(parser, false);
			names.add(name);
			if (parser.readIf("WITHLINK")) {
				if (null == linkDirs)
					linkDirs = new ArrayList<String>();
				linkDirs.add(DDLParserUtils.readTokenOrValue(parser, false));
				parser.readIf("'");
			}
			if (parser.getCurrentToken().equalsIgnoreCase(","))
				continue;
			else if (parser.getCurrentToken().equalsIgnoreCase(";"))
				break;
			else
				throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
		}
		if (names.size() == 0)
			throw new SQLException("Invalid command, have no slave dbn names.");
		SBuildSlaves s = new SBuildSlaves(names);
		s.setLinkDirs(linkDirs);
		return s;
	}

	private SShowCreateTable parseShowCreateTable() throws SQLException {
		parser.read();
		String tableName = DDLParserUtils.readTokenOrValue(parser, false);
		return new SShowCreateTable(tableName);
	}

	private SDesc parseDesc() throws SQLException {
		String tableName = DDLParserUtils.readTokenOrValue(parser, false);
		return new SDesc(tableName);
	}

	private SDescPolicy parseDescPolicy() throws SQLException {
		parser.read();
		String tableName = DDLParserUtils.readTokenOrValue(parser, false);
		return new SDescPolicy(tableName);
	}

	private SDescCluster parseDescCluster() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readTokenOrValue(parser, false);
		return new SDescCluster(name);

	}
	
	private SDescHashFunction parseDescHashFunction() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readTokenOrValue(parser, false);
		return new SDescHashFunction(name);
	}

	private SShowSchema parseShowSchema() throws SQLException {
		parser.read();
		return new SShowSchema();
	}

	private SShowTablesForPolicy parseShowTablesForPolicy() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "policy name");
		SShowTablesForPolicy s = new SShowTablesForPolicy(name);
		return s;
	}

	private SShowTablesForDbn parseShowTablesForDbn() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readTokenOrValue(parser, false);
		SShowTablesForDbn s = new SShowTablesForDbn(name);
		return s;
	}

	private SShowTablesForModel parseShowTablesForModel() throws SQLException {
		parser.read();
		String model = DDLParserUtils.readIdentifier(parser, "name of model");
		SShowTablesForModel s = new SShowTablesForModel(model);
		return s;
	}

	private SShowDbnsForPolicy parseShowDbnsForPolicy() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "policy name");
		return new SShowDbnsForPolicy(name);
	}

	private SShowDbnsForTable parseShowDbnsForTable() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "table name");
		return new SShowDbnsForTable(name);
	}

	private SShowDbnsForRecord parseShowDbnsForRecord() throws SQLException {
		parser.read();
		String tableName = null;
		List<List<String>> valuesList = null;
		DDLParserUtils.match(parser, "TABLE");
		if (parser.getCurrentToken().equalsIgnoreCase("=")) {
			parser.read();
		} else {
			throw new SQLException("Invalid alter table option: input string must be TABLE=");
		}
		tableName = parser.getCurrentToken();
		if(tableName == null || tableName.length() == 0)
			throw new SQLException("tableName can not be null");
		
		parser.read();
		DDLParserUtils.match(parser, ",");
		
		DDLParserUtils.match(parser, "VALUE");
		if (parser.getCurrentToken().equalsIgnoreCase("=")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("(")) {
				
				valuesList = new ArrayList<List<String>>();
				while (parser.readIf("(")) {
					List<String> keys = readStringList(",", new String[] { ")", ";" });
					valuesList.add(keys);
					DDLParserUtils.match(parser, ")");
					if (parser.getCurrentToken().equalsIgnoreCase(";"))
						break;
					DDLParserUtils.match(parser, ",");
				}
			} else {
				
				List<String> keys = readStringList(",", new String[] { ";" });
				valuesList = new ArrayList<List<String>>(keys.size());
				for (String value : keys)
					valuesList.add(Arrays.asList(value));
			}
		} else {
			throw new SQLException("Invalid alter table option: input string must be VALUE=");
		}

		if(valuesList == null || valuesList.size() == 0){
			throw new SQLException("must specify at least one key value");
		}
		
		final int baseSize = valuesList.get(0).size();
		for (List<String> keys : valuesList) {
			if (keys.size() != baseSize)
				throw new SQLException("size of key for one table should be the same.");
		}
		return new SShowDbnsForRecord(tableName, valuesList);
	}

	private SShowSlaveDbns parseShowSlaveDbns() throws SQLException {
		SShowSlaveDbns s = new SShowSlaveDbns();
		if (parser.readIf("FOR"))
			s.setMasterName(DDLParserUtils.readTokenOrValue(parser, false));
		return s;
	}

	private SShowTriggers parseShowTriggers() throws SQLException {
		parser.read();
		SShowTriggers r = new SShowTriggers();
		if (parser.getCurrentToken().equalsIgnoreCase("LIKE")) {
			parser.read();
			r.setTableName(DDLParserUtils.readIdentifier(parser, "table name"));
		} else if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			r.setTriggerName(DDLParserUtils.readIdentifier(parser, "trigger name"));
		}
		return r;
	}

	private SShowProcedures parseShowProcedures() throws SQLException {
		parser.read();
		SShowProcedures r = new SShowProcedures();
		if (parser.readIf("FOR"))
			r.setSpName(DDLParserUtils.readIdentifier(parser, "procedure name"));
		return r;
	}

	private SDump parseDump() throws SQLException {
		parser.read();
		DLOption dlOption = new DLOption();
		SDump r = new SDump(dlOption);
		List<String> tables = new LinkedList<String>();
		while (parser.getCurrentToken().equalsIgnoreCase("-")) {
			parser.read();
			if (parseDlOption(dlOption))
				continue;
			if (parser.getCurrentToken().equalsIgnoreCase("t")) {
				parser.read();
				r.setDir(DDLParserUtils.readTokenOrValue(parser, false));
			} else if (parser.getCurrentToken().equalsIgnoreCase("w")) {
				parser.read();
				r.setWhere(DDLParserUtils.readTokenOrValue(parser, false));
			} else if (parser.getCurrentToken().equalsIgnoreCase("o")) {
				parser.read();
				r.setOrderBy(DDLParserUtils.readTokenOrValue(parser, false));
			} else if (parser.getCurrentToken().equalsIgnoreCase("p")) {
				parser.read();
				r.setParallel(true);
			} else if (parser.getCurrentToken().equalsIgnoreCase("s")) {
				parser.read();
				r.setSelect(DDLParserUtils.readTokenOrValue(parser, false));
			} else if (parser.getCurrentToken().equalsIgnoreCase("l")) {
				parser.read();
				long limit = readLong("number of records");
				if (limit <= 0)
					throw new SQLException("Record limit should be positive: " + limit);
				r.setLimit(limit);
			} else
				throw new SQLException("Unknown option: " + parser.getCurrentToken());
		}
		if (r.getSelect() == null) {
			while (!parser.getCurrentToken().equals(";")
					&& !parser.getCurrentToken().equalsIgnoreCase("into")) {
				tables.add(parser.getCurrentToken());
				parser.read();
			}
			if (tables.size() == 0)
				throw new SQLException("No tables specified.");
			r.setTables(tables);
		}
		if (parser.getCurrentToken().equalsIgnoreCase("into")) {
			if (r.getDir() != null)
				throw new SQLException("Can not specify file if -t if specified.");
			parser.read();
			r.setFile(DDLParserUtils.readTokenOrValue(parser, false));
		}
		return r;
	}

	private boolean parseDlOption(DLOption dlOption) throws SQLException {
		if (parser.getCurrentToken().equalsIgnoreCase("r")) {
			parser.read();
			String lineSeparator = DDLParserUtils.readTokenOrValue(parser, false);
			if (lineSeparator.equalsIgnoreCase("<lrcf>"))
				lineSeparator = "\r\n";
			else 
				lineSeparator = StringUtils.deEscapeString(lineSeparator);
			dlOption.setLineSeparator(lineSeparator);
		}  else if (parser.getCurrentToken().equals("a")) {
			parser.read();
			String value = DDLParserUtils.readTokenOrValue(parser, false);
			String attrDelimiter;
			if (value.equals("<tab>"))
				attrDelimiter = "\t";
			else if (value.equals("<space>"))
				attrDelimiter = " ";
			else {
				value = StringUtils.deEscapeString(value);



				attrDelimiter = value;
			}
			dlOption.setAttrDelimiter(attrDelimiter);
		} else if (parser.getCurrentToken().equals("c")) {
			parser.read();
			dlOption.setCharset(DDLParserUtils.readTokenOrValue(parser, false));
		} else
			return false;
		return true;
	}

	private SSource parseSource() throws SQLException {
		parser.read();
		boolean quiet = false;
		int batchSize = 0;
		while (parser.getCurrentToken().equalsIgnoreCase("-")) {
			parser.read();
			if (parser.readIf("q")) {
				quiet = true;
				continue;
			} else if (parser.readIf("b")) {
				batchSize = Integer.parseInt(parser.getCurrentToken());
				parser.read();
				continue;
			} else
				throw new SQLException("Invalid option '-" + parser.getCurrentToken() + "'.");
		}
		String file = DDLParserUtils.readTokenOrValue(parser, false);
		return new SSource(file, quiet, batchSize);
	}

	private SLoad parseLoad() throws SQLException {
		parser.read();
		DLOption dlOption = new DLOption();
		SLoad r = new SLoad(dlOption);

		while (parser.getCurrentToken().equals("-")) {
			parser.read();
			if (parseDlOption(dlOption))
				continue;
			if (parser.getCurrentToken().equals("t")) {
				parser.read();
				r.setTempDir(DDLParserUtils.readTokenOrValue(parser, false));
			} else if (parser.getCurrentToken().equals("e")) {
				parser.read();
				r.setIgnoreError(true);
			} else if (parser.getCurrentToken().equals("-")) {
				parser.read();
				if (parser.getCurrentToken().equals("smart")) {
					r.setSmart(true);
				} else if (parser.getCurrentToken().equals("skip")) {
					DDLParserUtils.readAndMatch(parser, "-");
					if (!parser.getCurrentToken().equals("load"))
						throw new SQLException("Invalid option: --skip-" + parser.getCurrentToken());
					r.setSkipLoad(true);
				} else
					throw new SQLException("Invalid option: --" + parser.getCurrentToken());
				parser.read();
			} else
				throw new SQLException("Invalid option: -" + parser.getCurrentToken());
		}
		String file = DDLParserUtils.readTokenOrValue(parser, false);
		r.setFile(file);

		if (parser.getCurrentToken().equalsIgnoreCase("replace")) {
			r.setReplaceOnDuplicates(true);
			parser.read();
		}
		DDLParserUtils.match(parser, "into");
		String table = DDLParserUtils.readIdentifier(parser, "table name");
		r.setTable(table);

		if (parser.getCurrentToken().equals("(")) {
			parser.read();
			List<String> columns = readNameList(",", new String[] { ")" });
			r.setColumns(columns);
			DDLParserUtils.match(parser, ")");
		}
		return r;
	}

	private SAddUser parseAddUser() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "username");
		int type = User.USER_TYPE_MAN;
		if (parser.getCurrentToken().equalsIgnoreCase("type")) {
			parser.read();
			String typeStr = DDLParserUtils.readIdentifier(parser, "user type");
			if (typeStr.equalsIgnoreCase("dba"))
				type = User.USER_TYPE_DBA;
			else if (typeStr.equalsIgnoreCase("man"))
				type = User.USER_TYPE_MAN;
			else if (typeStr.equalsIgnoreCase("agent"))
				throw new SQLException("Can add user of type AGENT.");
			else
				throw new SQLException("Invalid user type: " + typeStr);
		}
		DDLParserUtils.match(parser, "password");
		String password = DDLParserUtils.readTokenOrValue(parser, false);
		return new SAddUser(name, password, type);
	}

	private SDropUser parseDropUser() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "username");
		return new SDropUser(name);
	}

    private SGrant parseGrant() throws SQLException {
        String schemaName = "";
        String entityName = "";
        boolean read = false;
        boolean write = false;
        boolean exec = false;
        boolean grantOption = false;
        boolean all = false;
        String user = "";

        parser.read();
        while (true) {
            if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.READ.name())) {
                read = true;
                parser.read();
            } else if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.WRITE.name())) {
                write = true;
                parser.read();
            } else if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.EXEC.name())) {
                exec = true;
                parser.read();
            } else if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.ALL.name())) {
                DDLParserUtils.readAndMatch(parser, "PRIVILEGES");
                read = write = grantOption = false;
                all = true;
            } else {
                throw new SQLException("Invalid privilege��"
                        + parser.getCurrentToken());
            }

            if (parser.getCurrentToken().equals(",")) {
                parser.read();
            } else if (parser.getCurrentToken().equalsIgnoreCase("ON")) {
                break;
            }
        }
        if (parser.getCurrentToken().equalsIgnoreCase("ON")) {
            
            parser.read();
            entityName = parser.getCurrentToken();
            
            parser.read();
            if (".".equals(parser.getCurrentToken())) {
                schemaName = entityName;
                parser.read();
                entityName = parser.getCurrentToken();
                parser.read();
            }
        } else {
            throw new SQLException("Missing granting object");
        }
        DDLParserUtils.match(parser, "TO");
        user = DDLParserUtils.readIdentifier(parser, "username");
        if (parser.getCurrentToken().equalsIgnoreCase("WITH")) {
            DDLParserUtils.readAndMatch(parser, "GRANT");
            DDLParserUtils.match(parser, "OPTION");
            grantOption = true;
        }
		EntityPrivilege entityPrivilege = new EntityPrivilege(schemaName + "."
				+ entityName, read, write, exec, grantOption, all);

        SGrant s = new SGrant(user, entityPrivilege);
        if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
            parser.read();
            s.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
        }
        if (parser.getCurrentToken().equals("/")) {
            DDLParserUtils.readAndMatch(parser, "*");
            if (!"clear_client_conn_pool".equalsIgnoreCase(parser
                    .getCurrentToken()))
                throw new SQLException("Invalid option '"
                        + parser.getCurrentToken() + "'!");
            parser.read();
            s.setClearClientConnPool(true);
            DDLParserUtils.match(parser, "*");
            DDLParserUtils.match(parser, "/");
        }

        return s;
    }

    private SRevoke parseRevoke() throws SQLException {
        String schemaName = "";
        String entityName = "";
        boolean read = false;
        boolean write = false;
        boolean exec = false;
        boolean grantOption = false;
        boolean all = false;
        String user = "";

        parser.read();
        while (true) {
            if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.READ.name())) {
                read = true;
                parser.read();
            } else if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.WRITE.name())) {
                write = true;
                parser.read();
            } else if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.EXEC.name())) {
                exec = true;
                parser.read();
            } else if (parser.getCurrentToken().equalsIgnoreCase(
                    EntityPrivilegeType.ALL.name())) {
                DDLParserUtils.readAndMatch(parser, "PRIVILEGES");
                read = write = grantOption = false;
                all = true;
            } else if (parser.getCurrentToken().equalsIgnoreCase("GRANT")) {
                DDLParserUtils.readAndMatch(parser, "OPTION");
                grantOption = true;
            } else
                throw new SQLException("Invalid privilege��"
                        + parser.getCurrentToken());
            if (parser.getCurrentToken().equals(","))
                parser.read();
            else if (parser.getCurrentToken().equalsIgnoreCase("ON"))
                break;
        }
        if (parser.getCurrentToken().equalsIgnoreCase("ON")) {
            
            parser.read();
            entityName = parser.getCurrentToken();
            
            parser.read();
            if (".".equals(parser.getCurrentToken())) {
                schemaName = entityName;
                parser.read();
                entityName = parser.getCurrentToken();
                parser.read();
            }
        } else {
            throw new SQLException("Missing revoking object");
        }
        DDLParserUtils.match(parser, "FROM");
        user = DDLParserUtils.readIdentifier(parser, "username");

		EntityPrivilege entityPrivilege = new EntityPrivilege(schemaName + "."
				+ entityName, read, write, exec, grantOption, all);

        SRevoke s = new SRevoke(user, entityPrivilege);
        if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
            parser.read();
            s.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
        }
        if (parser.getCurrentToken().equals("/")) {
            DDLParserUtils.readAndMatch(parser, "*");
            if (!"clear_client_conn_pool".equalsIgnoreCase(parser
                    .getCurrentToken()))
                throw new SQLException("Invalid option '"
                        + parser.getCurrentToken() + "'!");
            parser.read();
            s.setClearClientConnPool(true);
            DDLParserUtils.match(parser, "*");
            DDLParserUtils.match(parser, "/");
        }
        return s;
    }

	private SShowGrants parseShowGrants() throws SQLException {
		parser.read();
		String user = null;
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			user = DDLParserUtils.readIdentifier(parser, "username");
		}
		return new SShowGrants(user);
	}

	private SSetPassword parseSetPassword() throws SQLException {
		parser.read();
		String password = DDLParserUtils.readTokenOrValue(parser, false);
		String user = null;
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			user = DDLParserUtils.readIdentifier(parser, "username");
		}
		return new SSetPassword(user, password);
	}

	private SSetQuota parseSetQuota() throws SQLException {
		String user = null;
		parser.read();
		if (parser.readIf("OF"))
			user = DDLParserUtils.readIdentifier(parser, "username");
		DDLParserUtils.match(parser, "TO");
		long quota = readLong("QUOTA");
		if (quota <= 0)
			throw new SQLException("Invalid quota: " + quota + ", must be a positive number.");
		SSetQuota s = new SSetQuota(user, quota);
		if (parser.readIf("SLAVEQUOTA")) {
			DDLParserUtils.match(parser, "TO");
			s.setSlaveQuota(readLong("SlaveQuota"));
		}
		return s;
	}

	private SSetDesc parseSetDesc() throws SQLException {
		String user = null;
		parser.read();
		if (parser.readIf("OF"))
			user = DDLParserUtils.readIdentifier(parser, "username");
		String desc = DDLParserUtils.readTokenOrValue(parser, false);
		return new SSetDesc(user, desc);
	}

	private SSetRole parseSetRole() throws SQLException {
		String user = null;
		parser.read();
		if (parser.readIf("OF"))
			user = DDLParserUtils.readIdentifier(parser, "username");
		String str = DDLParserUtils.readTokenOrValue(parser, false);
		String[] roleStr = str.split(",");
		if (roleStr.length != 4)
			throw new SQLException("Invalid role form. Eg. '1,0,0,0'.");
		int[] tmp = new int[4];
		for (int i = 0; i < 4; i++) {
			tmp[i] = Integer.parseInt(roleStr[i]);
			if (tmp[i] < DBAPrivilege.RIGHT_NULL || tmp[i] > DBAPrivilege.RIGHT_WRITE)
				throw new SQLException("Invalid role right value '" + tmp[i] + "'.");
		}
		int[] roles = new int[4];
		roles[DBAPrivilege.ROLE_DEPLOY] = tmp[0];
		roles[DBAPrivilege.ROLE_MAINTAIN] = tmp[1];
		roles[DBAPrivilege.ROLE_ANALYZE] = tmp[2];
		roles[DBAPrivilege.ROLE_USERADM] = tmp[3];
		return new SSetRole(user, roles);
	}

	private SShowQuota parseShowQuota() throws SQLException {
		parser.read();
		String user = null;
		if (parser.readIf("OF"))
			user = DDLParserUtils.readIdentifier(parser, "username");
		return new SShowQuota(user);
	}

	private SAddHost parseAddHost() throws SQLException {
		parser.read();
		List<String> hosts = readHosts("TO");
		int type = readHostType();
		DDLParserUtils.readAndMatch(parser, "OF");
		String user = DDLParserUtils.readIdentifier(parser, "username");
		return new SAddHost(user, type, hosts);
	}

	private SRemoveHost parseRemoveHost() throws SQLException {
		parser.read();
		List<String> hosts = readHosts("FROM");
		int type = readHostType();
		DDLParserUtils.readAndMatch(parser, "OF");
		String user = DDLParserUtils.readIdentifier(parser, "username");
		return new SRemoveHost(user, type, hosts);
	}

	private SAddMonitorClients parseAddMonitorClients() throws SQLException {
		parser.read();
		Clients clients = readClients();
		return new SAddMonitorClients(clients);
	}

	private SRemoveMonitorClients parseRemoveMonitorClients() throws SQLException {
		parser.read();
		Clients clients = readClients();
		return new SRemoveMonitorClients(clients);
	}

	private SShowOps parseShowOps() throws SQLException {
		Clients clients = null;
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SShowOps(clients);
	}

	private SResetOps parseResetOps() throws SQLException {
		Clients clients = null;
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SResetOps(clients);
	}

	private SShowResourceStatus parseShowResourceStatus() throws SQLException {
		Clients clients = null;
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SShowResourceStatus(clients);
	}

	private SShowResource parseShowActive() throws SQLException {
		Pair<ResourceType, Clients> p = readOfTypeForClients();
		return new SShowResource(p.getFirst(), p.getSecond(), ResourceStatusType.ACTIVE);
	}

	private SShowResource parseShowIc() throws SQLException {
		Pair<ResourceType, Clients> p = readOfTypeForClients();
		return new SShowResource(p.getFirst(), p.getSecond(), ResourceStatusType.IMP_CLOSE);
	}

	private SShowResource parseShowHc() throws SQLException {
		Pair<ResourceType, Clients> p = readOfTypeForClients();
		return new SShowResource(p.getFirst(), p.getSecond(), ResourceStatusType.HANG_CLOSE);
	}

	private SShowDbnConn parseShowDbnConn() throws SQLException {
		Clients clients = null;
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SShowDbnConn(clients);
	}

	private SShowActiveDbnConn parseShowActiveDbnConn() throws SQLException {
		Clients clients = null;
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SShowActiveDbnConn(clients);
	}

	private SShowClientStatus parseShowClientStatus() throws SQLException {
		Clients clients = null;
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SShowClientStatus(clients);
	}

	private SShowClientTrace parseShowClientTrace() throws SQLException {
		Clients clients = null;
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SShowClientTrace(clients);
	}

	private SShowClientActiveConnTrace parseShowClientActiveConnTrace() throws SQLException {
		Clients clients = null;
		DDLParserUtils.readAndMatch(parser, "TRACE");
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SShowClientActiveConnTrace(clients);
	}

	private SKillHangingConn parseKillHangingConn() throws SQLException {
		int minutes = -1;
		Clients clients = null;
		parser.read();
		if (parser.getCurrentTokenType() == SQLLexParser.VALUE) {
			minutes = Integer.parseInt(parser.getCurrentValue().toString());
			parser.read();
		}
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new SKillHangingConn(minutes, clients);
	}

	private SKillThread parseKillThread() throws SQLException {
		SKillThread s = new SKillThread();
		if (parser.readIf("HOST")) {
			String host = DDLParserUtils.readTokenOrValue(parser, false);
			s.setHost(host);
			return s;
		} else if (parser.readIf("INFO")) {
			String info = DDLParserUtils.readTokenOrValue(parser, false);
			s.setInfo(info);
			return s;
		} else
			throw new SQLException("Invalid opt '" + parser.getCurrentToken()
					+ "', must be HOST or INFO.");
	}

	private SStopClients parseStopClients() throws SQLException {
		parser.read();
		if (parser.getCurrentToken().equals(";"))
			throw new SQLException("No clients specified.");
		Clients clients = readClients();
		return new SStopClients(clients);
	}

	private SStartClients parseStartClients() throws SQLException {
		parser.read();
		Clients clients = readClients();
		return new SStartClients(clients);
	}

	private SSetShowStatResults parseSetShowStatResults() throws SQLException {
		List<Integer> taskIds = new ArrayList<Integer>();
		List<List<Integer>> resultIds = new ArrayList<List<Integer>>();
		do {
			parser.read();
			List<Integer> resultId = readIntList(",", new String[] { "OF", ";" },
					"ID of statistic result");
			DDLParserUtils.match(parser, "OF");
			int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
			taskIds.add(taskId);
			resultIds.add(resultId);
		} while (parser.getCurrentToken().equals(","));
		if (!parser.getCurrentToken().equalsIgnoreCase(";"))
			throw new SQLException("Syntax error, expected ';', but was '" + parser.getCurrentToken()
					+ "'");
		return new SSetShowStatResults(taskIds, resultIds);
	}

	private SCreateStatTask parseCreateStatTask() throws SQLException {
		parser.read();
		if (parser.readIf("DO")) {
			StatTask statTask = new StatTask(0, 0, 0, new LinkedList<Integer>());
			List<String> actions = readStringList(",", new String[] { "ON" });
			for (String action : actions) {
				if (action.equalsIgnoreCase("OPS"))
					statTask.setStatOp(true);
				else if (action.equalsIgnoreCase("DDB"))
					statTask.setStatDBStmt(true);
				else if (action.equalsIgnoreCase("COLUMN"))
					statTask.setStatColumn(true);
				else if (action.equalsIgnoreCase("INDEX"))
					statTask.setStatIndex(true);
				else if (action.equalsIgnoreCase("MCV"))
					statTask.setStatMCV(true);
				else if (CmdHelper.isCommand(action, "BUCKET OF")) {
					SQLLexParser p2 = SQLLexParser.getLexParser(action);
					p2.read();
					p2.read("BUCKET");
					p2.read("OF");
					p2.read("(");
					List<String> policies = new LinkedList<String>();
					while (!p2.getCurrentToken().equals(")")) {
						policies.add(p2.getCurrentToken());
						p2.read();
					}
					statTask.setStatBucket(true);
					statTask.setPolicyList(policies);
				} else if (CmdHelper.isCommand(action, "MYSQL")) {
					statTask.setStatMysqlStmt(true);

					SQLLexParser p2 = SQLLexParser.getLexParser(action);
					p2.read();
					p2.read("MYSQL");
					if (p2.readIf("EXPLAIN")) {
						String explainPolicy = p2.getCurrentToken();
						if (explainPolicy.equalsIgnoreCase("disable"))
							statTask.setMysqlExplainPly(StatTask.MYSQL_EXPLAIN_DISABLED);
						else if (explainPolicy.equalsIgnoreCase("each"))
							statTask.setMysqlExplainPly(StatTask.MYSQL_EXPLAIN_EACH);
						else if (explainPolicy.equalsIgnoreCase("first"))
							statTask.setMysqlExplainPly(StatTask.MYSQL_EXPLAIN_FIRST);
						else
							throw new SQLException("Invalid MySQL explain policy: " + explainPolicy);
						p2.read();
					}
					if (p2.readIf("HS")) {
						String hsPolicy = p2.getCurrentToken();
						if (hsPolicy.equalsIgnoreCase("disable"))
							statTask.setMysqlHandlerPly(StatTask.MYSQL_HS_DISABLED);
						else if (hsPolicy.equalsIgnoreCase("each"))
							statTask.setMysqlHandlerPly(StatTask.MYSQL_HS_EACH);
						else if (hsPolicy.equalsIgnoreCase("sample")) {
							p2.read();
							if (p2.getCurrentTokenType() != SQLLexParser.VALUE
									|| !(p2.getCurrentValue() instanceof Integer))
								throw new SQLException("Sample interval must be a integer: "
										+ p2.getCurrentToken());
							int sampleInterval = (Integer) (p2.getCurrentValue());
							statTask.setMysqlHandlerPly(StatTask.MYSQL_HS_SAMPLE);
							statTask.setMysqlHandlerSample(sampleInterval);
						}
					}
				} else
					throw new SQLException("Invalid action: " + action);
			}

			DDLParserUtils.match(parser, "ON");
			Clients clients = readClients();
			return new SCreateStatTask(statTask, clients);
		} else if (parser.readIf("LIKE")) {
			int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
			return new SCreateStatTask(taskId);
		}
		return null;
	}

	private SDropStatTask parseDropStatTask() throws SQLException {
		parser.read();
		int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
		return new SDropStatTask(taskId);
	}

	private SStartStatTask parseStartStatTask() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "TASK");
		int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
		return new SStartStatTask(taskId);
	}

	private SStopStatTask parseStopStatTask() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "TASK");
		int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
		return new SStopStatTask(taskId);
	}

	private SGetStatResult parseGetStatResult() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "OF");
		int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
		SGetStatResult r = new SGetStatResult(taskId);
		if (parser.getCurrentToken().equalsIgnoreCase("DESC")) {
			DDLParserUtils.readAndMatch(parser, "BY");
			String desc = DDLParserUtils.readTokenOrValue(parser, false);
			r.setDesc(desc);
		}
		return r;
	}

	private SShowStatResults parseShowStatResults() throws SQLException {
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			List<Integer> taskIds = readIntList(",", new String[] { ";" }, "ID of statistic task");
			return new SShowStatResults(taskIds);
		} else
			return new SShowStatResults(null);
	}

	private SRemoveStatResult parseRemoveStatResult() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "RESULT");
		List<Integer> resultIds = readIntList(",", new String[] { "OF", ";" }, "ID of statistic result");
		DDLParserUtils.match(parser, "OF");
		int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
		return new SRemoveStatResult(taskId, resultIds);
	}

	private SAlterStatResult parseAlterStatResult() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "RESULT");
		int resultId = DDLParserUtils.readInt(parser, "ID of statistic result");
		DDLParserUtils.match(parser, "OF");
		int taskId = DDLParserUtils.readInt(parser, "ID of statistic task");
		DDLParserUtils.match(parser, "DESC");
		DDLParserUtils.match(parser, "BY");
		String desc = DDLParserUtils.readTokenOrValue(parser, false);
		return new SAlterStatResult(taskId, resultId, desc);
	}

	private SStat parseShowStat() throws SQLException {
		SStat.StatType statType;
		if (parser.getCurrentToken().equalsIgnoreCase("ddb"))
			statType = SStat.StatType.SHOW_STAT_DDB;
		else if (parser.getCurrentToken().equalsIgnoreCase("mysql"))
			statType = SStat.StatType.SHOW_STAT_MYSQL;
		else if (parser.getCurrentToken().equalsIgnoreCase("ops"))
			statType = SStat.StatType.SHOW_STAT_OPS;
		else if (parser.getCurrentToken().equalsIgnoreCase("bucket"))
			statType = SStat.StatType.SHOW_STAT_BUCKET;
		else if (parser.getCurrentToken().equalsIgnoreCase("explain"))
			statType = SStat.StatType.SHOW_STAT_EXPLAIN;
		else if (parser.getCurrentToken().equalsIgnoreCase("index"))
			statType = SStat.StatType.SHOW_STAT_INDEX;
		else if (parser.getCurrentToken().equalsIgnoreCase("column"))
			statType = SStat.StatType.SHOW_STAT_COLUMN;
		else if (parser.getCurrentToken().equalsIgnoreCase("mcv"))
			statType = SStat.StatType.SHOW_STAT_MCV;
		else if (parser.getCurrentToken().equalsIgnoreCase("table")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("memcached"))
				statType = SStat.StatType.SHOW_STAT_TABLE_MEMCACHED;
			else
				throw new SQLException("Invalid statistic type: SHOW STAT TABLE " 
						+ parser.getCurrentToken());
		} else if (parser.getCurrentToken().equalsIgnoreCase("extended")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("ddb"))
				statType = SStat.StatType.SHOW_STAT_EXTENDED_DDB;
			else if (parser.getCurrentToken().equalsIgnoreCase("mysql"))
				statType = SStat.StatType.SHOW_STAT_EXTENDED_MYSQL;
			else
				throw new SQLException("Invalid statistic type: " + parser.getCurrentToken());
		} else
			throw new SQLException("Invalid statistic type: " + parser.getCurrentToken());

		SStat s = new SStat(statType);

		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("where")) {
			parser.read();
			s
					.setWhere(DDLParserUtils.readString(parser, " ", new String[] { "group", "order", "limit", "into", "label",
							";" }));
		}
		if (parser.getCurrentToken().equalsIgnoreCase("group")) {
			DDLParserUtils.readAndMatch(parser, "by");
			s.setGroupBy(new HashSet<String>(readNameList(",", new String[] { "having", "order",
					"limit", "into", "label", ";" })));
		}
		if (parser.getCurrentToken().equalsIgnoreCase("having")) {
			parser.read();
			s.setHaving(DDLParserUtils.readString(parser, " ", new String[] { "order", "limit", "into", "label", ";" }));
		}
		if (parser.getCurrentToken().equalsIgnoreCase("order")) {
			DDLParserUtils.readAndMatch(parser, "by");
			s.setOrderBy(DDLParserUtils.readString(parser, " ", new String[] { "limit", "into", "label", ";" }));
		}
		if (parser.getCurrentToken().equalsIgnoreCase("limit")) {
			parser.read();
			s.setLimit(DDLParserUtils.readInt(parser, "LIMIT"));
		}
		if (parser.getCurrentToken().equalsIgnoreCase("into")) {
			DDLParserUtils.readAndMatch(parser, "outfile");
			s.setFile(DDLParserUtils.readTokenOrValue(parser, false));
		}
		if (parser.getCurrentToken().equalsIgnoreCase("label")) {
			DDLParserUtils.readAndMatch(parser, "by");
			if (s.getFile() != null)
				throw new SQLException("Can not specify 'into outfile' and 'label by' at the same time.");
			s.setLabelBy(readNameList(",", new String[] { ";" }));
		}
		return s;
	}

	private SShowStatAnalysis parseShowStatAnalysis() throws SQLException {
		parser.read();
		SShowStatAnalysis s = new SShowStatAnalysis();
		List<String> types = readStringList(",", new String[] { ";", "IN" });
		List<String> args = new LinkedList<String>();
		for (String type : types) {
			if (CmdHelper.isCommand(type, "forget bf"))
				s.setHasForgetBF(true);
			else if (CmdHelper.isCommand(type, "forget index on bf"))
				s.setHasForgetIndexOnBF(true);
			else if (CmdHelper.isCommand(type, "unused index"))
				s.setHasUnusedIndex(true);
			else if (CmdHelper.isCommand(type, "index advice", args, 1, true)) {
				s.setHasIndexAdvice(true);
				s.setIndexAdviceLengthLimit(Integer.parseInt(args.get(0)));
				args.clear();
			} else if (CmdHelper.isCommand(type, "lowcard index", args, 1, true)) {
				s.setHasLowCardIndex(true);
				s.setLowCard(Integer.parseInt(args.get(0)));
				args.clear();
			} else if (CmdHelper.isCommand(type, "using index", args, 2, true)) {
				s.setHasUsingIndex(true);
				s.setUsingIndexMaxAttrs(Integer.parseInt(args.get(0)));
				s.setUsingIndexMaxLength(Integer.parseInt(args.get(1)));
				args.clear();
			} else if (CmdHelper.isCommand(type, "deadlock ddb"))
				s.setHasDeadlockDdb(true);
			else if (CmdHelper.isCommand(type, "deadlock mysql"))
				s.setHasDeadlockMysql(true);
			else if (CmdHelper.isCommand(type, "ignored index"))
				s.setHasIgnoredIndex(true);
			else
				throw new SQLException("Invalid type: " + type);
		}
		return s;
	}

	private SDiffStat parseDiffStat() throws SQLException {
		parser.read();
		List<Integer> resultIds1 = readIntList(",", new String[] { ";", "OF" }, "ID of statistic result");
		DDLParserUtils.match(parser, "OF");
		int taskId1 = DDLParserUtils.readInt(parser, "ID of statistic task");
		if (resultIds1.size() == 1) {
			if (!parser.getCurrentToken().equalsIgnoreCase(","))
				throw new SQLException("No ID of the second statistic result");
			DDLParserUtils.match(parser, ",");
			List<Integer> resultIds2 = readIntList(",", new String[] { ";", "OF" },
					"ID of statistic result");
			DDLParserUtils.match(parser, "OF");
			int taskId2 = DDLParserUtils.readInt(parser, "ID of statistic task");
			if (resultIds2.size() != 1)
				throw new SQLException(
						"Syntax error, the right syntax is: DIFF STAT ((result_id1, result_id2 of task_id) | (result_id1 of task_id1, result_id2 of task_id2))");
			return new SDiffStat(resultIds1.get(0), taskId1, resultIds2.get(0), taskId2);
		} else if (resultIds1.size() > 2)
			throw new SQLException("Can only compare two results.");
		if (!parser.getCurrentToken().equals(";"))
			throw new SQLException("Syntax error, expect ';', but was '" + parser.getCurrentToken()
					+ "'");
		return new SDiffStat(resultIds1.get(0), taskId1, resultIds1.get(1), taskId1);
	}

	private SShowTableStat parseShowTableStat() throws SQLException {
		if (parser.getCurrentToken().equalsIgnoreCase("OF")) {
			parser.read();
			List<String> policies = readStringList(",", new String[] { ";", "IN" });
			if (policies.size() == 0)
				throw new SQLException("Input policy name like 'SHOW TABLE STAT OF policy_name'!");
			SShowTableStat s = new SShowTableStat(null);
			s.setPolicies(policies);
			return s;
		}
		List<String> tableNames = null;
		if (!parser.getCurrentToken().equals(";"))
			tableNames = readStringList(",", new String[] { ";", "IN" });
		return new SShowTableStat(tableNames);
	}

	private SCollectTableStat parseCollectTableStat() throws SQLException {
		parser.read();
		SCollectTableStat s;
		if (parser.getCurrentToken().equalsIgnoreCase("OF")) {
			parser.read();
			List<String> policies = readStringList(",", new String[] { ";", "/", "IN" });
			if (policies.size() == 0)
				throw new SQLException("Input policy name like 'COLLECT TABLE STAT OF policy_name'!");
			s = new SCollectTableStat(null);
			s.setPolicies(policies);
		} else {
			List<String> tableNames = null;
			if (!parser.getCurrentToken().equals(";"))
				tableNames = readStringList(",", new String[] { ";", "/", "IN" });
			s = new SCollectTableStat(tableNames);
		}
		if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
			parser.read();
			s.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
		}
		if (parser.getCurrentToken().equals("/")) {
			DDLParserUtils.readAndMatch(parser, "*");
			while (true) {
				if (parser.getCurrentToken().equalsIgnoreCase("ANALYZE_TABLE")) {
					s.setAnalyzeTable(true);
					parser.read();
				} else if (parser.getCurrentToken().equalsIgnoreCase("WITHOUT_NOTIFY_CLIENTS")) {
					s.setNotifyClients(false);
					parser.read();
				} else if (parser.getCurrentToken().equals("*")) {
					DDLParserUtils.readAndMatch(parser, "/");
					break;
				} else
					throw new SQLException("Invalid option: " + parser.getCurrentToken());
			}
		}
		return s;
	}

	private SAddBucketno parseAddBucketno() throws SQLException {
		SAddBucketno s = new SAddBucketno();
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("OF")) {
			parser.read();
			String policyName = DDLParserUtils.readString(parser, " ", new String[] { ";", "/", "IN" });
			s.setPolicyName(policyName);
		} else if (!parser.getCurrentToken().equals(";")) {
			String tableName = DDLParserUtils.readString(parser, " ", new String[] { ";", "/", "IN" });
			s.setTableName(tableName);
		} else
			throw new SQLException(
					"Syntax error, the right syntax is: ADD BUCKETNO (tbl_name | OF policy_name) []");
		if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
			parser.read();
			s.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
		}
		if (parser.getCurrentToken().equals("/")) {
			DDLParserUtils.readAndMatch(parser, "*");
			while (true) {
				if (parser.getCurrentToken().equalsIgnoreCase("WITH")) {
					DDLParserUtils.readAndMatch(parser, "LOCK");
					s.setWithLock(true);
				} else if (parser.getCurrentToken().equals("*")) {
					DDLParserUtils.readAndMatch(parser, "/");
					break;
				} else
					throw new SQLException("Invalid option: " + parser.getCurrentToken());
			}
		}
		return s;
	}

	private SUse parseUse() throws SQLException {
		parser.read();
		SUse s = new SUse();
		if (parser.readIf("SERIAL"))
			s.setSerial(true);
		if (parser.readIf("dbi"))
			s.setType(SUse.UseType.DBI);
		else if (parser.readIf("dba"))
			s.setType(SUse.UseType.DBA);
		else if (parser.readIf("sysdb"))
			s.setType(SUse.UseType.SYSDB);
		else if (parser.readIf("all"))
			s.setType(SUse.UseType.ALL);
		else if (parser.readIf("ddb")) {
			s.setType(SUse.UseType.DDB);
			s.setName(DDLParserUtils.readIdentifier(parser, "ddb name"));
		} else if (parser.getCurrentToken().equalsIgnoreCase("DBNS")) {
			DDLParserUtils.readAndMatch(parser, "FOR");
			if (parser.getCurrentToken().equalsIgnoreCase("TABLE"))
				s.setType(SUse.UseType.DBNS_FOR_TABLE);
			else if (parser.getCurrentToken().equalsIgnoreCase("POLICY"))
				s.setType(SUse.UseType.DBNS_FOR_POLICY);
			else
				throw new SQLException("Should be TABLE or POLICY, but was " + parser.getCurrentToken());
			parser.read();
			s.setName(DDLParserUtils.readIdentifier(parser, "name of table or policy"));
		} else {
			List<String> dbns = new ArrayList<String>();
			do {
				dbns.add(DDLParserUtils.getTokenOrValue(parser, false));
			} while (!nextToken(parser).equals(";"));
			s.setDbns(dbns);
			s.setType(SUse.UseType.DBNS);
		}
		
		
		return s;
	}

	private SAlterGlobalPsCache parseAlterGlobalPsCache() throws SQLException {
		DDLParserUtils.match(parser, "SET");
		SAlterGlobalPsCache s = new SAlterGlobalPsCache();
		while (true) {
			if (parser.getCurrentToken().equalsIgnoreCase("prepare_param_limit")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setPrepareParamLimit(DDLParserUtils.readInt(parser, "prepare_param_limit"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("cache_sql_size_limit")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setCacheSqlSizeLimit(readAllInt("cache_sql_size_limit"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("parseTree_cache_size")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setParseTreeCacheSize(readAllInt("parseTree_cache_size"));
			} else
				throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
			if (!parser.getCurrentToken().equals(","))
				break;
			parser.read();
		}
		return s;
	}

	private SAlterClientPsCache parseAlterClientPsCache() throws SQLException {
		long clientID = readLong("client_id");
		DDLParserUtils.match(parser, "SET");
		SAlterClientPsCache s = new SAlterClientPsCache(clientID);
		while (true) {
			if (parser.getCurrentToken().equalsIgnoreCase("cache_sql_size_limit")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setCacheSqlSizeLimit(readAllInt("cache_sql_size_limit"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("parseTree_cache_size")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setParseTreeCacheSize(readAllInt("parseTree_cache_size"));
			} else
				throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
			if (!parser.getCurrentToken().equals(","))
				break;
			parser.read();
		}
		return s;
	}

	private SSetTableIdAssignment parseSetTableIdAssignment() throws SQLException {
		String tblName = DDLParserUtils.readIdentifier(parser, "table name");
		SSetTableIdAssignment s = new SSetTableIdAssignment(tblName);
		while (true) {
			if (parser.getCurrentToken().equalsIgnoreCase("startid")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setStartID(readLong("startid"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("remainid")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setRemainID(readLong("remainid"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("assigncount")) {
				DDLParserUtils.readAndMatch(parser, "=");
				s.setAssignCount(readLong("assigncount"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("notifyclient")) {
				DDLParserUtils.readAndMatch(parser, "=");
				boolean notify = DDLParserUtils.readBoolean(parser);
				s.setNotifyClient(notify);
			} else
				throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
			if (!parser.getCurrentToken().equals(","))
				break;
			parser.read();
		}
		planType = PlanType.SCHEMA_MODIFY;
		return s;
	}

	private SChangeTablePolicy parseChangeTablePly() throws SQLException {
		String tblName = DDLParserUtils.readIdentifier(parser, "table name");
		String plyName = DDLParserUtils.readIdentifier(parser, "policy name");
		planType = PlanType.SCHEMA_MODIFY;
		return new SChangeTablePolicy(tblName, plyName);
	}

	private SChangeTableModel parseChangeTableModel() throws SQLException {
		String tblName = DDLParserUtils.readIdentifier(parser, "table name");
		String mdlName = "";
		mdlName = DDLParserUtils.readTokenOrValue(parser, false);
		planType = PlanType.SCHEMA_MODIFY;
		return new SChangeTableModel(tblName, mdlName);
	}

	private SChangeColumnComment parseChangeColumnComment() throws SQLException {
		String tblName = DDLParserUtils.readIdentifier(parser, "table name");
		String clnName = DDLParserUtils.readIdentifier(parser, "column name");
		if (!parser.getCurrentToken().equals("'"))
			throw new SQLException("Comment string is expected in '' ");
		String newComment = DDLParserUtils.getTokenOrValue(parser, false);
		parser.read();
		planType = PlanType.SCHEMA_MODIFY;
		return new SChangeColumnComment(tblName, clnName, newComment);
	}

	private SChangePolicyComment parseChangePolicyComment() throws SQLException {
		String plyName = DDLParserUtils.readIdentifier(parser, "policy name");
		if (!parser.getCurrentToken().equals("'"))
			throw new SQLException("Comment string is expected in ' ");
		String newComment = DDLParserUtils.getTokenOrValue(parser, false);
		parser.read();
		planType = PlanType.SCHEMA_MODIFY;
		return new SChangePolicyComment(plyName, newComment);
	}

	private SSetDbnDirty parseSetDbnDirty() throws SQLException {
		List<String> names = readStringList(",", new String[] { ";", "TRUE", "FALSE" });
		boolean dirty = DDLParserUtils.readBoolean(parser);
		SSetDbnDirty s = new SSetDbnDirty(names, dirty);
		if (parser.getCurrentToken().equalsIgnoreCase("WITH")) {
			if (!dirty)
				throw new SQLException(
						"'False' dirty will set all tables undirty, should not specified policies.");
			parser.read();
			DDLParserUtils.match(parser, "POLICY");
			List<String> plyNames = readStringList(",", new String[] { ";" });
			Set<String> plys = new HashSet<String>();
			plys.addAll(plyNames);
			s.setDirtyPlys(plys);
		}
		planType = PlanType.SCHEMA_MODIFY;
		return s;
	}
	
	private SSetSlaveAutoSwitch parseSetSlaveAutoSwitch() throws SQLException {
		String slaveName = DDLParserUtils.readIdentifier(parser, "slave name");
		boolean isAutoSwitch = DDLParserUtils.readIdentifier(parser, "isAutoSwitch").equalsIgnoreCase("TRUE");
		return new SSetSlaveAutoSwitch(slaveName, isAutoSwitch);
	}

	private SSlicedMassUpdate parseSliceMassUpdate(String sql) throws SQLException {
		
		parser.read();
		SSlicedMassUpdate r = new SSlicedMassUpdate();
		String index = null;
		while (parser.getCurrentToken().equals("-")) {
			parser.read();
			if (parser.getCurrentToken().equals("u")) {
				parser.read();
				r.setUser(DDLParserUtils.readIdentifier(parser, "user"));
			} else if (parser.getCurrentToken().equals("p")) {
				parser.read();
				r.setPassword(DDLParserUtils.readQuoteValue(parser, "password"));
			} else if (parser.getCurrentToken().equals("-")) {
				parser.read();
				if (parser.getCurrentToken().equals("start")) {
					r.setStart(readScopeValue(sql));
				} else if (parser.getCurrentToken().equals("end")) {
					r.setEnd(readScopeValue(sql));
				} else if (parser.getCurrentToken().equals("index")) {
					parser.read();
					index = parser.getCurrentToken();
					parser.read();
				} else if (parser.getCurrentToken().equals("slice")) {
					DDLParserUtils.readAndMatch(parser, "-");
					DDLParserUtils.match(parser, "size");
					r.setSliceSize(DDLParserUtils.readInt(parser, "slice-size"));
				} else if (parser.getCurrentToken().equals("sleep")) {
					DDLParserUtils.readAndMatch(parser, "-");
					DDLParserUtils.match(parser, "ratio");
					String str = parser.getCurrentToken();
					parser.read();
					double ratio = 0.0;
					try {
						ratio = Double.parseDouble(str);
					} catch (NumberFormatException e) {
						throw new SQLException("sleep-ratio should be Double.");
					}
					if (ratio <= 0.0)
						throw new SQLException("sleep-ratio shoud be greater than zero.");
					r.setSleepRatio(ratio);
				} else if (parser.getCurrentToken().equals("duration")) {
					parser.read();
					long duration = readLong("duration");
					if (duration <= 0)
						throw new SQLException("duration shoud be greater than zero.");
					r.setDuration(duration);
				} else if (parser.getCurrentToken().equals("user")) {
					parser.read();
					r.setUser(DDLParserUtils.readIdentifier(parser, "user"));
				} else if (parser.getCurrentToken().equals("password")) {
					parser.read();
					r.setPassword(DDLParserUtils.readQuoteValue(parser, "password"));
				} else if (parser.getCurrentToken().equals("delete")) {
					parser.read();
					r.setAction("delete");
				} else if (parser.getCurrentToken().equals("update")) {
					parser.read();
					r.setAction("update");
					r.setSetClause(DDLParserUtils.readQuoteValue(parser, "set clause"));
				} else
					throw new SQLException("Invalid option: --" + parser.getCurrentToken());
			} else
				throw new SQLException("Invalid option: -" + parser.getCurrentToken());
		}
		r.setTable(DDLParserUtils.readIdentifier(parser, "table name"));
		r.setCondition(DDLParserUtils.readQuoteValue(parser, "condition"));
		if(index != null){
			r.setBaseIndex(index);
		}
		return r;
	}

	private SBash parseBash() throws SQLException {
		parser.read();
		return new SBash(DDLParserUtils.readTokenOrValue(parser, false));
	}

	private Statement parseCreateTrProc(String sql) throws SQLException {
		DbnType dbnType = parseDbnTypeByHint(sql);
		return parseByDbn(dbnType, sql);
	}

	private String getNextToken(StringTokenizer tokenizer) throws SQLException {
		boolean isAnnotation = false;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!isAnnotation) {
				if (token.startsWith(""))
					continue;
				if (token.startsWith("*/")) {
					isAnnotation = true;
					continue;
				} else
					return token;
			} else if (token.endsWith("*/")) {
				isAnnotation = false;
				continue;
			}
		}
		if (isAnnotation)
			throw new SQLException("Syntax error,  not matched.");
		return null;
	}


	private Statement parseDropPolicy() throws SQLException {
		parser.read();
		SDropPolicy s = new SDropPolicy(DDLParserUtils.readIdentifier(parser, "policy name"));
		planType = PlanType.SCHEMA_MODIFY;
		return s;
	}

	private Statement parseDropTable() throws SQLException {
		parser.read();
		boolean exists = false;
		if (parser.readIf("IF")) {
			DDLParserUtils.match(parser, "EXISTS");
			exists = true;
		}
		SDropTable s = new SDropTable(DDLParserUtils.readIdentifier(parser, "table(view) name"));
		s.setExists(exists);
		planType = PlanType.SCHEMA_MODIFY;
		return s;
	}

	private Statement parseAlterTrigger(String sql) throws SQLException {
		parser.read();
		String triggerName = DDLParserUtils.readIdentifier(parser, "trigger name");
		if (parser.readIf("ADD")) {
			if (parser.readIf("DBN")) {
				List<String> dbns = readStringList(",", new String[] { ";" });
				SAlterTriggerAddDbn s = new SAlterTriggerAddDbn(triggerName);
				s.setDbns(dbns);
				return s;
			} else
				throw new SQLException("Invalid option 'ADD " + parser.getCurrentToken() + "'.");
		} else if (parser.readIf("REMOVE")) {
			if (parser.readIf("DBN")) {
				List<String> dbns = readStringList(",", new String[] { ";" });
				SAlterTriggerRemoveDbn s = new SAlterTriggerRemoveDbn(triggerName);
				s.setDbns(dbns);
				return s;
			} else
				throw new SQLException("Invalid option 'REMOVE " + parser.getCurrentToken() + "'.");
		} else if (parser.readIf("SET")) {
			if (parser.readIf("DESC")) {
				String desc = DDLParserUtils.readTokenOrValue(parser, false);
				SAlterTriggerSetDesc s = new SAlterTriggerSetDesc(triggerName);
				s.setDesc(desc);
				return s;
			} else if (parser.readIf("ENABLED")) {
				String enabled = DDLParserUtils.readTokenOrValue(parser, false);
				SAlterTriggerSetEnabled s = new SAlterTriggerSetEnabled(triggerName);
				s.setEnabled(enabled.equalsIgnoreCase("true"));
				return s;
			} else if (parser.readIf("SQL_DEFINE")) {
				
				DbnType dbnType = getDbnTypeByTrigger(triggerName);
				String create_stmt = sql.substring(parser.getLastParseIndex()).trim();
				SAlterTriggerSetSQL s = new SAlterTriggerSetSQL(triggerName);
				Statement stmt = parseByDbn(dbnType, create_stmt);
				if (!(stmt instanceof SCreateTrigger))
					throw new SQLException("Invalid create_trigger_stmt.");
				s.setCreateTrigger((SCreateTrigger) stmt);
				return s;
			} else
				throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
		} else
			throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
	}

	private Statement parseAlterProcedure(String sql) throws SQLException {
		parser.read();
		String spName = DDLParserUtils.readIdentifier(parser, "procedure name");
		if (parser.readIf("ADD")) {
			if (parser.readIf("DBN")) {
				List<String> dbns = readStringList(",", new String[] { ";" });
				SAlterProcedureAddDbn s = new SAlterProcedureAddDbn(spName);
				s.setDbns(dbns);
				return s;
			} else
				throw new SQLException("Invalid option 'ADD " + parser.getCurrentToken() + "'.");
		} else if (parser.readIf("REMOVE")) {
			if (parser.readIf("DBN")) {
				List<String> dbns = readStringList(",", new String[] { ";" });
				SAlterProcedureRemoveDbn s = new SAlterProcedureRemoveDbn(spName);
				s.setDbns(dbns);
				return s;
			} else
				throw new SQLException("Invalid option 'REMOVE " + parser.getCurrentToken() + "'.");
		} else if (parser.readIf("SET")) {
			if (parser.readIf("DESC")) {
				String desc = DDLParserUtils.readTokenOrValue(parser, false);
				SAlterProcedureSetDesc s = new SAlterProcedureSetDesc(spName);
				s.setDesc(desc);
				return s;
			} else if (parser.readIf("SQL_DEFINE")) {
				
				DbnType dbnType = getDbnTypeByProcedure(spName);
				String create_stmt = sql.substring(parser.getLastParseIndex()).trim();
				SAlterProcedureSetSQL s = new SAlterProcedureSetSQL(spName);
				Statement stmt = parseByDbn(dbnType, create_stmt);
				if (!(stmt instanceof SCreateProcedure))
					throw new SQLException("Invalid create_procedure_stmt.");
				s.setCreateProc((SCreateProcedure) stmt);
				return s;
			} else
				throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
		} else
			throw new SQLException("Invalid option '" + parser.getCurrentToken() + "'.");
	}


	
	private Statement parseAlterTrProcSql(String sql) throws SQLException {
		
		
		StringTokenizer tokenizer = new StringTokenizer(sql);
		if (!"ALTER".equalsIgnoreCase(getNextToken(tokenizer)))
			throw new SQLException("Syntax error, correct sql should be 'ALTER...'.");
		String token = getNextToken(tokenizer);
		if ("TRIGGER".equalsIgnoreCase(token)) {
			String triggerName = getNextToken(tokenizer, "trigger name");
			if (isNextToken(tokenizer, "SET")) {
				
				if (tokenizer.hasMoreTokens()) {
					String define = getNextToken(tokenizer);
					if (define.equalsIgnoreCase("SQL_DEFINE")) {
						
						DbnType dbnType = getDbnTypeByTrigger(triggerName);
						String create_stmt = sql.substring(sql.indexOf(define) + define.length()).trim();
						SAlterTriggerSetSQL s = new SAlterTriggerSetSQL(triggerName);
						Statement stmt = parseByDbn(dbnType, create_stmt);
						if (!(stmt instanceof SCreateTrigger))
							throw new SQLException("Invalid create_trigger_stmt.");
						s.setCreateTrigger((SCreateTrigger) stmt);
						return s;
					} else
						throw new SQLException("Invalid option '" + define + "'.");
				} else
					throw new SQLException("Syntax error, sql not completed.");
			}
		} else if ("PROCEDURE".equalsIgnoreCase(token)) {
			String spName = getNextToken(tokenizer, "procedure name");
			if (isNextToken(tokenizer, "SET")) {
				
				if (tokenizer.hasMoreTokens()) {
					String define = getNextToken(tokenizer);
					if (define.equalsIgnoreCase("SQL_DEFINE")) {
						
						DbnType dbnType = getDbnTypeByProcedure(spName);
						String create_stmt = sql.substring(sql.indexOf(define) + define.length()).trim();
						SAlterProcedureSetSQL s = new SAlterProcedureSetSQL(spName);
						Statement stmt = parseByDbn(dbnType, create_stmt);
						if (!(stmt instanceof SCreateProcedure))
							throw new SQLException("Invalid create_procedure_stmt.");
						s.setCreateProc((SCreateProcedure) stmt);
						return s;
					} else
						throw new SQLException("Invalid option '" + define + "'.");
				} else
					throw new SQLException("Syntax error, sql not completed.");
			}
		}
		return null;
	}

	private SOnlineMigrate parseOnlineMigrate() throws SQLException {
		int timeout = -1;
		while (parser.getCurrentToken().equals("-")) {
			parser.read();
			if (parser.getCurrentToken().equals("-")) {
				parser.read();
				if (parser.getCurrentToken().equals("rep_timeout")) {
					DDLParserUtils.readAndMatch(parser, "=");
					timeout = DDLParserUtils.readInt(parser, "Wait replication timeout");
				} else
					throw new SQLException("Invalid option: --" + parser.getCurrentToken());
			} else
				throw new SQLException("Invalid option: -" + parser.getCurrentToken());
		}
		DDLParserUtils.match(parser, "POLICY");
		List<String> policy = readStringList(",", new String[] { ";", "FROM" });
		DDLParserUtils.match(parser, "FROM");
		List<Pair<String, String>> srcDbs = readOnlineMigDB(true);
		DDLParserUtils.match(parser, "TO");
		List<Pair<String, String>> newDbs = readOnlineMigDB(false);
		DDLParserUtils.match(parser, "MIGSCALE");
		String migScaleStr = DDLParserUtils.readString(parser, " ", new String[] { ";" });
		int migScale = 0;
		try {
			migScale = Integer.parseInt(migScaleStr);
		} catch (NumberFormatException e) {
			throw new SQLException("migrate scale must be integer");
		}
		SOnlineMigrate s = new SOnlineMigrate(srcDbs, newDbs, migScale, policy);
		if (timeout > -1)
			s.setRepTimeout(timeout);
		planType = PlanType.DATA_MIGRATION;
		return s;
	}

	private List<Pair<String, String>> readOnlineMigDB(boolean isSource) throws SQLException {
		List<Pair<String, String>> dbs = new ArrayList<Pair<String, String>>();
		while (true) {
			String name = DDLParserUtils.readTokenOrValue(parser, false);
			String dir = "";
			if (parser.getCurrentToken().equalsIgnoreCase("WITH_DATA_DIR")) {
				parser.read();
				dir = DDLParserUtils.readTokenOrValue(parser, false);
			} else {
				if (!isSource) {
					throw new SQLException(
							"target migrate dbn must specify the data dir, use WITH_DATA_DIR");
				}
			}
			dbs.add(new Pair<String, String>(name, dir));
			if (parser.getCurrentToken().equals(",")) {
				parser.read();
				continue;
			} else
				break;
		}
		return dbs;
	}

	private SStartOnlineMigrate parseStartOnlineMigrate() throws SQLException {
		long id = readLong("ID of migration task");
		planType = PlanType.DATA_MIGRATION;
		return new SStartOnlineMigrate(id);
	}

	private SStartOnlineAlterTable parseStartOnlineAlterTable() throws SQLException {
		long id = readLong("ID of online alter table task");
		planType = PlanType.OTHER;
		return new SStartOnlineAlterTable(id);
	}

	private SSetOnlineAlterTable parseSetOnlineAlterTable() throws SQLException {
		long id = -1;
		int trunksize = -1;
		int sleeptime = -1;
		if (parser.getCurrentToken().equalsIgnoreCase("ID")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("=")) {
				parser.read();
			} else {
				throw new SQLException("Invalid alter table option: input string must be ID=");
			}
			String str = parser.getCurrentToken();
			try {
				id = Long.parseLong(str);
			} catch (NumberFormatException e) {
				throw new SQLException("Invalid alter table option, trunksize must be integer, input:"
						+ str);
			}
			parser.read();
		}
		if (parser.getCurrentToken().equalsIgnoreCase("TRUNKSIZE")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("=")) {
				parser.read();
			} else {
				throw new SQLException("Invalid alter table option: input string must be TRUNKSIZE=");
			}
			String str = parser.getCurrentToken();
			try {
				trunksize = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				throw new SQLException("Invalid alter table option, trunksize must be integer, input:"
						+ str);
			}
			parser.read();
		}
		if (parser.getCurrentToken().equalsIgnoreCase("SLEEPTIME")) {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("=")) {
				parser.read();
			} else {
				throw new SQLException("Invalid alter table option: input string must be SLEEPTIME=");
			}
			String str = parser.getCurrentToken();
			try {
				sleeptime = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				throw new SQLException("Invalid alter table option, trunksize must be integer, input:"
						+ str);
			}
			parser.read();
		}
		if (id == -1 || trunksize == -1 || sleeptime == -1)
			throw new SQLException("id, trunksize, sleeptime must all be specified");
		if (trunksize <= 0) {
			throw new SQLException("trunksize must be positive");
		}
		if (sleeptime < 0) {
			throw new SQLException("trunksize must not be negtive");
		}
		planType = PlanType.OTHER;
		return new SSetOnlineAlterTable(id, trunksize, sleeptime);
	}

	private SCancelOnlineAlterTable parseCancelOnlineAlterTable() throws SQLException {
		long id = readLong("ID of online alter table task");
		planType = PlanType.OTHER;
		return new SCancelOnlineAlterTable(id);
	}

	private SStopOnlineMigrate parseStopOnlineMigrate() throws SQLException {
		long id = readLong("ID of migration task");
		planType = PlanType.DATA_MIGRATION;
		return new SStopOnlineMigrate(id);
	}

	private SStopOnlineAlterTable parseStopOnlineAlterTable() throws SQLException {
		long id = readLong("ID of online alter table task");
		planType = PlanType.OTHER;
		return new SStopOnlineAlterTable(id);
	}

	private SDropOnlineMigrate parseDropOnlineMigrate() throws SQLException {
		List<Long> ids = readLongList(",", new String[] { ";" }, "ID of migration option");
		planType = PlanType.DATA_MIGRATION;
		return new SDropOnlineMigrate(ids);
	}

	private SDropOnlineAlterTable parseDropOnlineAlterTable() throws SQLException {
		List<Long> ids = readLongList(",", new String[] { ";" }, "ID of online alter table");
		planType = PlanType.OTHER;
		return new SDropOnlineAlterTable(ids);
	}

	private SShowOnlineMigrate parseShowOnlineMigrate() throws SQLException {
		SShowOnlineMigrate s = new SShowOnlineMigrate();
		if (parser.readIf("OF"))
			s.setTaskID(readLong("migration id"));
		return s;
	}

	private SShowOnlineAlterTable parseShowOnlineAlterTable() throws SQLException {
		SShowOnlineAlterTable s = new SShowOnlineAlterTable();
		if (parser.readIf("OF"))
			s.setTaskID(readLong("online alter table id"));
		return s;
	}

    private SSlowlogOperation parseCollectSlowlog() throws SQLException {
        Collection<String> nodeNameList = null;
        Date startDate = null;
        Date endDate = null;
        boolean dateIsRange = false;
        int dateRange = SSlowlogOperation.DATE_RANGE_DAY;
        int readMode = SSlowlogOperation.READ_ORDER;
        int resultShowMode = SSlowlogOperation.SHOW_TO_BOTH;
        String extraArgs = "-lt slow";

        
        if (parser.readIf("FOR")) {
            if (parser.readIf("DBNS")) {
                DDLParserUtils.match(parser, "(");
                List<String> rawNodeNames = this.readStringList(",",
                        new String[] { ";", ")" });
                DDLParserUtils.match(parser, ")");

                nodeNameList = new ArrayList<String>();
                for (String s : rawNodeNames) {
                    nodeNameList.add(s.replaceAll("'", ""));
                }
            } else {
                throw new SQLException("Invalid DBNS definition: "
                        + parser.getCurrentToken());
            }
        } else {
            throw new SQLException("Invalid DBNS definition: "
                    + parser.getCurrentToken(),
                    ", 'FOR DBNS' clause must be specified.");
        }

        
        if (parser.readIf("DATE")) {
            if (parser.readIf("BETWEEN")) {
                String startDateString = DDLParserUtils.readTokenOrValue(parser, false);
                if (!parser.readIf("AND")) {
                    throw new SQLException("Invalid DATE DEfinition, date "
                            + "should be seperated by AND token.");
                }
                String endDateString = DDLParserUtils.readTokenOrValue(parser, false);

                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyyMMdd HH:mm:ss");
                try {
                    startDate = sdf.parse(startDateString);
                    endDate = sdf.parse(endDateString);
                } catch (ParseException e) {
                    throw new SQLException("Invalid DATE format: "
                            + parser.getCurrentToken()
                            + ", it should be like 'yyyyMMdd HH:mm:ss'");
                }
            } else if (parser.readIf("RANGE")) {
                dateIsRange = true;
                if (parser.readIf("DAY")) {
                    dateRange = SSlowlogOperation.DATE_RANGE_DAY;
                } else if (parser.readIf("TWODAY")) {
                    dateRange = SSlowlogOperation.DATE_RANGE_2DAY;
                } else if (parser.readIf("WEEK")) {
                    dateRange = SSlowlogOperation.DATE_RANGE_WEEK;
                } else if (parser.readIf("MONTH")) {
                    dateRange = SSlowlogOperation.DATE_RANGE_MONTH;
                } else if (parser.readIf("YEAR")) {
                    dateRange = SSlowlogOperation.DATE_RANGE_YEAR;
                } else {
                    throw new SQLException("Invalid DATE RANGE: "
                            + parser.getCurrentToken(),
                            ", should be (DAY|TWODAY|WEEK|MONTH|YEAR).");
                }
            }
        } else {
            throw new SQLException("Invalid DATE definition: "
                    + parser.getCurrentToken(),
                    ", 'DATE' clause must be specified.");
        }

        
        if (parser.readIf("READ")) {
            if (parser.readIf("ORDER")) {
                readMode = SSlowlogOperation.READ_ORDER;
            } else if (parser.readIf("REVERSE")) {
                readMode = SSlowlogOperation.READ_REVERSE;
            } else if (parser.readIf("ORRE")) {
                readMode = SSlowlogOperation.READ_BOTH;
            } else {
                throw new SQLException("Invalid read order: "
                        + parser.getCurrentToken()
                        + ", it should be (ORDER|REVERSE|ORRE).");
            }
        }

        
        if (parser.readIf("SCREEN")) {
            if (parser.readIf("ONLY")) {
                resultShowMode = SSlowlogOperation.SHOW_TO_SCREEN;
            } else {
                throw new SQLException("Invalid persistence option: "
                        + parser.getCurrentToken()
                        + ", it should be [SCREEN ONLY|STORE ONLY].");
            }
        } else if (parser.readIf("STORE")) {
            if (parser.readIf("ONLY")) {
                resultShowMode = SSlowlogOperation.SHOW_TO_DB;
            } else {
                throw new SQLException("Invalid persistence option: "
                        + parser.getCurrentToken()
                        + ", it should be [SCREEN ONLY|STORE ONLY].");
            }
        } else {
            resultShowMode = SSlowlogOperation.SHOW_TO_BOTH;
        }

        
        int maxUserShow = 5;
        if (parser.readIf("MAX")) {
            if (parser.readIf("USER")) {
                maxUserShow = DDLParserUtils.readInt(parser, "Invalid MAX USER option: "
                        + parser.getCurrentToken() + ", must be integer.");
            } else {
                throw new SQLException("Invalid MAX USER option: "
                        + parser.getCurrentToken()
                        + ", should be MAX USER num.");
            }
        }

        
        if (parser.readIf("EXTRA")) {
            extraArgs = DDLParserUtils.readTokenOrValue(parser, false);
        }

        
        Calendar startCal = null;
        if (startDate != null) {
            startCal = Calendar.getInstance();
            startCal.setTime(startDate);
        }
        Calendar endCal = null;
        if (endDate != null) {
            endCal = Calendar.getInstance();
            endCal.setTime(endDate);
        }

        SSlowlogOperation statement = null;
        if (dateIsRange) {
            statement = new SSlowlogOperation(nodeNameList, dateRange,
                    readMode, maxUserShow, extraArgs, resultShowMode);
        } else {
            statement = new SSlowlogOperation(nodeNameList, startCal, endCal,
                    readMode, maxUserShow, extraArgs, resultShowMode);
        }

        return statement;
    }

    private SSlowlogHistory parseShowSlowlogHistory() throws SQLException {
        Collection<String> nodeNames = null;
        Date startDate = null;
        Date endDate = null;

        
        if (parser.readIf("FOR")) {
            if (parser.readIf("DBNS")) {
                DDLParserUtils.match(parser, "(");
                List<String> rawNodeNames = this.readStringList(",",
                        new String[] { ";", ")" });
                DDLParserUtils.match(parser, ")");

                nodeNames = new ArrayList<String>();
                for (String s : rawNodeNames) {
                    nodeNames.add(s.replaceAll("'", ""));
                }
            } else {
                throw new SQLException("Invalid DBNS definition: "
                        + parser.getCurrentToken());
            }
        }
        
        if (parser.readIf("DATE")) {
            if (parser.readIf("BETWEEN")) {
                String startDateString = DDLParserUtils.readTokenOrValue(parser, false);
                if (!parser.readIf("AND")) {
                    throw new SQLException("Invalid DATE DEfinition, date "
                            + "should be seperated by AND token.");
                }
                String endDateString = DDLParserUtils.readTokenOrValue(parser, false);

                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyyMMdd HH:mm:ss");
                try {
                    startDate = sdf.parse(startDateString);
                    endDate = sdf.parse(endDateString);
                } catch (ParseException e) {
                    throw new SQLException("Invalid DATE format: "
                            + parser.getCurrentToken()
                            + ", it should be like 'yyyyMMdd HH:mm:ss'");
                }
            }
        }

        
        Calendar startCal = null;
        if (startDate != null) {
            startCal = Calendar.getInstance();
            startCal.setTime(startDate);
        }
        Calendar endCal = null;
        if (endDate != null) {
            endCal = Calendar.getInstance();
            endCal.setTime(endDate);
        }
        SSlowlogHistory statement = new SSlowlogHistory(
                SSlowlogHistory.MODE_READ_RESULT, nodeNames,
                SSlowlogHistory.TIMEMODE_SOURCE, startCal, endCal);
        return statement;
    }
    
    
    private SShowNtseParam parseShowNtseParam() throws SQLException {
    	boolean isTable;
    	if (parser.readIf("TABLE"))
    		isTable = true;
    	else if (parser.readIf("COLUMN"))
    		isTable = false;
    	else
    		throw new SQLException("'TABLE' or 'COLUMN' are expected after 'NTSE'");
    	DDLParserUtils.match(parser, "PARAM");
    	DDLParserUtils.match(parser, "FOR");
    	return new SShowNtseParam(DDLParserUtils.readIdentifier(parser, "table name"), isTable);
    }

	
	
	

	public static void main(String[] args) {
		SPlan p = null;
		try {
			p = (SPlan) new DDLParser().parse("stop dbn 'db-25-1' 'reason';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(p);
		System.out.println(p.getPlanTime().getNextExeTime());
	}

	private SPlan parseAddPlan(String sql) throws SQLException {
		
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "name of plan");
		DDLParserUtils.match(parser, "START");
		PlanTime time = parsePlanTime();
		String job_sql = sql.substring(parser.getLastParseIndex()).trim();
		if (job_sql.startsWith("'"))
			job_sql = job_sql.substring(1).trim();

		List<PlanJob> planJobs = parsePlanJobs(job_sql);
		SPlan plan = new SPlan(name);
		plan.setPlanTime(time);
		plan.setPlanJobs(planJobs);
		return plan;
	}

	private SDropPlan parseDropPlan() throws SQLException {
		parser.read();
		String name = DDLParserUtils.readIdentifier(parser, "name of plan");
		return new SDropPlan(name);
	}

	private SPausePlan parsePausePlan() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "PLAN");
		String name = DDLParserUtils.readIdentifier(parser, "name of plan");
		return new SPausePlan(name);
	}

	private SResumePlan parseResumePlan() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "PLAN");
		String name = DDLParserUtils.readIdentifier(parser, "name of plan");
		return new SResumePlan(name);
	}

	private SAddPlanJob parseAddPlanJob(String sql) throws SQLException {
		parser.read();
		String job_sql = sql.substring(parser.getLastParseIndex()).trim();
		return new SAddPlanJob(parsePlanJob(job_sql, false));
	}

	private SDropPlanJob parseDropPlanJob() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "JOB");
		String name = DDLParserUtils.readIdentifier(parser, "name of plan");
		return new SDropPlanJob(name);
	}

	private SSetPlanTime parseSetPlanTime() throws SQLException {
		String cronExpr = null;
		if (parser.getCurrentToken().equals("'")) {
			cronExpr = (String) parser.getCurrentValue();
			parser.read();
		}
		Date st = null;
		Date et = null;
		Date d = new Date();
		if (parser.getCurrentToken().equalsIgnoreCase("FROM")) {
			parser.read();
			st = new Date(readTime(d.getTime()));
			d = st;
		}
		if (parser.getCurrentToken().equalsIgnoreCase("TO")) {
			parser.read();
			et = new Date(readTime(d.getTime()));
		}
		return new SSetPlanTime(cronExpr, st, et);
	}

	private SSetPlanNeedMail parseSetPlanNeedMail() throws SQLException {
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("true")) {
			parser.read();
			return new SSetPlanNeedMail(true);
		} else if (parser.getCurrentToken().equalsIgnoreCase("false")) {
			parser.read();
			return new SSetPlanNeedMail(false);
		} else
			throw new SQLException("Syntax error, needMail should be true of false, but was '"
					+ parser.getCurrentToken() + "'");
	}

	private PlanTime parsePlanTime() throws SQLException {
		SSetPlanTime s = parseSetPlanTime();
		CronExpression expr = null;
		try {
			expr = new CronExpression(s.getCronExpr());
		} catch (ParseException e) {
			throw new SQLException(e.getMessage());
		}
		Date st = s.getStartTime();
		if (st == null)
			st = new Date();
		Date et = s.getEndTime();
		PlanTime time = new PlanTime(expr, st, et);
		return time;
	}

	private SBackup parseBackup() throws SQLException {
		parser.read();
		BackupConfig config = new BackupConfig();
		while (parser.getCurrentToken().equalsIgnoreCase("-")) {
			parser.read();
			if (!parser.getCurrentToken().equalsIgnoreCase("-"))
				throw new SQLException("Options should starts with --");
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("exclude")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "myisam");
				config.setBackupMyisam(false);
			} else if (parser.getCurrentToken().equalsIgnoreCase("compress")) {
				parser.read();
				config.setCompress(true);
			} else if (parser.getCurrentToken().equalsIgnoreCase("execute")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "scripts");
				config.setRemoteCopy(true);
			} else if (parser.getCurrentToken().equalsIgnoreCase("clear")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "old");
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "data");
				config.setDelExpiredData(true);
				config.setExpiredDays(DDLParserUtils.readInt(parser, "expire days"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("sync")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "client");
				config.setSynch(true);
			} else
				throw new SQLException("Invalid option: " + parser.getCurrentToken());
		}

		if (parser.getCurrentToken().equals(";"))
			throw new SQLException("Please input 'all' or tableNames!");
		if (!parser.getCurrentToken().equalsIgnoreCase("ALL")) {
			List<String> tables = readStringList(",", new String[] { ";", "IN" });
			ArrayList<String> tableNameList = new ArrayList<String>();
			tableNameList.addAll(tables);
			config.setTableNameList(tableNameList);
		} else
			parser.read();

		return new SBackup(config);
	}

	private SExport parseExport() throws SQLException {
		
		DumpConfig config = new DumpConfig();
		while (parser.getCurrentToken().equalsIgnoreCase("-")) {
			parser.read();
			if (!parser.getCurrentToken().equalsIgnoreCase("-"))
				throw new SQLException("Options should starts with --");
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("csv")) {
				parser.read();
				config.setSql(false);
			} else if (parser.getCurrentToken().equalsIgnoreCase("compress")) {
				parser.read();
				config.setCompress(true);
			} else if (parser.getCurrentToken().equalsIgnoreCase("execute")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "scripts");
				config.setRemoteCopy(true);
			} else if (parser.getCurrentToken().equalsIgnoreCase("clear")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "old");
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "data");
				config.setDelExpiredData(true);
				config.setExpiredDays(DDLParserUtils.readInt(parser, "expire days"));
			} else if (parser.getCurrentToken().equalsIgnoreCase("ignore")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				if (parser.readIf("error"))
					config.setIgnoreDumpResult(true);
				else if (parser.readIf("check"))
					config.setIgnoreCheckResult(true);
				else
					throw new SQLException("Invalid options: --ignore-" + parser.getCurrentToken());
			} else if (parser.getCurrentToken().equalsIgnoreCase("where")) {
				parser.read();
				DDLParserUtils.match(parser, "=");
				config.setCondition(DDLParserUtils.readTokenOrValue(parser, false));
			} else if (parser.getCurrentToken().equalsIgnoreCase("fields")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				if (parser.readIf("terminated")) {
					DDLParserUtils.match(parser, "-");
					DDLParserUtils.match(parser, "by");
					DDLParserUtils.match(parser, "=");
					config.setFieldTerminate(DDLParserUtils.readTokenOrValue(parser, false));
				} else if (parser.readIf("enclosed")) {
					DDLParserUtils.match(parser, "-");
					DDLParserUtils.match(parser, "by");
					DDLParserUtils.match(parser, "=");
					config.setFieldEnclose(DDLParserUtils.readTokenOrValue(parser, false));
				} else
					throw new SQLException("Invalid fields options: --fields-"
							+ parser.getCurrentToken());
			} else if (parser.getCurrentToken().equalsIgnoreCase("lines")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "terminated");
				DDLParserUtils.match(parser, "-");
				DDLParserUtils.match(parser, "by");
				DDLParserUtils.match(parser, "=");
				config.setLineTerminate(DDLParserUtils.readTokenOrValue(parser, false));
			} else
				throw new SQLException("Invalid option: " + parser.getCurrentToken());
		}

		if (parser.getCurrentToken().equals(";"))
			throw new SQLException("Please input 'all' or tableNames!");
		if (!parser.getCurrentToken().equalsIgnoreCase("ALL")) {
			List<String> tables = readStringList(",", new String[] { ";", "IN" });
			ArrayList<String> tableNameList = new ArrayList<String>();
			tableNameList.addAll(tables);
			config.setTableList(tableNameList);
		} else
			parser.read();

		return new SExport(config);
	}

	private SExportSysdb parseExportSysdb() throws SQLException {
		DDLParserUtils.readAndMatch(parser, "TO");
		String dir = DDLParserUtils.readTokenOrValue(parser, false);
		return new SExportSysdb(dir);
	}

	private SCleanSysTables parseCleanSysTables() throws SQLException {
		SCleanSysTables s = null;
		do {
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("ALARM")) {
				parser.read();
				DDLParserUtils.match(parser, "BEFORE");
				long alarmTime = readBeforeTime(System.currentTimeMillis());
				if (null == s)
					s = new SCleanSysTables();
				s.setAlarmTime(alarmTime);
			} else if (parser.getCurrentToken().equalsIgnoreCase("DBN")) {
				parser.read();
				DDLParserUtils.match(parser, "BEFORE");
				long dbnTime = readBeforeTime(System.currentTimeMillis());
				if (null == s)
					s = new SCleanSysTables();
				s.setDbnTime(dbnTime);
			} else if (parser.getCurrentToken().equalsIgnoreCase("XA")) {
				parser.read();
				DDLParserUtils.match(parser, "BEFORE");
				long xaTime = readBeforeTime(System.currentTimeMillis());
				if (null == s)
					s = new SCleanSysTables();
				s.setXaTime(xaTime);
			} else
				throw new SQLException("Tables should be alarm or dbn or xa.");
		} while (parser.getCurrentToken().equals(","));



		return s;
	}

	private SSingleMigrate parseSingleMigrate() throws SQLException {
		if (parser.getCurrentToken().equalsIgnoreCase("WITH")) {
			parser.read();
			int id = DDLParserUtils.readInt(parser, "ID of migration operation");
			SSingleMigrate s = new SSingleMigrate(null);
			s.setTaskId(id);
			return s;
		}
		SSingleMigrate s = new SSingleMigrate(readMigTask());

		if (parser.getCurrentToken().equals("/")) {
			DDLParserUtils.readAndMatch(parser, "*");
			while (true) {
				if (parser.getCurrentToken().equalsIgnoreCase("ONLINE")) {
					parser.read();
					s.setOnline(true);
				} else if (parser.getCurrentToken().equalsIgnoreCase("OFFLINE")) {
					parser.read();
					s.setOnline(false);
				} else if (parser.getCurrentToken().equalsIgnoreCase("NO_ORDER")) {
					parser.read();
					s.setSelectOrder("NO_ORDER");
				} else if (parser.getCurrentToken().equalsIgnoreCase("ASCEND_ORDER")) {
					parser.read();
					s.setSelectOrder("ASCEND_ORDER");
				} else if (parser.getCurrentToken().equalsIgnoreCase("DESCEND_ORDER")) {
					parser.read();
					s.setSelectOrder("DESCEND_ORDER");
				} else if (parser.getCurrentToken().equals("*")) {
					DDLParserUtils.readAndMatch(parser, "/");
					break;
				} else
					throw new SQLException("Invalid option: " + parser.getCurrentToken());
			}
		}
		return s;
	}

	private SConcurrentMigrate parseConcurrentMigrate() throws SQLException {
		if (parser.getCurrentToken().equalsIgnoreCase("WITH")) {
			parser.read();
			List<Integer> ids = readIntList(",", new String[] { ";" }, "ID of migration option");
			SConcurrentMigrate s = new SConcurrentMigrate(null);
			s.setIds(ids);
			return s;
		}
		List<SMigTask> tasks = new ArrayList<SMigTask>();
		while (true) {
			tasks.add(readMigTask());
			if (parser.getCurrentToken().equals(",")) {
				parser.read();
				continue;
			} else
				break;
		}
		return new SConcurrentMigrate(tasks);
	}

	private SDropMigrate parseDropMigrate() throws SQLException {
		parser.read();
		List<Integer> ids = readIntList(",", new String[] { ";" }, "ID of migration option");
		return new SDropMigrate(ids);
	}

	private SShowMigrate parseShowMigrate() throws SQLException {
		parser.read();
		if (parser.getCurrentToken().equalsIgnoreCase("OF")) {
			parser.read();
			String type = DDLParserUtils.readIdentifier(parser, "type of migration");
			if (!"SINGLE".equalsIgnoreCase(type) && !"CONCURRENT".equalsIgnoreCase(type))
				throw new SQLException("Invalid type '" + type + "'");
			return new SShowMigrate(type);
		}
		return new SShowMigrate(null);
	}

	private SShowPartitions parseShowPartitions() throws SQLException {
		parser.read();
		String tableName = DDLParserUtils.readIdentifier(parser, "table name");
		SShowPartitions s = new SShowPartitions(tableName);
		if (parser.readIf("ON")) {
			String dbName = DDLParserUtils.readIdentifier(parser, "name of database node");
			s.setDbName(dbName);
		}
		return s;
	}

	private SAddPartitions parseAddPartitions() throws SQLException {
		parser.read();
		String tableName = DDLParserUtils.readIdentifier(parser, "table name");
		int count = DDLParserUtils.readInt(parser, "number of partition");
		return new SAddPartitions(tableName, count);
	}

	private SDropPartitions parseDropPartitions() throws SQLException {
		parser.read();
		String tableName = DDLParserUtils.readIdentifier(parser, "table name");
		int count = DDLParserUtils.readInt(parser, "number of partition");
		return new SDropPartitions(tableName, count);
	}

	private SShowLog parseShowLog() throws SQLException {
		parser.read();
		String fileName = "";
		int direction = LogFileReadDescriptor.DIRECTION_UP;
		int chunkSize = 4;
		int chunkCount = 1;
		while (parser.getCurrentToken().equalsIgnoreCase("-")) {
			parser.read();
			if (!parser.getCurrentToken().equalsIgnoreCase("-"))
				throw new SQLException("Options should starts with --");
			parser.read();
			if (parser.getCurrentToken().equalsIgnoreCase("direction")) {
				parser.read();
				DDLParserUtils.match(parser, "=");
				String d = DDLParserUtils.readIdentifier(parser, "direction of read");
				if (d.equalsIgnoreCase("TAIL"))
					direction = LogFileReadDescriptor.DIRECTION_UP;
				else if (d.equalsIgnoreCase("HEAD"))
					direction = LogFileReadDescriptor.DIRECTION_DOWN;
				else
					throw new SQLException("Invalid direction option '" + d + "'!");
				continue;
			} else if (parser.getCurrentToken().equalsIgnoreCase("chunk")) {
				parser.read();
				DDLParserUtils.match(parser, "-");
				if (parser.readIf("size")) {
					DDLParserUtils.match(parser, "=");
					chunkSize = DDLParserUtils.readInt(parser, "chunk size");
					if (chunkSize <= 0)
						throw new SQLException("Chunk size should >0, but was '" + chunkSize + "'");
				} else if (parser.readIf("count")) {
					DDLParserUtils.match(parser, "=");
					chunkCount = DDLParserUtils.readInt(parser, "chunk count");
					if (chunkCount <= 0)
						throw new SQLException("Chunk count should >0, but was '" + chunkCount + "'");
				} else
					throw new SQLException("Invalid options: --chunk-" + parser.getCurrentToken());
				continue;
			} else
				throw new SQLException("Invalid option: " + parser.getCurrentToken());
		}
		String type = DDLParserUtils.readIdentifier(parser, "type of log");
		if ("root".equalsIgnoreCase(type))
			fileName = "log/ddb_root.log";
		else if ("config".equalsIgnoreCase(type))
			fileName = "log/config.log";
		else if ("migrate".equalsIgnoreCase(type))
			fileName = "log/migration.log";
		else if ("maintain".equalsIgnoreCase(type))
			fileName = "log/maintain.log";
		else if ("alarm".equalsIgnoreCase(type))
			fileName = "log/alarm.log";
		else if ("stattask".equalsIgnoreCase(type))
			fileName = "log/stattask.log";
		else if ("xa".equalsIgnoreCase(type))
			fileName = "log/xa.log";
		else if ("request".equalsIgnoreCase(type))
			fileName = "log/request.log";
		else if ("plan".equalsIgnoreCase(type))
			fileName = "log/plan.log";
		else if ("replication".equalsIgnoreCase(type))
			fileName = "log/replication.log";
		else
			throw new SQLException("Invalid log type: " + type + " !");

		LogFileReadDescriptor fileDescriptor = new LogFileReadDescriptor(fileName, direction);
		fileDescriptor.setChunkCount(chunkCount);
		fileDescriptor.setChunkSize(chunkSize * 1024);
		return new SShowLog(fileDescriptor);
	}

	public RangePartition parseRangePartition(String sql) throws SQLException {
		
		sql = sql.replaceAll("`", ""); 
		sql = sql.replaceAll("\"", "");
		int pos = sql.toUpperCase().indexOf("PARTITION");
		if (-1 == pos)
			throw new SQLException("a not partitioned table!");
		String str = sql;
		str = str.substring(pos);

		parser = SQLLexParser.getLexParser(str);
		parser.read();
		DDLParserUtils.match(parser, "PARTITION");
		DDLParserUtils.match(parser, "BY");
		String type = DDLParserUtils.readIdentifier(parser, "type of partition");
		if (!"RANGE".equalsIgnoreCase(type))
			throw new SQLException("a not range partitioned table!");

		String field = readStringInBrackets(str);

		DDLParserUtils.match(parser, "(");
		TreeMap<String, String> partitions = new TreeMap<String, String>(); 
		while (true) {
			DDLParserUtils.match(parser, "PARTITION");
			String pname = DDLParserUtils.readIdentifier(parser, "type of partition");
			DDLParserUtils.match(parser, "VALUES");
			DDLParserUtils.match(parser, "LESS");
			DDLParserUtils.match(parser, "THAN");
			String value = "";
			if (parser.getCurrentToken().equalsIgnoreCase("MAXVALUE"))
				value = "MAXVALUE";
			else
				value = readStringInBrackets(str);
			partitions.put(value, pname);
			
			int lp = 1;
			while (!parser.parseOver()) {
				if (parser.getCurrentToken().equals("("))
					lp++;
				else if (parser.getCurrentToken().equals(")")) {
					if (lp == 1) 
						break;
					lp--;
				} else if (parser.getCurrentToken().equals(",") && lp == 1) { 
					parser.read();
					break;
				}
				parser.read();
			}
			if (parser.getCurrentToken().equals(")"))
				break;
			else
				continue;
		}
		DDLParserUtils.match(parser, ")");
		return new RangePartition(sql, field, partitions);
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

	
	
	

	private SMigTask readMigTask() throws SQLException {
		List<Integer> bucketnos = readIntList(",", new String[] { ";", "OF" }, "bucket no");
		DDLParserUtils.match(parser, "OF");
		String policy = DDLParserUtils.readIdentifier(parser, "policy name");
		DDLParserUtils.match(parser, "TO");
		String desdb = DDLParserUtils.readIdentifier(parser, "destination node");
		boolean reset = parser.readIf("WITH_RESET");
		return new SMigTask(bucketnos, policy, desdb, reset);
	}

	public List<PlanJob> parsePlanJobs(String job_sql) throws SQLException {
		job_sql = job_sql
				.replaceAll(
						"(,((\\s)*)JOB)|(,((\\s)*)job)|(,((\\s)*)Job)|(,((\\s)*)jOb)|(,((\\s)*)joB)|(,((\\s)*)JOb)|(,((\\s)*)JoB)|(,((\\s)*)jOB)",
						", JOB");

		List<String> jobNames = new ArrayList<String>();
		List<PlanJob> planJobs = new ArrayList<PlanJob>();
		int index = job_sql.toUpperCase().indexOf(", JOB");
		boolean isUseDbns = false;
		PlanJob job = null;
		while (index > 0) {
			String s = job_sql.substring(0, index).trim();
			job = parsePlanJob(s, isUseDbns);
			isUseDbns = job.getType() == PlanType.USE_DBNS;
			if (jobNames.contains(job.getName()))
				throw new SQLException("Job '" + job.getName() + "' already exists.");
			jobNames.add(job.getName());
			planJobs.add(job);
			job_sql = job_sql.substring(index + 2).trim();
			index = job_sql.toUpperCase().indexOf(", JOB");
		}
		job = parsePlanJob(job_sql, isUseDbns);
		if (jobNames.contains(job.getName()))
			throw new SQLException("Job '" + job.getName() + "' already exists");
		planJobs.add(job);
		return planJobs;
	}

	private PlanJob parsePlanJob(String sql, boolean useDbns) throws SQLException {
		if ((sql.toUpperCase().split("(;((\\s)*)JOB)")).length > 1)
			throw new SQLException("Jobs delimiter is ',', not ';'.");
		SQLLexParser p = SQLLexParser.getLexParser(sql);
		p.read();
		if (!"JOB".equalsIgnoreCase(p.getCurrentToken()))
			throw new SQLException("Syntax error, expect 'JOB', but was '" + p.getCurrentToken() + "'.");
		p.read();
		String name = "";
		try {
			name = p.readIdentifier();
		} catch (SQLException e) {
			throw new SQLException("Syntax error, you should specify name of job.");
		}
		String str = sql.substring(p.getLastParseIndex()).trim();
		String str_Up = str.toUpperCase();
		if (useDbns && !str_Up.startsWith("USE "))
			return new PlanJob(str, name, PlanType.USE_DBNS);
		if (str_Up.startsWith("SELECT") || str_Up.startsWith("UPDATE") || str_Up.startsWith("DELETE")
				|| str_Up.startsWith("INSERT") || str_Up.equals("BEGIN") || str_Up.equals("COMMIT")
				|| str_Up.equals("ROLLBACK") || str_Up.equals("AUTOCOMMIT")
				|| str_Up.equals("CLOSE CONNECTIONS"))
			return new PlanJob(str, name, PlanType.DML);
		DDLParser ddl = new DDLParser(clusterInfo);
		Statement s = ddl.parse(str);
		if (ddl.planType == PlanType.INVALID)
			throw new SQLException("Unsupported plan type:" + s.getClass().getName());
		return new PlanJob(str, name, ddl.planType);
	}

	private long readTime(long baseTime) throws SQLException {
		if (parser.getCurrentTokenType() != SQLLexParser.VALUE
				&& !(parser.getCurrentValue() instanceof String))
			throw new SQLException("Time should be a string, but is: " + parser.getCurrentToken() + "");
		String timeStr = (String) parser.getCurrentValue();
		parser.read();
		long time = TimeUtils.formatTime2Long(timeStr);
		if (time > 0)
			return time;
		if (timeStr.startsWith("+")) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(baseTime);
			CmdWordReader cwr = new CmdWordReader(timeStr.substring(1));
			int num = Integer.parseInt(cwr.next());
			String scale = cwr.next();
			if ("m".equalsIgnoreCase(scale))
				c.add(Calendar.MINUTE, num);
			else if ("h".equalsIgnoreCase(scale))
				c.add(Calendar.HOUR, num);
			else
				throw new SQLException("Invalid time scale '" + scale + "', must be 'h' or 'm'.");
			return c.getTimeInMillis();
		} else {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			CmdWordReader cwr = new CmdWordReader(timeStr);
			int hour = Integer.parseInt(cwr.next());
			String s = cwr.next();
			if (s.equalsIgnoreCase("pm") || s.equalsIgnoreCase("am")) {
				if (s.equalsIgnoreCase("pm"))
					c.set(Calendar.HOUR, hour + 12);
				else
					c.set(Calendar.HOUR, hour);
				s = cwr.next();
			} else
				c.set(Calendar.HOUR, hour);
			if (s != null && s.equalsIgnoreCase("tomorrow"))
				c.add(Calendar.DATE, 1);
			return c.getTimeInMillis();
		}
	}

	private long readBeforeTime(long baseTime) throws SQLException {
		if (parser.getCurrentTokenType() != SQLLexParser.VALUE
				&& !(parser.getCurrentValue() instanceof String))
			throw new SQLException("Time should be a string, but is: " + parser.getCurrentToken() + "");
		String timeStr = (String) parser.getCurrentValue();
		parser.read();
		long time = TimeUtils.formatTime2Long(timeStr);
		if (time > 0)
			return time;
		if (timeStr.startsWith("-")) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(baseTime);
			CmdWordReader cwr = new CmdWordReader(timeStr.substring(1));
			int num = Integer.parseInt(cwr.next());
			String scale = cwr.next();
			if ("m".equalsIgnoreCase(scale))
				c.add(Calendar.MINUTE, 0 - num);
			else if ("h".equalsIgnoreCase(scale))
				c.add(Calendar.HOUR, 0 - num);
			else if ("d".equalsIgnoreCase(scale))
				c.add(Calendar.DAY_OF_MONTH, 0 - num);
			else
				throw new SQLException("Invalid time scale '" + scale + "', must be 'd' or 'h' or 'm'.");
			return c.getTimeInMillis();
		}
		throw new SQLException("Invalid time format '" + timeStr + "'");
	}

	private Pair<ResourceType, Clients> readOfTypeForClients() throws SQLException {
		if (!parser.getCurrentToken().equalsIgnoreCase("OF"))
			DDLParserUtils.readAndMatch(parser, "OF");
		else
			parser.read();
		ResourceType type = readResourceType();
		Clients clients = null;
		if (parser.getCurrentToken().equalsIgnoreCase("FOR")) {
			parser.read();
			clients = readClients();
		}
		return new Pair<ResourceType, Clients>(type, clients);
	}

	private ResourceType readResourceType() throws SQLException {
		ResourceType type;
		if (parser.getCurrentToken().equalsIgnoreCase("CONN"))
			type = ResourceType.CONNECTION;
		else if (parser.getCurrentToken().equalsIgnoreCase("STMT"))
			type = ResourceType.STATEMENT;
		else if (parser.getCurrentToken().equalsIgnoreCase("PS"))
			type = ResourceType.PREPAREDSTATEMENT;
		else
			throw new SQLException("Invalid resource type '" + parser.getCurrentToken()
					+ "', should be in (CONN, STMT, PS)");
		parser.read();
		return type;
	}

	private Clients readClients() throws SQLException {
		List<Integer> ids = new LinkedList<Integer>();
		List<String> names = new LinkedList<String>();
		List<String> ips = new LinkedList<String>();
		List<Pair<String, Integer>> ipPorts = new LinkedList<Pair<String, Integer>>();

		while (true) {
			String s = DDLParserUtils.readString(parser, "", new String[] { ",", ";" });
			try {
				int v = Integer.valueOf(s);
				ids.add(v);
			} catch (NumberFormatException e) {
				int commaPos = s.indexOf(':');
				if (commaPos > 0) {
					String ip = s.substring(0, commaPos);
					if (!Validator.isIpAddress(ip))
						throw new SQLException("'" + ip + "' is not a valid IP address.");
					int port = Validator.isPort(s.substring(commaPos + 1));
					if (port < 0)
						throw new SQLException("'" + s.substring(commaPos + 1)
								+ "' is not a valid port number.");
					ipPorts.add(new Pair<String, Integer>(ip, port));
				} else {
					if (Validator.isIpAddress(s))
						ips.add(s);
					else
						names.add(s);
				}
			}
			if (!parser.getCurrentToken().equals(","))
				break;
			parser.read();
		}

		return new Clients(ids, names, ips, ipPorts);
	}

	private List<String> readHosts(String stop) throws SQLException {
		List<String> hosts = new LinkedList<String>();
		while (!parser.getCurrentToken().equalsIgnoreCase(stop)) {
			String host = DDLParserUtils.readTokenOrValue(parser, false);
			if (!Validator.isIpWildcard(host))
				throw new SQLException("'" + host + "' is not a valid IP address or IP segment.");
			hosts.add(host);
			if (parser.getCurrentToken().equals(","))
				parser.read();
		}
		parser.read();
		return hosts;
	}

	private int readHostType() throws SQLException {
		int type = SAddHost.TYPE_CLIENT;
		if (parser.getCurrentToken().equalsIgnoreCase("DBA"))
			type = SAddHost.TYPE_DBA;
		else if (parser.getCurrentToken().equalsIgnoreCase("CLIENT"))
			type = SAddHost.TYPE_CLIENT;
		else if (parser.getCurrentToken().equalsIgnoreCase("QS"))
			type = SAddHost.TYPE_QS;
		else
			throw new SQLException("Invalid host type: " + parser.getCurrentToken());
		return type;
	}


	private List<String> readNameList(String delimiter, String[] stops) throws SQLException {
		List<String> l = new LinkedList<String>();
		Set<String> stopsSet = new HashSet<String>();
		for (String stop : stops)
			stopsSet.add(stop.toLowerCase());
		while (true) {
			String t = DDLParserUtils.readTokenOrValue(parser, false);
			l.add(t);
			if (parser.getCurrentToken().equalsIgnoreCase(delimiter)) {
				parser.read();
				continue;
			}
			if (stopsSet.contains(parser.getCurrentToken().toLowerCase()))
				break;
		}
		return l;
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

	private List<Integer> readIntList(String delimiter, String[] stops, String errMsg)
			throws SQLException {
		List<Integer> l = new LinkedList<Integer>();
		Set<String> stopsSet = new HashSet<String>();
		for (String stop : stops)
			stopsSet.add(stop.toLowerCase());
		while (true) {
			Integer i = DDLParserUtils.readInt(parser, errMsg);
			l.add(i);
			if (parser.getCurrentToken().equalsIgnoreCase(delimiter)) {
				parser.read();
				continue;
			}
			if (stopsSet.contains(parser.getCurrentToken().toLowerCase()))
				break;
		}
		return l;
	}


	private int readAllInt(String errMsg) throws SQLException {
		boolean isNegative = parser.readIf("-");
		if (parser.getCurrentTokenType() != SQLLexParser.VALUE)
			throw new SQLException("Syntax error, " + errMsg + " should be an integer, but was '"
					+ parser.getCurrentToken() + "'");
		if (!(parser.getCurrentValue() instanceof Integer))
			throw new SQLException("Syntax error, " + errMsg + " should be an integer, but was '"
					+ parser.getCurrentValue() + "'");
		Integer r = (Integer) parser.getCurrentValue();
		parser.read();
		if (isNegative && 0 != r)
			r = 0 - r;
		return r;
	}

	private long readLong(String errMsg) throws SQLException {
		if (parser.getCurrentTokenType() != SQLLexParser.VALUE)
			throw new SQLException("Syntax error, " + errMsg + " should be an integer, but was '"
					+ parser.getCurrentToken() + "'");
		long v;
		if (parser.getCurrentValue() instanceof Integer)
			v = (Integer) parser.getCurrentValue();
		else if (parser.getCurrentValue() instanceof Long)
			v = (Long) parser.getCurrentValue();
		else if (parser.getCurrentValue() instanceof BigDecimal)
			v = ((BigDecimal) parser.getCurrentValue()).longValue();
		else
			throw new SQLException("Syntax error, " + " should be an long, but was '"
					+ parser.getCurrentValue() + "'");
		parser.read();
		return v;
	}

	private List<Long> readLongList(String delimiter, String[] stops, String errMsg) throws SQLException {
		List<Long> l = new LinkedList<Long>();
		Set<String> stopsSet = new HashSet<String>();
		for (String stop : stops)
			stopsSet.add(stop.toLowerCase());
		while (true) {
			Long i = readLong(errMsg);
			l.add(i);
			if (parser.getCurrentToken().equalsIgnoreCase(delimiter)) {
				parser.read();
				continue;
			}
			if (stopsSet.contains(parser.getCurrentToken().toLowerCase()))
				break;
		}
		return l;
	}


	private String nextToken(SQLLexParser parser) throws SQLException {
		parser.read();
		return parser.getCurrentToken();
	}


	private String getNextToken(StringTokenizer tokenizer, String expected) throws SQLException {
		String str = getNextToken(tokenizer);
		if (null == str)
			throw new SQLException("Syntax error, expect '" + expected + "', but sql end.");
		return str;
	}


	private boolean isNextToken(StringTokenizer tokenizer, String expected) throws SQLException {
		if (tokenizer.hasMoreTokens() && getNextToken(tokenizer).equalsIgnoreCase(expected))
			return true;
		return false;
	}

	
	private DbnType parseDbnTypeByCreate(String token, String sql) throws SQLException {
		DbnType dbnType = null;
		if (token.equalsIgnoreCase("TABLE")) {
			String hint = getHint(sql);
			if (null == hint)
				throw new SQLException("Syntax err, hint must define at the end of sql");

			
			String plyName = null;
			hint = hint + ";";
			SQLLexParser plyParser = SQLLexParser.getLexParser(hint);
			plyParser.read();
			while (!(token = plyParser.getCurrentToken()).equals(";")) {
				if (token.equalsIgnoreCase("POLICY")
						|| token.equalsIgnoreCase("TABLEGROUP")) {
					DDLParserUtils.readAndMatch(plyParser, "=");
					plyName = plyParser.readIdentifier();
					break;
				}
				plyParser.read();
			}
			if (null == plyName)
				throw new SQLException("Syntax err, no policy is define is sql");

			
			dbnType = getDbnTypeByPolicy(plyName);
		} else if (token.equalsIgnoreCase("CLUSTER")) {
			
			dbnType = DbnType.Oracle;
			String hint = getHint(sql);
			if (null != hint) {
				
				SQLLexParser p = SQLLexParser.getLexParser(hint);
				DDLParserUtils.readAndMatch(p, "POLICY");
				DDLParserUtils.match(p, "=");
				String plyName = DDLParserUtils.readIdentifier(p, "policy name");
				DbnType plyType = getDbnTypeByPolicy(plyName);
				if (plyType != DbnType.Oracle)
					throw new SQLException("Syntax err, DbnCluster can only be created on Oracle policy");
			}
		} else if (token.equalsIgnoreCase("UNIQUE") || token.equalsIgnoreCase("INDEX")
				|| token.equalsIgnoreCase("BITMAP")) {
			boolean isBitMap = false;
			boolean isKey = false;
			if (token.equalsIgnoreCase("BITMAP")) {
				
				isBitMap = true;
				DDLParserUtils.readAndMatch(parser, "INDEX");
			} else {
				 if (token.equalsIgnoreCase("UNIQUE")) {
						parser.read();
						
						if (parser.getCurrentToken().equalsIgnoreCase("KEY")) {
							isKey = true;
						} else if (!parser.getCurrentToken().equalsIgnoreCase("INDEX"))
							throw new SQLException("Expect INDEX/KEY but was " + parser.getCurrentToken());
					}
				 parser.read();
			}
			DDLParserUtils.readIdentifier(parser, "index name");
			
			boolean isUsing = false;
			if (parser.readIf("USING")) {
				parser.read();
				isUsing = true;
			}
			DDLParserUtils.match(parser, "ON");
			String tblName = DDLParserUtils.readIdentifier(parser, "table name");
			
			if (tblName.equalsIgnoreCase("CLUSTER") && !parser.readIf("(")) {
				dbnType = DbnType.Oracle;
			} else {
				dbnType = getDbnTypeByTable(tblName);
			}
			if (isUsing && dbnType != DbnType.MySQL)
				throw new SQLException("'USING index_type' use only in MySQL not Oracle");
			if (isKey && dbnType != DbnType.MySQL)
				throw new SQLException("'CREATE UNIQUE KEY' use only in MySQL not Oracle");
			if (isBitMap && dbnType != DbnType.Oracle)
				throw new SQLException("'CREATE BITMAP INDEX' use only in Oracle not MySQL");

			
			if (dbnType == DbnType.Oracle && hasHint(sql))
				throw new SQLException("Online alter table is not supported in Oracle database");
		} else if (token.equalsIgnoreCase("VIEW")) {
			String plyName = null;
			boolean hasDbnType = false;
			
			String hint = getHint(sql);
			if (null != hint) {
				hint = hint + ";";
				SQLLexParser plyParser = SQLLexParser.getLexParser(hint);
				plyParser.read();
				while(!(token = plyParser.getCurrentToken()).equals(";")) {
					if (token.equalsIgnoreCase("POLICY") 
							|| token.equalsIgnoreCase("TABLEGROUP")) {
						DDLParserUtils.readAndMatch(plyParser, "=");
						plyName = plyParser.readIdentifier();
						break;
					}
					plyParser.read();
				}
			}
			if (null != plyName) { 
				dbnType = getDbnTypeByPolicy(plyName);
				hasDbnType = true;
			} else {
				
				while(!(token = parser.getCurrentToken()).equals(";")) {
					if (parser.getCurrentToken().equalsIgnoreCase("FROM")) {
						parser.read();
						parser.readIf("(");
						
						String tblName = DDLParserUtils.readIdentifier(parser, "table name");
						dbnType = getDbnTypeByTable(tblName);
						hasDbnType = true;
						break;
					}
					parser.read();
				}
			}
			if (!hasDbnType)
				throw new SQLException("Syntax err, no base table can be found in the sql clause for view");

		} else if (token.equalsIgnoreCase("PROCEDURE") || token.equalsIgnoreCase("TRIGGER")) {
			dbnType = parseDbnTypeByHint(sql);
		}

		return dbnType;
	}

	
	private DbnType parseDbnTypeByAlter(String token, String sql) throws SQLException {
		DbnType dbnType = null;
		planType = PlanType.SCHEMA_MODIFY;
		parser.read();
		if (token.equalsIgnoreCase("TABLE")) {
			String tblName = DDLParserUtils.readIdentifier(parser, "table name");
			dbnType = getDbnTypeByTable(tblName);
			if ((parser.readIf("ADD") || parser.readIf("DROP")) && parser.readIf("PARTITION")) {
				planType = PlanType.PARTITION;
			}
		} else if (token.equalsIgnoreCase("VIEW")) {
			String viewName = DDLParserUtils.readIdentifier(parser, "name of view");
			dbnType = getDbnTypeByTable(viewName);
		} else if (token.equalsIgnoreCase("CLUSTER")) {
			dbnType = DbnType.Oracle;
		}
		
		if (dbnType == DbnType.Oracle && isOnlineAlterTable(sql))
			throw new SQLException("Online alter table is not supported in Oracle database");

		return dbnType;
	}

	
	private Statement parseSqlByDrop(String token, String sql) throws SQLException {
		DbnType dbnType = null;
		Statement stmt = null;
		if (token.equalsIgnoreCase("TABLE") || token.equalsIgnoreCase("VIEW") ) {
			parser.read();
			boolean exit_test = false;
			
			if (parser.readIf("IF")) {
				DDLParserUtils.match(parser, "EXISTS");
				exit_test = true;
			}
			String tblName = DDLParserUtils.readIdentifier(parser, "TABLE|VIEW name");
			dbnType = getDbnTypeByTable(tblName);
			if (exit_test && dbnType != DbnType.MySQL)
				throw new SQLException("Syntax err, 'IF EXISTS' use only in MySQL not Oracle");

			SDropTable dropTable = new SDropTable(tblName);
			stmt = dropTable;
		} else if (token.equalsIgnoreCase("CLUSTER")) {
			parser.read();
			
			String cluName = DDLParserUtils.readIdentifier(parser, "cluster name");
			boolean dropTables = false;
			if (parser.readIf("INCLUDING")) { 
				DDLParserUtils.match(parser, "TABLES");
				if (parser.readIf("CASCADE"))
					DDLParserUtils.match(parser, "CONSTRAINTS");
				dropTables = true;
			}
			SDropCluster dropCluster = new SDropCluster(cluName, dropTables);
			stmt = dropCluster;
		} else if (token.equalsIgnoreCase("TRIGGER")){
			parser.read();
			boolean exists = false;
			
			if (parser.readIf("IF")) {
				DDLParserUtils.match(parser, "EXISTS");
				exists = true;
			}
			String trName = DDLParserUtils.readIdentifier(parser, "trigger name");
			dbnType = getDbnTypeByTrigger(trName);
			if (dbnType != DbnType.MySQL && exists)
				throw new SQLException("Syntax err, 'IF EXISTS' use only in MySQL not Oracle");

			SDropTrigger s = new SDropTrigger(trName);
			s.setExists(exists);
			stmt = s;
		} else if (token.equalsIgnoreCase("PROCEDURE")) {
			parser.read();
			boolean exists = false;
			
			if (parser.readIf("IF")) {
				DDLParserUtils.match(parser, "EXISTS");
				exists = true;
			}
			String proName = DDLParserUtils.readIdentifier(parser, "procedure name");
			dbnType = getDbnTypeByProcedure(proName);
			if (dbnType != DbnType.MySQL && exists)
				throw new SQLException("Syntax err, 'IF EXISTS' use only in MySQL not Oracle");

			SDropProcedure s = new SDropProcedure(proName);
			s.setExists(exists);
			planType = PlanType.SCHEMA_MODIFY;
			stmt = s;
		} else if (token.equalsIgnoreCase("INDEX")) {
			parser.read();
			String indexName = DDLParserUtils.readIdentifier(parser, "Index name");

			boolean isForce = false;
			String tableName = null;
			String clusterName = null;

			
			boolean hasOn = parser.readIf("ON");
			if (hasOn) {
				tableName = DDLParserUtils.readIdentifier(parser, "Table name");
				dbnType = getDbnTypeByTable(tableName);
				if (dbnType != DbnType.MySQL)
					throw new SQLException("Syntax err, 'ON tbl_name' use only in MySQL not Oracle");
			} else {
				
				isForce = parser.readIf("FORCE");
				
				for (DbnCluster dbnCluster : clusterInfo.getDbnClusterMap().values()) {
					if (indexName.equals(dbnCluster.getClusterIndexName())) {
						clusterName = dbnCluster.getName();
						break;
					}
				}
				if (null == clusterName) {
					for (TableInfo table : clusterInfo.getTableMap().values()) {
						if (DbnType.Oracle == table.getDbnType()
								&& null != table.getIndexInfo(indexName)) {
								tableName = table.getName();
								break;
						}
					}
				} else {
					
					return new SDropClusterIndex(indexName, clusterName, isForce);
				}

				if (null == clusterName && null == tableName)
					throw new SQLException("Definition err, Index '" + indexName
							+ "' can not be found in Oracle clusters or tables");
				dbnType = DbnType.Oracle;
			}

			List<SAlterTableOp> ops = new LinkedList<SAlterTableOp>();
			if (dbnType == DbnType.MySQL) {
				SAlterTableOp op = new SAlterTableDropIndex(indexName, false);
				op.setSql("DROP INDEX " + indexName + " ON " + tableName);
				op.setClauseSql("DROP INDEX " + indexName);
				ops.add(op);
			} else if (dbnType == DbnType.Oracle) {
				SAlterTableOp op = new SAlterTableDropIndex(indexName, true);
				String cmd = "DROP INDEX " + indexName;
				if (isForce)
					cmd = cmd + " FORCE";
				op.setSql(cmd);
				op.setClauseSql(null);
				ops.add(op);
			}
			SAlterTable alterTable = new SAlterTable(tableName, ops);
			alterTable.setDbnType(dbnType);
			
			if (parser.getCurrentToken().equalsIgnoreCase("IN")) {
				parser.read();
				alterTable.setDdbName(DDLParserUtils.readIdentifier(parser, "ddb name"));
			}
			stmt = alterTable;

			if (dbnType == DbnType.Oracle) {
				
				if (hasHint(sql))
					throw new SQLException("Online alter table is not supported in Oracle database");
			} else if (dbnType == DbnType.MySQL){
				OnlineAlterTaskInfo oat = null;
				if (parser.getCurrentToken().equals("/"))
					oat = DDLParserUtils.parseAlterTableOptions(parser, alterTable);
				if (oat == null) {
					stmt = alterTable;
				} else {
					stmt = oat;
				}
			}
		}
		return stmt;
	}
	
	
	private SSlicedMassUpdatePair readScopeValue(String parserStr) throws SQLException {
		parser.read();
		String value = parser.getCurrentToken();
		if (value.equals("(")) {
			value = "(" + readStringInBrackets(parserStr) + ")";
		} else {
			parser.read();
		}
		return SSlicedMassUpdatePair.parse(value);
	}

	
	private DbnType getDbnTypeByTable(String tbl_name) throws SQLException {
		if (null == clusterInfo)
			throw new SQLException("No cluster is valid");
		TableInfo tbl = clusterInfo.getTableInfo(tbl_name);
		if (null == tbl)
			throw new SQLException("Definition err, table|view name '" + tbl_name + "' is not valid");
		return tbl.getDbnType();
	}

	
	private DbnType getDbnTypeByPolicy(String plyName) throws SQLException {
		if (null == clusterInfo)
			throw new SQLException("No cluster is valid");
		Policy ply = clusterInfo.getPolicy(plyName);
		if (null == ply) {
			throw new SQLException("Definition err, policy name '" + plyName + "' is not valid");
		}
		return ply.getDbnType();
	}

	
	private DbnType getDbnTypeByTrigger(String trName) throws SQLException {
		if (null == clusterInfo)
			throw new SQLException("No cluster is valid");
		Trigger tr = clusterInfo.getTrigger(trName);
		if (null == tr)
			throw new SQLException("Definition err, trigger name '" + trName + "' is not valid");
		return tr.getDbnType();
	}

	
	private DbnType getDbnTypeByProcedure(String proName) throws SQLException {
		if (null == clusterInfo)
			throw new SQLException("No cluster is valid");
		Routine rt = clusterInfo.getRoutine(proName);
		if (null == rt)
			throw new SQLException("Definition err, procedure name '" + proName + "' is not valid");
		return rt.getDbnType();
	}

	
	private boolean hasHint(String sql) {
		if (null == sql || "".equals("sql"))
			return true;
		sql = sql.trim();
		int lhint = sql.lastIndexOf("");
		
		if (lhint > 0 && rhint > 0 && (lhint + 2) < rhint && (rhint + 3) == sql.length())
			return true;
		else return false;
	}

	
	private String getHint(String sql) {
		if (null == sql || "".equals("sql"))
			return null;
		sql = sql.trim();
		int lhint = sql.lastIndexOf("");
		
		if (lhint > 0 && rhint > 0 && (lhint + 2) < rhint && (rhint + 3) == sql.length())
			return sql.substring(lhint + 2, rhint);
		else
			return null;
	}

	
	private boolean isOnlineAlterTable(String sql) {
		if (hasHint(sql) && getHint(sql).indexOf("ONLINE") > 0)
			return true;
		return false;
	}

	
	private DbnType parseDbnTypeByHint(String sql) throws SQLException {
		String dbnHint = getHint(sql);
		DbnType dbnType = DbnType.MySQL;
		if (null != dbnHint) {
			SQLLexParser p = SQLLexParser.getLexParser(dbnHint);
			DDLParserUtils.readAndMatch(p, "DBNTYPE");
			DDLParserUtils.match(p, "=");
			String dbnTypeStr = DDLParserUtils.readIdentifier(p, "dbntype");
			if (null != dbnTypeStr) {
				if (dbnTypeStr.equalsIgnoreCase("ORACLE")) {
					dbnType = DbnType.Oracle;
				} else if (dbnTypeStr.equalsIgnoreCase("MYSQL")) {
					dbnType = DbnType.MySQL;
				} else
					throw new SQLException("The dbntype should be ORACLE or MYSQL");
			} else
				throw new SQLException("The hint of trigger or procedure should be '");
		}

		return dbnType;
	}

	
	private Statement parseByDbn(DbnType type, String sql) throws SQLException {
		Statement st = null;
		if (null == type)
			throw new SQLException("Syntax err, can't not get the DbnType by incorrect sql clause");
		if (type == DbnType.MySQL) {
			st = new DDLParserMySQL().parseMySQL(sql);
		} else if (type == DbnType.Oracle) {
			st = new DDLParserOracle().parseOracle(sql);
		}
		return st;
	}
}
