package com.tpgl.tpgl.exception;

/**
 * Exception levée lorsqu'une règle de gestion métier AutoLoc est violée.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
