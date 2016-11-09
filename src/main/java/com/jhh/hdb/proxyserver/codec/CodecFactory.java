package com.jhh.hdb.proxyserver.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 编解码器类工厂
 * 
 *
 */
public class CodecFactory implements ProtocolCodecFactory {
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public CodecFactory() {
		encoder = new ResponseEncoder();
		decoder = new RequestDecoder();
	}

	public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
		return encoder;
	}

	public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
		return decoder;
	}
}
