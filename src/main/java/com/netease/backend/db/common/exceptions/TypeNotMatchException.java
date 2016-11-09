
package com.netease.backend.db.common.exceptions;


public class TypeNotMatchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String typeName1;
    private String typeName2;

    
    public TypeNotMatchException(String typeName1, String typeName2,
            String reason, Throwable cause) {
        super("Type " + typeName1 + " does not match type " + typeName2, cause);
        this.typeName1 = typeName1;
        this.typeName2 = typeName2;
    }

    
    public String getFirstTypeName() {
        return this.typeName1;
    }

    
    public String getSecondTypeName2() {
        return this.typeName2;
    }
}
