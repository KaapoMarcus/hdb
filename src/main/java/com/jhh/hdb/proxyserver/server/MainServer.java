package com.jhh.hdb.proxyserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.netease.backend.db.common.definition.Definition;
import com.jhh.hdb.proxyserver.codec.CodecFactory;
import com.jhh.hdb.proxyserver.config.Config;
import com.jhh.hdb.proxyserver.handlers.CmdHandlerFactory;

public class MainServer {
	private static Logger logger = Logger
			.getLogger(Definition.LOGGER_QUERY_SERVER);

	public static void main(String[] args) {
		MainServer server = new MainServer();

		try {
			server.initConfig();
		} catch (Exception e) {
			logger.error("error", e);
			System.exit(1);
		}
		logger.info("服务器配置信息读取完成");



		Config config = GlobalContext.getInstance().getConfig();
		IoAcceptor acceptor = new NioSocketAcceptor();

		acceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new CodecFactory()));
		
		// 添加线程池
		ThreadFactory factory = new ThreadFactory() {
			final AtomicInteger threadNumber = new AtomicInteger(1);

			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "My-Thread-"
						+ threadNumber.getAndIncrement());
				return t;
			}
		};
		acceptor.getFilterChain().addLast(
				"ThreadPool",
				new ExecutorFilter(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
						factory));
/*		// 打印所有往来数据包
		if (logger.isDebugEnabled())
			acceptor.getFilterChain().addLast("logger", server.getLogFilter());*/

		acceptor.setHandler(CmdHandlerFactory.getCmdHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		((NioSocketAcceptor) acceptor).getSessionConfig().setTcpNoDelay(true);
		//		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		try {
			acceptor.bind(new InetSocketAddress(config.getIp(), config
					.getPort()));
			logger.info("服务器启动完成，监听地址: " + config.getIp() + ":"
					+ config.getPort());
		} catch (IOException e) {
			logger.error("error", e);
		}
	}

	private void initConfig() throws Exception {
		String configFilePath = System.getProperty(Config.CONFIG_KEY,
				Config.DFAULT_CONF_FILE_PATH);
		Config config = new Config();
		config.loadFromFile(configFilePath);
		GlobalContext.getInstance().setConfig(config);
	}

	private IoFilter getLogFilter() {
		LoggingFilter filter = new LoggingFilter(Definition.LOGGER_QUERY_SERVER);
		filter.setExceptionCaughtLogLevel(LogLevel.NONE);//异常已经在qsCmdHandler中被打印
		filter.setMessageReceivedLogLevel(LogLevel.DEBUG);
		filter.setMessageSentLogLevel(LogLevel.DEBUG);
		filter.setSessionClosedLogLevel(LogLevel.DEBUG);
		filter.setSessionCreatedLogLevel(LogLevel.NONE);
		filter.setSessionIdleLogLevel(LogLevel.NONE);
		filter.setSessionOpenedLogLevel(LogLevel.DEBUG);

		return filter;
	}
}
