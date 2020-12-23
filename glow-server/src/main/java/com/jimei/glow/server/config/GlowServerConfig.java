package com.jimei.glow.server.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.jimei.glow.server.core.DataSourceProperties;
import com.jimei.glow.server.core.Properties;
import com.jimei.glow.server.core.SqlSessionTemplateManagement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.UserTransaction;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author yudm
 * @Date 2020/12/12 14:05
 * @Desc
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DataSourceProperties.class)
public class GlowServerConfig {
    @Bean
    public JtaTransactionManager transactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        UserTransaction userTransaction = new UserTransactionImp();
        return new JtaTransactionManager(userTransaction, userTransactionManager);
    }

    @Bean
    public SqlSessionTemplateManagement sqlSessionTemplateManagement(@Autowired DataSourceProperties dataSourceProperties) {
        //SqlSessionTemplate池
        Map<String, List<SqlSessionTemplate>> sstListMap = new HashMap<>();
        //SqlSessionTemplate数量
        Map<String, Integer> sstCountMap = new HashMap<>();
        //每个集群中SqlSessionTemplate正被使用的序号
        Map<String, Integer> sstDutyMap = new HashMap<>();
        //配置内容
        Map<String, List<Properties>> pListMap =
                dataSourceProperties.getDbs().stream().collect(Collectors.groupingBy(Properties::getClusterLabel));
        pListMap.forEach((k, v) -> {
            List<SqlSessionTemplate> sstList = new ArrayList<>();
            for (Properties p : v) {
                //数据源
                DruidXADataSource dds = new DruidXADataSource();
                //DruidDataSource dds = new DruidDataSource();
                dds.setUrl(p.getUrl());
                dds.setUsername(p.getUsername());
                dds.setPassword(p.getPassword());
                dds.setDriverClassName(p.getDriverClassName());
                //设置分布式事务管理
                AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
                dataSource.setUniqueResourceName(new Date().toString());
                dataSource.setMinPoolSize(10);
                dataSource.setMaxPoolSize(20);
                dataSource.setMaxLifetime(20);
                dataSource.setBorrowConnectionTimeout(30);
                try {
                    dataSource.setLoginTimeout(30);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                dataSource.setMaintenanceInterval(60);
                dataSource.setMaxIdleTime(60);
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
        SqlSessionTemplateManagement sstm = new SqlSessionTemplateManagement();
        sstm.setSstListMap(sstListMap);
        sstm.setSstCountMap(sstCountMap);
        sstm.setSstDutyMap(sstDutyMap);
        return sstm;
    }
}
