package com.sdps.gateway.gateway.feign.fallback;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollectionUtil;

import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.gateway.gateway.feign.MenuService;

/**
 * menuService降级工场
 */
@Slf4j
@Component
public class MenuServiceFallbackFactory implements FallbackFactory<MenuService> {
	@Override
	public MenuService create(Throwable throwable) {
		return new MenuService() {

			@Override
			public List<MenuDO> findByRoleCodes(String roleCodes) {
				log.error("调用findByRoleCodes异常：{}", roleCodes, throwable);
				return CollectionUtil.newArrayList();
			}

			@Override
			public CommonResult<Map<String, String>> serverLogin(
					String clusterId, String type, String username,
					Boolean isCache) {
				return CommonResult.error(1, "操作失败");
			}

		};

	}
}
