
package com.netease.backend.db.common.sql;

import java.sql.SQLException;
import java.util.Map;


public interface HintedSQLParser {

    
    String getSQL();

    
    void setSQL(String sql) throws SQLException;

    
    Map<String, String> getHints();

    
    void setHints(Map<String, String> hints) throws SQLException;
}
