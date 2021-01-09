package com.jimei.glow.common.base;

import org.omg.CORBA.INTERNAL;

/**
 * @Author yudm
 * @Date 2020/12/21 15:18
 * @Desc
 */
public interface GlowType {
    /**
     * 操作相关
     */
    int READ = 0;
    int WRITE = 1;
    int ROLLBACK = 2;
    int COMMIT = 3;
    /**
     * 数据类型相关
     */
    int BOOLEAN = 0;
    int BYTE = 1;
    int SHORT = 2;
    int CHAR = 3;
    int INT = 4;
    int FLOAT = 5;
    int DOUBLE = 6;
    int LONG = 7;
    int BIGINT = 8;
    int DECIMAL = 9;
    int TIME = 10;
    int STAMP = 11;
    int DATE = 12;
    int STRING = 13;
}
