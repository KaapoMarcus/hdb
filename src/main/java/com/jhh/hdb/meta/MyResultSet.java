
package com.jhh.hdb.meta;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.netease.backend.db.DBStatement;
import com.netease.backend.db.common.utils.OneBasedArray;
import com.netease.backend.db.exec.stat.StmtStatCollector;
import com.netease.backend.db.plan.performer.QueryPlanPerformer;
import com.netease.backend.db.result.DBResultSetMetaData;
import com.netease.backend.db.result.Record;
/**
 * 
 */
public class MyResultSet implements ResultSet, Serializable {
	private static final long serialVersionUID = 5945763063223081406L;

	// resultset metadata
	private ResultSetMetaData metadata;
    // records
	private OneBasedArray<Record> records;
	// Initially the cursor is positioned before the first row
	private int cursor = 0;
	
	private boolean isClosed = false;
	
	private transient Statement stmt;
    
    private int fetchSize = 0;
    
    private static int DEFAULT_FETCH_SIZE = 5;
    
    private boolean usePipeline = false;
    
    private ResultSetOperator resultSetOperator;
    
    public ResultSetOperator getResultSetOperator() {
		return resultSetOperator;
	}

	public void setResultSetOperator(ResultSetOperator resultSetOperator) {
		this.resultSetOperator = resultSetOperator;
	}

	private boolean isGeneratedKeys = false;
    
    private boolean isEmpty = false;
    
    private boolean hasMoreRecord = true;
    
    private int lastVisitedColumnIndex = 0;
	
    private boolean firstTime = true;
    
    
    
	public MyResultSet() {
	
	}
	
	public void init(ResultSetMetaData metadata) {
		this.metadata = metadata;
        records = new OneBasedArray<Record>();
	}
	
    public void setStatement(Statement stmt) throws SQLException {
        checkAvailable();
        this.stmt = stmt;
    }
    
    public Statement getStatement() throws SQLException  {
        checkAvailable();
        return this.stmt;
    }
    
    private void checkAvailable() throws SQLException {
        if (isClosed) {
            throw new SQLException("ResultSet has already closed!");
        }
    }
    
    private void cursorValidation() throws SQLException {
        if (isEmpty()) 
            throw new SQLException("Invalid cursor position!");
        
        if (cursor < 1 || cursor > records.size())
            throw new SQLException("Invalid cursor position!");
    }
    
    private void columnIndexValidation(int index) throws SQLException {
        if (index < 1 || index > this.metadata.getColumnCount())
            throw new SQLException("Invalid column index!");
    }
    
	public int resultCount() throws SQLException {
        checkAvailable();
		return records.size();
	}
	
	public ResultSetMetaData getMetaData() throws SQLException {
        checkAvailable();
		return this.metadata;
	}
	
	public void addRecord(Record rec) throws SQLException {
        checkAvailable();
//		rec.setMetaData(this.metadata);
		this.records.add(rec);
	}
    
    public void addRecordList(List<Record> record) throws SQLException {
        checkAvailable();
        this.records.addAll(record);
    }
    
    public OneBasedArray<Record> getAllRecord() throws SQLException {
        checkAvailable();
        return records;
    }
	
    public void clearRecords() throws SQLException {
        this.records.clear();
    }
    
	public Record getCurrentRecord() throws SQLException {
        checkAvailable();
		cursorValidation();

        return (Record) records.get(cursor);
	}
	
	public boolean isEmpty() throws SQLException {
        checkAvailable();

		return this.isEmpty;
	}
	
	public void setHasMoreResults(boolean hasmore) {
	    this.hasMoreRecord = hasmore;
	}
	
	public void setGenerateKeys(boolean flag) {
	    this.isGeneratedKeys = flag;
	}

	public boolean next() throws SQLException {
        checkAvailable();
         
        if (this.isEmpty()) {
            return false;
        }

        if (firstTime) {
        	
            Record rec = null;
            while ((rec = resultSetOperator.getNextTuple()) != null) {
                this.records.add(rec);
            }
            firstTime = false;
 
        }
        
        this.cursor++;
        if (this.cursor <= records.size())
            return true;
		return false;
	}

	public String getString(int column) throws SQLException {
        checkAvailable();
		cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;
        
        Record rec = (Record) records.get(this.cursor);
		return rec.getString(column);
	}

	public boolean getBoolean(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

        Record rec = (Record) records.get(this.cursor);
		return rec.getBoolean(column);
	}

	public byte getByte(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

		Record rec = (Record) records.get(this.cursor);
		return rec.getByte(column);
	}

	public short getShort(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

        Record rec = (Record) records.get(this.cursor);
		return rec.getShort(column);
	}

	public int getInt(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

        Record rec = (Record) records.get(this.cursor);
		return rec.getInt(column);
	}

	public long getLong(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

        Record rec = (Record) records.get(this.cursor);
		return rec.getLong(column);
	}

	public float getFloat(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

		Record rec = (Record) records.get(this.cursor);
		return rec.getFloat(column);
	}

	public double getDouble(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

        Record rec = (Record) records.get(this.cursor);
		return rec.getDouble(column);
	}

    public BigDecimal getBigDecimal(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

        Record rec = (Record) records.get(this.cursor);
        return rec.getBigDecimal(column);
    }

    public Date getDate(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;
        
        Record rec = (Record) records.get(this.cursor);
        return rec.getDate(column);
    }

    public Time getTime(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;
        
        Record rec = (Record) records.get(this.cursor);
        return rec.getTime(column);
    }
    
    public Timestamp getTimestamp(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;
        
        Record rec = (Record) records.get(this.cursor);
        return rec.getTimestamp(column);
    }
    
    public Object getObject(int column) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

        Record rec = (Record) records.get(this.cursor);
        return rec.getObject(column);
    }

	public String getString(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
		return rec.getString(column);
	}

	public boolean getBoolean(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
		return rec.getBoolean(column);
	}

	public byte getByte(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getByte(column);
	}

	public short getShort(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getShort(column);
	}

	public int getInt(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getInt(column);
	}

	public long getLong(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getLong(column);
	}

	public float getFloat(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getFloat(column);
	}

	public double getDouble(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getDouble(column);
	}
	
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getBigDecimal(column);
	}

	public Date getDate(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getDate(column);
	}
	
	public Timestamp getTimestamp(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getTimestamp(column);
	}
	
	public Time getTime(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getTime(column);
	}

	public Object getObject(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getObject(column);
	}

    public byte[] getBytes(int column) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        this.lastVisitedColumnIndex = column;
        return rec.getBytes(column);
    }
    
    public Blob getBlob(int column) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        this.lastVisitedColumnIndex = column;
        return rec.getBlob(column);
    }

    public byte[] getBytes(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getBytes(column);
    }
    
    public Blob getBlob(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getBlob(column);
    }
    

    public InputStream getBinaryStream(int column) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        this.lastVisitedColumnIndex = column;
        return new ByteArrayInputStream(rec.getBytes(column));
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return new ByteArrayInputStream(rec.getBytes(column));
    }
    
	public int findColumn(String columnName) throws SQLException {
        checkAvailable();

		return ((DBResultSetMetaData)getMetaData()).getColumnIndex(columnName);
	}

	public boolean isBeforeFirst() throws SQLException {
        checkAvailable();
		return cursor == 0;
	}

	public boolean isAfterLast() throws SQLException {
//        checkAvailable();
//		return cursor == records.size();
		
		throw new SQLException("Not supported function!");
	}

	public boolean isFirst() throws SQLException {
        checkAvailable();
		return cursor == 1;
	}

	public boolean isLast() throws SQLException {
//        checkAvailable();
//		return cursor == (records.size() - 1);
		throw new SQLException("Not supported function!");
	}

	public void beforeFirst() throws SQLException {
        checkAvailable();
		cursor = 0;
	}

	public void afterLast() throws SQLException {
//        checkAvailable();
//		cursor = records.size();
		throw new SQLException("Not supported function!");
	}

	public boolean first() throws SQLException {
        checkAvailable();

        if (records.size() > 0) {
			cursor = 1;
			return true;
		}
		cursor = 0;
		return false;
	}

	public boolean last() throws SQLException {
//        checkAvailable();
//
//		if ((records.size() - 1) > 0) {
//			cursor = (records.size() - 1);
//			return true;
//		}
//		cursor = 0;
//		return false;
		throw new SQLException("Not supported function!");
	}

	public int getRow() throws SQLException {
        checkAvailable();

		return cursor;
	}

    public void close() throws SQLException {
        this.metadata = null;
        this.records = null;
        this.cursor = 0;
        
/*        if (this.stmt instanceof DBStatement) {
            if (queryPlanPerformer != null) {
                queryPlanPerformer.close();
            }
        }
     
        if (this.stmt instanceof DBPreparedStatement) {
            if (queryPlanPerformer != null) {
                queryPlanPerformer.releaseResource();
            }
        }*/
//
//        queryPlanPerformer = null;
        
/*        if (ssObj != null) {
        	ssObj.getStmtStat().setDbnCount(ssObj.getDbnSet().size());
        	StmtStatCollector.addDDBStmtStat(ssObj.getStmtStat());
        	ssObj = null;
        }
        */
        this.isClosed = true;
    }

    public boolean wasNull() throws SQLException {
        checkAvailable();

        if (lastVisitedColumnIndex == 0) {
            throw new SQLException("No visited column!");
        }
        
        Record rec = (Record) records.get(this.cursor);
        return rec.wasNull(lastVisitedColumnIndex);
    }
    
	/* (non-Javadoc)
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		throw new SQLException("Not supported function!");
	}

    /* (non-Javadoc)
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	public Array getArray(String arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public Clob getClob(int column) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        this.lastVisitedColumnIndex = column;
        return rec.getClob(column);

	}

	public Clob getClob(String columnName) throws SQLException {
        checkAvailable();
        cursorValidation();

        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getClob(column);
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		return ResultSet.CONCUR_READ_ONLY;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public int getFetchSize() throws SQLException {
        return this.fetchSize;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int arg0, Map<String, Class<?>> arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String arg0, Map<String, Class<?>> arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public Date getDate(int column, Calendar cal) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

		// TODO Auto-generated method stub
		Record rec = (Record) records.get(this.cursor);
		return rec.getDate(column);
	}

	public Date getDate(String columnName, Calendar cal) throws SQLException {
        checkAvailable();
        cursorValidation();

		// TODO Auto-generated method stub
        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getDate(column);
	}

	public Time getTime(int column, Calendar cal) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

		// TODO Auto-generated method stub
		Record rec = (Record) records.get(this.cursor);
		return rec.getTime(column);
	}

	public Time getTime(String columnName, Calendar cal) throws SQLException {
        checkAvailable();
        cursorValidation();

		// TODO Auto-generated method stub
        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getTime(column);
	}

	public Timestamp getTimestamp(int column, Calendar cal) throws SQLException {
        checkAvailable();
        cursorValidation();
        columnIndexValidation(column);
        this.lastVisitedColumnIndex = column;

		// TODO Auto-generated method stub
		Record rec = (Record) records.get(this.cursor);
		return rec.getTimestamp(column);
	}

	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        checkAvailable();
        cursorValidation();

		// TODO Auto-generated method stub
        Record rec = (Record) records.get(this.cursor);
        int column = rec.getIndexByColumnName(columnName);
        this.lastVisitedColumnIndex = column;
        return rec.getTimestamp(column);
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {
		return ResultSet.FETCH_FORWARD;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	public void setFetchSize(int fetchSize) throws SQLException {
        this.fetchSize = fetchSize;
        if (this.fetchSize > 0) {
            this.usePipeline = true;
        }
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int arg0, Array arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String arg0, Array arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String arg0, InputStream arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String arg0, BigDecimal arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(String arg0, InputStream arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int arg0, Blob arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String arg0, Blob arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int arg0, boolean arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String arg0, boolean arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int arg0, byte arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String arg0, byte arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int arg0, byte[] arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String arg0, byte[] arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(String arg0, Reader arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int arg0, Clob arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String arg0, Clob arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int arg0, Date arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String arg0, Date arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int arg0, double arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String arg0, double arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int arg0, float arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String arg0, float arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int arg0, int arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String arg0, int arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int arg0, long arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String arg0, long arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String arg0) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int arg0, Object arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int arg0, Object arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String arg0, Object arg1, int arg2) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String arg0, Object arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int arg0, Ref arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String arg0, Ref arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int arg0, short arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String arg0, short arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int arg0, String arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String arg0, String arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int arg0, Time arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String arg0, Time arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String arg0, Timestamp arg1) throws SQLException {
		throw new SQLException("Not supported function!");
	}


	public boolean isClosed() {
		return isClosed;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public RowId getRowId(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public NClob getNClob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public String getNString(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	
}
