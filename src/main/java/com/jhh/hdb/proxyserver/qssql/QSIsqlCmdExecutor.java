package com.jhh.hdb.proxyserver.qssql;

import com.netease.cli.StringTable;
import com.netease.exec.Executor;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * 查询服务器处理Isql命令的执行器
 * 
 *
 */
public class QSIsqlCmdExecutor {

	private Executor executor = null;
	private QSIsqlOutput output = null;
	private QSPlugin qsPlugin = null;
	
	public QSIsqlCmdExecutor(SessionContext sessionContext) {
		output = new QSIsqlOutput();
		qsPlugin = new QSPlugin(sessionContext, executor);
		executor = new Executor(null, output);
		executor.installPlugin(qsPlugin);
	}
	
	public StringTable execute(String sql) throws Exception {
		//执行命令并获取返回
		executor.execute(sql);
		Object result = output.getResultData();
		
		//结果必须清理，避免影响下一次的执行
		output.clearResult();
		
		if (null == result)
			throw new NullPointerException("return Object from QSPlugiin is null.");
		if (!(result instanceof StringTable))
			throw new IllegalArgumentException("return Object from QSPlugin must be StringTable.");
		
		StringTable stringTable = (StringTable)result;
		
		/**
		 * 'show commands'命令由Executor的core插件截获，会返回部分查询服务器并不支持的命令
		 * 当前的做法是通过返回StringTable的名称(Commands)识别是否该命令的返回结果，并进行替换。
		 * 'show commands for'命令也会被core插件截获，但返回结果与要求相符，所以不用处理。
		 */
		if ("Commands".equalsIgnoreCase(stringTable.getName()))
			stringTable = qsPlugin.showCommands();
		
		return stringTable;
	}
}
