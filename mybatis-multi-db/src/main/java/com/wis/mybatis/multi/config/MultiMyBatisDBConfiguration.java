package com.wis.mybatis.multi.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuBo
 * 2019/12/2.
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@Import(DynamicDataSourceAspect.class)
public class MultiMyBatisDBConfiguration {

    @Bean(name="db1")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db1")
    @ConditionalOnExpression("'${spring.datasource.druid.db1.url:}'.length()>0")
    public DataSource db1(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name="db2")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db2")
    @ConditionalOnExpression("'${spring.datasource.druid.db2.url:}'.length()>0")
    public DataSource db2(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name="db3")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db3")
    @ConditionalOnExpression("'${spring.datasource.druid.db3.url:}'.length()>0")
    public DataSource db3(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name="db4")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db4")
    @ConditionalOnExpression("'${spring.datasource.druid.db4.url}'.startsWith('jdbc')")
    public DataSource db4(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name="db5")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db5")
    @ConditionalOnExpression("'${spring.datasource.druid.db5.url:}'.length()>0")
    public DataSource db5(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name="db6")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db6")
    @ConditionalOnExpression("'${spring.datasource.druid.db6.url:}'.length()>0")
    public DataSource db6(){
        return DruidDataSourceBuilder.create().build();
    }


    @Bean(name="db7")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db7")
    @ConditionalOnExpression("'${spring.datasource.druid.db7.url:}'.length()>0")
    public DataSource db7(){
        return DruidDataSourceBuilder.create().build();
    }


    @Bean(name="db8")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db8")
    @ConditionalOnExpression("'${spring.datasource.druid.db8.url:}'.length()>0")
    public DataSource db8(){
        return DruidDataSourceBuilder.create().build();
    }





    @Bean(name="db9")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db9")
    @ConditionalOnExpression("'${spring.datasource.druid.db9.url:}'.length()>0")
    public DataSource db9(){
        return DruidDataSourceBuilder.create().build();
    }


    @Bean(name="db10")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db10")
    @ConditionalOnExpression("'${spring.datasource.druid.db10.url:}'.length()>0")
    public DataSource db10(){
        return DruidDataSourceBuilder.create().build();
    }



    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = "dataSource")
    @ConditionalOnMissingBean
    @Primary
    public DynamicDataSource dynamicDataSource(){
        DynamicDataSource dataSource = new DynamicDataSource();
        Object[] datasources = applicationContext.getBeanNamesForType(DruidDataSource.class);
        Map targetSources = new HashMap(){{
            for (int i = 0; i < datasources.length; i++) {
                put(DatabaseType.pickType(datasources[i].toString()),applicationContext.getBean(datasources[i].toString()));
            }
        }};
        dataSource.setTargetDataSources(targetSources);
        dataSource.setDefaultTargetDataSource(applicationContext.getBean("db1"));
        return dataSource;
    }
}
