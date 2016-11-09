
package com.netease.backend.db.common.sql;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;


public class SSlowlogHistory extends Statement {

    
    private static final long serialVersionUID = 1L;

    public static String[] COLUMN_HEADERS_ALL = new String[] { "node",
            "task_id", "task_time", "start_time", "end_time", "count",
            "time_total", "time_max", "time_min", "lock_time_total",
            "lock_time_max", "lock_time_min", "rows_sent_avg", "rows_sent_max",
            "rows_sent_min", "rows_examined_avg", "rows_examined_max",
            "rows_examined_min", "db", "users", "query_abstract",
            "query_sample" };
    public static String[] COLUMN_HEADERS_TASKGROUP = new String[] {
            "task_time", "start_time", "end_time", "task_id", "nodes" };

    
    public static int MODE_READ_RESULT = 1;
    public static int MODE_READ_TASKGROUP = 2;

    public static int TIMEMODE_SOURCE = 1;
    public static int TIMEMODE_EXEC = 2;

    
    
    private int mode;
    private List<Integer> taskIds;
    private Collection<String> nodeNames;
    private int timeMode = SSlowlogHistory.TIMEMODE_SOURCE;
    private Calendar startTime;
    private Calendar endTime;

    
    public SSlowlogHistory(int mode, Collection<String> nodeNames,
            int timeMode, Calendar startTime, Calendar endTime) {
        this.mode = mode;
        this.nodeNames = nodeNames;
        this.timeMode = timeMode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.taskIds = null;
    }

    public SSlowlogHistory(List<Integer> taskIds) {
        this.mode = SSlowlogHistory.MODE_READ_RESULT;
        this.nodeNames = null;
        this.startTime = null;
        this.endTime = null;
        this.taskIds = taskIds;
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

    
    public Collection<String> getNodeNames() {
        return nodeNames;
    }

    
    public void setNodeNames(Collection<String> nodeNames) {
        this.nodeNames = nodeNames;
    }

    
    public int getTimeMode() {
        return timeMode;
    }

    
    public void setTimeMode(int timeMode) {
        this.timeMode = timeMode;
    }

    
    public List<Integer> getTaskIds() {
        return taskIds;
    }
}
