package com.ss.shoppingweb.exception;

/**非商户异常*/
public class NotMerchantException extends ServiceException{
    public NotMerchantException() {
        super();
    }

    public NotMerchantException(String message) {
        super(message);
    }

    public NotMerchantException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotMerchantException(Throwable cause) {
        super(cause);
    }

    protected NotMerchantException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}