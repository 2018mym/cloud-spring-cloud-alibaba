package com.gwm.cloudecs;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.gwm"})
@MapperScan({"com.gwm.cloudecs.dao"})
@EnableFeignClients(basePackages = "com.gwm")
@NacosPropertySource(dataId = "cloud-ecs", autoRefreshed = true)
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "10.255.128.190:8848"))
public class CloudEcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudEcsApplication.class, args);
    }

}
