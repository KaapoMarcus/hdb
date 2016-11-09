
package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class NTSETableMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tableName;
    private Map<String, String> tableParams = new HashMap<String, String>();
    private Map<String, Map<String, String>> columnParams = new HashMap<String, Map<String, String>>();

    
    public NTSETableMetadata(String tableName, Map<String, String> tableParams,
            Map<String, Map<String, String>> columnParams) {
        if (null == tableName || "".equals(tableName)) { throw new IllegalArgumentException(
                "��������Ϊ��"); }
        this.tableName = tableName;

        if (null != tableParams) {
            this.tableParams.putAll(tableParams);
        }

        if (null != columnParams) {
            this.columnParams.putAll(columnParams);
        }
    }

    public Map<String, String> getTableParams(){
        return this.tableParams;
    }

    public String getTableParam(String key){
        return this.tableParams.get(key);
    }

    public Map<String, Map<String, String>> getAllColumnParams(){
        return this.columnParams;
    }

    public Map<String, String> getColumnParams(String columnName){
        return this.columnParams.get(columnName);
    }

    public String getColumnParam(String columnName, String key){
        return this.columnParams.get(columnName).get(key);
    }

    
    public String getTableName() {
        return this.tableName;
    }
}
