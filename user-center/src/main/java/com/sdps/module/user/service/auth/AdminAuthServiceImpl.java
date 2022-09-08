package com.sdps.module.user.service.auth;

import com.sdps.common.exception.util.ServiceExceptionUtil;
import com.sdps.common.model.dataobject.user.AdminUserDO;
import com.sdps.common.model.user.LoginAppUser;
import com.sdps.common.util.servlet.ServletUtils;
import com.sdps.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.sdps.module.system.enums.ErrorCodeConstants;
import com.sdps.module.system.enums.sms.SmsSceneEnum;
import com.sdps.module.user.api.sms.SmsCodeApi;
import com.sdps.module.user.controller.admin.auth.vo.AuthSmsLoginReqVO;
import com.sdps.module.user.controller.admin.auth.vo.AuthSmsSendReqVO;
import com.sdps.module.user.convert.auth.AuthConvert;
import com.sdps.module.user.service.user.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * Auth Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {

    @Resource
    private AdminUserService userService;
    @Resource
    private SmsCodeApi smsCodeApi;

    @Override
    public void sendSmsCode(AuthSmsSendReqVO reqVO) {
        // 登录场景，验证是否存在
        if (userService.getUserByMobile(reqVO.getMobile()) == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.AUTH_MOBILE_NOT_EXISTS);
        }
        // 发送验证码
        SmsCodeSendReqDTO smsCodeSendReqDTO = AuthConvert.INSTANCE.convert(reqVO);
        smsCodeSendReqDTO.setCreateIp(ServletUtils.getClientIP());
        smsCodeApi.sendSmsCode(smsCodeSendReqDTO);
    }

    @Override
    public AdminUserDO smsLogin(String mobile, String code) {
        AuthSmsLoginReqVO authSmsLoginReqVO = new AuthSmsLoginReqVO();
        authSmsLoginReqVO.setMobile(mobile);
        authSmsLoginReqVO.setCode(code);
        smsCodeApi.useSmsCode(AuthConvert.INSTANCE.convert(authSmsLoginReqVO, SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), ServletUtils.getClientIP()));
        AdminUserDO user = userService.getUserByMobile(mobile);
        if (user == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_NOT_EXISTS);
        }
        return user;
    }


}
