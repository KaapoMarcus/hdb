
package com.netease.backend.db.common.schema;

import javax.sql.XADataSource;


public interface DbnXADataSource extends XADataSource {

    public Database getDatabase();
}
