package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;
import java.util.Comparator;

import com.netease.backend.db.common.cloud.Marker;
import com.netease.backend.db.common.schema.TableInfo;


public class Table implements Serializable, Marker {

	private static final long serialVersionUID = -6109614844616805759L;

	public static enum EngineType {
		INNODB, MEMORY, MYISAM, NTSE, OTHERS
	}

	public static enum TableSortField {
		NAME {
			@Override
			public Comparator<TableInfo> getComparator() {
				return new Comparator<TableInfo>() {
					public int compare(TableInfo t1, TableInfo t2) {
						return t1.getName().compareTo(t2.getName());
					}
				};
			}
		},
		TABLEGROUP_NAME {
			@Override
			public Comparator<TableInfo> getComparator() {
				return new Comparator<TableInfo>() {
					public int compare(TableInfo t1, TableInfo t2) {
						return t1.getBalancePolicy().getName()
								.compareTo(t2.getBalancePolicy().getName());
					}
				};
			}
		},
		ENGINE_TYPE {
			@Override
			public Comparator<TableInfo> getComparator() {
				return new Comparator<TableInfo>() {
					public int compare(TableInfo t1, TableInfo t2) {
						return t1.getType().compareTo(t2.getType());
					}
				};
			}
		},
		ASSIGN_ID_TYPE {
			public Comparator<TableInfo> getComparator() {
				return new Comparator<TableInfo>() {
					public int compare(TableInfo t1, TableInfo t2) {
						return Integer.valueOf(t1.getAssignIdType()).compareTo(
								Integer.valueOf(t2.getAssignIdType()));
					}
				};
			}
		};

		public abstract Comparator<TableInfo> getComparator();
	}

	
	public static final int ID_ASSIGN_TYPE_MSB = 1;

	
	public static final int ID_ASSIGN_TYPE_TSB = 2;

	public static final TableSortField DEFAULT_SORT_FIELD = TableSortField.NAME;

	
	private String name;

	
	private String tableGroupName;

	
	private String partitionKey;

	
	private EngineType engineType;

	
	private int assignIdType;

	
	private long startId;

	
	private long remainIdCount;

	
	public Table(String name, String tableGroupName, String partitionKey,
			EngineType type, int assignIdType, long startId, long remainIdCount) {
		if (assignIdType != ID_ASSIGN_TYPE_MSB
				&& assignIdType != ID_ASSIGN_TYPE_TSB)
			throw new IllegalArgumentException("Illegal id assign type "
					+ assignIdType);

		this.name = name;
		this.tableGroupName = tableGroupName;
		this.partitionKey = partitionKey;
		this.engineType = type;
		this.assignIdType = assignIdType;
		this.startId = startId;
		this.remainIdCount = remainIdCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableGroupName() {
		return tableGroupName;
	}

	public void setTableGroupName(String tableGroupName) {
		this.tableGroupName = tableGroupName;
	}

	public String getPartitionKey() {
		return partitionKey;
	}

	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}

	public EngineType getEngineType() {
		return engineType;
	}

	public void setEngineType(EngineType engineType) {
		this.engineType = engineType;
	}

	public int getAssignIdType() {
		return assignIdType;
	}

	public void setAssignIdType(int assignIdType) {
		this.assignIdType = assignIdType;
	}

	public long getStartId() {
		return startId;
	}

	public void setStartId(long startId) {
		this.startId = startId;
	}

	public long getRemainIdCount() {
		return remainIdCount;
	}

	public void setRemainIdCount(long remainIdCount) {
		this.remainIdCount = remainIdCount;
	}

	public String getMarker() {
		return getName();
	}
}
