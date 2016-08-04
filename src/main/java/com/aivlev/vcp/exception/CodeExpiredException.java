package com.aivlev.vcp.exception;

/**
 * Created by aivlev on 5/17/16.
 */
public class CodeExpiredException extends ApplicationException {

    private static final long serialVersionUID = -3817048861495633663L;

    public CodeExpiredException(String message) {
        super(message);
    }
}
