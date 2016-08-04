package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 4/26/16.
 */
public class ProcessMediaContentException extends ApplicationException {

    private static final long serialVersionUID = -3201097149034409510L;

    public ProcessMediaContentException(String message) {
        super(message);
    }

    public ProcessMediaContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
