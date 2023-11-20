package com.ss.shoppingweb.exception;


/**已注册开店身份证再次申请*/
public class IdcardDuplicateException extends ServiceException{

    public IdcardDuplicateException() {
        super();
    }

    public IdcardDuplicateException(String message) {
        super(message);
    }

    public IdcardDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdcardDuplicateException(Throwable cause) {
        super(cause);
    }

    protected IdcardDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}