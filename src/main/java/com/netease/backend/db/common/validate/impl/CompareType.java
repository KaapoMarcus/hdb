package com.netease.backend.db.common.validate.impl;


public class CompareType {
	public static final int	STRICT						= 0x00;
	public static final int	BASE_TYPE					= 0x01;
	public static final int	COLUMN_NAME_ONLY			= 0x02;
	public static final int	INTERSECT_STRICT			= 0x10;
	public static final int	INTERSECT_BASE_TYPE			= 0x11;
	public static final int	INTERSECT_COLUMN_NAME_ONLY	= 0x12;

	public static boolean isIntersectType(int type) {
		return (type & 0xf0) == 0x10;
	}

	public static int getBaseOption(int type) {
		return type & 0xf;
	}
}
