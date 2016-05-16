package com.aivlev.vcp.aop;

import com.aivlev.vcp.exception.AccessDeniedException;
import com.aivlev.vcp.exception.DuplicateEntityException;
import com.aivlev.vcp.exception.ModelNotFoundException;
import com.aivlev.vcp.model.*;
import com.aivlev.vcp.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by aivlev on 5/14/16.
 */
@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> accessDeniedExceptionHandler(AccessDeniedException ex, HttpServletRequest request) {

        com.aivlev.vcp.model.Error error = new Error("Access Denied Error", ex.getMessage());
        Response response = new Response(HttpStatus.FORBIDDEN.value(), ex.getMessage(), error);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Response> duplicateEntityExceptionHandler(DuplicateEntityException ex, HttpServletRequest request) {
        com.aivlev.vcp.model.Error error = new Error("Duplication Error", ex.getMessage());
        Response response = new Response(HttpStatus.CONFLICT.value(), ex.getMessage(), error);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<Response> modelNotFoundExceptionHandler(ModelNotFoundException ex, HttpServletRequest request) {
        com.aivlev.vcp.model.Error error = new Error("Not Found Error", ex.getMessage());
        Response response = new Response(HttpStatus.NOT_FOUND.value(), ex.getMessage(), error);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}