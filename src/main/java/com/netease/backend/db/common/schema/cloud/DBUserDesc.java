package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;
import java.util.Comparator;

import com.netease.backend.db.common.cloud.Marker;
import com.netease.backend.db.common.schema.User;


public class DBUserDesc implements Serializable, Marker {

	private static final long serialVersionUID = 683773746574389023L;
	
	public static enum DBUserSortField {
		NAME {
			@Override
			public Comparator<User> getComparator() {
				return new Comparator<User>() {
					public int compare(User u1, User u2) {
						return u1.getName().compareTo(u2.getName());
					}
				};
			}
		};
		public abstract Comparator<User> getComparator();
	}

	private String name;
	private String desc;

	public DBUserDesc(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMarker() {
		return getName();
	}
}
