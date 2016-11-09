package com.jhh.hdb.proxyserver.commands;

/**
 * mysql通信命令类别
 * 
 *
 */
public enum MySQLCmdType {
	// client to server
	authentication, 
	changeUser, 
	initDb, 
	ping, 
	query, 
	quit, 
	stmtClose, 
	stmtExecute, 
	stmtFetch, 
	stmtPrepare, 
	stmtReset, 
	stmtLongdata,

	// server to client
	handshake, 
	error, 
	ok, 
	resultset,
	prepareok,
	isqlresult
}
