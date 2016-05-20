package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/19/16.
 */
public class CodeNotFoundException extends ApplicationException {
    public CodeNotFoundException(String message) {
        super(message);
    }

    public CodeNotFoundException(Throwable cause) {
        super(cause);
    }

    public CodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
