package com.sdps.module.uaa;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.sdps.common.lb.annotation.EnableFeignInterceptor;

@Slf4j
@MapperScan(value = { "com.sdps.module" }, annotationClass = Mapper.class, lazyInitialization = "${mybatis.lazy-initialization:false}")
@EnableFeignClients(basePackages = { "com.sdps.common.feign" })
@EnableFeignInterceptor
@EnableDiscoveryClient
@EnableRedisHttpSession
@SpringBootApplication(scanBasePackages = { "com.sdps.module" })
public class UaaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UaaServerApplication.class, args);
		log.info("====UaaServerApplication====UAA服务启动成功");
	}
}
