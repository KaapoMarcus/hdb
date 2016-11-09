package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;



public class Explain implements Serializable {
	private static final long serialVersionUID = -8894776842658921517L;
	List<ExplainItem> itemList = new LinkedList<ExplainItem>();
	List<String> tableList = new LinkedList<String>();
	
	public Explain() throws SQLException {
		
	}
	
	public Explain(ResultSet explainOut) throws SQLException {
		while (explainOut.next()) {
			ExplainItem item = new ExplainItem(explainOut);
			itemList.add(item);
			if (item.getTable() == null)
				tableList = null;
			else if (tableList != null)
				tableList.add(item.getTable());
		}
	}
		
	public List<String> getTableList() {
		return tableList;
	}

	public String getSkeleton() {
		String r = null;
		for (ExplainItem item: itemList) {
			if (r == null)
				r = item.getSkeleton();
			else
				r += "||" + item.getSkeleton(); 
		}
		return r;
	}

	public List<ExplainItem> getItemList() {
		return itemList;
	}
	
	public void merge(Explain other) {
		if (other == null)
			return;
		if (getSkeleton().equals(other.getSkeleton()))
			for (int i = 0; i < itemList.size(); i++)
				itemList.get(i).merge(other.getItemList().get(i));
	}
}
