package com.jhh.hdb.proxyserver.codec;

/**
 * ComBuffer工厂接口
 * 
 *
 */
public interface BufferFactory {
	
	public ComBuffer getBuffer(int size);
}
