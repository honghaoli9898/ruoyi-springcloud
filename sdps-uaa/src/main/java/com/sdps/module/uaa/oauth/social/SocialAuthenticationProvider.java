package com.sdps.module.uaa.oauth.social;

import com.sdps.common.enums.UserTypeEnum;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.oauth2.token.OpenIdAuthenticationToken;
import com.sdps.common.oauth2.token.SocialAuthenticationToken;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.social.SocialTypeEnum;
import com.sdps.module.uaa.oauth.service.SocialUserService;
import com.sdps.module.uaa.oauth.service.impl.UserDetailServiceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author zlt
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class SocialAuthenticationProvider implements AuthenticationProvider {
    private UserDetailServiceFactory userDetailsServiceFactory;
    private SocialUserService socialUserService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        SocialAuthenticationToken authenticationToken = (SocialAuthenticationToken) authentication;
        String state = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();
        Integer type = (Integer) authenticationToken.getType();
        Long userId = socialUserService.getBindUserId(UserTypeEnum.ADMIN.getValue(), Integer.valueOf(type),
                code, state);
        if (userId == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AUTH_THIRD_LOGIN_NOT_BIND);
        }
        UserDetails user = userDetailsServiceFactory.getService(authenticationToken).loadUserByUserId(userId);
        if (user == null) {
            throw new InternalAuthenticationServiceException("type:".concat(SocialTypeEnum.valueOfType(type).getSource()).concat(", login faile"));
        }
        OpenIdAuthenticationToken authenticationResult = new OpenIdAuthenticationToken(user, user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
