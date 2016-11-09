package com.jhh.hdb.proxyserver.handlers;

import org.apache.mina.core.service.IoHandler;

import com.jhh.hdb.proxyserver.handlers.mysql.AuthenticationHandler;
import com.jhh.hdb.proxyserver.handlers.mysql.HandshakeHandler;
import com.jhh.hdb.proxyserver.handlers.mysql.InitDBHandler;
import com.jhh.hdb.proxyserver.handlers.mysql.QueryHandler;
import com.jhh.hdb.proxyserver.handlers.mysql.StmtCloseHandler;
import com.jhh.hdb.proxyserver.handlers.mysql.StmtExecuteHandler;
import com.jhh.hdb.proxyserver.handlers.mysql.StmtFetchHandler;

public class CmdHandlerFactory {

	public static IoHandler getCmdHandler() {
		CmdHandler handler = new CmdHandler();
		handler.setHandshakeHandler(new HandshakeHandler());
		handler.setAuthenticationHandler(new AuthenticationHandler());
		handler.setInitDBHandler(new InitDBHandler());
		handler.setQueryHandler(new QueryHandler());
//		handler.setPingHandler(new PingHandler());
		handler.setStmtCloseHandler(new StmtCloseHandler());
		handler.setStmtExecuteHandler(new StmtExecuteHandler());
		handler.setStmtFetchHandler(new StmtFetchHandler());
//		handler.setStmtPrepareHandler(new StmtPrepareHandler());
//		handler.setStmtResetHandler(new StmtResetHandler());
//		handler.setStmtLongdataHandler(new StmtSendLongDataHandler());
		return handler;
	}
}
