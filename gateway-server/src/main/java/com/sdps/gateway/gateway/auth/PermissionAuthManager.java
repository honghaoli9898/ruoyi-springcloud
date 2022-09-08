package com.sdps.gateway.gateway.auth;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import cn.hutool.core.collection.CollUtil;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.context.TenantContextHolder;
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.oauth2.service.impl.DefaultPermissionServiceImpl;
import com.sdps.gateway.gateway.feign.MenuService;

/**
 * url权限认证
 */
@Component
public class PermissionAuthManager extends DefaultPermissionServiceImpl
		implements ReactiveAuthorizationManager<AuthorizationContext> {
	@Resource
	private MenuService menuService;

	@Override
	public Mono<AuthorizationDecision> check(
			Mono<Authentication> authentication,
			AuthorizationContext authorizationContext) {
		return authentication
				.map(auth -> {
					ServerWebExchange exchange = authorizationContext
							.getExchange();
					ServerHttpRequest request = exchange.getRequest();
					List<String> tenantId = request.getHeaders().get(
							SecurityConstants.TENANT_ID_HEADER);
					TenantContextHolder.setTenantId(CollUtil.isNotEmpty(tenantId) ? Long
							.valueOf(tenantId.get(0)) : null);
					boolean isPermission = super.hasPermission(auth, request
							.getMethodValue(), request.getURI().getPath());
					return new AuthorizationDecision(isPermission);
				}).defaultIfEmpty(new AuthorizationDecision(false));
	}

	@Override
	public List<MenuDO> findMenuByRoleCodes(String roleCodes) {
		return menuService.findByRoleCodes(roleCodes);
	}
}
