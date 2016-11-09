package com.jhh.hdb.proxyserver.define;

import com.jhh.hdb.proxyserver.config.Config;


public class ServerCapabilities {
	//TODO: 有些值的意思还需要搞清楚
	public static final int CLIENT_LONG_PASSWORD = 1; /* new more secure passwords */
	public static final int CLIENT_FOUND_ROWS = 2; /* Found instead of affected rows */
	public static final int CLIENT_LONG_FLAG = 4; /* Get all column flags */
	public static final int CLIENT_CONNECT_WITH_DB = 8; /* One can specify db on connect */
	public static final int CLIENT_NO_SCHEMA = 16; /* Don't allow database.table.column */
	public static final int CLIENT_COMPRESS = 32; /* Can use compression protocol */
	public static final int CLIENT_ODBC = 64; /* Odbc client */
	public static final int CLIENT_LOCAL_FILES = 128; /* Can use LOAD DATA LOCAL */
	public static final int CLIENT_IGNORE_SPACE = 256; /* Ignore spaces before '(' */
	public static final int CLIENT_PROTOCOL_41 = 512; /* New 4.1 protocol */
	public static final int CLIENT_INTERACTIVE = 1024; /* This is an interactive client */
	public static final int CLIENT_SSL = 2048; /* Switch to SSL after handshake */
	public static final int CLIENT_IGNORE_SIGPIPE = 4096; /* IGNORE sigpipes */
	public static final int CLIENT_TRANSACTIONS = 8192; /* Client knows about transactions */
	public static final int CLIENT_RESERVED = 16384; /* Old flag for 4.1 protocol  */
	public static final int CLIENT_SECURE_CONNECTION = 32768; /* New 4.1 authentication */
	public static final int CLIENT_MULTI_STATEMENTS = 65536; /* Enable/disable multi-stmt support */
	public static final int CLIENT_MULTI_RESULTS = 131072; /* Enable/disable multi-results */

	public static int getDefaultCapabilities(Config config) {
		//TODO: 这个值来自于amoeba，待验证
		return 41485; //1010001000001101
	}

	public static boolean isLongPassword(int flag) {
		return (flag & CLIENT_LONG_PASSWORD) == 1;
	}

	public static boolean isFoundRows(int flag) {
		return ((flag & CLIENT_FOUND_ROWS) >>> 1) == 1;
	}

	public static boolean isLongFlag(int flag) {
		return ((flag & CLIENT_LONG_FLAG) >>> 2) == 1;
	}

	public static boolean isConnectWithDB(int flag) {
		return ((flag & CLIENT_CONNECT_WITH_DB) >>> 3) == 1;
	}

	public static boolean isNoSchema(int flag) {
		return ((flag & CLIENT_NO_SCHEMA) >>> 4) == 1;
	}

	public static boolean isCompress(int flag) {
		return ((flag & CLIENT_COMPRESS) >>> 5) == 1;
	}

	public static boolean isODBC(int flag) {
		return ((flag & CLIENT_ODBC) >>> 6) == 1;
	}

	public static boolean isLocalFiles(int flag) {
		return ((flag & CLIENT_LOCAL_FILES) >>> 7) == 1;
	}

	public static boolean isIgnoreSpace(int flag) {
		return ((flag & CLIENT_IGNORE_SPACE) >>> 8) == 1;
	}

	public static boolean isProtocol41(int flag) {
		return ((flag & CLIENT_PROTOCOL_41) >>> 9) == 1;
	}

	public static boolean isInteractive(int flag) {
		return ((flag & CLIENT_INTERACTIVE) >>> 10) == 1;
	}

	public static boolean isSSL(int flag) {
		return ((flag & CLIENT_SSL) >>> 11) == 1;
	}

	public static boolean isIgnoreSigpipe(int flag) {
		return ((flag & CLIENT_IGNORE_SIGPIPE) >>> 12) == 1;
	}

	public static boolean isTransaction(int flag) {
		return ((flag & CLIENT_TRANSACTIONS) >>> 13) == 1;
	}

	public static boolean isReserved(int flag) {
		return ((flag & CLIENT_RESERVED) >>> 14) == 1;
	}

	public static boolean isSecureConnection(int flag) {
		return ((flag & CLIENT_SECURE_CONNECTION) >>> 15) == 1;
	}

	public static boolean isMultiStatements(int flag) {
		return ((flag & CLIENT_MULTI_STATEMENTS) >>> 16) == 1;
	}

	public static boolean isMultiResults(int flag) {
		return ((flag & CLIENT_MULTI_RESULTS) >>> 17) == 1;
	}

}
