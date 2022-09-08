package com.sdps.common.oauth2.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import cn.hutool.extra.spring.SpringUtil;

import com.sdps.common.constant.CommonConstant;
import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.context.LoginUserContextHolder;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.oauth2.token.CustomWebAuthenticationDetails;


@Slf4j
public class AuthUtils {
	private AuthUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static final String BASIC_ = "Basic ";

	/**
	 * 获取requet(head/param)中的token
	 * 
	 * @param request
	 * @return
	 */
	public static String extractToken(HttpServletRequest request) {
		String token = extractHeaderToken(request);
		if (token == null) {
			token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
			if (token == null) {
				log.debug("Token not found in request parameters.  Not an OAuth2 request.");
			}
		}
		return token;
	}

	/**
	 * 解析head中的token
	 * 
	 * @param request
	 * @return
	 */
	private static String extractHeaderToken(HttpServletRequest request) {
		Enumeration<String> headers = request
				.getHeaders(CommonConstant.TOKEN_HEADER);
		while (headers.hasMoreElements()) {
			String value = headers.nextElement();
			if ((value.startsWith(OAuth2AccessToken.BEARER_TYPE))) {
				String authHeaderValue = value.substring(
						OAuth2AccessToken.BEARER_TYPE.length()).trim();
				int commaIndex = authHeaderValue.indexOf(',');
				if (commaIndex > 0) {
					authHeaderValue = authHeaderValue.substring(0, commaIndex);
				}
				return authHeaderValue;
			}
		}
		return null;
	}

	/**
	 * 校验accessToken
	 */
	public static AdminUserDO checkAccessToken(HttpServletRequest request) {
		String accessToken = extractToken(request);
		return checkAccessToken(accessToken);
	}

	public static AdminUserDO checkAccessToken(String accessTokenValue) {
		TokenStore tokenStore = SpringUtil.getBean(TokenStore.class);
		OAuth2AccessToken accessToken = tokenStore
				.readAccessToken(accessTokenValue);
		if (accessToken == null || accessToken.getValue() == null) {
			throw new InvalidTokenException("Invalid access token: "
					+ accessTokenValue);
		} else if (accessToken.isExpired()) {
			tokenStore.removeAccessToken(accessToken);
			throw new InvalidTokenException("Access token expired: "
					+ accessTokenValue);
		}
		OAuth2Authentication result = tokenStore
				.readAuthentication(accessToken);
		if (result == null) {
			throw new InvalidTokenException("Invalid access token: "
					+ accessTokenValue);
		}
		return setContext(result);
	}

	/**
	 * 用户信息赋值 context 对象
	 */
	public static AdminUserDO setContext(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
		AdminUserDO user = getUser(authentication);
		LoginUserContextHolder.setUser(user);
		return user;
	}

	/**
	 * *从header 请求中的clientId:clientSecret
	 */
	public static String[] extractClient(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith(BASIC_)) {
			throw new UnapprovedClientAuthenticationException("请求头中client信息为空");
		}
		return extractHeaderClient(header);
	}

	/**
	 * 从header 请求中的clientId:clientSecret
	 *
	 * @param header
	 *            header中的参数
	 */
	public static String[] extractHeaderClient(String header) {
		byte[] base64Client = header.substring(BASIC_.length()).getBytes(
				StandardCharsets.UTF_8);
		byte[] decoded = Base64.getDecoder().decode(base64Client);
		String clientStr = new String(decoded, StandardCharsets.UTF_8);
		String[] clientArr = clientStr.split(":");
		if (clientArr.length != 2) {
			throw new RuntimeException("Invalid basic authentication token");
		}
		return clientArr;
	}

	/**
	 * 获取登陆的用户名
	 */
	public static String getUsername(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String username = null;
		if (principal instanceof AdminUserDO) {
			username = ((AdminUserDO) principal).getUsername();
		} else if (principal instanceof String) {
			username = (String) principal;
		}
		return username;
	}

	/**
	 * 获取登陆的用户对象
	 */
	public static AdminUserDO getUser(Authentication authentication) {
		AdminUserDO user = null;
		if (authentication != null
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			Object principal = authentication.getPrincipal();
			// 客户端模式只返回一个clientId
			if (principal instanceof AdminUserDO) {
				user = (AdminUserDO) principal;
			}
		}
		return user;
	}

	/**
	 * 获取登陆的帐户类型
	 */
	@SuppressWarnings("unchecked")
	public static String getAccountType(Authentication authentication) {
		String accountType = null;
		if (authentication != null) {
			Object details = authentication.getDetails();
			if (details != null) {
				if (details instanceof CustomWebAuthenticationDetails) {
					CustomWebAuthenticationDetails detailsObj = (CustomWebAuthenticationDetails) details;
					accountType = detailsObj.getAccountType();
				} else if (details instanceof ServiceAuthenticationDetails) {
					accountType = SecurityConstants.DEF_ACCOUNT_TYPE;
				} else {
					Map<String, String> detailsMap = (Map<String, String>) details;
					if (detailsMap != null) {
						accountType = detailsMap
								.get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME);
					}
				}
			}
		}
		return accountType;
	}
}