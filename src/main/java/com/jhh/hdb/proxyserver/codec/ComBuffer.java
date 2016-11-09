package com.jhh.hdb.proxyserver.codec;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

/**
 * 通信缓冲池的抽象类
 * 
 *
 */
public interface ComBuffer {
	/**
	 * 从缓冲池中获取一个byte
	 * @return
	 */
	public byte get();
	
	/**
	 * 从缓冲池中获取字符串：null-terminated string
	 * @param decoder
	 * @return
	 * @throws CharacterCodingException
	 */
	public String getString(CharsetDecoder decoder)
			throws CharacterCodingException;

	/**
	 * 向缓冲池中写入一个byte
	 * @param b
	 */
	public void put(byte b);

	/**
	 * 向缓冲池中写入字符串：null-terminated string
	 * @param s
	 * @param encoder
	 * @throws CharacterCodingException
	 */
	public void putString(String s, CharsetEncoder encoder)
			throws CharacterCodingException;
	
	/**
	 * 在缓冲池中跳过指定数量的byte
	 * @param length
	 */
	public void skip(int length);
	
	/**
	 * 获取子缓冲池的列表
	 * @return
	 */
	public List<ComBuffer> getChildren();
	
	/**
	 * 返回底层的缓冲池对象
	 * @return
	 */
	public Object getBufferObject();
}
