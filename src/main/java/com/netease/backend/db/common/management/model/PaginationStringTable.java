package com.netease.backend.db.common.management.model;

import java.io.Serializable;

import com.netease.cli.StringTable;



public class PaginationStringTable implements Serializable {
    private static final long serialVersionUID = -5051420340370386417L;
    
    private StringTable table;
    
    private int totalCount;
    
    public PaginationStringTable(StringTable st) {
        table = st;
        totalCount = st.getData().size();
    }

    public PaginationStringTable(StringTable st, int count) {
        table = st;
        totalCount = count;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public StringTable getTable() {
        return table;
    }
}
