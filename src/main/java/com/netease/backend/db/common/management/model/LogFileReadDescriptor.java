package com.netease.backend.db.common.management.model;

import java.io.Serializable;



public class LogFileReadDescriptor implements Serializable {
	private static final long serialVersionUID = 4587973134625117402L;
	
	public static final int NO_DIRECTION = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;
	
	private static final int CHUNK_SIZE = 16384;
	
	private String fileName;
	private int direction;
	private int chunkCount;
	private int chunkSize;
	
	public LogFileReadDescriptor(String fileName, int direction) {
		this.fileName = fileName;
		this.direction = direction;
		this.chunkCount = 1;
		this.chunkSize = CHUNK_SIZE;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	public void setDirection(int d) {
		if (d != direction) {
			chunkCount = 1;
			this.direction = d;
		}
	}
	
	public int getChunkCount() {
		return this.chunkCount;
	}
	
	public void setChunkCount(int c) {
		this.chunkCount = c;
	}
	
	public int getChunkSize() {
		return this.chunkSize;
	}
	
	public void setChunkSize(int size) {
		this.chunkSize = size;
	}
}
