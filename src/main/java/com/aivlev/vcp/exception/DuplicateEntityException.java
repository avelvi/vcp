package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/16/16.
 */
public class DuplicateEntityException extends ApplicationException {

    private static final long serialVersionUID = 6028007751425549772L;

    public DuplicateEntityException(String message) {
        super(message);
    }
}
