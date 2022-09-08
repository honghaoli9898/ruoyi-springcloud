package com.sdps.common.oauth2.generatorKey;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;

import cn.hutool.core.collection.CollUtil;

public class CustomAuthenticationKeyGenerator implements
		AuthenticationKeyGenerator {
	private static final String CLIENT_ID = "client_id";

	private static final String SCOPE = "scope";

	private static final String USERNAME = "username";
	
	private static final String ROLE_LIST = "role_list";

	public String extractKey(OAuth2Authentication authentication) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		OAuth2Request authorizationRequest = authentication.getOAuth2Request();
		if (!authentication.isClientOnly()) {
			values.put(USERNAME, authentication.getName());
		}
		values.put(CLIENT_ID, authorizationRequest.getClientId());
		if (authorizationRequest.getScope() != null) {
			values.put(SCOPE, OAuth2Utils
					.formatParameterList(CollUtil.sortByPinyin(
							authorizationRequest.getScope())));
		}
		Collection<GrantedAuthority> authorities= authentication.getAuthorities();
		List<String> list = CollUtil.newArrayList();
		authorities.forEach(data -> list.add(data.getAuthority()));
		values.put(ROLE_LIST, OAuth2Utils.formatParameterList(CollUtil.sortByPinyin(list)));
		return generateKey(values);
	}

	protected String generateKey(Map<String, String> values) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(values.toString().getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		} catch (NoSuchAlgorithmException nsae) {
			throw new IllegalStateException(
					"MD5 algorithm not available.  Fatal (should be in the JDK).",
					nsae);
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalStateException(
					"UTF-8 encoding not available.  Fatal (should be in the JDK).",
					uee);
		}
	}
}
