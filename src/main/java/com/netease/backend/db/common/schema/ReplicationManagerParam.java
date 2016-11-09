package com.netease.backend.db.common.schema;

import com.netease.backend.db.common.management.Cluster;

public class ReplicationManagerParam {

	
	public static class StartSlaveParam {
		private User user;

		private Database db;

		private Cluster cluster;

		private boolean isModifyConfigFile = true;

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public Database getDb() {
			return db;
		}

		public void setDb(Database db) {
			this.db = db;
		}

		public Cluster getCluster() {
			return cluster;
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}

		public StartSlaveParam(User user, Database db, Cluster cluster, boolean isModifyConfigFile) {
			super();
			this.user = user;
			this.db = db;
			this.cluster = cluster;
			this.isModifyConfigFile = isModifyConfigFile;
		}

		public boolean isModifyConfigFile() {
			return isModifyConfigFile;
		}

		public void setModifyConfigFile(boolean isModifyConfigFile) {
			this.isModifyConfigFile = isModifyConfigFile;
		}
	}

	
	public static class StopSlaveParam {
		private User user;

		private Database db;

		private Cluster cluster;

		private boolean isModifyConfigFile = true;

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public Database getDb() {
			return db;
		}

		public void setDb(Database db) {
			this.db = db;
		}

		public Cluster getCluster() {
			return cluster;
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}

		public StopSlaveParam(User user, Database db, Cluster cluster, boolean isModifyConfigFile) {
			super();
			this.user = user;
			this.db = db;
			this.cluster = cluster;
			this.isModifyConfigFile = isModifyConfigFile;
		}

		public boolean isModifyConfigFile() {
			return isModifyConfigFile;
		}

		public void setModifyConfigFile(boolean isModifyConfigFile) {
			this.isModifyConfigFile = isModifyConfigFile;
		}
	}

	
	public static class RefreshSlaveParam {
		private User adminUser;

		private Database db;

		private Database masterDb;

		private boolean buildSlave;

		private Cluster cluster;

		private String dataDir;
		
		private boolean persistdb;
		
		private boolean startSlave;
		
		public User getAdminUser() {
			return adminUser;
		}

		public void setAdminUser(User adminUser) {
			this.adminUser = adminUser;
		}

		public Database getDb() {
			return db;
		}

		public void setDb(Database db) {
			this.db = db;
		}

		public Database getMasterDb() {
			return masterDb;
		}

		public void setMasterDb(Database masterDb) {
			this.masterDb = masterDb;
		}

		public boolean isBuildSlave() {
			return buildSlave;
		}

		public void setBuildSlave(boolean buildSlave) {
			this.buildSlave = buildSlave;
		}

		public Cluster getCluster() {
			return cluster;
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}

		public RefreshSlaveParam(User adminUser, Database db, Database masterDb, boolean buildSlave,
				Cluster cluster, String dataDir, boolean persistdb, boolean startSlave) {
			super();
			this.adminUser = adminUser;
			this.db = db;
			this.masterDb = masterDb;
			this.buildSlave = buildSlave;
			this.cluster = cluster;
			this.dataDir = dataDir;
			this.persistdb = persistdb;
			this.startSlave = startSlave;
		}

		public String getDataDir() {
			return dataDir;
		}

		public void setDataDir(String dataDir) {
			this.dataDir = dataDir;
		}

		public boolean isPersistdb() {
			return persistdb;
		}

		public void setPersistdb(boolean persistdb) {
			this.persistdb = persistdb;
		}

		public boolean isStartSlave() {
			return startSlave;
		}

		public void setStartSlave(boolean startSlave) {
			this.startSlave = startSlave;
		}

		
	}

}
