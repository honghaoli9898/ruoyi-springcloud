package com.sdps.framework.banner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sdps.framework.banner.core.BannerApplicationRunner;

/**
 * Banner 的自动配置类
 *
 * @author 芋道源码
 */
@Configuration
public class BannerAutoConfiguration {

	@Bean
	public BannerApplicationRunner bannerApplicationRunner() {
		return new BannerApplicationRunner();
	}

}
