package org.seckill.exception;

/**
 * Created by GYX on 2017/5/25.
 */
public class SeckillException extends RuntimeException   {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
