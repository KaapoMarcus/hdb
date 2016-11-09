package com.netease.backend.db.common.cmd;

public interface CmdCode {

	public static final int	REQ_INVALID	= -1;
	public static final int	REQ_END		= Integer.MIN_VALUE;
	public static final int	REQ_CONN	= 0x100;

	public static final int	OK			= Integer.MIN_VALUE ^ Integer.MAX_VALUE;

}
