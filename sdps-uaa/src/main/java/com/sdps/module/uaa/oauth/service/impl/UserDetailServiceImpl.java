package com.sdps.module.uaa.oauth.service.impl;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.pojo.CommonResult;
import com.sdps.module.system.enums.ErrorCodeConstants;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.stereotype.Service;

import com.sdps.common.constant.SecurityConstants;
import com.sdps.common.feign.UserService;
import com.sdps.common.model.user.LoginAppUser;
import com.sdps.module.uaa.oauth.service.ZltUserDetailsService;

import java.util.Map;

/**
 * @author zlt
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Service
public class UserDetailServiceImpl implements ZltUserDetailsService {
    private static final String ACCOUNT_TYPE = SecurityConstants.DEF_ACCOUNT_TYPE;

    @Resource
    private UserService userService;

    @Override
    public boolean supports(String accountType) {
        return ACCOUNT_TYPE.equals(accountType);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        LoginAppUser loginAppUser = userService.findByUsername(username);
        if (loginAppUser == null) {
            throw new InternalAuthenticationServiceException("用户名或密码错误");
        }
        return checkUser(loginAppUser);
    }

    @Override
    public SocialUserDetails loadUserByUserId(String openId) {
        LoginAppUser loginAppUser = userService.findByOpenId(openId);
        return checkUser(loginAppUser);
    }

    @Override
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        AdminUserDO adminUserDO = userService.selectByUserId(userId.toString());
        LoginAppUser loginAppUser = BeanUtil.copyProperties(adminUserDO, LoginAppUser.class);
        return checkUser(loginAppUser);
    }

    @Override
    public LoginAppUser smsLogin(String mobile, String code) {
        CommonResult commonResult = userService.smsLogin(mobile, code);
        if (commonResult.isError()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.SMS_MOBILE_FAILED);
        }
        LoginAppUser loginAppUser = new LoginAppUser();
        Map<String, String> data = (Map<String, String>) commonResult.getData();
        BeanUtil.fillBeanWithMap(data, loginAppUser, false);
        return checkUser(loginAppUser);
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) {
        AdminUserDO adminUserDO = userService.findByMobile(mobile);
        LoginAppUser loginAppUser = BeanUtil.copyProperties(adminUserDO, LoginAppUser.class);
        return checkUser(loginAppUser);
    }

    private LoginAppUser checkUser(LoginAppUser loginAppUser) {
        if (loginAppUser != null && !loginAppUser.isEnabled()) {
            throw new DisabledException("用户已作废");
        }
        return loginAppUser;
    }
}
