package com.sdps.module.user.config;

import org.springframework.context.annotation.Configuration;

import com.sdps.common.config.DefaultAsycTaskConfig;

/**
 * @author zlt
 * 线程池配置、启用异步
 * @Async quartz 需要使用
 */
@Configuration
public class AsycTaskExecutorConfig extends DefaultAsycTaskConfig {

}
