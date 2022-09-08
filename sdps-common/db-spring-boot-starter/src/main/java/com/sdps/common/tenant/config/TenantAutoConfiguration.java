package com.sdps.common.tenant.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.sdps.common.config.WebProperties;
import com.sdps.common.enums.WebFilterOrderEnum;
import com.sdps.common.mybatis.core.util.MyBatisUtils;
import com.sdps.common.tenant.core.aop.TenantIgnoreAspect;
import com.sdps.common.tenant.core.db.CustomTenantInterceptor;
import com.sdps.common.tenant.core.db.TenantDatabaseInterceptor;
import com.sdps.common.tenant.core.security.TenantSecurityWebFilter;
import com.sdps.common.tenant.core.service.TenantFrameworkService;
import com.sdps.common.tenant.core.service.TenantFrameworkServiceImpl;
import com.sdps.common.tenant.core.web.TenantContextWebFilter;
import com.sdps.common.web.core.handler.GlobalExceptionHandler;
import com.sdps.module.system.api.tenant.TenantApi;

@Configuration
@ConditionalOnProperty(prefix = "sdps.tenant", value = "enable", matchIfMissing = true)
// 允许使用 yudao.tenant.enable=false 禁用多租户
@EnableConfigurationProperties(TenantProperties.class)
public class TenantAutoConfiguration {

	@Bean
	public TenantFrameworkService tenantFrameworkService(TenantApi tenantApi) {
		return new TenantFrameworkServiceImpl(tenantApi);
	}

	// ========== AOP ==========

	@Bean
	public TenantIgnoreAspect tenantIgnoreAspect() {
		return new TenantIgnoreAspect();
	}

	// ========== DB ==========

	@Bean
	public TenantLineInnerInterceptor tenantLineInnerInterceptor(
			TenantProperties properties, MybatisPlusInterceptor interceptor) {
		CustomTenantInterceptor inner = new CustomTenantInterceptor(
				new TenantDatabaseInterceptor(properties),
				properties.getIgnoreSqls());
		// 添加到 interceptor 中
		// 需要加在首个，主要是为了在分页插件前面。这个是 MyBatis Plus 的规定
		MyBatisUtils.addInterceptor(interceptor, inner, 0);
		return inner;
	}

	// ========== WEB ==========

	@Bean
	public FilterRegistrationBean<TenantContextWebFilter> tenantContextWebFilter() {
		FilterRegistrationBean<TenantContextWebFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new TenantContextWebFilter());
		registrationBean.setOrder(WebFilterOrderEnum.TENANT_CONTEXT_FILTER);
		return registrationBean;
	}

	// ========== Security ==========

	@Bean
	public FilterRegistrationBean<TenantSecurityWebFilter> tenantSecurityWebFilter(
			TenantProperties tenantProperties, WebProperties webProperties,
			GlobalExceptionHandler globalExceptionHandler,
			TenantFrameworkService tenantFrameworkService) {
		FilterRegistrationBean<TenantSecurityWebFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new TenantSecurityWebFilter(
				tenantProperties, webProperties, globalExceptionHandler,
				tenantFrameworkService));
		registrationBean.setOrder(WebFilterOrderEnum.TENANT_SECURITY_FILTER);
		return registrationBean;
	}

}
