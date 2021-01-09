package com.jimei.glow.client.core;

/**
 * @Author yudm
 * @Date 2021/1/2 14:25
 * @Desc
 */
public class GlowClientException extends RuntimeException {
    public GlowClientException() {
    }

    public GlowClientException(String msg) {
        super(msg);
    }

    public GlowClientException(Throwable t) {
        super(t);
    }

    public GlowClientException(String msg, Throwable t) {
        super(msg, t);
    }
}
