package com.netease.backend.db.common.ha;


public class HAException extends Exception {
    static final long serialVersionUID = 1L;

    public HAException(String message) {
        super(message);
    }

    public HAException(String message, Throwable cause) {
        super(message, cause);
    }
}
