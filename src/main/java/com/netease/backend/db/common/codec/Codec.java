package com.netease.backend.db.common.codec;

import java.util.List;


public interface Codec {
	
	byte[] encode(int[] fieldNumbers, List<Object> objectList) throws EncodeException;
	
	
	public List<Object> decode(int[] fieldNumbers, byte[] buffer) throws DecodeException;
	
}
