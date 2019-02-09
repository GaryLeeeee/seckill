package com.garylee.seckill.exception;

/**
 * Created by GaryLee on 2019-02-01 14:46.
 */
public class SeckillCloseException extends SeckillException{
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
