package com.sdps.common.util.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.sdps.common.model.user.LoginUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.feign.UserService;
import com.sdps.common.model.dataobject.permission.RoleDO;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.util.web.WebFrameworkUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LoginUserUtils {
	/**
	 * 获取当前登录人
	 */
	public static AdminUserDO getCurrentUser(HttpServletRequest request,
			boolean isFull) {
		AdminUserDO user = null;

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication != null
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			Object principal = authentication.getPrincipal();
			// 客户端模式只返回一个clientId
			if (principal instanceof AdminUserDO) {
				user = (AdminUserDO) principal;
			}
		}
		if (user == null) {
			String userId = request.getHeader(SecurityConstants.USER_ID_HEADER);
			String username = request.getHeader(SecurityConstants.USER_HEADER);
			String roles = request.getHeader(SecurityConstants.ROLE_HEADER);

			if (StrUtil.isAllNotBlank(username, userId)) {
				if (isFull) {
					UserService userService = SpringUtil
							.getBean(UserService.class);
					user = userService.selectByUsername(username);
				} else {
					user = new AdminUserDO();
					user.setId(Long.valueOf(userId));
					user.setUsername(username);
				}
				if (StrUtil.isNotBlank(roles)) {
					List<RoleDO> sysRoleList = new ArrayList<>();
					Arrays.stream(roles.split(",")).forEach(role -> {
						RoleDO sysRole = new RoleDO();
						sysRole.setCode(role);
						sysRoleList.add(sysRole);
					});
					user.setRoles(sysRoleList);
				}
			}
		}
		return user;
	}

	public static LoginUser getLoginUser(HttpServletRequest request) {
		if(request == null){
			return null;
		}
		String tenantId = request.getHeader(SecurityConstants.TENANT_ID_HEADER);
		Long tenant = StrUtil.isNotBlank(tenantId) ? Long.valueOf(tenantId)
				: null;
		String userId = request.getHeader(SecurityConstants.USER_ID_HEADER);
		if(tenant==null && StrUtil.isBlank(userId)){
			return null;
		}
		LoginUser loginUser = new LoginUser();
		loginUser.setId(StrUtil.isNotBlank(userId) ?Long.valueOf(userId)
				: null);
		loginUser.setTenantId(tenant);
		loginUser.setUserType(WebFrameworkUtils.getLoginUserType());
		return loginUser;
	}

	public static LoginUser getLoginUser() {
		HttpServletRequest request =  getRequest();
		return getLoginUser(request);
	}
	public static HttpServletRequest getRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (!(requestAttributes instanceof ServletRequestAttributes)) {
			return null;
		}
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		return servletRequestAttributes.getRequest();
	}
}