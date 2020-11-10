package com.gwm.cloudalert;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.gwm"})
@MapperScan({"com.gwm.cloudalert.dao"})
public class AlertServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertServerApplication.class, args);
    }

}
