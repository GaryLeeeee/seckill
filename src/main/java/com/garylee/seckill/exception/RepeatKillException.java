package com.garylee.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * Created by GaryLee on 2019-02-01 14:38.
 */
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
