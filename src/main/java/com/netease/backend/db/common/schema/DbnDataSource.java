
package com.netease.backend.db.common.schema;

import javax.sql.DataSource;


public interface DbnDataSource extends DataSource {

    public Database getDatabase();
}
