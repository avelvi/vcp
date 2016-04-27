package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 4/20/16.
 */
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = -2882427373482230927L;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
