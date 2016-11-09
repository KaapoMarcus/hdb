package com.netease.backend.db.common.cmd;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum DDBCmd implements DDBCode {
	End(REQ_END, null),
	Conn(REQ_CONN, null),
	Cert(REQ_CERT, null),
	CertUpdate(REQ_CERT_UPDATE, null),
	IDAssign(REQ_ID_ASSIGN, null),
	IDRelease(REQ_ID_RELEASE, null),
	SessionCreate(REQ_SID_ASSIGN, null),
	SessionValidate(REQ_SID_VALIDATE, null),
	VerCheck(REQ_VER, null),
	ContextSync(REQ_CONTEXT_SYNC, null),
	XABranch(REQ_XA_BRANCH, null),
	XARec(REQ_XA_REC, null),
	Stat(REQ_STAT, null),
	PIDReq(REQ_PID, null),
	TimeStampSync(REQ_TIMESTAMP, null), ;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public final int	code;
	public final String	info;

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	DDBCmd(
		int code,
		String info)
	{
		this.code = code;
		this.info = info;
	}

	
	public static final DDBCmd valueOf(
		final int code)
	{
		return CACHE.get(code);
	}

	private static final Map<Integer, DDBCmd>	CACHE;

	static {
		final DDBCmd[] values = values();
		final Map<Integer, DDBCmd> map = new HashMap<Integer, DDBCmd>(
				values.length);
		for (final DDBCmd instance : values) {
			map.put(instance.code, instance);
		}
		CACHE = Collections.unmodifiableMap(map);
	}
}
