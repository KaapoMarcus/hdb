package com.jhh.hdb.proxyserver.handlers.mysql;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtCloseCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.StmtContext;

/**
 * �ر�һ��preparestatement����
 * 
 *
 */
public class StmtCloseHandler implements MySQLCmdHandler {

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		StmtCloseCmd stmtCloseCmd = (StmtCloseCmd) cmd;

		// ɾ���������б����pstmt��Ϣ
		StmtContext stmtContext = sessionContext.getStmtContextMap().remove(
				stmtCloseCmd.getStatementId());

		if (stmtContext != null) {
			stmtContext.closeResultset();

			stmtContext.closePstmt();
		}
		//���뷵�ؽ������ᵼ�¿ͻ��˶�ȡ��ݰ����
		return null;
	}
}
