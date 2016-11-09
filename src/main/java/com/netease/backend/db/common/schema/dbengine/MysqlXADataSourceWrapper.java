
package com.netease.backend.db.common.schema.dbengine;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnXADataSource;


public class MysqlXADataSourceWrapper implements DbnXADataSource {

    private final MysqlXADataSource _wrapper;
    private final Database _database;

    
    public MysqlXADataSourceWrapper(final MysqlXADataSource mxads,
            final Database database) {
        this._wrapper = mxads;
        this._database = database;
    }

    
    public int getLoginTimeout() throws SQLException {
        return this._wrapper.getLoginTimeout();
    }

    
    public XAConnection getXAConnection() throws SQLException {
        final XAConnection xaConn = this._wrapper.getXAConnection();

        
        final Connection conn = xaConn.getConnection();
        final Statement stat = conn.createStatement();
        for (final String initStatement : this._database
                .getConnectionInitStatements()) {
            stat.addBatch(initStatement);
        }
        stat.executeBatch();
        stat.close();
        conn.close();

        return xaConn;
    }

    
    public XAConnection getXAConnection(final String user, final String password)
            throws SQLException {
        this._wrapper.setUser(this._database.getRealUserName(user));
        this._wrapper.setPassword(password);

        return this.getXAConnection();
    }

    
    public PrintWriter getLogWriter() throws SQLException {
        return this._wrapper.getLogWriter();
    }

    
    public void setLogWriter(final PrintWriter out) throws SQLException {
        this._wrapper.setLogWriter(out);
    }

    
    public void setLoginTimeout(final int seconds) throws SQLException {
        this._wrapper.setLoginTimeout(seconds);
    }

    
    public Database getDatabase() {
        return this._database;
    }

}
