package com.jhh.hdb.proxyserver.codec.packets.mysql.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.codec.EncodeException;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.PrepareOkCmd;
import com.jhh.hdb.proxyserver.define.CharsetMappingTool;
import com.jhh.hdb.proxyserver.define.MysqlTypeDefs;
import com.jhh.hdb.proxyserver.define.ServerStatus;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ��PrepareOkCmd���б���
 * 
 *
 */
public class PrepareOkPacket extends MySQLPacket {

	public PrepareOkPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context)
			throws CharacterCodingException, DecodeException {
		return null;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd) throws UnsupportedEncodingException,
			CharacterCodingException, EncodeException {
		PrepareOkCmd preparedOkCmd  = (PrepareOkCmd) cmd;
		ComBuffer buffer = createBuffer(0);
		List<ComBuffer> bufferList = buffer.getChildren();
		
		try {
			//��һ����
			bufferList.add(encodeFirstPacket(preparedOkCmd));
			
			byte packetNumber = 2;
			
			//�����
			if (preparedOkCmd.getParameterCount() > 0) {
				List<ComBuffer> paramPackets = encodeParameterPackets(preparedOkCmd, packetNumber);
				bufferList.addAll(paramPackets);
				packetNumber += paramPackets.size();
				
				//���EOF Packet
				ComBuffer eofFields = EOFPacketFactory.getEOFPacketBuffer(
						bufferFactory, packetNumber, (short) 0,
						ServerStatus.SERVER_STATUS_AUTOCOMMIT);
				bufferList.add(eofFields);
				packetNumber++;
			}
			
			//�ֶΰ�
			if (preparedOkCmd.getResultSetMetaData() != null) {
				List<ComBuffer> paramPackets = encodeFieldPackets(preparedOkCmd, packetNumber);
				bufferList.addAll(paramPackets);
				packetNumber += paramPackets.size();
				
				//���EOF Packet
				ComBuffer eofFields = EOFPacketFactory.getEOFPacketBuffer(
						bufferFactory, packetNumber, (short) 0,
						ServerStatus.SERVER_STATUS_AUTOCOMMIT);
				bufferList.add(eofFields);
				packetNumber++;
			}
			
			return buffer;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EncodeException("SQLException in encoding preparedOkPacket: "
					+ e.getMessage());
		}
	}
	
	/**
	 * �����һ����
	 * <pre>
	 * Bytes    Name
 	 * -----    ----
 	 *	1       0 - marker for OK packet
 	 *	4       statement_handler_id
 	 *	2       number of columns in result set
 	 * 	2       number of parameters in query
 	 *	1       filler (always 0)
 	 *	2       warning count
	 * </pre>
	 * @param cmd
	 * @return
	 * @throws SQLException 
	 */
	private ComBuffer encodeFirstPacket(PrepareOkCmd cmd) throws SQLException {
		ComBuffer buffer = createBuffer(12);
		encodePacketNumber(buffer, (byte)1);//��һ����ı��Ϊ1
		
		MySQLPacketBuffer.writeByte(buffer, (byte)0);
		MySQLPacketBuffer.writeInt(buffer, cmd.getStatementId());
		MySQLPacketBuffer.writeShort(buffer, (short) (cmd
				.getResultSetMetaData() == null ? 0 : cmd
				.getResultSetMetaData().getColumnCount()));
		MySQLPacketBuffer.writeShort(buffer, cmd.getParameterCount());
		MySQLPacketBuffer.writeByte(buffer, (byte)0);//filler
		MySQLPacketBuffer.writeShort(buffer, cmd.getWarningCount());
		
		return buffer;
	}
	
	private List<ComBuffer> encodeParameterPackets(PrepareOkCmd cmd,
			byte beginPacketNumber) throws UnsupportedEncodingException,
			CharacterCodingException {
		
		List<ComBuffer> paramList = new ArrayList<ComBuffer>(cmd.getParameterCount());
		for (int i = 0; i < cmd.getParameterCount(); i++) {
			ComBuffer buffer = createBuffer(5);
			encodePacketNumber(buffer, (byte)(beginPacketNumber++));
			fillParameterOrField(buffer);
			paramList.add(buffer);
		}
		return paramList;
	}
	
	private List<ComBuffer> encodeFieldPackets(PrepareOkCmd cmd,
			byte beginPacketNumber) throws UnsupportedEncodingException,
			CharacterCodingException, SQLException {
		final ResultSetMetaData metaData = cmd.getResultSetMetaData();
		final int columnCount = metaData.getColumnCount();
		List<ComBuffer> fieldList = new ArrayList<ComBuffer>(columnCount);
		
		
		String charsetName = GlobalContext.getInstance().getConfig().getCharset().name();
		byte charsetIndex = CharsetMappingTool.getCharsetIndex(charsetName);
		
		for (int i = 1; i <= columnCount; i++) {
			ComBuffer buffer = createBuffer(5);
			encodePacketNumber(buffer, (byte) (beginPacketNumber++));
		
			int javaType = metaData.getColumnType(i);
			byte mysqlType = (byte) (MysqlTypeDefs.javaType2MysqlType(javaType) & 0xff);
		
			// ���Э�飬catalog����Ϊ"def"
			MySQLPacketBuffer.writeLengthCodedString(buffer, "def");
			MySQLPacketBuffer.writeLengthCodedString(buffer, metaData
					.getSchemaName(i));// db
			MySQLPacketBuffer.writeLengthCodedString(buffer, metaData
					.getTableName(i));// table
			MySQLPacketBuffer.writeLengthCodedString(buffer, metaData
					.getTableName(i));// org_table
			MySQLPacketBuffer.writeLengthCodedString(buffer, metaData
					.getColumnName(i));// name
			MySQLPacketBuffer.writeLengthCodedString(buffer, metaData
					.getColumnLabel(i));// org_name
			MySQLPacketBuffer.writeByte(buffer, (byte) 0x0c);// filler
			MySQLPacketBuffer.writeShort(buffer, charsetIndex);// charsetnr
			MySQLPacketBuffer.writeInt(buffer, /*metaData.getColumnDisplaySize(i)*/	0);// length
			MySQLPacketBuffer.writeByte(buffer, mysqlType);// type
			MySQLPacketBuffer.writeShort(buffer, (short) toFlag(metaData, i));// flags
			MySQLPacketBuffer.writeByte(buffer, (byte) 0);// decimals
			MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);
			MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);// filler
			MySQLPacketBuffer.writeLengthCodedString(buffer, null);// default
		
			fieldList.add(buffer);
		}// for
		
		return fieldList;
	} 

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * ���հ��Ŷ԰�ͷ���б��룬���Ԥ��Ϊ0
	 */
	private void encodePacketNumber(ComBuffer buffer, byte number) {
		MySQLPacketBuffer.writeInt3(buffer, 0);
		MySQLPacketBuffer.writeByte(buffer, number);
	}

	/**
	 * ����Ϊ��������ֶε���ݰ������������ݡ�
	 * @param buffer
	 * @throws CharacterCodingException 
	 * @throws UnsupportedEncodingException 
	 */
	private static void fillParameterOrField(ComBuffer buffer)
			throws UnsupportedEncodingException, CharacterCodingException {
		MySQLPacketBuffer.writeLengthCodedString(buffer, "def");
		MySQLPacketBuffer.writeLengthCodedString(buffer, "ddb");//db
		MySQLPacketBuffer.writeLengthCodedString(buffer, "ddb");//table
		MySQLPacketBuffer.writeLengthCodedString(buffer, "ddb");//org_table
		MySQLPacketBuffer.writeLengthCodedString(buffer, "ddb");//name
		MySQLPacketBuffer.writeLengthCodedString(buffer, "ddb");//org_name
		MySQLPacketBuffer.writeByte(buffer, (byte) 0x0c);//filler
		MySQLPacketBuffer.writeShort(buffer, (short)8);//charsetnr
		MySQLPacketBuffer.writeInt(buffer, 0);//length
		MySQLPacketBuffer.writeByte(buffer, (byte)1);//type
		MySQLPacketBuffer.writeShort(buffer, (short)0);//flags
		MySQLPacketBuffer.writeByte(buffer, (byte)0);//decimals
		MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);
		MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);//filler
		MySQLPacketBuffer.writeByte(buffer, (byte)251);//default, length_coded_string null
	}
	
	/**
	 * �����ֶε�flagsֵ
	 * 
	 * @param metaData
	 * @param column
	 * @return
	 * @throws SQLException
	 */
	private static int toFlag(ResultSetMetaData metaData, int column)
			throws SQLException {

		int flags = 0;
		if (metaData.isAutoIncrement(column)) {
			flags |= 0200;
		}
		return flags;
	}
	
}
