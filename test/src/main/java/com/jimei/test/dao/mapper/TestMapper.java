package com.jimei.test.dao.mapper;

import com.jimei.test.dao.entity.Test;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author yudm
 * @Date 2021/6/8 17:28
 * @Desc
 */
@Mapper
public interface TestMapper {
    void insert(Test test);
}
