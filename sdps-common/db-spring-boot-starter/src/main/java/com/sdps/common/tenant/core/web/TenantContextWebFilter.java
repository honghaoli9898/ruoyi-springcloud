package com.sdps.common.tenant.core.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import cn.hutool.core.util.StrUtil;

import com.sdps.common.constant.CommonConstant;
import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.context.TenantContextHolder;
import com.sdps.common.util.web.WebFrameworkUtils;

/**
 * 多租户 Context Web 过滤器 将请求 Header 中的 tenant-id 解析出来，添加到
 * {@link TenantContextHolder} 中，这样后续的 DB 等操作，可以获得到租户编号。
 *
 * @author 芋道源码
 */
public class TenantContextWebFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// 设置
		Long tenantId = WebFrameworkUtils.getTenantId(request);
		if (tenantId != null) {
			TenantContextHolder.setTenantId(tenantId);
		}
		String tenant = request.getParameter(CommonConstant.TENANT_ID_PARAM);
		if (StrUtil.isEmpty(tenant)) {
			tenant = request.getHeader(SecurityConstants.TENANT_HEADER);
		}
		// 保存租户id
		if (StrUtil.isNotEmpty(tenant)) {
			TenantContextHolder.setTenant(tenant);
		}
		try {
			chain.doFilter(request, response);
		} finally {
			// 清理
			TenantContextHolder.clear();
		}
	}

}
