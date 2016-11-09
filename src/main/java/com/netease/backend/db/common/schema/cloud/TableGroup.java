package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;
import java.util.Comparator;

import com.netease.backend.db.common.cloud.Marker;
import com.netease.backend.db.common.schema.Policy;


public class TableGroup implements Serializable, Marker {

	private static final long serialVersionUID = -8086065098362280425L;
	
	public static enum TableGroupSortField {
		NAME {
			@Override
			public Comparator<Policy> getComparator() {
				return new Comparator<Policy>() {
					public int compare(Policy p1, Policy p2) {
						return p1.getName().compareTo(p2.getName());
					}
				};
			}
		}, DBN_COUNT {
			@Override
			public Comparator<Policy> getComparator() {
				return new Comparator<Policy>() {
					public int compare(Policy p1, Policy p2) {
						return Long.valueOf(p1.getDbList().size()).compareTo(
								Long.valueOf(p2.getDbList().size()));
					}
				};
			}
		};
		
		public abstract Comparator<Policy> getComparator();
	}

	
	private String name;

	
	private String comment;

	
	private int dbnCount;
	
	
	public static final int MAX_NAME_LENGTH = 50;

	public TableGroup(String name, String comment, int dbnCount) {
		this.name = name;
		this.comment = comment;
		this.dbnCount = dbnCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getDbnCount() {
		return dbnCount;
	}

	public void setDbnCount(int dbnCount) {
		this.dbnCount = dbnCount;
	}
	
	public String getMarker() {
		return getName();
	}
	
}
