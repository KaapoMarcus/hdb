package com.netease.backend.db.common.schema.dbengine;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.definition.Definition;
import com.netease.backend.db.common.schema.ConnectingHost;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnDataSource;
import com.netease.backend.db.common.schema.DbnXADataSource;
import com.netease.backend.db.common.schema.EntityPrivilege;
import com.netease.backend.db.common.schema.User;
import com.netease.backend.db.common.schema.type.dbengine.MysqlEntityPrivilegeType;
import com.netease.backend.db.common.utils.Validator;


public class MysqlDatabase extends Database {

    private static final long serialVersionUID = 1L;

    
    protected MysqlDatabase(final int id, final String name, final String url,
            final String domainSchemaName, final String defaultTablespace)
            throws IllegalArgumentException {
        super(id, name, url, domainSchemaName, defaultTablespace);

        
        this.init();
    }

    
    private void init() {
        
        this.setManagementUserName(Definition.DEFAULT_MYSQL_SYS_USER);

        
        this.setTestQuery("select 1");
    }

    
    
    @Override
    public String getRealUserName(String userName) {
        if (userName.equals(Definition.DEFAULT_SYS_USER)) {
            userName = Definition.DEFAULT_MYSQL_SYS_USER;
        }

        return userName;
    }

    
    @Override
    public List<String> generateCreateUserStatements(final User user) {
        assert (user != null);

        final List<String> statements = new LinkedList<String>();

        
        Set<ConnectingHost> connectingHosts = new HashSet<ConnectingHost>();
        for (String ip : user.getClientIps()) {
            connectingHosts
                    .add(new ConnectingHost(ip, ConnectingHost.TYPE_APP));
        }
        for (String ip : user.getQsIps()) {
            connectingHosts.add(new ConnectingHost(ip, ConnectingHost.TYPE_QS));
        }
        statements.addAll(this.generateGrantStatementsForHosts(user,
                connectingHosts));

        return statements;
    }

    
    @Override
    public List<String> generateDropUserStatements(final User user) {
        assert (user != null);

        final List<String> statements = new LinkedList<String>();

        
        statements.add("DELETE FROM mysql.user WHERE user='" +
        		this.getRealUserName(user.getName()) + "'");
        statements.add("FLUSH PRIVILEGES");

        return statements;
    }

    
    public List<String> generateAddHostStatements(final User user,
            Set<ConnectingHost> connectingHosts) {
        assert (user != null || connectingHosts != null);

        return this.generateGrantStatementsForHosts(user, connectingHosts);
    }

    
    public List<String> generateDeleteHostStatements(final User user,
            Set<ConnectingHost> connectingHosts) {
        assert (user != null || connectingHosts != null);

        final List<String> statements = new LinkedList<String>();

        for (ConnectingHost connectingHost : connectingHosts) {
            
            statements.add("DELETE FROM mysql.user WHERE user='"
                    + this.getRealUserName(user.getName()) + "'"
                    + " AND host='" + connectingHost.getIp() + "'");
            statements.add("FLUSH PRIVILEGES");
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
        final Set<String> ipSet = new HashSet<String>();
        
        
        for (final ConnectingHost host : connectingHosts)
        	ipSet.add(host.getIp());

        for (final String ip : ipSet) {
            for (final EntityPrivilege privilege : privileges) {
                
                if (privilege.hasNonePrivileges()) {
                    continue;
                }
				
				
				
				
				
				

                statement.append("GRANT");
                
                if (privilege.hasAllPrivileges()) {
                    statement.append(" "
                            + MysqlEntityPrivilegeType.ALL.getIdentifier());
                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement
                                .append(" "
                                        + MysqlEntityPrivilegeType.READ
                                                .getIdentifier());
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + MysqlEntityPrivilegeType.WRITE
                                        .getIdentifier());
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement
                                .append(" "
                                        + MysqlEntityPrivilegeType.EXEC
                                                .getIdentifier());
                    }
                    if (privilege.hasGrantOption()) {
                    	 statement
                         .append(" "
                                 + MysqlEntityPrivilegeType.GRANT
                                         .getIdentifier());
                    }
                }
                if (statement.charAt(statement.length() - 1) == ',') {
                    statement.deleteCharAt(statement.length() - 1);
                }
                
                final String storedSchemaName = privilege.getSchemaName();
                final String schemaName = ("".equals(storedSchemaName) ? this
                        .getDomainSchemaName() : storedSchemaName);
                statement.append(" ON ");
                if (!"".equals(schemaName)) {
                    statement.append(schemaName + ".");
                }
                statement.append(privilege.getEntityName());
                
                statement.append(" TO '" + this.getRealUserName(user.getName())
                        + "'@'" + ip + "'");
                
                statement.append(" IDENTIFIED BY '" + user.getPassword() + "'");
                
                
                if (privilege.hasAllPrivileges() && privilege.hasGrantOption()) {
                    statement.append(" WITH GRANT OPTION");
                }
                
                statements.add(statement.toString());
                statement.delete(0, statement.length());
            }
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
        assert (user != null);
        assert (connectingHosts != null);

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
        final Set<String> ipSet = new HashSet<String>();

        
        for (final ConnectingHost host : connectingHosts)
        	ipSet.add(host.getIp());

        for (final String ip : ipSet) {
            for (final EntityPrivilege privilege : privileges) {
                
                if (privilege.hasNonePrivileges()) {
                    continue;
                }

                statement.append("REVOKE");
                if (privilege.hasAllPrivileges()) {
                	
                    statement.append(" "
                            + MysqlEntityPrivilegeType.ALL.getIdentifier()
                            + " GRANT OPTION");
                } else {
                    if (privilege.hasReadPrivilege()) {
                        statement
                                .append(" "
                                        + MysqlEntityPrivilegeType.READ
                                                .getIdentifier());
                    }
                    if (privilege.hasWritePrivilege()) {
                        statement.append(" "
                                + MysqlEntityPrivilegeType.WRITE
                                        .getIdentifier());
                    }
                    if (privilege.hasExecutePrivilege()) {
                        statement
                                .append(" "
                                        + MysqlEntityPrivilegeType.EXEC
                                                .getIdentifier());
                    }
                    if (privilege.hasGrantOption()) {
						statement
								.append(" "
										+ MysqlEntityPrivilegeType.GRANT
												.getIdentifier());
                    }
                    statement.deleteCharAt(statement.length() - 1);

                    
                    final String storedSchemaName = privilege.getSchemaName();
                    final String schemaName = ("".equals(storedSchemaName) ? this
                            .getDomainSchemaName()
                            : storedSchemaName);
                    statement.append(" ON " + schemaName + "."
                            + privilege.getEntityName());
                }

                
                statement.append(" FROM '"
                        + this.getRealUserName(user.getName()) + "'@'"
                        + ip + "'");
                
                statements.add(statement.toString());
                statement.delete(0, statement.length());
            }
        }

        return statements;
    }

    
    @Override
    public List<String> generateUserPwdChangeStatements(final User user,
            final String newPassword) {
        List<String> statements = new LinkedList<String>();
        StringBuilder statement = new StringBuilder();

        List<String> hosts = new LinkedList<String>();
        hosts.addAll(user.getClientIps());
        hosts.addAll(user.getQsIps());

        
        for (String clientIp : hosts) {
            statement.append("SET PASSWORD FOR '").append(
                    this.getRealUserName(user.getName()));
            statement.append("'@'").append(clientIp);
            statement.append("' = PASSWORD('").append(newPassword).append("')");
            statements.add(statement.toString());
            statement.delete(0, statement.length());
        }

        return statements;
    }

    
    
    public DbnDataSource getDataSource(final int socketTimeoutMs,
            final int loginTimeoutMs) throws SQLException {
        MysqlDataSource  mysqlDataSource = new MysqlDataSource();
        String url = this.getURL();
        mysqlDataSource.setURL(url);
        
        
        mysqlDataSource.setLoginTimeout(loginTimeoutMs / 1000);
        mysqlDataSource.setConnectTimeout(loginTimeoutMs);
        mysqlDataSource.setSocketTimeout(socketTimeoutMs);
        mysqlDataSource.setLoggerClassName("com.mysql.jdbc.log.NullLogger");
        updateDataSourcePropertiesWithUrl(mysqlDataSource, url);

        return new MysqlDataSourceWrapper(mysqlDataSource, this);
    }

    
    public DbnDataSource getDataSource(final Properties properties,
            final int socketTimeoutMs, final int loginTimeoutMs)
            throws SQLException {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        String url = this.getURL();
        mysqlDataSource.setURL(url);
        
        
        mysqlDataSource.setLoginTimeout(loginTimeoutMs / 1000);
        mysqlDataSource.setConnectTimeout(loginTimeoutMs);
        mysqlDataSource.setSocketTimeout(socketTimeoutMs);

        updateDataSourcePropertiesWithUrl(mysqlDataSource, url);
        
        return new MysqlDataSourceWrapper(mysqlDataSource, this);
    }

    
    public DbnXADataSource getXADataSource(final int socketTimeoutMs,
            final int loginTimeoutMs) throws SQLException {
    	MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        String url = this.getURL();
        mysqlXADataSource.setURL(url);
        
        
        mysqlXADataSource.setLoginTimeout(loginTimeoutMs / 1000);
        mysqlXADataSource.setConnectTimeout(loginTimeoutMs);
        mysqlXADataSource.setSocketTimeout(socketTimeoutMs);
        mysqlXADataSource.setLoggerClassName("com.mysql.jdbc.log.NullLogger");
        
        updateDataSourcePropertiesWithUrl(mysqlXADataSource, url);

        return new MysqlXADataSourceWrapper(mysqlXADataSource, this);
    }

    
    public DbnXADataSource getXADataSource(final Properties properties,
            final int socketTimeoutMs, final int loginTimeoutMs)
            throws SQLException {
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        String url = this.getURL();
        mysqlXADataSource.setURL(url);
        
        
        mysqlXADataSource.setLoginTimeout(loginTimeoutMs / 1000);
        mysqlXADataSource.setConnectTimeout(loginTimeoutMs);
        mysqlXADataSource.setSocketTimeout(socketTimeoutMs);
        
        updateDataSourcePropertiesWithUrl(mysqlXADataSource, url);

        return new MysqlXADataSourceWrapper(mysqlXADataSource, this);
    }

    

    @Override
    public Database copy() {
        return new MysqlDatabase(this.getId(), this.getName(), this.getURL(),
                this.getDomainSchemaName(), this.getDefaultTablespace());
    }

    
    @Override
    protected void parseUrl(final String url) throws IllegalArgumentException {
        if (url == null || url.length() == 0) { throw new IllegalArgumentException(
                "URL����Ϊ��"); }
        if (!url.startsWith("jdbc:mysql:
                "����ȷ��URL: " + url); }
        final int posBeforeIp = url.indexOf("
        final int length = 2;
        final int posBeforePort = url.indexOf(":", posBeforeIp);
        final int posLastSep = url.indexOf("/", posBeforePort + 2);
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
            this.setIp(url.substring(posBeforeIp + length, posLastSep));
            this.setPort(3306);
        }
        final int question = url.indexOf("?", posLastSep);
        if (question < 0) {
            this.setDatabase(url.substring(posLastSep + 1));
        } else {
        	String databaseStr = url.substring(posLastSep + 1, question);
        	if (databaseStr.length() == 0)
    			throw new IllegalArgumentException("����ȷ��URL: " + url);
            this.setDatabase(databaseStr);
        }
    }
    
    
    private static void updateDataSourcePropertiesWithUrl(MysqlDataSource dataSource, String url) {
    	if (dataSource == null || url == null || url.length() == 0)
    		return;
    	
    	String behavior = null;
        if ((behavior = getZeroDateTimeBehavior(url)) != null) {
        	dataSource.setZeroDateTimeBehavior(behavior);
        }
    }
    
    
    private static String getZeroDateTimeBehavior(String url) {
    	int paramIndex = url.indexOf("?");
    	if (paramIndex != -1) {
    		String paramString = url.substring(paramIndex + 1, url.length());
    		StringTokenizer queryParams = new StringTokenizer(paramString, "&");
			while (queryParams.hasMoreTokens()) {
				String parameterValuePair = queryParams.nextToken();

				int indexOfEquals = parameterValuePair.indexOf("=");

				String parameter = null;
				String value = null;

				if (indexOfEquals > 0) {
					parameter = parameterValuePair.substring(0, indexOfEquals);
					
					if (indexOfEquals + 1 < parameterValuePair.length()) {
						value = parameterValuePair.substring(indexOfEquals + 1);
					}
					
					if ((value != null && value.length() > 0)
							&& (parameter != null && parameter.length() > 0)
							&& parameter.equalsIgnoreCase("zeroDateTimeBehavior")) {
						return value;
					}
				}
			}
    	}
    	return null;
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
        return DbnType.MySQL;
    }
    
    
}
