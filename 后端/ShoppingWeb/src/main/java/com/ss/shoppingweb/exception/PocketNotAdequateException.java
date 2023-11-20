package com.ss.shoppingweb.exception;

public class PocketNotAdequateException extends ServiceException{
    public PocketNotAdequateException() {
        super();
    }

    public PocketNotAdequateException(String message) {
        super(message);
    }

    public PocketNotAdequateException(String message, Throwable cause) {
        super(message, cause);
    }

    public PocketNotAdequateException(Throwable cause) {
        super(cause);
    }

    protected PocketNotAdequateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
