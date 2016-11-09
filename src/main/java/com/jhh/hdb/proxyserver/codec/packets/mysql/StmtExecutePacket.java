package com.jhh.hdb.proxyserver.codec.packets.mysql;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtExecuteCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.StmtContext;

/**
 * Ԥ�������ִ������Ľ���
 * <pre>
 * Bytes                Name
 -----                ----
 1                    code
 4                    statement_id
 1                    flags
 4                    iteration_count
   if param_count > 0:
 (param_count+7)/8    null_bit_map
 1                    new_parameter_bound_flag
   if new_params_bound == 1:
 n*2                  type of parameters
 n                    values for the parameters 
 
 code:          always COM_EXECUTE
 
 statement_id:  statement identifier
 
 flags:         reserved for future use. In MySQL 4.0, always 0.
                In MySQL 5.0: 
                  0: CURSOR_TYPE_NO_CURSOR
                  1: CURSOR_TYPE_READ_ONLY
                  2: CURSOR_TYPE_FOR_UPDATE
                  4: CURSOR_TYPE_SCROLLABLE
 
 iteration_count: reserved for future use. Currently always 1.
 
 null_bit_map:  A bitmap indicating parameters that are NULL.
                Bits are counted from LSB, using as many bytes
                as necessary ((param_count+7)/8)
                i.e. if the first parameter (parameter 0) is NULL, then
                the least significant bit in the first byte will be 1.
 
 new_parameter_bound_flag:   Contains 1 if this is the first time
                             that "execute" has been called, or if
                             the parameters have been rebound.
 
 type:          Occurs once for each parameter; 
                The highest significant bit of this 16-bit value
                encodes the unsigned property. The other 15 bits
                are reserved for the type (only 8 currently used).
                This block is sent when parameters have been rebound
                or when a prepared statement is executed for the 
                first time.

 values:        for all non-NULL values, each parameters appends its value
                as described in Row Data Packet: Binary (column values)
 * </pre>
 * 
 *
 */
public class StmtExecutePacket extends MySQLPacket {

	public StmtExecutePacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext SessionContext)
			throws CharacterCodingException, DecodeException {
		StmtExecuteCmd cmd = new StmtExecuteCmd();
		decodePacketHeader(cmd, in);
		in.skip(1);//�����������
//		final byte commandType = MySQLPacketBuffer.readByte(in);
//		if (commandType != MySQLCommandNumber.COM_STMT_EXECUTE)
//			throw new DecodeException("Expected " + MySQLCommandNumber.COM_STMT_EXECUTE 
//					+ "(COM_STMT_EXECUTE), but was " + commandType);
		
		if (SessionContext == null)
			throw new DecodeException("SessionContext for decoding ExecutePacket is null.");
		
		int statementeId = MySQLPacketBuffer.readInt(in);
		StmtContext stmtContext = SessionContext.getStmtContextMap().get(statementeId);
		if (stmtContext == null)
			throw new DecodeException("StmtContext for decoding ExecutePacket is null. StatementId:" + statementeId);
		
		cmd.setStatementId(statementeId);
		cmd.setFlags(MySQLPacketBuffer.readByte(in));
		cmd.setIterationCount(MySQLPacketBuffer.readInt(in));
		
		final int paramCount = stmtContext.getParameterCount();
		
		if (paramCount > 0) {
			final int nullCount = (paramCount + 7) / 8;
	        byte[] nullBitsBuffer = new byte[nullCount];
	        for (int i = 0; i < nullCount; i++) {
	            nullBitsBuffer[i] = MySQLPacketBuffer.readByte(in);
	        }
	        
	        //null bit map
	        boolean[] nullMap = new boolean[paramCount];
	        for (int i = 0; i < paramCount; i++) {
	        	if ((nullBitsBuffer[i / 8] & (1 << (i & 7))) != 0) {
	        		nullMap[i] = true;
	        	}
	        }
	        cmd.setNullMap(nullMap);
	        
	        final byte newParameterBoundFlag = MySQLPacketBuffer.readByte(in);
	        if (newParameterBoundFlag == (byte)1) {
	        	//��ȡ�µĲ������Ͳ�����context
	        	List<Short> newParamTypes = new ArrayList<Short>(paramCount);
	        	for (int i = 0; i < paramCount; i++) {
	        		newParamTypes.add(MySQLPacketBuffer.readShort(in));
	        	}
	        	stmtContext.setParamTypes(newParamTypes);
	        }
	        
        	//CommandType(1), statement_id(4), flags(1), iteration_count(4), null_bit_map, new_parameter_bound_flag(1)
        	int paramValueLength = cmd.getLength() - 1 - 4 - 1 - 4 - nullCount - 1;
        	//parameter_types
        	if (newParameterBoundFlag == (byte)1) {
        		paramValueLength -= 2 * paramCount;
        	}
        	
        	if (paramValueLength > 0) {
        		//���ֵ��ͨ��SEND_LONG_DATA���͹����������ֵΪnull
	        	byte[] value = MySQLPacketBuffer.readBytes(in, paramValueLength);
	        	cmd.setParametersValueBuffer(new ParameterBuffer(value));
        	}
		} else {
			if (cmd.getLength() == 11) {
				/*
				 * ���������Ϊ0ʱ��new_parameter_bound_flag��ʶλ���ܴ���.
				 * �����������������Buffer�У����ⱻ�ϲ����������ݰ���
				 */
				MySQLPacketBuffer.readByte(in);
			}
		}
        
		return cmd;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd)
			throws UnsupportedEncodingException, CharacterCodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		// TODO Auto-generated method stub
		return 0;
	}

}

/**
 * ������ֵ��װΪComBuffer��ֻ��Ҫʵ��get()����
 * 
 *
 */
final class ParameterBuffer implements ComBuffer {

	byte[] content;
	private int pos = 0;
	
	public ParameterBuffer(byte[] value) {
		if (value == null) 
			throw new IllegalArgumentException("byte[] value is null.");
		this.content = value;
	}
	
	public byte get() {
		
		return content[pos++];
	}

	public Object getBufferObject() {
		throw new UnsupportedOperationException("ParameterBuffer doesn't support getBufferObject()");
	}

	public List<ComBuffer> getChildren() {
		throw new UnsupportedOperationException("ParameterBuffer doesn't supported getChildren()");
	}

	public String getString(CharsetDecoder decoder)
			throws CharacterCodingException {
		throw new UnsupportedOperationException("ParameterBuffer doesn't supported getString()");
	}

	public void put(byte b) {
		throw new UnsupportedOperationException("ParameterBuffer doesn't supported put()");
	}

	public void putString(String s, CharsetEncoder encoder)
			throws CharacterCodingException {
		throw new UnsupportedOperationException("ParameterBuffer doesn't supported putString()");
	}

	public void skip(int length) {
		throw new UnsupportedOperationException("ParameterBuffer doesn't supported skip()");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (content == null)
			sb.append("content-null ");
		else
			sb.append("length-").append(this.content.length);
		sb.append(" pos-").append(this.pos);
		return sb.toString();
	}
	
}