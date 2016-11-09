package com.netease.backend.db.common.schema.dbengine;

import java.io.Serializable;

public class MysqlInfo implements Serializable{

	private static final long serialVersionUID = 4235566173233357847L;
	
	
	private long byteRecPS;		
	private long byteSentPS;		
	private long connectionsPS;	
	private long questionsPS;		
	
	private long keyBlocksUsed;	
	private long keyReads;		
	private long keyReadRequests; 
	private long keyWrites;		
	private long keyWriteRequests;
	private long maxUsedConnections;
	private long openFiles;		
	private long slowQueries;		
	private long tableLocksImmediate;
	private long threadsConnected;	
	private long threadsRunning;	
	private long uptime; 
	
	private int PID;				
	private float cpuUsedRate;	
	private float memUsedRate;	
	private int VSZ;				
	private int RSS;				
	private String CMD;			

	public void setByteRecPS(long byteRecPS) {
		this.byteRecPS = byteRecPS;
	}
	public long getByteRecPS() {
		return byteRecPS;
	}
	public void setByteSentPS(long byteSentPS) {
		this.byteSentPS = byteSentPS;
	}
	public long getByteSentPS() {
		return byteSentPS;
	}
	public void setConnectionsPS(long connectionsPS) {
		this.connectionsPS = connectionsPS;
	}
	public long getConnectionsPS() {
		return connectionsPS;
	}
	public void setQuestionsPS(long questionsPS) {
		this.questionsPS = questionsPS;
	}
	public long getQuestionsPS() {
		return questionsPS;
	}
	public void setKeyBlocksUsed(long keyBlocksUsed) {
		this.keyBlocksUsed = keyBlocksUsed;
	}
	public long getKeyBlocksUsed() {
		return keyBlocksUsed;
	}
	public void setKeyReads(long keyReads) {
		this.keyReads = keyReads;
	}
	public long getKeyReads() {
		return keyReads;
	}
	public void setKeyReadRequests(long keyReadRequests) {
		this.keyReadRequests = keyReadRequests;
	}
	public long getKeyReadRequests() {
		return keyReadRequests;
	}
	public void setKeyWrites(long keyWrites) {
		this.keyWrites = keyWrites;
	}
	public long getKeyWrites() {
		return keyWrites;
	}
	public void setKeyWriteRequests(long keyWriteRequests) {
		this.keyWriteRequests = keyWriteRequests;
	}
	public long getKeyWriteRequests() {
		return keyWriteRequests;
	}
	public void setMaxUsedConnections(long maxUsedConnections) {
		this.maxUsedConnections = maxUsedConnections;
	}
	public long getMaxUsedConnections() {
		return maxUsedConnections;
	}
	public void setOpenFiles(long openFiles) {
		this.openFiles = openFiles;
	}
	public long getOpenFiles() {
		return openFiles;
	}
	public void setSlowQueries(long slowQueries) {
		this.slowQueries = slowQueries;
	}
	public long getSlowQueries() {
		return slowQueries;
	}
	public void setTableLocksImmediate(long tableLocksImmediate) {
		this.tableLocksImmediate = tableLocksImmediate;
	}
	public long getTableLocksImmediate() {
		return tableLocksImmediate;
	}
	public void setThreadsRunning(long threadsRunning) {
		this.threadsRunning = threadsRunning;
	}
	public long getThreadsRunning() {
		return threadsRunning;
	}
	public void setUptime(long uptime) {
		this.uptime = uptime;
	}
	public long getUptime() {
		return uptime;
	}
	public void setPID(int pID) {
		PID = pID;
	}
	public int getPID() {
		return PID;
	}
	public void setCpuUsedRate(float cpuUsedRate) {
		this.cpuUsedRate = cpuUsedRate;
	}
	public float getCpuUsedRate() {
		return cpuUsedRate;
	}
	public void setMemUsedRate(float memUsedRate) {
		this.memUsedRate = memUsedRate;
	}
	public float getMemUsedRate() {
		return memUsedRate;
	}
	public void setVSZ(int vSZ) {
		VSZ = vSZ;
	}
	public int getVSZ() {
		return VSZ;
	}
	public void setRSS(int rSS) {
		RSS = rSS;
	}
	public int getRSS() {
		return RSS;
	}
	public void setCMD(String cMD) {
		CMD = cMD;
	}
	public String getCMD() {
		return CMD;
	}
	public long getThreadsConnected() {
		return threadsConnected;
	}
	public void setThreadsConnected(long threadsConnected) {
		this.threadsConnected = threadsConnected;
	}
}
