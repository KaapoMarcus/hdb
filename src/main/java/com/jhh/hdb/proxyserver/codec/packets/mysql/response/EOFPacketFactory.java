package com.jhh.hdb.proxyserver.codec.packets.mysql.response;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;

/**
 * EOF�������
 * 
 *
 */
public class EOFPacketFactory {

	/**
	 * ������������ݵĻ���أ��û���ص�position��δflip
	 * <pre>
	 * EOF Packet
	 * Bytes       Name
	 * -----       ----
	 *  1          field_count, always = 0xfe
	 *  2          warning_count
	 *  2          Status Flags
	 * </pre>
	 * @param packetNumber
	 * @param factory
	 * @param warningCount
	 * @param serverStatus
	 * @return
	 */
	public static ComBuffer getEOFPacketBuffer(BufferFactory factory,
			byte packetNumber, short warningCount, short serverStatus) {
		ComBuffer buffer = factory.getBuffer(9);
		
		//��ͷ
		MySQLPacketBuffer.writeInt3(buffer, 0);
		MySQLPacketBuffer.writeByte(buffer, packetNumber);
		
		MySQLPacketBuffer.writeByte(buffer, (byte)0xfe);
		MySQLPacketBuffer.writeShort(buffer, warningCount);
		MySQLPacketBuffer.writeShort(buffer, serverStatus);
		return buffer;
	}
}
