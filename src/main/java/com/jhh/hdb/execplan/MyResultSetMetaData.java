package com.jhh.hdb.execplan;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.schema.ColumnInfo;
import com.netease.backend.db.common.utils.OneBasedArray;
import com.netease.backend.db.common.utils.SQLCommonUtils;
import com.netease.backend.db.result.ColumnMetadata;
import com.netease.backend.db.result.DBResultSetMetaData;
import com.netease.backend.db.result.dbengine.DataTypeConvert;
import com.netease.backend.db.result.memcached.ColumnTypeProducer;
import com.netease.backend.db.sql.Select;
import com.netease.backend.db.sql.expression.Expression;
import com.netease.backend.db.sql.expression.ExpressionColumn;
import com.netease.backend.db.table.Table;

/**
 * resultset metadata For common user, it is the same as other ResultSetMetaData
 * that JDBC driver implements in functionality, the difference is that we can
 * modify it(such as adding columns, delete columns) by ourselves
 * 
 * @author malong
 */
public class MyResultSetMetaData extends DBResultSetMetaData implements ResultSetMetaData, Serializable,
		Cloneable {
	private static final long serialVersionUID = -6974645790798023703L;

	private static Logger logger = Logger.getLogger(MyResultSetMetaData.class);

	private OneBasedArray<ColumnMetadata> columns;
	/**
	 * ��¼��Щ�б����Ǿۼ�������,Ϊ�˲�ѯ,������ת��,�����˾ۼ���������,��ֱ�Ӷ��н��в�ѯ��.
	 * ������index list,���ظ��û�֮ǰ����Ҫ���¼���ۼ�����ֵ�����Ͼۼ���������
	 */
	private List<Integer> convertedAggColumnIndex = new Vector<Integer>();

	private int aggDistinctIndex = -1;

	public List<Integer> getConvertedAggIndex() {
		return this.convertedAggColumnIndex;
	}

	public void setConvertedAggIndex(List<Integer> columnIndex) {
		this.convertedAggColumnIndex = columnIndex;
	}

	public MyResultSetMetaData() {
		columns = new OneBasedArray<ColumnMetadata>();
	}

	/**
	 * ����select�﷨������ɲ�ѯ����DBResultSetMetaData��OneBasedArray<
	 * ColumnMetadata> columns
	 * 
	 * @param select
	 * @throws SQLException
	 */
	public MyResultSetMetaData(Select select) throws SQLException {
		List<Expression> columnList = select.getColumns();
		int columnCount = columnList.size();
		columns = new OneBasedArray<ColumnMetadata>(columnCount);

		String columnName, tableName, schemaName;
		Integer columnType;
		for (int i = 0; i < columnCount; i++) {
			columnName = columnList.get(i).getColumnName();
			columnType = ColumnTypeProducer
					.produceColumnType(columnList.get(i));
			tableName = columnList.get(i).getTableName();
			Table table = columnList.get(i).getTable();
			schemaName = table == null ? "" : table.getDDBName();
			int displaySize;
			if (columnList.get(i) instanceof ExpressionColumn)
				displaySize = table.getColumn(columnName).getDisplaySize();
			else
				displaySize = ColumnInfo.getDefaultDisplaySize(columnType,
						select.getDbnType());

			columns.add(new ColumnMetadata(columnName, columnType, ColumnInfo
					.getTypeName(columnType, select.getDbnType()), tableName,
					schemaName, displaySize));
		}
	}

	public MyResultSetMetaData(ResultSetMetaData metadata, Select select) {
		try {
			int columnCount = metadata.getColumnCount();
			columns = new OneBasedArray<ColumnMetadata>(columnCount);

			for (int i = 1; i <= columnCount; i++) {
				String columnName, tableName, schemaName;
				int displaySize;
				columnName = metadata.getColumnLabel(i);
				tableName = metadata.getTableName(i);
				schemaName = metadata.getSchemaName(i);
				displaySize = metadata.getColumnDisplaySize(i);

				ColumnMetadata cmd = new ColumnMetadata(columnName,
						DataTypeConvert.convertType(metadata.getColumnType(i),
								DbnType.MySQL), metadata.getColumnTypeName(i),
						tableName, schemaName, displaySize);
				columns.add(cmd);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	public MyResultSetMetaData(MyResultSetMetaData md1, MyResultSetMetaData md2) {
		columns = new OneBasedArray<ColumnMetadata>();
		columns.addAll(md1.columns);
		columns.addAll(md2.columns);

	}

	public void addColumn(ColumnMetadata c) {
		columns.add(c);
	}

	public void addColumn(int cIndex, ColumnMetadata c) {
		columns.add(cIndex, c);
	}

	public OneBasedArray<ColumnMetadata> getColumns() {
		return columns;
	}

	public ColumnMetadata getColumn(int columnIndex) throws SQLException {
		checkIndex(columnIndex);
		return columns.get(columnIndex);
	}

	private void checkIndex(int index) throws SQLException {
		if (index <= 0 || index > columns.size()) {
			throw new SQLException("Invalid index: " + index
					+ " for ResultSetMetaData");
		}
	}

	public int getColumnCount() {
		return columns.size();
	}

	public String getTableName(int columnIndex) throws SQLException {
		checkIndex(columnIndex);
		return columns.get(columnIndex).getTableName();
	}

	public int getColumnType(int columnIndex) throws SQLException {
		checkIndex(columnIndex);
		return columns.get(columnIndex).getColumnType();
	}

	public String getColumnName(int columnIndex) throws SQLException {
		checkIndex(columnIndex);
		return columns.get(columnIndex).getColumnName();
	}

	public String getColumnTypeName(int column) throws SQLException {
		return columns.get(column).getColumnTypeName();
	}

	public int getColumnIndex(String columnName) throws SQLException {
		for (int i = 1; i <= columns.size(); i++) {
			if (columnName.equalsIgnoreCase(columns.get(i).getColumnName()))
				return i;
		}

		throw new SQLException("Can not find field name \"" + columnName + "\"");
	}

	public int getFunctionColumnIndex(String aggColumn) throws SQLException {
		for (int i = 1; i <= columns.size(); i++) {
			if (aggColumn.equalsIgnoreCase(columns.get(i).getColumnName()))
				return i;
		}

		// ���ֱ��ͨ��aggColumn��û�ҵ�, ����Sum(S.Sno)û��, ��ô����������ִ��Join
		// selectʱ�Ѿ���
		// �����ת������(ֻ����NestJoin�вŻ���ת��)
		// ��鱻ת�����к�S.Sno�Ƿ���ͬ, �����ͬ, ���ر�ת������index
		for (int i = 0; i < this.convertedAggColumnIndex.size(); i++) {
			int index = this.convertedAggColumnIndex.get(i);

			String colName = columns.get(index).getColumnName();
			String tableName = columns.get(index).getTableName();
			String str2 = aggColumn.substring(aggColumn.indexOf("(") + 1,
					aggColumn.indexOf(")")).trim();
			if (str2.equalsIgnoreCase(colName)
					|| str2.equalsIgnoreCase(tableName + "." + colName))
				return index;

		}
		throw new SQLException("Can not find field name \"" + aggColumn + "\"");
	}

	public int getColumnIndex(String tableName, String columnName)
			throws SQLException {
		for (int i = 1; i <= columns.size(); i++) {
			ColumnMetadata column = columns.get(i);
			if (columnName.equalsIgnoreCase(column.getColumnName())
					&& tableName.equals(column.getTableName())) {
				return i;
			}
		}

		throw new SQLException("Can not find field name \"" + tableName + "."
				+ columnName + "\"");
	}

	/**
	 * ����ƥ��ת�������
	 * 
	 * @param tableName
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public int getColumnIndexWithAgg(String tableName, String columnName)
			throws SQLException {
		for (int i = 0; i < this.convertedAggColumnIndex.size(); i++) {
			int index = this.convertedAggColumnIndex.get(i);

			ColumnMetadata column = columns.get(index);
			if (columnName.equalsIgnoreCase(column.getColumnName())
					&& tableName.equals(column.getTableName())) {
				return index;
			}
		}

		return getColumnIndex(tableName, columnName);
	}

	protected void addColumn(String columnName, int columnType,
			String columnTypeName, String tableName, String schemaName,
			int displaySize) {
		ColumnMetadata c = new ColumnMetadata(columnName, columnType,
				columnTypeName, tableName, schemaName, displaySize);
		addColumn(c);
	}

	// ignored methods
	public boolean isAutoIncrement(int arg0) throws SQLException {
		return false;
	}

	public boolean isCaseSensitive(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public boolean isSearchable(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public boolean isCurrency(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public int isNullable(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public boolean isSigned(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public int getColumnDisplaySize(int column) throws SQLException {
		return columns.get(column).getDisplaySize();
	}

	public String getColumnLabel(int column) throws SQLException {
		return columns.get(column).getColumnName();
	}

	public String getSchemaName(int arg0) throws SQLException {
		checkIndex(arg0);
		return columns.get(arg0).getSchemaName();
	}

	public int getPrecision(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public int getScale(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public String getCatalogName(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public boolean isReadOnly(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public boolean isWritable(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public boolean isDefinitelyWritable(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public String getColumnClassName(int arg0) throws SQLException { // add by
																		// WangLei
																		// 2008.6.6
		if (arg0 <= 0 || arg0 > columns.size())
			throw new SQLException("Invalid index: " + arg0
					+ " for ResultSetMetaData");
		ColumnMetadata column = columns.get(arg0);
		return SQLCommonUtils.getColumnClassNameForType(column.getColumnType());
	}

	public int getDistinctAggIndex() {
		return aggDistinctIndex;
	}

	public void setDistinctAggIndex(int distinctAggIndex) {
		this.aggDistinctIndex = distinctAggIndex;
	}

	public void removeColumn(int i) throws SQLException {
		/*
		 * if(aggDistinctIndex >= i){ throw new
		 * SQLException("can not remove column less than convertedAggColumnIndex"
		 * ); } for(Integer index : convertedAggColumnIndex){ if(index >= i){
		 * throw new
		 * SQLException("can not remove column less than convertedAggColumnIndex"
		 * ); } }
		 */columns.remove(i);
	}

	/**
	 * Deep Clone added by qiusf
	 */
	public Object clone() {
		try {
			MyResultSetMetaData cloned = (MyResultSetMetaData) super.clone();
			cloned.columns = new OneBasedArray<ColumnMetadata>();
			for (int i = 1; i <= columns.size(); i++) {
				cloned.columns.add(columns.get(i));
			}
			cloned.convertedAggColumnIndex = new Vector<Integer>();
			for (Integer index : convertedAggColumnIndex) {
				cloned.convertedAggColumnIndex.add(index);
			}
			return cloned;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
