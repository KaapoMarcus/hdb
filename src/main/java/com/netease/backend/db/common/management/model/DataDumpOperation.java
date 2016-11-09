package com.netease.backend.db.common.management.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.netease.backend.db.common.utils.Validator;


public class DataDumpOperation implements Serializable, Cloneable {
	
	private static final long serialVersionUID = -6536435957894867848L;

	
    public static final int STATUS_INITIALIZED = 0;
    
    
    public static final int STATUS_SENDED = 1;
    
    
    public static final int STATUS_ERROR = 2;
    
    
    public static final int STATUS_FINISH = 3;
    
    
    private boolean isAllDB;
    
	private List<String> tableNames;
	
	private String condition;
	
	private boolean isSql;
	
	
	private String fieldTerminate;
	
	
	private String lineTerminate;
	
	
	private String fieldEnclose;
	
	
	private boolean isByPartition;
	
	
	private int partitionDaysBefore;
	
	
	private int partitionsDumpNum;
	
	
	private String partitionsDump;
    
    
    private boolean needCompress;
    
    
    private boolean needExecScript;
    
    
    private boolean ignoreCheckResult;
	
    
    private boolean ignoreDumpResult;
    
    
    private boolean delExpiredData;
    
    
    private int expiredDays;
    
     
    private int status;
	
    
	public DataDumpOperation(List<String> tablenames, String whereCondition) {
		this.tableNames = tablenames;
		this.condition = whereCondition;
		this.status = STATUS_INITIALIZED;
		this.isAllDB = false;
		this.isSql = true;
		this.fieldTerminate = "\\t";
		this.lineTerminate = "\\n";
		this.fieldEnclose = " ";
		this.isByPartition = false;
		this.partitionsDump = "";
		this.partitionsDumpNum = 1;
        this.needCompress = false;
        this.needExecScript = false;
        this.ignoreCheckResult = true;
        this.ignoreDumpResult = false;
        this.delExpiredData = true;
        this.expiredDays = 4;
	}
	
	
	public List<String> getTableNames() {
		return this.tableNames;
	}
	
	
	public String getCondition() {
		return this.condition;
	}
	
	
	public int getStatus() {
		return this.status;
	}
	
	
	public void setTableName(List<String> tablename) {
		this.tableNames = tablename;
	}
	
	
	public void setCondition(String whereCondition) {
		this.condition = whereCondition;
	}
	
	
	public void setStatus(int s) {
		this.status = s;
	}
	
	public boolean isAllDB() {
		return this.isAllDB;
	}
	
	public void setAllDB(boolean flag) {
		this.isAllDB = flag;
	}
	
	public boolean isSql() {
		return this.isSql;
	}
	
	public void setIsSql(boolean flag) {
		this.isSql = flag;
	}
	
	public String getFieldTerminate() {
		return fieldTerminate;
	}

	public void setFieldTerminate(String fieldTerminate) {
		this.fieldTerminate = fieldTerminate;
	}
	
	public String getLineTerminate() {
		return lineTerminate;
	}
	
	public String getFieldEnclose() {
		return fieldEnclose;
	}

	public void setFieldEnclose(String fieldEnclose) {
		this.fieldEnclose = fieldEnclose;
	}

	public void setLineTerminate(String lineTerminate) {
		this.lineTerminate = lineTerminate;
	}
	
	public boolean isByPartition() {
		return isByPartition;
	}
	
	public void setByPartition(boolean b) {
		this.isByPartition = b;
	}
	
	public int getPartitionDaysBefore() {
		return partitionDaysBefore;
	}
	
	public void setPartitionDaysBefore(int d) {
		this.partitionDaysBefore = d;
	}
	
	public String getPartitionsDump() {
		return this.partitionsDump;
	}
	
	public int getPartitionsDumpNum() {
		return this.partitionsDumpNum;
	}
	
	public void setPartitionsDumpNum(int num) {
		this.partitionsDumpNum = num;
	}
    
    public boolean isNeedCompress() {
        return this.needCompress;
    }
    
    public void setNeedCompress(boolean compress) {
        this.needCompress = compress;
    }
    
    public boolean isNeedExecScript() {
        return this.needExecScript;
    }
    
    public void setNeedExecScript(boolean execScript) {
        this.needExecScript = execScript;
    }
    
    public boolean getIgnoreCheckResult() {
        return this.ignoreCheckResult;
    }
    
    public void setIgnoreCheckResult(boolean ignore) {
        this.ignoreCheckResult = ignore;
    }
    
    public boolean isIgnoreDumpResult() {
        return ignoreDumpResult;
    }
    
    public void setIgnoreDumpResult(boolean ignoreDumpResult) {
        this.ignoreDumpResult = ignoreDumpResult;
    }
    
    public boolean isDelExpiredData() {
        return this.delExpiredData;
    }
    
    public void setDelExpiredData(boolean del) {
        this.delExpiredData = del;
    }
    
    public int getExpiredDays() {
        return this.expiredDays;
    }
    
    public void setExpiredDays(int days) {
        this.expiredDays = days;
    }
	
	
	public void generatePartitionCondition(RangePartition partition) {
		if (!isByPartition || null == partition
				|| null == partition.getPartitionField()
				|| null == partition.getPartitions()) {
			return;
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 0 - partitionDaysBefore + 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		
		String partitionStr = "p" + sdf.format(c.getTime());
		ArrayList<String> partitionNames = new ArrayList<String>();
		Iterator<String> it = partition.getPartitions().keySet().iterator();
		while (it.hasNext()) {
			partitionNames.add(it.next());
		}
		Collections.sort(partitionNames);

		int i = 0;
		for (; i < partitionNames.size(); i++) {
			String name = partitionNames.get(i);
			if (partitionStr.compareTo(name) < 0) {
				break;
			}
		}
		if (0 == i) {
			
			this.condition = "true=false";
		} else {
			int max = i - 1;
			String maxValue = partition.getPartitions().get(
					partitionNames.get(max));
			this.condition = partition.getPartitionField().trim() + "<"
					+ maxValue.trim();

			int min = max - partitionsDumpNum;
			if (min >= 0) {
				String minValue = partition.getPartitions().get(
						partitionNames.get(min));
				this.condition += " and " + partition.getPartitionField().trim()
						+ ">=" + minValue.trim();
			} else {
				min = -1;
			}
			
			for(i = min + 1; i <= max; i++) {
				String name = partitionNames.get(i);
				if(i == min + 1) {
					partitionsDump = name;
				} else {
					partitionsDump += ";" + name;
				}
			}
		}
	}
    
    
    public Object clone() {
        try {
            DataDumpOperation cloned = (DataDumpOperation) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
	
	
	public String toString() {
		if(isAllDB) {
			return "���ݵ��������������������ݿ�";
		}
		if (isByPartition) {
			return "���ݵ������������������б�" + tableNames + "������" + partitionDaysBefore
					+ "����ǰ������";
		}
		return "���ݵ������������������б�" + tableNames + "������" + condition;
	}
	
	
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }

        if (otherObject == null) {
            return false;
        }

        if (this.getClass() != otherObject.getClass()) {
            return false;
        }

        DataDumpOperation other = (DataDumpOperation) otherObject;

        return ((this.isSql && other.isSql()) || 
        				(!this.isSql && !other.isSql() &&
        				this.lineTerminate.equals(other.getLineTerminate()) &&
        				this.fieldTerminate.equals(other.getFieldTerminate()) &&
        				this.fieldEnclose.equals(other.getFieldEnclose())
        				)
        		) &&
        		((this.isAllDB && other.isAllDB()) || 
        			(!this.isAllDB && !other.isAllDB() &&
                    Validator.isStrSetEquals(tableNames, other.getTableNames()) && 
        			this.condition.equals(other.getCondition()) &&
        			this.isByPartition == other.isByPartition() &&
        			this.partitionDaysBefore == other.getPartitionDaysBefore() &&
        			this.partitionsDumpNum == other.getPartitionsDumpNum()
        			)
        		) &&
                this.needCompress == other.isNeedCompress() &&
                this.needExecScript == other.isNeedExecScript()
                && ((!delExpiredData && !other.isDelExpiredData()) || (delExpiredData
                        && other.isDelExpiredData() && expiredDays == other
                        .getExpiredDays()))
                && this.ignoreCheckResult == other.getIgnoreCheckResult()
                && this.ignoreDumpResult == other.isIgnoreDumpResult();
    }
  
    
    public int hashCode() {
    	int nameCode = 1;
		if (null != tableNames) {
			Iterator<String> it = tableNames.iterator();
			while (it.hasNext()) {
				String name = it.next();
				nameCode = nameCode + (name == null ? 0 : name.hashCode());
			}
		}
		int noAllDB = 1237 + nameCode + condition.hashCode()
				+ (isByPartition ? 1231 : 1237) + partitionDaysBefore 
				+ partitionsDumpNum;
        int expired = delExpiredData ? (1231 + expiredDays) : 1237;
        int opt = (needCompress ? 1231 : 1237)
                + (needExecScript ? 1231 : 1237)
                + expired
                + (ignoreCheckResult ? 1231 : 1237)
                + (ignoreDumpResult ? 1231 : 1237);
		if (isSql) {
			return 1231 + (isAllDB ? 1231 : noAllDB) + opt;
		} else {
			return 1237 + lineTerminate.hashCode() + fieldTerminate.hashCode()
					+ fieldEnclose.hashCode() + (isAllDB ? 1231 : noAllDB) + opt;
		}
	}
}
