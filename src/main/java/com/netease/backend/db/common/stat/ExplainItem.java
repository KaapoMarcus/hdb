package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ExplainItem implements Serializable {
	private static final long serialVersionUID = 4381932313754871629L;
	private String table;
	private String selectType;
	private String type;
	private String possibleKeys;
	private String key;
	private String keyLen;
	private String ref;
	private String extra;
	private int rows;
	private int count;
	private int maxRows;
	
	public ExplainItem(ResultSet explainRow) throws SQLException {
		table = explainRow.getString("table");
		selectType = explainRow.getString("select_type");
		type = explainRow.getString("type");
		possibleKeys = explainRow.getString("possible_keys");
		if(possibleKeys == null || possibleKeys.equalsIgnoreCase("null"))
			possibleKeys = "";
		key = explainRow.getString("key");
		if(key == null || key.equalsIgnoreCase("null"))
			key = "";
		keyLen = explainRow.getString("key_len");
		if(keyLen == null || keyLen.equalsIgnoreCase("null"))
			keyLen = "";
		ref = explainRow.getString("ref");
		extra = explainRow.getString("Extra");
		rows = explainRow.getInt("rows");
		count = 1;
		maxRows = rows;
	}
	
	public ExplainItem(String table, String selectType, String type, String possibleKeys, 
			String key, String keyLen, String ref, String extra, int rows, int count, int maxRows)
	{
		this.table = table;
		this.selectType = selectType;
		this.type = type;
		this.possibleKeys = possibleKeys;
		this.key = key;
		this.keyLen = keyLen;
		this.ref = ref;
		this.extra = extra;
		this.rows = rows;
		this.count = count;
		this.maxRows = maxRows;
	}

	public String getExtra() {
		return extra;
	}

	public String getKey() {
		return key;
	}

	public String getKeyLen() {
		return keyLen;
	}

	public String getPossibleKeys() {
		return possibleKeys;
	}

	public String getRef() {
		return ref;
	}

	public String getSelectType() {
		return selectType;
	}
	
	public String getType() {
		return type;
	}

	public String getTable() {
		return table;
	}

	public int getRows() {
		return rows;
	}
	
	public int getAvgRows() {
		if(count == 0) return 0;
		else
			return rows/count;
	}
	
	public String getSkeleton() {
		return table + ":" + selectType + ":" + type + ":" + key + ":" + keyLen + ":" + extra;
	}

	public void merge(ExplainItem other)
	{
		count ++;
		rows += other.getRows();
		if(other.rows > rows)
			maxRows = other.rows;
	}

	public int getCount() {
		return count;
	}

	public int getMaxRows() {
		return maxRows;
	}
	
	public String toString()
	{
		return "table="+table+", selectType="+selectType+", type="+type+
		", possibleKeys="+possibleKeys+", key="+key+", keyLen="+keyLen+
		", extra="+extra+", explainCount="+count+", rows="+rows+", maxrows="+maxRows;
	}
}
