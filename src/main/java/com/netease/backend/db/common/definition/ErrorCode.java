package com.netease.backend.db.common.definition;


public class ErrorCode {
	
	public static final int ER_TRIGGER_DOES_NOT_EXIST = 1;

	
	public static final int ER_PROCEDURE_DOES_NOT_EXIST = 2;

	
	public static final int ER_TAB_VIEW_NOT_EXIST = 3;

	
	public static final int ER_CLUSTER_NOT_EXIST = 4;

	
	public static final int ER_INDEX_NOT_EXIST = 5;

	
	public static final int ER_PASSWORD_WRONG = 6;
	
	
	public static final int ER_DUPLICATE_KEY = 7;
	
	
	public static final int ER_DEAD_LOCK = 8;
	
	
	public static final int ER_LOCK_WAIT_TIMEOUT = 9;
	

	
	public static int[] getErrCodes(DbnType dbnType, int error) {
		if (dbnType == DbnType.MySQL)
			return MySQLErrorCode.getErrCodes(error);
		else if (dbnType == DbnType.Oracle)
			return OracleErrorCode.getErrCodes(error);
		else
			throw new IllegalArgumentException(
					"can not get error code, error dbn type:" + dbnType);
	}
	
	
	public static boolean contains(DbnType dbnType, int error, int expectError){
		int[] codes = getErrCodes(dbnType, error);
		for(int i=0; i<codes.length; i++){
			if(codes[i] == expectError)
				return true;
		}
		return false;
	}
}


class MySQLErrorCode {
	
	static final int MYSQL_TRG_DOES_NOT_EXIST = 1360;

	
	static final int MYSQL_PRO_DOES_NOT_EXITST = 1305;
	
	
	static final int MYSQL_TAB_VIEW_UNKNOWN = 1051;

	
	static final int MYSQL_TAB_VIEW_DOES_NOT_EXIST = 1146;

	
	static final int MYSQL_INDEX_DOES_NOT_EXIST = 1061;

	
	static final int MYSQL_PASSWORD_WRONG = 1045;

	
	static final int MYSQL_DUPLICATE_KEY = 1062;

	
	public static int[] getErrCodes(int error) {
		switch (error) {
		case ErrorCode.ER_TRIGGER_DOES_NOT_EXIST:
			return new int[] { MYSQL_TRG_DOES_NOT_EXIST };
		case ErrorCode.ER_PROCEDURE_DOES_NOT_EXIST:
			return new int[] { MYSQL_PRO_DOES_NOT_EXITST };
		case ErrorCode.ER_TAB_VIEW_NOT_EXIST:
			return new int[] { MySQLErrorCode.MYSQL_TAB_VIEW_UNKNOWN, MySQLErrorCode.MYSQL_TAB_VIEW_DOES_NOT_EXIST };
		case ErrorCode.ER_INDEX_NOT_EXIST:
			return new int[] { MySQLErrorCode.MYSQL_INDEX_DOES_NOT_EXIST };
		case ErrorCode.ER_PASSWORD_WRONG:
			return new int[] { MySQLErrorCode.MYSQL_PASSWORD_WRONG };
		case ErrorCode.ER_DUPLICATE_KEY:
			return new int[] { MySQLErrorCode.MYSQL_DUPLICATE_KEY };
		default:
			throw new IllegalArgumentException("can not find mysql error code:"
					+ error);
		}
	}

}


class OracleErrorCode {
	
	static final int ORACLE_TRG_DOES_NOT_EXIST = 4080;

	
	static final int ORACLE_OBJECT_DOES_NOT_EXITST = 4043;

	
	static final int ORACLE_TAB_VIEW_DOES_NOT_EXIST = 942;

	
	static final int ORACLE_CLUSTER_NOT_EXIST = 943;

	
	static final int ORACLE_INDEX_DOES_NOT_EXIST = 942;

	
	static final int ORACLE_PASSWORD_WRONG = 1017;

	
	static final int ORACLE_DUPLICATE_KEY = 1;
	
	
	static final int ORACLE_DEAD_LOCK = 60;
	
	
	static final int ORACLE_LOCK_WAIT_TIMEOUT = 30006;

	
	public static int[] getErrCodes(int error) {
		switch (error) {
		case ErrorCode.ER_TRIGGER_DOES_NOT_EXIST:
			return new int[] { ORACLE_TRG_DOES_NOT_EXIST };
		case ErrorCode.ER_PROCEDURE_DOES_NOT_EXIST:
			return new int[] { ORACLE_OBJECT_DOES_NOT_EXITST };
		case ErrorCode.ER_TAB_VIEW_NOT_EXIST:
			return new int[] { ORACLE_TAB_VIEW_DOES_NOT_EXIST };
		case ErrorCode.ER_INDEX_NOT_EXIST:
			return new int[] { ORACLE_INDEX_DOES_NOT_EXIST };
		case ErrorCode.ER_CLUSTER_NOT_EXIST:
			return new int[] { ORACLE_CLUSTER_NOT_EXIST };
		case ErrorCode.ER_PASSWORD_WRONG:
			return new int[] { ORACLE_PASSWORD_WRONG };
		case ErrorCode.ER_DUPLICATE_KEY:
			return new int[] { ORACLE_DUPLICATE_KEY };
		case ErrorCode.ER_DEAD_LOCK:
			return new int[] { ORACLE_DEAD_LOCK };
		case ErrorCode.ER_LOCK_WAIT_TIMEOUT:
			return new int[] { ORACLE_LOCK_WAIT_TIMEOUT };
		default:
			throw new IllegalArgumentException(
					"can not find oracle error code:" + error);
		}
	}

}
