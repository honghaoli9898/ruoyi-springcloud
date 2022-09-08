package com.sdps.module.uaa.oauth.social;

import com.sdps.module.uaa.oauth.service.SocialUserService;
import com.sdps.module.uaa.oauth.service.impl.UserDetailServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * openId的相关处理配置
 *
 * @author zlt
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Component
public class SocialAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    private UserDetailServiceFactory userDetailsServiceFactory;
    @Autowired
    private SocialUserService socialUserService;

    @Override
    public void configure(HttpSecurity http) {
        SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
        provider.setUserDetailsServiceFactory(userDetailsServiceFactory);
        provider.setSocialUserService(socialUserService);
        http.authenticationProvider(provider);
    }
}
