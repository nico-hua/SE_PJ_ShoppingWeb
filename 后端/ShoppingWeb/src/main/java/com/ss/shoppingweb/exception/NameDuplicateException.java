package com.ss.shoppingweb.exception;

/**用户名重复异常*/
public class NameDuplicateException extends ServiceException{
    public NameDuplicateException() {
        super();
    }

    public NameDuplicateException(String message) {
        super(message);
    }

    public NameDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameDuplicateException(Throwable cause) {
        super(cause);
    }

    protected NameDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
