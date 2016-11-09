package com.jhh.hdb.proxyserver.handlers.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.client.AServer;
import com.netease.backend.db.common.management.dbi.DBIContext;
import com.jhh.hdb.proxyserver.commands.AuthenticationCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.OkCmd;
import com.jhh.hdb.proxyserver.define.CharsetMappingTool;
import com.jhh.hdb.proxyserver.define.ErrorNoDef;
import com.jhh.hdb.proxyserver.define.ServerCapabilities;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.SessionStatusType;
import com.jhh.hdb.proxyserver.utils.ServerUtil;

/**
 * �����û���֤����
 * 
 *
 */
public class AuthenticationHandler implements MySQLCmdHandler {

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		AuthenticationCmd authenticationCmd = (AuthenticationCmd) cmd;
/*
		MySQLCmd err = checkCmd(authenticationCmd);
		if (err != null) {
			return err;
		}

		sessionContext.setClientFlags(authenticationCmd.getClientFlags());
		sessionContext.setClientCharset(Charset.forName(CharsetMapping
				.getCharset(authenticationCmd.getCharsetNumber())));
		sessionContext.setMaxPacketSize(authenticationCmd.getMaxPacketSize());
		sessionContext.setUsername(authenticationCmd.getUser());
		try {
			byte[] ps = authenticationCmd.getEncryptedPassword();
			if (ps == null) {
				ps = new byte[0];
			}
			sessionContext.setEncryptedPassword(ps);
		} catch (UnsupportedEncodingException e1) {
			return new ErrorCmd(e1);
		}

		if (ServerCapabilities.isConnectWithDB(authenticationCmd
				.getClientFlags())) {

			String databaseName = authenticationCmd.getDatabase();
			List<String> dbNames = QsUtils.separateDatabase(databaseName);

			List<String> urls = new ArrayList<String>(dbNames.size());
			for (String dbName : dbNames) {
				//������ddb�Ƿ���������ļ���
				if (!GlobalContext.getInstance().getQsConfig().getDdbMap()
						.containsKey(dbName)) {
					return updateNumber(new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
							ErrorNoDef.SQL_GENERAL,
							"database name can not be found on server: "
									+ dbName));
				}
				urls.add(GlobalContext.getInstance().getQsConfig().getDdbMap()
						.get(dbName));
			}

			try {
				String url = QsUtils.getQsConnectUrl(urls, sessionContext.getClientIp(),
						sessionContext.getRealPasswordSeed(), 
						GlobalContext.getInstance().getQsConfig().getPort());
				Connection conn = DriverManager.getConnection(url,
						sessionContext.getUsername(), sessionContext
								.getEncryptedPassword());
				
				sessionContext.setDbConnection(conn);

				//����õ�һ����ݿ�����
				String aDb = dbNames.get(0);
				//����ѯ�������е�ddb�����ʵ��master��һ�£�����Ҳ�����������Ϣ
				DBIContext context = AServer.getInstance().getContext()
						.getDBIContext(aDb);
				if (context == null) {
					Exception e = new Exception("ddb doesn't exist: " + aDb);
					return updateNumber(new ErrorCmd(e));
				}

				sessionContext.setPassword(context.getClientUserManager()
						.getUser(authenticationCmd.getUser()).getPassword());
			} catch (SQLException e) {
				return updateNumber(new ErrorCmd(e));
			}
		}
*/
		sessionContext.setSessionStatus(SessionStatusType.authenticated);
		return updateNumber(new OkCmd(sessionContext.getServerStatus().getStatus(),
				"connect succeed"));
	}

	/**
	 * �����ڲ����Ƿ�Ϸ�
	 * @param authenticationCmd
	 * @return
	 */
	private MySQLCmd checkCmd(AuthenticationCmd authenticationCmd) {
		int flag = authenticationCmd.getClientFlags();

		if (ServerCapabilities.isCompress(flag)) {
			return updateNumber(new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL, "server does not support compress."));
		}

		if (!ServerCapabilities.isProtocol41(flag)) {
			return updateNumber(new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL, "client must be protocol 41."));
		}

		/*int maxServerSize = GlobalContext.getInstance().getQsConfig()
				.getMaxPacketSize();
		if (authenticationCmd.getMaxPacketSize() > maxServerSize) {
			return updateNumber(new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL, "packet size too big:"
							+ authenticationCmd.getMaxPacketSize()
							+ ", max server packet size:" + maxServerSize));
		}*/

		if (authenticationCmd.getUser() == null
				|| authenticationCmd.getUser().length() == 0) {
			return updateNumber(new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL,
					"user name can not be null or empty"));
		}

		if (authenticationCmd.getDatabase() == null
				|| authenticationCmd.getDatabase().length() == 0) {
			return updateNumber(new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL,
					"database name can not be null or empty"));

		}
		return null;
	}

	/**
	 * �������趨Ϊ2
	 * @param cmd
	 * @return
	 */
	public static MySQLCmd updateNumber(MySQLCmd cmd) {
		//���ֵ���صİ��Ŷ�ӦΪ2
		cmd.setNumber((byte) 2);
		return cmd;
	}
}
