package com.sdps.module.uaa.oauth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;

import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.pojo.PageResult;
import com.sdps.common.redis.template.RedisRepository;
import com.sdps.module.uaa.oauth.model.TokenVo;
import com.sdps.module.uaa.oauth.service.ITokensService;

@Service
public class RedisTokensServiceImpl implements ITokensService {
	@Autowired
	private RedisRepository redisRepository;

	@Override
	public PageResult<TokenVo> listTokens(Map<String, Object> params,
			String clientId) {
		Integer page = MapUtils.getInteger(params, "page");
		Integer limit = MapUtils.getInteger(params, "limit");
		int[] startEnds = PageUtil.transToStartEnd(page, limit);
		// 根据请求参数生成redis的key
		String redisKey = getRedisKey(params, clientId);
		long size = redisRepository.length(redisKey);
		List<TokenVo> result = new ArrayList<>(limit);
		RedisSerializer<Object> valueSerializer = RedisSerializer.java();
		// 查询token集合
		List<Object> tokenObjs = redisRepository.getList(redisKey,
				startEnds[0], startEnds[1] - 1, valueSerializer);
		if (tokenObjs != null) {
			for (Object obj : tokenObjs) {
				DefaultOAuth2AccessToken accessToken = (DefaultOAuth2AccessToken) obj;
				// 构造token对象
				TokenVo tokenVo = new TokenVo();
				tokenVo.setTokenValue(accessToken.getValue());
				tokenVo.setExpiration(accessToken.getExpiration());

				// 获取用户信息
				Object authObj = redisRepository.get(
						SecurityConstants.REDIS_TOKEN_AUTH
								+ accessToken.getValue(), valueSerializer);
				OAuth2Authentication authentication = (OAuth2Authentication) authObj;
				if (authentication != null) {
					OAuth2Request request = authentication.getOAuth2Request();
					tokenVo.setUsername(authentication.getName());
					tokenVo.setClientId(request.getClientId());
					tokenVo.setGrantType(request.getGrantType());
				}

				Map<String, Object> additionalInformation = accessToken
						.getAdditionalInformation();
				String accountType = (String) additionalInformation
						.get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME);
				tokenVo.setAccountType(accountType);

				result.add(tokenVo);
			}
		}
		return PageResult.<TokenVo> builder().list(result).total(size).build();
	}

	/**
	 * 根据请求参数生成redis的key
	 */
	private String getRedisKey(Map<String, Object> params, String clientId) {
		String result;
		String username = MapUtils.getString(params, "username");
		if (StrUtil.isNotEmpty(username)) {
			result = SecurityConstants.REDIS_UNAME_TO_ACCESS + clientId + ":"
					+ username;
		} else {
			result = SecurityConstants.REDIS_CLIENT_ID_TO_ACCESS + clientId;
		}
		return result;
	}
}
