package com.jhh.hdb.proxyserver.define;

public class ErrorNoDef {
	/*******************************************************************
	 * error_no define
	 */

	// 通用错误
	public static final short ERROR_GENERAL = 1;

	// 用户认证错误
	public static final short ERROR_AUTH = 2;

	/*******************************************************************
	 * sql_state define
	 */

	// 通用错误
	public static final String SQL_GENERAL = "10000";

	// 用户认证错误
	public static final String SQL_AUTH = "20000";
}
