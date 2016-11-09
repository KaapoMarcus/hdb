package com.netease.backend.db.common.management.dbi;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import com.netease.backend.db.common.schema.User;
import com.netease.cli.StringTable;


public interface IDBIUserManager {

	
	Map<String, User> getUsers();

	
	boolean existUser(
		String userName);

	
	User getUser(
		String userName);

	
	User addUser(
		User user, String lwIp, String localIp);

	
	User removeUser(
		String username);

	
	void changeUserPass(
		String username, String newPass);

	
	void changeUserQuota(
		String username, long quota, long slaveQuota);

	
	void changeUserHost(
		String username, Set<String> clientIpSet);

	
	User verifyUser(
		String userName, String password, String localIp)
		throws SQLException;

	
	User verifyUser(
		String userName, String password, String localIp, String qsIp, String seed)
		throws SQLException;

	
	StringTable showLoginRecords();

	
	void reset();

}