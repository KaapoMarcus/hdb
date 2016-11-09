package com.jhh.hdb.proxyserver.codec;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.jhh.hdb.proxyserver.codec.packets.mysql.response.ErrorPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.response.HandshakePacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.response.IsqlResultPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.response.OkPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.response.PrepareOkPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.response.ResultSetPacket;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;

/**
 * 编码器，把MySQLCmd编码后发送给客户端
 * 
 *
 */
public class ResponseEncoder extends ProtocolEncoderAdapter {
	//编码工具类实例
	private HandshakePacket handshakePacket;
	private ErrorPacket errorPacket;
	private OkPacket okPacket;
	private PrepareOkPacket prepareOkPacket;
	private ResultSetPacket resultSetPacket;
	private IsqlResultPacket isqlResultPacket;

	ResponseEncoder() {
		BufferFactory factory = new IOBufferFactory();
		this.handshakePacket = new HandshakePacket(factory);
		this.errorPacket = new ErrorPacket(factory);
		this.okPacket = new OkPacket(factory);
		this.prepareOkPacket = new PrepareOkPacket(factory);
		this.resultSetPacket = new ResultSetPacket(factory);
		this.isqlResultPacket = new IsqlResultPacket(factory);
	}

	/**
	 * 做编码操作，由mina框架负责调用
	 */
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		MySQLCmd cmd = (MySQLCmd) message;

		ComBuffer comBuffer = null;
		switch (cmd.getType()) {
			case handshake: {
				comBuffer = handshakePacket.encode(cmd);
				break;
			}
			case error: {
				comBuffer = errorPacket.encode(cmd);
				break;
			}
			case ok: {
				comBuffer = okPacket.encode(cmd);
				break;
			}
			case resultset: {
				comBuffer = resultSetPacket.encode(cmd);
				break;
			}
			case prepareok: {
				comBuffer = prepareOkPacket.encode(cmd);
				break;
			}
			case isqlresult: {
				comBuffer = isqlResultPacket.encode(cmd);
				break;
			}
			default:
				throw new Exception("encode failed, unknown cmd type:"
						+ cmd.getType());
		}
		
		//缓存
		if (cmd.getKey() != null) {
			writeCache(cmd.getKey(), comBuffer);
		}
		
		//如果子缓冲池列表不为空，则父缓冲池无内容
		if (comBuffer.getChildren().size() > 0) {
			for (ComBuffer cb : comBuffer.getChildren())
				writeOut(cb, out);
		} else {
			writeOut(comBuffer, out);
		}
	}
	
	private void writeOut(ComBuffer comBuffer, ProtocolEncoderOutput out) {
		IoBuffer buffer = (IoBuffer)comBuffer.getBufferObject();
		buffer.flip();
		//前3个字节记录除包头外的包长度
		buffer.putMediumInt(0, buffer.remaining() - 4);
		out.write(buffer);
		//System.out.println(buffer);
	}
	
	/**将缓冲池的数据复制到ComBufferCache*/
	private void writeCache(String key, ComBuffer comBuffer) {
		List<byte[]> list = new ArrayList<byte[]>();
		if (comBuffer.getChildren().size() > 0) {
			for (ComBuffer cb : comBuffer.getChildren()) {
				list.add(copyToArray((IoBuffer)cb.getBufferObject()));
			}
		} else {
			list.add(copyToArray((IoBuffer)comBuffer.getBufferObject()));
		}
		ComBufferCache.put(key, list);
	}
	
	/**将IoBuffer中的数据拷贝到byte数组*/
	private byte[] copyToArray(IoBuffer buffer) {
		//调整游标位置
		buffer.flip();
		int length = buffer.remaining();
		byte[] copy = new byte[length];
		for (int i = 0; i < length; i++)
			copy[i] = buffer.get();
		//游标回到原来的位置
		return copy;
	}
}
