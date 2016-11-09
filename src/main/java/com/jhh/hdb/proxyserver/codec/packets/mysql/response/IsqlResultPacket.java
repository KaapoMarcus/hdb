package com.jhh.hdb.proxyserver.codec.packets.mysql.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

import com.netease.cli.StringTable;
import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.IsqlResultCmd;
import com.jhh.hdb.proxyserver.define.CharsetMappingTool;
import com.jhh.hdb.proxyserver.define.MysqlTypeDefs;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;

public class IsqlResultPacket extends MySQLPacket {

	public IsqlResultPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context)
			throws CharacterCodingException, DecodeException,
			UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd) throws UnsupportedEncodingException,
			CharacterCodingException {
		IsqlResultCmd rsCmd = (IsqlResultCmd) cmd;
		StringTable stringTable = rsCmd.getStringTable();
		
		if (stringTable == null)
			throw new NullPointerException("StringTable is null.");
		
		ComBuffer buffer = createBuffer(0);
		List<ComBuffer> bufferList = buffer.getChildren();
		byte packetNumber = 1;
		
		// �����ʼ��
		bufferList.add(encodeHeaderPacket(stringTable));
		packetNumber++;
		
		// �ֶΰ��б�
		List<ComBuffer> fieldBufferList = encodeFieldPacket(stringTable,
				packetNumber);
		bufferList.addAll(fieldBufferList);
		packetNumber += fieldBufferList.size();

		// ���EOF Packet
		ComBuffer eofFields = EOFPacketFactory.getEOFPacketBuffer(
				bufferFactory, packetNumber, (short) 0, rsCmd.getServerStatus());
		bufferList.add(eofFields);
		packetNumber++;
		
		// ��ݰ��б�
		List<ComBuffer> rowDataList = encodeRowDataPacket(stringTable, packetNumber);
		bufferList.addAll(rowDataList);
		packetNumber += rowDataList.size();
		
		// ���EOF Packet
		ComBuffer eofRows = EOFPacketFactory.getEOFPacketBuffer(
				bufferFactory, packetNumber, (short) 0, rsCmd.getServerStatus());
		bufferList.add(eofRows);
		
		return buffer;
	}
	
	private ComBuffer encodeHeaderPacket(StringTable stringTable) {
		// ��ͷ4��field_count���9
		ComBuffer buffer = createBuffer(13);
		encodePacketNumber(buffer, (byte) 1);

		int fieldCount = stringTable.getNumColumns();
		MySQLPacketBuffer.writeLengthCodedBinary(buffer, fieldCount);

		// extra�����ݲ����
		return buffer;
	}
	
	
	private List<ComBuffer> encodeFieldPacket(StringTable stringTable,
			byte beginPacketNumber) throws UnsupportedEncodingException, CharacterCodingException {

		final int columnCount = stringTable.getNumColumns();
		List<ComBuffer> bufferList = new ArrayList<ComBuffer>(columnCount);

		if (columnCount > 0) {
			String charsetName = GlobalContext.getInstance().getConfig()
					.getCharset().name();
			final byte charsetIndex = CharsetMappingTool.getCharsetIndex(charsetName);
			//�ֶ������޶�Ϊ�ַ�
			final byte mysqlType = (byte) (MysqlTypeDefs.FIELD_TYPE_STRING & 0xff);
			
			for (int i = 0; i < columnCount; i++) {
				ComBuffer buffer = createBuffer(5);
				encodePacketNumber(buffer, (byte) (beginPacketNumber++));

				// ���Э�飬catalog����Ϊ"def"
				MySQLPacketBuffer.writeLengthCodedString(buffer, "def");
				MySQLPacketBuffer.writeLengthCodedString(buffer, "ddb");// db�����Ϊddb
				MySQLPacketBuffer.writeLengthCodedString(buffer, stringTable.getName());// table
				MySQLPacketBuffer.writeLengthCodedString(buffer, stringTable.getName());// org_table
				MySQLPacketBuffer.writeLengthCodedString(buffer, stringTable.getHeader()[i]);// name
				MySQLPacketBuffer.writeLengthCodedString(buffer, stringTable.getHeader()[i]);// org_name
				MySQLPacketBuffer.writeByte(buffer, (byte) 0x0c);// filler
				MySQLPacketBuffer.writeShort(buffer, charsetIndex);// charsetnr
				MySQLPacketBuffer.writeInt(buffer, 0);// length
				MySQLPacketBuffer.writeByte(buffer, mysqlType);// type
				MySQLPacketBuffer.writeShort(buffer, (short) 0);// flags
				MySQLPacketBuffer.writeByte(buffer, (byte)0);// decimals�����Ϊ0
				MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);
				MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);// filler
				MySQLPacketBuffer.writeLengthCodedString(buffer, null);// default

				bufferList.add(buffer);
			}// for
		}// if

		return bufferList;
	}
	
	
	private List<ComBuffer> encodeRowDataPacket(StringTable stringTable,
			byte beginPacketNumber) throws UnsupportedEncodingException, CharacterCodingException {

		final int rowCount = stringTable.getData().size();
		List<ComBuffer> bufferList = new ArrayList<ComBuffer>(rowCount);
		int columnCount = stringTable.getNumColumns();
		if (rowCount > 0) {
			for (String[] row : stringTable.getData()) {
				ComBuffer buffer = createBuffer(5);// ȡ5û����������
				encodePacketNumber(buffer, (byte) (beginPacketNumber++));
				for (int i = 0; i < columnCount; i++) {
					MySQLPacketBuffer.writeLengthCodedString(buffer, row[i]);
				}
				bufferList.add(buffer);
			}
		}
		return bufferList;
	}
	
	/**
	 * ���հ��Ŷ԰�ͷ���б��룬���Ԥ��Ϊ0
	 * 
	 * @param buffer
	 * @param number
	 */
	private void encodePacketNumber(ComBuffer buffer, byte number) {
		MySQLPacketBuffer.writeInt3(buffer, 0);
		MySQLPacketBuffer.writeByte(buffer, number);
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		// TODO Auto-generated method stub
		return 0;
	}

}
