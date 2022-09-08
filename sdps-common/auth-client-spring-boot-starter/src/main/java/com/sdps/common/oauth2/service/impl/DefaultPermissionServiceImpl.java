package com.sdps.common.oauth2.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.AntPathMatcher;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import com.sdps.common.constant.CommonConstant;
import com.sdps.common.model.dataobject.permission.MenuDO;
import com.sdps.common.oauth2.properties.SecurityProperties;
import com.sdps.common.oauth2.util.AuthUtils;

/**
 * 请求权限判断service
 */
@Slf4j
public abstract class DefaultPermissionServiceImpl {

	@Autowired
	private SecurityProperties securityProperties;

	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * 查询当前用户拥有的资源权限
	 * 
	 * @param roleCodes
	 *            角色code列表，多个以','隔开
	 * @return
	 */
	public abstract List<MenuDO> findMenuByRoleCodes(String roleCodes);

	@SuppressWarnings("unchecked")
	public boolean hasPermission(Authentication authentication,
			String requestMethod, String requestURI) {
		// 前端跨域OPTIONS请求预检放行 也可通过前端配置代理实现
		if (HttpMethod.OPTIONS.name().equalsIgnoreCase(requestMethod)) {
			return true;
		}
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			// 判断是否开启url权限验证
			if (!securityProperties.getAuth().getUrlPermission().getEnable()) {
				return true;
			}
			// 超级管理员admin不需认证
			String username = AuthUtils.getUsername(authentication);
			if (CommonConstant.ADMIN_USER_NAME.equals(username)) {
				return true;
			}

			OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
			// 判断应用黑白名单
			if (!isNeedAuth(auth2Authentication.getOAuth2Request()
					.getClientId())) {
				return true;
			}

			// 判断不进行url权限认证的api，所有已登录用户都能访问的url
			for (String path : securityProperties.getAuth().getUrlPermission()
					.getIgnoreUrls()) {
				if (antPathMatcher.match(path, requestURI)) {
					return true;
				}
			}

			List<SimpleGrantedAuthority> grantedAuthorityList = (List<SimpleGrantedAuthority>) authentication
					.getAuthorities();
			if (CollectionUtil.isEmpty(grantedAuthorityList)) {
				log.warn("角色列表为空：{}", authentication.getPrincipal());
				return false;
			}

			String roleCodes = grantedAuthorityList.stream()
					.map(SimpleGrantedAuthority::getAuthority)
					.collect(Collectors.joining(", "));
			List<MenuDO> menuList = findMenuByRoleCodes(roleCodes);
			for (MenuDO menu : menuList) {
				if (StrUtil.isNotEmpty(menu.getUrl())
						&& antPathMatcher.match(menu.getUrl(), requestURI)) {
					if (StrUtil.isNotEmpty(menu.getPathMethod())) {
						return requestMethod.equalsIgnoreCase(menu
								.getPathMethod());
					} else {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 判断应用是否满足白名单和黑名单的过滤逻辑
	 * 
	 * @param clientId
	 *            应用id
	 * @return true(需要认证)，false(不需要认证)
	 */
	private boolean isNeedAuth(String clientId) {
		boolean result = true;
		// 白名单
		List<String> includeClientIds = securityProperties.getAuth()
				.getUrlPermission().getIncludeClientIds();
		// 黑名单
		List<String> exclusiveClientIds = securityProperties.getAuth()
				.getUrlPermission().getExclusiveClientIds();
		if (includeClientIds.size() > 0) {
			result = includeClientIds.contains(clientId);
		} else if (exclusiveClientIds.size() > 0) {
			result = !exclusiveClientIds.contains(clientId);
		}
		return result;
	}
}
