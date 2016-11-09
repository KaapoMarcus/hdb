package com.netease.backend.db.common.ha.util;

import java.util.List;


public class ZookeeperDataSchema {
	
	private String path;

	
	private String data;

	
	private List<ZookeeperDataSchema> children = null;

	public ZookeeperDataSchema(String path, String data,
			List<ZookeeperDataSchema> children) {
		super();
		this.path = path;
		this.data = data;
		this.children = children;
	}

	public ZookeeperDataSchema() {
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<ZookeeperDataSchema> getChildren() {
		return children;
	}

	public void setChildren(List<ZookeeperDataSchema> children) {
		this.children = children;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (null == obj)
			return false;
		if (!(obj instanceof ZookeeperDataSchema))
			return false;
		ZookeeperDataSchema dataSchema = (ZookeeperDataSchema) obj;
		if (path.equals(dataSchema.path) && this.data.equals(dataSchema.data))
			return true;
		else
			return false;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("path:");
		sb.append(path);
		sb.append("; data:");
		sb.append(data);
		sb.append(";");
		if (children != null && children.size() > 0) {
			sb.append(" children:");
			for (ZookeeperDataSchema child : children) {
				sb.append(child.getPath());
				sb.append(":");
				sb.append(child.getData());
				sb.append(";");
			}
		}

		return sb.toString();
	}
}
