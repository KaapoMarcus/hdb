
package com.netease.backend.db.common.stat;

import java.io.Serializable;

import com.netease.backend.db.common.utils.ObjectTable;


public class SlowlogHistoryResult implements Serializable {

    
    private static final long serialVersionUID = 1L;

    
    public static int STATUS_UNHANDLED = -1;
    public static int STATUS_SUCCESS = 0;
    public static int STATUS_FAILED = 1;
    public static int STATUS_PENDING = 2;

    
    private int resultStatus;
    private String resultStatusString;
    private ObjectTable resultTable;

    public SlowlogHistoryResult(int resultStatus, String resultStatusString,
            ObjectTable resultTable) {
        this.resultStatus = resultStatus;
        this.resultStatusString = resultStatusString;
        this.resultTable = resultTable;
    }

    
    public int getResultStatus() {
        return resultStatus;
    }

    
    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
    }

    
    public ObjectTable getResultTable() {
        return resultTable;
    }

    
    public void setResultTable(ObjectTable resultTable) {
        this.resultTable = resultTable;
    }

    
    public String getResultStatusString() {
        return resultStatusString;
    }

    
    public void setResultStatusString(String resultStatusString) {
        this.resultStatusString = resultStatusString;
    }

}
