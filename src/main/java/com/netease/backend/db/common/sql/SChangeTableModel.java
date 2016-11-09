package com.netease.backend.db.common.sql;


public class SChangeTableModel extends Statement {
	private static final long serialVersionUID = 2319061069546071707L;
	
	private String tblName;
	private String mdlName;
	
	public SChangeTableModel(String tbl, String mdl) {
		this.tblName = tbl;
		this.mdlName = mdl;
	}
	public String getTblName() {
		return tblName;
	}
	public String getMdlName() {
		return mdlName;
	}
}
