package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/16/16.
 */
public class ModelNotFoundException extends ApplicationException {
    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(Throwable cause) {
        super(cause);
    }

    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
