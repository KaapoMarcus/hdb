package com.netease.backend.db.common.cmd;


public interface DDBCode extends CmdCode {

	public static final int	REQ_CERT			= 0x101;
	public static final int	REQ_CERT_UPDATE		= 0x102;
	public static final int	REQ_ID_ASSIGN		= 0x103;
	public static final int	REQ_ASID			= 0x104;
	public static final int	REQ_ID_RELEASE		= 0x105;
	public static final int	REQ_SID_VALIDATE	= 0x106;
	public static final int	REQ_SID_ASSIGN		= 0x107;
	public static final int	REQ_VER				= 0x108;
	public static final int	REQ_CONTEXT_SYNC	= 0x109;
	public static final int	REQ_XA_BRANCH		= 0x10a;
	public static final int	REQ_XA_REC			= 0x10b;
	public static final int	REQ_STAT			= 0x10c;
	public static final int	REQ_PID				= 0x10d;
	public static final int	REQ_TIMESTAMP		= 0x10e;

	public static final int	ERR_UNKNOWN			= 0xff;
	public static final int	ERR_SYS_ERR			= 0x100;
	public static final int	ERR_STATE_ILLEGAL	= 0x101;
	public static final int	ERR_IO_ERR			= 0x102;
	public static final int	ERR_CONN_ERR		= 0x103;

	public static final int	ERR_ID_ASSIGN_ERR	= 0x201;
	public static final int	ERR_ID_RELEASE_ERR	= 0x202;

	public static final int	ERR_SIG_EXPIRED		= 0x210;
	public static final int	ERR_SIG_ASSIGN_ERR	= 0x211;

	public static final int	ERR_CERT_UPDATE_ERR	= 0x220;
	public static final int	ERR_CERT_ILLEGAL	= 0x221;

	public static final int	ERR_CONTEXT_INVALID	= 0x240;

	public static final int	ERR_PID_ASSIGN_ERR	= 0x250;
	
	

	public static final int	ERR_XAB_ERR			= 0x270;
	public static final int	ERR_XABR_ERR		= 0x271;

}
