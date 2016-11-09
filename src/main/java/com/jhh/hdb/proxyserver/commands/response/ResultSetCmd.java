package com.jhh.hdb.proxyserver.commands.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.jhh.hdb.proxyserver.codec.packets.mysql.response.ResultSetPacket;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmdType;

public class ResultSetCmd extends MySQLCmd {
	private ResultSet rs;

	private Statement stmt;

	private Connection conn = null;

	// �Ƿ�Ԥ�������Ľ��
	private boolean isPrepared;

	// ����ɱ���Ļ������
	private List<byte[]> cacheResult;

	private boolean closed = false;

	private byte packetNumber = ResultSetPacket.PACKET_NUMBER_FIRST;
	
	private boolean isStreamFirst = true;

	/**
	 * =-1 : ȫ������
	 * = 0 : �����ͣ����ڿ����α��Ԥ�������ĵ�һ�η�����ݰ�
	 * > 0 : ����ָ����������ݰ�
	 */
	private int fetchSize = -1;

	private boolean isStmtFetchBegin = true;

	private short serverStatus;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.resultset;
	}

	public ResultSet getRs() {
		return rs;
	}

	public List<byte[]> getCache() {
		return cacheResult;
	}

	/**
	 * ���캯��1
	 * 
	 * @param rs
	 * @param stmt
	 * @param isPrepared
	 */
	public ResultSetCmd(ResultSet rs, Statement stmt, boolean isPrepared,
			short serverStatus) {
		super();
		this.rs = rs;
		this.stmt = stmt;
		this.isPrepared = isPrepared;
		this.serverStatus = serverStatus;
	}

	/**
	 * ���캯��3.
	 * 
	 * @param cache
	 */
	public ResultSetCmd(List<byte[]> cache) {
		this.cacheResult = cache;
	}

	/**
	 * �ر��ڲ������ResultSet���������Դ �˷���������ResultSet�����͸�ͻ���֮�����ⲿ��ʾ����
	 * 
	 * @throws SQLException
	 */
	public void closeResultSet() throws SQLException {
		closed = true;

		if (!isPrepared) {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public boolean isPrepared() {
		return this.isPrepared;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ResultSetCmd] Connection:").append(nullMsg(this.conn));
		sb.append(", Statement:").append(nullMsg(this.stmt));
		sb.append(", ResultSet:").append(nullMsg(this.rs));
		sb.append(", IsPrepared:").append(isPrepared());
		if (isPrepared()) {
			sb.append(", isStmtFetchBegin:").append(isStmtFetchBegin());
			sb.append(", FetchSize:").append(getFetchSize());
		}
		sb.append(", CacheResult:").append(nullMsg(this.cacheResult));
		return sb.toString();
	}

	private static String nullMsg(Object obj) {
		return (obj == null ? "null" : "not null");
	}

	public void closeConWhenExecuted(Connection conn) {
		this.conn = conn;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public boolean isStmtFetchBegin() {
		return isStmtFetchBegin;
	}

	public void setStmtFetchBegin(boolean isFirst) {
		this.isStmtFetchBegin = isFirst;
	}

	public short getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(short serverStatus) {
		this.serverStatus = serverStatus;
	}

	public boolean isClosed() {
		return closed;
	}

	public byte getPacketNumber() {
		return packetNumber;
	}

	public void setPacketNumber(byte packetNumber) {
		this.packetNumber = packetNumber;
	}

	public boolean isStreamFirst() {
		return isStreamFirst;
	}

	public void setStreamFirst(boolean isStreamFirst) {
		this.isStreamFirst = isStreamFirst;
	}


	public int rowcount;
}
