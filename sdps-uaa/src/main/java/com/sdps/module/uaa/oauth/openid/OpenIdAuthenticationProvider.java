package com.sdps.module.uaa.oauth.openid;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.sdps.common.oauth2.token.OpenIdAuthenticationToken;
import com.sdps.module.uaa.oauth.service.impl.UserDetailServiceFactory;

/**
 * @author zlt
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class OpenIdAuthenticationProvider implements AuthenticationProvider {
    private UserDetailServiceFactory userDetailsServiceFactory;

    @Override
    public Authentication authenticate(Authentication authentication) {
        OpenIdAuthenticationToken authenticationToken = (OpenIdAuthenticationToken) authentication;
        String openId = (String) authenticationToken.getPrincipal();
        UserDetails user = userDetailsServiceFactory.getService(authenticationToken).loadUserByUserId(openId);
        if (user == null) {
            throw new InternalAuthenticationServiceException("openId错误");
        }
        OpenIdAuthenticationToken authenticationResult = new OpenIdAuthenticationToken(user, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OpenIdAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
