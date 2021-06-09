package com.jimei.test.svc.user;

import com.jimei.test.dao.entity.Test;
import com.jimei.test.dao.mapper.TestMapper;
import com.tm.silence.core.SqlExecutor;
import com.tm.silence.core.Table;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * @Author yudm
 * @Date 2021/5/30 20:13
 * @Desc
 */
@Service
//@Transactional
public class TestSvc {
    @Resource
    private TestMapper testMapper;

    public void insert() {
        for (int i = 0; i < 30; ++i) {
            doInsert(10 * i, 10 * i + 10);
        }
        for (int i = 400; i < 500; ++i) {
            Test test = new Test();
            test.setId(i);
            Table.insert(test);
        }
    }

    @Transactional
    @Async
    public void doInsert(int begin, int end) {
        for (int i = begin; i < end; ++i) {
            Test test = new Test();
            test.setId(i);
            Table.insert(test);
        }
    }

    public Long add1() {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            Test test = new Test();
            test.setId(i);
            Table.insert(test);
        }
        long end = System.currentTimeMillis();
        return end - begin;
    }

    public Long add2() {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            Test test = new Test();
            test.setId(i);
            testMapper.insert(test);
        }
        long end = System.currentTimeMillis();
        return end - begin;
    }


}
