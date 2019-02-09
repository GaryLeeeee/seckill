package com.garylee.seckill.exception;

/**
 * 秒杀相关业务异常
 * Created by GaryLee on 2019-02-01 14:48.
 */
public class SeckillException extends RuntimeException{
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
