package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/14/16.
 */
public class AccessDeniedException extends ApplicationException {

    private static final long serialVersionUID = -3947384020959895924L;

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
