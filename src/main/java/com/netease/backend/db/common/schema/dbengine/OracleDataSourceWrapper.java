
package com.netease.backend.db.common.schema.dbengine;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.pool.OracleDataSource;

import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnDataSource;


public class OracleDataSourceWrapper implements DbnDataSource {

    
    private static final long serialVersionUID = 1L;

    private final OracleDataSource _wrapper;
    private final Database _database;

    public OracleDataSourceWrapper(OracleDataSource dataSource,
            Database database) throws SQLException {
        this._wrapper = dataSource;
        this._database = database;
    }

    
    public Connection getConnection() throws SQLException {
        final Connection conn = this._wrapper.getConnection();

        
        final Statement stat = conn.createStatement();
        for (final String initStatement : this._database
                .getConnectionInitStatements()) {
            stat.addBatch(initStatement);
        }
        stat.executeBatch();
        stat.close();

        return conn;
    }

    
    public Connection getConnection(String user, String password)
            throws SQLException {
        this._wrapper.setUser(this._database.getRealUserName(user));
        this._wrapper.setPassword(password);
        final Connection conn = this.getConnection();

        return conn;
    }

    
    public int getLoginTimeout() throws SQLException {
        return this._wrapper.getLoginTimeout();
    }

    
    public PrintWriter getLogWriter() throws SQLException {
        return this._wrapper.getLogWriter();
    }

    
    public void setLoginTimeout(int seconds) throws SQLException {
        this._wrapper.setLoginTimeout(seconds);
    }

    
    public void setLogWriter(PrintWriter out) throws SQLException {
        this._wrapper.setLogWriter(out);
    }

    
    public Database getDatabase() {
        return this._database;
    }
}
