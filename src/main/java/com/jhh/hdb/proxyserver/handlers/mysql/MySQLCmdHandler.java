package com.jhh.hdb.proxyserver.handlers.mysql;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * mysql����?��
 * 
 *
 */
public interface MySQLCmdHandler {
	/**
	 * ����һ��mysql����
	 * @param sessionContext session��Ϣ������
	 * @param cmd mysql����
	 * @return
	 */
	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd);
}
