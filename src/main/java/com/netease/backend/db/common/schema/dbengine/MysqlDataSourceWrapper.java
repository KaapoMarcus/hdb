
package com.netease.backend.db.common.schema.dbengine;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnDataSource;


public class MysqlDataSourceWrapper implements DbnDataSource {

    private MysqlDataSource _wrapper;
    private Database _database;

    
    public MysqlDataSourceWrapper(MysqlDataSource mds, Database database) {
        this._wrapper = mds;
        this._database = database;
    }

    
    public Connection getConnection() throws SQLException {
        
        Connection conn = this._wrapper.getConnection();
        Statement stat = conn.createStatement();
        for (String initStatement : this._database.getConnectionInitStatements()) {
            stat.addBatch(initStatement);
        }
        stat.executeBatch();
        stat.close();

        return conn;
    }

    
    public Connection getConnection(String username, String password)
            throws SQLException {
        this._wrapper.setUser(this._database.getRealUserName(username));
        this._wrapper.setPassword(password);

        return this.getConnection();
    }

    
    public PrintWriter getLogWriter() throws SQLException {
        return this._wrapper.getLogWriter();
    }

    
    public int getLoginTimeout() throws SQLException {
        return this._wrapper.getLoginTimeout();
    }

    
    public void setLogWriter(PrintWriter out) throws SQLException {
        this._wrapper.setLogWriter(out);
    }

    
    public void setLoginTimeout(int seconds) throws SQLException {
        this._wrapper.setLoginTimeout(seconds);
    }

    
    public Database getDatabase() {
        return this._database;
    }

}
