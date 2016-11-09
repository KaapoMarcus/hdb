package com.netease.backend.db.common.sql;

import java.util.Calendar;
import java.util.Collection;

import com.netease.backend.db.common.schema.Database;

public class SSlowlogOperation extends Statement {

    
    private static final long serialVersionUID = 1L;

    
    public static int MODE_FIX_DATE = 1;
    public static int MODE_RANGE_DATE = 2;

    public static int READ_ORDER = 1;
    public static int READ_REVERSE = 2;
    public static int READ_BOTH = 3;

    public static int DATE_RANGE_DAY = 1;
    public static int DATE_RANGE_2DAY = 2;
    public static int DATE_RANGE_WEEK = 3;
    public static int DATE_RANGE_MONTH = 4;
    public static int DATE_RANGE_YEAR = 5;

    public static int SHOW_TO_SCREEN = 1;
    public static int SHOW_TO_DB = 2;
    public static int SHOW_TO_BOTH = 3;

    
    
    private int mode = SSlowlogOperation.MODE_FIX_DATE;
    private Collection<String> nodeNameList = null;
    private Calendar startTime = null;
    private Calendar endTime = null;
    private int dateRange = -1;
    private int readMode = SSlowlogOperation.READ_REVERSE;
    private int maxUserShow = 5;
    private String extraArgs = null;
    private int resultShowMode = SSlowlogOperation.SHOW_TO_BOTH;

    
    private int topN = 10;

    
    public SSlowlogOperation(Collection<String> nodeList, Calendar startTime,
            Calendar endTime, int sortMode, int maxUserShow, String extraArgs,
            int resultShowMode) {
        this.mode = MODE_FIX_DATE;
        this.nodeNameList = nodeList;
        this.startTime = startTime;
        this.endTime = endTime;
        this.readMode = sortMode;
        this.resultShowMode = resultShowMode;
        this.maxUserShow = maxUserShow;
        this.extraArgs = extraArgs;
    }

    public SSlowlogOperation(Collection<String> nodeList, int dateRange,
            int sortMode, int maxUserShow, String extraArgs,
            int resultShowMode) {
        this.mode = MODE_RANGE_DATE;
        this.nodeNameList = nodeList;
        this.dateRange = dateRange;
        this.readMode = sortMode;
        this.resultShowMode = resultShowMode;
        this.maxUserShow = maxUserShow;
        this.extraArgs = extraArgs;
    }

    
    public boolean addNodeName(String nodeName) {
        return this.nodeNameList.add(nodeName);
    }

    
    public boolean removeNodeName(String nodeName) {
        return this.nodeNameList.remove(nodeName);
    }

    
    public Collection<String> getNodeNameList() {
        return nodeNameList;
    }

    
    public int getMode() {
        return mode;
    }

    
    public void setMode(int mode) {
        this.mode = mode;
    }

    
    public Calendar getStartTime() {
        return startTime;
    }

    
    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    
    public Calendar getEndTime() {
        return endTime;
    }

    
    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    
    public int getReadMode() {
        return readMode;
    }

    
    public void setReadMode(int sortMode) {
        this.readMode = sortMode;
    }

    
    public String getExtraArgs() {
        return extraArgs;
    }

    
    public void setExtraArgs(String extraArgs) {
        this.extraArgs = extraArgs;
    }

    
    public int getTopN() {
        return topN;
    }

    
    public void setTopN(int topN) {
        this.topN = topN;
    }

    
    public int getResultShow() {
        return resultShowMode;
    }

    
    public void setResultShow(int resultShow) {
        this.resultShowMode = resultShow;
    }

    
    public int getDateRange() {
        return dateRange;
    }

    
    public void setDateRange(int dateRange) {
        this.dateRange = dateRange;
    }

    
    public int getMaxUserShow() {
        return maxUserShow;
    }

    
    public void setMaxUserShow(int maxUserShow) {
        this.maxUserShow = maxUserShow;
    }
}
