package com.jimei.glow.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.jimei.glow.server.core.GlowDataSourceProperty;
import com.jimei.glow.server.core.Property;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.UserTransaction;

/**
 * @Author yudm
 * @Date 2020/12/12 14:05
 * @Desc
 */
@Configuration
@EnableConfigurationProperties(GlowDataSourceProperty.class)
public class GlowServerAutoConfig {
    /*@Bean
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
                dataSourceProperties.getDbs().stream().collect(Collectors.groupingBy(Properties::getClustergroup));
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
    }*/

    // 配置连接池，这里直接new一个Druid连接池，
    // 也可以new其他的连接池，比如spring boot默认的hikari连接池
    @Bean(name = "db1DataSource")
    @Primary
    public DataSource setDataSource(@Autowired GlowDataSourceProperty glowDataSourceProperty) {
        Property p = glowDataSourceProperty.getDbs().get(0);
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(p.getUrl());
        ds.setUsername(p.getUsername());
        ds.setPassword(p.getPassword());
        ds.setDriverClassName(p.getDriverClassName());
        return ds;
    }

    // 事务配置
    @Bean(name = "db1TransactionManager")
    @Primary
    public DataSourceTransactionManager setTransactionManager(@Qualifier("db1DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JtaTransactionManager transactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        UserTransaction userTransaction = new UserTransactionImp();
        return new JtaTransactionManager(userTransaction, userTransactionManager);
    }

    // 配置sessionFactory，这里的多数据源就是每个数据源对应一个sessionFactory
    // 下面getResources的就是mapper.xml文件
    @Bean(name = "db1SqlSessionFactory")
    @Primary
    public SqlSessionFactory setSqlSessionFactory(@Qualifier("db1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/db1/*.xml"));
        return bean.getObject();
    }

    // 配置SqlSessionTemplate
    @Bean(name = "db1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("db1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
