package com.sdps.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sdps.common.utils.PwdEncoderUtil;

/**
 * 密码工具类
 */
public class DefaultPasswordConfig {

	/**
	 * 装配BCryptPasswordEncoder用户密码的匹配
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder() {
		return PwdEncoderUtil.getDelegatingPasswordEncoder("bcrypt");
	}

}
