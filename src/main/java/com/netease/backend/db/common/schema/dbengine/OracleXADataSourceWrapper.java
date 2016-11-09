
package com.netease.backend.db.common.schema.dbengine;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;

import oracle.jdbc.xa.client.OracleXADataSource;

import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnXADataSource;


public class OracleXADataSourceWrapper implements DbnXADataSource {

    private static final long serialVersionUID = 1L;

    private final OracleXADataSource _wrapper;
    private final Database _database;

    
    public OracleXADataSourceWrapper(final OracleXADataSource oxads,
            final Database database) throws SQLException {
        this._wrapper = oxads;
        this._database = database;
    }

    
    public int getLoginTimeout() throws SQLException {
        return this._wrapper.getLoginTimeout();
    }

    
    public PrintWriter getLogWriter() throws SQLException {
        return this._wrapper.getLogWriter();
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

    
    public void setLoginTimeout(final int seconds) throws SQLException {
        this._wrapper.setLoginTimeout(seconds);
    }

    
    public void setLogWriter(final PrintWriter out) throws SQLException {
        this._wrapper.setLogWriter(out);
    }

    
    public Database getDatabase() {
        return this._database;
    }

}
