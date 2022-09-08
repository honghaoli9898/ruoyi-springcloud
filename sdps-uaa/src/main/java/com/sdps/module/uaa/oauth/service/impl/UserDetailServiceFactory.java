package com.sdps.module.uaa.oauth.service.impl;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.oauth2.util.AuthUtils;
import com.sdps.module.uaa.oauth.service.ZltUserDetailsService;

/**
 * 用户service工厂
 *
 * @author zlt
 * @version 1.0
 * @date 2021/7/24
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
public class UserDetailServiceFactory {
    private static final String ERROR_MSG = "找不到账号类型为 {} 的实现类";

    @Resource
    private List<ZltUserDetailsService> userDetailsServices;

    public ZltUserDetailsService getService(Authentication authentication) {
        String accountType = AuthUtils.getAccountType(authentication);
        return this.getService(accountType);
    }

    public ZltUserDetailsService getService(String accountType) {
        if (StrUtil.isEmpty(accountType)) {
            accountType = SecurityConstants.DEF_ACCOUNT_TYPE;
        }
        log.info("UserDetailServiceFactory.getService:{}", accountType);
        if (CollUtil.isNotEmpty(userDetailsServices)) {
            for (ZltUserDetailsService userService : userDetailsServices) {
                if (userService.supports(accountType)) {
                    return userService;
                }
            }
        }
        throw new InternalAuthenticationServiceException(StrUtil.format(ERROR_MSG, accountType));
    }
}
