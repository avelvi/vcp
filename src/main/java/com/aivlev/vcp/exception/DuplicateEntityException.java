package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/16/16.
 */
public class DuplicateEntityException extends ApplicationException {
    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(Throwable cause) {
        super(cause);
    }

    public DuplicateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
