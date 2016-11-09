package com.jhh.hdb.sqlparser2.antiSQLInjection;

import java.util.Map;

public interface GContext {

    void setVars(Map vars);
    Map getVars();

}
