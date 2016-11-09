package com.jhh.hdb.proxyserver.codec;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.CachedBufferAllocator;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * 封装mina框架中的IOBuffer
 * 
 * AutoExpand
 * Writing variable-length data using NIO ByteBuffers is not really easy, 
 * and it is because its size is fixed. IoBuffer introduces autoExpand property. 
 * If autoExpand property is true, you never get BufferOverflowException or IndexOutOfBoundsException (except when index is negative). 
 * It automatically expands its capacity and limit value. 

 * There are two allocators provided out-of-the-box: 
 *  SimpleBufferAllocator (default) 
 *  CachedBufferAllocator 
 * 
 * 
 *
 */
public class IOBufferFactory implements BufferFactory {
	static{
		IoBuffer.setAllocator(new CachedBufferAllocator());

	}
	/**
	 * mina中的iobuffer，实现策略较多，可能需要以后优化
	 * @param size
	 * @return
	 */
	public ComBuffer getBuffer(int size) {
		//TODO: maybe need to be optimized
		IoBuffer buffer = IoBuffer.allocate(size);
		buffer.setAutoExpand(true);
		buffer.order(ByteOrder.LITTLE_ENDIAN);//MySQL包采用小端
		return new MinaComBuffer(buffer);
	}
}
