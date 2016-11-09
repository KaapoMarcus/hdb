package com.jhh.hdb.proxyserver.codec.packets.mysql.response;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jhh.hdb.proxyserver.codec.BufferFactory;
import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.EncodeException;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacket;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.ResultSetCmd;
import com.jhh.hdb.proxyserver.define.CharsetMappingTool;
import com.jhh.hdb.proxyserver.define.MysqlTypeDefs;
import com.jhh.hdb.proxyserver.define.ServerStatus;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.utils.ServerUtil;
import com.jhh.hdb.proxyserver.utils.ResultSetValue;
import com.jhh.hdb.proxyserver.utils.StmtPacketUtil;

public class ResultSetPacket extends MySQLPacket {
	public static final int PACKET_NUMBER_FIRST = 1;

	class RowDataReturn {
		List<ComBuffer> data;
		boolean needClose;

		public RowDataReturn(List<ComBuffer> data, boolean needClose) {
			super();
			this.data = data;
			this.needClose = needClose;
		}

	}

	public ResultSetPacket(BufferFactory factory) {
		super(factory);
	}

	@Override
	public MySQLCmd decode(ComBuffer in, SessionContext context)
			throws CharacterCodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComBuffer encode(MySQLCmd cmd) throws UnsupportedEncodingException,
			CharacterCodingException, EncodeException {
		ResultSetCmd rsCmd = (ResultSetCmd) cmd;

		if (rsCmd.getCache() != null) {
			ComBuffer buffer = encodeCachedResult(rsCmd.getCache());
			try {
				rsCmd.closeResultSet();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return buffer;
		}

		ResultSet resultSet = rsCmd.getRs();
		byte packetNumber = rsCmd.getPacketNumber();
		boolean needClose = false;
		if (null == resultSet)
			throw new IllegalArgumentException("ResultSet is null");

		try {
			ComBuffer buffer = createBuffer(0);
			List<ComBuffer> bufferList = buffer.getChildren();

			short serverStatus = rsCmd.getServerStatus();
			if (rsCmd.isPrepared()
					&& (!rsCmd.isStmtFetchBegin() || rsCmd.getFetchSize() == 0))
				serverStatus = (short) (serverStatus | (ServerStatus.SERVER_STATUS_CURSOR_EXISTS & 0xffff));

			// isStmtFetchBegin() == false: ����COM_FETCH_STMT����ķ��أ�����Ҫ��ʼ����ֶΰ�
			// isStmtFetchBegin() == true: ������ͨStatement�Ľ����PreparedStatement���ĵ�һ�η���
			if (rsCmd.isStmtFetchBegin() && rsCmd.isStreamFirst()) {
				// �����ʼ��
				bufferList.add(encodeHeaderPacket(resultSet));
				packetNumber++;

				// �ֶΰ��б�
				List<ComBuffer> fieldBufferList = encodeFieldPacket(resultSet,
						packetNumber);
				bufferList.addAll(fieldBufferList);
				packetNumber += fieldBufferList.size();

				// ���EOF Packet
				ComBuffer eofFields = EOFPacketFactory.getEOFPacketBuffer(
						bufferFactory, packetNumber, (short) 0, serverStatus);
				bufferList.add(eofFields);
				packetNumber++;
			}

			// ����ݰ��б�
			List<ComBuffer> rowDataList;
			RowDataReturn rowDataReturn;
			int maxRow = (GlobalContext.getInstance().getConfig()
					.isUseStreamFetch() && rsCmd.getKey() == null) ? GlobalContext
					.getInstance().getConfig().getStreamFetchSize()
					: Integer.MAX_VALUE;

			if (!rsCmd.isPrepared()) {// ��ͨ�������
				rowDataReturn = encodeRowDataPacket(resultSet, packetNumber,
						maxRow);
			} else if (rsCmd.getFetchSize() != 0) {//COM_FETCH_STMT����ķ��أ���δ�����α��Ԥ�������
				if (rsCmd.getFetchSize() > 0)
					maxRow = rsCmd.getFetchSize();
				rowDataReturn = encodeRowDataPacketInBinary(resultSet,
						packetNumber, maxRow);
			} else {
				//�����α��Ԥ�������ĵ�һ�η��أ����뷢����ݰ�
				return buffer;
			}
			rowDataList = rowDataReturn.data;
			needClose = rowDataReturn.needClose;
			bufferList.addAll(rowDataList);
			packetNumber += rowDataList.size();

			// ���EOF Packet
			if (needClose) {
				ComBuffer eofRows = EOFPacketFactory.getEOFPacketBuffer(
						bufferFactory, packetNumber, (short) 0, serverStatus);
				bufferList.add(eofRows);
			}

			return buffer;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EncodeException("SQLException in encoding resultset: "
					+ e.getMessage());
		} finally {
			rsCmd.setPacketNumber(packetNumber);
			rsCmd.setStreamFirst(false);
			try {
				if (needClose)
					rsCmd.closeResultSet();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getPacketSize(MySQLCmd cmd) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * ��������ʼ��
	 * 
	 * <pre>
	 * Result Set Header Packet 
	 * Bytes                        Name
	 * -----                        ----
	 * 1-9   (Length-Coded-Binary)  field_count
	 * 1-9   (Length-Coded-Binary)  extra
	 *  
	 * field_count: See the section "Types Of Result Packets"
	 *              to see how one can distinguish the
	 *              first byte of field_count from the first
	 *             byte of an OK Packet, or other packet types.
	 * 
	 * extra:       For example, SHOW COLUMNS uses this to send
	 *              the number of rows in the table.
	 * </pre>
	 * 
	 * @return
	 * @throws SQLException
	 */
	private ComBuffer encodeHeaderPacket(ResultSet resultset)
			throws SQLException {
		// ��ͷ4��field_count���9
		ComBuffer buffer = createBuffer(13);
		encodePacketNumber(buffer, (byte) 1);

		int fieldCount = resultset.getMetaData().getColumnCount();
		MySQLPacketBuffer.writeLengthCodedBinary(buffer, fieldCount);

		// extra�����ݲ����
		return buffer;
	}

	/**
	 * �����ֶ���Ϣ��ÿһ�ֶ����һ���������һ�������ʶ��
	 * 
	 * <pre>
	 * Field Packet 
	 *  Bytes                      Name
	 * -----                      ----
	 * n (Length Coded String)    catalog
	 * n (Length Coded String)    db
	 * 	n (Length Coded String)    table
	 * 	n (Length Coded String)    org_table
	 * 	n (Length Coded String)    name
	 * n (Length Coded String)    org_name
	 * 1                          (filler)
	 * 2                          charsetnr
	 * 4                          length
	 * 1                          type
	 * 2                          flags
	 * 1                          decimals
	 * 2                          (filler), always 0x00
	 * n (Length Coded Binary)    default
	 * </pre>
	 * 
	 * @param resultset
	 * @param beginPacketNumber
	 * @return
	 * @throws CharacterCodingException
	 * @throws UnsupportedEncodingException
	 */
	private List<ComBuffer> encodeFieldPacket(ResultSet resultset,
			byte beginPacketNumber) throws SQLException,
			UnsupportedEncodingException, CharacterCodingException {

		ResultSetMetaData metaData = resultset.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<ComBuffer> bufferList = new ArrayList<ComBuffer>(columnCount);

		if (columnCount > 0) {
			String charsetName = GlobalContext.getInstance().getConfig()
					.getCharset().name();
			final byte charsetIndex = CharsetMappingTool.getCharsetIndex(charsetName);

			for (int i = 1; i <= columnCount; i++) {
				ComBuffer buffer = createBuffer(5);
				encodePacketNumber(buffer, (byte) (beginPacketNumber++));

				// byte decimals = (byte)metaData.getScale(i);
				// DDBResultSetMetaData�ݲ�֧��getScale()����
				final byte decimals = 0;
				final int javaType = metaData.getColumnType(i);
				final byte mysqlType = (byte) (MysqlTypeDefs.javaType2MysqlType(javaType) & 0xff);
				final byte columnCharsetIndex = detectBinary(charsetIndex, javaType);
				final int columnLength = MysqlTypeDefs.getMysqlFieldLength(javaType, 
						charsetName, metaData.getColumnDisplaySize(i));

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
				MySQLPacketBuffer.writeShort(buffer, columnCharsetIndex);// charsetnr
				MySQLPacketBuffer.writeInt(buffer, columnLength);// length
				MySQLPacketBuffer.writeByte(buffer, mysqlType);// type
				MySQLPacketBuffer.writeShort(buffer,
						(short) toFlag(metaData, i));// flags
				MySQLPacketBuffer.writeByte(buffer, decimals);// decimals
				MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);
				MySQLPacketBuffer.writeByte(buffer, (byte) 0x00);// filler
				MySQLPacketBuffer.writeLengthCodedString(buffer, null);// default

				bufferList.add(buffer);
			}// for
		}// if

		return bufferList;
	}

	/**
	 * �����������Ϣ
	 * 
	 * <pre>
	 * ��Ԥ������䣬����ݲ����ַ�������ʽ��
	 * Bytes                   Name
	 * -----                   ----
	 * n (Length Coded String) (column value)
	 * ...
	 * </pre>
	 * 
	 * @param resultset
	 * @param beginPacketNumber
	 * @return
	 * @throws SQLException
	 * @throws CharacterCodingException
	 * @throws UnsupportedEncodingException
	 */
	private RowDataReturn encodeRowDataPacket(ResultSet resultset,
			byte beginPacketNumber, int maxRow) throws SQLException,
			UnsupportedEncodingException, CharacterCodingException {

		List<ComBuffer> bufferList = new LinkedList<ComBuffer>();
		ResultSetMetaData metaData = resultset.getMetaData();
		int columnCount = metaData.getColumnCount();
		int row = 1;
		boolean needClose = true;
		while (resultset.next()) {
			ComBuffer buffer = createBuffer(5);// ȡ5û����������
			encodePacketNumber(buffer, (byte) (beginPacketNumber++));
			for (int i = 1; i <= columnCount; i++) {
				switch (metaData.getColumnType(i)) {
				/*
				 * case����com.netease.backend.db.result.Record.getBytes()������Ӧ
				 * ����޸ģ��������ֶ���Ҫ�޸�
				 * */
				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
				case Types.BLOB: {
					MySQLPacketBuffer.writeLengthCodedBytes(buffer, resultset
							.getBytes(i));
					break;
				}
				case Types.TIMESTAMP: {
					String value = resultset.getString(i);
					if (GlobalContext.getInstance().getConfig()
							.isTimestampSkipNano() && value != null)
						value = ServerUtil.trimNano(value);
					MySQLPacketBuffer.writeLengthCodedString(buffer, value);
					break;
				}
				default: {
					MySQLPacketBuffer.writeLengthCodedString(buffer, resultset
							.getString(i));
				}
				}
			}
			bufferList.add(buffer);
			if (row >= maxRow) {
				needClose = false;
				break;
			}
			row++;
		}

		return new RowDataReturn(bufferList, needClose);
	}

	/**
	 * �Խ����ж����Ʊ��� 
	 * @param resultset
	 * @param beginPacketNumber
	 * @param count
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws CharacterCodingException
	 */
	private RowDataReturn encodeRowDataPacketInBinary(ResultSet resultset,
			byte beginPacketNumber, int maxRow) throws SQLException,
			UnsupportedEncodingException, CharacterCodingException {

		List<ComBuffer> bufferList = new LinkedList<ComBuffer>();

		if (maxRow == 0)
			return new RowDataReturn(/*Collections.emptyList()*/bufferList,
					false);

		ResultSetMetaData metaData = resultset.getMetaData();
		int columnCount = metaData.getColumnCount();
		int[] types = new int[columnCount];
		for (int i = 0; i < columnCount; i++) {// ��������Ϣ��������
			types[i] = metaData.getColumnType(i + 1);
		}

		int row = 1;
		boolean needClose = true;
		while (resultset.next()) {
			bufferList.add(encodeOneRowInBinary((byte) (beginPacketNumber++),
					columnCount, resultset, types));
			if (row >= maxRow) {
				needClose = false;
				break;
			}
			row++;
		}

		return new RowDataReturn(bufferList, needClose);
	}

	/**
	 * ��һ����ݽ��ж����Ʊ���
	 * </pre> Ԥ�������������ݲ��ö����Ʊ������ʽ�� 
	 * Bytes               Name 
	 * -----               ---- 
	 * 1                   0(packet header) 
	 * (col_count+7+2)/8   Null Bit Map with first two bits = 00 
	 * n                   column values 
	 * </pre>
	 * @param packetNumber
	 * @param columnCount
	 * @param resultset
	 * @param types
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws CharacterCodingException
	 * @throws SQLException
	 */
	private ComBuffer encodeOneRowInBinary(int packetNumber, int columnCount,
			ResultSet resultset, int[] types)
			throws UnsupportedEncodingException, CharacterCodingException,
			SQLException {

		ComBuffer buffer = createBuffer(9);// ȡ9û����������
		encodePacketNumber(buffer, (byte) packetNumber);
		MySQLPacketBuffer.writeByte(buffer, (byte) 0);// packet header

		int nullByteCount = (columnCount + 9) / 8;
		byte[] nullBitMap = new byte[nullByteCount];
		int nullMaskPos = 0;
		int bit = 4; // ǰ��λΪ00

		/*
		 * �Ƚ�ֵ��ȡ�����������Ƿ�Ϊnull���ж������null_bit_map�ı��룬������ֵ�������н���null�ж�
		 * ��Ϳ��Բ������ص��α꣬���������Ҫ����һ�����
		 */

		ResultSetValue[] values = new ResultSetValue[columnCount];
		for (int index = 0; index < columnCount; index++) {

			ResultSetValue value = new ResultSetValue();
			StmtPacketUtil.readResultSetValue(resultset, index + 1,
					types[index], value);
			values[index] = value;

			if (value.isNull) {
				nullBitMap[nullMaskPos] |= bit;
			}

			if (((bit <<= 1) & 255) == 0) {
				bit = 1; /* To next byte */
				nullMaskPos++;
			}
		}

		// Null Bit Map
		MySQLPacketBuffer.writeBytes(buffer, nullBitMap);

		// encode column values
		for (int index = 0; index < columnCount; index++) {
			if (!values[index].isNull)
				StmtPacketUtil.encodeValue(values[index], types[index], buffer);
		}

		return buffer;
	}

	/**
	 * ���뻺��Ľ�����
	 * 
	 * @param cachedResult
	 * @return
	 */
	private ComBuffer encodeCachedResult(List<byte[]> cachedResult) {
		if (cachedResult.size() > 1) {
			ComBuffer buffer = createBuffer(0);
			List<ComBuffer> bufferList = buffer.getChildren();

			for (byte[] array : cachedResult) {
				ComBuffer childBf = createBuffer(array.length);
				MySQLPacketBuffer.writeBytes(childBf, array);
				bufferList.add(childBf);
			}
			return buffer;
		} else if (cachedResult.size() == 1) {// һ�㲻�ᷢ��
			ComBuffer buffer = createBuffer(cachedResult.get(0).length);
			MySQLPacketBuffer.writeBytes(buffer, cachedResult.get(0));
			return buffer;
		} else
			throw new IllegalArgumentException("Cached ResultSet is empty");
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
		
		/*
		 * 0010 BLOB_FLAG
		 * 0080 BINARY_FLAG
		 * 0200 AUTO_INCREMENT_FLAG
		 */
		switch (metaData.getColumnType(column)) {
		case Types.BINARY:
		case Types.DATE:
		case Types.TIMESTAMP:
		case Types.TIME: {
			flags |= 0x80;
			break;
		}
		case Types.LONGVARCHAR: {
			flags |= 0x10;
			break;
		}
		case Types.VARBINARY:
		case Types.LONGVARBINARY: {
			flags |= 0x10;
			flags |= 0x80;
			break;
		}
		}

		if (metaData.isAutoIncrement(column)) {
			flags |= 0x200;
		}

		return flags;
	}
	
	/**
	 * ������ַ����ͣ����ַ�Ӧ����Ϊbinary
	 * <p>
	 * ���Connector/J 5.0.8 com.mysql.jdbc.Field�Ĺ��캯���֪��BLOB���͵��ַ����Ϊbinary���ܱ��ⱻת��ΪString
	 * </p>
	 * <p>
	 * 2012-09-19 ��MySQL���в��ԣ����ֳ����ַ����ͣ��������͵��ֶη��ص��ַ���binary
	 * </p>
	 * @param charsetIndex
	 * @param javaType
	 * @return
	 */
	private static byte detectBinary(byte charsetIndex, int javaType) {
		switch (javaType) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return charsetIndex;
		default:
			return CharsetMappingTool.getBinaryCharsetIndex();
		}
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
}
