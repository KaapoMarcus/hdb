package com.netease.backend.db.common.proc;


public class ProcException extends Exception {
    static final long serialVersionUID = 1L;

    public ProcException(String message) {
        super(message);
    }

    public ProcException(String message, Throwable cause) {
        super(message, cause);
    }
}
