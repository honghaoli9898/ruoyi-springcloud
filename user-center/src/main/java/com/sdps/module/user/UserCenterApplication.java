package com.sdps.module.user;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sdps.common.lb.annotation.EnableBaseFeignInterceptor;
import com.sdps.common.lb.annotation.EnableFeignInterceptor;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.sdps.module"})
@MapperScan(value = {"com.sdps.module"}, annotationClass = Mapper.class, lazyInitialization = "${mybatis.lazy-initialization:false}")
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.sdps.common.feign"})
@EnableTransactionManagement
@EnableBaseFeignInterceptor
@EnableFeignInterceptor
@EnableScheduling
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
        log.info("============用户中心启动成功==========");
    }
}
