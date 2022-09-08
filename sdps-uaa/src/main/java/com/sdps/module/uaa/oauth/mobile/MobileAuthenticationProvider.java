package com.sdps.module.uaa.oauth.mobile;

import lombok.Setter;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sdps.common.oauth2.token.MobileAuthenticationToken;
import com.sdps.module.uaa.oauth.service.ZltUserDetailsService;

@Setter
public class MobileAuthenticationProvider implements AuthenticationProvider {
    private ZltUserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        MobileAuthenticationToken authenticationToken = (MobileAuthenticationToken) authentication;
        String mobile = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();
        Integer mode = (Integer) authenticationToken.getMode();
        UserDetails user = null;
        switch (mode) {
            case 1:
                user = userDetailsService.smsLogin(mobile, password);
                break;
            case 0:
                user = userDetailsService.loadUserByMobile(mobile);
                if (user == null) {
                    throw new InternalAuthenticationServiceException("手机号或验证码错误");
                }
                if (!passwordEncoder.matches(password, user.getPassword())) {
                    throw new BadCredentialsException("手机号或密码错误");
                }
                break;
            default:
                break;
        }
        MobileAuthenticationToken authenticationResult = new MobileAuthenticationToken(mobile, password, mode,user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
