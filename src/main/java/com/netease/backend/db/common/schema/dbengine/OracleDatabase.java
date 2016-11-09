
package com.netease.backend.db.common.schema.dbengine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.xa.client.OracleXADataSource;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.definition.Definition;
import com.netease.backend.db.common.schema.ConnectingHost;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnDataSource;
import com.netease.backend.db.common.schema.DbnXADataSource;
import com.netease.backend.db.common.schema.EntityPrivilege;
import com.netease.backend.db.common.schema.User;
import com.netease.backend.db.common.schema.type.dbengine.OracleEntityPrivilegeType;
import com.netease.backend.db.common.utils.Validator;


public class OracleDatabase extends Database {

    
    private static final long serialVersionUID = 1L;

    protected OracleDatabase(final int id, final String name, final String url,
            final String domainSchemaName, final String defaultTablespace)
            throws IllegalArgumentException {
        super(id, name, url, "".equals(domainSchemaName)?Definition.DEFAULT_ORACLE_SYS_USER:domainSchemaName, defaultTablespace);

        
        this.init();
    }

    
    private void init() {
        
        this.setManagementUserName(Definition.DEFAULT_ORACLE_SYS_USER);
        
        final List<String> connectionInitStatements = new ArrayList<String>();
        
        
        connectionInitStatements.add("ALTER SESSION SET CURRENT_SCHEMA = "
                + this.getDomainSchemaName());
        
        connectionInitStatements.add("ALTER SESSION SET NLS_DATE_FORMAT = 'yyyy-mm-dd hh24:mi:ss'");
        connectionInitStatements.add("ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'yyyy-mm-dd hh24:mi:ss'");
        
        this.setConnectionInitStatements(connectionInitStatements);
        
        this.setTestQuery("select 1 from dual");
    }

    
    
    @Override
    public String getRealUserName(String userName) {
        if (userName.equals(Definition.DEFAULT_SYS_USER)) {
            
            userName = Definition.DEFAULT_ORACLE_SYS_USER;
        }

        return userName;
    }

    
    @Override
    public List<String> generateCreateUserStatements(final User user) {
        assert (user != null);

        final List<String> statements = new LinkedList<String>();
        final StringBuilder statement = new StringBuilder();

        statement.append("CREATE USER");
        statement.append(" \"" + this.getRealUserName(user.getName()) + "\"");
        
        statement.append(" IDENTIFIED BY \"" + user.getPassword() + "\"");
        
		final String tableSpace;
		if ((tableSpace = this.getDefaultTablespace()) != null
				&& tableSpace.length() > 0) {
			
			statement.append(" DEFAULT TABLESPACE \"" + tableSpace + "\"");
			
			statement.append(" QUOTA UNLIMITED ON \"" + tableSpace + "\"");
		}

        
        statements.add(statement.toString());
        statement.delete(0, statement.length());

        
        Set<ConnectingHost> connectingHosts = new HashSet<ConnectingHost>();
        for (String ip : user.getClientIps()) {
            connectingHosts
                    .add(new ConnectingHost(ip, ConnectingHost.TYPE_APP));
        }
        for (String ip : user.getQsIps()) {
            connectingHosts
                    .add(new ConnectingHost(ip, ConnectingHost.TYPE_QS));
        }

        
        statements
                .addAll(this.generateAddHostStatements(user, connectingHosts));

        
        statements.addAll(this.generateGrantStatements(user, connectingHosts,
                user.getEntityPrivileges()));

        
        statements.add("GRANT CREATE SESSION TO \""
                + this.getRealUserName(user.getName()) + "\"");

        return statements;
    }

    
    @Override
    public List<String> generateDropUserStatements(final User user) {
        assert (user != null);

        final List<String> statements = new LinkedList<String>();
        final StringBuilder statement = new StringBuilder();

        statement.append("DROP USER");
        
        statement.append(" \"" + this.getRealUserName(user.getName()) + "\"");
        
        statement.append(" CASCADE");
        
        statements.add(statement.toString());
        statement.delete(0, statement.length());

        
        statement.append("DELETE FROM " + this.getManagementUserName()
                + ".USER_AUTHORIZATION");
        statement.append(" WHERE USER_NAME = '" + user.getName() + "'");
        statements.add(statement.toString());
        statement.delete(0, statement.length());

        return statements;
    }

    
    public List<String> generateAddHostStatements(final User user,
            Set<ConnectingHost> connectingHosts) {
        assert (user != null || connectingHosts != null);

        final List<String> statements = new LinkedList<String>();
        final StringBuilder statement = new StringBuilder();

        
        for (ConnectingHost connectingHost : connectingHosts) {
            
            statement.append("DELETE FROM " + this.getManagementUserName()
                    + ".USER_AUTHORIZATION UA ");
            statement.append(" WHERE USER_NAME = '"
                    + this.getRealUserName(user.getName()) + "'");
            statement.append(" AND HOST_TYPE = '" + connectingHost.getType()
                    + "'");
            statement.append(" AND IP_ADDRESS = '" + connectingHost.getIp()
                    + "'");
            statements.add(statement.toString());
            statement.delete(0, statement.length());

            
            statement.append("INSERT INTO " + this.getManagementUserName()
                    + ".USER_AUTHORIZATION UA ");
            statement.append(" VALUES ('"
                    + this.getRealUserName(user.getName()) + "',");
            statement.append(" '" + connectingHost.getType() + "',");
            statement.append(" '" + connectingHost.getIp() + "')");

            statements.add(statement.toString());
            statement.delete(0, statement.length());
        }

        return statements;
    }

    
    public List<String> generateDeleteHostStatements(final User user,
            Set<ConnectingHost> connectingHosts) {
        assert (user != null || connectingHosts != null);
        final List<String> statements = new LinkedList<String>();

        
        for (ConnectingHost connectingHost : connectingHosts) {
            statements.add("DELETE FROM " + this.getManagementUserName()
                    + ".USER_AUTHORIZATION WHERE USER_NAME = '"
                    + this.getRealUserName(user.getName()) + "'"
                    + " AND HOST_TYPE = '" + connectingHost.getType() + "'"
                    + " AND IP_ADDRESS = '" + connectingHost.getIp() + "'");
        }
        return statements;
    }

    
    @Override
    public List<String> generateGrantStatementsForUser(final User user) {
        assert (user != null);

        final Set<ConnectingHost> connectingHosts = new HashSet<ConnectingHost>();
        final Set<EntityPrivilege> privileges = new HashSet<EntityPrivilege>();

        
        for (String ip : user.getClientIps()) {
            connectingHosts
                    .add(new ConnectingHost(ip, ConnectingHost.TYPE_APP));
        }
        for (String ip : user.getQsIps()) {
            connectingHosts.add(new ConnectingHost(ip, ConnectingHost.TYPE_QS));
        }

        
        privileges.addAll(user.getEntityPrivileges());

        return this.generateGrantStatements(user, connectingHosts, privileges);
    }

    
    @Override
    public List<String> generateGrantStatementsForHosts(final User user,
            final Set<ConnectingHost> connectingHosts) {
        assert (user != null);
        assert (connectingHosts != null);

        final Set<EntityPrivilege> privileges = new HashSet<EntityPrivilege>();

        privileges.addAll(user.getEntityPrivileges());

        return this.generateGrantStatements(user, connectingHosts, privileges);
    }

    
    @Override
    public List<String> generateGrantStatementsForPrivileges(final User user,
            final Set<EntityPrivilege> privileges) {
        assert (user != null);
        assert (privileges != null);

        final Set<ConnectingHost> connectingHosts = new HashSet<ConnectingHost>();

        
        for (String ip : user.getClientIps()) {
            connectingHosts
                    .add(new ConnectingHost(ip, ConnectingHost.TYPE_APP));
        }
        for (String ip : user.getQsIps()) {
            connectingHosts.add(new ConnectingHost(ip, ConnectingHost.TYPE_QS));
        }

        return this.generateGrantStatements(user, connectingHosts, privileges);
    }

    
    private List<String> generateGrantStatements(final User user,
            final Set<ConnectingHost> connectingHosts,
            final Set<EntityPrivilege> privileges) {
        assert (user != null || connectingHosts != null || privileges != null);

        final List<String> statements = new LinkedList<String>();
        final StringBuilder statement = new StringBuilder();

        for (final EntityPrivilege privilege : privileges) {
            
            if (privilege.hasNonePrivileges()) {
                continue;
            }

			
			
			
			
			

            
            
            boolean objectPrivilege = false;

            
            statement.append("GRANT");
            if ("*".equals(privilege.getSchemaName())
                    && "*".equals(privilege.getEntityName())) {
                
                if (privilege.hasAllPrivileges()) {
                    statement.append(" "
                            + OracleEntityPrivilegeType.GLOBAL_ALL
                                    .getIdentifier());

                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.GLOBAL_READ
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.GLOBAL_WRITE
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.GLOBAL_EXEC
                                        .getIdentifier() + ",");
                    }
                }
            } else if ("*".equals(privilege.getEntityName())) {
                
                if (!"".equals(privilege.getSchemaName())) {
                    statement.delete(0, statement.length());
                    continue;
                }
                
                if (privilege.hasAllPrivileges()) {
                    statement.append(" "
                            + OracleEntityPrivilegeType.DOMAIN_ALL
                                    .getIdentifier());
                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.DOMAIN_READ
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.DOMAIN_WRITE
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.DOMAIN_EXEC
                                        .getIdentifier() + ",");
                    }
                }
            } else {
                
                objectPrivilege = true;
                if (privilege.hasAllPrivileges()) {
                    statement.append(" "
                            + OracleEntityPrivilegeType.ALL.getIdentifier());
                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.READ
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.WRITE
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.EXEC
                                        .getIdentifier() + ",");
                    }
                }
            }
            if (statement.charAt(statement.length() - 1) == ',') {
                statement.deleteCharAt(statement.length() - 1);
            }

            
            if (objectPrivilege == true) {
                final String storedSchemaName = privilege.getSchemaName();
                final String schemaName = ("".equals(storedSchemaName) ? this
                        .getDomainSchemaName() : storedSchemaName);
                statement.append(" ON " + schemaName + "."
                        + privilege.getEntityName());
            }
            
            statement.append(" TO \"" + this.getRealUserName(user.getName())
                    + "\"");
            
            if (privilege.hasGrantOption()) {
                if (objectPrivilege == true) {
                    statement.append(" WITH GRANT OPTION");
                }
            }
            
            statements.add(statement.toString());
            statement.delete(0, statement.length());
        }

        return statements;
    }

    
    @Override
    public List<String> generateRevokeStatementsForUser(final User user) {
        final Set<ConnectingHost> connectingHosts = new HashSet<ConnectingHost>();
        final Set<EntityPrivilege> privileges = new HashSet<EntityPrivilege>();

        
        
        for (String ip : user.getClientIps()) {
            connectingHosts
                    .add(new ConnectingHost(ip, ConnectingHost.TYPE_APP));
        }
        for (String ip : user.getQsIps()) {
            connectingHosts.add(new ConnectingHost(ip, ConnectingHost.TYPE_QS));
        }

        
        privileges.addAll(user.getEntityPrivileges());

        return this.generateRevokeStatements(user, connectingHosts, privileges);
    }

    
    @Override
    public List<String> generateRevokeStatementsForHosts(final User user,
            final Set<ConnectingHost> connectingHosts) {
        final Set<EntityPrivilege> privileges = new HashSet<EntityPrivilege>();

        privileges.addAll(user.getEntityPrivileges());

        return this.generateRevokeStatements(user, connectingHosts, privileges);
    }

    
    @Override
    public List<String> generateRevokeStatementsForPrivileges(final User user,
            final Set<EntityPrivilege> privileges) {
        final Set<ConnectingHost> connectingHosts = new HashSet<ConnectingHost>();

        
        for (String ip : user.getClientIps()) {
            connectingHosts
                    .add(new ConnectingHost(ip, ConnectingHost.TYPE_APP));
        }
        for (String ip : user.getQsIps()) {
            connectingHosts.add(new ConnectingHost(ip, ConnectingHost.TYPE_QS));
        }

        return this.generateRevokeStatements(user, connectingHosts, privileges);
    }

    
    private List<String> generateRevokeStatements(final User user,
            final Set<ConnectingHost> connectingHosts,
            final Set<EntityPrivilege> privileges) {
        assert (user != null || connectingHosts != null || privileges != null);

        final List<String> statements = new LinkedList<String>();
        final StringBuilder statement = new StringBuilder();

        for (final EntityPrivilege privilege : privileges) {
            
            if (privilege.hasNonePrivileges()) {
                continue;
            }

			
			
			
			
			

            
            
            boolean objectPrivilege = false;

            
            statement.append("REVOKE");
            if ("*".equals(privilege.getSchemaName())
                    && "*".equals(privilege.getEntityName())) {
                
                if (privilege.hasAllPrivileges()) {
                    statement.append(" "
                            + OracleEntityPrivilegeType.GLOBAL_ALL
                                    .getIdentifier());
                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.GLOBAL_READ
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.GLOBAL_WRITE
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.GLOBAL_EXEC
                                        .getIdentifier() + ",");
                    }
                }
            } else if ("*".equals(privilege.getEntityName())) {
                
                if (!"".equals(privilege.getSchemaName())) {
                    statement.delete(0, statement.length());
                    continue;
                }

                
                if (privilege.hasAllPrivileges()) {
                    statement.append(" "
                            + OracleEntityPrivilegeType.DOMAIN_ALL
                                    .getIdentifier());
                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.DOMAIN_READ
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.DOMAIN_WRITE
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.DOMAIN_EXEC
                                        .getIdentifier() + ",");
                    }
                }
            } else {
                
                objectPrivilege = true;
                if (privilege.hasAllPrivileges()) {
                    statement.append(" "
                            + OracleEntityPrivilegeType.ALL.getIdentifier());
                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.READ
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.WRITE
                                        .getIdentifier() + ",");
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement.append(" "
                                + OracleEntityPrivilegeType.EXEC
                                        .getIdentifier() + ",");
                    }
                }
            }
            if (statement.charAt(statement.length() - 1) == ',') {
                statement.deleteCharAt(statement.length() - 1);
            }

            
            if (objectPrivilege == true) {
                final String storedSchemaName = privilege.getSchemaName();
                final String schemaName = ("".equals(storedSchemaName) ? this
                        .getDomainSchemaName() : storedSchemaName);
                statement.append(" ON " + schemaName + "."
                        + privilege.getEntityName());
            }
            
            statement.append(" FROM \"" + this.getRealUserName(user.getName())
                    + "\"");
            
            
            if (privilege.hasGrantOption()) {
                statement.append(" CASCATE CONSTRAINTS");
            }
            
            statements.add(statement.toString());
            statement.delete(0, statement.length());
        }

        return statements;
    }

    
    @Override
    public List<String> generateUserPwdChangeStatements(final User user,
            final String newPassword) {
        final List<String> statements = new LinkedList<String>();
        final StringBuilder statement = new StringBuilder();

        
        if (!newPassword.equals(user.getPassword())) {
            statement.append("ALTER USER  \"").append(user.getName()).append(
                    "\"");
            statement.append(" IDENTIFIED BY \"").append(newPassword).append(
                    "\"");
            statement.append(" REPLACE \"").append(user.getPassword()).append(
                    "\"");

            statements.add(statement.toString());
            statement.delete(0, statement.length());
        }

        
        Set<String> connectingHosts = new HashSet<String>();
        connectingHosts.addAll(user.getClientIps());
        connectingHosts.addAll(user.getQsIps());

        return statements;
    }

    
    
    public DbnDataSource getDataSource(int socketTimeoutMs,
            final int loginTimeoutMs) throws SQLException {
        OracleDataSource oracleDataSource = new OracleDataSource();
        oracleDataSource.setURL(this.getURL());
        oracleDataSource.setLoginTimeout(loginTimeoutMs / 1000);

        return new OracleDataSourceWrapper(oracleDataSource, this);
    }

    
    public DbnDataSource getDataSource(final Properties properties,
            int socketTimeoutMs, final int loginTimeoutMs) throws SQLException {
        OracleDataSource oracleDataSource = new OracleDataSource();
        oracleDataSource.setURL(this.getURL());
        oracleDataSource.setConnectionProperties(properties);
        oracleDataSource.setLoginTimeout(loginTimeoutMs / 1000);

        return new OracleDataSourceWrapper(oracleDataSource, this);
    }

    
    public DbnXADataSource getXADataSource(int socketTimeoutMs,
            final int loginTimeoutMs) throws SQLException {
    	OracleXADataSource oracleXADataSource = new OracleXADataSource();
        oracleXADataSource.setURL(this.getURL());
        oracleXADataSource.setLoginTimeout(loginTimeoutMs / 1000);

        return new OracleXADataSourceWrapper(oracleXADataSource, this);
    }

    
    public DbnXADataSource getXADataSource(final Properties properties,
            int socketTimeoutMs, final int loginTimeoutMs) throws SQLException {
        OracleXADataSource oracleXADataSource = new OracleXADataSource();
        oracleXADataSource.setURL(this.getURL());
        oracleXADataSource.setConnectionProperties(properties);
        oracleXADataSource.setLoginTimeout(loginTimeoutMs / 1000);

        return new OracleXADataSourceWrapper(oracleXADataSource, this);
    }

    @Override
    public Database copy() {
        return new OracleDatabase(this.getId(), this.getName(), this.getURL(),
                this.getDomainSchemaName(), this.getDefaultTablespace());
    }

    
    @Override
    protected void parseUrl(final String url) throws IllegalArgumentException {
        if (url == null || url.length() == 0) { throw new IllegalArgumentException(
                "URL����Ϊ��"); }
        if (!url.startsWith("jdbc:oracle:thin:@")) { throw new IllegalArgumentException(
                "����ȷ��URL: " + url); }
        final int posBeforeIp = url.indexOf("@");
        final int length = 1;
        final int posBeforePort = url.indexOf(":", posBeforeIp);
        final int posLastSep = url.indexOf(":", posBeforePort + 2);
        if (posBeforePort < 0 && posLastSep < 0) { throw new IllegalArgumentException(
                "����ȷ��URL:" + url); }
        if (posBeforePort > 0) {
            this.setIp(url.substring(posBeforeIp + length, posBeforePort));
            final String portString = url.substring(posBeforePort + 1,
                    posLastSep);
            try {
                this.setPort(Integer.parseInt(portString));
            } catch (final NumberFormatException e) {
                throw new IllegalArgumentException("����ȷ�Ķ˿ں�:" + portString);
            }
            if (!Validator.isPort(this.getPort())) { throw new IllegalArgumentException(
                    "����ȷ�Ķ˿ں�:" + portString); }
        } else {
            throw new IllegalArgumentException("����ȷ��URL:" + url);
        }
        final int question = url.indexOf("?", posLastSep);
        if (question < 0) {
            this.setDatabase(url.substring(posLastSep + 1));
        } else {
            
            
            throw new IllegalArgumentException("URL���ܰ����û����������: " + url);
        }
    }

    

    
    @Override
    public boolean isSameDBType(final Database another) {
        if (another.getDbnType() == this.getDbnType()) {
            return true;
        } else {
            return false;
        }
    }

    

    
    @Override
    public DbnType getDbnType() {
        return DbnType.Oracle;
    }
}