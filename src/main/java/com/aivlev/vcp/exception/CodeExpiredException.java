package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/17/16.
 */
public class CodeExpiredException extends ApplicationException {
    public CodeExpiredException(String message) {
        super(message);
    }

    public CodeExpiredException(Throwable cause) {
        super(cause);
    }

    public CodeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
