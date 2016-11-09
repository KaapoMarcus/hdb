package com.netease.backend.db.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.netease.backend.db.common.exceptions.DBSQLException;

public class Message {

    private static final Properties MESSAGES = new Properties();

    static {
        try {
            InputStream is = Message.class.getResourceAsStream("/com/netease/backend/db/common/utils/error_message");
            MESSAGES.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static DBSQLException getSQLException(int errorcode, char c, String sql) {
    	String message = MESSAGES.getProperty(errorcode + "");
    	message = "SQL: \"" + sql + "\", " + "char '" + c + "' , " + message;
    	return new DBSQLException(errorcode, message);
    }

    public static DBSQLException getSQLException(int errorcode, String token, String sql) {
    	String message = MESSAGES.getProperty(errorcode + "");
    	message = "SQL: \"" + sql + "\", " + "Token '" + token + "' , " + message;
    	return new DBSQLException(errorcode, message);
    }
    
    public static DBSQLException getSQLException(int errorcode, int position, String sql) {
    	String message = MESSAGES.getProperty(errorcode + "");
    	message = "SQL: \"" + sql + "\", " + "Index at " + position + ", " + message;
    	return new DBSQLException(errorcode, message);
    }
    
    public static DBSQLException getSQLException(int errorcode, int position, char c, String sql) {
    	String message = MESSAGES.getProperty(errorcode + "");
    	message = "SQL: \"" + sql + "\", " + "char '" + c + "' at index " + position + ", " + message;
    	return new DBSQLException(errorcode, message);
    }
    
    public static DBSQLException getSQLExcetpion(int errorcode, int position, String token, String sql) {
    	String message = MESSAGES.getProperty(errorcode + "");
    	message = "SQL: \"" + sql + "\", " + "token '" + token + "' at index " + position + ", " + message;
    	return new DBSQLException(errorcode, message);
    }
    
    public static DBSQLException getSQLException(int errorcode, String token, String expected, String sql) {
    	String message = MESSAGES.getProperty(errorcode + "");
    	message = "SQL: \"" + sql + "\", " + "'" + expected + "' expected instead of token '" + token + "', " + message;
    	return new DBSQLException(errorcode, message);
    }
    
    public static DBSQLException getSQLException(int errorcode, String token) {
        switch (errorcode) {
        case E_COLUMN_NOT_FOUND:
        case T_COLUMN_NOT_EXIST:
            return new DBSQLException(errorcode, "Column \'" + token + "\' not exist.");
        case T_TABLE_NOT_EXIST:
            return new DBSQLException(errorcode, "Table \'" + token + "\' not exist.");
        case R_ROUTINE_NOT_EXIST:
        	return new DBSQLException(errorcode, "Routine \'" + token + "\' not exist.");
        case C_DDB_NOT_FOUND:
        	return new DBSQLException(errorcode, "DDB \'" + token +"\' not exist.");
        default:
            String message = MESSAGES.getProperty(errorcode + "");
            message = message + ", \'" + token + "\'.";
            return new DBSQLException(errorcode, message);
        }
    }
    
    public static DBSQLException getSQLException(int errorcode) {
        String message = MESSAGES.getProperty(errorcode + "");
        return new DBSQLException(errorcode, message);
    }
    
    
    public static final int C_SQL_PARSE_ERROR = 11001;
    public static final int C_UNSUPPORTED_SQL_CHAR = 11002;
    public static final int C_ILLEGAL_SQL = 11003;
    public static final int C_ILLEGAL_SPECIAL_CHAR = 11004;
    public static final int C_EMPTY_TOKEN = 11005;
    public static final int C_ILLEGAL_DECIMAL = 11006;
    public static final int C_ILLEGAL_CHAR_LENGTH = 11007;
    public static final int C_ILLEGAL_T0KEN_TYPE = 11008;
    public static final int C_ILLEGAL_SELECT_SQL = 11009;
    public static final int C_ILLEGAL_IDENTIFIER = 11010;
    public static final int C_COLUMN_COUNT_DOES_NOT_MATCH = 11011;
    public static final int C_TALBE_NOT_FOUND = 11012;
    public static final int C_DISTINCT_NOT_ON_COLUMN = 11013;
    public static final int C_AGG_NOT_ON_COLUMN = 11014;
    public static final int C_MORE_THAN_ONE_DISTINCT = 11015;
    public static final int C_GROUP_BY_AGGREGATE = 11016;
    public static final int C_UNMATCH_QUOTE = 11017;
    public static final int C_EMPTY_SQL = 11018;
    public static final int C_UNSUPPORTED_CONDITION = 11019;
    public static final int C_CROSS_TABLE_COLUMN = 11020;
    public static final int C_OR_SUBCOND_IN_MULTI_TABLES = 11021;
    public static final int C_COMPARISON_WITH_OPERATION_IN_MULTI_TABLES = 11022;
    public static final int C_EXPRESSION_NOT_IN_GIVEN_TABLES = 11023;
    public static final int C_NO_JOIN_CONDITION_IN_MULTIPLE_TABLE_QUERY = 11024;
    public static final int C_ALIAS_NOT_SUPPORT = 11025;
    public static final int C_DDB_NOT_FOUND = 11026;
    
    public static final int V_DATA_CONVERTION_ERROR = 12001;
    public static final int V_UNSUPPORTED_DATA_TYPE = 12002;
    public static final int V_INVALID_NUMBER_FORMAT = 12003;
    public static final int V_NUMBER_OUT_OF_RANGE = 12004;
    public static final int V_DIVIDED_BY_ZERO = 12005;
    public static final int V_INVALID_DATE_FORMAT = 12006;
    public static final int V_INVALID_TIME_FORMAT = 12007;
    public static final int V_INVALID_TIMESTAMP_FORMAT = 12008;
    public static final int V_NULL_VALUE_COMPARE = 12009;
    
    public static final int E_UNSUPPORTED_AGG_TYPE = 13001;
    public static final int E_LIKE_ESCAPE_ERROR = 13002;
    public static final int E_ILLAGEL_COMPARE_WITH_NULL = 13003;
    public static final int E_AMBIGUOUS_COLUMN_NAME = 13004;
    public static final int E_COLUMN_NOT_FOUND = 13005;
    public static final int E_UNSUPPORTED_FUNCTION = 13006;
    public static final int E_7 = 13007;
    public static final int E_8 = 13008;
    public static final int E_9 = 13009;
    public static final int E_0 = 13010;
    
    public static final int U_HEX_STRING_ODD = 14001;
    public static final int U_HEX_STRING_WRONG = 14002;
    
    public static final int T_TABLE_NOT_EXIST = 15001;
    public static final int T_COLUMN_NOT_EXIST = 15002;
    
    public static final int P_DUPLI_RECORD = 1062;
    public static final int P_UNSUPPORTED_COMPARISON_TYPE = 16002;
    
    public static final int R_UNSUPPORTED_SQLTYPE = 17001;
    
    public static final int CONFLICT_WITH_MIGRATION = 18001;
    
    public static final int DDB_SHUT_DOWN = 19001;
    
    public static final int R_ROUTINE_NOT_EXIST = 20001;
    
    
    
}