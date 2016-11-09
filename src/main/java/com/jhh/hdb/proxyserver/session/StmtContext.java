package com.jhh.hdb.proxyserver.session;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.netease.backend.db.DBPreparedStatement;
import com.netease.backend.db.DBResultSet;
import com.netease.backend.db.common.definition.Definition;

public class StmtContext {
	/**配置管理日志*/
	private static Logger logger = Logger
			.getLogger(Definition.LOGGER_QUERY_SERVER);

	private long statementId;

	//存放COM_LONG_DATA引发的异常信息
	private String errorMsg = null;

	//参数信息
	private List<Short> paramTypes;

	//long data
	private StmtLongData[] longDataParams;

	private DBPreparedStatement pstmt;

	private DBResultSet resultset;

	public StmtContext(long statementId, int parameterCount,
			DBPreparedStatement pstmt) {
		this.statementId = statementId;
		this.longDataParams = new StmtLongData[parameterCount];
		this.pstmt = pstmt;
	}

	//清空缓存的long data
	public void resetLongData() {
		for (int i = 0; i < longDataParams.length; i++) {
			longDataParams[i] = null;
		}
		this.errorMsg = null;
	}

	public long getStatementId() {
		return statementId;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<Short> getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(List<Short> paramTypes) {
		this.paramTypes = paramTypes;
	}

	public StmtLongData[] getLongDataParams() {
		return longDataParams;
	}

	public void setLongDataParams(StmtLongData[] longDataParams) {
		this.longDataParams = longDataParams;
	}

	public int getParameterCount() {
		return longDataParams.length;
	}

	public DBPreparedStatement getPstmt() {
		return pstmt;
	}

	public void setPstmt(DBPreparedStatement pstmt) {
		this.pstmt = pstmt;
	}

	public DBResultSet getResultset() {
		return resultset;
	}

	public void setResultset(DBResultSet resultset) {
		this.resultset = resultset;
	}

	public void closeResultset() {
		if (resultset != null && !resultset.isClosed()) {
			try {
				resultset.close();
				resultset = null;
			} catch (SQLException e) {
				logger.error("error", e);
			}
		}

	}

	public void closePstmt() {
		try {
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			logger.error("error", e);
		}
	}
}