

package com.jhh.hdb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.utils.PriorityQueue;
import com.netease.backend.db.result.Comparator;
import com.netease.backend.db.result.Record;
import com.netease.backend.db.sql.Select;
import com.netease.backend.db.sql.expression.OrderBy;

/**
 * 
 */
public final class ResultSetOperator extends PriorityQueue {
    private List<OrderBy> conditions;
    private MyResultSetMetaData md;
    private Select select;
    

    public ResultSetOperator(ResultSet[] results ) {
        initialize(results.length);
 
        try {
            for (int i = 0; i < results.length; i++) {
                // ignore empty ResultSet, and set cursor in the first record
                if (results[i] != null && results[i].next()) {
                    this.put(results[i]);
                    
                    if (md == null)
                    	md = new MyResultSetMetaData(results[i].getMetaData(), select);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected boolean lessThan(Object a, Object b) {
        ResultSet rsa = (ResultSet) a;
        ResultSet rsb = (ResultSet) b;

//        if (Comparator.CompareResultSet(rsa, rsb, conditions, md, select.getDbnType()) < 0)
//            return true;
        return false;
    }

    public Record getNextTuple() throws SQLException {
        if (this.size() > 0) {
            ResultSet rs = (ResultSet) this.pop();
            Record rec = Record.getRecord(rs, md, DbnType.MySQL);
            if (rs.next()) {
                this.put(rs);
            }
            return rec;
        }
        return null;
    }


    
    public void clear() {
        this.conditions = null;
        this.md = null;
    }
}
