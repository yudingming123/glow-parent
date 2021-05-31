package com.jimei.test.svc.user;

import com.jimei.silence.core.sql.Table;
import com.jimei.test.dao.entity.Test;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Author yudm
 * @Date 2021/5/30 20:13
 * @Desc
 */
@Service
@Transactional
public class TestSvc {

    public void insert() {
        for (int i = 0; i < 100; ++i) {
            Test test = new Test();
            test.setId(i);
            Table.insert(test);
            if (i == 10) {
                throw new RuntimeException("出错了");
            }
        }
    }

}
