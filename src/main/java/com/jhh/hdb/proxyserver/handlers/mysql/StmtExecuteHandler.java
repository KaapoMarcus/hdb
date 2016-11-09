package com.jhh.hdb.proxyserver.handlers.mysql;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import com.netease.backend.db.DBPreparedStatement;
import com.netease.backend.db.DBResultSet;
import com.netease.backend.db.common.exceptions.SQLExceptionWithCause;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtExecuteCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.ResultSetCmd;
import com.jhh.hdb.proxyserver.define.ErrorNoDef;
import com.jhh.hdb.proxyserver.handlers.sql.SQLDispatch;
import com.jhh.hdb.proxyserver.handlers.sql.SQLExecutor;
import com.jhh.hdb.proxyserver.handlers.sql.SQLExecutorFactory;
import com.jhh.hdb.proxyserver.handlers.sql.SQLExecutorFactoryImpl;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.StmtContext;
import com.jhh.hdb.proxyserver.session.StmtLongData;
import com.jhh.hdb.proxyserver.utils.StmtPacketUtil;

/**
 * ִ��preparestatement���
 * 
 *
 */
public class StmtExecuteHandler implements MySQLCmdHandler {
	private SQLExecutorFactory sqlExecutorFactory = new SQLExecutorFactoryImpl();

	public static final byte CURSOR_TYPE_NO_CURSOR = 0;
	public static final byte CURSOR_TYPE_READ_ONLY = 1;
	public static final byte CURSOR_TYPE_FOR_UPDATE = 2;
	public static final byte CURSOR_TYPE_SCROLLABLE = 4;

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		StmtExecuteCmd stmtExecuteCmd = (StmtExecuteCmd) cmd;

		StmtContext stmtContext = sessionContext.getStmtContextMap().get(
				stmtExecuteCmd.getStatementId());
		if (stmtContext == null) {
			return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL,
					"execute preparedstatement failed, can not found statement id:"
							+ stmtExecuteCmd.getStatementId());
		}

		// �ر��ϴ������α�󻺴��resultset
		stmtContext.closeResultset();

		DBPreparedStatement pstmt = stmtContext.getPstmt();
		int oldFetchSize = 0;
		try {
			oldFetchSize = pstmt.getFetchSize();

			// ����ϴ�����blobʱ�Ѿ����?��ֱ�ӷ��ش�����Ϣ
			if (stmtContext.getErrorMsg() != null) {
				return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
						ErrorNoDef.SQL_GENERAL,
						"execute preparedstatement failed:"
								+ stmtContext.getErrorMsg());
			}

			// �����α�
			boolean openCursor = false;
			switch (stmtExecuteCmd.getFlags()) {
			case CURSOR_TYPE_NO_CURSOR:
				break;
			case CURSOR_TYPE_READ_ONLY:
				pstmt.setFetchSize(GlobalContext.getInstance().getConfig()
						.getStreamFetchSize());
				openCursor = true;
				break;
			case CURSOR_TYPE_FOR_UPDATE:
				return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
						ErrorNoDef.SQL_GENERAL,
						"unsupport cousor type: CURSOR_TYPE_FOR_UPDATE");
			case CURSOR_TYPE_SCROLLABLE:
				return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
						ErrorNoDef.SQL_GENERAL,
						"unsupport cousor type: CURSOR_TYPE_SCROLLABLE");
			default:
				return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
						ErrorNoDef.SQL_GENERAL, "unrecognized cousor type: "
								+ stmtExecuteCmd.getFlags());

			}

			// ���ò���
			final int paramCount = stmtContext.getParameterCount();
			for (int i = 0; i < paramCount; i++) {
				StmtLongData longData = stmtContext.getLongDataParams()[i];
				if (longData != null) {
					/*
					 * Connector/J 5.0.8αԤ�����setBlob()��setBinaryStream()�ڴ���������ļ�ʱ��ʱ����?
					 * ����·�����ƴsql������MySQL�ڵ��׳��﷨���󣬻�Ī���������ļ����ȡ�
					 * �ڲ����У�setBytes()�������С�
					 */
					/*Blob blob = new Blob(longData.convert2Array());
					pstmt.setBlob(i + 1, blob);*/
					pstmt.setBytes(i + 1, longData.convert2Array());
					continue;
				}

				Object value = null;
				if (!stmtExecuteCmd.getNullMap()[i]) {
					//�Բ���ֵ���н���
					try {
						value = StmtPacketUtil
								.readParameterValue(stmtExecuteCmd
										.getParametersValueBuffer(),
										stmtContext.getParamTypes().get(i)
												.shortValue());
					} catch (DecodeException e) {
						throw new SQLExceptionWithCause(e.getMessage(), e);
					} catch (UnsupportedEncodingException e2) {
						throw new SQLExceptionWithCause(e2.getMessage(), e2);
					}
				}
				//����ֵΪnullҲ֧��
				pstmt.setObject(i + 1, value);
			}
			String sql = pstmt.getOriginalSQL();
			SQLExecutor executor = sqlExecutorFactory.newExecutor(
					sessionContext, pstmt);
			MySQLCmd ret = SQLDispatch.dispatch(sessionContext, sql, executor);

			// �������Ա������α��ʹ��
			if (openCursor && ret instanceof ResultSetCmd) {
				((ResultSetCmd) ret).setFetchSize(0);
				stmtContext.setResultset((DBResultSet) ((ResultSetCmd) ret)
						.getRs());
			}
			return ret;
		} catch (SQLException e) {
			return new ErrorCmd(e);
		} finally {
			// �����������Ϣ
			stmtContext.setErrorMsg(null);
			stmtContext.resetLongData();
			try {
				pstmt.setFetchSize(oldFetchSize);
			} catch (SQLException e) {
			}
		}

	}

	public void setSqlExecutorFactory(SQLExecutorFactory sqlExecutorFactory) {
		this.sqlExecutorFactory = sqlExecutorFactory;
	}

}
