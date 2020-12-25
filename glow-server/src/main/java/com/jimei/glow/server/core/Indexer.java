package com.jimei.glow.server.core;

/**
 * @Author yudm
 * @Date 2020/12/25 16:42
 * @Desc 轮询计数器
 */
public class Indexer {
    private int total;
    private int index;

    public Indexer(int total, int index) {
        this.total = total;
        this.index = index;
    }

    public synchronized int getAndAdd() {
        int tmp = index;
        index = ++index % total;
        return tmp;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
