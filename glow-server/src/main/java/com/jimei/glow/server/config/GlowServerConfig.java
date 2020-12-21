package com.jimei.glow.server.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.jimei.glow.server.core.DataSourceProperties;
import com.jimei.glow.server.core.Properties;
import com.jimei.glow.server.core.SqlSessionTemplateManagement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yudm
 * @Date 2020/12/12 14:05
 * @Desc
 */
@Component
public class GlowServerConfig implements BeanDefinitionRegistryPostProcessor {
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //SqlSessionTemplate池
        Map<String, List<SqlSessionTemplate>> sstListMap = new HashMap<>();
        //SqlSessionTemplate数量
        Map<String, Integer> sstCountMap = new HashMap<>();
        //每个集群中SqlSessionTemplate正被使用的序号
        Map<String, Integer> sstDutyMap = new HashMap<>();
        //配置内容
        Map<String, List<Properties>> pListMap = dataSourceProperties.getPropertiesList().stream().collect(Collectors.groupingBy(Properties::getClusterLabel));
        pListMap.forEach((k, v) -> {
            List<SqlSessionTemplate> sstList = new ArrayList<>();
            for (Properties p : v) {
                //数据源
                DruidXADataSource dds = new DruidXADataSource();
                dds.setUrl(p.getUrl());
                dds.setUsername(p.getUsername());
                dds.setPassword(p.getPassword());
                dds.setDriverClassName(p.getDriverClassName());
                //设置分布式事务管理
                AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
                dataSource.setXaDataSource(dds);
                //SqlSessionFactoryBean
                SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
                ssfb.setDataSource(dataSource);
                SqlSessionFactory ssf;
                try {
                    ssf = ssfb.getObject();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //SqlSessionTemplate
                SqlSessionTemplate sst = new SqlSessionTemplate(ssf);
                sstList.add(sst);
            }
            sstListMap.put(k, sstList);
            sstCountMap.put(k, sstList.size());
            sstDutyMap.put(k, 0);
        });
        //构造bean定义
        BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplateManagement.class);
        BeanDefinition bean = beanBuilder.getRawBeanDefinition();
        //添加属性
        bean.setAttribute("sstListMap", sstListMap);
        bean.setAttribute("sstCountMap", sstCountMap);
        bean.setAttribute("sstDutyMap", sstDutyMap);

        //注册bean
        registry.registerBeanDefinition("sqlSessionTemplateManagement", bean);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

}
