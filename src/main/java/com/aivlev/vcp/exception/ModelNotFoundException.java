package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/16/16.
 */
public class ModelNotFoundException extends ApplicationException {

    private static final long serialVersionUID = 8261756934883221193L;

    public ModelNotFoundException(String message) {
        super(message);
    }
}
