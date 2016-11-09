package com.netease.backend.db.common.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.utils.StringUtils;


public class SAlterTable extends Statement {
    private static final long serialVersionUID = 8910267666160873492L;
    
    private boolean ignore;
    private String tableName;
    private List<SAlterTableOp> ops;
    private boolean withRename;
    private boolean dbnFirst;
    private boolean withMyISAMTemp;
    private boolean withLock;
    private boolean dupKeyChk;
    private boolean setDupKeyChk = false;
    
    private DbnType dbnType = DbnType.MySQL;
   
	
    private boolean alterColumn = false;
    
    public SAlterTable(String tableName, List<SAlterTableOp> ops) {
        this.tableName = tableName;
        this.ops = ops;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public List<SAlterTableOp> getOps() {
        return ops;
    }

	public boolean isDbnFirst() {
		return dbnFirst;
	}

	public void setDbnFirst(boolean dbnFirst) {
		this.dbnFirst = dbnFirst;
	}

	public boolean isWithMyISAMTemp() {
		return withMyISAMTemp;
	}

	public void setWithMyISAMTemp(boolean withMyISAMTemp) {
		this.withMyISAMTemp = withMyISAMTemp;
	}

	public boolean isWithRename() {
		return withRename;
	}

	public void setWithRename(boolean withRename) {
		this.withRename = withRename;
	}

	public boolean isWithLock() {
		return withLock;
	}

	public void setWithLock(boolean withLock) {
		this.withLock = withLock;
	}

	public boolean isAlterColumn() {
		return alterColumn;
	}

	public void setAlterColumn(boolean alterColumn) {
		this.alterColumn = alterColumn;
	}
	
	public boolean isIgnore() {
		return this.ignore;
	}
	
	public void setIgnore(boolean b) {
		this.ignore = b;
	}
	
	public void checkOps() throws SQLException {
		if (dbnType == DbnType.MySQL) {
			return;
		} else if (dbnType == DbnType.Oracle) { 
			int combinableOpCount = 0;
			int dropOpCount = 0;
			int singleOpCount = 0;
			for (SAlterTableOp op : ops) {
				if ((op instanceof SAlterTableAddColumn)
						|| (op instanceof SAlterTableModifyColumn)
						|| (op instanceof SAlterTableAddIndex && (!((SAlterTableAddIndex)op).isOracleCreated()))
						|| (op instanceof SAlterTableDropIndex && (!((SAlterTableDropIndex)op).isOracleCreated()))) {
					if (singleOpCount > 0 || dropOpCount > 0)
						throw new SQLException("��ָ���Ĳ�����Oracle�ϲ�������ִ�У�����");
					combinableOpCount ++;
				} else if (op instanceof SAlterTableDropColumn) {
					if (combinableOpCount > 0 || singleOpCount > 0)
						throw new SQLException("��ָ���Ĳ�����Oracle�ϲ�������ִ�У�����");
					dropOpCount ++;
				} else if ((op instanceof SAlterTableAddIndex && ((SAlterTableAddIndex)op).isOracleCreated())
						|| (op instanceof SAlterTableDropIndex && ((SAlterTableDropIndex)op).isOracleCreated())
						|| (op instanceof SAlterTableRename)
						|| (op instanceof SAlterTableRenameColumn)
						|| (op instanceof SAlterTableComment)
						|| (op instanceof SAlterTableCommentColumn)) {
					if (combinableOpCount > 0 || singleOpCount > 0 || dropOpCount > 0) {
						throw new SQLException("��ָ���Ĳ�����Oracle�ϲ�������ִ�У�����");
					}
					singleOpCount ++;
				}
			}
		}
	}
	
	public String getSql() {
		if (null == ops || ops.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		if (dbnType == DbnType.MySQL) {
			sb.append("ALTER ");
			if (ignore)
				sb.append("IGNORE ");
			sb.append("TABLE ").append(tableName).append(" ");
			for (int i = 0; i < ops.size(); i++) {
				if (i == 0)
					sb.append(ops.get(i).getClauseSql().trim());
				else
					sb.append(",").append(ops.get(i).getClauseSql().trim());
			}
		} else if (dbnType == DbnType.Oracle) {
			List<SAlterTableOp> combinableOps = new ArrayList<SAlterTableOp>();
			List<SAlterTableOp> dropOps = new ArrayList<SAlterTableOp>();
			SAlterTableOp singleOp = null;
			for (SAlterTableOp op : ops) {
				if ((op instanceof SAlterTableAddColumn)
						|| (op instanceof SAlterTableModifyColumn)
						|| (op instanceof SAlterTableAddIndex && (!((SAlterTableAddIndex)op).isOracleCreated()))
						|| (op instanceof SAlterTableDropIndex && (!((SAlterTableDropIndex)op).isOracleCreated()))) {
					combinableOps.add(op);
				} else if (op instanceof SAlterTableDropColumn) {
					dropOps.add(op);
				} else if ((op instanceof SAlterTableAddIndex && ((SAlterTableAddIndex)op).isOracleCreated())
						|| (op instanceof SAlterTableDropIndex && ((SAlterTableDropIndex)op).isOracleCreated())
						|| (op instanceof SAlterTableRename)
						|| (op instanceof SAlterTableRenameColumn)
						|| (op instanceof SAlterTableComment)
						|| (op instanceof SAlterTableCommentColumn)) {
					singleOp = op;
				}
			}
			if (combinableOps.size() > 0) {
				sb.append("ALTER TABLE ").append(tableName).append(" ");
				for (SAlterTableOp op : combinableOps) {
					sb.append(op.getClauseSql().trim()).append(" ");
				}
			} else if (dropOps.size() > 0) {
				sb.append("ALTER TABLE ").append(tableName).append(" DROP (");
				List<String> columns = new ArrayList<String>();
				for (SAlterTableOp op : dropOps) {
					columns.add(((SAlterTableDropColumn)op).getColumnName());
				}
				sb.append(StringUtils.convertToString(columns, ", ")).append(")");
			} else if (null != singleOp) {
				sb.append(singleOp.getSql());
			}
		}
		return sb.toString();
	}
	
	public String toString() {
		if (null == ops || ops.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append("Table ").append(tableName).append(" ");
		for (int i = 0; i < ops.size(); i++) {
			if (i > 0)
				sb.append(", ").append(ops.get(i));
			else
				sb.append(ops.get(i));
		}
		return sb.toString();
	}

	public boolean isDupKeyChk() {
		return dupKeyChk;
	}

	public void setDupKeyChk(boolean dupKeyChk) {
		this.dupKeyChk = dupKeyChk;
		this.setDupKeyChk=true;
	}

	public boolean isSetDupKeyChk() {
		return setDupKeyChk;
	}
	
	public void updateAlterTable(SAlterTable other) {
		this.tableName = other.tableName;
		this.ops = new ArrayList<SAlterTableOp>();
		if (null != other.ops)
			ops.addAll(other.ops);
		ignore = other.ignore;
		withRename = other.withRename;
		dbnFirst = other.dbnFirst;
		withMyISAMTemp = other.withMyISAMTemp;
		withLock = other.withLock;
		dupKeyChk = other.dupKeyChk;
		setDupKeyChk = other.setDupKeyChk;
		alterColumn = other.alterColumn;
		dbnType = other.dbnType;
	}
	
	 
    public DbnType getDbnType() {
		return dbnType;
	}

	public void setDbnType(DbnType dbnType) {
		this.dbnType = dbnType;
	}

}
