package com.gwm.cloudapi.config;

import com.alibaba.druid.pool.DruidDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * 表示一个配置文件
 */
@Configuration
public class DruidConfig {

    @Autowired
    private Environment env;

    /**
     * 加入到Spring容器中，并扫描spring.datasource前缀的配置
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druid(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.druid.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.druid.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.druid.password"));
        return dataSource;


    }
}