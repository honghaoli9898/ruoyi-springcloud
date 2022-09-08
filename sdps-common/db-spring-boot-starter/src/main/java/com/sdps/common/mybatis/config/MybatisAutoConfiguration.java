package com.sdps.common.mybatis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.KingbaseKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.sdps.common.mybatis.core.handler.DefaultDBFieldHandler;

/**
 * MyBaits 配置类
 *
 * @author 芋道源码
 */
@Configuration
// Mapper 懒加载，目前仅用于单元测试
public class MybatisAutoConfiguration {

	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
		mybatisPlusInterceptor
				.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
		return mybatisPlusInterceptor;
	}

	@Bean
	@ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "field-fill", havingValue = "true", matchIfMissing = true)
	public MetaObjectHandler defaultMetaObjectHandler() {
		return new DefaultDBFieldHandler(); // 自动填充参数类
	}

	@Bean
	@ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
	public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
		DbType dbType = IdTypeEnvironmentApplicationContextInitializer.getDbType(environment);
		if (dbType != null) {
			switch (dbType) {
			case POSTGRE_SQL:
				return new PostgreKeyGenerator();
			case ORACLE:
			case ORACLE_12C:
				return new OracleKeyGenerator();
			case H2:
				return new H2KeyGenerator();
			case KINGBASE_ES:
				return new KingbaseKeyGenerator();
			default:
				break;
			}
		}
		// 找不到合适的 IKeyGenerator 实现类
		throw new IllegalArgumentException(StrUtil.format(
				"DbType{} 找不到合适的 IKeyGenerator 实现类", dbType));
	}

}
