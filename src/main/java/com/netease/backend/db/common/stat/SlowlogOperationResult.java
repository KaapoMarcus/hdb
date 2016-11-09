package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SlowlogOperationResult implements Serializable {

    
    private static final long serialVersionUID = 1L;

    
    public static int STATUS_UNHANDLED = -1;
    public static int STATUS_SUCCESS = 0;
    public static int STATUS_FAILED = 1;
    public static int STATUS_PENDING = 2;

    
    private int status = SlowlogOperationResult.STATUS_UNHANDLED;
    private StringBuffer statusString = new StringBuffer();
    private Map<String, String> resultStringMap = new HashMap<String, String>();
    private Map<String, String> resultString4SQLMap = new HashMap<String, String>();
    private StringBuffer resultErrString = new StringBuffer();

    public SlowlogOperationResult() {

    }

    public SlowlogOperationResult(int status) {
        this.status = status;
    }

    public SlowlogOperationResult(int status, String statusString) {
        this.status = status;
        this.statusString.append(statusString);
    }

    
    public int getStatus() {
        return status;
    }

    
    public void setStatus(int status) {
        this.status = status;
    }

    
    public StringBuffer getStatusString() {
        return statusString;
    }

    
    public StringBuffer getResultErrString() {
        return resultErrString;
    }

    public int getNodeCount() {
        return this.resultStringMap.keySet().size();
    }

    public Collection<String> getNodeSet() {
        return this.resultStringMap.keySet();
    }

    public String getResultString(String nodeName) {
        return this.resultStringMap.get(nodeName);
    }

    public void putResultString(String nodeName, String output) {
        this.resultStringMap.put(nodeName, output);
    }

    public String getResultString4SQL(String nodeName) {
        return this.resultString4SQLMap.get(nodeName);
    }

    public void putResultString4SQL(String nodeName, String output) {
        this.resultString4SQLMap.put(nodeName, output);
    }
}
