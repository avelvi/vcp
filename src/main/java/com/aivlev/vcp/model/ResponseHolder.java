package com.aivlev.vcp.model;

/**
 * Created by aivlev on 4/19/16.
 */
public class ResponseHolder<T> {
    private T response;

    public ResponseHolder(T response) {
        this.response = response;
    }

    public T getResponse() {
        return response;
    }

}
