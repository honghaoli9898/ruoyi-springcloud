package com.sdps.common.web.core.filter;

import static com.sdps.common.exception.enums.GlobalErrorCodeConstants.DEMO_DENY;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import cn.hutool.core.util.StrUtil;

import com.sdps.common.pojo.CommonResult;
import com.sdps.common.util.servlet.ServletUtils;
import com.sdps.common.util.web.WebFrameworkUtils;

/**
 * 演示 Filter，禁止用户发起写操作，避免影响测试数据
 *
 * @author 芋道源码
 */
public class DemoFilter extends OncePerRequestFilter {

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String method = request.getMethod();
		return !StrUtil.equalsAnyIgnoreCase(method, "POST", "PUT", "DELETE") // 写操作时，不进行过滤率
				|| WebFrameworkUtils.getLoginUserId(request) == null; // 非登录用户时，不进行过滤
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain) {
		// 直接返回 DEMO_DENY 的结果。即，请求不继续
		ServletUtils.writeJSON(response, CommonResult.error(DEMO_DENY));
	}

}
