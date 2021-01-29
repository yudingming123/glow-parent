package com.jimei.glow.common.property;

import lombok.Data;

import java.util.List;

/**
 * @Author yudm
 * @Date 2020/12/12 14:38
 * @Desc
 */
@Data
public class GroupProperty {
    private HikariProperty hikari;
    private List<MetaProperty> metas;
}
