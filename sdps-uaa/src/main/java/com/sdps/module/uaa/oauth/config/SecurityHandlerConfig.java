package com.sdps.module.uaa.oauth.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.sdps.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.sdps.module.uaa.oauth.exception.ValidateCodeException;
import com.sdps.module.uaa.oauth.handler.OauthLogoutHandler;
import com.sdps.module.uaa.oauth.handler.OauthLogoutSuccessHandler;

/**
 * 认证错误处理
 *
 * @author zlt
 */
@Slf4j
@Configuration
public class SecurityHandlerConfig {
    @Bean
    public LogoutHandler logoutHandler() {
        return new OauthLogoutHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new OauthLogoutSuccessHandler();
    }

    @Bean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {
            private static final String BAD_MSG = "坏的凭证";
            private static final String BAD_MSG_EN = "Bad credentials";

            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                OAuth2Exception oAuth2Exception;
                ServiceException serviceException = null;
                if (e.getMessage() != null
                        && (BAD_MSG.equals(e.getMessage()) || BAD_MSG_EN.equals(e.getMessage()))) {
                    oAuth2Exception = new InvalidGrantException("用户名或密码错误", e);
                } else if (e instanceof InternalAuthenticationServiceException
                        || e instanceof ValidateCodeException) {
                    oAuth2Exception = new InvalidGrantException(e.getMessage(), e);
                } else if (e instanceof OAuth2Exception) {
                    oAuth2Exception = (OAuth2Exception) e;
                } else if (e instanceof ServiceException) {
                    serviceException = (ServiceException) e;
                    oAuth2Exception = new ServerErrorException("", e);
                } else {
                    oAuth2Exception = new ServerErrorException(e.getMessage(), e);
                }
                ResponseEntity<OAuth2Exception> response = super.translate(oAuth2Exception);
                ResponseEntity<OAuth2Exception> finalResponse = new ResponseEntity(response.getBody(), response.getHeaders(), HttpStatus.OK);
                finalResponse.getBody().addAdditionalInformation("code", serviceException == null ? String.valueOf(oAuth2Exception.getHttpErrorCode()) : String.valueOf(serviceException.getCode()));
                finalResponse.getBody().addAdditionalInformation("msg", serviceException == null ? oAuth2Exception.getMessage() : serviceException.getMessage());
                return finalResponse;
            }

            @SuppressWarnings("serial")
            class ServerErrorException extends OAuth2Exception {

                public ServerErrorException(String msg, Throwable t) {
                    super(msg, t);
                }

                @Override
                public String getOAuth2ErrorCode() {
                    return null;
                }

                @Override
                public int getHttpErrorCode() {
                    return 500;
                }

            }
        };
    }

    /**
     * 登陆成功
     */
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
}
