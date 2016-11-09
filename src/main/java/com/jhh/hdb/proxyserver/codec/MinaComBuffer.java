package com.jhh.hdb.proxyserver.codec;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

/**
 *  通信缓冲池，具体实现为mina的IoBuffer
 * 
 *
 */
public class MinaComBuffer implements ComBuffer {
	// mina-buffer
	private IoBuffer buffer;
	// 子缓冲池列表
	private List<ComBuffer> bufferList;

	MinaComBuffer(IoBuffer buffer) {
		this.buffer = buffer;
		this.bufferList = new ArrayList<ComBuffer>();
	}

	public byte get() {
		return buffer.get();
	}

	public String getString(CharsetDecoder decoder)
			throws CharacterCodingException {
		return buffer.getString(decoder);
	}

	public void put(byte b) {
		buffer.put(b);
	}

	public void putString(String s, CharsetEncoder encoder)
			throws CharacterCodingException {
		buffer.putString(s, encoder);
	}
	
	public void skip(int length) {
		buffer.skip(length);
	}

	public Object getBufferObject() {
		return buffer;
	}

	public List<ComBuffer> getChildren() {
		return bufferList;
	}
}
