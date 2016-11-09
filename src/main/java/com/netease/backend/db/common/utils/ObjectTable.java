
package com.netease.backend.db.common.utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.netease.cli.StringTable;


public class ObjectTable implements Serializable {

    
    private static final long serialVersionUID = 1L;

    private String tableName = null;
    private int tableColumnCount = 0;
    private List<Object> headers = new LinkedList<Object>();
    private List<List<Object>> tuples = new LinkedList<List<Object>>();

    public ObjectTable(String tableName, List<Object> headers) {
        this.tableName = tableName;
        this.headers.addAll(headers);
        this.tableColumnCount = headers.size();
    }

    public boolean addTuple(List<Object> tuple) {
        if (tuple.size() != this.tableColumnCount) {
            return false;
        }

        tuples.add(tuple);
        return true;
    }

    public StringTable toStringTable() {
        String[] headerString = new String[this.headers.size()];
        for (int i = 0; i < headerString.length; i++) {
            headerString[i] = this.headers.get(i).toString();
        }
        StringTable st = new StringTable(tableName, headerString);

        for (List<Object> tuple : this.tuples) {
            if (tuple == null) {
                continue;
            }

            String[] stringArray = new String[tuple.size()];
            for (int i = 0; i < stringArray.length; i++) {
                Object obj = tuple.get(i);
                if (obj == null) {
                    stringArray[i] = "NULL";
                } else {
                    stringArray[i] = obj.toString().trim();
                }
            }
            st.addRow(stringArray);
        }

        return st;
    }

    
    public List<List<Object>> getTuples() {
        return tuples;
    }

    
    public List<Object> getHeaders() {
        return headers;
    }

    
    public void setHeaders(List<Object> headers) {
        this.headers = headers;
    }

    
    public String getTableName() {
        return tableName;
    }

    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public static void main(String[] args) {
        List<Object> headers = new LinkedList<Object>();
        headers.add("to");
        headers.add("do");
        ObjectTable ot = new ObjectTable("test table", headers);

        List<Object> tuple = new LinkedList<Object>();
        tuple.add("list1");
        tuple.add(Calendar.getInstance());
        ot.addTuple(tuple);

        System.out.println(ot.toStringTable().getData());
    }

}
