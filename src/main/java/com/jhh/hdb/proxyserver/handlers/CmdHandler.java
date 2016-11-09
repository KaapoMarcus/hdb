package com.jhh.hdb.proxyserver.handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.netease.backend.db.common.definition.Definition;
import com.jhh.hdb.proxyserver.codec.UnsupportedCommandTypeException;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmdType;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.ResultSetCmd;
import com.jhh.hdb.proxyserver.handlers.mysql.MySQLCmdHandler;
import com.jhh.hdb.proxyserver.session.SessionContext;

public class CmdHandler implements IoHandler {

	private static Logger logger = Logger
			.getLogger(Definition.LOGGER_QUERY_SERVER);

	private MySQLCmdHandler handshakeHandler;
	private MySQLCmdHandler authenticationHandler;
	private MySQLCmdHandler initDBHandler;
	private MySQLCmdHandler queryHandler;
	private MySQLCmdHandler pingHandler;
	private MySQLCmdHandler stmtCloseHandler;
	private MySQLCmdHandler stmtExecuteHandler;
	private MySQLCmdHandler stmtFetchHandler;
	private MySQLCmdHandler stmtPrepareHandler;
	private MySQLCmdHandler stmtResetHandler;
	private MySQLCmdHandler stmtLongdataHandler;

	public CmdHandler() {
	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		if (cause instanceof IOException) {
			// IOException一般都是socket关闭
			if (logger.isDebugEnabled()) {
				logger.debug("IO通信:" + cause.getMessage());
			}
			clearSession(session);
			return;
		} else if (cause.getCause() != null
				&& (cause.getCause() instanceof UnsupportedCommandTypeException)) {
			/**
			 * UnsupportedCommandTypeException表示QsRequestDecoder中抛出的只需要打印DEBUG日志的异常
			 */
			if (logger.isDebugEnabled())
				logger.debug(cause.getMessage());
		} else {
			logger.error("服务器运行过程异常", cause);
		}

		session.write(new ErrorCmd(cause));
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		MySQLCmd cmd = (MySQLCmd) message;
		SessionContext sessionContext = SessionContext
				.getSessionContext(session);
		MySQLCmd returnCmd = null;
		switch (cmd.getType()) {
		case authentication: {
			returnCmd = authenticationHandler
					.handleMessage(sessionContext, cmd);
			break;
		}
		case initDb: {
			returnCmd = initDBHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case quit: {
			session.close(false);
			break;
		}
		case query: {
			returnCmd = queryHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case ping: {
			returnCmd = pingHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case stmtClose: {
			returnCmd = stmtCloseHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case stmtExecute: {
			returnCmd = stmtExecuteHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case stmtFetch: {
			returnCmd = stmtFetchHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case stmtPrepare: {
			returnCmd = stmtPrepareHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case stmtReset: {
			returnCmd = stmtResetHandler.handleMessage(sessionContext, cmd);
			break;
		}
		case stmtLongdata: {
			returnCmd = stmtLongdataHandler.handleMessage(sessionContext, cmd);
			break;
		}
		default:
			throw new Exception(
					"handle message failed, unsupport command type:"
							+ cmd.getType());
		}

		if (returnCmd != null) {
			if (returnCmd.getType() == MySQLCmdType.error) {
				logger.info("向客户端发送错误包：" + returnCmd);
			}
			session.write(returnCmd);
		}
	}

	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
				System.out.println("IDLE " + session.getIdleCount(status));
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		MySQLCmd cmd = (MySQLCmd) message;
		if (cmd.getType() == MySQLCmdType.resultset) {
			ResultSetCmd rscmd = (ResultSetCmd) cmd;
			if (!rscmd.isClosed()
					&& !(rscmd.isPrepared() && rscmd.getFetchSize() == 0)) {
				session.write(cmd);
			}
		}

	}

	public void sessionClosed(IoSession session) throws Exception {
		clearSession(session);
	}

	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	public void sessionOpened(IoSession session) throws Exception {
		SessionContext context = new SessionContext();
		MySQLCmd outcmd = handshakeHandler.handleMessage(context, null);
		SessionContext.setSessionContext(session, context);
		context.setClientIp(((InetSocketAddress) session.getRemoteAddress())
				.getAddress().getHostAddress());
		session.write(outcmd);

	}

	public MySQLCmdHandler getHandshakeHandler() {
		return handshakeHandler;
	}

	public void setHandshakeHandler(MySQLCmdHandler handshakeHandler) {
		this.handshakeHandler = handshakeHandler;
	}

	public MySQLCmdHandler getAuthenticationHandler() {
		return authenticationHandler;
	}

	public void setAuthenticationHandler(MySQLCmdHandler authenticationHandler) {
		this.authenticationHandler = authenticationHandler;
	}

	public MySQLCmdHandler getInitDBHandler() {
		return initDBHandler;
	}

	public void setInitDBHandler(MySQLCmdHandler initDBHandler) {
		this.initDBHandler = initDBHandler;
	}

	public MySQLCmdHandler getQueryHandler() {
		return queryHandler;
	}

	public void setQueryHandler(MySQLCmdHandler queryHandler) {
		this.queryHandler = queryHandler;
	}

	public MySQLCmdHandler getPingHandler() {
		return pingHandler;
	}

	public void setPingHandler(MySQLCmdHandler pingHandler) {
		this.pingHandler = pingHandler;
	}

	public MySQLCmdHandler getStmtCloseHandler() {
		return stmtCloseHandler;
	}

	public void setStmtCloseHandler(MySQLCmdHandler stmtCloseHandler) {
		this.stmtCloseHandler = stmtCloseHandler;
	}

	public MySQLCmdHandler getStmtExecuteHandler() {
		return stmtExecuteHandler;
	}

	public void setStmtExecuteHandler(MySQLCmdHandler stmtExecuteHandler) {
		this.stmtExecuteHandler = stmtExecuteHandler;
	}

	public MySQLCmdHandler getStmtFetchHandler() {
		return stmtFetchHandler;
	}

	public void setStmtFetchHandler(MySQLCmdHandler stmtFetchHandler) {
		this.stmtFetchHandler = stmtFetchHandler;
	}

	public MySQLCmdHandler getStmtPrepareHandler() {
		return stmtPrepareHandler;
	}

	public void setStmtPrepareHandler(MySQLCmdHandler stmtPrepareHandler) {
		this.stmtPrepareHandler = stmtPrepareHandler;
	}

	public MySQLCmdHandler getStmtResetHandler() {
		return stmtResetHandler;
	}

	public void setStmtResetHandler(MySQLCmdHandler stmtResetHandler) {
		this.stmtResetHandler = stmtResetHandler;
	}

	public MySQLCmdHandler getStmtLongdataHandler() {
		return stmtLongdataHandler;
	}

	public void setStmtLongdataHandler(MySQLCmdHandler stmtLongdataHandler) {
		this.stmtLongdataHandler = stmtLongdataHandler;
	}

	private void clearSession(IoSession session) {
		SessionContext sessionContext = SessionContext
				.getSessionContext(session);
		if (sessionContext != null) {
			try {
				sessionContext.clear();
			} catch (SQLException e) {
				logger.error("服务器回收session资源异常", e);
			}
		}

	}
}