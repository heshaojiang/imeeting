package com.github.pig.common.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author lengleng
 * @date 2017年12月21日20:45:10
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnloginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnloginException() {
    }

    public UnloginException(String message) {
        super(message);
    }

    public UnloginException(Throwable cause) {
        super(cause);
    }

    public UnloginException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnloginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
