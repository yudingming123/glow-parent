package com.jimei.glow.server.base;

import lombok.Data;

/**
 * @Author yudm
 * @Date 2020/12/19 14:43
 * @Desc
 */
@Data
public class RspInfo<T> {
    //状态码
    private int code;
    //抛出的异常对象信息
    private Throwable ex;
    //具体的数据
    private T data;

    public RspInfo(int code, Throwable ex, T data) {
        this.code = code;
        this.ex = ex;
        this.data = data;
    }

    public static <T> RspInfo<T> success(T data) {
        return new RspInfo<>(0, null, data);
    }

    public static RspInfo<Void> success() {
        return new RspInfo<>(0, null, null);
    }

    public static RspInfo<Void> fail(Throwable ex) {
        return new RspInfo<>(1, ex, null);
    }
}
