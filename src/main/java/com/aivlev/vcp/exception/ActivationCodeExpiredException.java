package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/17/16.
 */
public class ActivationCodeExpiredException extends ApplicationException {
    public ActivationCodeExpiredException(String message) {
        super(message);
    }

    public ActivationCodeExpiredException(Throwable cause) {
        super(cause);
    }

    public ActivationCodeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
