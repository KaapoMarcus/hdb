package com.jhh.hdb.proxyserver.commands.response;

import java.sql.SQLException;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.MySQLCmdType;
import com.jhh.hdb.proxyserver.define.ErrorNoDef;

/**
 * ִ��ʧ�ܷ��ذ�
 * field_count:       Always 0xff (255 decimal).
 
 errno:             The possible values are listed in the manual, and in
                    the MySQL source code file /include/mysqld_error.h.
 
 sqlstate marker:   This is always '#'. It is necessary for distinguishing
                    version-4.1 messages.
 
 sqlstate:          The server translates errno values to sqlstate values
                    with a function named mysql_errno_to_sqlstate(). The
                    possible values are listed in the manual, and in the
                    MySQL source code file /include/sql_state.h.
 
 message:           The error message is a string which ends at the end of
                    the packet, that is, its length can be determined from
                    the packet header. The MySQL client (in the my_net_read()
                    function) always adds '\0' to a packet, so the message
                    may appear to be a Null-Terminated String.
                    Expect the message to be between 0 and 512 bytes long.

 * 
 *
 */
public class ErrorCmd extends MySQLCmd {

	private byte fieldCount;
	private short errno;
	private byte marker;
	private String sqlstate;
	private String message;

	public ErrorCmd(byte fieldCount, short errno, byte marker, String sqlstate,
			String message) {
		super();
		this.fieldCount = fieldCount;
		this.errno = errno;
		this.marker = marker;
		this.sqlstate = sqlstate;
		this.message = message;
	}

	/**
	 * �򻯰湹�캯��
	 *  this.fieldCount = (byte) 0xff;
	 *  this.marker = (byte) 0x23;
	 * @param errno
	 * @param sqlstate
	 * @param message
	 */
	public ErrorCmd(short errno, String sqlstate, String message) {
		super();
		this.fieldCount = (byte) 0xff;
		this.marker = (byte) 0x23;
		this.errno = errno;
		this.sqlstate = sqlstate;
		this.message = message;
	}

	public ErrorCmd(SQLException e) {
		this.fieldCount = (byte) 0xff;
		this.marker = (byte) 0x23;
		this.errno = (short) e.getErrorCode();
		this.sqlstate = e.getSQLState();
		this.message = e.getMessage();
	}

	public ErrorCmd(Throwable cause) {
		this.fieldCount = (byte) 0xff;
		this.marker = (byte) 0x23;
		this.errno = ErrorNoDef.ERROR_GENERAL;
		this.sqlstate = ErrorNoDef.SQL_GENERAL;
		this.message = cause.getMessage();
	}

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.error;
	}

	public byte getFieldCount() {
		return fieldCount;
	}

	public void setFieldCount(byte fieldCount) {
		this.fieldCount = fieldCount;
	}

	public short getErrno() {
		return errno;
	}

	public void setErrno(short errno) {
		this.errno = errno;
	}

	public byte getMarker() {
		return marker;
	}

	public void setMarker(byte marker) {
		this.marker = marker;
	}

	public String getSqlstate() {
		return sqlstate;
	}

	public void setSqlstate(String sqlstate) {
		this.sqlstate = sqlstate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "[ErrorCmd] Error " + errno + " (" + sqlstate + "):" + message;
	}

}
