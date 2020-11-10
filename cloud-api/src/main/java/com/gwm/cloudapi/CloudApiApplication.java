package com.gwm.cloudapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"com.gwm.cloudecs.dao","com.gwm.cloudcommon.dao","com.gwm.cloudstreamsets.dao"})
@ComponentScan({"com.gwm"})
@ServletComponentScan({"com.gwm.cloudcommon.handler"})
public class CloudApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudApiApplication.class, args);
    }

}
