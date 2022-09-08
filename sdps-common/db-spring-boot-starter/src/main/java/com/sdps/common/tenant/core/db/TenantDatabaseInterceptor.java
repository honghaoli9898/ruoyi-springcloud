package com.sdps.common.tenant.core.db;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import cn.hutool.core.collection.CollUtil;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.sdps.common.context.TenantContextHolder;
import com.sdps.common.tenant.config.TenantProperties;

public class TenantDatabaseInterceptor implements TenantLineHandler {

	private final Set<String> ignoreTables = new HashSet<>();

	public TenantDatabaseInterceptor(TenantProperties properties) {
		// 不同 DB 下，大小写的习惯不同，所以需要都添加进去
		properties.getIgnoreTables().forEach(table -> {
			ignoreTables.add(table.toLowerCase());
			ignoreTables.add(table.toUpperCase());
		});
		// 在 OracleKeyGenerator 中，生成主键时，会查询这个表，查询这个表后，会自动拼接 TENANT_ID 导致报错
		ignoreTables.add("DUAL");
	}

	@Override
	public Expression getTenantId() {
		return new LongValue(TenantContextHolder.getRequiredTenantId());
	}

	@Override
	public boolean ignoreTable(String tableName) {
		return TenantContextHolder.isIgnore() // 情况一，全局忽略多租户
				|| CollUtil.contains(ignoreTables, tableName); // 情况二，忽略多租户的表
	}

}
