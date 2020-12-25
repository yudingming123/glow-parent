package com.jimei.glow.server.core;


/**
 * @Author yudm
 * @Date 2020/12/25 16:05
 * @Desc 异常类
 */
public class GlowSqlException extends RuntimeException {
    public GlowSqlException() {
    }

    public GlowSqlException(String msg) {
        super(msg);
    }

    public GlowSqlException(String msg, Throwable t) {
        super(msg, t);
    }
}
