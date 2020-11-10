package com.gwm.cloudstreamsets;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.gwm"})
@MapperScan({"com.gwm.cloudstreamsets.dao"})
@EnableFeignClients(basePackages = "com.gwm")
public class CloudStreamsetsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudStreamsetsApplication.class, args);
    }

}
