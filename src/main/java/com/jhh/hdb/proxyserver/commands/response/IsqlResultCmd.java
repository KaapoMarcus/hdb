package com.jhh.hdb.proxyserver.commands.response;



import com.netease.cli.StringTable;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmdType;

/**
 * ����ִ��Isql�����Ľ��
 * 
 *
 */
public class IsqlResultCmd extends MySQLCmd {
	
	private StringTable stringTable;
	
	private short serverStatus;
	
	public IsqlResultCmd(StringTable st, short serverStatus) {
		this.stringTable = st;
		this.serverStatus = serverStatus;
	}
	
	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.isqlresult;
	}

	public StringTable getStringTable() {
		return stringTable;
	}

	public short getServerStatus() {
		return serverStatus;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[IsqlResultCmd] StringTable name:");
		sb.append(stringTable.getName());
		sb.append(", numColumns:");
		sb.append(stringTable.getNumColumns());
		sb.append(", numRows:");
		sb.append(stringTable.getData().size());
		sb.append(", serverStatus:");
		sb.append(this.serverStatus);
		return sb.toString();
	}
}
