package com.netease.backend.db.common.cloud;

import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.management.PIDConfig;
import com.netease.backend.db.common.schema.ColumnInfo;
import com.netease.backend.db.common.schema.IndexInfo;
import com.netease.backend.db.common.schema.Policy;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.schema.User;
import com.netease.backend.db.common.schema.cloud.Column;
import com.netease.backend.db.common.schema.cloud.DBUser;
import com.netease.backend.db.common.schema.cloud.DBUserDesc;
import com.netease.backend.db.common.schema.cloud.Index;
import com.netease.backend.db.common.schema.cloud.Table;
import com.netease.backend.db.common.schema.cloud.Table.EngineType;
import com.netease.backend.db.common.schema.cloud.TableDetail;
import com.netease.backend.db.common.schema.cloud.TableGroup;
import com.netease.backend.db.common.utils.StringUtils;


public class CommonBeanConverter {

	
	public static TableDetail getTableDetailFromTableInfo(TableInfo tableInfo) {
		Table table = getTableFromTableInfo(tableInfo);
		List<Column> columnList = new ArrayList<Column>(
				tableInfo.getColumeCount());
		List<String> balanceFields = tableInfo.getBalanceFields();
		for (ColumnInfo ci : tableInfo.getColumns()) {
			boolean isPartitionKey = StringUtils.containIgnoreCase(
					balanceFields, ci.getName());
			long displayLength = ci.getLength();
			if (displayLength == 0 && ColumnInfo.isFixSizeType(ci.getType()))
				displayLength = ci.getSize();
			Column c = new Column(ci.getName(), ci.getTypeName(),
					(int) displayLength, isPartitionKey, ci.isAutoIncrement());
			columnList.add(c);
		}
		List<Index> indexList = new ArrayList<Index>(tableInfo.getIndexMap()
				.size());
		for (IndexInfo idx : tableInfo.getIndexMap().values()) {
			Index index = new Index(idx.getIndexName(),
					idx.getColumnNameList(), idx.isPrimaryKey(), idx.isUnique());
			indexList.add(index);
		}
		return new TableDetail(table, columnList, indexList);
	}

	
	public static Table getTableFromTableInfo(TableInfo tableInfo) {
		String partitionKeys = StringUtils.convertToString(
				tableInfo.getBalanceFields(), ',');
		String tableGroupName = tableInfo.getBalancePolicy().getName();
		EngineType type = getEngineType(tableInfo);
		int idAssignType = getIdAssignType(tableInfo);
		return new Table(tableInfo.getName(), tableGroupName, partitionKeys,
				type, idAssignType, tableInfo.getStartID(),
				tableInfo.getRemainIDCount());
	}

	
	public static EngineType getEngineType(TableInfo tableInfo) {
		String type = tableInfo.getType();
		if (type.equals(TableInfo.TYPE_INNODB))
			return EngineType.INNODB;
		else if (type.equals(TableInfo.TYPE_MYISAM))
			return EngineType.MYISAM;
		else if (type.equals(TableInfo.TYPE_MEMORY))
			return EngineType.MEMORY;
		else if (type.equals(TableInfo.TYPE_NTSE))
			return EngineType.NTSE;
		else
			return EngineType.OTHERS;
	}

	
	public static int getIdAssignType(TableInfo tableInfo) {
		if (tableInfo.getAssignIdType() == PIDConfig.TYPE_SIMPLE)
			return Table.ID_ASSIGN_TYPE_MSB;
		else if (tableInfo.getAssignIdType() == PIDConfig.TYPE_TIMEBASED)
			return Table.ID_ASSIGN_TYPE_TSB;
		else
			throw new IllegalArgumentException("Invalid id assign type:"
					+ tableInfo.getAssignIdType());
	}

	
	public static DBUser getDBUserFromUserInfo(User userInfo) {
		return new DBUser(userInfo.getName(), userInfo.getClientIps(),
				userInfo.getEntityPrivileges(), userInfo.getDesc());
	}

	
	public static DBUserDesc getDBUserDescFromUserInfo(User userInfo) {
		return new DBUserDesc(userInfo.getName(), userInfo.getDesc());
	}

	
	public static TableGroup getTableGroupFromPolicy(Policy policy) {
		return new TableGroup(policy.getName(), policy.getComment(), policy
				.getDbList().size());
	}

	public static Converter<TableInfo, Table> getTableConverter() {
		return new Converter<TableInfo, Table>() {
			public Table convertTo(TableInfo t) {
				return getTableFromTableInfo(t);
			}
		};
	}

	public static Converter<Policy, TableGroup> getTableGroupConverter() {
		return new Converter<Policy, TableGroup>() {
			public TableGroup convertTo(Policy t) {
				return getTableGroupFromPolicy(t);
			}
		};
	}

	public static Converter<User, DBUserDesc> getDBUserConverter() {
		return new Converter<User, DBUserDesc>() {
			public DBUserDesc convertTo(User t) {
				return getDBUserDescFromUserInfo(t);
			}
		};
	}
}
