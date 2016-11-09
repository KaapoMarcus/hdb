package com.jhh.hdb.proxyserver.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.CharacterCodingException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import com.jhh.hdb.proxyserver.codec.ComBuffer;
import com.jhh.hdb.proxyserver.codec.DecodeException;
import com.jhh.hdb.proxyserver.codec.packets.mysql.MySQLPacketBuffer;
import com.jhh.hdb.proxyserver.define.MysqlTypeDefs;
import com.jhh.hdb.proxyserver.server.GlobalContext;

public class StmtPacketUtil {

	/**
	 * read data from buffer into Object
	 * @param MySQLPacketBuffer
	 * @param bindValue
	 * @throws UnsupportedEncodingException 
	 */
	public static Object readParameterValue(ComBuffer buffer, short type)
			throws DecodeException, UnsupportedEncodingException {
		switch (type & 0xff) {
		case MysqlTypeDefs.FIELD_TYPE_TINY:
		case MysqlTypeDefs.FIELD_TYPE_YEAR://YEAR存储方式与TINYINT一样
			return MySQLPacketBuffer.readByte(buffer);
		case MysqlTypeDefs.FIELD_TYPE_SHORT:
			return MySQLPacketBuffer.readShort(buffer);
		case MysqlTypeDefs.FIELD_TYPE_INT24:
			return MySQLPacketBuffer.readInt3(buffer);
		case MysqlTypeDefs.FIELD_TYPE_LONG:
			return MySQLPacketBuffer.readInt(buffer);
		case MysqlTypeDefs.FIELD_TYPE_LONGLONG:
			return MySQLPacketBuffer.readLong(buffer);
		case MysqlTypeDefs.FIELD_TYPE_FLOAT:
			return MySQLPacketBuffer.readFloat(buffer);
		case MysqlTypeDefs.FIELD_TYPE_DOUBLE:
			return MySQLPacketBuffer.readDouble(buffer);
		case MysqlTypeDefs.FIELD_TYPE_TIME:
			return readTime(buffer);
		case MysqlTypeDefs.FIELD_TYPE_DATE:
		case MysqlTypeDefs.FIELD_TYPE_DATETIME:
		case MysqlTypeDefs.FIELD_TYPE_TIMESTAMP:
			return readDateObject(buffer);
		case MysqlTypeDefs.FIELD_TYPE_VAR_STRING:
		case MysqlTypeDefs.FIELD_TYPE_STRING:
		case MysqlTypeDefs.FIELD_TYPE_VARCHAR:
			return MySQLPacketBuffer.readLengthCodedString(buffer);
		case MysqlTypeDefs.FIELD_TYPE_DECIMAL:
		case MysqlTypeDefs.FIELD_TYPE_NEW_DECIMAL:
			String value = MySQLPacketBuffer.readLengthCodedString(buffer);
			if (value == null)
				return null;
			return new BigDecimal(value);
		case MysqlTypeDefs.FIELD_TYPE_TINY_BLOB:
		case MysqlTypeDefs.FIELD_TYPE_BLOB:
		case MysqlTypeDefs.FIELD_TYPE_MEDIUM_BLOB:
		case MysqlTypeDefs.FIELD_TYPE_LONG_BLOB:
			/*
			 * Connector/J 5.0.8伪预处理的setBlob()有时会带入字符转义导致内容被修改的问题，
			 * 读取为byte[]，DBI在处理时调用的也会是MySQL的setBytes()接口，避开可能的错误
			 */
			return readBlobInBytes(buffer);
		case MysqlTypeDefs.FIELD_TYPE_NULL:
			return null;
			
		/*
		 * 以上类型主要通过该文档确认：http://forge.mysql.com/wiki/MySQL_Internals
		 * 由于没能找到完整的文档说明，无法确认以下5中类型的编码方式
		 * 参照MySQL5.1的源码libmysql.c中mysql_stmt_bind_param()方法的处理方式，不支持以下5种类型
		 * 
		 */
		case MysqlTypeDefs.FIELD_TYPE_GEOMETRY: 
		case MysqlTypeDefs.FIELD_TYPE_NEWDATE:
		case MysqlTypeDefs.FIELD_TYPE_BIT:
		case MysqlTypeDefs.FIELD_TYPE_ENUM:
		case MysqlTypeDefs.FIELD_TYPE_SET:
		default: {
			throw new DecodeException("Unsupported parameter value type of prepared statement:" + type);
		}
		}
	}
	
	/**
	 * 读取结果集中的数据
	 * @param rs
	 * @param columnIndex
	 * @param javaType
	 * @param value
	 */
	public static void readResultSetValue(ResultSet rs, int columnIndex,
			int javaType, ResultSetValue value) throws SQLException {
		/*
		 * 这里的jdbc类型，是根据Connector/J_5.0.8整理出来的，只会有以下类型出现
		 */
		switch(javaType) {
		case Types.TINYINT:
			value.byteBinding = rs.getByte(columnIndex);
			break;
		case Types.SMALLINT:
			value.shortBinding = rs.getShort(columnIndex);
			break;
		case Types.INTEGER:
			value.intBinding = rs.getInt(columnIndex);
			break;
		case Types.BIGINT:
			value.longBinding = rs.getLong(columnIndex);
			break;
		case Types.REAL:
			value.floatBinding = rs.getFloat(columnIndex);
			break;
		case Types.DOUBLE:
			value.doubleBinding = rs.getDouble(columnIndex);
			break;
		case Types.DECIMAL:
			value.value = rs.getBigDecimal(columnIndex);
			break;
		case Types.TIME:
			value.value = rs.getTime(columnIndex);
			break;
		case Types.TIMESTAMP:
			value.value = rs.getTimestamp(columnIndex);
			break;
		case Types.DATE:
			value.value = rs.getDate(columnIndex);
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			value.value = rs.getString(columnIndex);
			break;
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BINARY:
		case Types.BIT:
			value.value = rs.getBytes(columnIndex);
			break;
		case Types.NULL://理论上不会出现
			value.isNull = true;
			return;
		default:
			throw new SQLException("Unsupported jdbc type for resultset reading:" + javaType);
		}
		
		value.isNull = rs.wasNull();
	}
	
	/**
	 * 根据具体类型将数据编码到缓冲池
	 * @param value
	 * @param javaType
	 * @param buffer
	 * @throws CharacterCodingException 
	 * @throws UnsupportedEncodingException 
	 * @throws SQLException 
	 */
	public static void encodeValue(ResultSetValue value, int javaType,
			ComBuffer buffer) throws UnsupportedEncodingException, CharacterCodingException, SQLException {
		/*
		 * 这里的jdbc类型，是根据Connector/J_5.0.8整理出来的，只会有以下类型出现
		 */
		if (value.isNull)
			return;
		
		switch(javaType) {
		case Types.TINYINT:
			MySQLPacketBuffer.writeByte(buffer, value.byteBinding);
			break;
		case Types.SMALLINT:
			MySQLPacketBuffer.writeShort(buffer, value.shortBinding);
			break;
		case Types.INTEGER:
			MySQLPacketBuffer.writeInt(buffer, value.intBinding);
			break;
		case Types.BIGINT:
			MySQLPacketBuffer.writeLong(buffer, value.longBinding);
			break;
		case Types.REAL:
			MySQLPacketBuffer.writeFloat(buffer, value.floatBinding);
			break;
		case Types.DOUBLE:
			MySQLPacketBuffer.writeDouble(buffer, value.doubleBinding);
			break;
		case Types.DECIMAL:
			MySQLPacketBuffer.writeLengthCodedString(buffer, value.value.toString());
			break;
		case Types.TIME:
			storeTime(buffer, (Time)value.value);
			break;
		case Types.TIMESTAMP:
			storeSqlTimestamp(buffer, (Timestamp)value.value);
			break;
		case Types.DATE:
			storeSqlDate(buffer, (java.sql.Date)value.value);
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			MySQLPacketBuffer.writeLengthCodedString(buffer, (String)value.value);
			break;
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BINARY:
		case Types.BIT:
			byte[] content = (byte[])value.value;
			MySQLPacketBuffer.writeLengthCodedBinary(buffer, content.length);
			MySQLPacketBuffer.writeBytes(buffer, content);
			break;
		case Types.NULL://理论上不会出现
			return;
		default:
			throw new SQLException("Unsupported jdbc type for resultset reading:" + javaType);
		}
	}
	
	/**
	 * 读取Time类型
	 * @param buffer
	 * @return
	 */
	public static Time readTime(ComBuffer buffer){
		byte length = MySQLPacketBuffer.readByte(buffer); //length
		if (length == 0)
			return null;
		
		MySQLPacketBuffer.readByte(buffer); //neg
		MySQLPacketBuffer.readInt(buffer); //date. no used now
		int hour = MySQLPacketBuffer.readByte(buffer);
		int minute = MySQLPacketBuffer.readByte(buffer);
		int second = MySQLPacketBuffer.readByte(buffer);
		
		//TODO:这里可以优化，不用每次都创建新实例，而改为放在ThreadLocal中
		Calendar cal = Calendar.getInstance();
		cal.set(0, 0, 0, hour, minute, second);
		
		if (length == 12) {
			MySQLPacketBuffer.readInt(buffer);//currently unused
		}
		
		return new java.sql.Time(cal.getTimeInMillis());
	}
	
	/**
	 * 读取Date类型
	 * @param buffer
	 * @return
	 */
	public static Object readDateObject(ComBuffer buffer){
		byte length = MySQLPacketBuffer.readByte(buffer); // length
		if (length == 0)
			return null;
		
		//TODO:这里可以优化，不用每次都创建新实例，而改为放在ThreadLocal中
		Calendar cal = Calendar.getInstance();
		
		int year = MySQLPacketBuffer.readShort(buffer);
		byte month = (byte)(MySQLPacketBuffer.readByte(buffer)-1); //数据包中月份范围是1-12，而 java对象中月份是0-11
		byte date = MySQLPacketBuffer.readByte(buffer);
		
		if (length == 4) {
			cal.set(year, month, date);
			return new java.sql.Date(cal.getTimeInMillis());
		}
		
		int hour = MySQLPacketBuffer.readByte(buffer);
		int minute = MySQLPacketBuffer.readByte(buffer);
		int second = MySQLPacketBuffer.readByte(buffer);
		
		cal.set(year, month, date, hour, minute, second);
		
		if (length == 11) {
			/*
			 * The fractional part of the second in microseconds; currently unused
			 * --MySQL5.1 C API
			 */
			MySQLPacketBuffer.readInt(buffer);
		}
		return new java.sql.Timestamp(cal.getTimeInMillis());
	}
	
	/**
	 * 编码Time类型
	 * <pre>
	 * Type       Size        Comment
 	 * ----       ----        -------
	 * time       1 + 0-11    Length + sign (0 = pos, 1= neg), 4 byte days,
     *                        1 byte HHMMDD, 4 byte billionth of a second
	 * </pre>
	 * @param buffer
	 * @param tm
	 */
	public static void storeTime(ComBuffer buffer, Time tm){
		MySQLPacketBuffer.writeByte(buffer, (byte) 8); // length
		MySQLPacketBuffer.writeByte(buffer, (byte) 0); // neg flag
		MySQLPacketBuffer.writeInt(buffer, 0); // tm->day, not used

		Calendar cal = Calendar.getInstance();
		
		cal.setTime(tm);
		MySQLPacketBuffer.writeByte(buffer, (byte) cal.get(Calendar.HOUR_OF_DAY));
		MySQLPacketBuffer.writeByte(buffer, (byte) cal.get(Calendar.MINUTE));
		MySQLPacketBuffer.writeByte(buffer, (byte) cal.get(Calendar.SECOND));
		//4 byte billionth of a second，not used
	}
	
	/**
	 * 编码Date类型
	 * <pre>
	 * Type      Size        Comment
	 * ----      ----        -------
	 * date      1 + 0-11    Length + 2 byte year, 1 byte MMDDHHMMSS,
     *                       4 byte billionth of a second
	 * </pre>
	 * @param buffer
	 * @param date
	 */
	public static void storeSqlDate(ComBuffer buffer, java.sql.Date dateTime) {
		
		Calendar sessionCalendar = Calendar.getInstance();
		sessionCalendar.setTime(dateTime);
		
		sessionCalendar.set(Calendar.HOUR_OF_DAY, 0);
		sessionCalendar.set(Calendar.MINUTE, 0);
		sessionCalendar.set(Calendar.SECOND, 0);

		MySQLPacketBuffer.writeByte(buffer, (byte)7); // length

		int year = sessionCalendar.get(Calendar.YEAR);
		int month = sessionCalendar.get(Calendar.MONTH) + 1;
		int date = sessionCalendar.get(Calendar.DAY_OF_MONTH);
		
		MySQLPacketBuffer.writeShort(buffer, (short)year);
		MySQLPacketBuffer.writeByte(buffer, (byte) month);
		MySQLPacketBuffer.writeByte(buffer, (byte) date);

		MySQLPacketBuffer.writeByte(buffer, (byte) 0);
		MySQLPacketBuffer.writeByte(buffer, (byte) 0);
		MySQLPacketBuffer.writeByte(buffer, (byte) 0);
	}
	
	/**
	 * 编码Timestamp类型
	 * <pre>
	 * Type      Size        Comment
	 * ----      ----        -------
	 * date      1 + 0-11    Length + 2 byte year, 1 byte MMDDHHMMSS,
     *                       4 byte billionth of a second
	 * </pre>
	 * @param buffer
	 * @param timeStamp
	 */
	public static void storeSqlTimestamp(ComBuffer buffer, java.sql.Timestamp timeStamp) {
		Calendar sessionCalendar = Calendar.getInstance();
		sessionCalendar.setTime(timeStamp);
		
		MySQLPacketBuffer.writeByte(buffer, (byte)11); // length

		int year = sessionCalendar.get(Calendar.YEAR);
		int month = sessionCalendar.get(Calendar.MONTH) + 1;
		int date = sessionCalendar.get(Calendar.DAY_OF_MONTH);
		
		MySQLPacketBuffer.writeShort(buffer, (short)year);
		MySQLPacketBuffer.writeByte(buffer, (byte) month);
		MySQLPacketBuffer.writeByte(buffer, (byte) date);

		MySQLPacketBuffer.writeByte(buffer, (byte) sessionCalendar
				.get(Calendar.HOUR_OF_DAY));
		MySQLPacketBuffer.writeByte(buffer, (byte) sessionCalendar
				.get(Calendar.MINUTE));
		MySQLPacketBuffer.writeByte(buffer, (byte) sessionCalendar
				.get(Calendar.SECOND));
		
		MySQLPacketBuffer.writeInt(buffer, timeStamp.getNanos());
	}
	
	/**
	 * 读取BLOB类型数据
	 * @param buffer
	 * @return
	 */
	public static Blob readBlob(ComBuffer buffer) {
		long length = MySQLPacketBuffer.readLengthCodedBinary(buffer);
		if (length == MySQLPacketBuffer.NULL_LENGTH)
			return null;
		if (length > GlobalContext.getInstance().getConfig().getBlobLength())
			throw new IllegalArgumentException("Blob length is out of range");
		byte[] data = MySQLPacketBuffer.readBytes(buffer, length);
		return new com.netease.backend.db.sql.data.Blob(data);
	}
	
	public static byte[] readBlobInBytes(ComBuffer buffer) {
		long length = MySQLPacketBuffer.readLengthCodedBinary(buffer);
		if (length == MySQLPacketBuffer.NULL_LENGTH)
			return null;
		if (length > GlobalContext.getInstance().getConfig().getBlobLength())
			throw new IllegalArgumentException("Blob length is out of range");
		return MySQLPacketBuffer.readBytes(buffer, length);
	}
}
