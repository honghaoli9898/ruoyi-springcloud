package com.sdps.common.errorcode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sdps.common.errorcode.core.generator.ErrorCodeAutoGenerator;
import com.sdps.common.errorcode.core.generator.ErrorCodeAutoGeneratorImpl;
import com.sdps.common.errorcode.core.loader.ErrorCodeLoader;
import com.sdps.common.errorcode.core.loader.ErrorCodeLoaderImpl;
import com.sdps.module.system.api.errorcode.ErrorCodeApi;

/**
 * 错误码配置类
 *
 * @author 芋道源码
 */
@Configuration
@ConditionalOnProperty(prefix = "sdps.error-code", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(ErrorCodeProperties.class)
@EnableScheduling
public class ErrorCodeConfiguration {

	@Bean
	public ErrorCodeAutoGenerator errorCodeAutoGenerator(
			@Value("${spring.application.name}") String applicationName,
			ErrorCodeProperties errorCodeProperties, ErrorCodeApi errorCodeApi) {
		return new ErrorCodeAutoGeneratorImpl(applicationName,
				errorCodeProperties.getConstantsClassList(), errorCodeApi);
	}

	@Bean
	public ErrorCodeLoader errorCodeLoader(
			@Value("${spring.application.name}") String applicationName,
			ErrorCodeApi errorCodeApi) {
		return new ErrorCodeLoaderImpl(applicationName, errorCodeApi);
	}

}
