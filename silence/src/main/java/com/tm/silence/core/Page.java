package com.tm.silence.core;

import lombok.Data;

import java.util.List;

/**
 * @Author yudm
 * @Date 2021/5/14 12:30
 * @Desc
 */
@Data
public class Page<T> {
    private int total;
    private int pageNum;
    private int pageSize;
    private List<T> list;
}
