package com.sdps.module.uaa.oauth.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.oauth2.util.AuthUtils;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.pojo.PageResult;
import com.sdps.module.uaa.oauth.model.TokenVo;
import com.sdps.module.uaa.oauth.service.ITokensService;

/**
 * token管理接口
 *
 * @author zlt
 */
@Slf4j
@RestController
@RequestMapping("/tokens")
public class TokensController {
	@Resource
	private ITokensService tokensService;

	@Resource
	private ClientDetailsService clientDetailsService;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenStore tokenStore;

	@GetMapping("")
	public PageResult<TokenVo> list(@RequestParam Map<String, Object> params,
			String tenantId) {
		return tokensService.listTokens(params, tenantId);
	}

	@GetMapping("/key")
	public CommonResult<String> key(HttpServletRequest request) {
		try {
			String[] clientArr = AuthUtils.extractClient(request);
			ClientDetails clientDetails = clientDetailsService
					.loadClientByClientId(clientArr[0]);
			if (clientDetails == null
					|| !passwordEncoder.matches(clientArr[1],
							clientDetails.getClientSecret())) {
				throw new BadCredentialsException("应用ID或密码错误");
			}
		} catch (AuthenticationException ae) {
			return CommonResult.error(1, ae.getMessage());
		}
		org.springframework.core.io.Resource res = new ClassPathResource(
				SecurityConstants.RSA_PUBLIC_KEY);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				res.getInputStream()))) {
			return CommonResult.success(br.lines().collect(
					Collectors.joining("\n")));
		} catch (IOException ioe) {
			log.error("key error", ioe);
			return CommonResult.error(1, ioe.getMessage());
		}
	}

	@GetMapping("/getUserInfoByToken")
	public CommonResult<Object> getUserInfoByToken(HttpServletRequest request) {
		String access_token = request.getParameter("access_token");
		OAuth2AccessToken oAuth2AccessToken = tokenStore
				.readAccessToken(access_token);
		if (null != oAuth2AccessToken) {
			OAuth2Authentication oAuth2Authentication = tokenStore
					.readAuthentication(access_token);
			Object principal = oAuth2Authentication.getUserAuthentication()
					.getPrincipal();
			return CommonResult.success(principal);
		} else {
			return CommonResult.error(1, "未获取到用户信息");
		}
	}
}
