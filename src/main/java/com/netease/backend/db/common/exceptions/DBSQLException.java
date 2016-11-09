
package com.netease.backend.db.common.exceptions;

import java.sql.SQLException;


public class DBSQLException extends SQLException {
    
    private static final long serialVersionUID = 1098127127637L;
    
    private int err_code;
	private String err_message;
    private String additionl_info;

    public DBSQLException(int errorcode, String emessage) {
        this(errorcode, emessage, null);    
    }
    
    public DBSQLException(int errorcode, String emessage, String addinfo) {
        this.err_code = errorcode;
        this.err_message = emessage;
        this.additionl_info = addinfo;
    }
    
	public String getMessage() {
        if (this.additionl_info == null) {
            return this.err_message;
        } else {
            return this.err_message + " - " + this.additionl_info;
        }
	}
    
    public int getErrorCode() {
        return this.err_code;
    }
}