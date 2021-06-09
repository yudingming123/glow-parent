package com.jimei.test.ctrl;

import com.jimei.test.svc.user.TestSvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author yudm
 * @Date 2021/5/30 20:10
 * @Desc
 */
@RestController
public class TestCtrl {
    @Resource
    private TestSvc testSvc;

    @GetMapping("test")
    public void test() {
        testSvc.insert();
    }

    @GetMapping("/add1")
    public Long add1() {
        return testSvc.add1();
    }

    @GetMapping("/add2")
    public Long add2() {
        return testSvc.add2();
    }
}
