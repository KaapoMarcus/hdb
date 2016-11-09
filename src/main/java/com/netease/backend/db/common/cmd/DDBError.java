package com.netease.backend.db.common.cmd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum DDBError implements DDBCode {

	
	UnknownErr(ERR_UNKNOWN, "δ֪����"),
	SysErr(ERR_SYS_ERR, "ϵͳ�ڲ�����"),
	StateIllegal(ERR_STATE_ILLEGAL, "�Ƿ�״̬��"),
	IOErr(ERR_IO_ERR, "I/O����"),
	ConnErr(ERR_CONN_ERR, "���Ӵ���"),
	
	IDAssignErr(ERR_ID_ASSIGN_ERR, "DBI��ID�������֤ʧ�ܣ�"),
	IDReleaseErr(ERR_ID_RELEASE_ERR, "DBI��ID�ͷ�ʧ�ܣ�"),
	
	SidAssignErr(ERR_SIG_ASSIGN_ERR, "Session����IDʧ�ܣ�"),
	SessionValidateErr(ERR_SIG_EXPIRED, "Session��֤ʧ�ܣ�"),
	
	CredUpdateErr(ERR_CERT_UPDATE_ERR, "�û��������ʧ�ܣ�"),
	CredIllegal(ERR_CERT_ILLEGAL, "�û�����������֤ʧ�ܣ�"),
	
	ContextErr(ERR_CONTEXT_INVALID, "��ȡDDB������ʧ�ܣ�"),
	
	PIDAssignErr(ERR_PID_ASSIGN_ERR, "������ID����ʧ�ܣ�"),
	
	XABErr(ERR_XAB_ERR, "����XA�����֧����ʧ�ܣ�"), 
	XABRErr(ERR_XABR_ERR, "��������ָ�ʧ�ܣ�"), ;
	;
	public final int	code;
	public final String	info;

	DDBError(
		int code,
		String info)
	{
		this.code = code;
		this.info = info;
	}

	
	public static final DDBError valueOf(
		final int code)
	{
		final DDBError err = CACHE.get(code);
		return (err != null) ? err : UnknownErr;
	}

	private static final Map<Integer, DDBError>	CACHE;

	static {
		final DDBError[] values = values();
		CACHE = new HashMap<Integer, DDBError>(values.length);
		for (final DDBError instance : values) {
			CACHE.put(instance.code, instance);
		}
	}

	
	private String								s	= null;

	@Override
	public String toString() {
		if (s == null) {
			s = "ERR<" + code + ">: " + info;
		}
		return s;
	}

	public static void main(
		String[] args)
	{
		System.out.println(Arrays.asList(DDBError.values()));
	}

}
