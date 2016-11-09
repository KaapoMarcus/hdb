
package com.netease.backend.db.common.schema.dbengine;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.definition.Definition;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.utils.SQLCommonUtils;
import com.netease.backend.db.common.utils.StringUtils;


public class DatabaseFactory {

    
    public Database newDatabase(String url, String dbnName,
            String domainSchemaName, String defaultTablespace, int id) {
        assert (domainSchemaName != null);
        assert (defaultTablespace != null);
        if (null == domainSchemaName) {
            throw new IllegalArgumentException(
                    "domainSchemaName should ot be null when creating database: "
                            + url);
        } else if (null == defaultTablespace) { throw new IllegalArgumentException(
                "defaultTablespace should not be null when creating database: "
                        + url); }

        defaultTablespace = defaultTablespace.trim();
        DbnType dbnType = SQLCommonUtils.getDbnTypeByUrl(url);

        switch (dbnType) {
        case MySQL:
            
            return new MysqlDatabase(id, dbnName, url, StringUtils
                    .getMySQLDbNameByUrl(url), defaultTablespace);
        case Oracle:
            return new OracleDatabase(id, dbnName, url, domainSchemaName,
                    defaultTablespace);
        default:
            throw new IllegalArgumentException("����ȷ�����ݿ�����: " + url);
        }
    }
}
