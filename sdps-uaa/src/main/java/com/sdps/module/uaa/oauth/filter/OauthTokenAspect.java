package com.sdps.module.uaa.oauth.filter;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.sdps.common.constant.CommonConstant;
import com.sdps.common.enums.UserTypeEnum;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.util.servlet.ServletUtils;
import com.sdps.module.system.api.logger.LoginLogApi;
import com.sdps.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.sdps.module.system.dal.mapper.user.SysAdminUserMapper;
import com.sdps.module.system.enums.logger.LoginLogTypeEnum;
import com.sdps.module.system.enums.logger.LoginResultEnum;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.StrUtil;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.context.TenantContextHolder;
import com.sdps.common.model.user.LoginUserVo;
import com.sdps.common.pojo.CommonResult;
import com.sdps.common.redis.template.RedisRepository;
import com.sdps.common.utils.AddrUtil;
import reactor.core.publisher.Mono;

/**
 * oauth-token拦截器 1. 赋值租户 2. 统一返回token格式
 *
 * @author zlt
 * @date 2020/3/29
 *       <p>
 *       Blog: https://zlt2000.gitee.io Github: https://github.com/zlt2000
 */
@Slf4j
@Component
@Aspect
public class OauthTokenAspect {
	@Autowired
	private RedisRepository redisRepository;

	@Autowired
	private TokenStore tokenStore;

	@Resource
	private LoginLogApi loginLogApi;

	@Autowired
	private SysAdminUserMapper sysAdminUserMapper;

	@Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
	public Object handleControllerMethod(ProceedingJoinPoint joinPoint)
			throws Throwable {
		String keyName = "";
		String limitKey = "";
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		String ip = AddrUtil.getRemoteAddr(request);
		try {
			Object[] args = joinPoint.getArgs();
			Principal principal = (Principal) args[0];
			Map<String, String> parameters = (Map<String, String>) args[1];
			String isKeepLogin = parameters.get("keepLogin");
			String grantType = parameters.get(OAuth2Utils.GRANT_TYPE);

			String username = parameters.get(SecurityConstants.USER_NAME);
			keyName = username;
			if (grantType.equals("password_code")) {
				if (StrUtil.isBlank(isKeepLogin)
						|| !Objects.equals("1", isKeepLogin)) {
					if (StrUtil.isBlank(username)) {
						return ResponseEntity.status(HttpStatus.OK).body(
								CommonResult.success("用户名不能为空"));
					}
					if (redisRepository.exists(SecurityConstants.LOGIN_KEY
							.concat(username))) {
						Map<String, Object> map = MapUtil.newHashMap();
						map.put(SecurityConstants.ALREADY_LOGIN, true);
						map.put(SecurityConstants.USER_NAME, username);
						return ResponseEntity.status(HttpStatus.OK).body(
								CommonResult.success(map));
					}
				}
				limitKey = SecurityConstants.LIMIT_KEY.concat(keyName)
						.concat("_")
						.concat(String.valueOf(Ipv4Util.ipv4ToLong(ip)));
				Object obj = redisRepository.get(limitKey);
				if (Objects.isNull(obj)) {
					redisRepository.setExpire(limitKey, 1,
							SecurityConstants.LIMIT_TIME, TimeUnit.SECONDS);
				} else {
					Long sum = Long.valueOf(obj.toString());
					if (sum >= SecurityConstants.LIMIT_COUNT) {
						createLoginLog(null, keyName, LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.LOGIN_FAILE_CNT_ERROR,request.getHeader(CommonConstant.TRACE_ID_HEADER));
						return ResponseEntity
								.status(HttpStatus.OK)
								.body(CommonResult.error(
										1,
										"登录次数过多请"
												.concat(String
														.valueOf(SecurityConstants.LIMIT_TIME / 60))
												.concat("分钟后尝试")));
					} else {
						sum += 1;
						redisRepository.setExpire(limitKey, sum,
								SecurityConstants.LIMIT_TIME, TimeUnit.SECONDS);
					}
				}
			}
			if (!(principal instanceof Authentication)) {
				throw new InsufficientAuthenticationException(
						"There is no client authentication. Try adding an appropriate authentication filter.");
			}
			String clientId = getClientId(principal);

			if (!parameters
					.containsKey(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME)) {
				parameters.put(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME,
						SecurityConstants.DEF_ACCOUNT_TYPE);
			}

			// 保存租户id
			TenantContextHolder.setTenant(clientId);
			Object proceed = joinPoint.proceed(args);
			if (SecurityConstants.AUTHORIZATION_CODE.equals(grantType)) {
				return proceed;
			} else {
				ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>) proceed;
				OAuth2AccessToken body = responseEntity.getBody();
				if (grantType.equals("password_code")) {
					LoginUserVo loginUserVo = new LoginUserVo(ip,
							DateUtil.now(), body.getValue());
					ClientDetails clientDetails = (ClientDetails) redisRepository
							.getRedisTemplate().opsForValue()
							.get(clientRedisKey(clientId));
					int validitySeconds = Objects.nonNull(clientDetails) ? clientDetails
							.getAccessTokenValiditySeconds()
							: SecurityConstants.ACCESS_TOKEN_VALIDITY_SECONDS;
					redisRepository.setExpire(
							SecurityConstants.LOGIN_KEY.concat(keyName),
							loginUserVo, validitySeconds, TimeUnit.SECONDS);
					redisRepository.del(limitKey);
					OAuth2AccessToken accessToken = tokenStore.readAccessToken(body.getValue());

					OAuth2Authentication authentication = tokenStore.readAuthentication(accessToken);
					Object obj = authentication.getPrincipal();
					if (obj instanceof AdminUserDO) {
						AdminUserDO user = (AdminUserDO) authentication.getPrincipal();
						TenantContextHolder.setTenantId(user.getTenantId());
						createLoginLog(user.getId(), user.getUsername(), LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.SUCCESS,request.getHeader(CommonConstant.TRACE_ID_HEADER));
					}
				}

				return ResponseEntity.status(HttpStatus.OK).body(
						CommonResult.success(body));
			}
		} catch (Exception e) {
			//createLoginLog(null, keyName, LoginLogTypeEnum.LOGIN_USERNAME, LoginResultEnum.BAD_CREDENTIALS,request.getHeader(CommonConstant.TRACE_ID_HEADER));
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			TenantContextHolder.clear();
		}
	}

	private String getClientId(Principal principal) {
		Authentication client = (Authentication) principal;
		if (!client.isAuthenticated()) {
			throw new InsufficientAuthenticationException(
					"The client is not authenticated.");
		}
		String clientId = client.getName();
		if (client instanceof OAuth2Authentication) {
			clientId = ((OAuth2Authentication) client).getOAuth2Request()
					.getClientId();
		}
		return clientId;
	}

	private String clientRedisKey(String clientId) {
		return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
	}


	private void createLoginLog(Long userId, String mobile, LoginLogTypeEnum logType, LoginResultEnum loginResult, String tracerId){
		// 插入登录日志
		LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
		reqDTO.setLogType(logType.getType());
		reqDTO.setTraceId(tracerId);
		reqDTO.setUserId(userId);
		reqDTO.setUserType(UserTypeEnum.ADMIN.getValue());
		reqDTO.setUsername(mobile);
		reqDTO.setUserAgent(ServletUtils.getUserAgent());
		reqDTO.setUserIp(ServletUtils.getClientIP());
		reqDTO.setResult(loginResult.getResult());
		loginLogApi.createLoginLog(reqDTO);
		// 更新最后登录时间
		if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
			AdminUserDO userDO = new AdminUserDO();
			userDO.setId(userId);
			userDO.setLoginIp(ServletUtils.getClientIP());
			userDO.setLoginDate(DateUtil.date());
			sysAdminUserMapper.updateById(userDO);
		}
	}
}
