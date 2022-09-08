package com.sdps.gateway.gateway.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.oauth2.util.AuthUtils;

/**
 * 认证成功处理类
 *
 * @author zlt
 * @date 2019/10/7
 *       <p>
 *       Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */
public class Oauth2AuthSuccessHandler implements
		ServerAuthenticationSuccessHandler {
	@Override
	public Mono<Void> onAuthenticationSuccess(
			WebFilterExchange webFilterExchange, Authentication authentication) {
		MultiValueMap<String, String> headerValues = new LinkedMultiValueMap<>(
				4);
		Object principal = authentication.getPrincipal();
		// 客户端模式只返回一个clientId
		if (principal instanceof AdminUserDO) {
			AdminUserDO user = (AdminUserDO) authentication.getPrincipal();
			headerValues.add(SecurityConstants.USER_ID_HEADER,
					String.valueOf(user.getId()));
			headerValues.add(SecurityConstants.USER_HEADER, user.getUsername());
			headerValues.add(SecurityConstants.TENANT_ID_HEADER, user
					.getTenantId().toString());
		}
		OAuth2Authentication oauth2Authentication = (OAuth2Authentication) authentication;
		String clientId = oauth2Authentication.getOAuth2Request().getClientId();
		headerValues.add(SecurityConstants.TENANT_HEADER, clientId);

		headerValues.add(SecurityConstants.ROLE_HEADER,
				CollectionUtil.join(authentication.getAuthorities(), ","));
		String accountType = AuthUtils.getAccountType(oauth2Authentication
				.getUserAuthentication());
		if (StrUtil.isNotEmpty(accountType)) {
			headerValues
					.add(SecurityConstants.ACCOUNT_TYPE_HEADER, accountType);
		}
		ServerWebExchange exchange = webFilterExchange.getExchange();
		ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
				.headers(h -> h.addAll(headerValues)).build();

		ServerWebExchange build = exchange.mutate().request(serverHttpRequest)
				.build();
		return webFilterExchange.getChain().filter(build);
	}
}
