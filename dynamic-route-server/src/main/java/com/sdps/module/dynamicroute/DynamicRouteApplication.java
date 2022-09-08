package com.sdps.module.dynamicroute;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.sdps.common.lb.annotation.EnableBaseFeignInterceptor;
import com.sdps.common.lb.annotation.EnableFeignInterceptor;

@Slf4j
@EnableDiscoveryClient
@MapperScan(value = { "com.sdps.module" }, annotationClass = Mapper.class, lazyInitialization = "${mybatis.lazy-initialization:false}")
@SpringBootApplication(scanBasePackages = { "com.sdps.module" })
@EnableFeignClients
@EnableBaseFeignInterceptor
@EnableFeignInterceptor
public class DynamicRouteApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamicRouteApplication.class, args);
		log.info("====DynamicRouteApplication====大数据动态代理服务启动成功");
	}

}
