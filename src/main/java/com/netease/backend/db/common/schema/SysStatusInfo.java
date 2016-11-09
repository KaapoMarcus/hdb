package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.HashMap;

public class SysStatusInfo implements Serializable{

	private static final long serialVersionUID = -2380007996389240402L;
	
	
	private long userTime;	
	private long niceTime;	
	private long sysTime;	
	private long idleTime;	
	private float busyRate;
	
	
	private float userRate;	
	private float systemRate; 
	private float iowaitRate;  
	private float idleRate;    
	
	
	private HashMap<String, Long> memInfoMap;
	private float memUsedRate;
	
	
	private String netDevName; 
	private long recBytes; 
	private long recPackets; 
	private long recErrs; 
	private long recDrop; 
	private long recFifo; 
	private long recFrame; 
	private long recCompressed; 
	private long recMulticast; 
	private long tranBytes; 
	private long tranPackets; 
	private long tranErrs; 
	private long tranDrop; 
	private long tranFifo; 
	private long tranColls; 
	private long tranCarrier; 
	private long tranCompressed; 
	
	
	private String diskPath;        
	private  long diskTotal;   
	private  long diskUsed;    
	private  long diskAvail;   
	private  float diskUsedRate; 
	
	
	private String ioDevName; 
	private float ioKBReadPS;	
	private float ioKBWritePS;  
	
	public void setUserTime(long userTime) {
		this.userTime = userTime;
	}
	public long getUserTime() {
		return userTime;
	}
	public void setNiceTime(long niceTime) {
		this.niceTime = niceTime;
	}
	public long getNiceTime() {
		return niceTime;
	}
	public void setSysTime(long sysTime) {
		this.sysTime = sysTime;
	}
	public long getSysTime() {
		return sysTime;
	}
	public void setIdleTime(long idleTime) {
		this.idleTime = idleTime;
	}
	public long getIdleTime() {
		return idleTime;
	}
	public void setMemInfoMap(HashMap<String, Long> memInfoMap) {
		this.memInfoMap = memInfoMap;
	}
	public HashMap<String, Long> getMemInfoMap() {
		return memInfoMap;
	}
	public void setNetDevName(String netDevName) {
		this.netDevName = netDevName;
	}
	public String getNetDevName() {
		return netDevName;
	}
	public void setRecBytes(long recBytes) {
		this.recBytes = recBytes;
	}
	public long getRecBytes() {
		return recBytes;
	}
	public void setRecPackets(long recPackets) {
		this.recPackets = recPackets;
	}
	public long getRecPackets() {
		return recPackets;
	}
	public void setRecErrs(long recErrs) {
		this.recErrs = recErrs;
	}
	public long getRecErrs() {
		return recErrs;
	}
	public void setRecDrop(long recDrop) {
		this.recDrop = recDrop;
	}
	public long getRecDrop() {
		return recDrop;
	}
	public void setRecFifo(long recFifo) {
		this.recFifo = recFifo;
	}
	public long getRecFifo() {
		return recFifo;
	}
	public void setRecFrame(long recFrame) {
		this.recFrame = recFrame;
	}
	public long getRecFrame() {
		return recFrame;
	}
	public void setRecCompressed(long recCompressed) {
		this.recCompressed = recCompressed;
	}
	public long getRecCompressed() {
		return recCompressed;
	}
	public void setRecMulticast(long recMulticast) {
		this.recMulticast = recMulticast;
	}
	public long getRecMulticast() {
		return recMulticast;
	}
	public void setTranBytes(long tranBytes) {
		this.tranBytes = tranBytes;
	}
	public long getTranBytes() {
		return tranBytes;
	}
	public void setTranPackets(long tranPackets) {
		this.tranPackets = tranPackets;
	}
	public long getTranPackets() {
		return tranPackets;
	}
	public void setTranErrs(long tranErrs) {
		this.tranErrs = tranErrs;
	}
	public long getTranErrs() {
		return tranErrs;
	}
	public void setTranDrop(long tranDrop) {
		this.tranDrop = tranDrop;
	}
	public long getTranDrop() {
		return tranDrop;
	}
	public void setTranFifo(long tranFifo) {
		this.tranFifo = tranFifo;
	}
	public long getTranFifo() {
		return tranFifo;
	}
	public void setTranColls(long tranColls) {
		this.tranColls = tranColls;
	}
	public long getTranColls() {
		return tranColls;
	}
	public void setTranCarrier(long tranCarrier) {
		this.tranCarrier = tranCarrier;
	}
	public long getTranCarrier() {
		return tranCarrier;
	}
	public void setTranCompressed(long tranCompressed) {
		this.tranCompressed = tranCompressed;
	}
	public long getTranCompressed() {
		return tranCompressed;
	}
	public void setDiskPath(String diskPath) {
		this.diskPath = diskPath;
	}
	public String getDiskPath() {
		return diskPath;
	}
	public void setDiskTotal(long diskTotal) {
		this.diskTotal = diskTotal;
	}
	public long getDiskTotal() {
		return diskTotal;
	}
	public void setDiskUsed(long diskUsed) {
		this.diskUsed = diskUsed;
	}
	public long getDiskUsed() {
		return diskUsed;
	}
	public void setDiskAvail(long diskAvail) {
		this.diskAvail = diskAvail;
	}
	public long getDiskAvail() {
		return diskAvail;
	}
	public void setDiskUsedRate(float diskUsedRate) {
		this.diskUsedRate = diskUsedRate;
	}
	public float getDiskUsedRate() {
		return diskUsedRate;
	}
	public void setIoDevName(String ioDevName) {
		this.ioDevName = ioDevName;
	}
	public String getIoDevName() {
		return ioDevName;
	}
	public void setIoKBReadPS(float ioKBReadPS) {
		this.ioKBReadPS = ioKBReadPS;
	}
	public float getIoKBReadPS() {
		return ioKBReadPS;
	}
	public void setIoKBWritePS(float ioKBWritePS) {
		this.ioKBWritePS = ioKBWritePS;
	}
	public float getIoKBWritePS() {
		return ioKBWritePS;
	}
	
	
	public long getMemTotal() {
		return memInfoMap.get("MemTotal");
	}

	
	public long getMemFree() {
		return memInfoMap.get("MemFree");
	}

	
	public long getBuffers() {
		return memInfoMap.get("Buffers");
	}

	
	public long getCached() {
		return memInfoMap.get("Cached");
	}

	
	public long getSwapCached() {
		return memInfoMap.get("SwapCached");
	}

	
	public long getActive() {
		return memInfoMap.get("Active");
	}

	
	public long getInActive() {
		return memInfoMap.get("Inactive");
	}

	
	public long getHighTotal() {
		return memInfoMap.get("HighTotal");
	}

	
	public long getHighFree() {
		return memInfoMap.get("HighFree");
	}

	
	public long getLowTotal() {
		return memInfoMap.get("LowTotal");
	}

	
	public long getLowFree() {
		return memInfoMap.get("LowFree");
	}

	
	public long getSwapTotal() {
		return memInfoMap.get("SwapTotal");
	}

	
	public long getSwapFree() {
		return memInfoMap.get("SwapFree");
	}

	
	public long getDirty() {
		return memInfoMap.get("Dirty");
	}

	
	public long getWriteback() {
		return memInfoMap.get("Writeback");
	}

	
	public long getMapped() {
		return memInfoMap.get("Mapped");
	}

	
	public long getSlab() {
		return memInfoMap.get("Slab");
	}

	
	public long getCommitLimit() {
		if ( memInfoMap.containsKey("CommitLimit") )
			return memInfoMap.get("CommitLimit");
		return 0;
	}

	
	public long getCommittedAS() {
		return memInfoMap.get("Committed_AS");
	}

	
	public long getPageTables() {
		return memInfoMap.get("PageTables");
	}

	
	public long getVmallocTotal() {
		return memInfoMap.get("VmallocTotal");
	}

	
	public long getVmallocUsed() {
		return memInfoMap.get("VmallocUsed");
	}

	
	public long getVmallocChunk() {
			return memInfoMap.get("VmallocChunk");
	}

	
	public long getHugePagesTotal() {
		if ( memInfoMap.containsKey("HugePagesTotal") )
			return memInfoMap.get("HugePagesTotal");
		else 
			return 0;
	}

	
	public long getHugePagesFree() {
		if ( memInfoMap.containsKey("HugePagesFree") )
			return memInfoMap.get("HugePagesFree");
		else
			return 0;
	}

	
	public long getHugePageSize() {
		if ( memInfoMap.containsKey("HugePagesSize") )
			return memInfoMap.get("HugePageSize");
		else
			return 0;
	}
	public void setBusyRate(float busyRate) {
		this.busyRate = busyRate;
	}
	public float getBusyRate() {
		return busyRate;
	}
	public void setMemUsedRate(float memUsedRate) {
		this.memUsedRate = memUsedRate;
	}
	public float getMemUsedRate() {
		return memUsedRate;
	}
	public float getUserRate() {
		return userRate;
	}
	public void setUserRate(float userRate) {
		this.userRate = userRate;
	}
	public float getSystemRate() {
		return systemRate;
	}
	public void setSystemRate(float systemRate) {
		this.systemRate = systemRate;
	}
	public float getIowaitRate() {
		return iowaitRate;
	}
	public void setIowaitRate(float iowaitRate) {
		this.iowaitRate = iowaitRate;
	}
	public float getIdleRate() {
		return idleRate;
	}
	public void setIdleRate(float idleRate) {
		this.idleRate = idleRate;
	}
}
