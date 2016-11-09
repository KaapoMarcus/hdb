package com.jhh.hdb.proxyserver.commands;

/**
 * 处理客户端退出命令
 * 
 *
 */
public class QuitCmd extends CommandCmd {

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.quit;
	}
	
	@Override
	public String toString() {
		return "[QuitCmd] Quit";
	}

}
