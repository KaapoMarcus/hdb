package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;


public class Stat implements Serializable{

	private static final long serialVersionUID = -4109507177986393172L;
	
	protected String name;
	
	protected String desc;
	
	protected long count;
	
	protected long time;
	
	protected Stat parent;
	
	protected LinkedList<Stat> children = new LinkedList<Stat>();
	
	public Stat(String name, String desc) {
		this.name = name;
	}
	
	public Stat(String name, String desc, Stat parent) {
		this.name = name;
		this.parent = parent;
		parent.addChild(this);
	}
	
	public String getName() {
		return name;
	}
	
	public long getCount() {
		return count;
	}

	public long getTime() {
		return time;
	}

	public double getAvgTime() {
		if (count == 0)
			return 0;
		else
			return (double)time / count;
	}
	
	public Stat getParent() {
		return parent;
	}
	
	public Collection<Stat> getChildren() {
		return children;
	}
	
	
	public String getIndentName(boolean treeVisible, int indent) {
		int nestLevel = 0;
		Stat p = this;
		while ((p = p.parent) != null)
			nestLevel++;
		boolean hasChildren = children.size() > 0;
		String iName = "";
		
		if (treeVisible) {
			for (int l = 0; l < nestLevel - 1; l++) {
				iName += "|";
				for (int i = 1; i < indent; i++)
					iName += " ";
			}
			if (nestLevel > 0) {
				iName += "|";
				for (int i = 1; i < indent; i++)
					iName += "-";
			}
			if (hasChildren) 
				iName += "v";
			else
				iName += ">";
			iName += " ";
		}
		
		else {
			for (int i = 0; i < nestLevel * indent; i++) 
				iName += " ";
		}
		return iName + name;
	}
	
	public void addStat(long time) {
		count++;
		this.time += time;
		if (parent != null)
			parent.addStat(time);
	}
	
	public void addStat(long count, long time) {
		this.count += count;
		this.time += time;
		if (parent != null)
			parent.addStat(count, time);
	}
	
	public void reset() {
		if (parent != null) {
			parent.count -= count;
			parent.time -= time;
		}
		if(children == null || children.size() == 0)
		{
			count = 0;
			time = 0;
		}
		for (Stat s : children)
			s.reset();
	}
	
	public void merge(Stat another) {
		count += another.count;
		time += another.time;
	}
	
	protected void addChild(Stat child) {
		children.add(child);
	}
	
	public String toString() {
		return name + ", " + count + ", " + time + (parent == null ? "" : ", " + parent.name)
			+ (children == null ? "" : "-> " + children);
	}
}
