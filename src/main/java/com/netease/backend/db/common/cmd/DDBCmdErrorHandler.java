package com.netease.backend.db.common.cmd;

import java.io.IOException;


public class DDBCmdErrorHandler {

	protected static DDBCmdException newError(
		CmdConnection conn, DDBError err)
		throws DDBCmdException, IOException
	{
		return newError(conn, err, null, null);
	}

	protected static DDBCmdException newError(
		CmdConnection conn, DDBError err, String more)
		throws DDBCmdException, IOException
	{
		return newError(conn, err, more, null);
	}

	protected static DDBCmdException newError(
		CmdConnection conn, DDBError err, Throwable cause)
		throws DDBCmdException, IOException
	{
		return newError(conn, err, null, cause);
	}

	protected static DDBCmdException newError(
		CmdConnection conn, DDBError err, String info, Throwable cause)
		throws DDBCmdException, IOException
	{
		conn.err(err.code, info); 
		return new DDBCmdException(err.code, info, cause);
	}

	protected static String genErrLog(
		DDBCmdException err, int ASID, DDBCmd req)
	{
		return genErrLog(DDBError.valueOf(err.getCode()), err.getMessage(),
				ASID, req);
	}

	protected static String genErrLog(
		DDBError err, int ASID, DDBCmd req)
	{
		return genErrLog(err, null, ASID, req);
	}

	protected static String genErrLog(
		DDBError err, String info, int ASID, DDBCmd req)
	{
		return genErrLog(err.code, (info != null) ? (err.info + info) : info,
				ASID, req.code, req.info);
	}

	protected static String genErrLog(
		int errCode, String errInfo, int ASID, int reqCode, String req)
	{
		final StringBuilder ss = new StringBuilder();
		ss.append("ERR<").append(errCode).append("> on REQ<").append(reqCode)
				.append("> of AS<").append(ASID);
		if ((errInfo != null) && (errInfo.length() > 0)) {
			ss.append("> : ").append(errInfo);
		} else {
			ss.append('>');
		}
		return ss.toString();
	}

}
