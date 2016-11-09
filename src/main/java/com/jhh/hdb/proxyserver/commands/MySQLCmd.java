package com.jhh.hdb.proxyserver.commands;

/**
 * 所有mysql命令的父类。
 * 查询服务器中，由codec包负责解码成MySQLCmd。统一交由handler包执行后，再由codec包进行编码发送给客户端
 * 
 *
 */
public abstract class MySQLCmd {
	// 命令包长度
	private int length;

	// 命令序列号
	private byte number;
	
	//缓存标识
	private String key = null;
	
	public MySQLCmd() {
		/*
		 * 由qs发送到客户端的一般都是1，但有特例，需要特殊处理：
		 * 1、三步握手中，s->c为0，然后c->s为1，最后s->为2(不管是OK还是Error)
		 * 2、返回ResultSet时，header packet是1，后面的依次递增
		 * */
		this.number = (byte)1;
	}

	public abstract MySQLCmdType getType();

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte getNumber() {
		return number;
	}

	public void setNumber(byte number) {
		this.number = number;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
}
